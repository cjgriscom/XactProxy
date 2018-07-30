package com.xactmetal.abstraction.proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

final class DataConverters {
	
	public static void main(String[] args) throws FileNotFoundException {
		// Generate primitives
		File src = new File("src/main/java/com/xactmetal/abstraction/proxy/DataConverters.java");
		Scanner in = new Scanner(src);
		boolean started = false;
		ArrayList<String> lines = new ArrayList<String>();
		while (in.hasNextLine()) {
			String line = in.nextLine();
			if (line.equals("\t// template for primitives")) {
				lines.add("\n\t// Generated boolean variant");
				started = true;
			} else if (line.equals("\t// end template for primitives")) {
				started = false;
			} else if (started) {
				lines.add(line);
			}
		}
		in.close();
		
		String[] primTypes = new String[] {"byte", "short", "int", "long", "float", "double", "remove"};
		String[] objTypes = new String[] {"Byte", "Short", "Integer", "Long", "Float", "Double", "String"};
		
		for (int i = 0; i < primTypes.length; i++) {
			for (String line : lines) {
				System.out.println(line.replaceAll("boolean", primTypes[i]).replaceAll("Boolean", objTypes[i]));
			}
		}
	}
	
	static interface UnwrapFlatMap {
		<MapContext, ArrContext> Object unwrapFlatMap(
				DeconversionHandler<MapContext, ArrContext> converter, MapContext src, String name, ProxyDatatype type) throws ClassCastException, ClassNotFoundException;
	}
	
	static interface ConstructAndFill {
		<MapContext, ArrContext> Object constructAndFill(
				DeconversionHandler<MapContext, ArrContext> converter, ArrContext src, ProxyDatatype type) throws ClassCastException, ClassNotFoundException;
	}
	
	static interface WrapFlatMap {
		<MapContext, ArrContext> MapContext wrapFlatMap(
				ConversionHandler<MapContext, ArrContext> converter, MapContext dest, String name, Object item, ProxyDatatype type);
	}
	
	static interface FillAbstractArray {
		<MapContext, ArrContext> ArrContext fillAbstractArray(
				ConversionHandler<MapContext, ArrContext> converter, Object src, ArrContext dest, ProxyDatatype type);
	}
	
	// template for primitives
	public static <MapContext, ArrContext> Object unwrapFlatMapBoolean(DeconversionHandler<MapContext, ArrContext> converter, MapContext src, String name, ProxyDatatype type) {
		return converter.getBoolean(src, name);
	}
	
	public static <MapContext, ArrContext> Object constructAndFillBoolean(DeconversionHandler<MapContext, ArrContext> converter, ArrContext src, ProxyDatatype type) {
		int len = converter.arrayLength(src);
		if (type.isPrimitive()) {
			boolean[] arr = new boolean[len];
			for (int i = 0; i < len; i++) arr[i] = converter.getBoolean(src, i);
			return arr;
		} else {
			Boolean[] arr = new Boolean[len];
			for (int i = 0; i < len; i++) 
				if (!converter.isNull(src, i)) arr[i] = converter.getBoolean(src, i);
			return arr;
		}
	}
	
	public static <MapContext, ArrContext> MapContext wrapFlatMapBoolean(ConversionHandler<MapContext, ArrContext> converter, MapContext dest, String name, Object item, ProxyDatatype type) {
		return converter.putBoolean(dest, name, (Boolean) item);
	}
	
	public static <MapContext, ArrContext> ArrContext fillAbstractArrayBoolean(ConversionHandler<MapContext, ArrContext> converter, Object src, ArrContext dest, ProxyDatatype type) {
		if (type.isPrimitive()) {
			boolean[] srcArr = (boolean[]) src;
			for (int i = 0; i < srcArr.length; i++) converter.putBoolean(dest, i, srcArr[i]);
		} else {
			Boolean[] srcArr = (Boolean[]) src;
			for (int i = 0; i < srcArr.length; i++) {
				if (srcArr[i] != null) converter.putBoolean(dest, i, srcArr[i]);
				else converter.putNull(dest, i);
			}
		}
		return dest;
	}
	// end template for primitives


