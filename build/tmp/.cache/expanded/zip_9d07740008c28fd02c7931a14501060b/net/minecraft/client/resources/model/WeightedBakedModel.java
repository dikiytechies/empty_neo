package net.minecraft.client.resources.model;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeightedBakedModel extends DelegateBakedModel implements net.neoforged.neoforge.client.model.IDynamicBakedModel {
    private final SimpleWeightedRandomList<BakedModel> list;

    public WeightedBakedModel(SimpleWeightedRandomList<BakedModel> p_371780_) {
        super(p_371780_.unwrap().getFirst().data());
        this.list = p_371780_;
    }

    @Override // FORGE: Implement our overload so child models can have custom logic
    public List<BakedQuad> getQuads(@Nullable BlockState p_235058_, @Nullable Direction p_235059_, RandomSource p_235060_, net.neoforged.neoforge.client.model.data.ModelData modelData, @org.jetbrains.annotations.Nullable net.minecraft.client.renderer.RenderType renderType) {
        return this.list.getRandomValue(p_235060_).map(p_370367_ -> p_370367_.getQuads(p_235058_, p_235059_, p_235060_, modelData, renderType)).orElse(Collections.emptyList());
    }

    @Override // FORGE: Get render types based on the active weighted model
    public net.neoforged.neoforge.client.ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, net.neoforged.neoforge.client.model.data.ModelData data) {
        return list.getRandomValue(rand)
                .map(model -> model.getRenderTypes(state, rand, data))
                .orElse(net.neoforged.neoforge.client.ChunkRenderTypeSet.none());
    }
}
