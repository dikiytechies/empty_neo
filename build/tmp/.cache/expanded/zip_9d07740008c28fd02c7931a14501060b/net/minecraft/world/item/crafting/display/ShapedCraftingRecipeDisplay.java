package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;

public record ShapedCraftingRecipeDisplay(int width, int height, List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation)
    implements RecipeDisplay {
    public static final MapCodec<ShapedCraftingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
        p_380232_ -> p_380232_.group(
                    Codec.INT.fieldOf("width").forGetter(ShapedCraftingRecipeDisplay::width),
                    Codec.INT.fieldOf("height").forGetter(ShapedCraftingRecipeDisplay::height),
                    SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(ShapedCraftingRecipeDisplay::ingredients),
                    SlotDisplay.CODEC.fieldOf("result").forGetter(ShapedCraftingRecipeDisplay::result),
                    SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(ShapedCraftingRecipeDisplay::craftingStation)
                )
                .apply(p_380232_, ShapedCraftingRecipeDisplay::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ShapedCraftingRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        ShapedCraftingRecipeDisplay::width,
        ByteBufCodecs.VAR_INT,
        ShapedCraftingRecipeDisplay::height,
        SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
        ShapedCraftingRecipeDisplay::ingredients,
        SlotDisplay.STREAM_CODEC,
        ShapedCraftingRecipeDisplay::result,
        SlotDisplay.STREAM_CODEC,
        ShapedCraftingRecipeDisplay::craftingStation,
        ShapedCraftingRecipeDisplay::new
    );
    public static final RecipeDisplay.Type<ShapedCraftingRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

    public ShapedCraftingRecipeDisplay(int width, int height, List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) {
        if (ingredients.size() != width * height) {
            throw new IllegalArgumentException("Invalid shaped recipe display contents");
        } else {
            this.width = width;
            this.height = height;
            this.ingredients = ingredients;
            this.result = result;
            this.craftingStation = craftingStation;
        }
    }

    @Override
    public RecipeDisplay.Type<ShapedCraftingRecipeDisplay> type() {
        return TYPE;
    }

    @Override
    public boolean isEnabled(FeatureFlagSet p_380274_) {
        return this.ingredients.stream().allMatch(p_379643_ -> p_379643_.isEnabled(p_380274_)) && RecipeDisplay.super.isEnabled(p_380274_);
    }
}
