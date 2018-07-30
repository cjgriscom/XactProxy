package com.xactmetal.abstraction.proxy;

public abstract class AbstractConversionHandler<MapContext, ArrContext> {
	protected static String OPTIONAL_PROXYINTERFACE_CLASS_KWD = "getClass";
	
	protected abstract int arrayLength(ArrContext src);
	
	protected abstract MapContext constructMap(int slots);
	protected abstract ArrContext constructArray(int size);
	
	// Optional, called after object completion
	protected ProxyInterface postResolve(ProxyInterface obj) {return obj;}

	protected abstract boolean isNull(MapContext src, String name);
	protected abstract boolean isNull(ArrContext src, int i);
	
	protected abstract String getProxyInterfaceClassName(MapContext src);
	protected abstract boolean getBoolean(MapContext src, String name);
	protected abstract boolean getBoolean(ArrContext src, int i);
	protected abstract byte getByte(MapContext src, String name);
	protected abstract byte getByte(ArrContext src, int i);
	protected abstract short getShort(MapContext src, String name);
	protected abstract short getShort(ArrContext src, int i);
	protected abstract int getInteger(MapContext src, String name);
	protected abstract int getInteger(ArrContext src, int i);
	protected abstract long getLong(MapContext src, String name);
	protected abstract long getLong(ArrContext src, int i);
	protected abstract float getFloat(MapContext src, String name);
	protected abstract float getFloat(ArrContext src, int i);
	protected abstract double getDouble(MapContext src, String name);
	protected abstract double getDouble(ArrContext src, int i);
	protected abstract String getString(MapContext src, String name);
	protected abstract String getString(ArrContext src, int i);

	protected abstract MapContext getNestedProxy(MapContext src, String name);
	protected abstract MapContext getNestedProxy(ArrContext src, int i);
	protected abstract ArrContext getArray(MapContext src, String name);
	protected abstract ArrContext getArray(ArrContext src, int i);

	protected abstract MapContext putNull(MapContext dest, String name);
	protected abstract ArrContext putNull(ArrContext dest, int i);
	
	protected abstract MapContext putProxyInterfaceClassName(MapContext dest, String className);
	protected abstract MapContext putBoolean(MapContext dest, String name, boolean obj);
	protected abstract ArrContext putBoolean(ArrContext dest, int i, boolean obj);
	protected abstract MapContext putByte(MapContext dest, String name, byte obj);
	protected abstract ArrContext putByte(ArrContext dest, int i, byte obj);
	protected abstract MapContext putShort(MapContext dest, String name, short obj);
	protected abstract ArrContext putShort(ArrContext dest, int i, short obj);
	protected abstract MapContext putInteger(MapContext dest, String name, int obj);
	protected abstract ArrContext putInteger(ArrContext dest, int i, int obj);
	protected abstract MapContext putLong(MapContext dest, String name, long obj);
	protected abstract ArrContext putLong(ArrContext dest, int i, long obj);
	protected abstract MapContext putFloat(MapContext dest, String name, float obj);
	protected abstract ArrContext putFloat(ArrContext dest, int i, float obj);
	protected abstract MapContext putDouble(MapContext dest, String name, double obj);
	protected abstract ArrContext putDouble(ArrContext dest, int i, double obj);
	protected abstract MapContext putString(MapContext dest, String name, String obj);
	protected abstract ArrContext putString(ArrContext dest, int i, String obj);
	
	protected abstract MapContext putNestedProxy(MapContext dest, String name, MapContext obj);
	protected abstract ArrContext putNestedProxy(ArrContext dest, int i, MapContext obj);
	protected abstract MapContext putArray(MapContext dest, String name, ArrContext obj);
	protected abstract ArrContext putArray(ArrContext dest, int i, ArrContext obj);
	
}
