package me.ammonium.anviloverride;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.enchantments.Enchantment.DAMAGE_ALL;

public final class AnvilOverride extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        System.out.println("AnvilOverride is now enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPrepareAnvilEvent(PrepareAnvilEvent e) {
        Map<Enchantment, Integer> applyEnchantments = new HashMap<>();
        AnvilInventory anvilInventory = e.getInventory();
        ItemStack secondItem = anvilInventory.getItem(1);

        if(secondItem == null || anvilInventory.getItem(0) == null){
            return;
        }
        if(secondItem.getType().equals(Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)secondItem.getItemMeta();
            Map<Enchantment, Integer> enchantments = enchantmentStorageMeta.getStoredEnchants();

            if(enchantments.get(DAMAGE_ALL) != null) {
                if (enchantments.get(DAMAGE_ALL) > 5) {
                    applyEnchantments.put(DAMAGE_ALL, enchantments.get(DAMAGE_ALL));
                }
            }

            if(enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL) != null) {
                if(enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL) > 4){
                    applyEnchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL,
                            enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL));
                }
            }

            if(enchantments.get(Enchantment.DURABILITY) != null){
                if(enchantments.get(Enchantment.DURABILITY) > 3){

                    applyEnchantments.put(Enchantment.DURABILITY,
                            enchantments.get(Enchantment.DURABILITY));
                }
            }

            if(enchantments.get(Enchantment.DIG_SPEED) != null){
                if(enchantments.get(Enchantment.DIG_SPEED) > 5){

                    applyEnchantments.put(Enchantment.DIG_SPEED,
                            enchantments.get(Enchantment.DIG_SPEED));
                }
            }


        }

        ItemStack firstItem = anvilInventory.getItem(0).clone();
        Map<Enchantment, Integer> firstItemEnchantments = firstItem.getEnchantments();

        // override to make sure already existing unsafe enchantments aren't removed

        if(anvilInventory.getItem(2) == null){ return; }

        ItemStack resultItem = anvilInventory.getItem(2).clone();
        Map<Enchantment, Integer> vanillaResultEnchantments = resultItem.getEnchantments();

        for (Enchantment differentEnchantment: firstItemEnchantments.keySet()) {
            if((!vanillaResultEnchantments.containsKey(differentEnchantment)) ||
                    vanillaResultEnchantments.get(differentEnchantment) <
                            firstItemEnchantments.get(differentEnchantment)){
                // big check to see if vanilla anvil mechanics removed unsafe enchantment
                System.out.println("Vanilla anvil removed unsafe enchant, readding...");
                resultItem.addUnsafeEnchantment(differentEnchantment, firstItemEnchantments.get(differentEnchantment));
            }
        }

        if(applyEnchantments.size() > 0) {
            for (Enchantment applyEnchantment: applyEnchantments.keySet()) {
                Integer enchantmentLevel = applyEnchantments.get(applyEnchantment);

                // add unsafe enchantments if needed
                System.out.println("Attempting to add unsafe enchantment...");
                System.out.println("Enchantment to add: " + applyEnchantment + ": Lvl." + enchantmentLevel);

                if (vanillaResultEnchantments.get(applyEnchantment) != null) {
                    if (vanillaResultEnchantments.get(applyEnchantment) > enchantmentLevel) {
                        System.out.println("Already has a higher encantment level: " +
                                vanillaResultEnchantments.get(applyEnchantment));
                        resultItem.addUnsafeEnchantment(applyEnchantment,  vanillaResultEnchantments.get(applyEnchantment));
                    } else {
                        boolean matchesType = applyEnchantment.canEnchantItem(resultItem);
                        System.out.println("Matches type: " + matchesType);
                        if (matchesType) {
                            // adds enchantment only if it is compatible
                            resultItem.addUnsafeEnchantment(applyEnchantment, enchantmentLevel);
                            System.out.println("Added unsafe enchantment.");
                            System.out.println(resultItem.getEnchantments().toString());
                        } else {
                            System.out.println("Item doesn't match enchantment's tool type.");
                        }
                    }
                }
            }
        }

        anvilInventory.setItem(2, resultItem);
        e.setResult(resultItem);

    }

}

