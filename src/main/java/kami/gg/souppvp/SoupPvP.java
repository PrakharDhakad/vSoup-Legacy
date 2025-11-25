package kami.gg.souppvp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import kami.gg.souppvp.coinflip.listener.CoinFlipListener;
import kami.gg.souppvp.coinflip.listener.WagerCustomEventListeners;
import kami.gg.souppvp.command.*;
import kami.gg.souppvp.command.admin.BuildCommand;
import kami.gg.souppvp.command.admin.CuboidSetCommand;
import kami.gg.souppvp.command.admin.statistics.*;
import kami.gg.souppvp.command.bounty.BountyCommand;
import kami.gg.souppvp.command.bounty.BountyListCommand;
import kami.gg.souppvp.command.credit.CreditsAddCommand;
import kami.gg.souppvp.command.credit.CreditsPayCommand;
import kami.gg.souppvp.command.credit.CreditsSetCommand;
import kami.gg.souppvp.command.leave.LeaveCommand;
import kami.gg.souppvp.command.leave.OPLeaveCommand;
import kami.gg.souppvp.command.shop.RepairCommand;
import kami.gg.souppvp.events.impl.sumo.SumoHandler;
import kami.gg.souppvp.events.impl.sumo.SumoListener;
import kami.gg.souppvp.events.impl.sumo.command.*;
import kami.gg.souppvp.handlers.*;
import kami.gg.souppvp.juggernaut.JuggernautListener;
import kami.gg.souppvp.killstreak.KillstreaksHandler;
import kami.gg.souppvp.kit.KitsHandler;
import kami.gg.souppvp.listener.*;
import kami.gg.souppvp.perk.PerksHandler;
import kami.gg.souppvp.profile.Profile;
import kami.gg.souppvp.profile.ProfileTypeAdapter;
import kami.gg.souppvp.scoreboard.ScoreboardAdapter;
import kami.gg.souppvp.tasks.CanaPerkAndFiremanKitTask;
import kami.gg.souppvp.tasks.ClearDropsTask;
import kami.gg.souppvp.tasks.ClearTimerCacheTask;
import kami.gg.souppvp.tasks.SaveProfilesTask;
import kami.gg.souppvp.teleportation.SpawnTeleporatationListener;
import kami.gg.souppvp.tier.TiersListener;
import kami.gg.souppvp.timer.TimersHandler;
import kami.gg.souppvp.timer.TimersListener;
import kami.gg.souppvp.util.assemble.Assemble;
import kami.gg.souppvp.util.menu.MenuListener;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class SoupPvP extends JavaPlugin {

    @Getter public static final Gson GSON = new Gson();
    @Getter public static final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {}.getType();
    @Getter @Setter public static Boolean isFreeKitsMode;
    @Getter public static SoupPvP instance;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private KitsHandler kitsHandler;
    private ProfilesHandler profilesHandler;
    private CombatTagsHandler combatTagsHandler;
    private SpawnTeleportationHandler spawnTeleportationHandler;
    private CoinFlipsHandler coinFlipsHandler;
    private SpawnHandler spawnHandler;
    private ClearDropsTask clearDropsTask;
    private SaveProfilesTask saveProfilesTask;
    private ClearTimerCacheTask clearTimerCacheTask;
    private CanaPerkAndFiremanKitTask canaPerkAndFiremanKitTask;
    private SumoHandler sumoHandler;
    private NoFallDamageHandler noFallDamageHandler;
    private PerksHandler perksHandler;
    private KillstreaksHandler killstreaksHandler;
    private TimersHandler timersHandler;
    private CommandService drink;

    @Override
    public void onEnable(){
        instance = this;
        SoupPvP.getInstance().saveDefaultConfig();
        isFreeKitsMode = SoupPvP.getInstance().getConfig().getBoolean("FREE-KITS");
        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(6000L);
        }
        setupDatabase();
        kitsHandler = new KitsHandler();
        profilesHandler = new ProfilesHandler();
        combatTagsHandler = new CombatTagsHandler();
        spawnTeleportationHandler = new SpawnTeleportationHandler();
        coinFlipsHandler = new CoinFlipsHandler();
        spawnHandler = new SpawnHandler();
        sumoHandler = new SumoHandler();
        noFallDamageHandler = new NoFallDamageHandler();
        perksHandler = new PerksHandler();
        killstreaksHandler = new KillstreaksHandler();
        timersHandler = new TimersHandler();
        clearDropsTask = new ClearDropsTask();
        saveProfilesTask = new SaveProfilesTask();
        clearTimerCacheTask = new ClearTimerCacheTask();
        canaPerkAndFiremanKitTask = new CanaPerkAndFiremanKitTask();
        (new PacketBorderHandler()).start();
        drink = Drink.get(this);
        registerScoreboard();
        registerCommands();
        registerListeners();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof Player || entity instanceof Minecart || entity instanceof Wither || entity instanceof ItemFrame || entity instanceof EnderDragon)) {
                    entity.remove();
                }
            }
        }
    }

    @Override
    public void onDisable(){
        SoupPvP.getInstance().getProfilesHandler().saveProfiles();
    }

    private void setupDatabase() {
        if (getConfig().getBoolean("MONGO.URI.ENABLED")){
            MongoClientURI mongoClientURI = new MongoClientURI(getConfig().getString("MONGO.URI.CONNECTION"));
            mongoClient = new MongoClient(mongoClientURI);
            mongoDatabase = mongoClient.getDatabase(getConfig().getString("MONGO.URI.DATABASE"));
        } else {
            if (getConfig().getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
                mongoClient = new MongoClient(new ServerAddress(getConfig().getString("MONGO.HOST"),
                        getConfig().getInt("MONGO.PORT")),
                        Collections.singletonList(MongoCredential.createCredential(
                                getConfig().getString("MONGO.AUTHENTICATION.USERNAME"),
                                getConfig().getString("MONGO.AUTHENTICATION.AUTHENTICATION-DATABASE"),
                                getConfig().getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray())
                        ));
                mongoDatabase = mongoClient.getDatabase(getConfig().getString("MONGO.AUTHENTICATION.AUTHENTICATION-DATABASE"));
            } else {
                mongoClient = new MongoClient(new ServerAddress(getConfig().getString("MONGO.HOST"), getConfig().getInt("MONGO.PORT")));
                mongoDatabase = mongoClient.getDatabase(getConfig().getString("MONGO.DATABASE"));
            }
        }
    }

    private void registerScoreboard(){
        Assemble assemble = new Assemble(this, new ScoreboardAdapter());
        assemble.setTicks(2);
    }

    public void registerCommands(){
        drink.bind(Profile.class).toProvider(new ProfileTypeAdapter());
        drink.register(new SetStatisticsBountyCommand(), "setstatistics", "")
                .registerSub(new SetStatisticsKillstreakCommand())
                .registerSub(new SetStatisticsDeathsCommand())
                .registerSub(new SetStatisticsKillsCommand())
                .registerSub(new SetStatisticsKillstreakCommand());
        drink.register(new CuboidSetCommand(), "cuboid", "");
        drink.register(new BountyCommand(), "bounty", "")
                .registerSub(new BountyListCommand());
        drink.register(new CreditsAddCommand(), "credits", "credit")
                .registerSub(new CreditsSetCommand());
        drink.register(new CreditsPayCommand(), "pay", "");
        drink.register(new LeaveCommand(), "leave", "spawn");
        drink.register(new OPLeaveCommand(), "opleave", "opspawn");
        drink.register(new RepairCommand(), "repair", "fix");
        drink.register(new CoinflipCommand(), "coinflip", "wager", "cf");
        drink.register(new FreeKitsCommand(), "free", "");
        drink.register(new HostCommand(), "host", "");
        drink.register(new JuggernautCommand(), "juggernaut", "");
        drink.register(new KillstreakCommand(), "killstreak", "killstreak", "ks");
        drink.register(new OptionsCommand(), "option", "options", "setting", "settings");
        drink.register(new PerksCommand(), "perks", "perk");
        drink.register(new ShopCommand(), "shop", "");
        drink.register(new StatisticsCommand(), "statistics", "statistic", "stats", "stat");
        drink.register(new TiersCommand(), "tiers", "tier");
        drink.register(new SumoCancelCommand(), "sumo", "")
                .registerSub(new SumoHostCommand())
                .registerSub(new SumoJoinCommand())
                .registerSub(new SumoLeaveCommand())
                .registerSub(new SumoSetSpawnCommand())
                .registerSub(new SumoTpCommand());
        drink.register(new BuildCommand(), "build");
        drink.registerCommands();
    }

    private void registerListeners(){
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new MenuListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ChatListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new GeneralListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SoupListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SpawnEventItemsListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new PvPListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SpawnListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SpawnTeleporatationListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new ShopItemsListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new TiersListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new KillStreakAnnouncerListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new BountyListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new LunarClientListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new NoFallDamageListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new CoinFlipListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new WagerCustomEventListeners(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new SumoListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new JuggernautListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new StrengthAndInstantHarmNerfListener(), this);
        SoupPvP.getInstance().getServer().getPluginManager().registerEvents(new TimersListener(), this);
    }

}
