package kami.gg.souppvp.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SpawnListeners implements Listener {

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player) || profile.getProfileState() == ProfileState.SPAWN) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMoveItem(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getWhoClicked() instanceof Player) {
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getWhoClicked().getUniqueId());
            if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(event.getWhoClicked()) || profile.getProfileState() == ProfileState.SPAWN){
                event.setCancelled(true);
            }
        }
    }

}

