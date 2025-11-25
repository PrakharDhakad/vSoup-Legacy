package kami.gg.souppvp.handlers;

import kami.gg.souppvp.SoupPvP;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class PacketBorderHandler extends Thread {

    private final int REGION_DISTANCE = 8;
    private final int REGION_DISTANCE_SQUARED = REGION_DISTANCE * REGION_DISTANCE;

    private static HashMap<UUID, Map<Location, Long>> sentBlockChanges = new HashMap<>();

    public PacketBorderHandler() {

    }

    public void run() {
        while (true) {
            for (Player player : SoupPvP.getInstance().getServer().getOnlinePlayers()) {
                try {
                    checkPlayer(player);
                    Thread.sleep(20L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkPlayer(Player player) {
        try {

            if (player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            boolean isInSpawnCuboid = SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player);

            if (isInSpawnCuboid) {
                clearPlayer(player);
                return;
            }

            if (!sentBlockChanges.containsKey(player.getUniqueId())) {
                sentBlockChanges.put(player.getUniqueId(), new HashMap<>());
            }

            Iterator<Map.Entry<Location, Long>> bordersIterator = sentBlockChanges.get(player.getUniqueId()).entrySet().iterator();

            // Remove borders after they 'expire' -- This is used to get rid of block changes the player has walked away from,
            // whose value in the map hasn't been updated recently.
            while (bordersIterator.hasNext()) {
                Map.Entry<Location, Long> border = bordersIterator.next();

                if (System.currentTimeMillis() >= border.getValue()) {
                    Location loc = border.getKey();
                    if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
                        continue;
                    }

                    Block block = loc.getBlock();
                    player.sendBlockChange(loc, block.getType(), block.getData());
                    bordersIterator.remove();
                }
            }
            sendClaimToPlayer(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendClaimToPlayer(Player player) {
        // This gets us all the coordinates on the outside of the claim.
        // Probably could be made better
        for (Block block : SoupPvP.getInstance().getSpawnHandler().getCuboid().getBlocks()){
            Location onPlayerY = new Location(player.getWorld(), block.getX(), player.getLocation().getY(), block.getZ());
            if (onPlayerY.distanceSquared(player.getLocation()) > REGION_DISTANCE_SQUARED) {
                continue;
            }

            for (int i = -4; i < 5; i++) {
                Location check = onPlayerY.clone().add(0, i, 0);

                if (check.getWorld().isChunkLoaded(check.getBlockX() >> 4, check.getBlockZ() >> 4) && check.getBlock().getType().isTransparent() && check.distanceSquared(onPlayerY) < REGION_DISTANCE_SQUARED && SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(check.getBlock()) && check.getBlock().getLocation().distance(player.getLocation()) <= 2) {
                    player.sendBlockChange(check, Material.STAINED_GLASS, (byte) 14); // Red stained glass
                    sentBlockChanges.get(player.getUniqueId()).put(check, System.currentTimeMillis() + 4000L); // The time the glass will stay for if the player walks away
                }
            }
        }
    }

    private static void clearPlayer(Player player) {
        if (sentBlockChanges.containsKey(player.getUniqueId())) {
            for (Location changedLoc : sentBlockChanges.get(player.getUniqueId()).keySet()) {
                if (!changedLoc.getWorld().isChunkLoaded(changedLoc.getBlockX() >> 4, changedLoc.getBlockZ() >> 4)) {
                    continue;
                }

                Block block = changedLoc.getBlock();
                player.sendBlockChange(changedLoc, block.getType(), block.getData());
            }

            sentBlockChanges.remove(player.getUniqueId());
        }
    }

}
