// Copyright (C) 2009-2023 Lemoine Automation Technologies
//
// SPDX-License-Identifier: EPL-2.0

package com.lemoinetechnologies.pulse.reporting.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;

public class Test {

	public static boolean isInt(String value) {
		Double d = Double.parseDouble(value);
		if(d == d.intValue()) {
			return true;
		}else {
			return false;
		}
	}
	public static Map<String,String> getURLParameters(String queryString) {
		String[] array = queryString.split("&");
		Map<String,String> map = new TreeMap<String, String>();

		for(int i = 0; i < array.length; i++){
			String[] tab = array[i].split("=");
			if(tab.length > 1){
				map.put(tab[0], tab[1]); // Warning ! tab[1] is encoded
			}
			else {
				map.put(tab[0], null);
			}			
		}
		return map;
	}
	
	public static String getStackTraceAsString(Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.print(" [ ");
		pw.print(throwable.getClass().getName());
		pw.println(" ] ");
		pw.println("");
		pw.print(throwable.getMessage());
		pw.println("");
		throwable.printStackTrace(pw);
		pw.println("");
		pw.println("");
		return sw.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int a = 20;
		int b = 0;
		
		try{
			System.out.println(a/b);
		}
		catch(ArithmeticException e){
			System.out.println("getName = "+e.getClass().getName());
			System.out.println("getSimpleName = "+e.getClass().getSimpleName());
			System.out.println("getCanonicalName = "+e.getClass().getCanonicalName());
			System.out.println("toString = "+e.getClass().toString());
			System.out.println(getStackTraceAsString(e));
		}
		
	}

}
