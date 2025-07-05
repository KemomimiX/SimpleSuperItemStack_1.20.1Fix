package cn.ksmcbrigade.ssis.mixin;

import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cn.ksmcbrigade.ssis.Config;

import java.io.IOException;

@Mixin(Container.class)
public interface IInventoryMixin {
    /**
     * @author KSmc_brigade
     * @reason nothing
     */
    @Overwrite
    default int getMaxStackSize() throws IOException{
        Config.init();
        return Config.get();
    }
}
