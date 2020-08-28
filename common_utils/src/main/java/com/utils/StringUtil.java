package com.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @jar	commons-lang3
 */
public class StringUtil {
	
	
	/**
	 * @return 重复多次字符串
	 */
	public static String repeat(String str,int times){
		StringBuilder result = new StringBuilder("");
		if(times>0){
			for(int i=0;i<times;i++){
				result.append(str);
			}			
		}
		return result.toString();
	}
	
	
	
	/**
	 * @return  反转字符串
	 */
	public static String reverse(String str){
		return new StringBuilder(str).reverse().toString();
	}
	
	
	/**
	 * @return 删除字符串中某字符串, 拼接剩余部分
	 */
	public static String cut(String org,String str){
		StringBuilder sb = new StringBuilder("");
		if(!"".equals(str.trim())&&org.indexOf(str)>-1){
			for(String s:org.split(str)){
				sb.append(s);
			}
		}else{
			sb.append(org);
		}
		return sb.toString();
	}
	
	/**
	 * @return	生成新字符串 (避免重名)
	 */
	public static String rename(String str){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String ext = sdf.format(new Date())+new Random().nextInt(100);
		return str+"_"+ext;
	}
	
	/**
	 * @jar commons-lang3
	 * @return	判断一组字符串中是否有字符串为空
	 */
	public static boolean isEmpty(String...strs){
		boolean empty = false;
		for(int i =0;i<strs.length;i++) {
			if(StringUtils.isEmpty(strs[i])) {
				empty = true;
				break;
			}
		}
		return empty;
	}
	
	
	/**
	 * @jar	commons-lang3
	 * @return	判断一组字符串是否都为空
	 */
	public static boolean allEmpty(String...strs) {
		boolean empty = true;
		for(int i =0;i<strs.length;i++) {
			if(!StringUtils.isEmpty(strs[i])) {
				empty = false;
				break;
			}
		}		
		return empty;
	}
	
	
	/**
	 * @jar commons-lang3
	 * @param url 访问地址		params	访问参数
	 * @return	拼接带访问参数的url
	 */
	public static String url(String url,Map<String,String> params){
		List<String> strs = new ArrayList<>();
		for(String key:params.keySet()){
			strs.add(key+"="+params.get(key));
		}
		return url+"?"+StringUtils.join(strs,"&&");
	}
	
	
	/**
	 * @return	判断字符串是否为数字
	 */
	public static boolean isNumeric(String str){
		return str.matches("-?[0-9]+.?[0-9]*");
	}
	
	/**
	 * @return	判断字符串是否为正整数
	 */
	public static boolean isInteger(String str){
		return str.matches("^[0-9]{1,}$");
	}
	
	/**
	 * @return	判断是否为email
	 */
	public static boolean isEmail(String str){
		String reg =  "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		return str.matches(reg);
	}
	
	
	/**
	 * @return	验证URL合法性
	 */
	public static boolean isURL(String url){
		 String regex = "^((https|http|ftp|rtsp|mms)?://)"
	                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?"
	               + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"
	                 + "|"
	                 + "([0-9a-z_!~*'()-]+\\.)*"
	                 + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." 
	                + "[a-z]{2,6})"
	                + "(:[0-9]{1,5})?"
	                + "((/?)|"
	                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$"; 
	    return  url.toLowerCase().matches(regex);	
	}

	/**
	 * @return  排序 整型字符串集合
	 */
	public static List<String> sortIntegers(List<String> nums){
		List<String> list = new ArrayList<>();
		int[] arry = new int[nums.size()];
		for(int i=0;i<nums.size();i++){
			arry[i] = Integer.valueOf(nums.get(i));
		}
		Arrays.sort(arry);
		for(int num:arry){
			list.add(String.valueOf(num));
		}
		return list;
	}
	
	
	/**
	 * @return  排序double型字符串集合
	 */
	public static List<String> sortDoubles(List<String> nums){
		List<String> list = new ArrayList<>();
		double[] arry = new double[nums.size()];
		for(int i=0;i<nums.size();i++){
			arry[i] = Double.valueOf(nums.get(i));
		}
		Arrays.sort(arry);
		for(double num:arry){
			list.add(String.valueOf(num));
		}
		return list;
	}
	
