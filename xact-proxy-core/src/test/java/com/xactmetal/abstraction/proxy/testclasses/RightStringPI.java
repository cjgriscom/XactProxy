package com.xactmetal.abstraction.proxy.testclasses;

import com.xactmetal.abstraction.proxy.ProxyInterface;

public interface RightStringPI extends ProxyInterface {
	public String rightValue();
	
	public default String rightValueLowercase() {return rightValue().toLowerCase();}
	public default int length() {return rightValue().length();}

	public void setRightValue(String rightValue);
	public RightStringPI setRightValueChained(String rightValue);
}
