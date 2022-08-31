package me.sheaksadi.villagerutils.mixin;

import me.sheaksadi.villagerutils.VillagerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class OpenScreenMixin {


    @Shadow public abstract void updateWindowTitle();

    @Shadow @Final private Window window;

    @Shadow public boolean skipGameRender;

    @Shadow @Final public Mouse mouse;

    @Shadow @Final private SoundManager soundManager;

    @Inject(method = "setScreen", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/render/BufferRenderer;unbindAll()V"), cancellable = true)
    private void onOpenScreen(Screen screen, CallbackInfo info) {
        if (screen == null) {
            this.soundManager.resumeAll();
            this.mouse.lockCursor();
        }
        if (VillagerUtils.isVillagerScreen()) {
            if (screen != null) {
                KeyBinding.unpressAll();
                ((Screen)screen).init(VillagerUtils.mc, this.window.getScaledWidth(), this.window.getScaledHeight());
                this.skipGameRender=false;
            }
            this.updateWindowTitle();
           info.cancel();
        }

    }


}
