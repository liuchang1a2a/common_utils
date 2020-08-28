package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

/**
 *	jars: httpclient， fastjson, jsoup
 */
public class HttpUtil {
	
	// get post put
	// html	 获取某标签   video/img...
	
	private static final int SUCCESS_CODE = 200;
	
	
	
	public static String post(String url, List<NameValuePair> elements){
		return post(url, elements,null,null);
	}
	
	
	/**
	 * @param elements 传递参数
	 * @param headers 请求头数据
	 * @param params { charset: 字符集, so_timeout: 请求超时, conn_timeout: 连接超时, req_timeout 获取连接超时}
	 * @return	发送post请求
	 */
	public static String post(String url, List<NameValuePair> elements,Map<String,String> headers,Map<String,String> config){
		String charset = (config!=null&&config.containsKey("charset"))?config.get("charset"):"UTF-8";
		String so_timeout = (config!=null&&config.containsKey("so_timeout"))?config.get("so_timeout"):"5000";
		String conn_timeout = (config!=null&&config.containsKey("conn_timeout"))?config.get("conn_timeout"):"5000";
		String req_timeout = (config!=null&&config.containsKey("req_timeout"))?config.get("req_timeout"):"3000";
		String result = "";
		CloseableHttpClient client = null;
		CloseableHttpResponse resp = null;
		try{
			client = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			RequestConfig conf = RequestConfig.custom().setConnectTimeout(Integer.valueOf(conn_timeout)).setSocketTimeout(Integer.valueOf(so_timeout))
					.setConnectionRequestTimeout(Integer.valueOf(req_timeout)).build();
			post.setConfig(conf);
			StringEntity entity = new UrlEncodedFormEntity(elements,charset);
			post.setEntity(entity);
			if(headers!=null&&!headers.isEmpty()){
				for(String key:headers.keySet()){
					post.setHeader(new BasicHeader(key, headers.get(key)));
				}
			}else{
				post.setHeader(new BasicHeader("Content-Type","application/x-www-form-urlencoded; charset="+charset.toLowerCase()));
				post.setHeader(new BasicHeader("Accept", "text/plain;charset="+charset.toLowerCase()));
			}
			resp = client.execute(post);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(SUCCESS_CODE==statusCode){
				result = EntityUtils.toString(resp.getEntity(),charset);				
			}			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(client!=null)
					client.close();
				if(resp!=null)
					resp.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * @return	获取网站HTML源代码
	 */
	public static String get(String url){
		return get(url,null,null,null);
	}	
	
	public static String get(String url, List<NameValuePair> elements){
		return get(url,elements,null,null);
	}
	
	/**
	 * @param elements 传递参数
	 * @param headers 请求头数据
	 * @param params { charset: 字符集, so_timeout: 请求超时, conn_timeout: 连接超时, req_timeout 获取连接超时}
	 * @return	发送post请求
	 */
	public static String get(String url, List<NameValuePair> elements,Map<String,String> headers,Map<String,String> config){
		String charset = (config!=null&&config.containsKey("charset"))?config.get("charset"):"UTF-8";
		String so_timeout = (config!=null&&config.containsKey("so_timeout"))?config.get("so_timeout"):"5000";
		String conn_timeout = (config!=null&&config.containsKey("conn_timeout"))?config.get("conn_timeout"):"5000";
		String req_timeout = (config!=null&&config.containsKey("req_timeout"))?config.get("req_timeout"):"3000";
		String result = "";
		CloseableHttpClient client = null;
		CloseableHttpResponse resp = null;
		try{
			client = HttpClients.createDefault();
			URIBuilder uriBuilder = new URIBuilder(url);
			if(elements!=null&&!elements.isEmpty()){
				uriBuilder.addParameters(elements);
			}			
			HttpGet get = new HttpGet(uriBuilder.build());
			RequestConfig conf = RequestConfig.custom().setConnectTimeout(Integer.valueOf(conn_timeout)).setSocketTimeout(Integer.valueOf(so_timeout))
					.setConnectionRequestTimeout(Integer.valueOf(req_timeout)).build();
			get.setConfig(conf);
			if(headers!=null&&!headers.isEmpty()){
				for(String key:headers.keySet()){
					get.setHeader(new BasicHeader(key, headers.get(key)));
				}
			}else{
				get.setHeader(new BasicHeader("Content-Type","application/x-www-form-urlencoded; charset="+charset.toLowerCase()));
				get.setHeader(new BasicHeader("Accept", "text/plain;charset="+charset.toLowerCase()));
			}
			resp = client.execute(get);
			int statusCode = resp.getStatusLine().getStatusCode();
			if(SUCCESS_CODE==statusCode){
				result = EntityUtils.toString(resp.getEntity(),charset);				
			}	
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(client!=null) client.close();
				if(resp!=null) resp.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	
	/**
	 * @return	转换请求参数
	 */
	public static List<NameValuePair> convertJson(JSONObject reqData){
		List<NameValuePair> list = new ArrayList<>();		
		if(reqData!=null&&!reqData.isEmpty()){
			for(String key:reqData.keySet()){
				list.add(new BasicNameValuePair(key, reqData.getString(key)));
			}		
		}		
		return list;
	}
	
	
	public static List<String> getHtmlTag(String url, String tag,String attr){
		return getHtmlTag(url,tag,attr,"get",null,null,null);
	}
	
	
	public static List<String> getHtmlTag(String url, String tag,String attr,String reqType,Map<String,String> params){
		return getHtmlTag(url, tag, attr, reqType, params,null,null);	
	}
	
	
	/**
	 * @jar jsoup
	 * @param url 网址   tag 标签名称	attr 属性	 reqType(post/get/put)
	 * @param params 请求参数	headers	请求头   config 请求配置
	 * @return	获取html网页某标签属性值
	 */
	public static List<String> getHtmlTag(String url,String tag,String attr,String reqType,Map<String,String> params,Map<String,String> headers,Map<String,String> config){
		List<String> list = new ArrayList<String>();
		int timeout = 3000;
		try{
			Connection conn = Jsoup.connect(url);
			if(headers!=null&&!headers.isEmpty()){
				for(String key:headers.keySet()){
					conn.header(key, headers.get(key));
				}
			}
			if(params!=null&&!params.isEmpty()){
				conn.data(params);
			}			
			if(config!=null){
				timeout = config.containsKey("timeout")?Integer.valueOf(config.get("timeout")):timeout;
			}
			conn.timeout(timeout);			
			Document doc = null;
			if("post".equals(reqType.toLowerCase())){
				doc = conn.post();
			}else{
				doc = conn.get();
			}
			Elements eles = doc.getElementsByTag(tag);
			if(!eles.isEmpty()){
				for(Element ele:eles){
					if(attr==null||"".equals(attr)){
						list.add(ele.html());
					}else{
						if(ele.hasAttr(attr)){
							list.add(ele.attr(attr));
						}						
					}					
				}
			}			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
	
	/**
	 * @param url	服务器地址	 file 控制层导入的文件	
	 * @param params 需要的其他参数
	 * @return	转发文件到指定服务器
	 */
	public static String postFile(String url,MultipartFile file,JSONObject params){
		return postFile(url, file, params, null, null);		
	}
	
	
	/**
	 * @param url	服务器地址	 file 控制层导入的文件	
	 * @param params 需要的其他参数
	 * @param headers	请求头信息
	 * @param config	{fileName: 网络文件重命名, paramName: 服务器端接收文件参数名, charset: 字符集}
	 * @return	转发文件到指定服务器
	 */
	public static String postFile(String url,MultipartFile file,JSONObject params,JSONObject headers,JSONObject config){
		String result = "";
		String fileName = (config!=null&&config.containsKey("fileName"))?config.getString("fileName"):file.getOriginalFilename();
		String paramName = (config!=null&&config.containsKey("paramName"))?config.getString("paramName"):"file";
		String charset = (config!=null&&config.containsKey("charset"))?config.getString("charset"):"UTF-8";
		try(
			InputStream is = file.getInputStream();	
		){
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			if(headers!=null&&!headers.isEmpty()){
				for(String header:headers.keySet()){
					httpPost.setHeader(header, headers.getString(header));
				}
			}
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody(paramName, is,ContentType.MULTIPART_FORM_DATA,fileName);
			builder.setCharset(Charset.forName(charset));
			if(params!=null&&!params.isEmpty()){
				ContentType contentType = ContentType.create("text/plain", Charset.forName(charset));
				for(String key:params.keySet()){
					builder.addTextBody(key, params.getString(key),contentType);
				}
			}
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse res = httpClient.execute(httpPost);
			HttpEntity resEntity = res.getEntity();
			if(resEntity!=null){
				result = EntityUtils.toString(resEntity,Charset.forName(charset));
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	
	/**
	 * @param url	服务器地址	 filepath 本地文件路径(包含文件名)	
	 * @param params	需要的其他参数
	 * @return	上传本地文件到服务器
	 */
	public static String postFile(String url,String filepath,JSONObject params){
		return postFile(url, filepath, params, null, null);
	}
	
	
	
	/**
	 * @param url	服务器地址	 filepath 本地文件路径(包含文件名)	
	 * @param params	需要的其他参数
	 * @param headers 	请求头信息
	 * @param config	{fileName: 网络文件重命名, paramName: 服务器端接收文件参数名, charset: 字符集}
	 * @return	上传本地文件到服务器
	 */
	public static String postFile(String url,String filepath,JSONObject params,JSONObject headers,JSONObject config){
		String result = "";
		File file = new File(filepath);
		String fileName = (config!=null&&config.containsKey("fileName"))?config.getString("fileName"):file.getName();
		String paramName = (config!=null&&config.containsKey("paramName"))?config.getString("paramName"):"file";
		String charset = (config!=null&&config.containsKey("charset"))?config.getString("charset"):"UTF-8";
		
		try(
			InputStream is = new FileInputStream(new File(filepath));	
		){
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			if(headers!=null&&!headers.isEmpty()){
				for(String header:headers.keySet()){
					httpPost.setHeader(header, headers.getString(header));
				}
			}
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody(paramName, is,ContentType.MULTIPART_FORM_DATA,fileName);
			builder.setCharset(Charset.forName(charset));
			if(params!=null&&!params.isEmpty()){
				ContentType contentType = ContentType.create("text/plain", Charset.forName(charset));
				for(String key:params.keySet()){
					builder.addTextBody(key, params.getString(key),contentType);
				}
			}
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse res = httpClient.execute(httpPost);
			HttpEntity resEntity = res.getEntity();
			if(resEntity!=null){
				result = EntityUtils.toString(resEntity,Charset.forName(charset));
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	
	/**
	 * @param url	服务器地址 		fileURL	网络文件地址
	 * @param params 需要发送的其它参数
	 * @return	发送网络文件及参数到指定服务器
	 */
	public static String postWebFile(String url,String fileURL,JSONObject params){
		return postWebFile(url, fileURL, params, null, null);
	}

	
	
	/**
	 * @jar		httpclient, httpmime
	 * @param url	服务器地址 		fileURL	网络文件地址
	 * @param params	需要发送的其它参数
	 * @param headers	请求头信息
	 * @param config	{fileName: 网络文件重命名, paramName: 服务器端接收文件参数名, charset: 字符集}
	 * @return	发送网络文件及参数到指定服务器
	 */
	public static String postWebFile(String url,String fileURL,JSONObject params,JSONObject headers,JSONObject config){
		String result = "";
		String fileName = (config!=null&&config.containsKey("fileName"))?config.getString("fileName"):FileUtil.getNameByURL(fileURL, 20);
		String paramName = (config!=null&&config.containsKey("paramName"))?config.getString("paramName"):"file";
		String charset = (config!=null&&config.containsKey("charset"))?config.getString("charset"):"UTF-8";
		
		try(
			InputStream is = new URL(fileURL).openStream();	
		){
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);
			if(headers!=null&&!headers.isEmpty()){
				for(String header:headers.keySet()){
					httpPost.setHeader(header, headers.getString(header));
				}
			}
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addBinaryBody(paramName, is,ContentType.MULTIPART_FORM_DATA,fileName);
			builder.setCharset(Charset.forName(charset));
			if(params!=null&&!params.isEmpty()){
				ContentType contentType = ContentType.create("text/plain", Charset.forName(charset));
				for(String key:params.keySet()){
					builder.addTextBody(key, params.getString(key),contentType);
				}
			}
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse res = httpClient.execute(httpPost);
			HttpEntity resEntity = res.getEntity();
			if(resEntity!=null){
				result = EntityUtils.toString(resEntity,Charset.forName(charset));
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		
		
	}
	
	

}
