package com.xactmetal.abstraction.proxy.testclasses;

import com.xactmetal.abstraction.proxy.ProxyInterface;

public interface LeftStringPI extends ProxyInterface {
	public String leftValue();

	public void setLeftValue(String leftValue);
	public LeftStringPI setLeftValueChained(String leftValue);
}
