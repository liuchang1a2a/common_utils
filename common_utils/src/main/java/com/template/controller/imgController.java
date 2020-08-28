package com.template.controller;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONObject;
import com.utils.FileUtil;
import com.utils.ImageUtil;
import com.utils.StringUtil;
import com.wf.captcha.ChineseGifCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;



/**
 **	jar: fastjson, commons-fileupload, commons-io	easy-captcha
 */
@Controller
@RequestMapping("/img")
@CrossOrigin
public class imgController {
	
	/**
	 * jsonStr={}
	 * ext: 验证码图片扩展名		txt: 验证码可选字符  's,a,f,x,w'
	 * num: 验证码字符个数		cn:是否为中文		repeat: 是否包含重复字符
	 * @param res 获取验证码
	 */
	@RequestMapping("/code")
	public void drawCheckCode(HttpServletRequest req,HttpServletResponse res){
		String jsonStr = req.getParameter("jsonStr")==null?"{}":req.getParameter("jsonStr");
		// 获取随机字符
		JSONObject params = JSONObject.parseObject(jsonStr);
		String ext = params.containsKey("ext")?params.getString("ext"):"png";
		String code = "";
		int num = params.containsKey("num")?params.getInteger("num"):4;
		String txt = params.containsKey("txt")?params.getString("txt"):"";
		boolean cn = params.containsKey("cn")?params.getBoolean("cn"):false;
		boolean repeat = params.containsKey("repeat")?params.getBoolean("repeat"):true;
		if(!"".equals(txt)){
			code = StringUtil.randomTxt(txt, num, repeat);		
		}else if(cn){
			code = StringUtil.randomCnTxt(num, repeat);
		}else{
			code = StringUtil.randomTxt(num, repeat);
		}
		
		req.getSession().setAttribute("checkCode", code);
		BufferedImage codeImg= ImageUtil.checkCode(params, code);
		res.setContentType(FileUtil.getContTypeByExt(ext)); 
		// 画图形
		try(
			OutputStream os = res.getOutputStream();){
			ImageIO.write(codeImg,ext,os);
			os.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
		 
	}
	
	
	/**
	 * @jar easy-captcha	https://gitee.com/whvse/EasyCaptcha/tree/master
	 * @param	len 验证码长度		cn	是否中文	width,height  验证码宽高	font字体	 fsize 字体大小
	 * @param	type  1: 字母数字混合	2:	纯数字	3: 纯字母	 4:纯大写字母	5:纯小写字母	6:数字大写字母
	 * @return	获取gif特效验证码
	 */
	@RequestMapping("/gifcode")
	public void drawGifCode(HttpServletRequest req,HttpServletResponse res){
		int len = req.getParameter("len")==null?4:Integer.valueOf(req.getParameter("len"));	
		boolean cn = req.getParameter("cn")==null?false:"true".equals(req.getParameter("cn"));
		int width = req.getParameter("width")==null?130:Integer.valueOf(req.getParameter("width"));	
		int height = req.getParameter("height")==null?48:Integer.valueOf(req.getParameter("height"));	
		String font = req.getParameter("font")==null?(cn?"楷体":"Verdana"):req.getParameter("font");	
		int fsize = req.getParameter("fsize")==null?32:Integer.valueOf(req.getParameter("fsize"));	
		int type = req.getParameter("type")==null?Captcha.TYPE_DEFAULT:Integer.valueOf(req.getParameter("type"));	
		res.setContentType("image/gif");
		res.setHeader("Pragma", "No-cache");
		res.setHeader("Cache-Control", "No-cache");
		res.setDateHeader("Expires", 0);
		try(OutputStream os = res.getOutputStream();){
			HttpSession session = req.getSession();
			if(!cn){
				GifCaptcha gifCaptcha = new GifCaptcha(width,height,len);
				gifCaptcha.setFont(new Font(font,Font.PLAIN,fsize));			
				gifCaptcha.setCharType(type);				
				session.setAttribute("checkCode", gifCaptcha.text().toLowerCase());
				gifCaptcha.out(os);
			}else{				
				ChineseGifCaptcha captcha = new ChineseGifCaptcha(width, height, len);
				captcha.setFont(new Font(font,Font.PLAIN,fsize));
				captcha.setCharType(type);
				
				session.setAttribute("checkCode", captcha.text().toLowerCase());
				captcha.out(os);
			}	
			
		}catch(Exception e){
				e.printStackTrace();
		} 
		
		
	}
	
	
	
	@RequestMapping("/chkcode")
	@ResponseBody
	public String checkCode(HttpServletRequest req,HttpServletResponse res) throws Exception{
		JSONObject result = new JSONObject();
		String code = req.getParameter("code").toLowerCase();
		String checkCode = (String)req.getSession().getAttribute("checkCode");
		if(checkCode.equals(code)){
			result.put("code", "0");
			result.put("msg", "success");
		}else{
			result.put("code", "-1"); 
			result.put("msg", "验证码错误");
		}
		return result.toString();
	}
	
	/**
	 * jar: commons-fileupload, commons-io
	 * @param path: 保存路径
	 * @return	上传图片文件
	 */
	@RequestMapping("/upload")
	@ResponseBody
	public String upload(@RequestParam(value="file")MultipartFile file,HttpServletRequest req){
		JSONObject result = new JSONObject();
		if(!ImageUtil.isImg(FileUtil.MultipartToFile(file, "/file/"))){
			result.put("code", "999");
			result.put("msg", "请上传图片文件");
			return result.toString();
		}
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"/file/img":req.getParameter("path");
		String name = file.getOriginalFilename();
		String savename= FileUtil.newFileName(name);
		String dst = path+File.separator+savename; 
		
		if(FileUtil.upload(file, root+dst)){
			JSONObject info = new JSONObject();
			info.put("org", name);
			info.put("name", savename);
			info.put("path", dst);
			info.put("ext", FileUtil.getExt(dst));
			info.put("mimetype", FileUtil.getContType(dst));
			info.put("size", file.getSize());
			JSONObject detail = ImageUtil.info(root+dst);
			info.put("width", detail.getInteger("width"));
			info.put("height", detail.getInteger("height"));			
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
	 * url: 远程文件	path: 保存路径
	 * @return 下载网络图片
	 */
	@RequestMapping("/download")
	@ResponseBody
	public String download(HttpServletRequest req,HttpServletResponse res){
		JSONObject result = new JSONObject();
		String url = req.getParameter("url")==null?"":req.getParameter("url");
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"/file/img":req.getParameter("path");
		if(!StringUtil.isURL(url)){
			result.put("code", "-1");
			result.put("msg", "URL违法");
			return result.toString();
		}
		File dir = new File(root+path);
		if(!dir.exists()||!dir.isDirectory()){
			dir.mkdirs();
		}
		String name = FileUtil.newFileName(FileUtil.getNameByURL(url, 15));
		if(FileUtil.download(url, root+path+File.separator+name)){
			JSONObject info = new JSONObject();
			File local = new File(root+path+File.separator+name);
			info.put("name", local.getName());
			info.put("path", path+File.separator+name);
			info.put("ext", FileUtil.getExt(local));
			info.put("mimetype", FileUtil.getContType(local));
			info.put("size", local.length());
			result.put("info", info);
			result.put("code", "0");
			result.put("msg", "success");			
		}else{
			result.put("code", "-1");
			result.put("msg", "下载远程文件失败");
		}	
		
		return result.toString();
	}
	
	
	/**
	 * @param json:{ text: 文字	color: 文字颜色	fstyle: 字体	fsize: 字体大小,x, y: 文字坐标, degree: 旋转角度 }
	 * @param ext: 扩展名, path: 图片相对路径
	 * @return 	图片添加文字 
	 */
	@RequestMapping("/marktxt")
	public void markTxt(HttpServletRequest req,HttpServletResponse res){
		String json = req.getParameter("json")==null?"{}":req.getParameter("json");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		String ext = req.getParameter("ext")==null?"png":req.getParameter("ext");
		String root = req.getServletContext().getRealPath("/");
		JSONObject params = JSONObject.parseObject(json);
		BufferedImage img = ImageUtil.markByText(new File(root+path), params);
		res.setContentType(FileUtil.getContTypeByExt(ext)); 
		try(
			OutputStream os = res.getOutputStream();){
			ImageIO.write(img,ext,os);
			os.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * @param path: 图片相对路径, icon: 图标相对路径, ext: 扩展名
	 * @param json:{ clarity:透明度,	x, y: 图标坐标, degree: 旋转角度 }
	 * @return	图片添加图标
	 */
	@RequestMapping("/markicon")
	public static void markIcon(HttpServletRequest req,HttpServletResponse res){
		String json = req.getParameter("json")==null?"{}":req.getParameter("json");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		String icon = req.getParameter("icon")==null?"":req.getParameter("icon");
		String ext = req.getParameter("ext")==null?"png":req.getParameter("ext");
		String root = req.getServletContext().getRealPath("/");
		JSONObject params = JSONObject.parseObject(json);
		BufferedImage bi = ImageUtil.markByIcon(new File(root+path), new File(root+icon), params);
		res.setContentType(FileUtil.getContTypeByExt(ext)); 
		try(
			OutputStream os = res.getOutputStream();){
			ImageIO.write(bi,ext,os);
			
			os.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}		
		
	}
	
	
	
	/**
	 * @param ext 二维码图片格式
	 * @param json {"content":内容, "img":logo地址, "compress": 是否压缩logo, "size":二维码尺寸, logo_width, logo_height}
	 * @return 生成二维码
	 */
	@RequestMapping("/qrencode")
	public static void qrEncode(HttpServletRequest req,HttpServletResponse res){
		String json = req.getParameter("json")==null?"{}":req.getParameter("json");
		String ext = req.getParameter("ext")==null?"png":req.getParameter("ext");
		String root = req.getServletContext().getRealPath("/");
		JSONObject params = JSONObject.parseObject(json);
		if(params.containsKey("img")&&!"".equals(params.getString("img"))){
			params.put("img", root+params.getString("img"));
		}
		BufferedImage bi = ImageUtil.qrEncode(params);
		res.setContentType(FileUtil.getContTypeByExt(ext)); 
		try(
			OutputStream os = res.getOutputStream();){
			ImageIO.write(bi,ext,os);
			os.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}			
	}
	
	
	
	
	/**
	 * @param 图片相对路径
	 * @return	解析二维码图片
	 */
	@RequestMapping("/qrdecode")
	@ResponseBody
	public static String qrDecode(HttpServletRequest req,HttpServletResponse res){
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		JSONObject jsobject = new JSONObject();
		File file = new File(root+path);
		if(!file.exists()||!file.isFile()){
			jsobject.put("code", "999");
			jsobject.put("msg", "请重新上传二维码");
			return jsobject.toString();
		}
		
		String content = ImageUtil.qrDecode(file);
		jsobject.put("code", "0");
		jsobject.put("msg", "ok");
		jsobject.put("content", content);
		
		return jsobject.toString();
	}
	
	
	
	/**
	 * @param	path 原图片路径(含文件名)	 scale 缩放比例 (>1 放大, <1缩小)
	 * @param 	缩放图片
	 */
	@RequestMapping("/zoom")
	public void zoom(HttpServletRequest req,HttpServletResponse res){
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		double scale = req.getParameter("scale")==null?1.0:Double.valueOf(req.getParameter("scale"));
				
		try(OutputStream os = res.getOutputStream();){
			res.setContentType(FileUtil.getContType(root+path));
			if(!"".equals(path)&&ImageUtil.zoom(new File(root+path), scale, os)){
				os.flush();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * @param path	原图片相对路径	scale 图片质量比
	 * @return	调整图片质量
	 */
	@RequestMapping("/quality")
	public void quality(HttpServletRequest req,HttpServletResponse res){
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		double scale = req.getParameter("scale")==null?1.0:Double.valueOf(req.getParameter("scale"));
		try(OutputStream os = res.getOutputStream();){
			res.setContentType(FileUtil.getContType(root+path));
			if(!"".equals(path)&&ImageUtil.quality(new File(root+path), scale, os)){
				os.flush();
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * @param	path 图片相对路径	limit 限制大小(KB)		accuracy 精度
	 * @return	压缩图片大小 	
	 */
	@RequestMapping("/compress")
	public void compress(HttpServletRequest req,HttpServletResponse res){
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		int limit = req.getParameter("limit")==null?100:Integer.valueOf(req.getParameter("limit"));
		double accuracy = req.getParameter("accuracy")==null?0.9:Double.valueOf(req.getParameter("accuracy"));
		try(OutputStream os = res.getOutputStream();){
			res.setContentType(FileUtil.getContType(root+path));
			File src = new File(root+path);
			String fmt = FileUtil.getExt(src);
			String dst = src.getParent()+File.separator+FileUtil.newFileName(src.getName());
			if(!"".equals(path)&&ImageUtil.compress(root+path, dst, limit,accuracy)){
				BufferedImage bi = ImageIO.read(new File(dst));
				ImageIO.write(bi, fmt, os);
				os.flush();
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
	} 
	
	
	
	/**
	 * @param	path 图片相对路径	limit 限制大小(KB)		accuracy 精度
	 * @return	添加水印	
	 */
	@RequestMapping("/watermark")
	public void watermark(HttpServletRequest req,HttpServletResponse res){
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		String water = req.getParameter("water")==null?"file/img/icon.png":req.getParameter("water");
		float clarity = req.getParameter("clarity")==null?0.8f:Float.valueOf(req.getParameter("clarity"));
		try(OutputStream os = res.getOutputStream();){
			res.setContentType(FileUtil.getContType(root+path));
			ImageUtil.watermark(root+path, root+water, os, Positions.BOTTOM_RIGHT, clarity);
			os.flush();
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * @param	path  图片路径(含文件名)	 angle	旋转角度
	 * @return	旋转图片
	 */
	@RequestMapping("/rotate")
	public void rotate(HttpServletRequest req,HttpServletResponse res){
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		Double	angle = req.getParameter("angle")==null?90.00:Double.valueOf(req.getParameter("angle"));
		try(OutputStream os = res.getOutputStream();){
			res.setContentType(FileUtil.getContType(root+path));
			ImageUtil.rotate(root+path, angle, os);
			os.flush();
		}catch(Exception e){
			e.printStackTrace();
		}			
	}
	
	
	
	/**
	 * @param	path 图片相对路径(含文件名)	width,height 宽/高
	 * @return	调整图片宽高	
	 */
	@RequestMapping("/resize")
	public void resize(HttpServletRequest req,HttpServletResponse res){
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		int width = req.getParameter("width")==null?ImageUtil.info(root+path).getInteger("width"):Integer.valueOf(req.getParameter("width"));
		int height = req.getParameter("height")==null?ImageUtil.info(root+path).getInteger("height"):Integer.valueOf(req.getParameter("height"));
		try(OutputStream os = res.getOutputStream();){
			res.setContentType(FileUtil.getContType(root+path));
			ImageUtil.resize(root+path, width, height, os);
			os.flush();
		}catch(Exception e){
			e.printStackTrace();
		}			
	}
	
	
	
	/**
	 * @param path	图片路径(含文件名)	width, height	裁剪宽高
	 * @return	裁剪图片
	 */
	@RequestMapping("/cut")
	public void cut(HttpServletRequest req,HttpServletResponse res){
		String root = req.getServletContext().getRealPath("/");
		String path = req.getParameter("path")==null?"":req.getParameter("path");
		int width = req.getParameter("width")==null?ImageUtil.info(root+path).getInteger("width"):Integer.valueOf(req.getParameter("width"));
		int height = req.getParameter("height")==null?ImageUtil.info(root+path).getInteger("height"):Integer.valueOf(req.getParameter("height"));
		try(OutputStream os = res.getOutputStream();){
			res.setContentType(FileUtil.getContType(root+path));
			ImageUtil.cut(root+path, Positions.CENTER,width, height, os);
			os.flush();
		}catch(Exception e){
			e.printStackTrace();
		}	
	}
		
	
	
	
	public static void main(String[] args){
		
	}
	
	
}
