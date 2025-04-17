package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HumanoidModel<T extends HumanoidRenderState> extends EntityModel<T> implements ArmedModel, HeadedModel {
    public static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F, Set.of("head"));
    public static final float OVERLAY_SCALE = 0.25F;
    public static final float HAT_OVERLAY_SCALE = 0.5F;
    public static final float LEGGINGS_OVERLAY_SCALE = -0.1F;
    private static final float DUCK_WALK_ROTATION = 0.005F;
    private static final float SPYGLASS_ARM_ROT_Y = (float) (Math.PI / 12);
    private static final float SPYGLASS_ARM_ROT_X = 1.9198622F;
    private static final float SPYGLASS_ARM_CROUCH_ROT_X = (float) (Math.PI / 12);
    private static final float HIGHEST_SHIELD_BLOCKING_ANGLE = (float) (-Math.PI * 4.0 / 9.0);
    private static final float LOWEST_SHIELD_BLOCKING_ANGLE = 0.43633232F;
    private static final float HORIZONTAL_SHIELD_MOVEMENT_LIMIT = (float) (Math.PI / 6);
    public static final float TOOT_HORN_XROT_BASE = 1.4835298F;
    public static final float TOOT_HORN_YROT_BASE = (float) (Math.PI / 6);
    public final ModelPart head;
    public final ModelPart hat;
    public final ModelPart body;
    public final ModelPart rightArm;
    public final ModelPart leftArm;
    public final ModelPart rightLeg;
    public final ModelPart leftLeg;

    public HumanoidModel(ModelPart p_170677_) {
        this(p_170677_, RenderType::entityCutoutNoCull);
    }

    public HumanoidModel(ModelPart p_170679_, Function<ResourceLocation, RenderType> p_170680_) {
        super(p_170679_, p_170680_);
        this.head = p_170679_.getChild("head");
        this.hat = this.head.getChild("hat");
        this.body = p_170679_.getChild("body");
        this.rightArm = p_170679_.getChild("right_arm");
        this.leftArm = p_170679_.getChild("left_arm");
        this.rightLeg = p_170679_.getChild("right_leg");
        this.leftLeg = p_170679_.getChild("left_leg");
    }

    public static MeshDefinition createMesh(CubeDeformation p_170682_, float p_170683_) {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild(
            "head",
            CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_170682_),
            PartPose.offset(0.0F, 0.0F + p_170683_, 0.0F)
        );
        partdefinition1.addOrReplaceChild(
            "hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_170682_.extend(0.5F)), PartPose.ZERO
        );
        partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_170682_),
            PartPose.offset(0.0F, 0.0F + p_170683_, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_arm",
            CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170682_),
            PartPose.offset(-5.0F, 2.0F + p_170683_, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_arm",
            CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170682_),
            PartPose.offset(5.0F, 2.0F + p_170683_, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_leg",
            CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170682_),
            PartPose.offset(-1.9F, 12.0F + p_170683_, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_leg",
            CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170682_),
            PartPose.offset(1.9F, 12.0F + p_170683_, 0.0F)
        );
        return meshdefinition;
    }

    public void setupAnim(T p_361833_) {
        super.setupAnim(p_361833_);
        HumanoidModel.ArmPose humanoidmodel$armpose = p_361833_.leftArmPose;
        HumanoidModel.ArmPose humanoidmodel$armpose1 = p_361833_.rightArmPose;
        float f = p_361833_.swimAmount;
        boolean flag = p_361833_.isFallFlying;
        this.head.xRot = p_361833_.xRot * (float) (Math.PI / 180.0);
        this.head.yRot = p_361833_.yRot * (float) (Math.PI / 180.0);
        if (flag) {
            this.head.xRot = (float) (-Math.PI / 4);
        } else if (f > 0.0F) {
            this.head.xRot = Mth.rotLerpRad(f, this.head.xRot, (float) (-Math.PI / 4));
        }

        float f1 = p_361833_.walkAnimationPos;
        float f2 = p_361833_.walkAnimationSpeed;
        this.rightArm.xRot = Mth.cos(f1 * 0.6662F + (float) Math.PI) * 2.0F * f2 * 0.5F / p_361833_.speedValue;
        this.leftArm.xRot = Mth.cos(f1 * 0.6662F) * 2.0F * f2 * 0.5F / p_361833_.speedValue;
        this.rightLeg.xRot = Mth.cos(f1 * 0.6662F) * 1.4F * f2 / p_361833_.speedValue;
        this.leftLeg.xRot = Mth.cos(f1 * 0.6662F + (float) Math.PI) * 1.4F * f2 / p_361833_.speedValue;
        this.rightLeg.yRot = 0.005F;
        this.leftLeg.yRot = -0.005F;
        this.rightLeg.zRot = 0.005F;
        this.leftLeg.zRot = -0.005F;
        if (p_361833_.isPassenger) {
            this.rightArm.xRot += (float) (-Math.PI / 5);
            this.leftArm.xRot += (float) (-Math.PI / 5);
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = (float) (Math.PI / 10);
            this.rightLeg.zRot = 0.07853982F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = (float) (-Math.PI / 10);
            this.leftLeg.zRot = -0.07853982F;
        }

        boolean flag1 = p_361833_.mainArm == HumanoidArm.RIGHT;
        if (p_361833_.isUsingItem) {
            boolean flag2 = p_361833_.useItemHand == InteractionHand.MAIN_HAND;
            if (flag2 == flag1) {
                this.poseRightArm(p_361833_, humanoidmodel$armpose1);
            } else {
                this.poseLeftArm(p_361833_, humanoidmodel$armpose);
            }
        } else {
            boolean flag3 = flag1 ? humanoidmodel$armpose.isTwoHanded() : humanoidmodel$armpose1.isTwoHanded();
            if (flag1 != flag3) {
                this.poseLeftArm(p_361833_, humanoidmodel$armpose);
                this.poseRightArm(p_361833_, humanoidmodel$armpose1);
            } else {
                this.poseRightArm(p_361833_, humanoidmodel$armpose1);
                this.poseLeftArm(p_361833_, humanoidmodel$armpose);
            }
        }

        this.setupAttackAnimation(p_361833_, p_361833_.ageInTicks);
        if (p_361833_.isCrouching) {
            this.body.xRot = 0.5F;
            this.rightArm.xRot += 0.4F;
            this.leftArm.xRot += 0.4F;
            this.rightLeg.z += 4.0F;
            this.leftLeg.z += 4.0F;
            this.head.y += 4.2F;
            this.body.y += 3.2F;
            this.leftArm.y += 3.2F;
            this.rightArm.y += 3.2F;
        }

        if (humanoidmodel$armpose1 != HumanoidModel.ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.rightArm, p_361833_.ageInTicks, 1.0F);
        }

        if (humanoidmodel$armpose != HumanoidModel.ArmPose.SPYGLASS) {
            AnimationUtils.bobModelPart(this.leftArm, p_361833_.ageInTicks, -1.0F);
        }

        if (f > 0.0F) {
            float f7 = f1 % 26.0F;
            HumanoidArm humanoidarm = p_361833_.attackArm;
            float f3 = humanoidarm == HumanoidArm.RIGHT && p_361833_.attackTime > 0.0F ? 0.0F : f;
            float f4 = humanoidarm == HumanoidArm.LEFT && p_361833_.attackTime > 0.0F ? 0.0F : f;
            if (!p_361833_.isUsingItem) {
                if (f7 < 14.0F) {
                    this.leftArm.xRot = Mth.rotLerpRad(f4, this.leftArm.xRot, 0.0F);
                    this.rightArm.xRot = Mth.lerp(f3, this.rightArm.xRot, 0.0F);
                    this.leftArm.yRot = Mth.rotLerpRad(f4, this.leftArm.yRot, (float) Math.PI);
                    this.rightArm.yRot = Mth.lerp(f3, this.rightArm.yRot, (float) Math.PI);
                    this.leftArm.zRot = Mth.rotLerpRad(
                        f4, this.leftArm.zRot, (float) Math.PI + 1.8707964F * this.quadraticArmUpdate(f7) / this.quadraticArmUpdate(14.0F)
                    );
                    this.rightArm.zRot = Mth.lerp(
                        f3, this.rightArm.zRot, (float) Math.PI - 1.8707964F * this.quadraticArmUpdate(f7) / this.quadraticArmUpdate(14.0F)
                    );
                } else if (f7 >= 14.0F && f7 < 22.0F) {
                    float f8 = (f7 - 14.0F) / 8.0F;
                    this.leftArm.xRot = Mth.rotLerpRad(f4, this.leftArm.xRot, (float) (Math.PI / 2) * f8);
                    this.rightArm.xRot = Mth.lerp(f3, this.rightArm.xRot, (float) (Math.PI / 2) * f8);
                    this.leftArm.yRot = Mth.rotLerpRad(f4, this.leftArm.yRot, (float) Math.PI);
                    this.rightArm.yRot = Mth.lerp(f3, this.rightArm.yRot, (float) Math.PI);
                    this.leftArm.zRot = Mth.rotLerpRad(f4, this.leftArm.zRot, 5.012389F - 1.8707964F * f8);
                    this.rightArm.zRot = Mth.lerp(f3, this.rightArm.zRot, 1.2707963F + 1.8707964F * f8);
                } else if (f7 >= 22.0F && f7 < 26.0F) {
                    float f5 = (f7 - 22.0F) / 4.0F;
                    this.leftArm.xRot = Mth.rotLerpRad(f4, this.leftArm.xRot, (float) (Math.PI / 2) - (float) (Math.PI / 2) * f5);
                    this.rightArm.xRot = Mth.lerp(f3, this.rightArm.xRot, (float) (Math.PI / 2) - (float) (Math.PI / 2) * f5);
                    this.leftArm.yRot = Mth.rotLerpRad(f4, this.leftArm.yRot, (float) Math.PI);
                    this.rightArm.yRot = Mth.lerp(f3, this.rightArm.yRot, (float) Math.PI);
                    this.leftArm.zRot = Mth.rotLerpRad(f4, this.leftArm.zRot, (float) Math.PI);
                    this.rightArm.zRot = Mth.lerp(f3, this.rightArm.zRot, (float) Math.PI);
                }
            }

            float f9 = 0.3F;
            float f6 = 0.33333334F;
            this.leftLeg.xRot = Mth.lerp(f, this.leftLeg.xRot, 0.3F * Mth.cos(f1 * 0.33333334F + (float) Math.PI));
            this.rightLeg.xRot = Mth.lerp(f, this.rightLeg.xRot, 0.3F * Mth.cos(f1 * 0.33333334F));
        }
    }

    private void poseRightArm(T p_364666_, HumanoidModel.ArmPose p_362238_) {
        switch (p_362238_) {
            case EMPTY:
                this.rightArm.yRot = 0.0F;
                break;
            case ITEM:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) (Math.PI / 10);
                this.rightArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.poseBlockingArm(this.rightArm, true);
                break;
            case BOW_AND_ARROW:
                this.rightArm.yRot = -0.1F + this.head.yRot;
                this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
                this.rightArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
                this.leftArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
                break;
            case THROW_SPEAR:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) Math.PI;
                this.rightArm.yRot = 0.0F;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, p_364666_.maxCrossbowChargeDuration, p_364666_.ticksUsingItem, true);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
                break;
            case SPYGLASS:
                this.rightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (p_364666_.isCrouching ? (float) (Math.PI / 12) : 0.0F), -2.4F, 3.3F);
                this.rightArm.yRot = this.head.yRot - (float) (Math.PI / 12);
                break;
            case TOOT_HORN:
                this.rightArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
                this.rightArm.yRot = this.head.yRot - (float) (Math.PI / 6);
                break;
            case BRUSH:
                this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) (Math.PI / 5);
                this.rightArm.yRot = 0.0F;
            default:
                p_362238_.applyTransform(this, p_364666_, net.minecraft.world.entity.HumanoidArm.RIGHT);
        }
    }

    private void poseLeftArm(T p_361741_, HumanoidModel.ArmPose p_361463_) {
        switch (p_361463_) {
            case EMPTY:
                this.leftArm.yRot = 0.0F;
                break;
            case ITEM:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) (Math.PI / 10);
                this.leftArm.yRot = 0.0F;
                break;
            case BLOCK:
                this.poseBlockingArm(this.leftArm, false);
                break;
            case BOW_AND_ARROW:
                this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
                this.leftArm.yRot = 0.1F + this.head.yRot;
                this.rightArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
                this.leftArm.xRot = (float) (-Math.PI / 2) + this.head.xRot;
                break;
            case THROW_SPEAR:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) Math.PI;
                this.leftArm.yRot = 0.0F;
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, p_361741_.maxCrossbowChargeDuration, p_361741_.ticksUsingItem, false);
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
                break;
            case SPYGLASS:
                this.leftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (p_361741_.isCrouching ? (float) (Math.PI / 12) : 0.0F), -2.4F, 3.3F);
                this.leftArm.yRot = this.head.yRot + (float) (Math.PI / 12);
                break;
            case TOOT_HORN:
                this.leftArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
                this.leftArm.yRot = this.head.yRot + (float) (Math.PI / 6);
                break;
            case BRUSH:
                this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) (Math.PI / 5);
                this.leftArm.yRot = 0.0F;
            default:
                p_361463_.applyTransform(this, p_361741_, net.minecraft.world.entity.HumanoidArm.LEFT);
        }
    }

    private void poseBlockingArm(ModelPart p_304530_, boolean p_304416_) {
        p_304530_.xRot = p_304530_.xRot * 0.5F - 0.9424779F + Mth.clamp(this.head.xRot, (float) (-Math.PI * 4.0 / 9.0), 0.43633232F);
        p_304530_.yRot = (p_304416_ ? -30.0F : 30.0F) * (float) (Math.PI / 180.0) + Mth.clamp(this.head.yRot, (float) (-Math.PI / 6), (float) (Math.PI / 6));
    }

    protected void setupAttackAnimation(T p_361366_, float p_102859_) {
        float f = p_361366_.attackTime;
        if (!(f <= 0.0F)) {
            HumanoidArm humanoidarm = p_361366_.attackArm;
            ModelPart modelpart = this.getArm(humanoidarm);
            this.body.yRot = Mth.sin(Mth.sqrt(f) * (float) (Math.PI * 2)) * 0.2F;
            if (humanoidarm == HumanoidArm.LEFT) {
                this.body.yRot *= -1.0F;
            }

            float f2 = p_361366_.ageScale;
            this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F * f2;
            this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F * f2;
            this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0F * f2;
            this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F * f2;
            this.rightArm.yRot = this.rightArm.yRot + this.body.yRot;
            this.leftArm.yRot = this.leftArm.yRot + this.body.yRot;
            this.leftArm.xRot = this.leftArm.xRot + this.body.yRot;
            float $$5 = 1.0F - f;
            $$5 *= $$5;
            $$5 *= $$5;
            $$5 = 1.0F - $$5;
            float f3 = Mth.sin($$5 * (float) Math.PI);
            float f4 = Mth.sin(f * (float) Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
            modelpart.xRot -= f3 * 1.2F + f4;
            modelpart.yRot = modelpart.yRot + this.body.yRot * 2.0F;
            modelpart.zRot = modelpart.zRot + Mth.sin(f * (float) Math.PI) * -0.4F;
        }
    }

    private float quadraticArmUpdate(float p_102834_) {
        return -65.0F * p_102834_ + p_102834_ * p_102834_;
    }

    public void copyPropertiesTo(HumanoidModel<T> p_372920_) {
        p_372920_.head.copyFrom(this.head);
        p_372920_.body.copyFrom(this.body);
        p_372920_.rightArm.copyFrom(this.rightArm);
        p_372920_.leftArm.copyFrom(this.leftArm);
        p_372920_.rightLeg.copyFrom(this.rightLeg);
        p_372920_.leftLeg.copyFrom(this.leftLeg);
    }

    public void setAllVisible(boolean p_102880_) {
        this.head.visible = p_102880_;
        this.hat.visible = p_102880_;
        this.body.visible = p_102880_;
        this.rightArm.visible = p_102880_;
        this.leftArm.visible = p_102880_;
        this.rightLeg.visible = p_102880_;
        this.leftLeg.visible = p_102880_;
    }

    @Override
    public void translateToHand(HumanoidArm p_102854_, PoseStack p_102855_) {
        this.root.translateAndRotate(p_102855_);
        this.getArm(p_102854_).translateAndRotate(p_102855_);
    }

    protected ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum ArmPose implements net.neoforged.fml.common.asm.enumextension.IExtensibleEnum {
        EMPTY(false),
        ITEM(false),
        BLOCK(false),
        BOW_AND_ARROW(true),
        THROW_SPEAR(false),
        CROSSBOW_CHARGE(true),
        CROSSBOW_HOLD(true),
        SPYGLASS(false),
        TOOT_HORN(false),
        BRUSH(false);

        private final boolean twoHanded;
        @org.jetbrains.annotations.Nullable
        private final net.neoforged.neoforge.client.IArmPoseTransformer forgeArmPose;

        @net.neoforged.fml.common.asm.enumextension.ReservedConstructor
        private ArmPose(boolean p_102896_) {
            this.twoHanded = p_102896_;
            this.forgeArmPose = null;
        }

        private ArmPose(boolean twoHanded, net.neoforged.neoforge.client.IArmPoseTransformer forgeArmPose) {
            this.twoHanded = twoHanded;
            com.google.common.base.Preconditions.checkNotNull(forgeArmPose, "Cannot create new ArmPose with null transformer!");
            this.forgeArmPose = forgeArmPose;
        }

        public boolean isTwoHanded() {
            return this.twoHanded;
        }

        public <T extends HumanoidRenderState> void applyTransform(HumanoidModel<T> model, T entity, net.minecraft.world.entity.HumanoidArm arm) {
            if (this.forgeArmPose != null) this.forgeArmPose.applyTransform(model, entity, arm);
        }

        public static net.neoforged.fml.common.asm.enumextension.ExtensionInfo getExtensionInfo() {
            return net.neoforged.fml.common.asm.enumextension.ExtensionInfo.nonExtended(HumanoidModel.ArmPose.class);
        }
    }
}
