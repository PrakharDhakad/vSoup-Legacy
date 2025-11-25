package kami.gg.souppvp.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PvPListeners implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            if (profile.isTeleportingToSpawn()) {
                profile.removeSpawnTeleportation();
                player.sendMessage(CC.translate("&cYour spawn teleportation has been cancelled as you have been combat-tagged."));
            }
            if (profile.getProfileState() == ProfileState.SPAWN) return;
            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)) return;
            profile.addCombatTag();
        }
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player){
            Player damagedPlayer = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();
            Profile damagedProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(damagedPlayer.getUniqueId());
            Profile damagerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(damager.getUniqueId());
            if (damagerProfile.getProfileState() == ProfileState.SPAWN || damagedProfile.getProfileState() == ProfileState.SPAWN) return;
            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(damager) || SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(damagedPlayer)) return;
            damagerProfile.addCombatTag();
            damagedProfile.addCombatTag();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (event.getEntity().getKiller() != null && event.getEntity().getKiller() != event.getEntity()) {
            Profile killerProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getKiller().getUniqueId());
            if (killerProfile.isJuggernaut()) return;
            killerProfile.setKills(killerProfile.getKills() + 1);
            killerProfile.setCurrentKillstreak(killerProfile.getCurrentKillstreak() + 1);
            if (killerProfile.getCurrentKillstreak() > killerProfile.getHighestKillstreak()){
                event.getEntity().getKiller().sendMessage(CC.translate("&aHighest Killstreak! &fYou are on a new highest killstreak of &a" + killerProfile.getCurrentKillstreak() + "&f."));
                killerProfile.setHighestKillstreak(killerProfile.getCurrentKillstreak());
            }
            killerProfile.setCredits(killerProfile.getCredits() + 17);
            killerProfile.setExperiences(killerProfile.getExperiences() + 3);
            if (killerProfile.getEnableKillDeathMessages()){
                if (killerProfile.getCurrentKit().getName().equals("Pro")){
                    event.getEntity().getKiller().getPlayer().sendMessage(CC.translate("&9You have killed &a" + event.getEntity().getName() + " &9for &a34 &9credits and &a3 &9xp."));
                } else {
                    event.getEntity().getKiller().getPlayer().sendMessage(CC.translate("&9You have killed &a" + event.getEntity().getName() + " &9for &a17 &9credits and &a3 &9xp."));
                }
            }
            if (profile.getEnableKillDeathMessages()){
                event.getEntity().sendMessage(CC.translate("&cYou have been killed by &a" + event.getEntity().getKiller().getName() + "&c."));
            }
            if (profile.getCurrentKillstreak() >= 10){
                for (Profile profile1 : SoupPvP.getInstance().getProfilesHandler().getProfiles()){
                    if (profile1.getEnableKillDeathMessages()){
                        Bukkit.getPlayer(profile1.getUuid()).sendMessage(CC.translate("&e" + profile.getUsername() + " &adied with a &e" + profile.getCurrentKillstreak() + " &akillstreak!"));
                    }
                }
            } else {
                for (Profile profile1 : SoupPvP.getInstance().getProfilesHandler().getProfiles()){
                    if (profile1.getEnableKillDeathMessages()){
                        Bukkit.getPlayer(profile1.getUuid()).sendMessage(CC.translate("&e" + killerProfile.getUsername() + " &ahas killed &e" + event.getEntity().getName() + "&a!"));
                    }
                }
            }
        } else {
            for (Profile profile1 : SoupPvP.getInstance().getProfilesHandler().getProfiles()){
                if (profile1.getEnableKillDeathMessages()){
                    Bukkit.getPlayer(profile1.getUuid()).sendMessage(CC.translate("&e" + event.getEntity().getName() + " &adied."));
                    if (profile1.equals(profile)){
                        Bukkit.getPlayer(profile1.getUuid()).sendMessage(CC.translate("&cYou died."));
                    }
                }
            }
        }
        profile.setCurrentKillstreak(0);
        profile.setDeaths(profile.getDeaths() + 1);
        LunarClientListener.updateNametag(event.getEntity());
    }

    @EventHandler
    public void onSoupRefillSign(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock().getType().equals(Material.SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST) || event.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
                Sign refillSign = (Sign) event.getClickedBlock().getState();
                if (refillSign.getLine(0).contains("Free") && refillSign.getLine(1).contains("Soup")) {
                    if (profile.isJuggernaut()){
                        player.sendMessage(CC.translate("&cYou may not refill soups whilst in Juggernaut."));
                        return;
                    }
                    Inventory inventory = Bukkit.createInventory(null, 54, "Refill station");
                    for (int i = 0; i < inventory.getSize(); i++) {
                        if (inventory.getItem(i) == null) {
                            inventory.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                        }
                    }
                    player.openInventory(inventory);
                }
            }
        }
    }

}
