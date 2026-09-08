package cn.ksmcbrigade.ssis.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Keeps Minecraft 1.20.1's original ItemStack wire format intact while
 * transporting the real SSIS stack count through the ItemStack NBT payload.
 *
 * Vanilla 1.20.1 encodes the network stack count as a byte. SSIS allows
 * counts larger than 127, so the byte alone is insufficient. This mixin
 * leaves that byte untouched and adds a countMod integer to the NBT only
 * for oversized stacks. The receiving side restores the real count after
 * the vanilla ItemStack has been decoded.
 *
 * Both server and client must use the patched mod.
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {

    @Unique
    private ItemStack ssis$currentWriteStack;

    @Inject(method = "writeItem", at = @At("HEAD"))
    private void ssis$captureWriteStack(ItemStack stack, CallbackInfoReturnable<FriendlyByteBuf> cir) {
        this.ssis$currentWriteStack = stack;
    }

    @Redirect(
            method = "writeItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/FriendlyByteBuf;writeNbt(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/network/FriendlyByteBuf;"
            )
    )
    private FriendlyByteBuf ssis$writeNetworkTag(FriendlyByteBuf buf, CompoundTag tag) {
        ItemStack stack = this.ssis$currentWriteStack;

        if (stack != null && stack.getCount() > 127) {
            CompoundTag networkTag = tag == null ? new CompoundTag() : tag.copy();
            networkTag.putInt("countMod", stack.getCount());
            return buf.writeNbt(networkTag);
        }

        return buf.writeNbt(tag);
    }

    @Inject(method = "writeItem", at = @At("RETURN"))
    private void ssis$clearWriteStack(ItemStack stack, CallbackInfoReturnable<FriendlyByteBuf> cir) {
        this.ssis$currentWriteStack = null;
    }

    @Inject(method = "readItem", at = @At("RETURN"))
    private void ssis$restoreNetworkCount(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();

        if (stack == null || stack.getTag() == null || !stack.getTag().contains("countMod")) {
            return;
        }

        int realCount = stack.getTag().getInt("countMod");
        if (realCount > 0) {
            stack.setCount(realCount);
        }
    }
}
