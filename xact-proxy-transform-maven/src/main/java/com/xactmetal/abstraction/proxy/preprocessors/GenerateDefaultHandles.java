package com.xactmetal.abstraction.proxy.preprocessors;

import static com.xactmetal.abstraction.proxy.preprocessors.Util.isProxyInterface;
import static com.xactmetal.abstraction.proxy.preprocessors.Util.log;

import java.lang.reflect.Modifier;
import java.util.LinkedList;

import javassist.ClassMap;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class GenerateDefaultHandles extends ClassTransformer {
	
	private static final String LOG_H = "[GenerateDefaultHandles] ";

	@Override
	public void applyTransformations(ClassPool classPool, CtClass classToTransform) {
		// Verify that it's a ProxyInterface
		if (
				classToTransform.isInterface() && 
				isProxyInterface(LOG_H, classToTransform)
				) { 

			LinkedList<CtMethod> instanceMethods = new LinkedList<>();
			LinkedList<CtMethod> defaultMethods = new LinkedList<>();
			for (CtMethod m : classToTransform.getDeclaredMethods()) {
				if (m.getMethodInfo2().isMethod()) {
					int modifiers = m.getModifiers();
					if (!Modifier.isAbstract(modifiers) && !Modifier.isStatic(modifiers)) {
						defaultMethods.add(m);
					} else if (!Modifier.isStatic(modifiers)) {
						instanceMethods.add(m);
					}
				}
			}
			
			if (!defaultMethods.isEmpty()) {
				for (CtMethod m : defaultMethods) {
					log(LOG_H, "Creating static handle for default " + classToTransform.getName() + " : " + m.getName());
					try {
						CtClass[] parms = new CtClass[m.getParameterTypes().length + 1];
						parms[0] = classToTransform;
						System.arraycopy(m.getParameterTypes(), 0, parms, 1, m.getParameterTypes().length);
						CtMethod stm = new CtMethod(m.getReturnType(), "_default_" + m.getName(), parms, classToTransform);
						stm.setModifiers(Modifier.STATIC + Modifier.PUBLIC);
						stm.setBody(m, new ClassMap());
						
						classToTransform.addMethod(stm);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
			
		}
	}

}
