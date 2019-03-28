package com.xactmetal.abstraction.proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

class ProxyObject implements InvocationHandler, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	static <T extends ProxyInterface> T newInstance(Class<T> proxyInterface) throws IllegalArgumentException {
		// Ensure interface follows rules while caching it
		ProxyTemplate template = ProxyInterfaceCache.validateProxyInterface_Lock(proxyInterface);
		
		return (T) Proxy.newProxyInstance(
				proxyInterface.getClassLoader(), 
				new Class<?>[] {proxyInterface},
				new ProxyObject(proxyInterface, template));
	}
	
	final Class<? extends ProxyInterface> proxyInterface;
	
	private final TreeMap<String, Object> fields = new TreeMap<>();
		
	ProxyObject(Class<? extends ProxyInterface> proxyInterface, ProxyTemplate template) {
		this.proxyInterface = proxyInterface;
		
		// Instantiate primitives
		for (String key : templateKeys()) {
			ProxyDatatype dt = template.datatypes.get(key);
			if (dt.isPrimitive() && dt.dimensions == 0) {
				fields.put(key, dt.defaultValue);
			}
			
		}
	}
	
	// Deserialize
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}
	
	void putObjectFromDatatypeConverterInternal(String name, Object obj) {
		fields.put(name, obj);
	}
	
	Object getObjectFromDatatypeConverterInternal(String name) {
		return fields.get(name);
	}
	
	private transient ProxyTemplate cachedTemplate = null;
	ProxyTemplate getTemplate() {
		if (cachedTemplate == null) {
			cachedTemplate = ProxyInterfaceCache.validateProxyInterface_Lock(proxyInterface);
		}
		return cachedTemplate;
	}
	
	ProxyDatatype getDatatypeFromDatatypeConverterInternal(String name) {
		return getTemplate().datatypes.get(name);
	}
	
	Collection<String> templateKeys() {
		if (getTemplate().ordered) return getTemplate().orderedDatatypeKeys;
		else return getTemplate().datatypes.keySet();
	}
	
	@Override
	public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
		String name = m.getName();
		int numParams = m.getParameterCount();
		
		final ArrayList<String> setter;
		
		if (m.isDefault()) {
			return getTemplate().defaults.get(m.toGenericString())
					.bindTo(proxy)
					.invokeWithArguments(args);
		} else if ((setter = getTemplate().setters.get(m.toGenericString())) != null && setter.size() == args.length) {
			for (int i = 0; i < args.length; i++) {
				fields.put(setter.get(i), args[i]);
			}
			
			if (m.getReturnType() == proxyInterface) {
				return proxy;
			} else {
				return null;
			}
		} else if (numParams == 0) {
			if (getTemplate().datatypes.containsKey(name)) {
				return fields.get(name);
			} else if (name.equals("hashCode")) {
				return this.hashCode();
			} else if (name.equals("toString")) {
				StringBuilder out = new StringBuilder();
				out.append("[");
				out.append(proxyInterface.getSimpleName());
				out.append(" | ");
				boolean first = true;
				
				for (String key : templateKeys()) {
					Object obj = fields.get(key);
					if (!first) out.append(", ");
					first = false;
					out.append(key);
					out.append(": ");
					out.append(obj);
					
				}
				out.append("]");
				
				return out.toString();
			}
		} else if (numParams == 1 && name.equals("equals") && m.getParameterTypes()[0] == Object.class) {
			// Null argument and this is not null
			if (args[0] == null) return false;
			// This is a proxy and other is not a proxy
			if (!Proxy.isProxyClass(args[0].getClass())) return false;
			
			InvocationHandler invok = Proxy.getInvocationHandler(args[0]);
			
			// Other proxy is not part of ProxyInterface
			if (! (invok instanceof ProxyObject)) return false;
			
			Iterator<Object> ti = fields.values().iterator();
			Iterator<Object> oi = ((ProxyObject)invok).fields.values().iterator();
			
			while(ti.hasNext() && oi.hasNext()){
				Object thisObj = ti.next();
				Object otherObj = oi.next();
				if (thisObj == null) {
					if (otherObj != null) return false;
				} else if (otherObj == null) {
					if (thisObj != null) return false;
				} else if (thisObj instanceof Object[] && otherObj instanceof Object[]) {
					return Arrays.deepEquals((Object[])thisObj, (Object[])otherObj);
				} else {
					if (!thisObj.equals(otherObj)) return false;
				}
			}
			return true;
		} else if (numParams == 1 && name.equals("convert")) {
			return ProxyDatatype.convertFromProxyInterface((ConversionHandler<?,?>) args[0], this);
		}
		throw new UnsupportedOperationException();
	}
}
