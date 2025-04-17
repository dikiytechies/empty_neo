package net.minecraft.server.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientCertificate;
import com.mojang.authlib.GameProfile;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;

public class PlayerSafetyServiceTextFilter extends ServerTextFilter {
    private final ConfidentialClientApplication client;
    private final ClientCredentialParameters clientParameters;
    private final Set<String> fullyFilteredEvents;
    private final int connectionReadTimeoutMs;

    private PlayerSafetyServiceTextFilter(
        URL p_364655_,
        ServerTextFilter.MessageEncoder p_363695_,
        ServerTextFilter.IgnoreStrategy p_364609_,
        ExecutorService p_362703_,
        ConfidentialClientApplication p_364360_,
        ClientCredentialParameters p_362037_,
        Set<String> p_361204_,
        int p_364088_
    ) {
        super(p_364655_, p_363695_, p_364609_, p_362703_);
        this.client = p_364360_;
        this.clientParameters = p_362037_;
        this.fullyFilteredEvents = p_361204_;
        this.connectionReadTimeoutMs = p_364088_;
    }

    @Nullable
    public static ServerTextFilter createTextFilterFromConfig(String p_364386_) {
        JsonObject jsonobject = GsonHelper.parse(p_364386_);
        URI uri = URI.create(GsonHelper.getAsString(jsonobject, "apiServer"));
        String s = GsonHelper.getAsString(jsonobject, "apiPath");
        String s1 = GsonHelper.getAsString(jsonobject, "scope");
        String s2 = GsonHelper.getAsString(jsonobject, "serverId", "");
        String s3 = GsonHelper.getAsString(jsonobject, "applicationId");
        String s4 = GsonHelper.getAsString(jsonobject, "tenantId");
        String s5 = GsonHelper.getAsString(jsonobject, "roomId", "Java:Chat");
        String s6 = GsonHelper.getAsString(jsonobject, "certificatePath");
        String s7 = GsonHelper.getAsString(jsonobject, "certificatePassword", "");
        int i = GsonHelper.getAsInt(jsonobject, "hashesToDrop", -1);
        int j = GsonHelper.getAsInt(jsonobject, "maxConcurrentRequests", 7);
        JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "fullyFilteredEvents");
        Set<String> set = new HashSet<>();
        jsonarray.forEach(p_361597_ -> set.add(GsonHelper.convertToString(p_361597_, "filteredEvent")));
        int k = GsonHelper.getAsInt(jsonobject, "connectionReadTimeoutMs", 2000);

        URL url;
        try {
            url = uri.resolve(s).toURL();
        } catch (MalformedURLException malformedurlexception) {
            throw new RuntimeException(malformedurlexception);
        }

        ServerTextFilter.MessageEncoder servertextfilter$messageencoder = (p_360550_, p_361856_) -> {
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("userId", p_360550_.getId().toString());
            jsonobject1.addProperty("userDisplayName", p_360550_.getName());
            jsonobject1.addProperty("server", s2);
            jsonobject1.addProperty("room", s5);
            jsonobject1.addProperty("area", "JavaChatRealms");
            jsonobject1.addProperty("data", p_361856_);
            jsonobject1.addProperty("language", "*");
            return jsonobject1;
        };
        ServerTextFilter.IgnoreStrategy servertextfilter$ignorestrategy = ServerTextFilter.IgnoreStrategy.select(i);
        ExecutorService executorservice = createWorkerPool(j);

        IClientCertificate iclientcertificate;
        try (InputStream inputstream = Files.newInputStream(Path.of(s6))) {
            iclientcertificate = ClientCredentialFactory.createFromCertificate(inputstream, s7);
        } catch (Exception exception1) {
            LOGGER.warn("Failed to open certificate file");
            return null;
        }

        ConfidentialClientApplication confidentialclientapplication;
        try {
            confidentialclientapplication = ConfidentialClientApplication.builder(s3, iclientcertificate)
                .sendX5c(true)
                .executorService(executorservice)
                .authority(String.format(Locale.ROOT, "https://login.microsoftonline.com/%s/", s4))
                .build();
        } catch (Exception exception) {
            LOGGER.warn("Failed to create confidential client application");
            return null;
        }

        ClientCredentialParameters clientcredentialparameters = ClientCredentialParameters.builder(Set.of(s1)).build();
        return new PlayerSafetyServiceTextFilter(
            url,
            servertextfilter$messageencoder,
            servertextfilter$ignorestrategy,
            executorservice,
            confidentialclientapplication,
            clientcredentialparameters,
            set,
            k
        );
    }

    private IAuthenticationResult aquireIAuthenticationResult() {
        return this.client.acquireToken(this.clientParameters).join();
    }

    @Override
    protected void setAuthorizationProperty(HttpURLConnection p_364490_) {
        IAuthenticationResult iauthenticationresult = this.aquireIAuthenticationResult();
        p_364490_.setRequestProperty("Authorization", "Bearer " + iauthenticationresult.accessToken());
    }

    @Override
    protected FilteredText filterText(String p_361948_, ServerTextFilter.IgnoreStrategy p_361780_, JsonObject p_364887_) {
        JsonObject jsonobject = GsonHelper.getAsJsonObject(p_364887_, "result", null);
        if (jsonobject == null) {
            return FilteredText.fullyFiltered(p_361948_);
        } else {
            boolean flag = GsonHelper.getAsBoolean(jsonobject, "filtered", true);
            if (!flag) {
                return FilteredText.passThrough(p_361948_);
            } else {
                for (JsonElement jsonelement : GsonHelper.getAsJsonArray(jsonobject, "events", new JsonArray())) {
                    JsonObject jsonobject1 = jsonelement.getAsJsonObject();
                    String s = GsonHelper.getAsString(jsonobject1, "id", "");
                    if (this.fullyFilteredEvents.contains(s)) {
                        return FilteredText.fullyFiltered(p_361948_);
                    }
                }

                JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "redactedTextIndex", new JsonArray());
                return new FilteredText(p_361948_, this.parseMask(p_361948_, jsonarray, p_361780_));
            }
        }
    }

    @Override
    protected int connectionReadTimeout() {
        return this.connectionReadTimeoutMs;
    }
}
