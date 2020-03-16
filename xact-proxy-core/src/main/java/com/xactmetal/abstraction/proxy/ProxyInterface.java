package com.xactmetal.abstraction.proxy;

import java.util.Comparator;

public interface ProxyInterface {
	
	public static <T extends ProxyInterface> T newInstance(Class<T> proxyInterface) throws IllegalArgumentException {
		return ProxyObject.newInstance(proxyInterface);
	}
	
	@SuppressWarnings("unchecked")
	public static <MapContext, ArrContext, P extends ProxyInterface> P from(Class<P> type, MapContext src, DeconversionHandler<MapContext, ArrContext> converter) {
		return (P) ProxyDatatype.convertToProxyInterface(type, converter, src);
	}
	
	public static <MapContext, ArrContext> MapContext convert(ProxyInterface intr, ConversionHandler<MapContext, ArrContext> converter) {
		return ProxyDatatype.convertFromProxyInterface(converter, intr);
	}
	
	public <MapContext, ArrContext> MapContext convert(
			ConversionHandler<MapContext, ArrContext> converter);

	@SuppressWarnings("unchecked")
	public static <T extends ProxyInterface> Class<T> classOf(T proxyInterface) {
		return (Class<T>) proxyInterface.getClass().getInterfaces()[0];
	}
	
	/**
	 * Returns a comparator that will sort a list of field names according to their @Order declarations
	 * @param proxyInterface
	 * @return
	 */
	public static <T extends ProxyInterface> Comparator<String> getKeyOrderComparator(Class<T> proxyInterface) {
		return ProxyObject.getKeyOrderComparator(proxyInterface);
	}
	
}
