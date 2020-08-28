package com.template.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;

/**
 *	@jar  fastjson	UserAgentUtils
 */
@Controller
@RequestMapping("/http")
public class httpController {
	
	
	/**
	 * @jar	fastjson	UserAgentUtils
	 * @return	获取请求基本信息
	 */
	@RequestMapping("/reqinfo")
	@ResponseBody
	public String reqinfo(HttpServletRequest req,HttpServletResponse res){
		JSONObject result = new JSONObject();
		JSONObject info = new JSONObject();
		info.put("referer", req.getHeader("referer")); 
		info.put("lang", req.getHeader("Accept-Language"));
		String ip = req.getHeader("X-Real-IP");
	    if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
	    	ip = req.getHeader("X-Forwarded-For");
		    if (!StringUtils.isEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
		        int index = ip.indexOf(','); 
		        if (index != -1) {	// 多次反向代理后会有多个IP值，第一个为真实IP。
		            ip= ip.substring(0, index);
		        }
		    } else {
		        ip= req.getRemoteAddr();
		    }	
	    }
	    info.put("ip", ip);
	    try{
	    	InetAddress inet = InetAddress.getByName(ip);
	    	info.put("host", inet.getHostName());
		    String ua = req.getHeader("User-Agent");
		    UserAgent userAgent = UserAgent.parseUserAgentString(ua); 
		    Browser browser = userAgent.getBrowser();  
		    OperatingSystem os = userAgent.getOperatingSystem();
			info.put("mac", getMac(ip));
			info.put("OS", os.getName());
			info.put("Browser", browser.getName());			
		    result.put("code", "0");
		    result.put("msg", "OK");
			result.put("info", info);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }    
		return result.toString();
	}
	
	
	
	
	/**
     * @param ipAddress 127.0.0.1
     * @return	根据IP地址获取mac地址
     */
	private static String getMac(String ipAddress) {
	    String str = "";
	    String macAddress = "";
	    final String LOOPBACK_ADDRESS = "127.0.0.1";
	    try{
		    if (LOOPBACK_ADDRESS.equals(ipAddress)) {
		        InetAddress inetAddress = InetAddress.getLocalHost();
		        byte[] mac = NetworkInterface.getByInetAddress(inetAddress)
		            .getHardwareAddress();
		        // 下面代码是把mac地址拼装成String
		        StringBuilder sb = new StringBuilder();
		        for (int i = 0; i < mac.length; i++) {
		            if (i != 0) {
		                sb.append("-");
		            }
		            // mac[i] & 0xFF 是为了把byte转化为正整数
		            String s = Integer.toHexString(mac[i] & 0xFF);
		            sb.append(s.length() == 1 ? 0 + s : s);
		        }
		        // 把字符串所有小写字母改为大写成为正规的mac地址并返回
		        macAddress = sb.toString().trim().toUpperCase();
		    } else {
		    	// 获取非本地IP的MAC地址
		    	System.out.println(ipAddress);
		        Process p = Runtime.getRuntime().exec("nbtstat -A " + ipAddress);
		        InputStreamReader ir = new InputStreamReader(p.getInputStream());
		        BufferedReader br = new BufferedReader(ir);
		        while ((str = br.readLine()) != null) {
		          if(str.indexOf("MAC")>1){
		              macAddress = str.substring(str.indexOf("MAC")+9, str.length());
		              macAddress = macAddress.trim();
		              break;
		           }
		        }
		        p.destroy();
		        br.close();
		        ir.close();		      
		   }
	    }catch(Exception e){
		   e.printStackTrace();
	    }
	    return macAddress;
	}

	
	
	
	
}
