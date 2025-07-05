package cn.ksmcbrigade.ssis.mixin;

import cn.ksmcbrigade.ssis.Config;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "reloadResourcePacks(ZLnet/minecraft/client/Minecraft$GameLoadCookie;)Ljava/util/concurrent/CompletableFuture;",at = @At("TAIL"))
    public void reloadConfig(CallbackInfoReturnable<CompletableFuture<Void>> cir) throws IOException {
        Config.reload();
    }
}
