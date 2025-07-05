package cn.ksmcbrigade.ssis.mixin.fix;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MilkBucketItem.class)
public class MilkBucketMixin {
    @Inject(method = "finishUsingItem",at = @At("RETURN"))
    public void finish(ItemStack stack, Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir){
        if(!stack.isEmpty() && user instanceof Player player && !player.isCreative()){
            player.addItem(new ItemStack(Items.BUCKET,0));
        }
    }
}
