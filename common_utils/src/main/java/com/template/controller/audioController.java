package com.template.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/audio")
public class audioController {
	
	// 读取音频流
	@RequestMapping("/play")
	public static void play(HttpServletRequest req,HttpServletResponse res){
		String root  = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path");
		File audio = new File(root+path);
		res.setHeader("Content-Type", "audio/mpeg");
		try(FileInputStream fis = new FileInputStream(audio);
			OutputStream os = res.getOutputStream();){
			byte[] buffer = new byte[500*1024];
			int len = -1;
			while((len=fis.read(buffer))!=-1){
				os.write(buffer,0,len);		
				os.flush();
			}						
		}catch (Exception e) {
			e.printStackTrace();
		}		
		
	}
	
	
	// websocket 播放隐藏真实audio地址	防下载	网易云
	
	
	
	// 取暂时一次性下载链接
	
}
