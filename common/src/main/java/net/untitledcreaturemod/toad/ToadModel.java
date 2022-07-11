package net.untitledcreaturemod.toad;

import net.minecraft.util.Identifier;
import net.untitledcreaturemod.setup.ModIdentifiers;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class ToadModel extends AnimatedGeoModel<ToadEntity> {
    @Override
    public Identifier getModelLocation(ToadEntity object) {
        return ModIdentifiers.Toad.MODEL;
    }

    @Override
    public Identifier getTextureLocation(ToadEntity object) {
        return ModIdentifiers.Toad.TEXTURE;
    }

    @Override
    public Identifier getAnimationFileLocation(ToadEntity animatable) {
        return ModIdentifiers.Toad.ANIMATIONS;
    }

    @Override
    public void setLivingAnimations(ToadEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        if (entity.isBaby()) {
            IBone root = this.getAnimationProcessor().getBone("root");
            if (root != null) {
                root.setScaleX(0.7f);
                root.setScaleY(0.7f);
                root.setScaleZ(0.7f);
            }
        }

        IBone head = this.getAnimationProcessor().getBone("head");
        if (entity.canLookAround() && head != null) {
            EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
            head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
            head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        }
    }
}
