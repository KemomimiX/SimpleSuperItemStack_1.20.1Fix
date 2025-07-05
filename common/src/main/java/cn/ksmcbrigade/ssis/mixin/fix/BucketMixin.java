package cn.ksmcbrigade.ssis.mixin.fix;

import dev.architectury.hooks.fluid.FluidBucketHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketMixin {

    @Shadow @Final private Fluid content;

    @Inject(method = "getEmptySuccessItem",at = @At("HEAD"),cancellable = true)
    private static void get(ItemStack stack, Player player, CallbackInfoReturnable<ItemStack> cir){
        Fluid fluid1 = null;
        if(stack.getItem() instanceof BucketItem bucketItem) fluid1 = bucketItem.arch$getFluid();
        if(fluid1 != Fluids.EMPTY && stack.getCount()>=2){
            cir.setReturnValue(stack.copyWithCount(stack.getCount()-1));
            cir.cancel();
        }
    }

    @Inject(method = "use",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResultHolder;sidedSuccess(Ljava/lang/Object;Z)Lnet/minecraft/world/InteractionResultHolder;",shift = At.Shift.BEFORE))
    private void use(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir){
        if(this.content==null || this.content == Fluids.EMPTY) return;
        ItemStack stack = user.getItemInHand(hand);
        if(!user.isCreative() && stack.getCount()>=2){
            user.addItem(new ItemStack(Items.BUCKET));
        }
    }
}
