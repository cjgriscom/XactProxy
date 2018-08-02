package com.xactmetal.abstraction.proxy;

public interface DeconversionHandler<MapContext, ArrContext> {
	public int arrayLength(ArrContext src);
	
	// Optional, called before filling out object
	public default ProxyInterface preResolve(ProxyInterface obj) {return obj;}
	// Optional, called after object completion
	public default ProxyInterface postResolve(ProxyInterface obj) {return obj;}

	public boolean isNull(MapContext src, String name);
	public boolean isNull(ArrContext src, int i);
	
	public boolean getBoolean(MapContext src, String name);
	public boolean getBoolean(ArrContext src, int i);
	public byte getByte(MapContext src, String name);
	public byte getByte(ArrContext src, int i);
	public short getShort(MapContext src, String name);
	public short getShort(ArrContext src, int i);
	public int getInteger(MapContext src, String name);
	public int getInteger(ArrContext src, int i);
	public long getLong(MapContext src, String name);
	public long getLong(ArrContext src, int i);
	public float getFloat(MapContext src, String name);
	public float getFloat(ArrContext src, int i);
	public double getDouble(MapContext src, String name);
	public double getDouble(ArrContext src, int i);
	public String getString(MapContext src, String name);
	public String getString(ArrContext src, int i);

	public MapContext getNestedProxy(Class<? extends ProxyInterface> targetClass, MapContext src, String name);
	public MapContext getNestedProxy(Class<? extends ProxyInterface> targetClass, ArrContext src, int i);
	public ArrContext getArray(MapContext src, String name);
	public ArrContext getArray(ArrContext src, int i);
}
