package net.minecraft.client.resources.model;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MultiPartBakedModel extends DelegateBakedModel implements net.neoforged.neoforge.client.model.IDynamicBakedModel {
    private final List<MultiPartBakedModel.Selector> selectors;
    private final Map<BlockState, BitSet> selectorCache = new Reference2ObjectOpenHashMap<>();

    private static BakedModel getFirstModel(List<MultiPartBakedModel.Selector> p_371884_) {
        if (p_371884_.isEmpty()) {
            throw new IllegalArgumentException("Model must have at least one selector");
        } else {
            return p_371884_.getFirst().model();
        }
    }

    public MultiPartBakedModel(List<MultiPartBakedModel.Selector> p_119462_) {
        super(getFirstModel(p_119462_));
        this.selectors = p_119462_;
    }

    public BitSet getSelectors(@Nullable BlockState p_235050_) {
            BitSet bitset = this.selectorCache.get(p_235050_);
            if (bitset == null) {
                bitset = new BitSet();

                for (int i = 0; i < this.selectors.size(); i++) {
                    if (this.selectors.get(i).condition.test(p_235050_)) {
                        bitset.set(i);
                    }
                }

                this.selectorCache.put(p_235050_, bitset);
            }
            return bitset;
    }

    // FORGE: Implement our overloads (here and below) so child models can have custom logic
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState p_235050_, @Nullable Direction p_235051_, RandomSource p_235052_, net.neoforged.neoforge.client.model.data.ModelData modelData, @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.RenderType renderType) {
        if (p_235050_ == null) {
            return Collections.emptyList();
        } else {
            BitSet bitset = getSelectors(p_235050_);
            List<List<BakedQuad>> list = new ArrayList<>();
            long j = p_235052_.nextLong();

            for (int k = 0; k < bitset.length(); k++) {
                if (bitset.get(k)) {

                    var model = this.selectors.get(k).model;
                    if (renderType == null || model.getRenderTypes(p_235050_, p_235052_, modelData).contains(renderType)) { // FORGE: Only put quad data if the model is using the render type passed
                        p_235052_.setSeed(j);
                        list.add(model.getQuads(p_235050_, p_235051_, p_235052_, net.neoforged.neoforge.client.model.data.MultipartModelData.resolve(modelData, model), renderType));
                    }
                }
            }

            return net.neoforged.neoforge.common.util.ConcatenatedListView.of(list);
        }
    }

    @Override
    public net.neoforged.neoforge.client.model.data.ModelData getModelData(net.minecraft.world.level.BlockAndTintGetter level, net.minecraft.core.BlockPos pos, BlockState state, net.neoforged.neoforge.client.model.data.ModelData modelData) {
        return net.neoforged.neoforge.client.model.data.MultipartModelData.create(selectors, getSelectors(state), level, pos, state, modelData);
    }

    @Override // FORGE: Get render types based on the selectors matched by the given block state
    public net.neoforged.neoforge.client.ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, net.neoforged.neoforge.client.model.data.ModelData data) {
        var renderTypeSets = new java.util.LinkedList<net.neoforged.neoforge.client.ChunkRenderTypeSet>();
        var selectors = getSelectors(state);
        for (int i = 0; i < selectors.length(); i++)
            if (selectors.get(i))
                renderTypeSets.add(this.selectors.get(i).model.getRenderTypes(state, rand, data));
        return net.neoforged.neoforge.client.ChunkRenderTypeSet.union(renderTypeSets);
    }

    @OnlyIn(Dist.CLIENT)
    public static record Selector(Predicate<BlockState> condition, BakedModel model) {
    }
}
