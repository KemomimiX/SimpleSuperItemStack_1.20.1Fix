package cn.ksmcbrigade.ssis.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;

@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {

    @Unique
    private static PrintWriter ssis$debugWriter;

    @Unique
    private static boolean ssis$initialized = false;

    @Unique
    private static synchronized void ssis$initDebug() {
        if (ssis$initialized) {
            return;
        }

        ssis$initialized = true;

        try {
            File file = new File("ssis-debug.log");

            ssis$debugWriter = new PrintWriter(
                    new FileWriter(file, true),
                    true
            );

            ssis$debugWriter.println();
            ssis$debugWriter.println(
                    "========== SSIS DEBUG START "
                            + LocalTime.now()
                            + " =========="
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Unique
    private static synchronized void ssis$log(String text) {
        if (!ssis$initialized) {
            ssis$initDebug();
        }

        String line =
                "[SSIS DEBUG "
                        + LocalTime.now()
                        + "] "
                        + text;

        // Minecraft 控制台
        System.out.println(line);

        // 独立日志文件
        if (ssis$debugWriter != null) {
            ssis$debugWriter.println(line);
            ssis$debugWriter.flush();
        }
    }

    @Inject(
            method = "writeItem",
            at = @At("HEAD")
    )
    private void ssis$debugWrite(
            ItemStack stack,
            CallbackInfoReturnable<FriendlyByteBuf> cir
    ) {
        if (stack == null || stack.isEmpty()) {
            return;
        }

        int count = stack.getCount();

        if (count >= 100 || count <= 0) {
            CompoundTag tag = stack.getTag();

            ssis$log(
                    "WRITE_ITEM"
                            + " | thread=" + Thread.currentThread().getName()
                            + " | item=" + stack.getItem()
                            + " | count=" + count
                            + " | tag=" + tag
                            + " | countMod="
                            + (tag != null && tag.contains("countMod")
                            ? tag.getInt("countMod")
                            : "NONE")
            );
        }
    }

    @Inject(
            method = "readItem",
            at = @At("RETURN")
    )
    private void ssis$debugRead(
            CallbackInfoReturnable<ItemStack> cir
    ) {
        ItemStack stack = cir.getReturnValue();

        if (stack == null || stack.isEmpty()) {
            return;
        }

        int count = stack.getCount();
        CompoundTag tag = stack.getTag();

        if (
                count >= 100 ||
                (tag != null && tag.contains("countMod"))
        ) {
            ssis$log(
                    "READ_ITEM"
                            + " | thread=" + Thread.currentThread().getName()
                            + " | item=" + stack.getItem()
                            + " | count=" + count
                            + " | tag=" + tag
                            + " | countMod="
                            + (tag != null && tag.contains("countMod")
                            ? tag.getInt("countMod")
                            : "NONE")
            );
        }
    }
}
