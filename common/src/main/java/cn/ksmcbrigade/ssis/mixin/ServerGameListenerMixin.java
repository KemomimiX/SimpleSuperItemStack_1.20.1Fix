package cn.ksmcbrigade.ssis.mixin;

import cn.ksmcbrigade.ssis.Config;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.io.IOException;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGameListenerMixin {
    @ModifyConstant(method = "handleSetCreativeModeSlot",constant = @Constant(intValue = 64))
    public int action(int constant) throws IOException {
        Config.init();
        return Config.get();
    }
}
