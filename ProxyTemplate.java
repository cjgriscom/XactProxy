package com.xactmetal.abstraction.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import com.xactmetal.abstraction.proxy.ProxyDatatype.Base;

final class ProxyTemplate {
	
	static final Constructor<MethodHandles.Lookup> lookupPrivConst;
	static {
		try {
			lookupPrivConst = 
					Lookup.class.getDeclaredConstructor(Class.class, int.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
		if (!lookupPrivConst.isAccessible()) {
			lookupPrivConst.setAccessible(true);
		}
	}
	
	private static final TreeSet<String> reservedWords = new TreeSet<>();
	static {
		reservedWords.add("convert");
		reservedWords.add("hashCode");
		reservedWords.add("equals");
		reservedWords.add("toString");
		reservedWords.add("notify");
		reservedWords.add("notifyAll");
		reservedWords.add("wait");
		reservedWords.add("getClass");
	}
	
	// Keep track of any references to other ProxyInterface types
	// Reject cycles
	final HashSet<Class<?>> references = new HashSet<>();
	
	// Maps field names to datatypes
	final TreeMap<String, ProxyDatatype> datatypes = new TreeMap<>();
	// Maps setter method name to the serviced fields
	final TreeMap<String, ArrayList<String>> setters = new TreeMap<>();
	final TreeMap<String, MethodHandle> defaults = new TreeMap<>();
	
	ProxyTemplate(Class<?> proxyInterface) throws IllegalArgumentException {
		
		TreeMap<String, ProxyDatatype> setterFields = new TreeMap<>();
		for (Method meth : proxyInterface.getDeclaredMethods()) {
			if (Modifier.isStatic(meth.getModifiers())) { // Ignore interface static methods
				continue;
			} else if (meth.isDefault()) {
				// Default methods must be invoked with a special handler
				try {
					MethodHandle h = lookupPrivConst.newInstance(proxyInterface, MethodHandles.Lookup.PRIVATE)
						.unreflectSpecial(meth, proxyInterface);
					
					defaults.put(meth.toGenericString(), h);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else if (meth.getReturnType() == void.class || meth.getReturnType() == proxyInterface) {
				// Setter
				if (meth.getParameterCount() == 0) 
					throw new IllegalArgumentException("ProxyInterface setter " + 
							meth.getName() + " has 0 parameters");
				
				ArrayList<String> serviced = new ArrayList<String>();
				
				for (Parameter p : meth.getParameters()) {
					ProxyDatatype type = mapDatatype(p.getType(), proxyInterface);

					ProxyDatatype existing = setterFields.put(p.getName(), type);
					
					if (existing != null && existing != type) {
						throw new IllegalArgumentException("ProxyInterface field " + p.getName() + " has mismatched setter datatypes in " + proxyInterface.getName());
					}
					
					if (reservedWords.contains(p.getName())) throw new IllegalArgumentException(p.getName() + 
							" is a reserved word in " + proxyInterface.getName());
					serviced.add(p.getName());
				}
				setters.put(meth.toGenericString(), serviced);
			} else {
				// Getter
				if (meth.getParameterCount() > 0) 
					throw new IllegalArgumentException("ProxyInterface getter " + 
							meth.getName() + " has > 0 parameters in " + proxyInterface.getName());
				
				ProxyDatatype type = mapDatatype(meth.getReturnType(), proxyInterface);
				
				if (reservedWords.contains(meth.getName())) throw new IllegalArgumentException(meth.getName() + 
						" is a reserved word in " + proxyInterface.getName());
				datatypes.put(meth.getName(), type);
			}
		}
		
		// Check consistency
		TreeSet<String> mergedKeys = new TreeSet<>();
		mergedKeys.addAll(setterFields.keySet());
		mergedKeys.addAll(datatypes.keySet());
		
		for (String field : mergedKeys) {
			if (! setterFields.containsKey(field)) {
				throw new IllegalArgumentException("ProxyInterface field " + field + " has no setter in " + proxyInterface.getName());
			} else if (! datatypes.containsKey(field)) {
				throw new IllegalArgumentException("ProxyInterface field " + field + " has no getter in " + proxyInterface.getName());
			} else if (!setterFields.get(field).equals(datatypes.get(field))) {
				throw new IllegalArgumentException("ProxyInterface field " + field + " has mismatched getter/setter datatypes in " + proxyInterface.getName());
			}
		}
		
		// Add superinterface setters and fields
		for (Class<?> c : proxyInterface.getInterfaces()) {
			if (ProxyInterfaceCache.hasCachedProxyInterface(c)) {
				// TODO is there any redundant work here?
				ProxyTemplate subtemplate = ProxyInterfaceCache.validateProxyInterface(c);
				datatypes.putAll(subtemplate.datatypes);
				setters.putAll(subtemplate.setters);
				references.add(c);
				references.addAll(subtemplate.references);
				// Add defaults without replacing any
				for (Entry<String, MethodHandle> def : subtemplate.defaults.entrySet()) {
					if (!defaults.containsKey(def.getKey())) defaults.put(def.getKey(), def.getValue());
				}
			}
		}
		
		recursiveReferenceLoopCheck(datatypes.values(), proxyInterface);
	}
	
	private void recursiveReferenceLoopCheck(Collection<ProxyDatatype> types, Class<?> owner) {
		for (ProxyDatatype t : types) {
			if (t.base == Base.ProxyInterface) {
				if (references.contains(t.proxyInterface)) {
					throw new IllegalArgumentException("Reference loop for " + t.proxyInterface + " detected in " + owner);
				} else {
					recursiveReferenceLoopCheck(
							ProxyInterfaceCache.validateProxyInterface(t.proxyInterface)
							.datatypes.values(), owner);
				}
			}
		}
		
	}
	
	// Recursive type unwrap for arrays
	private static int getArrayDimensions(Class<?> param, int dims) {
		if (param.isArray()) return getArrayDimensions(param.getComponentType(), dims+1);
		else return dims;
	}
	
	private static Class<?> getArrayBaseType(Class<?> param) {
		if (param.isArray()) return getArrayBaseType(param.getComponentType());
		else return param;
	}
	
	private ProxyDatatype mapDatatype(Class<?> param, Class<?> owner) throws IllegalArgumentException {
		int dims = getArrayDimensions(param,0);
		param = getArrayBaseType(param);
		
		if (param == boolean.class) {
			if (dims == 0) return new ProxyDatatype(Base.Boolean, false);
			else return new ProxyDatatype(Base.Boolean, dims, true);
		} else if (param == byte.class) {
			if (dims == 0) return new ProxyDatatype(Base.Byte, (byte) 0);
			else return new ProxyDatatype(Base.Byte, dims, true);
		} else if (param == short.class) {
			if (dims == 0) return new ProxyDatatype(Base.Short, (short) 0);
			else return new ProxyDatatype(Base.Short, dims, true);
		} else if (param == int.class) {
			if (dims == 0) return new ProxyDatatype(Base.Integer, 0);
			else return new ProxyDatatype(Base.Integer, dims, true);
		} else if (param == long.class) {
			if (dims == 0) return new ProxyDatatype(Base.Long, 0L);
			else return new ProxyDatatype(Base.Long, dims, true);
		} else if (param == float.class) {
			if (dims == 0) return new ProxyDatatype(Base.Float, 0f);
			else return new ProxyDatatype(Base.Float, dims, true);
		} else if (param == double.class) {
			if (dims == 0) return new ProxyDatatype(Base.Double, 0.);
			else return new ProxyDatatype(Base.Double, dims, true);
			
		} else if (param == Boolean.class) {
			return new ProxyDatatype(Base.Boolean, dims, false);
		} else if (param == Byte.class) {
			return new ProxyDatatype(Base.Byte, dims, false);
		} else if (param == Short.class) {
			return new ProxyDatatype(Base.Short, dims, false);
		} else if (param == Integer.class) {
			return new ProxyDatatype(Base.Integer, dims, false);
		} else if (param == Long.class) {
			return new ProxyDatatype(Base.Long, dims, false);
		} else if (param == Float.class) {
			return new ProxyDatatype(Base.Float, dims, false);
		} else if (param == Double.class) {
			return new ProxyDatatype(Base.Double, dims, false);
			
		} else if (param == String.class) {
			return new ProxyDatatype(Base.String, dims, false);
		} else if (param == owner) {
			throw new IllegalArgumentException("Recursive storage not supported in " + owner.getName());
		} else {
			for (Class<?> i : param.getInterfaces()) {
				if (i == ProxyInterface.class) {
					if (ProxyInterfaceCache.validationInProgress(param)) {
						throw new IllegalArgumentException("Reference loop for " + param + " detected in " + owner);
					}
					ProxyInterfaceCache.validateProxyInterface(param);
					return new ProxyDatatype(Base.ProxyInterface, dims, param);
				}
			}
		}
		throw new IllegalArgumentException("Unsupported datatype " + param.getName() + 
				" in " + owner.getName());
	}
	
}
