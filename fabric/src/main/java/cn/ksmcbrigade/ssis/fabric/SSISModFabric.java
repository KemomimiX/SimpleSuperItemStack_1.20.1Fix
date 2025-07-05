package cn.ksmcbrigade.ssis.fabric;

import net.fabricmc.api.ModInitializer;

import cn.ksmcbrigade.ssis.SSISMod;

public final class SSISModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        SSISMod.init();
    }
}
