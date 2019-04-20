package com.xactmetal.abstraction.proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Consumer;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import com.xactmetal.abstraction.proxy.testclasses.DatatypesPI;
import com.xactmetal.abstraction.proxy.testclasses.DeepArrayOfDeepArrayPI;
import com.xactmetal.abstraction.proxy.testclasses.DeepArrayPI;
import com.xactmetal.abstraction.proxy.testutil.Util;

public class DatatypesTest {
	Logger logger = Logger.getLogger("DatatypesTest");
	
	//@BeforeEach
	//void beforeEachTest() {
	//	logger.info("Before Each Test");
	//}
	
	//@RepeatedTest(2)
	
	@Test
	void test_ProxyInterface_classOf() {
		DatatypesPI testPI = ProxyInterface.newInstance(DatatypesPI.class);
		assertEquals(ProxyInterface.classOf(testPI), DatatypesPI.class);
	}
	
	@Test
	void testDatatypes() {
		Consumer<DatatypesPI> fillDatatypePI = (datatypePI) -> {
			datatypePI.setBooleanData(true);
			datatypePI.setWBooleanData(true);
			datatypePI.setByteData((byte)12);
			datatypePI.setWByteData((byte)13);
			datatypePI.setShortData((short)14);
			datatypePI.setWShortData((short)15);
			datatypePI.setIntData(16);
			datatypePI.setWIntegerData(17);
			datatypePI.setLongData(18L);
			datatypePI.setWLongData(19L);
			datatypePI.setFloatData(0.6f);
			datatypePI.setWFloatData(0.7f);
			datatypePI.setDoubleData(0.8);
			datatypePI.setWDoubleData(0.9);
			datatypePI.setStringData("ten".toUpperCase());
		};
		
		DatatypesPI datatypePI0 = ProxyInterface.newInstance(DatatypesPI.class);
		DatatypesPI datatypePI1 = ProxyInterface.newInstance(DatatypesPI.class);
		
		// Check arrays are equal
		assertTrue(datatypePI0.equals(datatypePI1));
		
		// Test default values
		assertTrue(datatypePI0.booleanData() == false);
		assertTrue(datatypePI0.wBooleanData() == null);
		assertTrue(datatypePI0.byteData() == (byte)0);
		assertTrue(datatypePI0.wByteData() == null);
		assertTrue(datatypePI0.shortData() == (short)0);
		assertTrue(datatypePI0.wShortData() == null);
		assertTrue(datatypePI0.intData() == 0);
		assertTrue(datatypePI0.wIntegerData() == null);
		assertTrue(datatypePI0.longData() == 0L);
		assertTrue(datatypePI0.wLongData() == null);
		assertTrue(datatypePI0.floatData() == 0f);
		assertTrue(datatypePI0.wFloatData() == null);
		assertTrue(datatypePI0.doubleData() == 0.);
		assertTrue(datatypePI0.wDoubleData() == null);
		assertTrue(datatypePI0.stringData() == null);
		
		// Check self-equality
		datatypePI0.equals(datatypePI0);
		
		// Fill with values
		fillDatatypePI.accept(datatypePI0);
		fillDatatypePI.accept(datatypePI1);

		// Check self-equality
		datatypePI1.equals(datatypePI1);
		
		for (DatatypesPI datatypePI : new DatatypesPI[]{datatypePI0, datatypePI1}) {
			assertTrue(datatypePI.booleanData() == true);
			assertTrue(datatypePI.wBooleanData().equals(true));
			assertTrue(datatypePI.byteData() == (byte)12);
			assertTrue(datatypePI.wByteData().equals(new Byte((byte)13)));
			assertTrue(datatypePI.shortData() == (short)14);
			assertTrue(datatypePI.wShortData().equals(new Short((short)15)));
			assertTrue(datatypePI.intData() == 16);
			assertTrue(datatypePI.wIntegerData().equals(new Integer(17)));
			assertTrue(datatypePI.longData() == 18L);
			assertTrue(datatypePI.wLongData().equals(new Long(19L)));
			assertTrue(datatypePI.floatData() == 0.6f);
			assertTrue(datatypePI.wFloatData().equals(new Float(0.7f)));
			assertTrue(datatypePI.doubleData() == 0.8);
			assertTrue(datatypePI.wDoubleData().equals(new Double(0.9)));
			assertTrue(datatypePI.stringData().equals("TEN"));
		}
		
		// Check arrays are equal
		assertTrue(datatypePI0.equals(datatypePI1));

		// Change a value at a time, check inequality
		fillDatatypePI.accept(datatypePI1); datatypePI1.setBooleanData(false);   assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setWBooleanData(null);   assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setByteData((byte)-1);   assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setWByteData(null);      assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setShortData((short)-1); assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setWShortData(null);     assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setIntData(-1);          assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setWIntegerData(null);   assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setLongData(-1L);        assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setWLongData(null);      assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setFloatData(-1f);       assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setWFloatData(null);     assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setDoubleData(-1.);      assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setWDoubleData(null);    assertTrue(!datatypePI0.equals(datatypePI1));
		fillDatatypePI.accept(datatypePI1); datatypePI1.setStringData(null);     assertTrue(!datatypePI0.equals(datatypePI1));
	}
	
