package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;

public record FurnaceRecipeDisplay(SlotDisplay ingredient, SlotDisplay fuel, SlotDisplay result, SlotDisplay craftingStation, int duration, float experience)
    implements RecipeDisplay {
    public static final MapCodec<FurnaceRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
        p_380853_ -> p_380853_.group(
                    SlotDisplay.CODEC.fieldOf("ingredient").forGetter(FurnaceRecipeDisplay::ingredient),
                    SlotDisplay.CODEC.fieldOf("fuel").forGetter(FurnaceRecipeDisplay::fuel),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(FurnaceRecipeDisplay::result),
                    SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(FurnaceRecipeDisplay::craftingStation),
                    Codec.INT.fieldOf("duration").forGetter(FurnaceRecipeDisplay::duration),
                    Codec.FLOAT.fieldOf("experience").forGetter(FurnaceRecipeDisplay::experience)
                )
                .apply(p_380853_, FurnaceRecipeDisplay::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, FurnaceRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
        SlotDisplay.STREAM_CODEC,
        FurnaceRecipeDisplay::ingredient,
        SlotDisplay.STREAM_CODEC,
        FurnaceRecipeDisplay::fuel,
        SlotDisplay.STREAM_CODEC,
        FurnaceRecipeDisplay::result,
        SlotDisplay.STREAM_CODEC,
        FurnaceRecipeDisplay::craftingStation,
        ByteBufCodecs.VAR_INT,
        FurnaceRecipeDisplay::duration,
        ByteBufCodecs.FLOAT,
        FurnaceRecipeDisplay::experience,
        FurnaceRecipeDisplay::new
    );
    public static final RecipeDisplay.Type<FurnaceRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public RecipeDisplay.Type<FurnaceRecipeDisplay> type() {
        return TYPE;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet p_379749_) {
        return this.ingredient.isEnabled(p_379749_) && this.fuel().isEnabled(p_379749_) && RecipeDisplay.super.isEnabled(p_379749_);
    }
}
