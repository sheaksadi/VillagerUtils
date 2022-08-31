package me.sheaksadi.villagerutils;

import me.sheaksadi.villagerutils.gui.SelectEnchantGui;
import me.sheaksadi.villagerutils.gui.SelectEnchantScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.*;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class VillagerUtils  implements ModInitializer , ClientModInitializer {
    public static final String MOD_ID="villagerutils";
    public static final Logger LOGGER= LoggerFactory.getLogger("VillageUtils");



    public static MinecraftClient mc = MinecraftClient.getInstance();
    public static VillagerUtils villagerUtils;
    private final KeyBinding keyBinding = new KeyBinding(MOD_ID+".key.test", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, I18n.translate(MOD_ID+".category.title")) ;
    private final KeyBinding configKeyBinding = new KeyBinding(MOD_ID+".key.config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, I18n.translate(MOD_ID+".category.title")) ;


    public static List<Enchantment> enchantments = new ArrayList<>();
    public static List<Enchantment> allEnchantments = new ArrayList<>();
    public static boolean rollForMax = true;

    private static boolean active = false;
    private static boolean VillagerScreen = false;
    private boolean breaking = false;
    public static int stage = 1;
    public static Mouse mouse;


    @Override
    public void onInitialize() {
        LOGGER.info("Wtf Dude this is not a server mod");
    }

    /*
    1 right click
    1.5 profession check
    2 check trades
    3

     */



    @Override
    public void onInitializeClient() {
        if (villagerUtils==null) villagerUtils = this;
        KeyBindingHelper.registerKeyBinding(keyBinding);
        KeyBindingHelper.registerKeyBinding(configKeyBinding);
        mouse = new Mouse(mc);

            setEnchantmentList();
            ClientTickEvents.START_CLIENT_TICK.register(client -> {
                if (keyBinding.wasPressed()){
                     toggle();
                }
                if (configKeyBinding.wasPressed()){
                     mc.setScreen(new SelectEnchantScreen(new SelectEnchantGui()));
                }


                if (isActive()) {
                    if (!mouse.isCursorLocked()){
                        mouse.lockCursor();
                    }
//                    assert mc.player != null;
//                    mc.player.sendMessage(new LiteralText(String.valueOf(stage)),true);
                    if (stage==1){
                        if (BlockUtils.lookingAtValidVillager()==null) {
                            mc.player.sendMessage(new LiteralText("Not looking at Villager"),false);
                            toggle();
                        }
                        if (BlockUtils.lookingAtValidVillager()==VillagerProfession.NITWIT){
                            mc.player.sendMessage(new LiteralText("Villager cant be a Nitwit"),false);
                            toggle();
                        }

                        BlockState state = MinecraftClient.getInstance().world.getBlockState(BlockUtils.getWorkstationPos());
                        if (BlockUtils.lookingAtValidVillager()== VillagerProfession.NONE && state.isAir()){
                            stage=3;
                        }
                        BlockUtils.getVillagerTrades();
                    }
                    if (stage==2){
                        BlockUtils.breakBlock(BlockUtils.getWorkstationPos());
                    }
                    if (stage==3){
                        BlockUtils.placeBlock(BlockUtils.getWorkstationPos());
                    }


                }
            });

        LOGGER.info("its working");
    }
    public void villagerCycleMain(){

            BlockUtils.placeBlock(BlockUtils.getWorkstationPos());

    }


    public static boolean isVillagerScreen() {
        return VillagerScreen;
    }

    public static void toggle (){
        assert mc.player != null;
        stage=1;
        if (!active){
            mc.player.sendMessage(new LiteralText("Villager Utils started").formatted(Formatting.AQUA),false);
            active = true;
            VillagerScreen=true;
        }
        else{
            mc.player.sendMessage(new LiteralText("Villager Utils stopped").styled(style -> style.withColor(0xFFC107)),false);
            active = false;
            VillagerScreen=false;
        }
    }
    public static boolean isActive(){
        return active;
    }
    private void setEnchantmentList(){
        //all-purpose
        allEnchantments.add(Enchantments.MENDING);
        allEnchantments.add(Enchantments.UNBREAKING);
        allEnchantments.add(Enchantments.VANISHING_CURSE);
        //armour
        allEnchantments.add(Enchantments.BINDING_CURSE);
        allEnchantments.add(Enchantments.AQUA_AFFINITY);
        allEnchantments.add(Enchantments.BLAST_PROTECTION);
        allEnchantments.add(Enchantments.DEPTH_STRIDER);
        allEnchantments.add(Enchantments.FEATHER_FALLING);
        allEnchantments.add(Enchantments.FIRE_PROTECTION);
        allEnchantments.add(Enchantments.FROST_WALKER);
        allEnchantments.add(Enchantments.PROJECTILE_PROTECTION);
        allEnchantments.add(Enchantments.PROTECTION);
        allEnchantments.add(Enchantments.RESPIRATION);
        allEnchantments.add(Enchantments.THORNS);
        //tools
        allEnchantments.add(Enchantments.EFFICIENCY);
        allEnchantments.add(Enchantments.FORTUNE);
        allEnchantments.add(Enchantments.SILK_TOUCH);
        allEnchantments.add(Enchantments.LUCK_OF_THE_SEA);
        allEnchantments.add(Enchantments.LURE);
        //Weapons
        allEnchantments.add(Enchantments.BANE_OF_ARTHROPODS);
        allEnchantments.add(Enchantments.LOOTING);
        allEnchantments.add(Enchantments.FIRE_ASPECT);
        allEnchantments.add(Enchantments.IMPALING);
        allEnchantments.add(Enchantments.KNOCKBACK);
        allEnchantments.add(Enchantments.SHARPNESS);
        allEnchantments.add(Enchantments.SMITE);
        allEnchantments.add(Enchantments.SWEEPING);
        allEnchantments.add(Enchantments.CHANNELING);
        allEnchantments.add(Enchantments.FLAME);
        allEnchantments.add(Enchantments.INFINITY);
        allEnchantments.add(Enchantments.LOYALTY);
        allEnchantments.add(Enchantments.RIPTIDE);
        allEnchantments.add(Enchantments.MULTISHOT);
        allEnchantments.add(Enchantments.PIERCING);
        allEnchantments.add(Enchantments.PUNCH);
        allEnchantments.add(Enchantments.POWER);
        allEnchantments.add(Enchantments.QUICK_CHARGE);
        sort((ArrayList<Enchantment>) allEnchantments);
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
    public static void sort(ArrayList<Enchantment> list) {
        list.sort(Comparator.comparing(e -> e.getName(e.getMaxLevel()).getString()));
    }
}

