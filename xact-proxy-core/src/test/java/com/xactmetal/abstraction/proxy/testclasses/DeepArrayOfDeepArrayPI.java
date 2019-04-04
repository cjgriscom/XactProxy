package com.xactmetal.abstraction.proxy.testclasses;

import com.xactmetal.abstraction.proxy.ProxyInterface;

public interface DeepArrayOfDeepArrayPI extends ProxyInterface {
	
	DeepArrayOfDeepArrayPI setDeepArray(DeepArrayPI[][] deepArray);
	DeepArrayPI[][] deepArray();
	
}
