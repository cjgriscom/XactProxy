package com.xactmetal.abstraction.proxy.testclasses;

import com.xactmetal.abstraction.proxy.ProxyInterface;

public interface DatatypesPI extends ProxyInterface {
	
	DatatypesPI setBooleanData(boolean booleanData);
	DatatypesPI setWBooleanData(Boolean wBooleanData);
	
	boolean booleanData();
	Boolean wBooleanData();
	
	DatatypesPI setByteData(byte byteData);
	DatatypesPI setWByteData(Byte wByteData);
	
	byte byteData();
	Byte wByteData();
	
	DatatypesPI setShortData(short shortData);
	DatatypesPI setWShortData(Short wShortData);

	short shortData();
	Short wShortData();

	DatatypesPI setIntData(int intData);
	DatatypesPI setWIntegerData(Integer wIntegerData);

	int intData();
	Integer wIntegerData();

	DatatypesPI setLongData(long longData);
	DatatypesPI setWLongData(Long wLongData);

	long longData();
	Long wLongData();

	DatatypesPI setFloatData(float floatData);
	DatatypesPI setWFloatData(Float wFloatData);

	float floatData();
	Float wFloatData();

	DatatypesPI setDoubleData(double doubleData);
	DatatypesPI setWDoubleData(Double wDoubleData);

	double doubleData();
	Double wDoubleData();
	
	DatatypesPI setStringData(String stringData);
	
	String stringData();
	
	DatatypesPI setIntArray(int[] intArray);
	int[] intArray();
	
	DatatypesPI setWIntArray(Integer[] wIntArray);
	Integer[] wIntArray();
	
	public static void main(String[] args) {
		
		String[] classesP = new String[]{"Boolean", "Byte", "Short", "Integer", "Long", "Float", "Double", "String"};
		for (String c : classesP) {
			System.out.println("\tDatatypesPI set"+c+"Data("+c.toLowerCase()+" "+c.toLowerCase()+"Data);");
			System.out.println("\tDatatypesPI setW"+c+"Data("+c+" w"+c+"Data);");
			System.out.println();
			System.out.println("\t"+c.toLowerCase()+" "+c.toLowerCase()+"Data();");
			System.out.println("\t"+c+" w"+c+"Data();");
			System.out.println();
		}
		
		for (String c : classesP) {
			System.out.println("\t\tdatatypePI0.set"+c+"Data(true); datatypePI1.set"+c+"Data(true);");
			System.out.println("\t\tdatatypePI0.setW"+c+"Data(true); datatypePI1.setW"+c+"Data(true);");
		}
	}
}
