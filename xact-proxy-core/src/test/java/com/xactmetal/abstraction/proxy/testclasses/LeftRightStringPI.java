package com.xactmetal.abstraction.proxy.testclasses;

import com.xactmetal.abstraction.proxy.ProxyInterface;

public interface LeftRightStringPI extends LeftStringPI, RightStringPI, ProxyInterface {
	public String centerValue();
	
	public void setCenterValue(String centerValue);
	public LeftRightStringPI setCenterValueChained(String centerValue);

	public default void setLeftCenterRightValues(String leftValue, String centerValue, String rightValue) {
		setLeftValue(leftValue);
		setCenterValue(centerValue);
		setRightValue(rightValue);
	}
	public default LeftRightStringPI setLeftCenterRightValuesChained(String leftValue, String centerValue, String rightValue) {
		return (LeftRightStringPI) (((LeftRightStringPI)setLeftValueChained(leftValue)).
				setCenterValueChained(centerValue).
				setRightValueChained(rightValue));
	}
	
	public default String concatAllValues() {
		return leftValue() + centerValue() + rightValue();
	}
	public default String joinAllValues(String delim1, String delim2) {
		return leftValue() + delim1 + centerValue() + delim2 + rightValue();
	}
	public default String joinAllValues(char delim1, char delim2) {
		return leftValue() + delim1 + centerValue() + delim2 + rightValue();
	}
}
