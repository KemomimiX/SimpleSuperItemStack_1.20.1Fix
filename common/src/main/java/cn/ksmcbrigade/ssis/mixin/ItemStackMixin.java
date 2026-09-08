package cn.ksmcbrigade.ssis.mixin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cn.ksmcbrigade.ssis.Config;

import java.util.Optional;
import java.io.IOException;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow private int count;

    @Mutable
    @Shadow @Final public static Codec<ItemStack> CODEC;

    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void onGetMaxStackSize(CallbackInfoReturnable<Integer> cir) throws IOException{
        Config.init();
        cir.setReturnValue(Config.get());
    }

    @Redirect(method = "save",at = @At(value = "INVOKE", target ="Lnet/minecraft/nbt/CompoundTag;putByte(Ljava/lang/String;B)V"))
    private void write(CompoundTag instance, String key, byte value){
        instance.putByte(key,value);
        instance.putInt("countMod",this.count);
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V",at = @At(value = "TAIL"))
    private void read(CompoundTag nbt, CallbackInfo ci){
        if(nbt.contains("countMod")){
            this.count = nbt.getInt("countMod");
        }
    }

    @Inject(method = "<clinit>",at = @At("TAIL"))
    private static void clinit(CallbackInfo ci){
        CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem), Codec.INT.fieldOf("Count").forGetter(ItemStack::getCount), CompoundTag.CODEC.optionalFieldOf("tag").forGetter((itemStack) -> {
                return Optional.ofNullable(itemStack.getTag());
            }),Codec.INT.optionalFieldOf("countMod").forGetter((o)-> Optional.of(o.getCount()))).apply(instance, (item, integer, compoundTag, integer2) -> {
                int d = integer;
                if(integer2.isPresent()){
                    d = integer2.get();
                }
                return new ItemStack(item,d);
            });
        });
    }
}