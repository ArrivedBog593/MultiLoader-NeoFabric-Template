package com.example.examplemod.mixin;

import com.example.examplemod.Constants;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// A common mixin, applied on every loader: listed in examplemod.mixins.json.
@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void examplemod$init(CallbackInfo info) {
        Constants.LOG.info("This line is printed by a common mixin, on Minecraft {}",
                SharedConstants.getCurrentVersion().getName());
    }
}