	// Generated byte variant
	public static <MapContext, ArrContext> Object unwrapFlatMapByte(DeconversionHandler<MapContext, ArrContext> converter, MapContext src, String name, ProxyDatatype type) {
		return converter.getByte(src, name);
	}
	
	public static <MapContext, ArrContext> Object constructAndFillByte(DeconversionHandler<MapContext, ArrContext> converter, ArrContext src, ProxyDatatype type) {
		int len = converter.arrayLength(src);
		if (type.isPrimitive()) {
			byte[] arr = new byte[len];
			for (int i = 0; i < len; i++) arr[i] = converter.getByte(src, i);
			return arr;
		} else {
			Byte[] arr = new Byte[len];
			for (int i = 0; i < len; i++) 
				if (!converter.isNull(src, i)) arr[i] = converter.getByte(src, i);
			return arr;
		}
	}
	
	public static <MapContext, ArrContext> MapContext wrapFlatMapByte(ConversionHandler<MapContext, ArrContext> converter, MapContext dest, String name, Object item, ProxyDatatype type) {
		return converter.putByte(dest, name, (Byte) item);
	}
	
	public static <MapContext, ArrContext> ArrContext fillAbstractArrayByte(ConversionHandler<MapContext, ArrContext> converter, Object src, ArrContext dest, ProxyDatatype type) {
		if (type.isPrimitive()) {
			byte[] srcArr = (byte[]) src;
			for (int i = 0; i < srcArr.length; i++) converter.putByte(dest, i, srcArr[i]);
		} else {
			Byte[] srcArr = (Byte[]) src;
			for (int i = 0; i < srcArr.length; i++) {
				if (srcArr[i] != null) converter.putByte(dest, i, srcArr[i]);
				else converter.putNull(dest, i);
			}
		}
		return dest;
	}

	// Generated short variant
	public static <MapContext, ArrContext> Object unwrapFlatMapShort(DeconversionHandler<MapContext, ArrContext> converter, MapContext src, String name, ProxyDatatype type) {
		return converter.getShort(src, name);
	}
	
	public static <MapContext, ArrContext> Object constructAndFillShort(DeconversionHandler<MapContext, ArrContext> converter, ArrContext src, ProxyDatatype type) {
		int len = converter.arrayLength(src);
		if (type.isPrimitive()) {
			short[] arr = new short[len];
			for (int i = 0; i < len; i++) arr[i] = converter.getShort(src, i);
			return arr;
		} else {
			Short[] arr = new Short[len];
			for (int i = 0; i < len; i++) 
				if (!converter.isNull(src, i)) arr[i] = converter.getShort(src, i);
			return arr;
		}
	}
	
	public static <MapContext, ArrContext> MapContext wrapFlatMapShort(ConversionHandler<MapContext, ArrContext> converter, MapContext dest, String name, Object item, ProxyDatatype type) {
		return converter.putShort(dest, name, (Short) item);
	}
	
	public static <MapContext, ArrContext> ArrContext fillAbstractArrayShort(ConversionHandler<MapContext, ArrContext> converter, Object src, ArrContext dest, ProxyDatatype type) {
		if (type.isPrimitive()) {
			short[] srcArr = (short[]) src;
			for (int i = 0; i < srcArr.length; i++) converter.putShort(dest, i, srcArr[i]);
		} else {
			Short[] srcArr = (Short[]) src;
			for (int i = 0; i < srcArr.length; i++) {
				if (srcArr[i] != null) converter.putShort(dest, i, srcArr[i]);
				else converter.putNull(dest, i);
			}
		}
		return dest;
	}

	// Generated int variant
	public static <MapContext, ArrContext> Object unwrapFlatMapInteger(DeconversionHandler<MapContext, ArrContext> converter, MapContext src, String name, ProxyDatatype type) {
		return converter.getInteger(src, name);
	}
	
