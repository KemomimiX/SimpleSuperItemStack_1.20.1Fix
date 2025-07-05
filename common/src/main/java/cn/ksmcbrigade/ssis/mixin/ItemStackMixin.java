package cn.ksmcbrigade.ssis.mixin;

import cn.ksmcbrigade.ssis.Config;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

import static net.minecraft.world.item.ItemStack.ITEM_NON_AIR_CODEC;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Mutable
    @Shadow @Final public static Codec<ItemStack> CODEC;

    @Inject(method = "<clinit>",at = @At("TAIL"))
    private static void clinit(CallbackInfo ci) throws IOException {
        Config.init();
        CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create((p_347288_) -> p_347288_.group(ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder), ExtraCodecs.intRange(1, Config.get()).fieldOf("count").orElse(1).forGetter(ItemStack::getCount), DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)).apply(p_347288_, ItemStack::new)));
    }

    @Inject(method = "getMaxStackSize",at = @At("RETURN"),cancellable = true)
    private void max(CallbackInfoReturnable<Integer> cir) throws IOException {
        Config.init();
        cir.setReturnValue(Config.get());
        cir.cancel();
    }
}
