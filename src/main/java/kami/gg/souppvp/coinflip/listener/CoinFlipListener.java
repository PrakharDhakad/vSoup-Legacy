package kami.gg.souppvp.coinflip.listener;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.CoinFlip;
import kami.gg.souppvp.coinflip.CoinFlipState;
import kami.gg.souppvp.coinflip.menu.confirmation.ConfirmWagerMenu;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.MathUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class CoinFlipListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event){

        Player player = event.getPlayer();
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        String message = event.getMessage();

        if (profile.getCoinFlipState().equals(CoinFlipState.CREATING)){
            event.setCancelled(true);
            if (MathUtil.isNumeric(message)){
                Integer amount = Integer.parseInt(message);
                if (profile.getCredits() < amount){
                    player.sendMessage(CC.translate("&cInsufficient credits! Try entering a lower amount!"));
                } else if (amount <= 0){
                    player.sendMessage(CC.translate("&cThe wager amount has to be greater than zero!"));
                }  else {
                    new ConfirmWagerMenu(amount).openMenu(player);
                    profile.setCoinFlipState(CoinFlipState.NONE);
                    /*player.sendMessage(CC.translate("&aSuccessfully created a new coin flip wager!"));
                    WagerCreateEvent wagerCreateEvent = new WagerCreateEvent(player.getUniqueId(), amount);
                    Bukkit.getPluginManager().callEvent(wagerCreateEvent);
                    profile.setCredits(profile.getCredits() - amount);
                    player.sendMessage(CC.translate("&aYou have temporarily been deducted &a" + amount + " &acredits for the wager. To receive back your credits, you can cancel the wager."));*/
                }
            } else {
                profile.setCoinFlipState(CoinFlipState.NONE);
                player.sendMessage(CC.translate("&aSuccessfully cancelled the coin flip creation procedure."));
            }
        }

    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (SoupPvP.getInstance().getCoinFlipsHandler().hasCoinFlipWager(uuid)){
            CoinFlip coinflip = SoupPvP.getInstance().getCoinFlipsHandler().getPlayerCoinFlip(uuid);
            Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(uuid);
            profile.setCredits(profile.getCredits() + coinflip.getAmount());
            profile.saveProfile();
            SoupPvP.getInstance().getCoinFlipsHandler().removeCoinFlip(coinflip);
        }
    }

}
