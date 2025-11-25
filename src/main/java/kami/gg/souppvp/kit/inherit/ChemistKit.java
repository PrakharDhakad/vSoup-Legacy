package kami.gg.souppvp.kit.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ChemistKit extends Kit {

    @Override
    public String getName() {
        return "Chemist";
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.LEGENDARY;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.POTION).durability(16428).build();
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("&7Begin with splash potions of instant damage and");
        description.add("&7poisons. After each kill, get a refill of splash");
        description.add("&7potions to continue irritating enemies.");

        return description;
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(new ItemBuilder(Material.IRON_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).enchantment(Enchantment.DURABILITY, 3).build());
        itemStacks.add(new ItemBuilder(Material.POTION).amount(3).durability(16428).build());
        itemStacks.add(new ItemBuilder(Material.POTION).amount(1).durability(16420).build());
        return itemStacks;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.CHAINMAIL_BOOTS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.CHAINMAIL_LEGGINGS).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.IRON_CHESTPLATE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build(),
                new ItemBuilder(Material.CHAINMAIL_HELMET).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        List<PotionEffect> potionEffects = new ArrayList<>();
        potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        return potionEffects;
    }

    @Override
    public void onSelect(Player player) {

    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        Player killer = event.getEntity().getKiller();
        Player target = event.getEntity();
        if (event.getEntity().getKiller() != null){
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(killer.getUniqueId());
            if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) {
                return;
            }
            Kit kit = SoupPvP.getInstance().getKitsHandler().getKitByName("Chemist");
            if (profile.getCurrentKit().equals(kit)){
                ItemStack splashPotionOfHarming = new ItemBuilder(Material.POTION).durability(16428).build();
                ItemStack twoSplashPotionOfHarming = new ItemBuilder(Material.POTION).amount(2).durability(16428).build();
                if (killer.getInventory().firstEmpty() == -1){
                    for (int i=0; i<killer.getInventory().getSize(); i++){
                        ItemStack item = killer.getInventory().getItem(i);
                        if (item != null){
                            if (item.getType() == Material.BOWL || item.getType() == Material.MUSHROOM_SOUP){
                                killer.getInventory().setItem(i, twoSplashPotionOfHarming);
                                return;
                            }
                            if (item.isSimilar(splashPotionOfHarming)){
                                killer.getInventory().setItem(i, twoSplashPotionOfHarming);
                                return;
                            }
                        }
                    }
                } else {
                    killer.getInventory().addItem(twoSplashPotionOfHarming);
                }
            }
        }
    }

}
