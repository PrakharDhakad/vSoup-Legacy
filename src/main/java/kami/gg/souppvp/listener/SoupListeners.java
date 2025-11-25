package kami.gg.souppvp.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.killstreak.KillstreakReward;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.TaskUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SoupListeners implements Listener {

    @EventHandler
    public void onSoupConsumption(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getItemInHand().isSimilar(KillstreakReward.GRANDMA_SOUP) && player.getHealth() < 19.5 && ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)))) {
            player.setHealth(player.getMaxHealth());
            player.getItemInHand().setType(Material.BOWL);
            player.updateInventory();
            TaskUtil.runLater(() -> {
                LunarClientListener.updateNametag(player);
            }, 1L);
        }
        if (player.getItemInHand().getType() == Material.MUSHROOM_SOUP && player.getHealth() < 19.5 && ((event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)))) {
            player.setHealth(Math.min(player.getHealth() + 7.0, 20.0));
            player.getItemInHand().setType(Material.BOWL);
            player.updateInventory();
            TaskUtil.runLater(() -> {
                LunarClientListener.updateNametag(player);
            }, 1L);
        }
    }

    @EventHandler
    public void onBowlDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
            Material drop = event.getItemDrop().getItemStack().getType();
            Player player = event.getPlayer();
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
            Kit kit = SoupPvP.getInstance().getKitsHandler().getKitByName(profile.getCurrentKit().getName());
            if (drop.name().contains("SWORD") || drop.name().contains("AXE") || event.getItemDrop().getItemStack().hasItemMeta()) {
                event.setCancelled(true);
                player.updateInventory();
            }
            if (drop == Material.BOWL) {
                event.getItemDrop().remove();
            }
        }
    }

}
