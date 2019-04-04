package com.xactmetal.abstraction.proxy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies a data order for binary conversion streams
 * Ordered getters in a ProxyInterface must each specify an index without any gaps,
 *   or an IllegalArgumentException will be thrown
 * @author cjgriscom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
	public int value();
}
