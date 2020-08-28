package com.template.controller;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.utils.FileUtil;
import com.utils.HttpUtil;

/**
 * jar: fastjson
 */
@Controller
@RequestMapping("/file")
public class fileController {
	
	
	/**
	 * @return	上传文件
	 */
	@RequestMapping("/upload")
	@ResponseBody
	public String upload(@RequestParam(value="file")MultipartFile file,HttpServletRequest req){
		JSONObject result = new JSONObject();
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"/file/tmp":req.getParameter("path");
		String name = file.getOriginalFilename();
		String savename= FileUtil.newFileName(name);
		String dst = path+File.separator+savename; 
		if(FileUtil.upload(file,root+dst)){
			JSONObject info = new JSONObject();
			info.put("org", name);
			info.put("name", savename);
			info.put("path", dst);
			info.put("ext", FileUtil.getExt(dst));
			info.put("mimetype", FileUtil.getContType(dst));
			info.put("size", file.getSize());
			result.put("info", info);		
			result.put("code", "0");
			result.put("msg", "success");		
		}else{
			result.put("code", "-1");
			result.put("msg", "解析文件失败");
		}
		
		return result.toString();
	}
	
	
	
	/**
	 * @param url	服务器地址	 file 控制层导入的文件	
	 * @param params 需要的其他参数
	 * @param headers	请求头信息
	 * @param config	{fileName: 网络文件重命名, paramName: 服务器端接收文件参数名, charset: 字符集}
	 * @return	转发文件到指定服务器
	 */
	@RequestMapping("/httpUpload")
	@ResponseBody
	public String httpUpload(@RequestParam(value="file")MultipartFile file,HttpServletRequest req,HttpServletResponse res){
		JSONObject result = new JSONObject();
		String url = req.getParameter("url")==null?"":req.getParameter("url");
		String params = req.getParameter("params")==null?"{}":req.getParameter("params");
		String headers = req.getParameter("headers") ==null?"{}":req.getParameter("headers");
		String config = req.getParameter("config")==null?"{}":req.getParameter("config");
		if("".equals(url)){
			result.put("code", "-1");
			result.put("url", "服务器地址不能为空");
		}else{
			String msg = HttpUtil.postFile(url, file, JSONObject.parseObject(params), JSONObject.parseObject(headers), JSONObject.parseObject(config));			
			result = JSONObject.parseObject(msg);
		}		
		return result.toString();
	}
	
	
	/**
	 * @param path: 保存路径	folder: 文件夹名称	 uid: 文件唯一标识 -- 文件相对路径
	 * @return	上传文件夹
	 */
	@RequestMapping("/uploadDir")
	@ResponseBody
	public String uploadDir(@RequestParam(value="file")MultipartFile[] files,HttpServletRequest req){
		JSONObject result = new JSONObject();
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"/file/tmp":req.getParameter("path");
		String folder = req.getParameter("folder")==null?"":req.getParameter("folder");
		boolean done = true;
	    for (MultipartFile file : files){
	    	String org = file.getOriginalFilename();
	    	String uid = org.substring(org.lastIndexOf("_")+1);
	    	String dir = req.getParameter(uid)==null?"":req.getParameter(uid);
	    	String dst = root+path+File.separator+dir;
	    	if(!FileUtil.upload(file, dst)){
	    		done = false;
	    	}
	    }
	    if(done){
	    	result.put("code", "0");
	    	result.put("msg", "ok");
	    	result.put("dir", path+File.separator+folder);
	    	
	    }else{
	    	result.put("code", "-1");
	    	result.put("msg", "上传失败");
	    }	    
	    
		return result.toString();
	}
	
	
	
	
	
	/**	
	 * @param	path: 包含文件/文件夹名称 
	 * @return	删除文件/文件夹
	 */
	@RequestMapping("/del")
	@ResponseBody
	public String del(HttpServletRequest req){
		JSONObject result = new JSONObject();
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"":req.getParameter("path");		
		if(!"".equals(path)){
			if(FileUtil.delete(root+path)){
				result.put("code", "0");
				result.put("msg", "删除成功");
			}else{
				result.put("code", "-1");
				result.put("msg", "删除失败");
			}			
		}else{
			result.put("code", "999");
			result.put("msg", "文件路径不能为空");
		}		
		return result.toString();
	}
	
	
	
