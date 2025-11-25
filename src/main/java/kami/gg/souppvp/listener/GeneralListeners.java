package kami.gg.souppvp.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.handlers.SpawnHandler;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class GeneralListeners implements Listener {

    @EventHandler
    public void onPlayerItemDamageEvent(PlayerItemDamageEvent event) {
        Random random = new Random();
        if (50 < random.nextInt(100)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event){
        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);
        for (Player player : Bukkit.getOnlinePlayers()){
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        }

        Profile playerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
        Perk profilePerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(playerProfile.getActivePerks().get(2));
        Perk conartistPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Conartist");
        if (profilePerk == conartistPerk){
            if (new Random().nextInt(101) <= 50) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(SoupPvP.getInstance(), () -> event.getEntity().spigot().respawn(), 2L);
                return;
            }
        }
        ItemStack mushroom = new ItemStack(Material.MUSHROOM_SOUP);
        for (int i=0; i<9; i++){
            event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), mushroom);
        }
        Location deathLocation = event.getEntity().getLocation();
        TaskUtil.runLater(() -> {
            for (Entity entity : Bukkit.getServer().getWorld("world").getEntities()) {
                if (entity.getLocation().distance(deathLocation) > 5){
                    return;
                }
                if (entity.getType().equals(EntityType.DROPPED_ITEM)) {
                    entity.remove();
                }
            }
        }, 60L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(SoupPvP.getInstance(), () -> event.getEntity().spigot().respawn(), 2L);
    }

    @EventHandler
    public void onPlayerAutoRespawnEvent(PlayerRespawnEvent event){
        Player player = event.getPlayer();

        Location spawn = Bukkit.getWorlds().get(0).getSpawnLocation();
        spawn.add(0.5, 0, 0.5);
        player.teleport(spawn);

        PlayerUtil.resetPlayer(player);
    }

    /*@EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
            Player player = event.getPlayer();
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.getGameMode() != GameMode.CREATIVE) {
                if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.ENDER_CHEST || event.getClickedBlock().getType() == Material.CHEST)) {
                    event.setCancelled(true);
                }
            }
        }
    }*/

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().hasMetadata("build")){
            event.setCancelled(false);
            event.setUseItemInHand(Event.Result.DEFAULT);
        } else {
            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(event.getPlayer())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority= EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (!event.getPlayer().hasMetadata("build")){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (!event.getPlayer().hasMetadata("build")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExplosionPrimeEvent(ExplosionPrimeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SURVIVAL){
            ItemStack itemStack = event.getItemDrop().getItemStack();
            Material itemType = itemStack.getType();
            String itemTypeName = itemType.name().toLowerCase();
            if (itemTypeName.contains("sword") || itemTypeName.contains("axe") || itemType == Material.BOW) {
                player.sendMessage(CC.translate("&cYou can't drop your attacking weapon."));
                event.setCancelled(true);
            }
            if (itemTypeName.contains("helmet") || itemTypeName.contains("chestplate") || itemTypeName.contains("leggings") || itemTypeName.contains("boots")) {
                player.sendMessage(CC.translate("&cYou can't drop your armor."));
                event.setCancelled(true);
            }
            if (itemType == Material.BOWL) {
                event.getItemDrop().remove();
            } else {
                TasksUtility.runTaskLater(() -> {
                    event.getItemDrop().remove();
                }, 5 * 20);
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event){
        if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            Player player = event.getPlayer();
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            if (profile.getProfileState() == ProfileState.SPAWN){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPluginEnableEvent(PluginEnableEvent event) {
        World lobbyWorld = Bukkit.getWorld("world");
        lobbyWorld.setGameRuleValue("doDaylightCycle", "false");
        lobbyWorld.setTime(6000);
        lobbyWorld.setStorm(false);
        lobbyWorld.setWeatherDuration(0);
        lobbyWorld.setAnimalSpawnLimit(0);
        lobbyWorld.setAmbientSpawnLimit(0);
        lobbyWorld.setMonsterSpawnLimit(0);
        lobbyWorld.setWaterAnimalSpawnLimit(0);
        lobbyWorld.setDifficulty(Difficulty.HARD);
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            String[] lines = event.getLines();
            for (int i = 0; i < lines.length; i++) {
                event.setLine(i, CC.translate(lines[i]));
            }
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event){
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    public void onBlockSpreadEvent(BlockSpreadEvent event){
        if (event.getNewState().getType() == Material.FIRE) {
            event.setCancelled(true);
        }
    }

    /*@EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (!(SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getPlayer().getUniqueId()).getProfileState().equals(ProfileState.COMBAT))){
            event.setCancelled(true);
        }
    }*/

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getEntity().remove();
                }
            }.runTaskLater(SoupPvP.getInstance(), 15 * 10);
        }
    }

    @EventHandler
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        int id = event.getBlock().getTypeId();
        if(id == 8 || id == 9) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void enderPearlThrown(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(event.getTo())){
                player.sendMessage(CC.translate("&cYou may not pearl into spawn."));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event){
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player) && profile.getProfileState() == ProfileState.COMBAT){
            if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockY() == event.getFrom().getBlockY() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) return;
            player.teleport(findNearestLocationNearSpawnCuboid(player.getLocation().getBlock()).add(0.5, 0, 0.5));
        }
    }

    private static Location findNearestLocationNearSpawnCuboid(Block block){
        ArrayList<Location> placingLocation = new ArrayList<>();
        placingLocation.add(block.getLocation().add(0, 0, 0));
        placingLocation.add(block.getLocation().add(1, 0, 0));
        placingLocation.add(block.getLocation().add(1, 0, 1));
        placingLocation.add(block.getLocation().add(0, 0, 1));
        placingLocation.add(block.getLocation().add(-1, 0, 1));
        placingLocation.add(block.getLocation().add(-1, 0, 0));
        placingLocation.add(block.getLocation().add(-1, 0, -1));
        placingLocation.add(block.getLocation().add(0, 0, -1));
        placingLocation.add(block.getLocation().add(1, 0, -1));
        for (Location location : placingLocation){
            if (!SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(location)){
                return location;
            }
        }
        return null;
    }

}
