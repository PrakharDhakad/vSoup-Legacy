package kami.gg.souppvp.util;

import kami.gg.souppvp.SoupPvP;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class XPBarTimer {

    private static final Map<Player, BukkitTask> runnables = new HashMap<>();

    public static void runXpBar(Player player, int cooldown) {
        if (player.getLevel() < cooldown) {
            if (runnables.get(player) != null)
                runnables.get(player).cancel();

            player.setLevel(cooldown);

            runnables.put(player, new BukkitRunnable() {

                final long time = System.currentTimeMillis() + cooldown * 1000L;

                @Override
                public void run() {

                    if (!runnables.containsKey(player)) {
                        cancel();
                        player.setLevel(0);
                        player.setExp(0);
                        return;
                    }

                    if(System.currentTimeMillis() > this.time) {
                        cancel();

                        player.setLevel(0);
                        player.setExp(0);
                        return;
                    }

                    long remaining = this.time - System.currentTimeMillis();

                    player.setLevel(Math.round(remaining) / 1000);
                    player.setExp(remaining / (cooldown * 1000F));
                }

            }.runTaskTimerAsynchronously(SoupPvP.getInstance(), 1L, 1L));
        }
    }

    public static void remove(Player player) {
        if (runnables.isEmpty()) return;
        if (runnables.containsKey(player)){
            for (Player player1 : runnables.keySet()){
                if (player1.equals(player)){
                    runnables.remove(player);
                }
            }
            player.setLevel(-1);
            player.setExp(0);
        }
    }
}
