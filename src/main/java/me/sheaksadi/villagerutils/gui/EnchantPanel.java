package me.sheaksadi.villagerutils.gui;

import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import me.sheaksadi.villagerutils.VillagerUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class EnchantPanel extends WPlainPanel {
    WButton button;

    public EnchantPanel() {

        button = new WButton();
        this.add(button, 0,0, 120, 18);
        this.setSize(120, 18);
       // this.validate(this.getHost());
    }
}
