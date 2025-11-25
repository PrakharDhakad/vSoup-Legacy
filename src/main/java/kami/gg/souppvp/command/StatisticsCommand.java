package kami.gg.souppvp.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.perk.Perk;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

public class StatisticsCommand {

    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Command(name = "", desc = "view a player's statistics", usage = "<profile>")
    public void execute(@Sender CommandSender sender, Profile profile){
        if (profile == null){
            sender.sendMessage(CC.translate("&cCouldn't resolve that player's name."));
        } else {
            sender.sendMessage(CC.translate(StringUtils.repeat("&7&m-", 53)));
            if (sender.getName().equals(profile.getUsername())){
                sender.sendMessage(CC.translate("&bYour Statistics:"));
            } else {
                sender.sendMessage(CC.translate("&b" + profile.getUsername() + "'s Statistics:"));
            }
            sender.sendMessage(CC.translate(" &fKills: &b" + profile.getKills()));
            sender.sendMessage(CC.translate(" &fDeaths: &b" + profile.getDeaths()));
            if (profile.getDeaths() == 0){
                sender.sendMessage(CC.translate(" &fKDR: &6Infinity"));
            } else {
                double kdr = (double) profile.getKills() / (double) profile.getDeaths();
                String context = kdr >= 1 ? "&a" : "&c";
                sender.sendMessage(CC.translate(" &fKDR: " + context + df.format(kdr)));
            }
            Perk incognitoPerk = SoupPvP.getInstance().getPerksHandler().getPerkByName("Incognito");
            if (SoupPvP.getInstance().getPerksHandler().getPerkByName(profile.getActivePerks().get(2)) != incognitoPerk){
                sender.sendMessage(CC.translate(" &fCurrent Killstreak: &b" + profile.getCurrentKillstreak()));
            }
            sender.sendMessage(CC.translate(" &fHighest Killstreak: &b" + profile.getHighestKillstreak()));
            sender.sendMessage(CC.translate(" &fCredits: &b" + profile.getCredits()));
            sender.sendMessage(CC.translate(" &fTier: &7" + profile.getTier().getDisplay() + "✫"));
            if (profile.getBounty() > 0){
                sender.sendMessage(CC.translate(" &fBounty: &b" + profile.getBounty()));
            }
            sender.sendMessage(CC.translate(StringUtils.repeat("&7&m-", 53)));
        }
    }

}