	public static <MapContext, ArrContext> Object constructAndFillInteger(DeconversionHandler<MapContext, ArrContext> converter, ArrContext src, ProxyDatatype type) {
		int len = converter.arrayLength(src);
		if (type.isPrimitive()) {
			int[] arr = new int[len];
			for (int i = 0; i < len; i++) arr[i] = converter.getInteger(src, i);
			return arr;
		} else {
			Integer[] arr = new Integer[len];
			for (int i = 0; i < len; i++) 
				if (!converter.isNull(src, i)) arr[i] = converter.getInteger(src, i);
			return arr;
		}
	}
	
	public static <MapContext, ArrContext> MapContext wrapFlatMapInteger(ConversionHandler<MapContext, ArrContext> converter, MapContext dest, String name, Object item, ProxyDatatype type) {
		return converter.putInteger(dest, name, (Integer) item);
	}
	
	public static <MapContext, ArrContext> ArrContext fillAbstractArrayInteger(ConversionHandler<MapContext, ArrContext> converter, Object src, ArrContext dest, ProxyDatatype type) {
		if (type.isPrimitive()) {
			int[] srcArr = (int[]) src;
			for (int i = 0; i < srcArr.length; i++) converter.putInteger(dest, i, srcArr[i]);
		} else {
			Integer[] srcArr = (Integer[]) src;
			for (int i = 0; i < srcArr.length; i++) {
				if (srcArr[i] != null) converter.putInteger(dest, i, srcArr[i]);
				else converter.putNull(dest, i);
			}
		}
		return dest;
	}

	// Generated long variant
	public static <MapContext, ArrContext> Object unwrapFlatMapLong(DeconversionHandler<MapContext, ArrContext> converter, MapContext src, String name, ProxyDatatype type) {
		return converter.getLong(src, name);
	}
	
	public static <MapContext, ArrContext> Object constructAndFillLong(DeconversionHandler<MapContext, ArrContext> converter, ArrContext src, ProxyDatatype type) {
		int len = converter.arrayLength(src);
		if (type.isPrimitive()) {
			long[] arr = new long[len];
			for (int i = 0; i < len; i++) arr[i] = converter.getLong(src, i);
			return arr;
		} else {
			Long[] arr = new Long[len];
			for (int i = 0; i < len; i++) 
				if (!converter.isNull(src, i)) arr[i] = converter.getLong(src, i);
			return arr;
		}
	}
	
	public static <MapContext, ArrContext> MapContext wrapFlatMapLong(ConversionHandler<MapContext, ArrContext> converter, MapContext dest, String name, Object item, ProxyDatatype type) {
		return converter.putLong(dest, name, (Long) item);
	}
	
	public static <MapContext, ArrContext> ArrContext fillAbstractArrayLong(ConversionHandler<MapContext, ArrContext> converter, Object src, ArrContext dest, ProxyDatatype type) {
		if (type.isPrimitive()) {
			long[] srcArr = (long[]) src;
			for (int i = 0; i < srcArr.length; i++) converter.putLong(dest, i, srcArr[i]);
		} else {
			Long[] srcArr = (Long[]) src;
			for (int i = 0; i < srcArr.length; i++) {
				if (srcArr[i] != null) converter.putLong(dest, i, srcArr[i]);
				else converter.putNull(dest, i);
			}
		}
		return dest;
	}

	// Generated float variant
	public static <MapContext, ArrContext> Object unwrapFlatMapFloat(DeconversionHandler<MapContext, ArrContext> converter, MapContext src, String name, ProxyDatatype type) {
		return converter.getFloat(src, name);
	}
	
	public static <MapContext, ArrContext> Object constructAndFillFloat(DeconversionHandler<MapContext, ArrContext> converter, ArrContext src, ProxyDatatype type) {
		int len = converter.arrayLength(src);
		if (type.isPrimitive()) {
			float[] arr = new float[len];
			for (int i = 0; i < len; i++) arr[i] = converter.getFloat(src, i);
			return arr;
		} else {
			Float[] arr = new Float[len];
			for (int i = 0; i < len; i++) 
				if (!converter.isNull(src, i)) arr[i] = converter.getFloat(src, i);
			return arr;
		}
	}
	
