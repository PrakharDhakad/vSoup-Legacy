package kami.gg.souppvp.kit.inherit;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.KitRarity;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.timer.Timer;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.DurationFormatter;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.XPBarTimer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SpidermanKit extends Kit {

    @Override
    public String getName() {
        return "Spiderman";
    }

    @Override
    public KitRarity getRarityType() {
        return KitRarity.ULTIMATE;
    }

    @Override
    public Integer getPrice() {
        return getRarityType().getPrice();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.WEB).build();
    }

    @Override
    public List<String> getDescription() {
        List<String> description = new ArrayList<>();
        description.add("&7Be a superhero and fight criminals like your enemies. Using your");
        description.add("&7web shooter, trap and slow down enemies to catch them quickly.");
        return description;
    }

    @Override
    public List<ItemStack> getCombatEquipments() {
        List<ItemStack> itemStacks = new ArrayList<>();
        itemStacks.add(new ItemBuilder(Material.IRON_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).enchantment(Enchantment.DURABILITY, 1).build());
        itemStacks.add(new ItemBuilder(Material.WEB).name(CC.translate("&dWeb Shooter")).build());
        return itemStacks;
    }

    @Override
    public ItemStack[] getArmor() {
        return new ItemStack[]{
                new ItemBuilder(Material.LEATHER_BOOTS).color(Color.BLUE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).color(Color.RED).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build(),
                new ItemBuilder(Material.LEATHER_CHESTPLATE).color(Color.BLUE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build(),
                new ItemBuilder(Material.LEATHER_HELMET).color(Color.RED).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.DURABILITY, 3).build()
        };
    }

    @Override
    public List<PotionEffect> getPotionEffects() {
        List<PotionEffect> potionEffects = new ArrayList<>();
        potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        potionEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        return potionEffects;
    }

    @Override
    public void onSelect(Player player) {

    }

    ArrayList<BlockState> toRollback = new ArrayList<>();

    public ArrayList<Location> getSurroundingLocations(Location location) {
        ArrayList<Location> list = new ArrayList<Location>();
        list.add(location.clone().add(1.0D, 1.0D, -1.0D));
        list.add(location.clone().add(-1.0D, 1.0D, -1.0D));
        list.add(location.clone().add(1.0D, 1.0D, 1.0D));
        list.add(location.clone().add(-1.0D, 1.0D, 1.0D));
        list.add(location.clone().add(0.0D, 1.0D, 0.0D));
        list.add(location.clone().add(-1.0D, 1.0D, 0.0D));
        list.add(location.clone().add(1.0D, 1.0D, 0.0D));
        list.add(location.clone().add(0.0D, 1.0D, -1.0D));
        list.add(location.clone().add(0.0D, 1.0D, 1.0D));

        list.add(location.clone().add(-1.0D, 1.0D, 0.0D));
        list.add(location.clone().add(0.0D, 1.0D, 1.0D));
        list.add(location.clone().add(0.0D, 1.0D, -1.0D));
        list.add(location.clone().add(1.0D, 1.0D, -1.0D));
        list.add(location.clone().add(1.0D, 1.0D, 1.0D));
        list.add(location.clone().add(-0.0D, 1.0D, 1.0D));
        list.add(location.clone().add(-1.0D, 1.0D, -1.0D));
        list.add(location.clone().add(1.0D, 1.0D, -1.0D));
        list.add(location.clone().add(1.0D, 1.0D, 0.0D));
        return list;
    }

    public void returnBack(BlockState blockState) {
        if (blockState instanceof Sign) {
            final Sign sign = (Sign)blockState;
            final Location location = sign.getLocation();
            location.getWorld().getBlockAt(location).setType(blockState.getType());
            final Sign sign2 = (Sign)location.getWorld().getBlockAt(location).getState();
            for (int i = 0; i < 4; ++i) {
                sign2.setLine(i, sign.getLines()[i]);
            }
            sign2.update(true);
        }
        else {
            blockState.update(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Kit kit = SoupPvP.getInstance().getKitsHandler().getKitByName("Spiderman");
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.isInEvent() || profile.getProfileState() == ProfileState.SPAWN) return;
        if (profile.getCurrentKit().equals(kit)){
            if (event.getPlayer().getItemInHand().isSimilar(this.getCombatEquipments().get(1)) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
                event.setCancelled(true);
                player.updateInventory();
                if (SoupPvP.getInstance().getTimersHandler().hasTimer(player.getUniqueId(), "Web Shooter", true)) {
                    player.sendMessage(ChatColor.RED + "You can't use this for another " + ChatColor.YELLOW + DurationFormatter.getRemaining(SoupPvP.getInstance().getTimersHandler().getRemaining(player.getUniqueId(), "Web Shooter", true), true) + ChatColor.RED + ".");
                    return;
                }
                SoupPvP.getInstance().getTimersHandler().addPlayerTimer(player.getUniqueId(), new Timer("Web Shooter", TimeUnit.SECONDS.toMillis(45)), true);
                XPBarTimer.runXpBar(player, 45);
                @SuppressWarnings("deprecation")
                FallingBlock block = event.getPlayer().getWorld().spawnFallingBlock(player.getEyeLocation(), Material.STAINED_GLASS, (byte) 0);
                block.setMetadata("avatar", new FixedMetadataValue(SoupPvP.getInstance(), player.getUniqueId()));
                block.setDropItem(false);
                player.getLocation().getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 1F, 1F);

                block.setVelocity(event.getPlayer().getEyeLocation().getDirection().multiply(2.3).add(new Vector(0, 0.3, 0)));
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (block.isDead() || !block.isValid() || !player.isOnline()) {
                            cancel();
                            return;
                        }

                        for(Entity entity : block.getNearbyEntities(3, 3, 3)) {
                            if(entity instanceof Player) {
                                Profile profile1 = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getPlayer().getUniqueId());
                                if(entity.getUniqueId().equals(player.getUniqueId()) || profile1.getProfileState() == ProfileState.SPAWN) {
                                    continue;
                                }

                                block.remove();
                                cancel();

                                Player found = (Player) entity;

                                Location location = found.getLocation();
                                ArrayList<BlockState> blockList = new ArrayList<>();
                                Block block = location.getBlock();

                                if(block.getType() == Material.WEB) {
                                    location = location.add(0, 1.0D, 0);
                                }

                                for(Location scan : getSurroundingLocations(location)) {
                                    if(scan.getBlock().getType().equals(Material.WEB)) {
                                        continue;
                                    }

                                    if(scan.getBlock().getType() != Material.AIR) continue;

                                    blockList.add(scan.getBlock().getState());
                                }

                                for (BlockState blockState : blockList) {
                                    toRollback.add(blockState);
                                    blockState.getBlock().setType(Material.WEB);
                                }

                                Bukkit.getScheduler().scheduleSyncDelayedTask(SoupPvP.getInstance(), () -> {
                                    for (BlockState blockState : blockList) {
                                        returnBack(blockState);
                                        toRollback.remove(blockState);
                                    }
                                }, 120L);

                                break;
                            }
                        }
                    }
                }.runTaskTimer(SoupPvP.getInstance(), 2L, 2L);
            }
        }
    }

}
