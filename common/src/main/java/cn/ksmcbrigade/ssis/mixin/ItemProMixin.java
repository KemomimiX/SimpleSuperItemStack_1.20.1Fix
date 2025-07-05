package cn.ksmcbrigade.ssis.mixin;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cn.ksmcbrigade.ssis.Config;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(Item.Properties.class)
public class ItemProMixin {

    @Shadow
    int maxStackSize;

    @Inject(method = "<init>",at = @At("TAIL"))
    public void init(CallbackInfo ci) throws IOException{
        Config.init();
        this.maxStackSize = Config.get();
    }

    @Inject(method = "stacksTo",at = @At("RETURN"))
    public void stackTo(int p_41488_, CallbackInfoReturnable<Item.Properties> cir) throws IOException{
        Config.init();
        this.maxStackSize = Config.get();
    }
}
