package cn.ksmcbrigade.ssis.mixin.fix;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public class BucketMixin {

    @Shadow @Final private Fluid content;
    @Unique
    private Player player;
    @Unique
    private InteractionHand hand;

    @Inject(method = "use",at = @At("HEAD"))
    private void head(Level pLevel, Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir){
        this.player = pPlayer;
        this.hand = pHand;
    }

    @Redirect(method = "use",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResultHolder;sidedSuccess(Ljava/lang/Object;Z)Lnet/minecraft/world/InteractionResultHolder;"))
    private <T> InteractionResultHolder<T>  get(T p_19093_, boolean p_19094_){
        if(this.content!= Fluids.EMPTY && p_19093_ instanceof ItemStack stack){
            if(stack.getCount()>=2 && player!=null && !player.hasInfiniteMaterials() && hand!=null){
                player.setItemInHand(hand,stack);
                return new InteractionResultHolder(InteractionResult.CONSUME,p_19093_);
            }
        }

        return InteractionResultHolder.sidedSuccess(p_19093_, p_19094_);
    }

    @Inject(method = "use",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResultHolder;sidedSuccess(Ljava/lang/Object;Z)Lnet/minecraft/world/InteractionResultHolder;",shift = At.Shift.BEFORE))
    private void use(Level pLevel, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir){
        if(this.content == Fluids.EMPTY || this.content==null) return;
        ItemStack stack = user.getItemInHand(hand);
        if(!user.isCreative() && stack.getCount()>=2){
            user.addItem(new ItemStack(Items.BUCKET));
        }
    }
}
