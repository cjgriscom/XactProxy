package com.xactmetal.abstraction.proxy.preprocessors;

import java.lang.reflect.Modifier;

import com.xactmetal.abstraction.proxy.ProxyInterface;
import com.xactmetal.abstraction.proxy.annotations.ReadOnly;
import com.xactmetal.abstraction.proxy.annotations.SetterArguments;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodParametersAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;
import nl.topicus.plugins.maven.javassist.ClassTransformer;
import nl.topicus.plugins.maven.javassist.TransformationException;

public class AddNamedSetterArguments extends ClassTransformer {
	
	private boolean isProxyInterface(CtClass classToTransform) {
		boolean isProxyInterface = false;
		try {
			for (CtClass c : classToTransform.getInterfaces()) {
				if (c.getName().equals(ProxyInterface.class.getCanonicalName())) isProxyInterface = true;
			}
		} catch (NotFoundException e) {
			log(classToTransform, "NotFoundException");
		}
		
		return isProxyInterface;
	}
	
	private boolean isReadOnly(CtClass classToTransform) {
		return classToTransform.hasAnnotation(ReadOnly.class);
	}
	
	private boolean isProxySetterMethod(CtClass classToTransform, CtMethod m) {
		
		try {
			if (m.getReturnType().equals(CtClass.voidType) || m.getReturnType().equals(classToTransform)) {
				// This is a setter method return type
				
				if (Modifier.isAbstract(m.getModifiers())) {
					// Is an abstract method
					return true;
				}
			}
		} catch (NotFoundException e) {
			log(classToTransform, m, "NotFoundException");
		}
		return false;
	}
	
	private boolean hasSetterArgumentsAnnotation(CtClass classToTransform, CtMethod m) {
		return m.hasAnnotation(SetterArguments.class);
	}
	
	private boolean addSetterArgumentsAnnotation(CtClass classToTransform, CtMethod m) {
		ClassFile classFile = classToTransform.getClassFile();
		ConstPool cp = classFile.getConstPool();
		AnnotationsAttribute attr = new AnnotationsAttribute(cp, AnnotationsAttribute.visibleTag);
		Annotation annot = new Annotation(SetterArguments.class.getName(), cp);
		ArrayMemberValue nameArray = new ArrayMemberValue(new StringMemberValue(cp), cp);
		MethodParametersAttribute nameTable = (MethodParametersAttribute) m.getMethodInfo().getAttribute(javassist.bytecode.MethodParametersAttribute.tag);
		
		MemberValue[] nameArrayValues = new MemberValue[nameTable.size()];
		for (int i = 0; i < nameArrayValues.length; i++) {
			int frameWithNameAtConstantPool = nameTable.name(i);
			String variableName = m.getMethodInfo().getConstPool().getUtf8Info(frameWithNameAtConstantPool);
			log(classToTransform, m, "  Adding " + variableName);
			nameArrayValues[i] = new StringMemberValue(variableName, cp);
		}
		nameArray.setValue(nameArrayValues);
		annot.addMemberValue("names", nameArray);
		attr.addAnnotation(annot);
		m.getMethodInfo().addAttribute(attr);

		return true;
	}
	
	@Override
	public void applyTransformations(ClassPool classPool, CtClass classToTransform) throws TransformationException {
		// Verify that it's a ProxyInterface and not read only
		if (
				classToTransform.isInterface() && 
				isProxyInterface(classToTransform) && 
				!isReadOnly(classToTransform)
				) { 
			
			for (CtMethod m : classToTransform.getMethods()) {
				if (	m.getDeclaringClass().equals(classToTransform) && // Declared in proxy itself
						isProxySetterMethod(classToTransform, m) // Has all correct attributes of a setter
						) {
					
					if (hasSetterArgumentsAnnotation(classToTransform, m)) {
						// Annotation already exists
						log(classToTransform, m, "Skipped");
					} else {
						// Adding annotation
						if (addSetterArgumentsAnnotation(classToTransform, m)) {
							log(classToTransform, m, "Added SetterArguments annotations");
						}
					}
					
					
				}
			}
		}
	}
	
	
	
	private static final String LOG_H = "[AddNamedSetterArguments] ";
	
	private void log(CtClass classToTransform, CtMethod m, String line) {
		log(classToTransform.getName() + " - " + m.getName() + ": " + line);
	}
	
	private void log(CtClass classToTransform, String line) {
		log(classToTransform.getName() + ": " + line);
	}
	
	private void log(String line) {
		System.out.println(LOG_H + line);
	}
	
}
