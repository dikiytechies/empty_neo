package net.minecraft.client.resources.metadata.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.StringRepresentable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record VillagerMetadataSection(VillagerMetadataSection.Hat hat) {
    public static final Codec<VillagerMetadataSection> CODEC = RecordCodecBuilder.create(
        p_389666_ -> p_389666_.group(
                    VillagerMetadataSection.Hat.CODEC.optionalFieldOf("hat", VillagerMetadataSection.Hat.NONE).forGetter(VillagerMetadataSection::hat)
                )
                .apply(p_389666_, VillagerMetadataSection::new)
    );
    public static final MetadataSectionType<VillagerMetadataSection> TYPE = new MetadataSectionType<>("villager", CODEC);

    @OnlyIn(Dist.CLIENT)
    public static enum Hat implements StringRepresentable {
        NONE("none"),
        PARTIAL("partial"),
        FULL("full");

        public static final Codec<VillagerMetadataSection.Hat> CODEC = StringRepresentable.fromEnum(VillagerMetadataSection.Hat::values);
        private final String name;

        private Hat(String p_389513_) {
            this.name = p_389513_;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
