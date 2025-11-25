package kami.gg.souppvp.listener;

import com.lunarclient.bukkitapi.LunarClientAPI;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.TaskUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LunarClientListener implements Listener {

    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        updateNametag(player);
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            if (event.getCause() == EntityDamageEvent.DamageCause.LAVA || event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK){
                Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(event.getEntity().getUniqueId());
                if (profile.getActivePerks().get(2).equalsIgnoreCase("Cana")) {
                    return;
                }
            }
            TaskUtil.runLater(() -> {
                updateNametag((Player) event.getEntity());
            }, 1L);
        }
    }

    @EventHandler
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent event){
        if (event.getEntity() instanceof Player){
            TaskUtil.runLater(() -> {
                LunarClientListener.updateNametag((Player) event.getEntity());
            }, 1L);
        }
    }

    public static List<String> fetchNameTag(Player target) {
        List<String> tag = new ArrayList<>();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(target.getUniqueId());
        tag.add(CC.translate("&a" + target.getName()));
        if (profile.isJuggernaut()){
            tag.add(CC.translate("&4&lJuggernaut"));
        }
        Perk currentPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(0));
        Perk tricksterPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Trickster");
        if (currentPerk == tricksterPerk){
            tag.add(CC.translate("&f" + (new Random().nextInt(11)) + " &4" + StringEscapeUtils.unescapeJava("\u2764")));
        } else {
            tag.add(CC.translate("&f" + ((int) target.getHealth()/2) + " &4" + StringEscapeUtils.unescapeJava("\u2764")));
        }
        if (profile.getBounty() > 0){
            if (currentPerk == tricksterPerk){
                tag.add(CC.translate("&aBounty: &e" + new Random().nextInt(1001)));
            } else {
                tag.add(CC.translate("&aBounty: &e" + profile.getBounty()));
            }
        }
        return tag;
    }

    public static void updateNametag(Player player) {
        SoupPvP.getInstance().getServer().getOnlinePlayers().forEach(it -> {
            final List<String> loopPlayerNameTag = fetchNameTag(it);
            final List<String> targetNameTag = fetchNameTag(player);

            LunarClientAPI.getInstance().overrideNametag(it, loopPlayerNameTag, player);
            LunarClientAPI.getInstance().overrideNametag(player, targetNameTag, it);
        });
    }

}
