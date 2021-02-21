import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

public class illegalAnvils implements Listener {
    @EventHandler
    public void onPrepareAnvilEvent(PrepareAnvilEvent e) {
        System.out.println("PrepareAnvilEvent detected.");
        boolean override = false;
        Enchantment applyEnchantment = null;
        Integer enchantmentLevel = null;
        AnvilInventory anvilInventory = e.getInventory();
        System.out.println(Arrays.toString(anvilInventory.getContents()));
        ItemStack secondItem = anvilInventory.getSecondItem();
        if(secondItem == null || anvilInventory.getFirstItem() == null){
            return;
        }

        if(secondItem.getType().equals(Material.ENCHANTED_BOOK)) {
            Map<Enchantment, Integer> enchantments = secondItem.getEnchantments();

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
            assert enchantmentLevel != null;
            ItemStack resultItem = anvilInventory.getFirstItem();
            assert resultItem != null;
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
            // none of those two work

        }
    }
}
