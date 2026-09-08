package cn.ksmcbrigade.ssis.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * SSIS network synchronization fix for Minecraft 1.20.1.
 *
 * Vanilla ItemStack network encoding stores the stack count in a byte,
 * while SSIS allows counts larger than 127.
 *
 * We deliberately DO NOT change the vanilla network protocol.
 * Instead, the real count is temporarily stored in the ItemStack NBT
 * as "countMod". The vanilla writeItem() method then serializes that NBT
 * normally. The receiver restores the real count from countMod.
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {

    @Unique
    private ItemStack ssis$currentWriteStack;

    @Unique
    private CompoundTag ssis$originalTag;

    @Unique
    private boolean ssis$modifiedTag;

    /**
     * Before vanilla writes the ItemStack, temporarily add countMod.
     */
    @Inject(method = "writeItem", at = @At("HEAD"))
    private void ssis$prepareNetworkStack(
            ItemStack stack,
            CallbackInfoReturnable<FriendlyByteBuf> cir
    ) {
        if (stack == null || stack.isEmpty() || stack.getCount() <= 127) {
            return;
        }

        this.ssis$currentWriteStack = stack;
        this.ssis$originalTag = stack.getTag();

        CompoundTag networkTag = stack.getOrCreateTag();
        networkTag.putInt("countMod", stack.getCount());

        this.ssis$modifiedTag = true;
    }

    /**
     * Restore the original ItemStack NBT after vanilla finishes writing.
     */
    @Inject(method = "writeItem", at = @At("RETURN"))
    private void ssis$restoreOriginalTag(
            ItemStack stack,
            CallbackInfoReturnable<FriendlyByteBuf> cir
    ) {
        if (!this.ssis$modifiedTag || stack != this.ssis$currentWriteStack) {
            return;
        }

        stack.setTag(this.ssis$originalTag);

        this.ssis$currentWriteStack = null;
        this.ssis$originalTag = null;
        this.ssis$modifiedTag = false;
    }

    /**
     * After vanilla reads the ItemStack, restore the real SSIS count.
     */
    @Inject(method = "readItem", at = @At("RETURN"))
    private void ssis$restoreNetworkCount(
            CallbackInfoReturnable<ItemStack> cir
    ) {
        ItemStack stack = cir.getReturnValue();

        if (stack == null || stack.isEmpty()) {
            return;
        }

        CompoundTag tag = stack.getTag();

        if (tag == null || !tag.contains("countMod")) {
            return;
        }

        int realCount = tag.getInt("countMod");

        if (realCount > 0) {
            stack.setCount(realCount);
        }
    }
}
