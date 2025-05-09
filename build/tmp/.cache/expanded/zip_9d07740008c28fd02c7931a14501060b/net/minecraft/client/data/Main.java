package net.minecraft.client.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.SharedConstants;
import net.minecraft.SuppressForbidden;
import net.minecraft.client.ClientBootstrap;
import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.server.Bootstrap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Main {
    @DontObfuscate
    @SuppressForbidden(
        reason = "System.out needed before bootstrap"
    )
    public static void main(String[] p_388033_) throws IOException {
        SharedConstants.tryDetectVersion();
        OptionParser optionparser = new OptionParser();
        OptionSpec<Void> optionspec = optionparser.accepts("help", "Show the help menu").forHelp();
        OptionSpec<Void> optionspec1 = optionparser.accepts("client", "Include client generators");
        OptionSpec<Void> optionspec2 = optionparser.accepts("all", "Include all generators");
        OptionSpec<String> optionspec3 = optionparser.accepts("output", "Output folder").withRequiredArg().defaultsTo("generated");
        OptionSpec<String> existing = optionparser.accepts("existing", "Existing resource packs that generated resources can reference").withRequiredArg();
        optionparser.accepts("gameDir").withRequiredArg().ofType(java.io.File.class).defaultsTo(new java.io.File(".")).required(); //Need by modlauncher, so lets just eat it
        OptionSpec<String> mod = optionparser.accepts("mod", "A modid to dump").withRequiredArg().withValuesSeparatedBy(",");
        OptionSpec<Void> flat = optionparser.accepts("flat", "Do not append modid prefix to output directory when generating for multiple mods");
        OptionSpec<String> assetIndex = optionparser.accepts("assetIndex").withRequiredArg();
        OptionSpec<java.io.File> assetsDir = optionparser.accepts("assetsDir").withRequiredArg().ofType(java.io.File.class);
        OptionSpec<Void> validateSpec = optionparser.accepts("validate", "Validate inputs");
        OptionSet optionset = optionparser.parse(p_388033_);
        if (!optionset.has(optionspec) && optionset.hasOptions()) {
            Path path = Paths.get(optionspec3.value(optionset));
            boolean flag = optionset.has(optionspec2);
            boolean flag1 = flag || optionset.has(optionspec1);
            java.util.Collection<Path> existingPacks = optionset.valuesOf(existing).stream().map(Paths::get).toList();
            java.util.Set<String> mods = new java.util.HashSet<>(optionset.valuesOf(mod));
            boolean isFlat = mods.isEmpty() || optionset.has(flat);
            boolean validate = optionset.has(validateSpec);
            DataGenerator datagenerator = new DataGenerator(isFlat ? path : path.resolve("minecraft"), SharedConstants.getCurrentVersion(), true);
            if (mods.contains("minecraft") || mods.isEmpty()) {
            addClientProviders(datagenerator, flag1);
            }
            net.neoforged.neoforge.data.loading.DatagenModLoader.begin(mods, path, java.util.List.of(), existingPacks, false, false, validate, isFlat, optionset.valueOf(assetIndex), optionset.valueOf(assetsDir), () -> {
                ClientBootstrap.bootstrap();
                net.neoforged.neoforge.client.ClientHooks.registerSpriteSourceTypes();
                net.neoforged.neoforge.client.entity.animation.json.AnimationTypeManager.init();
            }, net.neoforged.neoforge.data.event.GatherDataEvent.Client::new, datagenerator);
        } else {
            optionparser.printHelpOn(System.out);
        }
    }

    public static void addClientProviders(DataGenerator p_388709_, boolean p_388751_) {
        DataGenerator.PackGenerator datagenerator$packgenerator = p_388709_.getVanillaPack(p_388751_);
        datagenerator$packgenerator.addProvider(ModelProvider::new);
        datagenerator$packgenerator.addProvider(EquipmentAssetProvider::new);
    }
}
