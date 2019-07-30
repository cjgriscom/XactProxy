package com.xactmetal.abstraction.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import com.xactmetal.abstraction.proxy.testclasses.LeftRightStringPI;

public class InheritanceTest {
	Logger logger = Logger.getLogger("InheritanceTest");
	
	@Test
	void testJointInheritance() {
		LeftRightStringPI lrs = ProxyInterface.newInstance(LeftRightStringPI.class);
		
		// Test inherited (default) non-chained setter
		lrs.setLeftCenterRightValues("T", "O", "E");
		assertEquals("TOE", lrs.concatAllValues());
		assertEquals("THOSE", lrs.joinAllValues('H', 'S')); // Test an overloaded default method
		
		// Test inherited (default) chained setter
		assertTrue(lrs.setLeftCenterRightValuesChained("BR", "A", "INS") == lrs);
		assertEquals("BRAINS", lrs.concatAllValues());
		assertEquals(1, lrs.length()); // Test overridden default method
		assertEquals(6, lrs.concatLength()); // Test calls to super default methods
		assertEquals("213", lrs.concatLengthString()); // Test calls to super default methods
		
		// Test individual chained setters
		assertTrue(lrs.setLeftValueChained("R") == lrs);
		assertTrue(lrs.setCenterValueChained("I") == lrs);
		assertTrue(lrs.setRightValueChained("O") == lrs);
		// Test inherited default methods
		assertEquals("rio", lrs.leftValueLowercase() + lrs.centerValueLowercase() + lrs.rightValueLowercase());
		assertEquals("rio", lrs.concatAllLowercaseValues()); // Test default method that calls inherited defaults
		assertEquals("RHINO", lrs.joinAllValues("H", "N")); // Test other overloaded default method
	}
	
}
