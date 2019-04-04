package com.xactmetal.abstraction.proxy.testclasses;

import com.xactmetal.abstraction.proxy.ProxyInterface;

public interface DeepArrayPI extends ProxyInterface {
	
	DeepArrayPI setDeepArray(int[][] deepArray);
	int[][] deepArray();
	
}
