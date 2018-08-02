package com.xactmetal.abstraction.proxy;

public interface ProxyInterface {
	
	public static <T extends ProxyInterface> T newInstance(Class<T> proxyInterface) throws IllegalArgumentException {
		return ProxyObject.newInstance(proxyInterface);
	}
	
	@SuppressWarnings("unchecked")
	public static <MapContext, ArrContext, P extends ProxyInterface> P from(Class<P> type, MapContext src, DeconversionHandler<MapContext, ArrContext> converter) {
		return (P) ProxyDatatype.convertToProxyInterface(type, converter, src);
	}
	
	public <MapContext, ArrContext> MapContext convert(
			ConversionHandler<MapContext, ArrContext> converter);
	
}
