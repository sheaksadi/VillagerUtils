package me.sheaksadi.villagerutils;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class VillagerUtils  implements ModInitializer , ClientModInitializer {
    public static final String MOD_ID="villagerutils";
    public static final Logger LOGGER= LoggerFactory.getLogger("VillageUtils");



    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static VillagerUtils villagerUtils;
    private final KeyBinding keyBinding = new KeyBinding(MOD_ID+".key.test", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, I18n.translate(MOD_ID+".category.title")) ;
    


    private static boolean active = false;
    private boolean breaking = false;


    @Override
    public void onInitialize() {

    }


    @Override
    public void onInitializeClient() {
        if (villagerUtils==null) villagerUtils = this;
        KeyBindingHelper.registerKeyBinding(keyBinding);



            ClientTickEvents.START_CLIENT_TICK.register(client -> {
                if (keyBinding.wasPressed()){
                    toggle();
                }

                if (isActive()) {
                    BlockUtils.getVillagerTrades();

                }
            });

        LOGGER.info("its working");
    }
    public void villagerCycleMain(){

            BlockUtils.placeBlock(BlockUtils.getWorkstationPos());

    }


    public static void toggle (){
        if (!active){
            mc.player.sendMessage(Text.of("Villager Utils started"),false);
            active = true;
        }
        else{
            mc.player.sendMessage(Text.of("Villager Utils stopped"),false);
            active = false;
        }
    }
    public static boolean isActive(){
        return active;
    }


    public static void msg(){
//        if (mc.world == null) return;
//
//        BaseText message = new LiteralText("");
//        message.append("");
//        if (prefixTitle != null) message.append(getCustomPrefix(prefixTitle, prefixColor));
//        message.append(msg);
//
//
//
//        ((ChatHudAccessor) mc.inGameHud.getChatHud()).add(message, 0);
    }
}


