package com.template.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigRequest;
import com.aliyuncs.afs.model.v20180112.AuthenticateSigResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.utils.HttpUtil;


@Controller
@RequestMapping("/robot")
@CrossOrigin
public class robotController {

	/**
	 * @keys https://www.google.com/recaptcha/admin/site/352279106/settings
	 * @param token 前端校验结果	表单提交默认参数名  "g-recaptcha-response"
	 * @return	recaptcha2人机校验 (google 防爬虫)
	 */
	@RequestMapping("/recaptcha2")
	@ResponseBody
	public String recaptcha2(HttpServletRequest req,HttpServletResponse res){
		JSONObject jsobject = new JSONObject();
		String type = req.getParameter("type")==null?"explicit":req.getParameter("type");
		String token = req.getParameter("token")==null?"":req.getParameter("token");
		String secretKey = "";	// 站点私钥
		if("explicit".equals(type)){
			//公钥: 6LdCWv8UAAAAADsi6hbAe30Ksm5YIp59k5UFspOv
			secretKey = "6LdCWv8UAAAAAPs5YdH2GE52J47pg17xl71GH_x9";	 // 显式私钥
		}else{
			// 公钥: 6Ld3t_8UAAAAAAZdUxg0UI9rrc3NBeTn1kdefoLP
			secretKey = "6Ld3t_8UAAAAADJ00jXP5ULEurMKwom8N7VA_Rs0";	 // 隐式私钥
		}
		String url = "https://recaptcha.net/recaptcha/api/siteverify";
		JSONObject eles = new JSONObject();
		eles.put("secret", secretKey);
		eles.put("response", token);
		Map<String,String> headers = new HashMap<>();
		headers.put("User-Agent", "Mozilla/5.0");
		headers.put("Accept-Language", "en-US,en;q=0.5");
		String resp = HttpUtil.post(url, HttpUtil.convertJson(eles), headers, null);
		if(!"".equals(resp)){
			try{
				JSONObject result = JSONObject.parseObject(resp);
				if(result.getBoolean("success")){
					jsobject.put("code", "0");
					jsobject.put("msg", "OK");
					jsobject.put("ts", result.getString("challenge_ts"));
				}else{
					jsobject.put("code", "-1");
					jsobject.put("msg", "校验失败");
				}
			}catch(Exception e){
				e.printStackTrace();
				jsobject.put("code", "-1");
				jsobject.put("msg", "校验失败");
			}
		}
		
		return jsobject.toString();
	}
	
	
	
	/**
	 * @param 	token 前端校验结果
	 * @return	recaptcha3人机校验 (google 防爬虫)
	 */
	@RequestMapping("/recaptcha3")
	@ResponseBody
	public String recaptcha3(HttpServletRequest req,HttpServletResponse res){
		JSONObject jsobject = new JSONObject();
		String token = req.getParameter("token")==null?"":req.getParameter("token");
		// 公钥	6Lc-KgAVAAAAAPX5uW0sqjWFRe_Oj83xJI6u93Sn
		String secretKey = "6Lc-KgAVAAAAAHiuSg7K2T7QJLDzz7a7wkyJ_zLm";  // 私钥
		String url = "https://recaptcha.net/recaptcha/api/siteverify";
		JSONObject eles = new JSONObject();
		eles.put("secret", secretKey);
		eles.put("response", token);
		Map<String,String> headers = new HashMap<>();
		headers.put("User-Agent", "Mozilla/5.0");
		headers.put("Accept-Language", "en-US,en;q=0.5");
		String resp = HttpUtil.post(url, HttpUtil.convertJson(eles), headers, null);
		if(!"".equals(resp)){
			try{
				JSONObject result = JSONObject.parseObject(resp);
				if(result.getBoolean("success")){
					jsobject.put("code", "0");
					jsobject.put("msg", "OK");
					jsobject.put("ts", result.getString("challenge_ts"));
				}else{
					jsobject.put("code", "-1");
					jsobject.put("msg", "校验失败");
				}
			}catch(Exception e){
				e.printStackTrace();
				jsobject.put("code", "-1");
				jsobject.put("msg", "校验失败");
			}
		}
		
		
		return jsobject.toString();
	}
	
	
	//LTAI4GHwU5sphvy4L2Y2jTta
	private static final String aliKey = "";
	
	//BVj9fZgnPDt8UKSl0a4TbXUZDIKgMP
	private static final String aliSecret = "";
	
	/**
	 * @jar aliyun-java-sdk-afs   aliyun-java-sdk-core
	 * @return	aliyun 人机验证 (付费)	配合前端ali插件
	 */
	@RequestMapping("/aliyun")
	@ResponseBody
	public String aliyun(HttpServletRequest req,HttpServletResponse res){
		String sessionId = req.getParameter("sessionId")==null?"":req.getParameter("sessionId");
		String sig = req.getParameter("sig")==null?"":req.getParameter("sig");
		String token = req.getParameter("token")==null?"":req.getParameter("token");
		String scene = req.getParameter("scene")==null?"":req.getParameter("scene");
		JSONObject result = new JSONObject();
		try{
			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliKey, aliSecret);
			IAcsClient client = new DefaultAcsClient(profile);
	        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "afs", "afs.aliyuncs.com");
	        AuthenticateSigRequest request = new AuthenticateSigRequest();
	        request.setSessionId(sessionId);// 必填参数，从前端获取，不可更改，android和ios只传这个参数即可
	        request.setSig(sig);// 必填参数，从前端获取，不可更改
	        request.setToken(token);// 必填参数，从前端获取，不可更改
	        request.setScene(scene);// 必填参数，从前端获取，不可更改
	        request.setAppKey("");// 必填参数，后端填写 FFFF0N0000000000924C
	        request.setRemoteIp("127.0.0.1");// 必填参数，后端填写        
	        AuthenticateSigResponse response = client.getAcsResponse(request);
            if(response.getCode() == 100) {
                result.put("code", "0");
                result.put("msg", "OK");
            } else {
            	result.put("code", "-1");
            	result.put("msg", "校验失败");
            }
		}catch(Exception e){
			e.printStackTrace();
			result.put("code", "-1");
        	result.put("msg", "校验失败");
		}		
		
        return result.toString();
	}
	
	
	
	public static void main(String[] args) {
		
	}

}
