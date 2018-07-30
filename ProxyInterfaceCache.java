package com.xactmetal.abstraction.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
			// Special exception for interfaces with only static and default methods
			for (Method m : proxyInterface.getDeclaredMethods()) {
				if (m.isDefault()) continue;
				if (Modifier.isStatic(m.getModifiers())) continue;
				
				throw new IllegalArgumentException(proxyInterface + " does not extend ProxyInterface");
			}
		}
		
		validatedClasses.put(proxyInterface, new ProxyTemplate(proxyInterface));
		
		validatingClasses.remove(proxyInterface);
		
		return validatedClasses.get(proxyInterface);
	}
}
