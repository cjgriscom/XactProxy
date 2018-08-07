package com.xactmetal.abstraction.proxy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use to label setter arguments if the compiler's -parameters flag
 *   is not enabled
 * @author cjgriscom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SetterArguments {
	public String[] names();
}
