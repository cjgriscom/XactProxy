package com.xactmetal.abstraction.proxy;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;

import com.xactmetal.abstraction.proxy.DataConverters.ConstructAndFill;
import com.xactmetal.abstraction.proxy.DataConverters.FillAbstractArray;
import com.xactmetal.abstraction.proxy.DataConverters.UnwrapFlatMap;
import com.xactmetal.abstraction.proxy.DataConverters.WrapFlatMap;

final class ProxyDatatype {
	public final Base base;
	public final int dimensions;
	public final Object defaultValue;
	public final Class<?> proxyInterface;
	public final boolean primitive;
	
	public static enum Base {
		Boolean(boolean.class,Boolean.class,boolean[].class,Boolean[].class,DataConverters::unwrapFlatMapBoolean,DataConverters::constructAndFillBoolean,DataConverters::wrapFlatMapBoolean,DataConverters::fillAbstractArrayBoolean),
		Byte   (byte.class,   Byte.class,   byte[].class,   Byte[].class,   DataConverters::unwrapFlatMapByte   ,DataConverters::constructAndFillByte   ,DataConverters::wrapFlatMapByte   ,DataConverters::fillAbstractArrayByte   ),
		Short  (short.class,  Short.class,  short[].class,  Short[].class,  DataConverters::unwrapFlatMapShort  ,DataConverters::constructAndFillShort  ,DataConverters::wrapFlatMapShort  ,DataConverters::fillAbstractArrayShort  ),
		Integer(int.class,    Integer.class,int[].class,    Integer[].class,DataConverters::unwrapFlatMapInteger,DataConverters::constructAndFillInteger,DataConverters::wrapFlatMapInteger,DataConverters::fillAbstractArrayInteger),
		Long   (long.class,   Long.class,   long[].class,   Long[].class,   DataConverters::unwrapFlatMapLong   ,DataConverters::constructAndFillLong   ,DataConverters::wrapFlatMapLong   ,DataConverters::fillAbstractArrayLong   ),
		Float  (float.class,  Float.class,  float[].class,  Float[].class,  DataConverters::unwrapFlatMapFloat  ,DataConverters::constructAndFillFloat  ,DataConverters::wrapFlatMapFloat  ,DataConverters::fillAbstractArrayFloat  ),
		Double (double.class, Double.class, double[].class, Double[].class, DataConverters::unwrapFlatMapDouble ,DataConverters::constructAndFillDouble ,DataConverters::wrapFlatMapDouble ,DataConverters::fillAbstractArrayDouble ),
		String (null,         String.class, null,           String[].class, DataConverters::unwrapFlatMapString ,DataConverters::constructAndFillString ,DataConverters::wrapFlatMapString ,DataConverters::fillAbstractArrayString ),
		ProxyInterface(null,  null,         null,           null,            ProxyDatatype::unwrapFlatMapProxy  , ProxyDatatype::constructAndFillProxy  , ProxyDatatype::wrapFlatMapProxy  , ProxyDatatype::fillAbstractArrayProxy  );
		
		final Class<?> primitiveClass, objectClass, primitive1DClass, object1DClass;
		final UnwrapFlatMap unwrapFlatMap;
		final ConstructAndFill constructAndFill;
		final WrapFlatMap wrapFlatMap;
		final FillAbstractArray fillAbstractArray;
		
