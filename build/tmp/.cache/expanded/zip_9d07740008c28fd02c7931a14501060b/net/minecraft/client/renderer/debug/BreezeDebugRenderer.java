package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.BreezeDebugPayload;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class BreezeDebugRenderer {
    private static final int JUMP_TARGET_LINE_COLOR = ARGB.color(255, 255, 100, 255);
    private static final int TARGET_LINE_COLOR = ARGB.color(255, 100, 255, 255);
    private static final int INNER_CIRCLE_COLOR = ARGB.color(255, 0, 255, 0);
    private static final int MIDDLE_CIRCLE_COLOR = ARGB.color(255, 255, 165, 0);
    private static final int OUTER_CIRCLE_COLOR = ARGB.color(255, 255, 0, 0);
    private static final int CIRCLE_VERTICES = 20;
    private static final float SEGMENT_SIZE_RADIANS = (float) (Math.PI / 10);
    private final Minecraft minecraft;
    private final Map<Integer, BreezeDebugPayload.BreezeInfo> perEntity = new HashMap<>();

    public BreezeDebugRenderer(Minecraft p_312380_) {
        this.minecraft = p_312380_;
    }

    public void render(PoseStack p_312334_, MultiBufferSource p_312422_, double p_312916_, double p_312212_, double p_312705_) {
        LocalPlayer localplayer = this.minecraft.player;
        localplayer.level()
            .getEntities(EntityType.BREEZE, localplayer.getBoundingBox().inflate(100.0), p_312383_ -> true)
            .forEach(
                p_375446_ -> {
                    Optional<BreezeDebugPayload.BreezeInfo> optional = Optional.ofNullable(this.perEntity.get(p_375446_.getId()));
                    optional.map(BreezeDebugPayload.BreezeInfo::attackTarget)
                        .map(p_390085_ -> localplayer.level().getEntity(p_390085_))
                        .map(p_359255_ -> p_359255_.getPosition(this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true)))
                        .ifPresent(
                            p_312926_ -> {
                                drawLine(p_312334_, p_312422_, p_312916_, p_312212_, p_312705_, p_375446_.position(), p_312926_, TARGET_LINE_COLOR);
                                Vec3 vec3 = p_312926_.add(0.0, 0.01F, 0.0);
                                drawCircle(
                                    p_312334_.last().pose(),
                                    p_312916_,
                                    p_312212_,
                                    p_312705_,
                                    p_312422_.getBuffer(RenderType.debugLineStrip(2.0)),
                                    vec3,
                                    4.0F,
                                    INNER_CIRCLE_COLOR
                                );
                                drawCircle(
                                    p_312334_.last().pose(),
                                    p_312916_,
                                    p_312212_,
                                    p_312705_,
                                    p_312422_.getBuffer(RenderType.debugLineStrip(2.0)),
                                    vec3,
                                    8.0F,
                                    MIDDLE_CIRCLE_COLOR
                                );
                                drawCircle(
                                    p_312334_.last().pose(),
                                    p_312916_,
                                    p_312212_,
                                    p_312705_,
                                    p_312422_.getBuffer(RenderType.debugLineStrip(2.0)),
                                    vec3,
                                    24.0F,
                                    OUTER_CIRCLE_COLOR
                                );
                            }
                        );
                    optional.map(BreezeDebugPayload.BreezeInfo::jumpTarget)
                        .ifPresent(
                            p_390083_ -> {
                                drawLine(
                                    p_312334_, p_312422_, p_312916_, p_312212_, p_312705_, p_375446_.position(), p_390083_.getCenter(), JUMP_TARGET_LINE_COLOR
                                );
                                DebugRenderer.renderFilledBox(
                                    p_312334_,
                                    p_312422_,
                                    AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(p_390083_)).move(-p_312916_, -p_312212_, -p_312705_),
                                    1.0F,
                                    0.0F,
                                    0.0F,
                                    1.0F
                                );
                            }
                        );
                }
            );
    }

    private static void drawLine(
        PoseStack p_311968_, MultiBufferSource p_312154_, double p_312731_, double p_312933_, double p_312347_, Vec3 p_312653_, Vec3 p_312341_, int p_311785_
    ) {
        VertexConsumer vertexconsumer = p_312154_.getBuffer(RenderType.debugLineStrip(2.0));
        vertexconsumer.addVertex(p_311968_.last(), (float)(p_312653_.x - p_312731_), (float)(p_312653_.y - p_312933_), (float)(p_312653_.z - p_312347_))
            .setColor(p_311785_);
        vertexconsumer.addVertex(p_311968_.last(), (float)(p_312341_.x - p_312731_), (float)(p_312341_.y - p_312933_), (float)(p_312341_.z - p_312347_))
            .setColor(p_311785_);
    }

    private static void drawCircle(
        Matrix4f p_312289_, double p_312777_, double p_312720_, double p_312378_, VertexConsumer p_312840_, Vec3 p_312033_, float p_312193_, int p_312116_
    ) {
        for (int i = 0; i < 20; i++) {
            drawCircleVertex(i, p_312289_, p_312777_, p_312720_, p_312378_, p_312840_, p_312033_, p_312193_, p_312116_);
        }

        drawCircleVertex(0, p_312289_, p_312777_, p_312720_, p_312378_, p_312840_, p_312033_, p_312193_, p_312116_);
    }

    private static void drawCircleVertex(
        int p_312234_,
        Matrix4f p_312568_,
        double p_312049_,
        double p_312421_,
        double p_311755_,
        VertexConsumer p_311778_,
        Vec3 p_312214_,
        float p_312646_,
        int p_312055_
    ) {
        float f = (float)p_312234_ * (float) (Math.PI / 10);
        Vec3 vec3 = p_312214_.add((double)p_312646_ * Math.cos((double)f), 0.0, (double)p_312646_ * Math.sin((double)f));
        p_311778_.addVertex(p_312568_, (float)(vec3.x - p_312049_), (float)(vec3.y - p_312421_), (float)(vec3.z - p_311755_)).setColor(p_312055_);
    }

    public void clear() {
        this.perEntity.clear();
    }

    public void add(BreezeDebugPayload.BreezeInfo p_312506_) {
        this.perEntity.put(p_312506_.id(), p_312506_);
    }
}
