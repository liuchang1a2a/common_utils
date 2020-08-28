package com.utils;

import java.util.Arrays;

public class ArrayUtil {
	
	
	public static String join(Object[] objs,String joint){
		String str = Arrays.toString(objs);
		return str.substring(1,str.length()-1).replace(",", joint).replace(" ", "");
	}
	
	
	
}
