package cn.ksmcbrigade.ssis.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * Fix ItemStack count synchronization for stacks larger than 127.
 *
 * Vanilla 1.20.1 writes ItemStack count as a signed byte.
 * This causes counts >= 128 to become invalid on the receiving side.
 *
 * Both client and server must have this mod installed.
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {

    /**
     * Write ItemStack using VarInt for the stack count.
     *
     * @author KSmc_brigade
     * @reason Support ItemStack counts larger than 127.
     */
    @Overwrite
    public FriendlyByteBuf writeItem(ItemStack stack) {
        FriendlyByteBuf buf = (FriendlyByteBuf) (Object) this;

        if (stack.isEmpty()) {
            buf.writeBoolean(false);
            return buf;
        }

        buf.writeBoolean(true);

        // Item registry ID
        buf.writeVarInt(Item.getId(stack.getItem()));

        // IMPORTANT:
        // Vanilla uses writeByte() here.
        // SSIS needs a variable-length integer instead.
        buf.writeVarInt(stack.getCount());

        // Item NBT
        CompoundTag tag = stack.getTag();
        buf.writeNbt(tag);

        return buf;
    }

    /**
     * Read ItemStack using VarInt for the stack count.
     *
     * @author KSmc_brigade
     * @reason Support ItemStack counts larger than 127.
     */
    @Overwrite
    public ItemStack readItem() {
        FriendlyByteBuf buf = (FriendlyByteBuf) (Object) this;

        if (!buf.readBoolean()) {
            return ItemStack.EMPTY;
        }

        Item item = Item.byId(buf.readVarInt());
        int count = buf.readVarInt();

        CompoundTag tag = buf.readNbt();

        ItemStack stack = new ItemStack(item, count);
        stack.setTag(tag);

        return stack;
    }
}