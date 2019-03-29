package com.xactmetal.abstraction.proxy;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import com.xactmetal.abstraction.proxy.DataConverters.ConstructAndFill;
import com.xactmetal.abstraction.proxy.DataConverters.EqualsFunction;
import com.xactmetal.abstraction.proxy.DataConverters.FillAbstractArray;
import com.xactmetal.abstraction.proxy.DataConverters.UnwrapFlatMap;
import com.xactmetal.abstraction.proxy.DataConverters.WrapFlatMap;

final class ProxyDatatype {
	public final Base base;
	public final int dimensions;
	public final Object defaultValue;
	public final Class<? extends ProxyInterface> proxyInterface;
	public final boolean primitive;
	
	public static enum Base {
		Boolean(boolean.class,Boolean.class,boolean[].class,Boolean[].class,(a,b)->Arrays.equals((boolean[])a,(boolean[])b),DataConverters::unwrapFlatMapBoolean,DataConverters::constructAndFillBoolean,DataConverters::wrapFlatMapBoolean,DataConverters::fillAbstractArrayBoolean),
		Byte   (byte.class,   Byte.class,   byte[].class,   Byte[].class,   (a,b)->Arrays.equals(   (byte[])a,   (byte[])b),DataConverters::unwrapFlatMapByte   ,DataConverters::constructAndFillByte   ,DataConverters::wrapFlatMapByte   ,DataConverters::fillAbstractArrayByte   ),
		Short  (short.class,  Short.class,  short[].class,  Short[].class,  (a,b)->Arrays.equals(  (short[])a,  (short[])b),DataConverters::unwrapFlatMapShort  ,DataConverters::constructAndFillShort  ,DataConverters::wrapFlatMapShort  ,DataConverters::fillAbstractArrayShort  ),
		Integer(int.class,    Integer.class,int[].class,    Integer[].class,(a,b)->Arrays.equals(    (int[])a,    (int[])b),DataConverters::unwrapFlatMapInteger,DataConverters::constructAndFillInteger,DataConverters::wrapFlatMapInteger,DataConverters::fillAbstractArrayInteger),
		Long   (long.class,   Long.class,   long[].class,   Long[].class,   (a,b)->Arrays.equals(   (long[])a,   (long[])b),DataConverters::unwrapFlatMapLong   ,DataConverters::constructAndFillLong   ,DataConverters::wrapFlatMapLong   ,DataConverters::fillAbstractArrayLong   ),
		Float  (float.class,  Float.class,  float[].class,  Float[].class,  (a,b)->Arrays.equals(  (float[])a,  (float[])b),DataConverters::unwrapFlatMapFloat  ,DataConverters::constructAndFillFloat  ,DataConverters::wrapFlatMapFloat  ,DataConverters::fillAbstractArrayFloat  ),
		Double (double.class, Double.class, double[].class, Double[].class, (a,b)->Arrays.equals( (double[])a, (double[])b),DataConverters::unwrapFlatMapDouble ,DataConverters::constructAndFillDouble ,DataConverters::wrapFlatMapDouble ,DataConverters::fillAbstractArrayDouble ),
		String (null,         String.class, null,           String[].class, (a,b)->false                                   ,DataConverters::unwrapFlatMapString ,DataConverters::constructAndFillString ,DataConverters::wrapFlatMapString ,DataConverters::fillAbstractArrayString ),
		ProxyInterface(null,  null,         null,           null,           (a,b)->false                                   , ProxyDatatype::unwrapFlatMapProxy  , ProxyDatatype::constructAndFillProxy  , ProxyDatatype::wrapFlatMapProxy  , ProxyDatatype::fillAbstractArrayProxy  );
		
		final Class<?> primitiveClass, objectClass, primitive1DClass, object1DClass;
		final EqualsFunction arrayEqualsFunction;
		final UnwrapFlatMap unwrapFlatMap;
		final ConstructAndFill constructAndFill;
		final WrapFlatMap wrapFlatMap;
		final FillAbstractArray fillAbstractArray;
		
		Base(
				Class<?> primitiveClass, Class<?> objectClass, 
				Class<?> primitive1DClass, Class<?> object1DClass, 
				EqualsFunction arrayEqualsFunction,
				UnwrapFlatMap unwrapFlatMap, ConstructAndFill constructAndFill,
				WrapFlatMap wrapFlatMap, FillAbstractArray fillAbstractArray) {
			
			this.primitiveClass = primitiveClass;
			this.objectClass = objectClass;
			this.primitive1DClass = primitive1DClass;
			this.object1DClass = object1DClass;
			this.arrayEqualsFunction = arrayEqualsFunction;
			this.unwrapFlatMap = unwrapFlatMap;
			this.constructAndFill = constructAndFill;
			this.wrapFlatMap = wrapFlatMap;
			this.fillAbstractArray = fillAbstractArray;
		}
	}
	
