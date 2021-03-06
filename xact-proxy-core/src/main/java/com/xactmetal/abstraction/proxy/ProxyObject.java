package com.xactmetal.abstraction.proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
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
	
	public static <T extends ProxyInterface> Comparator<String> getKeyOrderComparator(Class<T> proxyInterface) {
		ProxyTemplate template = ProxyInterfaceCache.validateProxyInterface_Lock(proxyInterface);
		return new Comparator<String>() {
			HashMap<String, Integer> order = new HashMap<>(); {
				Collection<String> src;
				if (template.ordered) src = template.orderedDatatypeKeys;
				else src = template.datatypes.keySet();
				int i = 0;
				for (String key : src) order.put(key, i++);
			}
			
			@Override
			public int compare(String o1, String o2) {
				return Integer.compare(order.get(o1), order.get(o2));
			}
		};
		
	}

	private transient ProxyTemplate template = null;
	
	final Class<? extends ProxyInterface> proxyInterface;
	
	private final TreeMap<String, Object> fields = new TreeMap<>();
		
	ProxyObject(Class<? extends ProxyInterface> proxyInterface, ProxyTemplate template) {
		this.proxyInterface = proxyInterface;
		this.template = template;
		
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
	
	ProxyTemplate getTemplate() {
		if (template == null) {
			template = ProxyInterfaceCache.validateProxyInterface_Lock(proxyInterface);
		}
		return template;
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
			Method staticHandle = getTemplate().defaultStaticHandles.get(m);
			if (staticHandle != null) {
				Object[] staticArgs = new Object[args==null?1:args.length + 1];
				staticArgs[staticArgs.length - 1] = proxy;
				if (args != null) System.arraycopy(args, 0, staticArgs, 1, args==null?0:args.length);
				return staticHandle.invoke(null, staticArgs);
			} else {
				return getTemplate().defaults.get(m)
					.bindTo(proxy)
					.invokeWithArguments(args);
			}
		} else if ((setter = getTemplate().setters.get(m)) != null && setter.size() == args.length) {
			for (int i = 0; i < args.length; i++) {
				fields.put(setter.get(i), args[i]);
			}
			
			if (m.getReturnType().isAssignableFrom(proxyInterface)) {
				return proxy;
			} else {
				return null;
			}
		} else if (numParams == 0) {
			if (getTemplate().datatypes.containsKey(name)) {
				return fields.get(name);
			} else if (name.equals("hashCode")) {
				int hashCode = getTemplate().titleHashCode; // Begin with name of class
				Iterator<Entry<String, ProxyDatatype>> dti = getTemplate().datatypes.entrySet().iterator();
				
				// Loop through items and add (name.hashCode() ^ obj.hashCode()) for every non-null
				while (dti.hasNext()){
					Entry<String, ProxyDatatype> entry = dti.next();
					Object obj = fields.get(entry.getKey());
					if (obj != null) {
						int nameHashCode = entry.getKey().hashCode();
						int objHashCode = entry.getValue().hashCodeFor(obj);
						hashCode += nameHashCode ^ objHashCode;
					}
				}
				return hashCode;
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

			Iterator<Entry<String, ProxyDatatype>> dti = getTemplate().datatypes.entrySet().iterator();
			
			while (dti.hasNext()){
				Entry<String, ProxyDatatype> entry = dti.next();
				Object thisObj = fields.get(entry.getKey());
				Object otherObj = ((ProxyObject)invok).fields.get(entry.getKey());
				if (thisObj == null) {
					if (otherObj != null) return false;
				} else if (otherObj == null) {
					if (thisObj != null) return false;
				} else {
					if (!entry.getValue().equalsOther(thisObj, otherObj)) return false;
				}
			}
			return true;
		} else if (numParams == 1 && name.equals("convert")) {
			return ProxyDatatype.convertFromProxyInterface((ConversionHandler<?,?>) args[0], this);
		}
		
		throw new UnsupportedOperationException("Unknown method " + name + " in " + this.proxyInterface.getName());
	}
}
