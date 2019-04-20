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
		assertTrue(lrs.setLeftCenterRightValuesChained("A", "C", "E") == lrs);
		assertEquals("ACE", lrs.concatAllValues());
		lrs.setLeftCenterRightValues("R", "I", "O");
		assertEquals("RHINO", lrs.joinAllValues("H", "N"));
		assertEquals("RHINO", lrs.joinAllValues('H', 'N'));
	}
	
}
