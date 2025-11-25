package kami.gg.souppvp.scoreboard;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.events.impl.sumo.SumoState;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileState;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.TimeUtil;
import kami.gg.souppvp.util.assemble.AssembleAdapter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardAdapter implements AssembleAdapter {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&b&lSoupPvP");
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> toReturn = new ArrayList<>();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (profile.getEnableScoreboard()){
            toReturn.add("&a&7&m--------------------");
            if (profile.getLoaded()){
                toReturn.add("&fKills: &b" + profile.getKills());
                toReturn.add("&fKillstreak: &b" + profile.getCurrentKillstreak());
                toReturn.add("&fDeaths: &b" + profile.getDeaths());
                toReturn.add("&fCredits: &b" + profile.getCredits());
                if (profile.getBounty() > 0){
                    toReturn.add("&fBounty: &b" + profile.getBounty());
                }
                if (profile.getProfileState() == ProfileState.IN_EVENT || profile.getProfileState() == ProfileState.SPECTATING_EVENT){
                    toReturn.clear();
                    toReturn.add("&a&7&m--------------------");
                    toReturn.add("&fEvent: &bSumo");
                    if (profile.getSumoEvent().getState().equals(SumoState.WAITING)){
                        toReturn.add("&fPlayers: &b" + SoupPvP.getInstance().getSumoHandler().getActiveSumo().getEventPlayers().size() + "/" + SoupPvP.getInstance().getSumoHandler().getActiveSumo().getMaxPlayers());
                        toReturn.add("&fState: &bWaiting...");
                    } else {
                        toReturn.add("&fRemaining: &b" + profile.getSumoEvent().getRemainingPlayers().size() + "/" + profile.getSumoEvent().getTotalPlayers());
                        toReturn.add("&fDuration: &b" + profile.getSumoEvent().getRoundDuration());
                        toReturn.add("");
                        toReturn.add("&b" + profile.getSumoEvent().getRoundPlayerA().getUsername() + " &f(" + ((CraftPlayer) Bukkit.getPlayer(profile.getSumoEvent().getRoundPlayerA().getUsername())).getHandle().ping + "ms)");
                        toReturn.add("&fvs");
                        toReturn.add("&b" + profile.getSumoEvent().getRoundPlayerB().getUsername() + " &f(" + ((CraftPlayer) Bukkit.getPlayer(profile.getSumoEvent().getRoundPlayerB().getUsername())).getHandle().ping + "ms)");
                    }
                    toReturn.add("");
                    toReturn.add("&bkami.gg");
                    toReturn.add("&a&7&m--------------------");
                }
                if (SoupPvP.getInstance().getSpawnHandler().getCuboid().contains(player)){
                    if (profile.getProfileState() == ProfileState.SPAWN){
                        toReturn.add("&fTier Level: &b" + profile.getTier().getDisplay() + "✫");
                        toReturn.add("&fCurrent Kit: &r" + profile.getCurrentKit().getRarityType().getColor() + profile.getCurrentKit().getName());
                    }
                }
                if (SoupPvP.getInstance().getSumoHandler().getActiveSumo() != null && SoupPvP.getInstance().getSumoHandler().getActiveSumo().getState().equals(SumoState.WAITING)){
                    if (profile.getSumoEvent() == null && profile.getProfileState().equals(ProfileState.SPAWN)){
                        toReturn.add("");
                        toReturn.add("&fEvent: &bSumo");
                        toReturn.add("&fPlayers: &b" + SoupPvP.getInstance().getSumoHandler().getActiveSumo().getEventPlayers().size() + "/" + SoupPvP.getInstance().getSumoHandler().getActiveSumo().getMaxPlayers());
                        toReturn.add("&fState: &bWaiting...");
                    }
                }
                if (profile.getSumoEvent() == null){
                    if (profile.isTeleportingToSpawn()){
                        toReturn.add("&aSpawn TP: &f" + TimeUtil.convertToHhMmSs((SoupPvP.getInstance().getSpawnTeleportationHandler().getSpawnTeleporataion().get(player.getUniqueId())-System.currentTimeMillis()) / 1000));
                    }
                    if (SoupPvP.getInstance().getCombatTagsHandler().getCombatTags().containsKey(player.getUniqueId()) && (SoupPvP.getInstance().getCombatTagsHandler().getCombatTags().get(player.getUniqueId()) - System.currentTimeMillis() > 0)){
                        toReturn.add("&cCombat Tag: &f" + TimeUtil.convertToHhMmSs((SoupPvP.getInstance().getCombatTagsHandler().getCombatTags().get(player.getUniqueId()) - System.currentTimeMillis())/1000));
                    }
                }
            } else {
                toReturn.add("&c&lLoading Your Profile:");
                toReturn.add("&cWe are currently loading");
                toReturn.add("&cyour profile. This should");
                toReturn.add("&clast for a few seconds only.");
            }
            toReturn.add("");
            toReturn.add("&bkami.gg");
            toReturn.add("&b&7&m--------------------");
        } else {
            toReturn.add("&b&7&m--------------------");
            toReturn.add("  &3kami.gg");
            toReturn.add("&b&7&m--------------------");
        }
        return toReturn;
    }
}
