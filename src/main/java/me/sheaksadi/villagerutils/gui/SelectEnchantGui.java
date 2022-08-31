package me.sheaksadi.villagerutils.gui;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import me.sheaksadi.villagerutils.VillagerUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.BiConsumer;

public class SelectEnchantGui extends LightweightGuiDescription {
    public SelectEnchantGui() {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(300, 400);
        root.setInsets(Insets.ROOT_PANEL);
        WLabel label = new WLabel(new LiteralText("Select Enchantments"), 0x00E676);
        root.add(label, 6, 0, 3, 3);


//        for (Enchantment enchantment : VillagerUtils.enchantments){
//            WButton button = new WButton(new TranslatableText(enchantment.getName(enchantment.getMaxLevel()).getString()));
//            button.setOnClick(() -> {
//                // This code runs on the client when you click the button.
//                VillagerUtils.enchantments.remove(enchantment);
//                root.remove(button);
//
//            });
//            root.add(button, 4, VillagerUtils.enchantments.indexOf(enchantment)+4, 4, 2);
//
//        }

        BiConsumer<Enchantment, EnchantPanel> configurator = (Enchantment enchantment, EnchantPanel ePanel) -> {
            ePanel.button.setLabel(new TranslatableText(enchantment.getName(enchantment.getMaxLevel()).getString()));
            ePanel.button.setOnClick(() -> {
                // This code runs on the client when you click the button.

                ePanel.remove(ePanel.button);
                VillagerUtils.enchantments.add(enchantment);
               VillagerUtils.allEnchantments.remove(enchantment);
                VillagerUtils.sort((ArrayList<Enchantment>) VillagerUtils.allEnchantments);
                VillagerUtils.sort((ArrayList<Enchantment>) VillagerUtils.enchantments);

                root.validate(this);

            });
            ePanel.button.onShown();
            ePanel.button.validate(this);
        };

        BiConsumer<Enchantment, EnchantPanel> selectedConfigurator = (Enchantment enchantment, EnchantPanel ePanel) -> {

            ePanel.button.setLabel(new TranslatableText(enchantment.getName(enchantment.getMaxLevel()).getString()));
            ePanel.button.setOnClick(() -> {
                // This code runs on the client when you click the button.
                ePanel.remove(ePanel.button);
                VillagerUtils.allEnchantments.add(enchantment);
                VillagerUtils.enchantments.remove(enchantment);
                VillagerUtils.sort((ArrayList<Enchantment>) VillagerUtils.enchantments);
                VillagerUtils.sort((ArrayList<Enchantment>) VillagerUtils.allEnchantments);
                root.validate(this);

            });


        };


        WListPanel<Enchantment, EnchantPanel> panel = new WListPanel<>(VillagerUtils.allEnchantments, EnchantPanel::new, configurator);
        panel.setListItemHeight(18);
        root.add(panel, 2, 4, 8, 14);

        WListPanel<Enchantment,EnchantPanel> selectedItemPanel = new WListPanel<>(VillagerUtils.enchantments,EnchantPanel::new, selectedConfigurator);
        selectedItemPanel.setListItemHeight(18);
        root.add(selectedItemPanel, 10, 4, 8, 14);

//        WSprite icon = new WSprite(new Identifier("minecraft:textures/item/redstone.png"));
//        root.add(icon, 0, 2, 1, 1);

//        WButton button = new WButton(new TranslatableText("gui"));
//        root.add(button, 0, 3, 4, 1);

        WToggleButton tButton = new WToggleButton(new LiteralText("Search for max level"));
        tButton.setToggle(VillagerUtils.rollForMax);
        tButton.setOnToggle((toggle)->{
            VillagerUtils.rollForMax= !toggle;
            VillagerUtils.LOGGER.info("togle");
        });
        root.add(tButton, 1, 2, 8, 2);
        root.validate(this);

    }


}
