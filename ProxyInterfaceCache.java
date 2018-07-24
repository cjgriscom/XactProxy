package com.xactmetal.abstraction.proxy;

import java.util.HashMap;
import java.util.HashSet;

final class ProxyInterfaceCache {
	private static final HashSet<Class<?>> validatingClasses = new HashSet<>();
	private static final HashMap<Class<?>, ProxyTemplate> validatedClasses = new HashMap<>();
	
	static boolean hasCachedProxyInterface(Class<?> proxyInterface) {
		return validatedClasses.containsKey(proxyInterface);
	}

	static boolean validationInProgress(Class<?> proxyInterface) {
		return validatingClasses.contains(proxyInterface);
	}

	static ProxyTemplate validateProxyInterface(Class<?> proxyInterface) throws IllegalArgumentException {
		if (hasCachedProxyInterface(proxyInterface)) return validatedClasses.get(proxyInterface);
		
		validatingClasses.add(proxyInterface); // Use to detect loops
		
		boolean foundProxyInterface = false;
		for (Class<?> c : proxyInterface.getInterfaces()) {
			if (c.equals(ProxyInterface.class)) {
				foundProxyInterface = true;
			} else {
				validateProxyInterface(c);
			}
		}
		
		if (!foundProxyInterface) {
			throw new IllegalArgumentException(proxyInterface + " does not extend ProxyInterface");
		}
		
		validatedClasses.put(proxyInterface, new ProxyTemplate(proxyInterface));
		
		validatingClasses.remove(proxyInterface);
		
		return validatedClasses.get(proxyInterface);
	}
}
