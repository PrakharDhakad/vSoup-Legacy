package kami.gg.souppvp.command.leave;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.TimeUtil;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaveCommand {

    @Command(name = "", desc = "teleport to spawn")
    public void execute(@Sender CommandSender sender){
        Player player = (Player) sender;
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());

        if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)){
            player.sendMessage(CC.translate("&cYou can't do this in spawn."));
        } else {
            if (profile.isCombatTagged()){
                player.sendMessage(CC.translate("&cYou're currently combat-tagged."));
            } else {
                if (profile.getSumoEvent() == null){
                    profile.addSpawnTeleportation();
                    new BukkitRunnable() {
                        int i = 6;
                        @Override
                        public void run() {
                            if (profile.isTeleportingToSpawn()){
                                i--;
                                if (i > 0){
                                    player.sendMessage(CC.translate("&3Spawn teleportation in &b" + TimeUtil.convertToHhMmSs((long) i) + "&3..."));
                                    PlayerUtil.playSound(player, Sound.CHICKEN_EGG_POP);
                                }
                                if ((SoupPvP.getInstance().getSpawnTeleportationHandler().getSpawnTeleporataion().get(player.getUniqueId())-System.currentTimeMillis()) / 1000 == 0) {
                                    profile.removeSpawnTeleportation();
                                    PlayerUtil.resetPlayer(player);
                                    cancel();
                                }
                            } else {
                                cancel();
                            }
                        }
                    }.runTaskTimerAsynchronously(SoupPvP.getInstance(), 0, 20L);
                } else {
                    player.sendMessage(CC.translate("&cYou're currently in a sumo event."));
                }
            }
        }
    }

}
