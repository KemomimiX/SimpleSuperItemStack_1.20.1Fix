package cn.ksmcbrigade.ssis.mixin;

import cn.ksmcbrigade.ssis.Config;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(Item.Properties.class)
public abstract class ItemProMixin {
    @Shadow public abstract <T> Item.Properties component(DataComponentType<T> p_333852_, T p_330859_);

    @Inject(method = "<init>",at = @At("TAIL"))
    public void init(CallbackInfo ci) throws IOException {
        Config.init();
        this.component(DataComponents.MAX_STACK_SIZE,Config.get());
    }

    @Inject(method = "stacksTo",at = @At("TAIL"))
    public void max(int pMaxStackSize, CallbackInfoReturnable<Item.Properties> cir) throws IOException {
        Config.init();
        this.component(DataComponents.MAX_STACK_SIZE,Config.get());
    }
}
