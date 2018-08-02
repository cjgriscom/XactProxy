package com.xactmetal.abstraction.proxy.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Specifies that the ProxyInterface shall contain no setters, i.e.
 *   is intended for deconversion only. Setters will not be checked.
 * @author cjgriscom
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadOnly {}
