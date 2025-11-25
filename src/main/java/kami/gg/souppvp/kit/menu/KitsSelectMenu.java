package kami.gg.souppvp.kit.menu;

import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.kit.Kit;
import kami.gg.souppvp.kit.button.KitButton;
import kami.gg.souppvp.kit.button.RandomKitButton;
import kami.gg.souppvp.kit.button.SelectPreviousKitButton;
import kami.gg.souppvp.kit.button.YourStatisticsButton;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.util.CC;
import kami.gg.souppvp.util.menu.Button;
import kami.gg.souppvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KitsSelectMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("Select a kit to equip");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Profile profile = SoupPvP.getInstance().getProfilesHandler().getProfileByUUID(player.getUniqueId());
        Map<Integer, Button> buttonMap = new HashMap<>();
        buttonMap.put(2, new RandomKitButton());
        buttonMap.put(4, new YourStatisticsButton(profile));
        buttonMap.put(6, new SelectPreviousKitButton(profile));
        for (int i=0; i<18; i++){
            buttonMap.putIfAbsent(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, " "));
        }
        for (Kit kit : SoupPvP.getInstance().getKitsHandler().getKits()){
            buttonMap.put(buttonMap.size(), new KitButton(kit));
        }
        return buttonMap;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }
}