	public boolean isPrimitive() {
		return primitive;
	}
	
	public Class<?> getBaseClass() {
		if (base == Base.ProxyInterface) return proxyInterface;
		return isPrimitive() ? base.primitiveClass : base.objectClass;
	}
	
	public Class<?> get1DClass() {
		if (base == Base.ProxyInterface) return getArrayClass(proxyInterface);
		return isPrimitive() ? base.primitive1DClass : base.object1DClass;
	}
	
	public Class<?> getNDClass(int dims) {
		if (dims == 0) return getBaseClass();
		if (dims == 1) return get1DClass();
		
		Class<?> wrap = get1DClass();
		
		for (int i = 1; i < dims; i++) {
			wrap = getArrayClass(wrap);
		}
		
		return wrap;
	}
	
	@SuppressWarnings("unchecked")
	static <T> Class<? extends T[]> getArrayClass(Class<T> clazz) {
		return (Class<? extends T[]>) Array.newInstance(clazz, 0).getClass();
	}
	
	// Generic
	private ProxyDatatype(Base base, int dimensions, Object defaultValue, Class<? extends ProxyInterface> proxyInterface, boolean primitive) {
		this.base = base;
		this.dimensions = dimensions;
		this.defaultValue = defaultValue;
		this.proxyInterface = proxyInterface;
		this.primitive = primitive;
	}
	
	// ProxyInterface
	public ProxyDatatype(Base base, int dimensions, Class<? extends ProxyInterface> proxyInterface) {
		this(base, dimensions, null, proxyInterface, false);
	}
	
	// Objects
	public ProxyDatatype(Base base, int dimensions, boolean primitive) {
		this(base, dimensions, null, null, primitive);
	}
	
	// Primitives
	public ProxyDatatype(Base base, Object defaultValue) {
		this(base, 0, defaultValue, null, true);
	}
	