	/**
	 * @return 获取UUID (32位)
	 */
	public static String get32bituuid()
	{
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	
	/**
	 * @return	md5加密 (32位)
	 */
	public static String md5(String plainText){
		byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(plainText.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        //将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);
        // 如果生成数字未满32位，需要前面补0
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
	}
	
	public static String base64Encode(String str){
		return base64Encode(str, "utf-8");
	}
	
	/**
	 * @return	base64 加密
	 */
	public static String base64Encode(String str, String charset){
		String base64= "";
		try{
			base64 = new BASE64Encoder().encode(str.getBytes(charset));
		}catch(Exception e){
			e.printStackTrace();
		}
		return base64;
	}
	
	
	public static String base64Decode(String str){
		return base64Decode(str,"utf-8");
	}
	
	/**
	 * @return	base64解码
	 */
	public static String base64Decode(String str,String charset){
		String org = "";
		try{
			org = new String(new BASE64Decoder().decodeBuffer(str),charset);
		}catch(Exception e){
			e.printStackTrace();
		}
		return org;
	}
	
	/**
	 * @param repeatable=true, num<=62 
	 * @return 获取随机字符串 (0-9,a-z, A-Z)
	 */
	public static String randomTxt(int num,boolean repeatable){
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();
		if(repeatable){
			for(int i=0;i<num;i++){
				int number = rand.nextInt(3);
				int result = 0;
				switch (number) {
					case 0:
						result=(int)(Math.random()*25+65);
						sb.append((char)result);
						break;
					case 1:
						result=(int)(Math.random()*25+97);
						sb.append((char)result);
						break;
					case 2:
						sb.append(String.valueOf(new Random().nextInt(10)));
						break;
				}
				
			}
		}else if(num<=62){
			Set<Character> set = new HashSet<>();
			while(set.size()<num){
				int number = rand.nextInt(3);
				int result = 0;
				switch (number) {
					case 0:
						result=(int)(Math.random()*25+65);
						break;
					case 1:
						result=(int)(Math.random()*25+97);
						break;
					case 2:
						result = (int)(Math.random()*9+48);
						break;
				}
				set.add((char)result);
			}
			for(char ch:set){
				sb.append(ch);
			}			
		}else{
			return null;
		}
		return sb.toString();
	}
	
	
	
	/**
	 * @param str  1,2,A,b,S,o,0
	 * @return	获取随机字符串
	 */
	public static String randomTxt(String str,int num,boolean repeatable){
		StringBuilder sb = new StringBuilder();
		String[] strs = str.split(",");
		Random rand = new Random();
		if(repeatable){
			for(int i=0;i<num;i++){
				int idx = rand.nextInt(strs.length);
				sb.append(strs[idx]);
			}			
		}else{
			Set<String> set = new HashSet<>();
			Set<String> result = new HashSet<>(); 
			for(String s:strs){
				set.add(s);
			}	
			if(num<=set.size()){
				while(result.size()<num){
					int idx = rand.nextInt(set.size());
					result.add((String)set.toArray()[idx]);
				}
				for(String r:result){
					sb.append(r);
				}				
			}else{
				return null;
			}			
		}		
		
		return sb.toString();
	}
	
	
	/**
	 * 
	 * @return 获取随机中文字符串
	 */
	public static String randomCnTxt(int num){
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for(int i=0;i<num;i++){
			int hightPos = (176 + Math.abs(random.nextInt(39)));
	        int lowPos = (161 + Math.abs(random.nextInt(93)));
	        byte[] b = new byte[2];
	        b[0] = (Integer.valueOf(hightPos)).byteValue();
	        b[1] = (Integer.valueOf(lowPos)).byteValue();
	        try {
	        	String str = new String(b, "GBK");
	        	sb.append(str.charAt(0));
	        } catch (Exception e) {
	            e.printStackTrace();
	            sb.append("空");
	        }        
		}
		return sb.toString();
	}
	
	public static String randomCnTxt(int num, boolean repeatable){
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		if(repeatable){
			for(int i=0;i<num;i++){
				int hightPos = (176 + Math.abs(random.nextInt(39)));
		        int lowPos = (161 + Math.abs(random.nextInt(93)));
		        byte[] b = new byte[2];
		        b[0] = (Integer.valueOf(hightPos)).byteValue();
		        b[1] = (Integer.valueOf(lowPos)).byteValue();
		        try {
		        	String str = new String(b, "GBK");
		        	sb.append(str.charAt(0));
		        } catch (Exception e) {
		            e.printStackTrace();
		            sb.append("空");
		        }        
			}
		}else{
			Set<Character> set = new HashSet<>();
			while(set.size()<num){
				int hightPos = (176 + Math.abs(random.nextInt(39)));
		        int lowPos = (161 + Math.abs(random.nextInt(93)));
		        byte[] b = new byte[2];
		        b[0] = (Integer.valueOf(hightPos)).byteValue();
		        b[1] = (Integer.valueOf(lowPos)).byteValue();
		        try {
		        	String str = new String(b, "GBK");
		        	set.add(str.charAt(0));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }      				
			}
			for(char c:set){
				sb.append(c);
			}
			
		}		
		return sb.toString();
	}
	
	
	/**
	 * @jar 	commons-lang3
	 * @return	转义HTML文本
	 */
	public static String escapeHtml(String html){
		return StringEscapeUtils.escapeHtml4(html);
	}
	
	
	
	/**
	 * @jar		commons-lang3
	 * @return	反转义HTML (转为实际符号 	&nbsp;->空格)	 
	 */
	public static String unescapeHtml(String html){
		return StringEscapeUtils.unescapeHtml4(html);
	}
	
	
	
	public static void main(String[] args) {
		
	}

}
