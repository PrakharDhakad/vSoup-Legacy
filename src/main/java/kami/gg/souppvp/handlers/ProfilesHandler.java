package kami.gg.souppvp.handlers;

import com.mongodb.client.MongoCollection;
import kami.gg.souppvp.SoupPvP;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileListeners;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class ProfilesHandler {

    private MongoCollection<Document> mongoCollection = SoupPvP.getInstance().getMongoDatabase().getCollection("Profiles");
    private List<Profile> profiles;

    public ProfilesHandler(){
        profiles = new ArrayList<>();
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ProfileListeners(), SoupPvP.getInstance());
    }

    public Profile getProfileByName(String playerName){
        Player player = Bukkit.getPlayer(playerName);

        if (player != null) {
            for (Profile profile : profiles){
                if (profile.getUsername().equalsIgnoreCase(playerName)){
                    return profile;
                }
            }
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

        if (offlinePlayer.hasPlayedBefore()) {
            for (Profile profile : profiles){
                if (profile.getUuid().equals(offlinePlayer.getUniqueId())){
                    return profile;
                }
            }
            return new Profile(offlinePlayer.getUniqueId());
        }

        UUID uuid = Bukkit.getPlayer(playerName).getUniqueId();

        if (uuid != null) {
            for (Profile profile : profiles){
                if (profile.getUsername().equalsIgnoreCase(playerName)){
                    return profile;
                }
            }
            return new Profile(playerName);
        }

        return null;

    }

    public Profile getProfileByUUID(UUID uuid){
        if (Bukkit.getPlayer(uuid) != null){
            for (Profile profile : profiles){
                if (profile.getUuid().equals(uuid)){
                    return profile;
                }
            }
        }
        return null;
    }

    public void saveProfiles(){
        for (Profile profile : SoupPvP.getInstance().getProfilesHandler().getProfiles()) {
            profile.saveProfile();
        }
    }

}
