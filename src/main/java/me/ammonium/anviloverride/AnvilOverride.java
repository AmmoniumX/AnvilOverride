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

import java.util.Map;

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
        boolean override = false;
            Enchantment applyEnchantment = null;
        Integer enchantmentLevel = null;
        AnvilInventory anvilInventory = e.getInventory();
        ItemStack secondItem = anvilInventory.getSecondItem();
        if(secondItem == null || anvilInventory.getFirstItem() == null){
            return;
        }
        if(secondItem.getType().equals(Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)secondItem.getItemMeta();
            Map<Enchantment, Integer> enchantments = enchantmentStorageMeta.getStoredEnchants();

            if(enchantments.get(Enchantment.DAMAGE_ALL) != null) {
                if (enchantments.get(Enchantment.DAMAGE_ALL) > 5) {
                    override = true;
                    applyEnchantment = Enchantment.DAMAGE_ALL;
                    enchantmentLevel = enchantments.get(Enchantment.DAMAGE_ALL);
                }
            }

            if(enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL) != null) {
                if(enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL) > 4){
                    override = true;
                    applyEnchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
                    enchantmentLevel = enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL);
                }
            }

            if(enchantments.get(Enchantment.DURABILITY) != null){
                if(enchantments.get(Enchantment.DURABILITY) > 3){
                    override = true;
                    applyEnchantment = Enchantment.DURABILITY;
                    enchantmentLevel = enchantments.get(Enchantment.DURABILITY);
                }
            }


        }
        if(override){
            System.out.println("Starting override...");
            System.out.println("Enchantment to override: " + applyEnchantment + " " + enchantmentLevel);
            assert enchantmentLevel != null;
            ItemStack resultItem = anvilInventory.getFirstItem().clone();
            Map<Enchantment, Integer> resultEnchants = resultItem.getEnchantments();
            if(resultEnchants.get(applyEnchantment) != null) {
                if(resultEnchants.get(applyEnchantment) >= enchantmentLevel){
                    return;
                }
            }
            resultItem.addUnsafeEnchantment(applyEnchantment, enchantmentLevel);
            anvilInventory.setRepairCost(10);
            anvilInventory.setResult(resultItem);
            e.setResult(resultItem);

        }
    }

}