	public static <MapContext, ArrContext> MapContext wrapFlatMapFloat(ConversionHandler<MapContext, ArrContext> converter, MapContext dest, String name, Object item, ProxyDatatype type) {
		return converter.putFloat(dest, name, (Float) item);
	}
	
	public static <MapContext, ArrContext> ArrContext fillAbstractArrayFloat(ConversionHandler<MapContext, ArrContext> converter, Object src, ArrContext dest, ProxyDatatype type) {
		if (type.isPrimitive()) {
			float[] srcArr = (float[]) src;
			for (int i = 0; i < srcArr.length; i++) converter.putFloat(dest, i, srcArr[i]);
		} else {
			Float[] srcArr = (Float[]) src;
			for (int i = 0; i < srcArr.length; i++) {
				if (srcArr[i] != null) converter.putFloat(dest, i, srcArr[i]);
				else converter.putNull(dest, i);
			}
		}
		return dest;
	}

	// Generated double variant
	public static <MapContext, ArrContext> Object unwrapFlatMapDouble(DeconversionHandler<MapContext, ArrContext> converter, MapContext src, String name, ProxyDatatype type) {
		return converter.getDouble(src, name);
	}
	
	public static <MapContext, ArrContext> Object constructAndFillDouble(DeconversionHandler<MapContext, ArrContext> converter, ArrContext src, ProxyDatatype type) {
		int len = converter.arrayLength(src);
		if (type.isPrimitive()) {
			double[] arr = new double[len];
			for (int i = 0; i < len; i++) arr[i] = converter.getDouble(src, i);
			return arr;
		} else {
			Double[] arr = new Double[len];
			for (int i = 0; i < len; i++) 
				if (!converter.isNull(src, i)) arr[i] = converter.getDouble(src, i);
			return arr;
		}
	}
	
	public static <MapContext, ArrContext> MapContext wrapFlatMapDouble(ConversionHandler<MapContext, ArrContext> converter, MapContext dest, String name, Object item, ProxyDatatype type) {
		return converter.putDouble(dest, name, (Double) item);
	}
	
	public static <MapContext, ArrContext> ArrContext fillAbstractArrayDouble(ConversionHandler<MapContext, ArrContext> converter, Object src, ArrContext dest, ProxyDatatype type) {
		if (type.isPrimitive()) {
			double[] srcArr = (double[]) src;
			for (int i = 0; i < srcArr.length; i++) converter.putDouble(dest, i, srcArr[i]);
		} else {
			Double[] srcArr = (Double[]) src;
			for (int i = 0; i < srcArr.length; i++) {
				if (srcArr[i] != null) converter.putDouble(dest, i, srcArr[i]);
				else converter.putNull(dest, i);
			}
		}
		return dest;
	}

	// Generated String variant
	public static <MapContext, ArrContext> Object unwrapFlatMapString(DeconversionHandler<MapContext, ArrContext> converter, MapContext src, String name, ProxyDatatype type) {
		return converter.getString(src, name);
	}
	
	public static <MapContext, ArrContext> Object constructAndFillString(DeconversionHandler<MapContext, ArrContext> converter, ArrContext src, ProxyDatatype type) {
		int len = converter.arrayLength(src);
		
		String[] arr = new String[len];
		for (int i = 0; i < len; i++) 
			if (!converter.isNull(src, i)) arr[i] = converter.getString(src, i);
		return arr;
	}
	
	public static <MapContext, ArrContext> MapContext wrapFlatMapString(ConversionHandler<MapContext, ArrContext> converter, MapContext dest, String name, Object item, ProxyDatatype type) {
		return converter.putString(dest, name, (String) item);
	}
	
	public static <MapContext, ArrContext> ArrContext fillAbstractArrayString(ConversionHandler<MapContext, ArrContext> converter, Object src, ArrContext dest, ProxyDatatype type) {
		String[] srcArr = (String[]) src;
		for (int i = 0; i < srcArr.length; i++) {
			if (srcArr[i] != null) converter.putString(dest, i, srcArr[i]);
			else converter.putNull(dest, i);
		}
		
		return dest;
	}

}
