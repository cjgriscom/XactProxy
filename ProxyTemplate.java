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
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import com.xactmetal.abstraction.proxy.ProxyDatatype.Base;
import com.xactmetal.abstraction.proxy.annotations.Order;
import com.xactmetal.abstraction.proxy.annotations.Ordered;
import com.xactmetal.abstraction.proxy.annotations.ReadOnly;

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
	// Array of ordered keys for use with the @Order annotation
	// It is populated by continually verifying length against #inserted
	// If length != inserted at end of constructor, there is a hole
	// If an element already exists in the list, there's a collision
	final ArrayList<String> orderedDatatypeKeys = new ArrayList<String>();
	// Maps setter method name to the serviced fields
	final TreeMap<String, ArrayList<String>> setters = new TreeMap<>();
	final TreeMap<String, MethodHandle> defaults = new TreeMap<>();
	final boolean empty;
	final boolean readOnly;
	final boolean ordered;
	
	ProxyTemplate(Class<?> proxyInterface) throws IllegalArgumentException {
		boolean tmpEmpty = true; // Start assuming empty
		
		// Check readOnly
		readOnly = proxyInterface.getDeclaredAnnotation(ReadOnly.class) != null;
		// Check ordered
		ordered = proxyInterface.getDeclaredAnnotation(Ordered.class) != null;

		// Traverse declared methods
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
				if (readOnly) {
					throw new IllegalArgumentException("Encountered setter in ReadOnly ProxyInterface " + proxyInterface.getName());
				}
				
				tmpEmpty = false;
				
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
				
				tmpEmpty = false;
				
				ProxyDatatype type = mapDatatype(meth.getReturnType(), proxyInterface);
				
				if (reservedWords.contains(meth.getName())) throw new IllegalArgumentException(meth.getName() + 
						" is a reserved word in " + proxyInterface.getName());
				datatypes.put(meth.getName(), type);
				
				if (ordered) {
					Order order = meth.getDeclaredAnnotation(Order.class);
					if (order != null) {
						int i = order.value();
						while (orderedDatatypeKeys.size() <= i) orderedDatatypeKeys.add(null);
						if (orderedDatatypeKeys.get(i) == null) {
							orderedDatatypeKeys.set(i, meth.getName());
						} else {
							// Order named twice
							throw new IllegalArgumentException("Order collision " + i + " in " + proxyInterface.getName());
						}
					} else {
						throw new IllegalArgumentException("Missing Order annotation for " + meth.getName() + " in " + proxyInterface.getName());
					}
				}
			}
		}
		
		// Check consistency
		TreeSet<String> mergedKeys = new TreeSet<>();
		mergedKeys.addAll(setterFields.keySet());
		mergedKeys.addAll(datatypes.keySet());
		
		for (String field : mergedKeys) {
			if (!readOnly && !setterFields.containsKey(field)) { // Not read only and undefined setter
				throw new IllegalArgumentException("ProxyInterface field " + field + " has no setter in " + proxyInterface.getName());
			} else if (! datatypes.containsKey(field)) {
				throw new IllegalArgumentException("ProxyInterface field " + field + " has no getter in " + proxyInterface.getName());
			} else if (!readOnly && !setterFields.get(field).equals(datatypes.get(field))) {
				throw new IllegalArgumentException("ProxyInterface field " + field + " has mismatched getter/setter datatypes in " + proxyInterface.getName());
			}
		}
		
		if (ordered) {
			if (orderedDatatypeKeys.size() != datatypes.size()) {
				throw new IllegalArgumentException("Gap in Order annotations in " + proxyInterface.getName());
			}
		}
		
		// Set empty status
		empty = tmpEmpty;
		
		
		// Add superinterface setters and fields
		
		//Don't duplicate order entries
		TreeSet<String> includedOrderKeys = new TreeSet<>(); 
		ArrayList<String> parentOrderKeys = new ArrayList<>();
		
		// getInterfaces is ordered
		for (Class<?> c : proxyInterface.getInterfaces()) {
			if (ProxyInterfaceCache.hasCachedProxyInterface(c)) {
				// TODO is there any redundant work here?
				ProxyTemplate subtemplate = ProxyInterfaceCache.validateProxyInterface(c);
				datatypes.putAll(subtemplate.datatypes);
				setters.putAll(subtemplate.setters);
				references.add(c);
				references.addAll(subtemplate.references);
				// Add inherited defaults without replacing any
				for (Entry<String, MethodHandle> def : subtemplate.defaults.entrySet()) {
					if (!defaults.containsKey(def.getKey())) defaults.put(def.getKey(), def.getValue());
				}
				
				// Check readOnly and Ordered inheritance
				checkReadOnlyAndOrderedInheritanceChain(proxyInterface, c, subtemplate);
				
				// Stack ordered properties
				// Propagate even if this is an empty class
				//   so that empty ProxyInterfaces don't break the order chain
				if (empty || ordered) {
					for (String k : subtemplate.orderedDatatypeKeys) {
						if (includedOrderKeys.add(k)) {
							// Didn't already exist
							parentOrderKeys.add(k);
						}
					}
				}
				
			}
		}
		
		if (empty) {
			this.orderedDatatypeKeys.addAll(parentOrderKeys);
		} else if (ordered) {
			Ordered ordered = proxyInterface.getAnnotation(Ordered.class);
			switch (ordered.parentOrder()) {
			case PREFIX:
				this.orderedDatatypeKeys.addAll(0, parentOrderKeys); // Add to front
				break;
			case POSTFIX:
				this.orderedDatatypeKeys.addAll(parentOrderKeys); // Add to end
				break;
			}
		}
		
		recursiveReferenceLoopCheck(datatypes.values(), proxyInterface);
	}
	
	private void checkReadOnlyAndOrderedInheritanceChain(Class<?> thisProxy, Class<?> subProxy, ProxyTemplate subtemplate) {
		if (subtemplate.empty) {
			// Empty proxies aren't required to declare ReadOnly so we must check parents
			for (Class<?> c : subProxy.getInterfaces()) {
				if (ProxyInterfaceCache.hasCachedProxyInterface(c)) {
					ProxyTemplate nextSubtemplate = ProxyInterfaceCache.validateProxyInterface(c);
					checkReadOnlyAndOrderedInheritanceChain(thisProxy, c, nextSubtemplate);
				}
			}
		} else if (subtemplate.readOnly != this.readOnly) {
			throw new IllegalArgumentException("ReadOnly inheritance must remain consistent in " + thisProxy.getName());
		} else if (this.ordered && !subtemplate.ordered) {
			throw new IllegalArgumentException("Parent types must annotate Ordered " + thisProxy.getName());
		}
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
	
	@SuppressWarnings("unchecked")
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
					return new ProxyDatatype(Base.ProxyInterface, dims, (Class<? extends ProxyInterface>) param);
				}
			}
		}
		throw new IllegalArgumentException("Unsupported datatype " + param.getName() + 
				" in " + owner.getName());
	}
	
}
