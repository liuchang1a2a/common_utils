package com.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * jars: tika-core	commons-io	commons-codec
 */
public class FileUtil {
	
	/**
	 * jdk 1.8
	 * @return  获取文件扩展名格式 ： jpg,png,gif
	 */
	public static String getExt(File f){
		String ext = "";
		String name = f.getName().trim();
		if(!"".equals(name)&&name.contains(".")){
			ext = name.substring(name.lastIndexOf(".")+1,name.length());
		}
		return ext;
	}
	
	public static String getExt(String filepath){ 
		File f = new File(filepath);
		return getExt(f);
	}
	
	/**
	 * jar: tika-core
	 * @return	根据ContentType获取文件后缀名
	 */
	public static String getExtByType(String ContentType){
		String ext = "";
		try{
			MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
			MimeType type = allTypes.forName(ContentType); 
			ext = type.getExtension().substring(1);
		}catch(Exception e){
			e.printStackTrace();
		}
		return ext;
	}
	
	
	/**
	 * @return	根据流获取扩展名
	 */
	public static String getExtByStream(InputStream is){
		String ext = "";
		byte[] b = new byte[3];
		try{
			is.read(b, 0, b.length);
			if(b!=null&&b.length>0){
				String header = "";
				StringBuilder sb = new StringBuilder();
				for(int i=0;i<b.length;i++){
					int v= b[i] & 0xFF;
					String hv = Integer.toHexString(v);
					if(hv.length()<2){
						sb.append(0);
					}
					sb.append(hv);
				}
				header =sb.toString().toUpperCase().substring(0,6);
				switch(header){
					case "FFD8FF": ext = "jpg"; break;
			        case "89504E": ext = "png"; break;
			        case "474946": ext = "gif"; break;
			        case "49492A": ext = "tif"; break;
			        case "424D": ext = "bmp"; break;
			        case "414331": ext = "dwg"; break;
			        case "384250": ext = "psd"; break;
			        case "7B5C72": ext = "rtf"; break;
			        case "3C3F78": ext = "xml"; break;
			        case "3C2144": ext = "html"; break;
			        case "44656C": ext = "eml"; break;
			        case "CFAD12": ext = "dbx"; break;
			        case "214244": ext = "pst"; break;
			        case "D0CF11": ext = "xls"; break;
			        case "537461": ext = "mdb"; break;
			        case "FF5750": ext = "wpd"; break;
			        case "252150": ext = "eps"; break;
			        case "255044": ext = "pdf"; break;
			        case "AC9EBD": ext = "qdf"; break;
			        case "E38285": ext = "pwl"; break;
			        case "504B03": ext = "zip"; break;
			        case "526172": ext = "rar"; break;
			        case "574156": ext = "wav"; break;
			        case "415649": ext = "avi"; break;
			        case "2E7261": ext = "ram"; break;
			        case "2E524D": ext = "rm"; break;
			        case "000001": ext = "mpg"; break;
			        case "6D6F6F": ext = "mov"; break;
			        case "3026B2": ext = "asf"; break;
			        case "4D5468": ext = "mid"; break;
			        default: break;
				}				
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
		return ext;
	}
	
	
	/**
	 * @return  获取文件类型  	ContentType/MimeType
	 */
	public static String getContType(File f){
		return getContType(f.getAbsolutePath());
	}
	
	public static String getContType(String filepath){
		String contentType ="";
		try{
			Path path = Paths.get(filepath); 
            contentType = Files.probeContentType(path); 
		}catch(Exception e){
			e.printStackTrace();
		}
		return contentType;
	}
	
	
	/**
	 * @return	根据扩展名获取文件类型
	 */
	public static String getContTypeByExt(String ext){
		String contentType="";
		try {  
			Path path = Paths.get("ct."+ext); 
            contentType = Files.probeContentType(path); 
        } catch (Exception e) {  
            e.printStackTrace();  
        }		
		return contentType;
	}
	
	
	
	
	
	
	/**
	 * 获取文件大小, 默认保留小数点后两位
	 * @return 默认byte, unit: 'gb','mb','kb','b' 
	 */
	public static double getSize(File f,String unit){
		double size = 0;
		if(f.exists()&&f.isFile()){
			DecimalFormat df = new DecimalFormat("#.00");
			if("gb".equals(unit)){
				size = Double.valueOf(df.format((double)f.length()/(1024*1024*1024)));
			}else if("mb".equals(unit)){
				size = Double.valueOf(df.format((double)f.length()/(1024*1024)));
			}else if("kb".equals(unit)){
				size = Double.valueOf(df.format((double)f.length()/1024));
			}else{
				size = f.length();
			}			
		}		
		return size;
	}
	
	/**
	 * @param num: 小数点后保留位数
	 * @return 默认byte, unit: 'gb','mb','kb','b'
	 */
	
	public static double getSize(File f,String unit,int num){
		double size = 0;
		if(f.exists()&&f.isFile()){
			String fmt ="#.";
			if(num<=0){
				fmt = "#.00";
			}else{
				for(int i=0;i<num;i++){
					fmt+="0";
				}
			}
			DecimalFormat df = new DecimalFormat(fmt);
			if("gb".equals(unit)){
				size = Double.valueOf(df.format((double)f.length()/(1024*1024*1024)));
			}else if("mb".equals(unit)){
				size = Double.valueOf(df.format((double)f.length()/(1024*1024)));
			}else if("kb".equals(unit)){
				size = Double.valueOf(df.format((double)f.length()/1024));
			}else{
				size = f.length();
			}			
		}		
		return size;
	}	
	
	
	/**
	 * 智能显示文件大小
	 * @return
	 */
	public static String getSize(File f){ 
		String size = "";
		if(f.exists()&&f.isFile()){
			DecimalFormat df = new DecimalFormat("#.00");
			if(f.length()>=1024*1024*1024){	
				size = df.format((double)f.length()/(1024*1024*1024))+" GB";
			}else if(f.length()>=1024*1024){
				size = df.format((double)f.length()/(1024*1024))+" MB";
			}else if(f.length()>=1024){
				size = df.format((double)f.length()/1024)+" KB";
			}else{
				size = f.length()+" B";
			}			
		}	
		return size;
	}
	
	
	/**
	 * 字节流拷贝文件
	 * @return 
	 */
	public static boolean fileCopyByStream(String orgpath,String dstpath){
		boolean done = false;
		File org = new File(orgpath);
		if(!org.exists()||!org.isFile()){
			return done;
		}
		File parent = new File(dstpath).getParentFile();
		if(!parent.exists()||!parent.isDirectory()){
			parent.mkdirs();
		}
		try(
			InputStream input = new FileInputStream(orgpath);
			OutputStream output = new FileOutputStream(dstpath);
		){			
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
			done = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return done;
	}
	
	   
	/**
	 * 字符流拷贝文件 
	 * @return
	 */
	public static boolean fileCopyByReader(String orgpath,String dstpath){
		boolean done = false;
		File org = new File(orgpath);
		if(!org.exists()||!org.isFile()){
			return done;
		}
		File parent = new File(dstpath).getParentFile();
		if(!parent.exists()||!parent.isDirectory()){
			parent.mkdirs();
		}
		try(
			BufferedReader br = new BufferedReader(new FileReader(orgpath));
			BufferedWriter bw = new BufferedWriter(new FileWriter(dstpath));
		){
			String line="";
			while((line = br.readLine())!=null){
				bw.write(line+"\r\n");
				bw.flush();
			}
			done = true;			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return done;
	}
	
	
	/**
	 * @param dst: 新文件名  
	 * @return 重命名文件与源文件同路径 (文件夹)
	 */
	public static boolean rename(File org,String dst){
		boolean done = false;
		if(org.exists()){
			done = org.renameTo(new File(org.getParent()+"/"+dst));
		}
		return done;
	}
	
	/**
	 * 
	 * @param dst  文件(文件夹)剪切的目标地址
	 * @return
	 */
	public static boolean cutPaste(File org,String dst){
		boolean done = false;
		if(org.exists()){
			File target = new File(dst);
			File parent = target.getParentFile();
			if(!parent.exists()){
				parent.mkdirs();
			}
			done = org.renameTo(target);
		}
		return done;
	}
	
	
	/**
	 * 递归拷贝文件夹
	 * @return	
	 */
	public static boolean folderCopyByStream(String org,String dst){
		boolean done = false;
		File source = new File(org);
		if(source.exists()&&source.isDirectory()){
			for(File f:source.listFiles()){
				String target = dst+f.getAbsolutePath().substring(f.getParent().length());
				if(f.isDirectory()){
					new File(target).mkdir();
					folderCopyByStream(f.getAbsolutePath(), target);
				}else{
					fileCopyByStream(f.getAbsolutePath(), target);
				}
			}
			done=true;
		}		
		
		return done;
	}
	
	/**
	 * 读取文本文件内容
	 * @return
	 */
	public static String readTxtFile(File f){
		StringBuilder txt = new StringBuilder("");
		if(f.exists()&&f.isFile()&&f.length()>0){
			try(
				BufferedReader br = new BufferedReader(new FileReader(f));	
			){
				String line = "";
				while((line = br.readLine())!=null){
					txt.append(line);
				}				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}		
		return txt.toString();
	}
	
	
	/**
	 * @return  获取properties文件中属性
	 */
	public static String getProp(String pfile,String prop){
		String value = "";
		Properties props = new Properties();
		try(
			InputStream is = FileUtil.class.getClassLoader().getResourceAsStream(pfile);
		){
			props.load(is);
			value = props.getProperty(prop);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return value;		
	}
	
	
	/**
	 * jar: commons-io
	 * @root 保存文件路径
	 * @return MultipartFile转为File类型
	 */
	public static File MultipartToFile(MultipartFile file,String root){
		File f=null;
		try{
			f=new File(root+file.getName());
			FileUtils.copyInputStreamToFile(file.getInputStream(), f);
		}catch(Exception e){
			e.printStackTrace();
		}
		return f;
	}
	
	
	
	/**
	 * @return	生成新的文件名 (避免重名发生)
	 */
	public static String newFileName(String name){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String extra = sdf.format(new Date())+new Random().nextInt(100);
		if(name.contains(".")){
			String[] strs = name.split("\\.");
			String ext = "."+ strs[strs.length-1];
			return name.substring(0, name.lastIndexOf(ext))+"_"+extra+ext;
		}else{
			return name+"_"+extra;
		}		
	}
	
	/**
	 * @param 	path为文件绝对路径(包含文件名)
	 * @return	提交文件到服务器
	 */
	public static boolean upload(MultipartFile file,String path){
		boolean done =false;
		File parent = new File(path).getParentFile();
		if(!parent.exists()||!parent.isDirectory()){
			parent.mkdirs();
		}		
		try(
				InputStream input = file.getInputStream();
				OutputStream output = new FileOutputStream(path);
			){			
				byte[] buf = new byte[1024];
				int bytesRead;
				while ((bytesRead = input.read(buf)) > 0) {
					output.write(buf, 0, bytesRead);
				}			
				output.flush();
				done =true;
			}catch(Exception e){
				e.printStackTrace();
			}	
		return done;
	}
	
	
	/**
	 * @param	path 文件路径
	 * @return	获取文件名	(不包含后缀)
	 */
	public static String getNameWithoutExt(String path){
		String name = new File(path).getName();
		if(name.indexOf(".")>-1){
			name = name.substring(0,name.lastIndexOf("."));
		}		
		return name;
	}
	
	
	
	/**
	 * @param	length 限制长度(包含扩展名)
	 * @return	获取网络文件名  (过长截取)
	 */
	public static String getNameByURL(String url,int length){
		String name ="";
		if(url.indexOf("\\")>-1){
			name = url.substring(url.lastIndexOf("\\")+1);
		}else if(url.indexOf("/")>-1){
			name = url.substring(url.lastIndexOf("/")+1);
		}
		
		if(name.length()>length){
			name = name.substring(name.length()-length);
		}
		
		return name;
	}
	
	
	
	/**
	 * @param path: 文件保存路径 (包含文件名)
	 * @return	下载网络文件到本地
	 */
	public static boolean download(String url,String path){
		boolean done = false;
		InputStream is = null;
		OutputStream os = null;
		try{
			URL weburl = new URL(url);
			URLConnection conn = weburl.openConnection();
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0(compatible;MSIE 5.0;Windows NT;DigExt)");
			conn.setConnectTimeout(5*1000);
			long size = conn.getContentLengthLong();
			int unit = 1024;
			if(size>1024*1024){		//大于1M, 修改下载速度
				unit = 1024*1024;
			}
			byte[] bs = new byte[unit];
			is = conn.getInputStream();
			os = new FileOutputStream(path);
			int len;
			while((len=is.read(bs))!=-1){
				os.write(bs,0,len);
			}			
			done = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			 try {
				if(os!=null){
					os.close();
				}
				if(is!=null){
					is.close();
				}					
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		}
		
		
		return done;
	}
	
	
	
	/**
	 * @return	base64加密
	 */
	public static String base64Encode(File f){
		String base64="";
		try(
			InputStream is = new FileInputStream(f);
		){
			byte[] bytes = new byte[is.available()];
			is.read(bytes);
			is.close();
			base64 = new BASE64Encoder().encode(bytes);
		}catch(Exception e){
			e.printStackTrace();
		}		
		return base64;
	}
	
	
	/**
	 * @param str	base64加密字符串
	 * @param path	保存路径(包含文件名)
	 * @return	base64解密
	 */
	public static File base64Decode(String str, String path){
		File file = new File(path);
		File parent = new File(path).getParentFile();
		if(!parent.exists()||!parent.isDirectory()){
			parent.mkdirs();
		}		
		try(
			FileOutputStream fos = new FileOutputStream(file);
		){
			byte[] bytes = new BASE64Decoder().decodeBuffer(str);
			fos.write(bytes);		
		}catch(Exception e){
			e.printStackTrace();
			file = null;
		}
		return file;
	}
	
	
	// 拆分文件
	
	
	public static List<String> split(File f, int size){
		return split(f, size, null);
	}
	
	
	
	/**
	 * @param f	被拆分文件
	 * @param size	拆分单个文件大小
	 * @param extension	拆分文件后缀名
	 * @return	拆分大文件为多个指定大小的小文件
	 */
	public static List<String> split(File file, int size,String extension){
		List<String> paths = new ArrayList<String>();
		if(file.exists()&&file.isFile()){
			String org = file.getName();
			org = org.contains(".")?org.substring(0,org.lastIndexOf(".")):org;
			String ext = (extension==null)?getExt(file):extension;		
			String parent = file.getParent();
			try(
				InputStream in = new FileInputStream(file);
			){
				int len = -1;
				int idx =0;
				byte[] bytes = new byte[size];
				while((len=in.read(bytes))!=-1){
					String child = parent+File.separator+org+"_"+idx+"."+ext;
					OutputStream os = new FileOutputStream(new File(child));
					os.write(bytes,0,len);
					os.close();
					idx++;
					paths.add(child);
				}		
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}		
		
		
		return paths;
	}
	
	
	
	
	
	public static List<String> split(String path,int num){
		return split(path, num, null);
	}
	
	/**
	 * @param path	被拆分文件路径(含文件名)
	 * @param num	拆分个数
	 * @param ext 	拆分文件后缀名 (.tmp, etc)
	 * @return	拆分大文件为指定个数文件
	 */
	public static List<String> split(String path,int num,String extension){
		List<String> paths = new ArrayList<String>();
		File file = new File(path);
		if(file.exists()&&file.isFile()){
			String org = file.getName();
			org = org.contains(".")?org.substring(0,org.lastIndexOf(".")):org;
			String ext = (extension==null)?getExt(file):extension;		
			String parent = file.getParent();
			try(
				InputStream in = new FileInputStream(file);
			){
				int length = (int)file.length();
				int size = length/num+length%num;
				int len = -1;
				int idx =0;
				byte[] bytes = new byte[size];
				while((len=in.read(bytes))!=-1){
					String child = parent+File.separator+org+"_"+idx+"."+ext;
					OutputStream os = new FileOutputStream(new File(child));
					os.write(bytes,0,len);
					os.close();
					idx++;
					paths.add(child);
				}		
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}		
		
		return paths;
	}	
	
	
	
	
	/**
	 * @param paths	多文件路径(包含文件名)
	 * @param dst	合并文件路径(含文件名)
	 * @return	合并多个文件
	 */
	public static File migrate(List<String> paths,String dst){
		File file = new File(dst);
		File parent = file.getParentFile();
		if(!parent.exists()||!parent.isDirectory()){
			parent.mkdirs();
		}
		try(
			FileOutputStream fos = new FileOutputStream(file);
		){
			for(String path:paths){
				FileInputStream fis = new FileInputStream(path);
				int len =-1;
				byte[] buf = new byte[1024];
				while( (len= fis.read(buf))!=-1 ){  
		            fos.write(buf, 0, len);  
		        }
				fis.close();
			}			
			fos.flush();
		}catch(Exception e){
			file = null;
			e.printStackTrace();			
		}
		
		
		return file;
	}
	
	/**
	 * @param paths
	 * @param dst
	 * @return
	 */
	public static boolean zip(List<String> paths,String dst){
		boolean done =  false;
		List<File> files = new ArrayList<>();
		for(String path:paths){
			files.add(new File(path));
		}
		File out = new File(dst);
		File parent = out.getParentFile();
		if(!parent.exists()||!parent.isDirectory()){
			parent.mkdirs();
		}
		try{
			done = zip(files,new FileOutputStream(out),true);	
		}catch(Exception e){
			e.printStackTrace();
		}
		return done;
	}
	
	/**
	 * @param files	需要压缩的文件/文件夹 	out	输出流(文件流)
	 * @param keepFiles	是否删除源文件/文件夹
	 * @return	压缩文件/文件夹为zip/rar
	 */
	public static boolean zip(List<File> files, OutputStream out, boolean keepFiles){
		boolean done = false;
		try(
			ZipOutputStream zos = new ZipOutputStream(out);
		){
			for(File file:files){
				if(file.isFile()){	// 文件
					int size = (file.length()>100*1024*1024)?10*1024*1024:1024*1024;
					byte[] buf = new byte[size];
					zos.putNextEntry(new ZipEntry(file.getName()));
					int len;
					FileInputStream fis = new FileInputStream(file);
					while((len=fis.read(buf))!=-1){
						zos.write(buf, 0, len);
					}
					zos.closeEntry();
					fis.close();
					if(!keepFiles){
						file.delete();
					}
				}else{
					zipDir(file,zos,file.getName(),keepFiles);					
				}				
			}			
			done = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return done;
	}
	
	
	
	/**
	 * @return	递归压缩文件夹
	 */
	private static boolean zipDir(File dir,ZipOutputStream zos,String name, boolean keepFiles){
		boolean done = false;
		int size = (dir.length()>100*1024*1024)?10*1024*1024:1024*1024;
		byte[] buf = new byte[size];
		try{
			if(dir.isFile()){
				zos.putNextEntry(new ZipEntry(name));
				int len;
				FileInputStream fis = new FileInputStream(dir);
				while((len=fis.read(buf))!=-1){
					zos.write(buf,0,len);
				}
				zos.closeEntry();
				fis.close();
			}else{
				File[] listFiles = dir.listFiles();
				if(listFiles==null||listFiles.length==0){
					zos.putNextEntry(new ZipEntry(name+"/"));
					zos.closeEntry();
				}else{
					for(File file:listFiles){
						zipDir(file,zos,name+"/"+file.getName(),keepFiles);
					}
				}				
			}
			if(!keepFiles){
				dir.delete();
			}
			done = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return done;
	}
	
	
	
	/**
	 * @return	删除文件/文件夹
	 */
	public static boolean delete(String path){
		boolean done = false;
		File file = new File(path);
		try{
			if(file.isFile()){
				file.delete();
			}else{
				File[] files = file.listFiles();
				if(files==null||files.length==0){
					file.delete();
				}else{
					for(File f:files){
						delete(f.getAbsolutePath());
					}
					file.delete();
				}
			}
			if(!file.exists()){
				done = true;
			}			
		}catch(Exception e){
			e.printStackTrace();
		}		
		return done;
	}
	
	
	
	public static boolean resumeDownload(String url,File dst, int eachSize){
		boolean done = false;
		try{
			URL link = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) link.openConnection();
			int length = conn.getContentLength();
			Map<String,String> config = new HashMap<String, String>();
			if(length>1*1024*1024*1024){
				config.put("unit", "10");
			}else if(length>100*1024*1024){
				config.put("unit", "1");
			}else if(length>10*1024*1024){
				config.put("unit", "0.5");
			}else{
				config.put("unit", "0.1");
			}			
			done = resumeDownload(url, dst, eachSize, null, null, config);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return done;
	}
	
	
	/**
	 * @param	url 下载地址	dst 预保存文件	eachSize 单线程处理字节数
	 * @param	params	连接参数	headers	请求头	
	 * @param	config	连接配置 {	timeout: 等待多线程结束超时,	method: get/post, ReadTimeout: 读取数据超时, ConnectTimeout: 连接超时 , 	charset 参数字符集,	  unit 每次下载单元(M) }
	 * @return	断点续传(多线程-下载)
	 */
	public static boolean resumeDownload(String url,File dst, int eachSize, Map<String,String> params,Map<String,String> headers,Map<String,String> config){
		boolean done = false;
		try{
			URL link = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) link.openConnection();
			int length = conn.getContentLength();
			File parent = dst.getParentFile();
			if(!parent.exists()||!parent.isDirectory()){
				parent.mkdirs();
			}
			RandomAccessFile raf = new RandomAccessFile(dst, "rwd");
			raf.setLength(length);
			raf.close();
			int threadNum = length/eachSize==0?1:length/eachSize+1;
			final CountDownLatch latch = new CountDownLatch(threadNum);
			for(int id=1;id<=threadNum;id++){
				int startPos = (id-1)*eachSize;
				int endPos = (id*eachSize)-1;
				if(threadNum==id){
					endPos=length;
				}
				new Thread(new ResumeThread(id, startPos, endPos, url, dst.getAbsolutePath(), params, headers, config, latch)).start();
			}
			int timeout = (config!=null&&config.containsKey("timeout"))?Integer.valueOf(config.get("timeout")):3600*1000;
			if(latch.await(timeout,TimeUnit.SECONDS)){
				done = true;
				// 判断是否有子线程存在中断异常
				for(int id=1;id<=threadNum;id++){
					String name = dst.getName();
					String tmpname =name.indexOf(".")>-1?name.split("\\.")[0]+"_tmp_"+id:name+"_tmp_"+id;
					String tmppath =dst.getParent()+File.separator+tmpname;
					if(new File(tmppath).exists()){
						done = false;
					}
				}		
				System.out.println("下载完成");
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return done;
	}
	
	
	
	public static boolean resumeDownload(String url,String path,int threadNum){
		boolean done = false;
		try{
			URL link = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) link.openConnection();
			int length = conn.getContentLength();
			Map<String,String> config = new HashMap<String, String>();
			if(length>1*1024*1024*1024){
				config.put("unit", "10");
			}else if(length>100*1024*1024){
				config.put("unit", "1");
			}else if(length>10*1024*1024){
				config.put("unit", "0.5");
			}else{
				config.put("unit", "0.1");
			}			
			done = resumeDownload(url, path, threadNum, null, null, config);
		}catch(Exception e){
			e.printStackTrace();
		}
		return done;
	}
	
	/**
	 * @param	url 下载地址	path 保存路径(含文件名)	threadNum	开启线程数
	 * @param	params	连接参数	headers	请求头	
	 * @param	config	连接配置 {	timeout: 等待多线程结束超时,	method: get/post, ReadTimeout: 读取数据超时, ConnectTimeout: 连接超时 , 	charset 参数字符集,	  unit 每次下载单元(M) }
	 * @return	断点续传(多线程-下载)
	 */
	public static boolean resumeDownload(String url,String path,int threadNum,Map<String,String> params,Map<String,String> headers,Map<String,String> config){
		boolean done = false;
		try{
			URL link = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) link.openConnection();
			int length = conn.getContentLength();
			File parent = new File(path).getParentFile();
			if(!parent.exists()||!parent.isDirectory()){
				parent.mkdirs();
			}
			RandomAccessFile raf = new RandomAccessFile(path, "rwd");
			raf.setLength(length);
			raf.close();
			int eachSize = length/threadNum;
			final CountDownLatch latch = new CountDownLatch(threadNum);
			for(int id=1;id<=threadNum;id++){
				int startPos = (id-1)*eachSize;
				int endPos = (id*eachSize)-1;
				if(threadNum==id){
					endPos=length;
				}
				new Thread(new ResumeThread(id, startPos, endPos, url, path, params, headers, config, latch)).start();
			}
			int timeout = (config!=null&&config.containsKey("timeout"))?Integer.valueOf(config.get("timeout")):3600*1000;
			if(latch.await(timeout,TimeUnit.SECONDS)){
				done = true;
				// 判断是否有子线程存在中断异常
				for(int id=1;id<=threadNum;id++){
					String name = new File(path).getName();
					String tmpname =name.indexOf(".")>-1?name.split("\\.")[0]+"_tmp_"+id:name+"_tmp_"+id;
					String tmppath = parent.getAbsolutePath()+File.separator+tmpname;
					if(new File(tmppath).exists()){
						done = false;
					}
				}		
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return done;
	}
	
	
	/**
	 *@version	断点续传-子线程
	 */
	static class ResumeThread implements Runnable{
		private int id;
		private int startPos;
		private int endPos;
		private	String url;
		private String path;	//临时文件保存路径
		private Map<String,String> params;
		private Map<String,String> headers;
		private Map<String,String> config;
		private final CountDownLatch latch;
		
		public ResumeThread(int id, int startPos, int endPos, String url,
				String path, Map<String, String> params,
				Map<String, String> headers, Map<String, String> config,CountDownLatch latch) {
			super();
			this.id = id;
			this.startPos = startPos;
			this.endPos = endPos;
			this.url = url;
			this.path = path;
			this.params = params;
			this.headers = headers;
			this.config = config;
			this.latch =latch;
		}

		@Override
		public void run() {
			String method = (this.config!=null&&this.config.containsKey("method"))?this.config.get("method").toUpperCase():"GET";
			int ReadTimeout = (this.config!=null&&this.config.containsKey("ReadTimeout"))?Integer.valueOf(this.config.get("ReadTimeout")):5000;
			int ConnectTimeout = (this.config!=null&&this.config.containsKey("ConnectTimeout"))?Integer.valueOf(this.config.get("ConnectTimeout")):5000;
			String Charset = (this.config!=null&&this.config.containsKey("charset"))?this.config.get("charset").toUpperCase():"UTF-8";
			double unit = (this.config!=null&&this.config.containsKey("unit"))?Double.valueOf(this.config.get("unit")):10;
			RandomAccessFile raf = null;
			InputStream is = null;
			try{
				URL link = new URL(this.url);
				File org = new File(this.path);
				String name = org.getName();
				String tmpname =name.indexOf(".")>-1?name.split("\\.")[0]+"_tmp_"+this.id:name+"_tmp_"+this.id;
				String tmppath = org.getParent()+File.separator+tmpname;
				HttpURLConnection conn = (HttpURLConnection)link.openConnection();
				conn.setRequestMethod(method);
				if("POST".equals(method)){		//POST不能使用缓存
					conn.setUseCaches(false);
				}
				conn.setInstanceFollowRedirects(true);
				conn.setReadTimeout(ReadTimeout);
				conn.setConnectTimeout(ConnectTimeout);
				if(headers!=null&&!headers.isEmpty()){
					for(String key:headers.keySet()){
						conn.setRequestProperty(key, headers.get(key));
					}
				}
				File tmp = new File(tmppath);
				if(tmp.exists()&&tmp.length()>0){
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tmp)));
					String savePos = br.readLine();
					if(savePos!=null&&savePos.length()>0){
						startPos = Integer.valueOf(savePos);
					}
					br.close();
				}
				conn.setRequestProperty("User-Agent", "Mozilla/4.0 compatible; MSIE 5.0;Windows NT; DigExt)");
				conn.setRequestProperty("Range", "bytes="+startPos+"-"+endPos);
				
				if(params!=null&&!params.isEmpty()){
					StringBuffer content = new StringBuffer("");
					for(String param: params.keySet()){
						content.append(param+"="+URLEncoder.encode(params.get(param),Charset));
						content.append("&");
					}
					conn.connect();
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.writeBytes(content.substring(0, content.length()-1));
					out.flush();
					out.close();
				}
				
				raf = new RandomAccessFile(this.path, "rwd");
				raf.seek(startPos);
				is = conn.getInputStream();
				byte[] bytes = new byte[(int)(1024*1024*unit)];
				int len = -1;
				int newPos = startPos;
				while((len=is.read(bytes))!=-1){
					raf.write(bytes,0,len);
					RandomAccessFile raf_tmp = new RandomAccessFile(tmp, "rwd");
					String savePoint = String.valueOf(newPos+=len);
					raf_tmp.write(savePoint.getBytes());
					raf_tmp.close();
				}
				tmp.delete();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				this.latch.countDown();		//防止主线程死锁等待
				try{
					if(is!=null)is.close();
					if(raf!=null) raf.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}	
	}
	
	
	
	/**
	 * @return CRC校验
	 * @info 多用于大文件分片上传校验
	 * 计算接收完毕的文件crc校验码，和在请求阶段上传的总文件的CRC校验码进行比对。一致则该文件成功接收
	 */
	public static String getCrc(InputStream is){
		String result = "";
		byte[] buffer = new byte[1024*1024];
		try{
			CRC32 crc32 = new CRC32();
			if(is!=null){
				int len = -1;
				while((len=is.read(buffer))!=-1){
					crc32.update(buffer,0,len);					
				}
				result = String.valueOf(crc32.getValue());
			}			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(is!=null) is.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	
	
	/**
	 * @return	MD5校验 (JAVA自带)
	 * @info	多用于小文件上传校验, 耗时长
	 */
	public static String getMd5(InputStream is){
		String result = "";
		try{
			if(is!=null){
				final char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', 
			            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' }; 
				byte[] buffer = new byte[1024*1024];
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				int len = -1;
				while((len=is.read(buffer))!=-1){
					md5.update(buffer, 0, len);
				}
				byte[] data = md5.digest();
				StringBuilder sb = new StringBuilder(data.length * 2);
				for(int i=0;i<data.length;i++){
					sb.append(hexChar[(data[i] & 0xf0) >>> 4]);
					sb.append(hexChar[data[i] & 0x0f]);
				}
				result = sb.toString();
			}			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(is!=null) is.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	
	
	/**
	 * @jar		commons-codec
	 * @return	Md5校验
	 */
	public static String getMd5Str(InputStream is){
		String result = "";
		try{
			if(is!=null){
				result = DigestUtils.md5Hex(is);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(is!=null) is.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	
	
	
	public static void main(String[] args){
		try(FileInputStream fis = new FileInputStream(new File("D://IP.png"))){
			String ext = getExtByStream(fis);
			System.out.println(ext);
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
}