	@Test
	void testPrimitiveArrayEquality() {
		int[] array0 = Util.genFilled5Array();
		int[] array1 = Util.genFilled5Array();
		
		DatatypesPI arrayPI0 = ProxyInterface.newInstance(DatatypesPI.class).setIntArray(array0);
		DatatypesPI arrayPI1 = ProxyInterface.newInstance(DatatypesPI.class).setIntArray(array1);
		
		// Check arrays are equal
		assertTrue(arrayPI0.equals(arrayPI1));
		
		// Change a value
		arrayPI1.intArray()[0] = 0x1230123;
		
		// Check arrays are not equal
		assertTrue(!arrayPI0.equals(arrayPI1));
	}
	
	@Test
	void testWrappedPrimitiveArrayEquality() {
		Integer[] array0 = Util.genFilled5WrapperArray();
		Integer[] array1 = Util.genFilled5WrapperArray();
		
		DatatypesPI arrayPI0 = ProxyInterface.newInstance(DatatypesPI.class).setWIntArray(array0);
		DatatypesPI arrayPI1 = ProxyInterface.newInstance(DatatypesPI.class).setWIntArray(array1);
		
		// Check arrays are equal
		assertTrue(arrayPI0.equals(arrayPI1));
		
		// Change a value
		arrayPI1.wIntArray()[0] = 0x1230123;
		
		// Check arrays are not equal
		assertTrue(!arrayPI0.equals(arrayPI1));
		
		// Change a value to null
		arrayPI1.wIntArray()[0] = null;
		
		// Check arrays are not equal
		assertTrue(!arrayPI0.equals(arrayPI1));
		
		// Change both values to null
		arrayPI0.wIntArray()[0] = null;
		
		// Check arrays are equal
		assertTrue(arrayPI0.equals(arrayPI1));
		
		// Change other value to nonnull
		arrayPI1.wIntArray()[0] = 0x1230123;
		
		// Check arrays are not equal
		assertTrue(!arrayPI0.equals(arrayPI1));
	}
	
	@Test
	void testDeepArrayEquality() {
		int[][] array0 = Util.genFilled5x5Array();
		int[][] array1 = Util.genFilled5x5Array();
		
		DeepArrayPI deepArrayPI0 = ProxyInterface.newInstance(DeepArrayPI.class).setDeepArray(array0);
		DeepArrayPI deepArrayPI1 = ProxyInterface.newInstance(DeepArrayPI.class).setDeepArray(array1);
		
		// Check arrays are equal
		assertTrue(deepArrayPI0.equals(deepArrayPI1));
		
		// Change a value
		deepArrayPI1.deepArray()[0][0] = 0x1230123;
		
		// Check arrays are not equal
		assertTrue(!deepArrayPI0.equals(deepArrayPI1));
	}
	
	@Test
	void testDeepArrayOfDeepArrayEquality() {
		int[][] array0 = Util.genFilled5x5Array();
		int[][] array1 = Util.genFilled5x5Array();
		
		DeepArrayPI[][] nest0 = new DeepArrayPI[1][1];
		nest0[0][0] = ProxyInterface.newInstance(DeepArrayPI.class).setDeepArray(array0);
		
		DeepArrayPI[][] nest1 = new DeepArrayPI[1][1];
		nest1[0][0] = ProxyInterface.newInstance(DeepArrayPI.class).setDeepArray(array1);
		
		DeepArrayOfDeepArrayPI deepOfDeep0 = ProxyInterface.newInstance(DeepArrayOfDeepArrayPI.class).setDeepArray(nest0);
		DeepArrayOfDeepArrayPI deepOfDeep1 = ProxyInterface.newInstance(DeepArrayOfDeepArrayPI.class).setDeepArray(nest1);
		
		// Check arrays are equal
		assertTrue(deepOfDeep0.equals(deepOfDeep1));
		
		// Change a value
		array0[0][0] = 0x1230123;
		
		// Check arrays are not equal
		assertTrue(!deepOfDeep0.equals(deepOfDeep1));
	}
}