	public ProxyDatatype descendArray() {
		return new ProxyDatatype(base, dimensions-1, defaultValue, proxyInterface, primitive);
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ProxyDatatype) {
			return ((ProxyDatatype) other).base == this.base && 
					((ProxyDatatype) other).dimensions == this.dimensions && 
					((ProxyDatatype) other).isPrimitive() == this.isPrimitive();
		} else {
			return false;
		}
	}
	
	// Check if one object of this type equals another of the same type (nonnull) 
	public boolean equalsOther(Object thisObj, Object otherObj) {
		if (dimensions == 0) return thisObj.equals(otherObj);
		else if (dimensions == 1 && primitive) return this.base.arrayEqualsFunction.equalsFunction(thisObj, otherObj);
		return Arrays.deepEquals((Object[]) thisObj, (Object[]) otherObj);
	}
	
	static <MapContext, ArrContext> MapContext convertFromProxyInterface(
			ConversionHandler<MapContext, ArrContext> converter,
			ProxyObject ih) {
		
		MapContext mapctx = converter.constructMap(ih.proxyInterface, ih.templateKeys().size());

		for (String key : ih.templateKeys()) {
			mapctx = ih.getDatatypeFromDatatypeConverterInternal(key)
					.wrapAndPut(converter, key, ih.getObjectFromDatatypeConverterInternal(key), mapctx);
		}
		
		return mapctx;
	}
	
	static <MapContext, ArrContext> MapContext convertFromProxyInterface(
			ConversionHandler<MapContext, ArrContext> converter,
			ProxyInterface intr) {
		
		return convertFromProxyInterface(converter, (ProxyObject) Proxy.getInvocationHandler(intr));
	}

	public <MapContext, ArrContext> MapContext wrapAndPut(
			ConversionHandler<MapContext, ArrContext> converter,
			String name,
			Object item,
			MapContext mapctx) throws IllegalArgumentException {
		
		if (item == null) {
			return converter.putNull(mapctx, name);
		} else if (dimensions == 0) {
			return base.wrapFlatMap.wrapFlatMap(converter, mapctx, name, item, this);
		} else {
			ArrContext arr = createWrappedNDArray(converter, item);
			return converter.putArray(mapctx, name, arr);
		}
	}
	
	private <MapContext, ArrContext> ArrContext createWrappedNDArray(
			ConversionHandler<MapContext, ArrContext> converter, 
			Object item) throws IllegalArgumentException {
		
		int len = Array.getLength(item);
		ArrContext arr = converter.constructArray(getNDClass(dimensions-1), len);
		
		if (dimensions == 1) {
			return base.fillAbstractArray.fillAbstractArray(converter, item, arr, this);
		} else {
			Object[] unwrappedSrc = (Object[])item;
			ProxyDatatype descend = descendArray();
			for (int i = 0; i < len; i++) {
				if (unwrappedSrc[i] == null) arr = converter.putNull(arr, i);
				else arr = converter.putArray(arr, i, descend.createWrappedNDArray(converter, unwrappedSrc[i]));
			}
			return arr;
		}
	}
	
	static <MapContext, ArrContext> ProxyInterface convertToProxyInterface(
			Class<? extends ProxyInterface> templateClass,
			DeconversionHandler<MapContext, ArrContext> converter,
			MapContext mapctx) {
		ProxyInterface intr = ProxyObject.newInstance(templateClass);
		intr = converter.preResolve(templateClass, intr);
		ProxyObject ih = (ProxyObject) Proxy.getInvocationHandler(intr);
		
		for (String key : ih.templateKeys()) {
			ih.putObjectFromDatatypeConverterInternal(key, 
					ih.getDatatypeFromDatatypeConverterInternal(key).unwrap(converter, key, mapctx));
		}
		
		return converter.postResolve(templateClass, intr);
	}
	
	private <MapContext, ArrContext> Object unwrap(
			DeconversionHandler<MapContext, ArrContext> converter, 
			String name,
			MapContext mapctx) throws IllegalArgumentException {
		
		if (converter.isNull(mapctx, name)) {
			if (this.isPrimitive() && this.dimensions == 0) {
				return this.defaultValue;
			} else {
				return null;
			}
		} else if (dimensions == 0) {
			return base.unwrapFlatMap.unwrapFlatMap(converter, mapctx, name, this);
		} else {
			return createUnwrappedNDArray(converter, converter.getArray(getNDClass(dimensions-1), mapctx, name));
		}
	}
	
	private <MapContext, ArrContext> Object createUnwrappedNDArray(
			DeconversionHandler<MapContext, ArrContext> converter, 
			ArrContext src) throws IllegalArgumentException {
		
		if (dimensions == 1) {
			return base.constructAndFill.constructAndFill(converter, src, this);
		} else {
			Class<?> arrayBase = getNDClass(dimensions-1);
			int len = converter.arrayLength(src);
			Object[] arr = (Object[]) Array.newInstance(arrayBase, len);
			ProxyDatatype descend = descendArray();
			for (int i = 0; i < len; i++) {
				if (converter.isNull(src, i)) arr[i] = null;
				else arr[i] = descend.createUnwrappedNDArray(converter, converter.getArray(arrayBase, src, i));
			}
			return arr;
		}
	}
	

	// Data Converters for Proxy Objects
	public static <MapContext, ArrContext> Object unwrapFlatMapProxy(DeconversionHandler<MapContext, ArrContext> converter, MapContext src, String name, ProxyDatatype type) {
		@SuppressWarnings("unchecked")
		MapContext nested = converter.getNestedProxy((Class<? extends ProxyInterface>) type.getBaseClass(), src, name);
		return convertToProxyInterface(type.proxyInterface, converter, nested);
	}
	
	public static <MapContext, ArrContext> Object constructAndFillProxy(DeconversionHandler<MapContext, ArrContext> converter, ArrContext src, ProxyDatatype type) {
		int len = converter.arrayLength(src);
		
		Object[] arr = (Object[]) Array.newInstance(type.proxyInterface, len);
		for (int i = 0; i < len; i++) {
			if (!converter.isNull(src, i)) {
				@SuppressWarnings("unchecked")
				Object o = convertToProxyInterface(type.proxyInterface, converter, converter.getNestedProxy((Class<? extends ProxyInterface>) type.getBaseClass(), src, i));
				arr[i] = (ProxyInterface)type.proxyInterface.cast(o);
			}
		}
		return arr;
	}
	
	public static <MapContext, ArrContext> MapContext wrapFlatMapProxy(ConversionHandler<MapContext, ArrContext> converter, MapContext dest, String name, Object item, ProxyDatatype type) {
		return converter.putNestedProxy(dest, name, convertFromProxyInterface(converter, (ProxyInterface) item));
	}
	
	public static <MapContext, ArrContext> ArrContext fillAbstractArrayProxy(ConversionHandler<MapContext, ArrContext> converter, Object src, ArrContext dest, ProxyDatatype type) {
		Object[] srcArr = (Object[]) src;
		for (int i = 0; i < srcArr.length; i++) {
			if (srcArr[i] == null) converter.putNull(dest, i);
			else converter.putNestedProxy(dest, i, convertFromProxyInterface(converter, (ProxyInterface) srcArr[i]));
		}
		
		return dest;
	}
}
