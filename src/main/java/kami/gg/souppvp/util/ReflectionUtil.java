package kami.gg.souppvp.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class ReflectionUtil {
    private static final Map<Class<?>, Class<?>> CORRESPONDING_TYPES = new HashMap();

    static {
        CORRESPONDING_TYPES.put(Byte.class, Byte.TYPE);
        CORRESPONDING_TYPES.put(Short.class, Short.TYPE);
        CORRESPONDING_TYPES.put(Integer.class, Integer.TYPE);
        CORRESPONDING_TYPES.put(Long.class, Long.TYPE);
        CORRESPONDING_TYPES.put(Character.class, Character.TYPE);
        CORRESPONDING_TYPES.put(Float.class, Float.TYPE);
        CORRESPONDING_TYPES.put(Double.class, Double.TYPE);
        CORRESPONDING_TYPES.put(Boolean.class, Boolean.TYPE);
    }

    private static Class<?> getPrimitiveType(Class<?> clazz) {
        return CORRESPONDING_TYPES.containsKey(clazz) ? CORRESPONDING_TYPES.get(clazz) : clazz;
    }

    private static Class<?>[] toPrimitiveTypeArray(Object[] objects) {
        int a = objects != null ? objects.length : 0;
        Class[] types = new Class[a];
        for (int i = 0; i < a; ++i) {
            types[i] = ReflectionUtil.getPrimitiveType(objects[i].getClass());
        }
        return types;
    }

    private static Class<?>[] toPrimitiveTypeArray(Class<?>[] classes) {
        int a = classes != null ? classes.length : 0;
        Class[] types = new Class[a];
        for (int i = 0; i < a; ++i) {
            types[i] = ReflectionUtil.getPrimitiveType(classes[i]);
        }
        return types;
    }

    private static boolean equalsTypeArray(Class<?>[] a, Class<?>[] o) {
        if (a.length != o.length) {
            return false;
        }
        for (int i = 0; i < a.length; ++i) {
            if (a[i].equals(o[i]) || a[i].isAssignableFrom(o[i])) continue;
            return false;
        }
        return true;
    }

    public static Class<?> getClass(String name, DynamicPackage pack, String subPackage) throws Exception {
        return Class.forName((Object)((Object)pack) + (subPackage != null && subPackage.length() > 0 ? "." + subPackage : "") + "." + name);
    }

    public static Class<?> getClass(String name, DynamicPackage pack) throws Exception {
        return ReflectionUtil.getClass(name, pack, null);
    }

    public static Constructor<?> getConstructor(Class<?> clazz, Class<?> ... paramTypes) {
        Class<?>[] t = ReflectionUtil.toPrimitiveTypeArray(paramTypes);
        for (Constructor<?> c : clazz.getConstructors()) {
            Class<?>[] types = ReflectionUtil.toPrimitiveTypeArray(c.getParameterTypes());
            if (!ReflectionUtil.equalsTypeArray(types, t)) continue;
            return c;
        }
        return null;
    }

    public static Object newInstance(Class<?> clazz, Object ... args) throws Exception {
        return ReflectionUtil.getConstructor(clazz, ReflectionUtil.toPrimitiveTypeArray(args)).newInstance(args);
    }

    public static Object newInstance(String name, DynamicPackage pack, String subPackage, Object ... args) throws Exception {
        return ReflectionUtil.newInstance(ReflectionUtil.getClass(name, pack, subPackage), args);
    }

    public static Object newInstance(String name, DynamicPackage pack, Object ... args) throws Exception {
        return ReflectionUtil.newInstance(ReflectionUtil.getClass(name, pack, null), args);
    }

    public static Method getMethod(String name, Class<?> clazz, Class<?> ... paramTypes) {
        Class<?>[] t = ReflectionUtil.toPrimitiveTypeArray(paramTypes);
        for (Method m : clazz.getMethods()) {
            Class<?>[] types = ReflectionUtil.toPrimitiveTypeArray(m.getParameterTypes());
            if (!m.getName().equals(name) || !ReflectionUtil.equalsTypeArray(types, t)) continue;
            return m;
        }
        return null;
    }

    public static Object invokeMethod(String name, Class<?> clazz, Object obj, Object ... args) throws Exception {
        return ReflectionUtil.getMethod(name, clazz, ReflectionUtil.toPrimitiveTypeArray(args)).invoke(obj, args);
    }

    public static Field getField(String name, Class<?> clazz) throws Exception {
        return clazz.getDeclaredField(name);
    }

    public static Object getValue(String name, Object obj) throws Exception {
        Field f = ReflectionUtil.getField(name, obj.getClass());
        f.setAccessible(true);
        return f.get(obj);
    }

    public static void setValue(Object obj, FieldEntry entry) throws Exception {
        Field f = ReflectionUtil.getField(entry.getKey(), obj.getClass());
        f.setAccessible(true);
        f.set(obj, entry.getValue());
    }

    public static void setValues(Object obj, FieldEntry ... entrys) throws Exception {
        FieldEntry[] arrfieldEntry = entrys;
        int n = entrys.length;
        for (int i = 0; i < n; ++i) {
            FieldEntry f = arrfieldEntry[i];
            ReflectionUtil.setValue(obj, f);
        }
    }

    public static enum DynamicPackage {
        MINECRAFT_SERVER{

            public String toString() {
                return "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().substring(23, 30);
            }
        }
        ,
        CRAFTBUKKIT{

            public String toString() {
                return Bukkit.getServer().getClass().getPackage().getName();
            }
        };

    }

    public static class FieldEntry {
        final String key;
        final Object value;

        public FieldEntry(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public Object getValue() {
            return this.value;
        }
    }
}

