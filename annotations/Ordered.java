package com.xactmetal.abstraction.proxy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a class for ordered getters.
 * Each getter must be annotated with Order.
 * @author cjgriscom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Ordered {
	public enum ParentOrder {
		PREFIX, POSTFIX
	}
	
	public ParentOrder parentOrder() default ParentOrder.PREFIX;
}
