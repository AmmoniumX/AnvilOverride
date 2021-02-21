package me.ammonium.anviloverride;

import org.apache.commons.lang.StringUtils;
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

    enum ToolType{ DIGGING, SWORD, ARMOR, ANY }

    boolean matchesToolType(ToolType type, Material item){
        switch (type){
            case ARMOR -> {
                return StringUtils.endsWithAny(item.toString(), new String[]{"HELMET", "CHESTPLATE",
                "LEGGINGS", "BOOTS"}); }

            case SWORD -> { return item.toString().endsWith("SWORD"); }

            case DIGGING -> {
                return StringUtils.endsWithAny(item.toString(), new String[]{"PICKAXE", "AXE",
                        "SHOVEL"}); }

            case ANY -> {return true;}

            default -> {
                return false;
            }
        }
    }


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
        Enchantment applyEnchantment = null;
        Integer enchantmentLevel = null;
        AnvilInventory anvilInventory = e.getInventory();
        ItemStack secondItem = anvilInventory.getSecondItem();
        ToolType toolType = null;

        if(secondItem == null || anvilInventory.getFirstItem() == null){
            return;
        }
        if(secondItem.getType().equals(Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)secondItem.getItemMeta();
            Map<Enchantment, Integer> enchantments = enchantmentStorageMeta.getStoredEnchants();

            if(enchantments.get(Enchantment.DAMAGE_ALL) != null) {
                if (enchantments.get(Enchantment.DAMAGE_ALL) > 5) {
                    applyEnchantment = Enchantment.DAMAGE_ALL;
                    enchantmentLevel = enchantments.get(Enchantment.DAMAGE_ALL);
                    toolType = ToolType.SWORD;
                }
            }

            if(enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL) != null) {
                if(enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL) > 4){
                    applyEnchantment = Enchantment.PROTECTION_ENVIRONMENTAL;
                    enchantmentLevel = enchantments.get(Enchantment.PROTECTION_ENVIRONMENTAL);
                    toolType = ToolType.ARMOR;
                }
            }

            if(enchantments.get(Enchantment.DURABILITY) != null){
                if(enchantments.get(Enchantment.DURABILITY) > 3){
                    applyEnchantment = Enchantment.DURABILITY;
                    enchantmentLevel = enchantments.get(Enchantment.DURABILITY);
                    toolType = ToolType.ANY;
                }
            }

            if(enchantments.get(Enchantment.DIG_SPEED) != null){
                if(enchantments.get(Enchantment.DIG_SPEED) > 5){
                    applyEnchantment = Enchantment.DIG_SPEED;
                    enchantmentLevel = enchantments.get(Enchantment.DIG_SPEED);
                    toolType = ToolType.DIGGING;
                }
            }


        }

        ItemStack firstItem = anvilInventory.getFirstItem().clone();
        Map<Enchantment, Integer> firstItemEnchantments = firstItem.getEnchantments();

        if(applyEnchantment != null && enchantmentLevel != null) {
            // add unsafe enchantments if needed
            System.out.println("Attempting to add unsafe enchantment...");
            System.out.println("Enchantment to add: " + applyEnchantment + ": Lvl." + enchantmentLevel);

            if (firstItemEnchantments.get(applyEnchantment) != null) {
                if (firstItemEnchantments.get(applyEnchantment) >= enchantmentLevel) {
                    return;
                }
            }
            if (matchesToolType(toolType, firstItem.getType())) {
                // adds enchantment only if it is compatible
                firstItem.addUnsafeEnchantment(applyEnchantment, enchantmentLevel);
            } else {
                System.out.println("Item doesn't match enchantment's tool type.");
            }

            anvilInventory.setResult(firstItem);
            e.setResult(firstItem);

        } else if (anvilInventory.getResult() != null){
            // if there's no unsafe enchantments to add, still override to make sure already existing unsafe
            // enchantments aren't removed
            ItemStack vanillaResult = anvilInventory.getResult();
            Map<Enchantment, Integer> vanillaResultEnchantments = vanillaResult.getEnchantments();

            for (Enchantment differentEnchantment: firstItemEnchantments.keySet()) {
                if(!vanillaResultEnchantments.containsKey(differentEnchantment) ||
                    vanillaResultEnchantments.get(differentEnchantment) <
                    firstItemEnchantments.get(differentEnchantment)){
                    // big check to see if vanilla anvil mechanics removed unsafe enchantment
                    System.out.println("Vanilla anvil removed unsafe enchant, readding...");
                    vanillaResult.addUnsafeEnchantment(differentEnchantment, firstItemEnchantments.get(differentEnchantment));
                }
            }

            anvilInventory.setResult(vanillaResult);
            e.setResult(vanillaResult);

        }

        if (anvilInventory.getRepairCost() >= anvilInventory.getMaximumRepairCost()) {
            anvilInventory.setRepairCost(anvilInventory.getRepairCost() % anvilInventory.getMaximumRepairCost());
        }

    }

}

