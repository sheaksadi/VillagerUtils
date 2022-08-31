package me.sheaksadi.villagerutils.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import me.sheaksadi.villagerutils.VillagerUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SelectEnchantScreen extends CottonClientScreen {
    MinecraftClient mc = VillagerUtils.mc;


    public SelectEnchantScreen(GuiDescription description) {
        super(description);
    }
}
