package net.danskii.wayfarers_tales.mixin;

import net.danskii.wayfarers_tales.entity.wisp.WispEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HostileEntity.class)
public abstract class HostileEntityTargetMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void wayfarers_tales$targetWisps(EntityType<? extends HostileEntity> entityType, World world, CallbackInfo callbackInfo) {
        MobEntity mob = (MobEntity) (Object) this;
        ((MobEntityAccessor) mob).wayfarers_tales$getTargetSelector()
                .add(4, new ActiveTargetGoal<>(mob, WispEntity.class, 10, true, false, null));
    }
}
