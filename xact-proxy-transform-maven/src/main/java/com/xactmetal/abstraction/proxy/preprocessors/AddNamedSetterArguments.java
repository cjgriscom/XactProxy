package com.xactmetal.abstraction.proxy.preprocessors;

import static com.xactmetal.abstraction.proxy.preprocessors.Util.*;

import com.xactmetal.abstraction.proxy.annotations.SetterArguments;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodParametersAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class AddNamedSetterArguments extends ClassTransformer {
	
	private static final String LOG_H = "[AddNamedSetterArguments] ";
	
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
			log(LOG_H, classToTransform, m, "  Adding " + variableName);
			nameArrayValues[i] = new StringMemberValue(variableName, cp);
		}
		nameArray.setValue(nameArrayValues);
		annot.addMemberValue("names", nameArray);
		attr.addAnnotation(annot);
		m.getMethodInfo().addAttribute(attr);

		return true;
	}
	
	@Override
	public void applyTransformations(ClassPool classPool, CtClass classToTransform) {
		// Verify that it's a ProxyInterface and not read only
		if (
				classToTransform.isInterface() && 
				isProxyInterface(LOG_H, classToTransform) && 
				!isReadOnly(LOG_H, classToTransform)
				) { 
			
			for (CtMethod m : classToTransform.getMethods()) {
				if (	m.getDeclaringClass().equals(classToTransform) && // Declared in proxy itself
						isProxySetterMethod(LOG_H, classToTransform, m) // Has all correct attributes of a setter
						) {
					
					if (hasSetterArgumentsAnnotation(classToTransform, m)) {
						// Annotation already exists
						log(LOG_H, classToTransform, m, "Skipped");
					} else {
						// Adding annotation
						if (addSetterArgumentsAnnotation(classToTransform, m)) {
							log(LOG_H, classToTransform, m, "Added SetterArguments annotations");
						}
					}
					
					
				}
			}
		}
	}
}
