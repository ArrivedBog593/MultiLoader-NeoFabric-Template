package com.example.examplemod.fabric.mixin;

import com.example.examplemod.Constants;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// A Fabric-only mixin: listed in examplemod.fabric.mixins.json.
@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init()V", at = @At("HEAD"))
    private void examplemod$init(CallbackInfo info) {
        Constants.LOG.info("This line is printed by a Fabric mixin");
    }
}
