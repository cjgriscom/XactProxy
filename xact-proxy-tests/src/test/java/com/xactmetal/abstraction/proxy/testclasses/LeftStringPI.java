package com.xactmetal.abstraction.proxy.testclasses;

import com.xactmetal.abstraction.proxy.ProxyInterface;

public interface LeftStringPI extends ProxyInterface {
	public String leftValue();
	
	public default String leftValueLowercase() {return leftValue().toLowerCase();}
	public default int length() {return leftValue().length();}

	public void setLeftValue(String leftValue);
	public LeftStringPI setLeftValueChained(String leftValue);
}
