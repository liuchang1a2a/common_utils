package com.template.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.utils.FileUtil;
import com.utils.VideoUtil;


/**
 * @jar	fastjson	ffmpeg.exe	
 */
@Controller
@RequestMapping("/video")
public class videoController {
	
	/**
	 * @param  path	资源相对路径(含文件名)	 
	 * @return 读取本地资源, 返回视频流
	 */
	@RequestMapping("/getLocal")
	public void getLocal(HttpServletRequest req,HttpServletResponse res){
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		String agent = req.getHeader("User-Agent").toUpperCase();
		File file = new File(root+path);
		try(
			InputStream is = new FileInputStream(file);
		){
			byte[] buffer = new byte[is.available()];
			is.read(buffer);
			res.reset();
			if(agent.indexOf("FIREFOX")!=-1){
				res.addHeader("Content-Disposition", "attachment;filename="+new String(file.getName().getBytes("GB2312"),"ISO-8859-1"));
			}else{
				res.addHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(file.getName(),"UTF-8"));
			}
			
			res.setCharacterEncoding("UTF-8");
			res.addHeader("Content-Length", ""+file.length());
			res.setContentType("video/mpeg4");
			OutputStream os = res.getOutputStream();
			os.write(buffer);			
		}catch(Exception e){
			e.printStackTrace();			
			// 输出 视频播送失败  文字	错误页面
			res.setContentType("text/html;charset=utf-8");
			try{
				PrintWriter writer = res.getWriter();
				writer.print("<span style='color:red;'>视频文件读取失败</span>");
				writer.flush();
				writer.close();
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}			
	}
	
	
	/**
	 * @param url 视频链接地址
	 * @return 读取URL资源, 返回视频流
	 */
	@RequestMapping("/getWeb")
	public void getWeb(HttpServletRequest req,HttpServletResponse res){
		String url = req.getParameter("url")==null?"":req.getParameter("url");
		InputStream is = null;
		OutputStream os = null;
		try{
			URL link = new URL(url);
			URLConnection conn = link.openConnection();
			is = conn.getInputStream();
			res.reset();
			res.addHeader("Content-Disposition", "attachment;filename="+FileUtil.getNameByURL(url, 30));
			res.setCharacterEncoding("UTF-8");
			res.setContentType("video/mpeg4");
			os = res.getOutputStream();
			int len = -1;
			byte[] buffer = new byte[500*1024];
			while((len=is.read(buffer))!=-1){
				os.write(buffer,0,len);
			}			
			
		}catch(Exception e){
			e.printStackTrace();
			res.setContentType("text/html;charset=utf-8");
			try{
				PrintWriter writer = res.getWriter();
				writer.print("<span style='color:red;'>视频读取失败</span>");
				writer.flush();
				writer.close();
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}finally{
			try{
				if(is!=null) is.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}		
	}
	
	
	
	
	/**
	 * @frontend video.js videojs-contrib-hls.min.js
	 * @param	path 源文件路径(含文件名)		target	m3u8保存路径(含文件名)
	 * @return	获取m3u8视频文件  (ts分段播放)
	 */
	@RequestMapping("/m3u8")
	@ResponseBody
	public String m3u8(HttpServletRequest req,HttpServletResponse res){
		JSONObject m3u8 = new JSONObject();
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		String target = req.getParameter("target")==null?"":req.getParameter("target");
		String ffmpeg = root+"plugin/ffmpeg/ffmpeg.exe";
		if("".equals(target)){
			target =  new File(root+path).getParent()+File.separator+FileUtil.newFileName(FileUtil.getNameWithoutExt(path)+".m3u8");
		}
		if(VideoUtil.m3u8(root+path, target, ffmpeg, 10, 0)){
			m3u8.put("code", "0");
			m3u8.put("msg", "ok");
			m3u8.put("m3u8", target.substring(root.length()));
		}else{
			m3u8.put("code", "-1");
			m3u8.put("msg", "获取m3u8文件失败");
		}		
		
		return m3u8.toString();
	}
	
	
	
	
	
}
