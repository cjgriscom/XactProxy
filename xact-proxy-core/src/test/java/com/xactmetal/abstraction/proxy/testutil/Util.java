package com.xactmetal.abstraction.proxy.testutil;

public class Util {
	
	public static int[] genFilled5Array() {
		int[] array0 = new int[5];
		for (int x = 0; x < 5; x++) array0[x] = x;
		return array0;
	}
	
	public static int[][] genFilled5x5Array() {
		int[][] array0 = new int[5][5];
		for (int x = 0; x < 5; x++) for (int y = 0; y < 5; y++) array0[x][y] = x * 5 + y;
		return array0;
	}
	
	public static Integer[] genFilled5WrapperArray() {
		Integer[] array0 = new Integer[5];
		for (int x = 0; x < 5; x++) array0[x] = x;
		return array0;
	}
}
