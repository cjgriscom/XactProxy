package com.xactmetal.abstraction.proxy.preprocessors;

import java.lang.reflect.Modifier;

import com.xactmetal.abstraction.proxy.ProxyInterface;
import com.xactmetal.abstraction.proxy.annotations.ReadOnly;
import com.xactmetal.abstraction.proxy.annotations.SetterArguments;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class Util {

	static boolean isProxyInterface(String head, CtClass classToTransform) {
		boolean isProxyInterface = false;
		try {
			for (CtClass c : classToTransform.getInterfaces()) {
				if (c.getName().equals(ProxyInterface.class.getCanonicalName())) isProxyInterface = true;
			}
		} catch (NotFoundException e) {
			log(head, classToTransform, "NotFoundException");
		}
		
		return isProxyInterface;
	}
	
	static boolean isReadOnly(String head, CtClass classToTransform) {
		return classToTransform.hasAnnotation(ReadOnly.class);
	}
	
	static boolean isProxySetterMethod(String head, CtClass classToTransform, CtMethod m) {
		
		try {
			if (m.getReturnType().equals(CtClass.voidType) || m.getReturnType().equals(classToTransform)) {
				// This is a setter method return type
				
				if (Modifier.isAbstract(m.getModifiers())) {
					// Is an abstract method
					return true;
				}
			}
		} catch (NotFoundException e) {
			log(head, classToTransform, m, "NotFoundException");
		}
		return false;
	}
	
	static boolean hasSetterArgumentsAnnotation(CtClass classToTransform, CtMethod m) {
		return m.hasAnnotation(SetterArguments.class);
	}
	
	static void log(String head, CtClass classToTransform, CtMethod m, String line) {
		log(head, classToTransform.getName() + " - " + m.getName() + ": " + line);
	}
	
	static void log(String head, CtClass classToTransform, String line) {
		log(head, classToTransform.getName() + ": " + line);
	}
	
	static void log(String head, String line) {
		System.out.println(head + line);
	}
}
