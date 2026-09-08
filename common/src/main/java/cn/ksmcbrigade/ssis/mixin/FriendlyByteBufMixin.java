
package cn.ksmcbrigade.ssis.mixin;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * 修复 SSIS 联机 128+ 堆叠同步。
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin extends ByteBuf {

    /**
     * 把堆叠数量写入改成 VarInt。
     */
    @Overwrite
    public FriendlyByteBuf writeItemStack(net.minecraft.world.item.ItemStack stack) {
        this.writeBoolean(!stack.isEmpty());

        if (!stack.isEmpty()) {
            this.writeId(net.minecraft.core.registries.BuiltInRegistries.ITEM, stack.getItem());
            this.writeVarInt(stack.getCount());
            this.writeNbt(stack.getTag());
        }

        return (FriendlyByteBuf)(Object)this;
    }

    /**
     * 读取时对应 VarInt。
     */
    @Overwrite
    public net.minecraft.world.item.ItemStack readItem() {
        if (!this.readBoolean()) {
            return net.minecraft.world.item.ItemStack.EMPTY;
        }

        var item = this.readById(net.minecraft.core.registries.BuiltInRegistries.ITEM);
        int count = this.readVarInt();
        var tag = this.readNbt();

        var stack = new net.minecraft.world.item.ItemStack(item, count);
        stack.setTag(tag);

        return stack;
    }
}
