package com.xactmetal.abstraction.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

final class ProxyInterfaceCache {
	private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private static final Lock readLock = readWriteLock.readLock();
	private static final Lock writeLock = readWriteLock.writeLock();
	private static final HashSet<Class<?>> validatingClasses = new HashSet<>();
	private static final HashMap<Class<?>, ProxyTemplate> validatedClasses = new HashMap<>();
	
	static boolean hasCachedProxyInterface(Class<?> proxyInterface) {
		readLock.lock();
		try{
			return validatedClasses.containsKey(proxyInterface);
		} finally {
			readLock.unlock();
		}
	}

	static ProxyTemplate validateProxyInterface_Lock(Class<?> proxyInterface) throws IllegalArgumentException {
		readLock.lock();
		try{
			if (validatedClasses.containsKey(proxyInterface)) return validatedClasses.get(proxyInterface);
		} finally {
			readLock.unlock();
		}
		
		writeLock.lock();
		try{
			insert(proxyInterface);
			return validatedClasses.get(proxyInterface);
		} finally {
			writeLock.unlock();
		}
	}

	static ProxyTemplate validateProxyInterface_NoLock(Class<?> proxyInterface) throws IllegalArgumentException {
		if (hasCachedProxyInterface(proxyInterface)) return validatedClasses.get(proxyInterface);
		insert(proxyInterface);
		return validatedClasses.get(proxyInterface);
		
	}
	
	static void validateProxyInterface_NoLock_CheckForLoop(Class<?> proxyInterface, Class<?> owner) {
		if (validatingClasses.contains(proxyInterface)) {
			throw new IllegalArgumentException("Reference loop for " + proxyInterface + " detected in " + owner);
		}
		validateProxyInterface_NoLock(proxyInterface);
	}
	
	private static boolean extendsProxyInterface(Class<?> interfaceClass) {
		for (Class<?> c : interfaceClass.getInterfaces()) {
			if (c.equals(ProxyInterface.class)) return true;
		}
		return false;
	}
	
	private static void insert(Class<?> proxyInterface) {
		validatingClasses.add(proxyInterface); // Use to detect loops
		
		for (Class<?> c : proxyInterface.getInterfaces()) {
			if (c.equals(ProxyInterface.class)) {
				// 
			} else if (extendsProxyInterface(c)) {
				validateProxyInterface_NoLock(c);
			} else {
				// Special exception for interfaces with only static and default methods
				for (Method m : proxyInterface.getDeclaredMethods()) {
					if (m.isDefault()) continue;
					if (Modifier.isStatic(m.getModifiers())) continue;
					
					throw new IllegalArgumentException(proxyInterface + " does not extend ProxyInterface");
				}
			}
		}
		
		validatedClasses.put(proxyInterface, new ProxyTemplate(proxyInterface));
		
		validatingClasses.remove(proxyInterface);
	}
}
