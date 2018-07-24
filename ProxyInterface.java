package com.xactmetal.abstraction.proxy;

public interface ProxyInterface {
	
	public static <T extends ProxyInterface> T newInstance(Class<T> proxyInterface) throws IllegalArgumentException {
		return ProxyObject.newInstance(proxyInterface);
	}
	
	public static <MapContext, ArrContext> ProxyInterface from(MapContext src, AbstractConversionHandler<MapContext, ArrContext> converter) throws ClassCastException, ClassNotFoundException {
		return ProxyDatatype.convertToProxyInterface(converter, src);
	}
	
	public <MapContext, ArrContext> MapContext convert(
			AbstractConversionHandler<MapContext, ArrContext> converter);
	
}
