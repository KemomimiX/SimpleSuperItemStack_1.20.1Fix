package cn.ksmcbrigade.ssis.forge;

import cn.ksmcbrigade.ssis.SSISMod;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SSISMod.MOD_ID)
public final class SSISModForge {
    public SSISModForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(SSISMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        SSISMod.init();
    }
}
