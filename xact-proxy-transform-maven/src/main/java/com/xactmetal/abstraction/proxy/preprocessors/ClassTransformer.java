package com.xactmetal.abstraction.proxy.preprocessors;

import javassist.ClassPool;
import javassist.CtClass;

public abstract class ClassTransformer {
	public abstract void applyTransformations(ClassPool classPool, CtClass classToTransform);
}
