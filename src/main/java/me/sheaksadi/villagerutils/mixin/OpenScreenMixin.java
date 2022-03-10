package me.sheaksadi.villagerutils.mixin;

import me.sheaksadi.villagerutils.VillagerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class OpenScreenMixin {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onOpenScreen(Screen screen, CallbackInfo info) {
        VillagerUtils.LOGGER.info("villager");
        if (screen instanceof MerchantScreen) {
            if (VillagerUtils.isActive()) {
                info.cancel();
            }
        }

    }


}
