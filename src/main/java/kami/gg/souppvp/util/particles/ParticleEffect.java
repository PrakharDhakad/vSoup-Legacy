package kami.gg.souppvp.util.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public enum ParticleEffect {

    HUGE_EXPLOSION("HUGE_EXPLOSION", 0, "hugeexplosion", "EXPLOSION_HUGE"),
    LARGE_EXPLODE("LARGE_EXPLODE", 1, "largeexplode", "EXPLOSION_LARGE"),
    BUBBLE("BUBBLE", 2, "bubble", "WATER_BUBBLE"),
    SUSPEND("SUSPEND", 3, "suspend", "SUSPENDED"),
    DEPTH_SUSPEND("DEPTH_SUSPEND", 4, "depthSuspend", "SUSPENDED_DEPTH"),
    MAGIC_CRIT("MAGIC_CRIT", 5, "magicCrit", "CRIT_MAGIC"),
    MOB_SPELL("MOB_SPELL", 6, "mobSpell", "SPELL_MOB"),
    MOB_SPELL_AMBIENT("MOB_SPELL_AMBIENT", 7, "mobSpellAmbient", "SPELL_MOB_AMBIENT"),
    INSTANT_SPELL("INSTANT_SPELL", 8, "instantSpell", "SPELL_INSTANT"),
    WITCH_MAGIC("WITCH_MAGIC", 9, "witchMagic", "SPELL_WITCH"),
    EXPLODE("EXPLODE", 10, "explode", "EXPLOSION_NORMAL"),
    SPLASH("SPLASH", 11, "splash", "WATER_SPLASH"),
    LARGE_SMOKE("LARGE_SMOKE", 12, "largesmoke", "SMOKE_LARGE"),
    RED_DUST("RED_DUST", 13, "reddust", "REDSTONE"),
    SNOWBALL_POOF("SNOWBALL_POOF", 14, "snowballpoof", "SNOWBALL"),
    ANGRY_VILLAGER("ANGRY_VILLAGER", 15, "angryVillager", "VILLAGER_ANGRY"),
    HAPPY_VILLAGER("HAPPY_VILLAGER", 16, "happerVillager", "VILLAGER_HAPPY"),
    EXPLOSION_NORMAL("EXPLOSION_NORMAL", 17, ParticleEffect.EXPLODE.getName()),
    EXPLOSION_LARGE("EXPLOSION_LARGE", 18, ParticleEffect.LARGE_EXPLODE.getName()),
    EXPLOSION_HUGE("EXPLOSION_HUGE", 19, ParticleEffect.HUGE_EXPLOSION.getName()),
    FIREWORKS_SPARK("FIREWORKS_SPARK", 20, "fireworksSpark"),
    WATER_BUBBLE("WATER_BUBBLE", 21, ParticleEffect.BUBBLE.getName()),
    WATER_SPLASH("WATER_SPLASH", 22, ParticleEffect.SPLASH.getName()),
    WATER_WAKE("WATER_WAKE", 23),
    SUSPENDED("SUSPENDED", 24, ParticleEffect.SUSPEND.getName()),
    SUSPENDED_DEPTH("SUSPENDED_DEPTH", 25, ParticleEffect.DEPTH_SUSPEND.getName()),
    CRIT("CRIT", 26, "crit"),
    CRIT_MAGIC("CRIT_MAGIC", 27, ParticleEffect.MAGIC_CRIT.getName()),
    SMOKE_NORMAL("SMOKE_NORMAL", 28),
    SMOKE_LARGE("SMOKE_LARGE", 29, ParticleEffect.LARGE_SMOKE.getName()),
    SPELL("SPELL", 30, "spell"),
    SPELL_INSTANT("SPELL_INSTANT", 31, ParticleEffect.INSTANT_SPELL.getName()),
    SPELL_MOB("SPELL_MOB", 32, ParticleEffect.MOB_SPELL.getName()),
    SPELL_MOB_AMBIENT("SPELL_MOB_AMBIENT", 33, ParticleEffect.MOB_SPELL_AMBIENT.getName()),
    SPELL_WITCH("SPELL_WITCH", 34, ParticleEffect.WITCH_MAGIC.getName()),
    DRIP_WATER("DRIP_WATER", 35, "dripWater"),
    DRIP_LAVA("DRIP_LAVA", 36, "dripLava"),
    VILLAGER_ANGRY("VILLAGER_ANGRY", 37, ParticleEffect.ANGRY_VILLAGER.getName()),
    VILLAGER_HAPPY("VILLAGER_HAPPY", 38, ParticleEffect.HAPPY_VILLAGER.getName()),
    TOWN_AURA("TOWN_AURA", 39, "townaura"),
    NOTE("NOTE", 40, "note"),
    PORTAL("PORTAL", 41, "portal"),
    ENCHANTMENT_TABLE("ENCHANTMENT_TABLE", 42, "enchantmenttable"),
    FLAME("FLAME", 43, "flame"),
    LAVA("LAVA", 44, "lava"),
    FOOTSTEP("FOOTSTEP", 45, "footstep"),
    CLOUD("CLOUD", 46, "cloud"),
    REDSTONE("REDSTONE", 47, "reddust"),
    SNOWBALL("SNOWBALL", 48, "snowballpoof"),
    SNOW_SHOVEL("SNOW_SHOVEL", 49, "snowshovel"),
    SLIME("SLIME", 50, "slime"),
    HEART("HEART", 51, "heart"),
    BARRIER("BARRIER", 52),
    ITEM_CRACK("ITEM_CRACK", 53),
    BLOCK_CRACK("BLOCK_CRACK", 54),
    BLOCK_DUST("BLOCK_DUST", 55),
    WATER_DROP("WATER_DROP", 56),
    ITEM_TAKE("ITEM_TAKE", 57),
    MOB_APPEARANCE("MOB_APPEARANCE", 58);

    private final String particleName;
    private final String enumValue;
    private static Class<?> nmsPacketPlayOutParticle;
    private static Class<?> nmsEnumParticle;
    private static int particleRange;

    static {
        ParticleEffect.nmsPacketPlayOutParticle = ReflectionUtilities.getNMSClass("PacketPlayOutWorldParticles");
        ParticleEffect.particleRange = 25;
    }

    ParticleEffect(String s, int n, String particleName, String enumValue) {
        this.particleName = particleName;
        this.enumValue = enumValue;
    }

    ParticleEffect(String s, int n, String particleName) {
        this(s, n, particleName, null);
    }

    ParticleEffect(String s, int n) {
        this(s, n, null, null);
    }

    public String getName() {
        return this.particleName;
    }

    public static void setRange(int range) {
        if (range < 0) {
            throw new IllegalArgumentException("Range must be positive!");
        }
        if (range > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Range is too big!");
        }
        ParticleEffect.particleRange = range;
    }

    public static int getRange() {
        return ParticleEffect.particleRange;
    }

    public void sendToPlayer(Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
        if (!isPlayerInRange(player, location)) {
            return;
        }
        if (ReflectionUtilities.getVersion().contains("v1_8")) {
            try {
                if (ParticleEffect.nmsEnumParticle == null) {
                    ParticleEffect.nmsEnumParticle = ReflectionUtilities.getNMSClass("EnumParticle");
                }
                Object packet = ParticleEffect.nmsPacketPlayOutParticle.getConstructor(ParticleEffect.nmsEnumParticle, Boolean.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE, int[].class).newInstance(getEnum(ParticleEffect.nmsEnumParticle.getName() + "." + ((this.enumValue != null) ? this.enumValue : this.name().toUpperCase())), true, (float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count, new int[0]);
                Object handle = ReflectionUtilities.getHandle(player);
                Object connection = ReflectionUtilities.getField(handle.getClass(), "playerConnection").get(handle);
                ReflectionUtilities.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, packet);
                return;
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable to send Particle " + this.name() + ". (Version 1.8): " + e.getMessage());
            }
        }
        try {
            if (this.particleName == null) {
                throw new Exception();
            }
            Object packet = ParticleEffect.nmsPacketPlayOutParticle.getConstructor(String.class, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE).newInstance(this.particleName, (float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count);
            Object handle = ReflectionUtilities.getHandle(player);
            Object connection = ReflectionUtilities.getField(handle.getClass(), "playerConnection").get(handle);
            ReflectionUtilities.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, packet);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to send Particle " + this.name() + ". (Invalid Server Version: 1.7) " + e.getMessage());
        }
    }

    public void sendToPlayers(Collection<? extends Player> collection, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
        for (Player p : collection) {
            this.sendToPlayer(p, location, offsetX, offsetY, offsetZ, speed, count);
        }
    }

    public void sendToPlayers(Player[] players, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count) throws Exception {
        for (Player p : players) {
            this.sendToPlayer(p, location, offsetX, offsetY, offsetZ, speed, count);
        }
    }

    private static Enum<?> getEnum(String enumFullName) {
        String[] x = enumFullName.split("\\.(?=[^\\.]+$)");
        if (x.length == 2) {
            String enumClassName = x[0];
            String enumName = x[1];
            try {
                Class<Enum> cl = (Class<Enum>) Class.forName(enumClassName);
                return Enum.valueOf(cl, enumName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static boolean isPlayerInRange(Player p, Location center) {
        double distance = 0.0;
        return (distance = center.distance(p.getLocation())) <= Double.MAX_VALUE && distance < ParticleEffect.particleRange;
    }

    public static class ReflectionUtilities {
        public static void setValue(Object instance, String fieldName, Object value) throws Exception {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        }

        public static Object getValue(Object instance, String fieldName) throws Exception {
            Field field = instance.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        }

        public static String getVersion() {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            String version = name.substring(name.lastIndexOf(46) + 1) + ".";
            return version;
        }

        public static Class<?> getNMSClass(String className) {
            String fullName = "net.minecraft.server." + getVersion() + className;
            Class<?> clazz = null;
            try {
                clazz = Class.forName(fullName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return clazz;
        }

        public static Class<?> getOBCClass(String className) {
            String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
            Class<?> clazz = null;
            try {
                clazz = Class.forName(fullName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return clazz;
        }

        public static Object getHandle(Object obj) {
            try {
                return getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static Field getField(Class<?> clazz, String name) {
            try {
                Field field = clazz.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
            Method[] methods;
            for (int length = (methods = clazz.getMethods()).length, i = 0; i < length; ++i) {
                Method m = methods[i];
                if (m.getName().equals(name) && (args.length == 0 || ClassListEqual(args, m.getParameterTypes()))) {
                    m.setAccessible(true);
                    return m;
                }
            }
            return null;
        }

        public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
            boolean equal = true;
            if (l1.length != l2.length) {
                return false;
            }
            for (int i = 0; i < l1.length; ++i) {
                if (l1[i] != l2[i]) {
                    equal = false;
                    break;
                }
            }
            return equal;
        }
    }
}
