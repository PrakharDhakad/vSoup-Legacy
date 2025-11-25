package kami.gg.souppvp.coinflip.button;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.coinflip.CoinFlip;
import kami.gg.souppvp.coinflip.events.WagerCancelEvent;
import kami.gg.souppvp.coinflip.events.WagerStartEvent;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.ItemBuilder;
import kami.gg.souppvp.util.PlayerUtil;
import kami.gg.souppvp.util.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CoinFlipWagerButton extends Button {

    private static final DecimalFormat df = new DecimalFormat("0");

    private final CoinFlip coinFlip;

    public CoinFlipWagerButton(CoinFlip coinFlip){
        this.coinFlip = coinFlip;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        Profile creatorProfile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(coinFlip.getCreator());
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(CC.translate("&e&lWager:"));
        lore.add(CC.translate("&a" + coinFlip.getAmount() + " credits"));
        lore.add("");
        lore.add(CC.translate("&e&l" + Bukkit.getPlayer(coinFlip.getCreator()).getName() + "'s Stats"));
        lore.add(CC.translate("&c• &eTotal Games: &a" + creatorProfile.getTotalWagerGames()));
        lore.add(CC.translate("&c• &eWon: &a" + creatorProfile.getWagersWon()));
        lore.add(CC.translate("&c• &eLost: &a" + creatorProfile.getWagersLost()));
        if (creatorProfile.getWagersWon() == 0) {
            lore.add(CC.translate("&c• &eWin Percent: &aN/A"));
        } else {
            double percentage = (double) creatorProfile.getWagersWon() / (double) creatorProfile.getWagersLost() * 100;
            lore.add(CC.translate("&c• &eWin Percent: &a" + creatorProfile.getWinPercent() + "%"));
        }
        lore.add("");
        if (profile.equals(creatorProfile)){
            lore.add(CC.translate("&7Right-Click to &c&lCANCEL &7the bet!"));
        } else {
            if (profile.getCredits() < coinFlip.getAmount()){
                lore.add(CC.translate("&cInsufficient Credits!"));
            } else {
                lore.add(CC.translate("&7Click here to &a&lACCEPT &7the bet!"));
            }
        }
        return new ItemBuilder(Material.SKULL_ITEM).durability((short) 3).name(CC.translate("&a&l" + Bukkit.getPlayer(coinFlip.getCreator()).getName())).lore(lore).build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile creator = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(coinFlip.getCreator());
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        if (creator.equals(profile)) {
            if (clickType.isLeftClick()) {
                player.sendMessage(CC.translate("&cYou cannot accept your own coin flip game."));
            }
            if (clickType.isRightClick()) {
                    player.sendMessage(CC.translate("&7The wager has been returned. (&a" + coinFlip.getAmount() + " &acredits&7)"));
                    player.sendMessage(CC.translate("&cYou cancelled your coinflip game."));
                    WagerCancelEvent wagerCancelEvent = new WagerCancelEvent(coinFlip);
                    Bukkit.getPluginManager().callEvent(wagerCancelEvent);
                    PlayerUtil.playSound(player, Sound.CLICK);
            }
        }  else {
            if (clickType.isLeftClick()){
                if (profile.getCredits() < coinFlip.getAmount()){
                    PlayerUtil.playSound(player, Sound.DIG_GRASS);
                } else {
                    if (coinFlip.getOpponent() == null){
                        if (SoupPvP.getInstance().getCoinFlipsHandler().hasCoinFlipWager(profile.getUuid())){
                            CoinFlip playerCoinFlip= SoupPvP.getInstance().getCoinFlipsHandler().getPlayerCoinFlip(player.getUniqueId());
                            SoupPvP.getInstance().getCoinFlipsHandler().removeCoinFlip(playerCoinFlip);
                            SoupPvP.getInstance().getCoinFlipsHandler().getCoinFlips().remove(playerCoinFlip);
                        }
                        WagerStartEvent wagerStartEvent = new WagerStartEvent(coinFlip, player.getUniqueId());
                        Bukkit.getPluginManager().callEvent(wagerStartEvent);
                        PlayerUtil.playSound(player, Sound.CLICK);
                    } else {
                        PlayerUtil.playSound(player, Sound.DIG_GRASS);
                    }
                }
            }
        }
    }

}
