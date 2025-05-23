package net.minecraft.client.model;

import net.minecraft.client.animation.definitions.FrogAnimation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.FrogRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FrogModel extends EntityModel<FrogRenderState> {
    private static final float MAX_WALK_ANIMATION_SPEED = 1.5F;
    private static final float MAX_SWIM_ANIMATION_SPEED = 1.0F;
    private static final float WALK_ANIMATION_SCALE_FACTOR = 2.5F;
    private final ModelPart body = this.root.getChild("body");
    private final ModelPart head = this.body.getChild("head");
    private final ModelPart eyes = this.head.getChild("eyes");
    private final ModelPart tongue = this.body.getChild("tongue");
    private final ModelPart leftArm = this.body.getChild("left_arm");
    private final ModelPart rightArm = this.body.getChild("right_arm");
    private final ModelPart leftLeg = this.root.getChild("left_leg");
    private final ModelPart rightLeg = this.root.getChild("right_leg");
    private final ModelPart croakingBody = this.body.getChild("croaking_body");

    public FrogModel(ModelPart p_233362_) {
        super(p_233362_.getChild("root"));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(3, 1).addBox(-3.5F, -2.0F, -8.0F, 7.0F, 3.0F, 9.0F).texOffs(23, 22).addBox(-3.5F, -1.0F, -8.0F, 7.0F, 0.0F, 9.0F),
            PartPose.offset(0.0F, -2.0F, 4.0F)
        );
        PartDefinition partdefinition3 = partdefinition2.addOrReplaceChild(
            "head",
            CubeListBuilder.create().texOffs(23, 13).addBox(-3.5F, -1.0F, -7.0F, 7.0F, 0.0F, 9.0F).texOffs(0, 13).addBox(-3.5F, -2.0F, -7.0F, 7.0F, 3.0F, 9.0F),
            PartPose.offset(0.0F, -2.0F, -1.0F)
        );
        PartDefinition partdefinition4 = partdefinition3.addOrReplaceChild("eyes", CubeListBuilder.create(), PartPose.offset(-0.5F, 0.0F, 2.0F));
        partdefinition4.addOrReplaceChild(
            "right_eye", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F), PartPose.offset(-1.5F, -3.0F, -6.5F)
        );
        partdefinition4.addOrReplaceChild(
            "left_eye", CubeListBuilder.create().texOffs(0, 5).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F), PartPose.offset(2.5F, -3.0F, -6.5F)
        );
        partdefinition2.addOrReplaceChild(
            "croaking_body",
            CubeListBuilder.create().texOffs(26, 5).addBox(-3.5F, -0.1F, -2.9F, 7.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F)),
            PartPose.offset(0.0F, -1.0F, -5.0F)
        );
        PartDefinition partdefinition5 = partdefinition2.addOrReplaceChild(
            "tongue", CubeListBuilder.create().texOffs(17, 13).addBox(-2.0F, 0.0F, -7.1F, 4.0F, 0.0F, 7.0F), PartPose.offset(0.0F, -1.01F, 1.0F)
        );
        PartDefinition partdefinition6 = partdefinition2.addOrReplaceChild(
            "left_arm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 3.0F), PartPose.offset(4.0F, -1.0F, -6.5F)
        );
        partdefinition6.addOrReplaceChild(
            "left_hand", CubeListBuilder.create().texOffs(18, 40).addBox(-4.0F, 0.01F, -4.0F, 8.0F, 0.0F, 8.0F), PartPose.offset(0.0F, 3.0F, -1.0F)
        );
        PartDefinition partdefinition7 = partdefinition2.addOrReplaceChild(
            "right_arm", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 3.0F), PartPose.offset(-4.0F, -1.0F, -6.5F)
        );
        partdefinition7.addOrReplaceChild(
            "right_hand", CubeListBuilder.create().texOffs(2, 40).addBox(-4.0F, 0.01F, -5.0F, 8.0F, 0.0F, 8.0F), PartPose.offset(0.0F, 3.0F, 0.0F)
        );
        PartDefinition partdefinition8 = partdefinition1.addOrReplaceChild(
            "left_leg", CubeListBuilder.create().texOffs(14, 25).addBox(-1.0F, 0.0F, -2.0F, 3.0F, 3.0F, 4.0F), PartPose.offset(3.5F, -3.0F, 4.0F)
        );
        partdefinition8.addOrReplaceChild(
            "left_foot", CubeListBuilder.create().texOffs(2, 32).addBox(-4.0F, 0.01F, -4.0F, 8.0F, 0.0F, 8.0F), PartPose.offset(2.0F, 3.0F, 0.0F)
        );
        PartDefinition partdefinition9 = partdefinition1.addOrReplaceChild(
            "right_leg", CubeListBuilder.create().texOffs(0, 25).addBox(-2.0F, 0.0F, -2.0F, 3.0F, 3.0F, 4.0F), PartPose.offset(-3.5F, -3.0F, 4.0F)
        );
        partdefinition9.addOrReplaceChild(
            "right_foot", CubeListBuilder.create().texOffs(18, 32).addBox(-4.0F, 0.01F, -4.0F, 8.0F, 0.0F, 8.0F), PartPose.offset(-2.0F, 3.0F, 0.0F)
        );
        return LayerDefinition.create(meshdefinition, 48, 48);
    }

    public void setupAnim(FrogRenderState p_365278_) {
        super.setupAnim(p_365278_);
        this.animate(p_365278_.jumpAnimationState, FrogAnimation.FROG_JUMP, p_365278_.ageInTicks);
        this.animate(p_365278_.croakAnimationState, FrogAnimation.FROG_CROAK, p_365278_.ageInTicks);
        this.animate(p_365278_.tongueAnimationState, FrogAnimation.FROG_TONGUE, p_365278_.ageInTicks);
        if (p_365278_.isSwimming) {
            this.animateWalk(FrogAnimation.FROG_SWIM, p_365278_.walkAnimationPos, p_365278_.walkAnimationSpeed, 1.0F, 2.5F);
        } else {
            this.animateWalk(FrogAnimation.FROG_WALK, p_365278_.walkAnimationPos, p_365278_.walkAnimationSpeed, 1.5F, 2.5F);
        }

        this.animate(p_365278_.swimIdleAnimationState, FrogAnimation.FROG_IDLE_WATER, p_365278_.ageInTicks);
        this.croakingBody.visible = p_365278_.croakAnimationState.isStarted();
    }
}
