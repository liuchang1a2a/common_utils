package com.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class CmdUtil {
	
	
	/**
	 * @param command	命令行(空格拆分单位元素)
	 * @return	执行命令行
	 */
	public static boolean execute(List<String> command){
		boolean done = false;
		ProcessBuilder builder = new ProcessBuilder();
		try{
			builder.command(command);
			builder.redirectErrorStream(true);
			Process proc = builder.start();
			consumeInputStream(proc.getInputStream());
			consumeInputStream(proc.getErrorStream());
			int exit = proc.waitFor();
			if(exit==0){
				done = true;
			}				
		}catch(Exception e){
			e.printStackTrace();
		}
		return done;
	}
	
	private static String consumeInputStream(InputStream is){
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    String s ;
	    StringBuilder sb = new StringBuilder();
	    try{
		    while((s=br.readLine())!=null){
		        System.out.println(s);
		        sb.append(s);
		    }
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    return sb.toString();
	}
	
	
	
	
	
	
}