		Base(
				Class<?> primitiveClass, Class<?> objectClass, 
				Class<?> primitive1DClass, Class<?> object1DClass, 
				UnwrapFlatMap unwrapFlatMap, ConstructAndFill constructAndFill,
				WrapFlatMap wrapFlatMap, FillAbstractArray fillAbstractArray) {
			
			this.primitiveClass = primitiveClass;
			this.objectClass = objectClass;
			this.primitive1DClass = primitive1DClass;
			this.object1DClass = object1DClass;
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
	private ProxyDatatype(Base base, int dimensions, Object defaultValue, Class<?> proxyInterface, boolean primitive) {
		this.base = base;
		this.dimensions = dimensions;
		this.defaultValue = defaultValue;
		this.proxyInterface = proxyInterface;
		this.primitive = primitive;
	}
	
	// ProxyInterface
	public ProxyDatatype(Base base, int dimensions, Class<?> proxyInterface) {
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
	
	static <MapContext, ArrContext> MapContext convertFromProxyInterface(
			AbstractConversionHandler<MapContext, ArrContext> converter,
			ProxyObject ih) {
		
		MapContext mapctx = converter.constructMap(ih.templateKeys().size());
		converter.putProxyInterfaceClassName(mapctx, ih.proxyInterface.getName());
		
		for (String key : ih.templateKeys()) {
			mapctx = ih.getDatatypeFromDatatypeConverterInternal(key)
					.wrapAndPut(converter, key, ih.getObjectFromDatatypeConverterInternal(key), mapctx);
		}
		
		return mapctx;
	}
	
	static <MapContext, ArrContext> MapContext convertFromProxyInterface(
			AbstractConversionHandler<MapContext, ArrContext> converter,
			ProxyInterface intr) {
		
		return convertFromProxyInterface(converter, (ProxyObject) Proxy.getInvocationHandler(intr));
	}

	public <MapContext, ArrContext> MapContext wrapAndPut(
			AbstractConversionHandler<MapContext, ArrContext> converter,
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
			AbstractConversionHandler<MapContext, ArrContext> converter, 
			Object item) throws IllegalArgumentException {
		
		int len = Array.getLength(item);
		ArrContext arr = converter.constructArray(len);
		
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
	
	@SuppressWarnings("unchecked")
	static <MapContext, ArrContext> ProxyInterface convertToProxyInterface(
			AbstractConversionHandler<MapContext, ArrContext> converter,
			MapContext mapctx) throws ClassCastException, ClassNotFoundException {
		String className = converter.getProxyInterfaceClassName(mapctx);
		Class<? extends ProxyInterface> clazz = (Class<? extends ProxyInterface>) Class.forName(className);
		ProxyInterface intr = ProxyObject.newInstance(clazz);
		ProxyObject ih = (ProxyObject) Proxy.getInvocationHandler(intr);

		for (String key : ih.templateKeys()) {
			ih.putObjectFromDatatypeConverterInternal(key, 
					ih.getDatatypeFromDatatypeConverterInternal(key).unwrap(converter, key, mapctx));
		}
		
		return converter.postResolve(intr);
	}
	
	private <MapContext, ArrContext> Object unwrap(
			AbstractConversionHandler<MapContext, ArrContext> converter, 
			String name,
			MapContext mapctx) throws IllegalArgumentException, ClassCastException, ClassNotFoundException {
		
		if (converter.isNull(mapctx, name)) return null;
		else if (dimensions == 0) {
			return base.unwrapFlatMap.unwrapFlatMap(converter, mapctx, name, this);
		} else {
			return createUnwrappedNDArray(converter, converter.getArray(mapctx, name));
		}
	}
	
	private <MapContext, ArrContext> Object createUnwrappedNDArray(
			AbstractConversionHandler<MapContext, ArrContext> converter, 
			ArrContext src) throws IllegalArgumentException, ClassCastException, ClassNotFoundException {
		
		if (dimensions == 1) {
			return base.constructAndFill.constructAndFill(converter, src, this);
		} else {
			int len = converter.arrayLength(src);
			Object[] arr = (Object[]) Array.newInstance(getNDClass(dimensions-1), len);
			ProxyDatatype descend = descendArray();
			for (int i = 0; i < len; i++) {
				if (converter.isNull(src, i)) arr[i] = null;
				else arr[i] = descend.createUnwrappedNDArray(converter, converter.getArray(src, i));
			}
			return arr;
		}
	}
	

	// Data Converters for Proxy Objects
	public static <MapContext, ArrContext> Object unwrapFlatMapProxy(AbstractConversionHandler<MapContext, ArrContext> converter, MapContext src, String name, ProxyDatatype type) throws ClassCastException, ClassNotFoundException {
		MapContext nested = converter.getNestedProxy(src, name);
		return convertToProxyInterface(converter, nested);
	}
	
	public static <MapContext, ArrContext> Object constructAndFillProxy(AbstractConversionHandler<MapContext, ArrContext> converter, ArrContext src, ProxyDatatype type) throws ClassCastException, ClassNotFoundException {
		int len = converter.arrayLength(src);
		
		Object[] arr = (Object[]) Array.newInstance(type.proxyInterface, len);
		for (int i = 0; i < len; i++) {
			if (!converter.isNull(src, i)) {
				Object o = convertToProxyInterface(converter, converter.getNestedProxy(src, i));
				arr[i] = (ProxyInterface)type.proxyInterface.cast(o);
			}
		}
		return arr;
	}
	
	public static <MapContext, ArrContext> MapContext wrapFlatMapProxy(AbstractConversionHandler<MapContext, ArrContext> converter, MapContext dest, String name, Object item, ProxyDatatype type) {
		return converter.putNestedProxy(dest, name, convertFromProxyInterface(converter, (ProxyInterface) item));
	}
	
	public static <MapContext, ArrContext> ArrContext fillAbstractArrayProxy(AbstractConversionHandler<MapContext, ArrContext> converter, Object src, ArrContext dest, ProxyDatatype type) {
		Object[] srcArr = (Object[]) src;
		for (int i = 0; i < srcArr.length; i++) {
			if (srcArr[i] == null) converter.putNull(dest, i);
			else converter.putNestedProxy(dest, i, convertFromProxyInterface(converter, (ProxyInterface) srcArr[i]));
		}
		
		return dest;
	}
}
