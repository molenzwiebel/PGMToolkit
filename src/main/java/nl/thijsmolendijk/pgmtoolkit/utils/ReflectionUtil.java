package nl.thijsmolendijk.pgmtoolkit.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class ReflectionUtil {
	public static Object getClass(String name, Object... args) throws Exception {
		Class<?> c = Class.forName(ReflectionUtil.getPackageName() + "." + name);
		int params = 0;
		if (args != null)
			params = args.length;
		for (Constructor<?> co : c.getConstructors())
			if (co.getParameterTypes().length == params)
				return co.newInstance(args);
		return null;
	}

	public static Method getMethod(String name, Class<?> c, int params) {
		for (Method m : c.getMethods())
			if (m.getName().equals(name) && m.getParameterTypes().length == params)
				return m;
		return null;
	}

	public static Object getFieldValue(String field, Class<?> clazz, Object obj) {
		try {
			Field f = clazz.getDeclaredField(field);
			return f.get(obj);
		} catch (Exception e) { return null; }
	}

	public static Object invokeMethod(String methodName, Object obj, Object... args) throws Exception {
		Method m = getMethod(methodName, obj.getClass(), args.length);
		m.setAccessible(true);
		return m.invoke(obj, args);
	}

	public static void setValue(Object instance, String fieldName, Object value) throws Exception {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(instance, value);
	}

	public static String getPackageName() {
		return "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	public static Object getHandle(Object c) {
		Object returning = null;
		try {
			returning = invokeMethod("getHandle", c);
		} catch (Exception e) { }
		return returning;
	}

	public static void resendChunkToPlayer(Chunk c, Player p) {
		try {
			Object playerHandle = getHandle(p);
			Object chunkCoordIntPairQueue = getFieldValue("chunkCoordIntPairQueue", playerHandle.getClass(), playerHandle);
			Object chunkCoordIntPair = getClass("ChunkCoordIntPair", c.getX(), c.getZ());
			invokeMethod("add", chunkCoordIntPairQueue, chunkCoordIntPair);
		} catch (Exception e) {
			
		}
	}
}