package com.xactmetal.abstraction.proxy;

public interface ConversionHandler<MapContext, ArrContext> extends ConversionConstants {
	public MapContext constructMap(int slots);
	public ArrContext constructArray(int size);
	
	public MapContext putNull(MapContext dest, String name);
	public ArrContext putNull(ArrContext dest, int i);
	
	public MapContext putProxyInterfaceClassName(MapContext dest, String className);
	public MapContext putBoolean(MapContext dest, String name, boolean obj);
	public ArrContext putBoolean(ArrContext dest, int i, boolean obj);
	public MapContext putByte(MapContext dest, String name, byte obj);
	public ArrContext putByte(ArrContext dest, int i, byte obj);
	public MapContext putShort(MapContext dest, String name, short obj);
	public ArrContext putShort(ArrContext dest, int i, short obj);
	public MapContext putInteger(MapContext dest, String name, int obj);
	public ArrContext putInteger(ArrContext dest, int i, int obj);
	public MapContext putLong(MapContext dest, String name, long obj);
	public ArrContext putLong(ArrContext dest, int i, long obj);
	public MapContext putFloat(MapContext dest, String name, float obj);
	public ArrContext putFloat(ArrContext dest, int i, float obj);
	public MapContext putDouble(MapContext dest, String name, double obj);
	public ArrContext putDouble(ArrContext dest, int i, double obj);
	public MapContext putString(MapContext dest, String name, String obj);
	public ArrContext putString(ArrContext dest, int i, String obj);
	
	public MapContext putNestedProxy(MapContext dest, String name, MapContext obj);
	public ArrContext putNestedProxy(ArrContext dest, int i, MapContext obj);
	public MapContext putArray(MapContext dest, String name, ArrContext obj);
	public ArrContext putArray(ArrContext dest, int i, ArrContext obj);
	
}
