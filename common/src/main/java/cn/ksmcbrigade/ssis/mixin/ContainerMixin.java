package cn.ksmcbrigade.ssis.mixin;

import cn.ksmcbrigade.ssis.Config;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.IOException;

@Mixin(Container.class)
public interface ContainerMixin {
    /**
     * @author KSmc_brigade
     * @reason overwrite to support higher item count
     */
    @Overwrite
    default int getMaxStackSize() throws IOException {
        Config.init();
        return Config.get();
    }
}