	@RequestMapping("/zip")
	public ResponseEntity<byte[]> zip(HttpServletRequest req,HttpServletResponse res){
		ResponseEntity<byte[]> re = null;
		String root = req.getServletContext().getRealPath("/");
		String filename = req.getParameter("filename")==null?"compress.zip":req.getParameter("filename");
		String dst = root+"/file/tmp/"+filename;
		String json = req.getParameter("json")==null?"{}":req.getParameter("json");
		JSONObject params = JSONObject.parseObject(json);
		
		if(params.containsKey("paths")){
			JSONArray paths = params.getJSONArray("paths");
			List<String> list = new ArrayList<>();
			for(int i=0;i<paths.size();i++){
				list.add(root+paths.getString(i));				
			}
			try{
				if(FileUtil.zip(list, dst)){
					HttpHeaders headers = new HttpHeaders();  
				    String downloadFileName = new String(filename.getBytes("UTF-8"),"iso-8859-1");
				    headers.setContentDispositionFormData("attachment", downloadFileName); 
				    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				    re = new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(dst)),    
				               headers, HttpStatus.CREATED);  
				}		
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	    return re;
	}
	
	
	/**
	 * @intro	如实现似迅雷本地续传，需客户端
	 * @param	url	下载地址
	 * @param	path: 保存路径		num: 开启下载线程数	
	 * @return 	(服务器端--断点续传) 下载文件	
	 * @benefit 防止访问目标服务器不稳定,导致的已下载数据丢失
	 */
	@RequestMapping("/download")
	public void download(HttpServletRequest req,HttpServletResponse res){
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"/file/tmp":req.getParameter("path");
		int num	= req.getParameter("num")==null?3:Integer.valueOf(req.getParameter("num"));
		String url = req.getParameter("url")==null?"":req.getParameter("url");
		try{
			String error = "";
			if("".equals(url.trim())){
				error = "URL不能为空";
			}else{
				// 如必要, 可死循环直到文件下载完毕
				String name = FileUtil.getNameByURL(url, 40);
				String dst = root+path+File.separator+name;
				if(FileUtil.resumeDownload(url, dst, num)){
					res.reset();
					res.setContentType("bin");
					res.addHeader("Content-Disposition", "attachment;filename=\""+name+"\"");
					InputStream is = new FileInputStream(dst);
					byte[] bytes = new byte[1024*1024];
					int len;
					while((len=is.read(bytes))>0){
						res.getOutputStream().write(bytes,0,len);
					}
					is.close();
				}else{
					error = "下载失败, 请重试!";
				}				
			}					
			if(!"".equals(error)){  // 最好返回错误页面
				res.setContentType("text/html;charset=utf-8");
				PrintWriter writer = res.getWriter();
				writer.print("<span style='color:red;'>"+error+"</span>");
				writer.flush();
				writer.close();
			}			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	
	
	/**
	 * @frontend	前端插件   plupload
	 * @param name	原文件名	path 保存路径	 size 原文件大小	chunk 分片索引(前端插件自动传)	chunk_size 分片大小(M)
	 * @return	大文件分片上传
	 */
	@RequestMapping("/chunkUpload")
	@ResponseBody
	public String chunkUpload(@RequestParam(value="file")MultipartFile file,HttpServletRequest req,HttpServletResponse res){
		JSONObject result = new JSONObject();
		String root = req.getServletContext().getRealPath("/");
		String name = req.getParameter("name")==null?"":req.getParameter("name");
		String path = req.getParameter("path")==null?"/file/tmp":req.getParameter("path");
		int size = req.getParameter("size")==null?0:Integer.valueOf(req.getParameter("size")); //整个文件大小
		int chunk = req.getParameter("chunk")==null?-1:Integer.valueOf(req.getParameter("chunk")); // 每一片数据索引	0,1..
		int chunk_size = req.getParameter("chunk_size")==null?-1:Integer.valueOf(req.getParameter("chunk_size"));
		
		if("".equals(name)||0==size||-1==chunk||-1==chunk_size){
			result.put("code", "-1");
			result.put("msg", "参数错误");
			return result.toString();
		}
		
		File parent = new File(root+path);
		if(!parent.exists()||!parent.isDirectory()){
			parent.mkdirs();
		}
		String dst = root+path+File.separator+name;	
		
		try(
			RandomAccessFile raf = new RandomAccessFile(dst, "rw");
			InputStream is = file.getInputStream();
		){

			raf.seek(chunk*chunk_size*1024*1024);
			int len = -1;
			byte[] bytes = new byte[500*1024];
			
			while((len=is.read(bytes))!=-1){
				raf.write(bytes, 0, len);
			}
			
			if(raf.length()==size){
				result.put("code", "0");
				result.put("msg", "上传成功");
			}else{
				result.put("code", "999");
				result.put("msg", "上传中");
			}
			result.put("name", name);
			result.put("path", path+File.separator+name);
			raf.close();
		}catch(Exception e){
			e.printStackTrace();
			result.put("code", "-2");
			result.put("msg", "上传失败");
		}		
		
		
		return result.toString(); 
	}
	
	
	
	
	/**
	 * @frontend	前端插件   plupload
	 * @param name	原文件名	path 保存路径	 size 原文件大小	chunk 分片索引(前端插件自动传)
	 * @return	大文件分片上传 (文件流)
	 */
	@RequestMapping("/chunkUploadEx")
	@ResponseBody
	public String chunkUploadEx(@RequestParam(value="file")MultipartFile file,HttpServletRequest req,HttpServletResponse res){
		JSONObject result = new JSONObject();
		String root = req.getServletContext().getRealPath("/");
		String name = req.getParameter("name")==null?"":req.getParameter("name");
		String path = req.getParameter("path")==null?"/file/tmp":req.getParameter("path");
		int size = req.getParameter("size")==null?0:Integer.valueOf(req.getParameter("size"));
		int chunk = req.getParameter("chunk")==null?-1:Integer.valueOf(req.getParameter("chunk")); 
		if("".equals(name)||0==size||-1==chunk){
			result.put("code", "-1");
			result.put("msg", "参数错误");
			return result.toString();
		}
		
		File parent = new File(root+path);
		if(!parent.exists()||!parent.isDirectory()){
			parent.mkdirs();
		}
		String dst = root+path+File.separator+name;	
		synchronized(this){
			try(
				InputStream is = file.getInputStream();
				OutputStream os = new FileOutputStream(dst, true);
			){
				int len = -1;
				byte[] bytes = new byte[500*1024];
				
				while((len=is.read(bytes))!=-1){
					os.write(bytes, 0, len);
				}					
				os.flush();
				
				result.put("name", name);
				result.put("path", path+File.separator+name);
			}catch(EOFException eof){
				
			}catch(Exception e){
				e.printStackTrace();
				result.put("code", "-2");
				result.put("msg", "上传失败");
			}
		}	
		
		if(new File(dst).exists()&&new File(dst).length()==size){
			result.put("code", "0");
			result.put("msg", "上传成功");
		}else{
			result.put("code", "999");
			result.put("msg", "上传中");
		}		
		
		return result.toString();
	}
		
	
}
