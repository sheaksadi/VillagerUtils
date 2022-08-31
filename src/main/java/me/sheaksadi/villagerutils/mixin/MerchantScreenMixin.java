package me.sheaksadi.villagerutils.mixin;


import me.sheaksadi.villagerutils.VillagerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public class MerchantScreenMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onOpenScreen(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {


            if (VillagerUtils.isVillagerScreen()) {
                ci.cancel();
            }


    }
//
//    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
//    private void onOpenScreen(CallbackInfo ci) {
//        VillagerUtils.LOGGER.info("no init");
//            if (VillagerUtils.isVillagerScreen()) {
//                ci.cancel();
//            }
//
//
//    }


}
