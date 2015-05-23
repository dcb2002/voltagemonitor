/**
 * @author huangjinhong 
 * qq:2260806429 
 * email:xxonehjh@163.com 
 */
package com.hjh.voltagemonitor.core.util;

import java.lang.reflect.Field;

public class ObjectUtil {

	public static boolean set(Object obj, String name, String value) {
		Field f;
		try {
			f = getField(obj.getClass(), name);
			f.setAccessible(true);

			Object fvalue = value;
			if (f.getType().equals(int.class)) {
				fvalue = Integer.parseInt(value);
			}

			if (f.getType().equals(long.class)) {
				fvalue = Long.parseLong(value);
			}

			if (f.getType().equals(float.class)) {
				fvalue = Float.parseFloat(value);
			}

			f.set(obj, fvalue);
			return true;
		} catch (Exception e) {
			LogHelper.err(e);
		}
		return false;
	}

	public static Object get(Object obj, String name) {
		Field f;
		try {
			f = getField(obj.getClass(), name);
			f.setAccessible(true);
			return f.get(obj);
		} catch (Exception e) {
			LogHelper.err(e);
		}
		return null;
	}

	private static Field getField(Class<?> cls, String name) {
		Field f = null;
		try {
			f = cls.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			return getField(cls.getSuperclass(), name);
		}
		return f;
	}

}
