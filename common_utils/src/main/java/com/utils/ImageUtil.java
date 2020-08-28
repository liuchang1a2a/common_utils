package com.utils;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.name.Rename;

import com.alibaba.fastjson.JSONObject;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;



/**
 * jars: fastjson, com.google.zxing, metadata-extractor
 */
public class ImageUtil {
	
	/**
	 * @return	判断文件是否为图片
	 */
	public static boolean isImg(File file){
		boolean valid = false;
		try{
			Image image = ImageIO.read(file);
			valid = (image!=null);
		}catch(Exception e){
		}
		return valid;
	}
	
	/**
	 * @param path	图片路径(含文件名)
	 * @return	获取图片基本信息
	 */
	public static JSONObject info(String path){
		JSONObject info = new JSONObject();
		try{
			File img = new File(path);
			BufferedImage bi= ImageIO.read(img);
			info.put("name", img.getName());
			info.put("width", bi.getWidth());
			info.put("height", bi.getHeight());
			info.put("mimetype", FileUtil.getContType(img));
			info.put("ext", FileUtil.getExt(path));
			info.put("size", img.length());
		}catch(Exception e){
			e.printStackTrace();
		}		
		
		return info;
	}
	
	
	/**
	 * @jar metadata-extractor
	 * @param path	图片路径(含文件名)
	 * @return	获取图片EXIF信息
	 */
	public static JSONObject exif(String path){
		JSONObject exif = new JSONObject();
		File img = new File(path);
		try{
			Metadata metadata = ImageMetadataReader.readMetadata(img);
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag: directory.getTags()) {
                    exif.put(tag.getTagName(), tag.getDescription());
                }
            }			
		}catch(Exception e){
			e.printStackTrace();
		}
		return exif;
	}
	
	
	/**
	 * @param path	图片路径(含文件名)
	 * @param width	最大宽度	height	最大高度
	 * @return	验证图片宽高是否合法
	 */
	public static boolean limit(String path,int width,int height){
		boolean valid = true;
		try{
			BufferedImage bi = ImageIO.read(new File(path));
			if(bi.getWidth()>width||bi.getHeight()>height){
				valid = false;
			}			
		}catch(Exception e){
			e.printStackTrace();
		}		
		return valid;
	}
	
	
	/**
	 * @param params={}
	 * width,height: 图片宽高	lnum: 干扰线数量	dnum: 噪点数量
	 * color: 文字颜色, bgcolor: 背景颜色, lcolor: 干扰线颜色	dcolor: 噪点颜色
	 * fstyle: 字体	fsize: 文字大小
	 * @param txt 二维码文本
	 * @return 生成验证码     置于template项目  		画图形，生成二维码图片
	 */
	public static BufferedImage checkCode(JSONObject params,String txt){
        int	width = params.containsKey("width")?params.getInteger("width"):200;
		int height = params.containsKey("height")?params.getInteger("height"):70;
		int lnum = params.containsKey("lnum")?params.getInteger("lnum"):4;
		int dnum = params.containsKey("dnum")?params.getInteger("dnum"):30;
		String color = params.containsKey("color")?params.getString("color"):"";//randomColor
		String bgcolor = params.containsKey("bgcolor")?params.getString("bgcolor"):"";
		String lcolor = params.containsKey("lcolor")?params.getString("lcolor"):""; //randomColor
		String dcolor = params.containsKey("dcolor")?params.getString("dcolor"):""; //randomColor
		String fstyle = params.containsKey("fstyle")?params.getString("fstyle"):"微软雅黑";
		int fsize = params.containsKey("fsize")?params.getInteger("fsize"):40;
		BufferedImage codeImg=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		
		Graphics2D graphics = (Graphics2D)codeImg.getGraphics();
		graphics.setColor("".equals(bgcolor)?Color.WHITE:String2Color(bgcolor));//设置验证码背景色
        graphics.fillRect(0, 0, width, height);//填充背景
        graphics.setFont(new Font(fstyle, Font.BOLD, fsize));
		
        int x = 10; //旋转原点的 x 坐标
		
        Random rand = new Random();
        for(int i=0;i<txt.length();i++){
        	graphics.setColor("".equals(color)?randomColor():String2Color(color));
            int degree = rand.nextInt() % 30;  //角度小于30度
            //正向旋转
            graphics.rotate(degree * Math.PI / 180, x, 45);
            graphics.drawString(txt.charAt(i)+"", x, 45);
            //反向旋转
            graphics.rotate(-degree * Math.PI / 180, x, 45);
            x += 48;        
        }
        
        for (int i = 0; i<lnum; i++) {
        	graphics.setColor("".equals(lcolor)?randomColor():String2Color(lcolor));
        	graphics.drawLine(rand.nextInt(width), rand.nextInt(height),
        			rand.nextInt(width), rand.nextInt(height));
        }
		
        for(int i=0;i<dnum;i++){
        	int x1 = rand.nextInt(width);
        	int y1 = rand.nextInt(height);
        	graphics.setColor("".equals(dcolor)?randomColor():String2Color(dcolor));
            graphics.fillRect(x1, y1, 2,2);
        }
		
		return codeImg;
	}
	
	
	/**
	 * @return	获取随机颜色
	 */
	public static Color randomColor(){
		Random rand = new Random();
		return new Color(rand.nextInt(256),rand.nextInt(256),rand.nextInt(256));
	}
	
	
	/** 
	 * @param str '#FFB400'
	 * @return	获取指定RGB颜色
	 */
	public static Color String2Color(String str) {  
        int i = Integer.parseInt(str.substring(1), 16);  
        return new Color(i);  
    }  
		
	
	/**
	 * @param params {
	 * 	text: 文字	 color: 文字颜色	fstyle: 字体	fsize: 字体大小 	
	 *  x, y: 文字坐标	  degree: 文字旋转角度
	 * }
	 * @return 图片添加文字
	 */
	public static BufferedImage markByText(File file,JSONObject params){
		String text = params.containsKey("text")?params.getString("text"):"";
		String color = params.containsKey("color")?params.getString("color"):"";
		String fstyle = params.containsKey("fstyle")?params.getString("fstyle"):"微软雅黑";
		int fsize = params.containsKey("fsize")?params.getInteger("fsize"):40;
		int x = params.containsKey("x")?params.getInteger("x"):-1;
		int y = params.containsKey("y")?params.getInteger("y"):-1;
		int degree = params.containsKey("degree")?params.getInteger("degree"):-1;
		BufferedImage img = null;
		try{
			Image orgImg = ImageIO.read(file);
			int width = orgImg.getWidth(null);
			int height = orgImg.getHeight(null);
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = img.createGraphics();
			g.drawImage(orgImg, 0, 0, width, height,null);
			g.setColor("".equals(color)?randomColor():String2Color(color));
			if(degree!=-1){
		        g.rotate(Math.toRadians(degree),(double) img.getWidth() / 2, (double) img.getHeight() / 2);
			}
			g.setFont(new Font(fstyle,Font.BOLD,fsize));
			int marklen = g.getFontMetrics(g.getFont()).charsWidth(text.toCharArray(), 0, text.length());
			if(x==-1){
				x = width -2*marklen;
			}
			if(y==-1){
				y = height-2*marklen;
			}
			g.drawString(text, x, y);
			g.dispose();
			
		}catch(Exception e){
			e.printStackTrace();
			img =null;
		}
		
		return img;
	}
	
	
	/**
	 * @param file	图片文件, icon 图标文件
	 * @param params	{clarity:透明度 0-1f,	 x, y: 图标坐标, degree: 旋转角度}
	 * @return	图片添加图标	
	 */
	public static BufferedImage markByIcon(File file,File icon,JSONObject params){
		float clarity = params.containsKey("clarity")?params.getFloat("clarity"):1f;
		int x = params.containsKey("x")?params.getInteger("x"):0;
		int y = params.containsKey("y")?params.getInteger("y"):-1;
		int degree = params.containsKey("degree")?params.getInteger("degree"):0;
		BufferedImage bi = null;
		try{
			Image ic = ImageIO.read(icon);
			int icheight = ic.getHeight(null);
			Image img = ImageIO.read(file);
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bi.createGraphics();
			y = (y==-1?(height/2-icheight/2):y);
			// 设置对线段的锯齿状边缘处理
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(img.getScaledInstance(width, height, Image.SCALE_SMOOTH),0,0,null);
			if(0!=degree){
				g.rotate(Math.toRadians(degree),(double)bi.getWidth()/2,(double)bi.getHeight()/2);
			}
			ImageIcon imgIcon = new ImageIcon(icon.getAbsolutePath());
			Image con = imgIcon.getImage();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,clarity));
			g.drawImage(con,x,y,null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			g.dispose();		
		}catch(Exception e){
			e.printStackTrace();
		}
		return bi;
	}
	
	
	// gif图添加文字
	// https://blog.csdn.net/liaoguolingxian/article/details/79036848
	
	
	
	
	/**
	 * @jar groupId: com.google.zxing 	artifactId: core,javase
	 * @param params {"content":内容, "img":logo地址, "compress": 是否压缩logo, "size":二维码尺寸, logo_width, logo_height}
	 * @return	生成QR(二维码)
	 */
	public static BufferedImage qrEncode(JSONObject params){
		String content = params.containsKey("content")?params.getString("content"):"生成二维码";
		String imgPath = params.containsKey("img")?params.getString("img"):"";
		boolean compress = params.containsKey("compress")?params.getBoolean("compress"):true;
		int size = params.containsKey("size")?params.getInteger("size"):300;
		int lwidth = params.containsKey("logo_width")?params.getInteger("logo_width"):60;
		int lheight = params.containsKey("logo_height")?params.getInteger("logo_height"):60;
		BufferedImage image = null;
		try{
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		    hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		    hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		    hints.put(EncodeHintType.MARGIN, 1);
		    BitMatrix bitMatrix = new MultiFormatWriter().encode(content,BarcodeFormat.QR_CODE, size, size, hints);
		    int width = bitMatrix.getWidth();
		    int height = bitMatrix.getHeight();
		    image = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
	        for (int x = 0; x < width; x++) {
	            for (int y = 0; y < height; y++) {
	                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
	                        : 0xFFFFFFFF);
	            }
	        }
	        
	        if (!"".equals(imgPath)) {
	        	Image src = ImageIO.read(new File(imgPath));
	            width = src.getWidth(null);
	            height = src.getHeight(null);
	            if (compress) { // 压缩LOGO    
	                if (width > lwidth) {
	                    width = lwidth;
	                }
	                if (height > lheight) {
	                    height = lheight;
	                }
	                Image img= src.getScaledInstance(width, height,
	                        Image.SCALE_SMOOTH);
	                BufferedImage tag = new BufferedImage(width, height,
	                        BufferedImage.TYPE_INT_RGB);
	                Graphics g = tag.getGraphics();
	                g.drawImage(img, 0, 0, null); // 绘制缩小后的图    
	                g.dispose();
	                src = img;
	            }
	            // 插入LOGO    
	            Graphics2D graph = image.createGraphics();
	            int x = (size - width) / 2;
	            int y = (size - height) / 2;
	            graph.drawImage(src, x, y, width, height, null);
	            Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
	            graph.setStroke(new BasicStroke(3f));
	            graph.draw(shape);
	            graph.dispose();
	        	
	        }        
	        
		}catch(Exception e){
			e.printStackTrace();
		}
		
				
		return image;
	}
	
	
	
	/**
	 * @param file	二维码文件
	 * @return	解析二维码
	 */
	public static String qrDecode(File file){
		String content = "";
		try{
			BufferedImage image = ImageIO.read(file);
			BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
	        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	        Result result;
	        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
	        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
	        result = new MultiFormatReader().decode(bitmap, hints);
	        content = result.getText();
		}catch(Exception e){
			e.printStackTrace();
		}
		return content;
	}
	
	
	/**
	 * @param src	原始图片
	 * @param angel	视频旋转度
	 * @return	根据视频旋转度调整图片	(launched by VideoUtil)
	 */
	public static BufferedImage rotate(BufferedImage src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
        int type = src.getColorModel().getTransparency();
        Rectangle rect_des = calcRotatedSize(new Rectangle(new Dimension(src_width, src_height)), angel);
        BufferedImage bi = new BufferedImage(rect_des.width, rect_des.height, type);
        Graphics2D g2 = bi.createGraphics();
        g2.translate((rect_des.width - src_width) / 2, (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return bi;
    }
	
	
	/**
	 * @param src	原始图片框架
	 * @param angel	视频旋转度
	 * @return	计算图片旋转大小
	 */
	private static Rectangle calcRotatedSize(Rectangle src, int angel) {
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }
        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);
        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new Rectangle(new Dimension(des_width, des_height));
    }
	
	
	/**
	 * @jar	thumbnail
	 * @param img 原图片	scale 缩放比例  (>1->变大，<1=缩小)	 os	输出流
	 * @return	等比例缩放
	 */
	public static boolean zoom(File img, double scale,OutputStream os){
		boolean done = false;
		try(FileInputStream fis = new FileInputStream(img);){
			Thumbnails.of(fis).scale(scale).toOutputStream(os);
			done = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return done;
	}
	
	
	/**
	 * @return	等比例缩放
	 */
	public static boolean zoom(InputStream is, double scale,OutputStream os){
		boolean done = false;
		try{
			Thumbnails.of(is).scale(scale).toOutputStream(os);
			done = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return done;
	}
	
	
	/**
	 * @jar	thumbnail
	 * @param files 批量图片	scale 缩放比例  (>1->变大，<1=缩小)		fmt	缩略文件格式	
	 * @return	批量缩放图片  (生成文件名 thumbnail.xxxx.fmt)
	 */
	public static boolean zoom(File[] files,double scale,String fmt){
		return zoom(files, scale, fmt,null);
	}
	
	
	/**
	 * @jar	thumbnail
	 * @param files 批量图片	scale 缩放比例  (>1->变大，<1=缩小)		fmt	缩略文件格式	dir 保存文件夹
	 * @return	批量缩放图片  (生成文件名 thumbnail.xxxx.fmt)
	 */
	public static boolean zoom(File[] files,double scale,String fmt,String dir){
		boolean done = false;
		try{
			Builder<File> build = Thumbnails.of(files).scale(scale).outputFormat(fmt);
			if(dir==null||"".equals(dir)){
				build.toFiles(Rename.PREFIX_DOT_THUMBNAIL);
			}else{
				build.toFiles(new File(dir),Rename.PREFIX_DOT_THUMBNAIL);
			}			
			done = true;
		}catch(Exception e){
			e.printStackTrace();
		}		
		
		return done;
	}
	
	
	
	/**
	 * @jar thumbnails
	 * @param	img 原图片路径(含文件名)		scale 图片质量比 [0-1]
	 * @param	os 结果输出流
	 * @return	调节图片质量  (尺寸不变)
	 */
	public static boolean quality(File img,double scale,OutputStream os){
		boolean done = false;
		try{
			Thumbnails.of(img).scale(1f).outputQuality(scale).toOutputStream(os);
			done = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return done;
	}
	
	
	/**
	 * @return	调节图片质量  (尺寸不变)
	 */
	public static boolean quality(InputStream is,double scale,OutputStream os){
		boolean done = false;
		try{
			Thumbnails.of(is).scale(1f).outputQuality(scale).toOutputStream(os);
			done = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return done;
	}
	
	
	/**
	 * @jar	thumbnails
	 * @param src 原图片路径   dst 保存路径	
	 * @param limitSize	限制大小(K)	accuracy 精度(递归压缩比率 [0-1])
	 * @return	压缩图片 小于指定大小
	 */
	public static boolean compress(String src,String dst, int limitSize, double accuracy){
		boolean done = false;
		try{
			File parent = new File(dst).getParentFile();
			if(!parent.exists()||!parent.isDirectory()){
				parent.mkdirs();
			}
			File img = new File(src);
			long size = img.length();
			if(size <= limitSize*1024){
				done = true;
			}else{
				BufferedImage bi = ImageIO.read(img);
				int width = bi.getWidth();
				int height = bi.getHeight();
				int desWidth = new BigDecimal(width).multiply(new BigDecimal(accuracy)).intValue();
				int desHeight = new BigDecimal(height).multiply(new BigDecimal(accuracy)).intValue();
				Thumbnails.of(src).size(desWidth, desHeight).outputQuality(accuracy).toFile(dst);
				done = compress(dst,dst,limitSize,accuracy);			
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		return done;
	}
	
	
	
	/**
	 * @param src 原图路径(含文件名)	 waterPic 水印路径(含文件名)	 os  结果输出流
	 * @return	添加水印
	 */
	public static boolean watermark(String src,String waterPic,OutputStream os,Position p,float clarity){
		boolean done = false;
		try{
			done = watermark(src, waterPic, os, p, 1.0, 1.0, clarity);
		}catch(Exception e){
			e.printStackTrace();
		}		
		return done;
	}
	
	
	
	/**
	 * @param src 原图路径(含文件名)	 waterPic 水印路径(含文件名)	 os  结果输出流
	 * @param p	水印相对位置   Positions.BOTTOM_RIGHT
	 * @param scale	缩放比例	accuracy 质量比	clarity 水印透明度
	 * @return	添加水印
	 */
	public static boolean watermark(String src,String waterPic,OutputStream os,Position p,double scale,double accuracy, float clarity){
		boolean done = false;
		try{
			Thumbnails.of(src).scale(scale).watermark(p, ImageIO.read(new File(waterPic)), clarity).outputQuality(accuracy).toOutputStream(os);
			done =true;
		}catch(Exception e){
			e.printStackTrace();
		}		
		return done;
	}
	
	
	
	/**
	 * @param src	图片路径(含文件名)  angle 旋转角度
	 * @return	旋转图片
	 */
	public static boolean rotate(String src,double angle,OutputStream os){
		boolean done = false;
		try{
			done = rotate(src, 1.0, angle, os);
		}catch(Exception e){
			e.printStackTrace();
		}		
		return done;
	}
	
	
	
	/**
	 * @param src	图片路径(含文件名)  scale  缩放比例		angle 旋转角度		os 输出流
	 * @return	旋转图片
	 */
	public static boolean rotate(String src,double scale, double angle,OutputStream os){
		boolean done = false;
		try{
			Thumbnails.of(src).scale(scale).rotate(angle).toOutputStream(os);
			done =true;
		}catch(Exception e){
			e.printStackTrace();
		}		
		return done;
	}
	
	
	
	/**
	 * @param src	原图路径(含文件名)	width, height	设置的宽高
	 * @param os	输出流
	 * @return	重置图片宽高
	 */
	public static boolean resize(String src,int width,int height,OutputStream os){
		boolean done = false;
		try{
			Thumbnails.of(src).forceSize(width, height).toOutputStream(os);
			done = true;
		}catch(Exception e){
			e.printStackTrace();
		}		
		return done;
	}
	
	
	
	/**
	 * @param src	原图路径(含文件名)	p 裁剪区域		width,height	裁剪宽高
	 * @param os	输出流
	 * @return	裁剪图片 (区域)
	 */
	public static boolean cut(String src,Position p,int width, int height,OutputStream os){
		boolean done = false;
		try{
			Thumbnails.of(src).sourceRegion(p, width, height).scale(1.0).toOutputStream(os);
			done =true;
		}catch(Exception e){
			e.printStackTrace();
		}		
		return done;
	}
	
	
	
	/**
	 * @param src	原图路径(含文件名)	x,y	裁剪起始坐标	width, height	裁剪宽高
	 * @param os	输出流
	 * @return	裁剪图片(坐标)
	 */
	public static boolean cut(String src,int x,int y,int width,int height,OutputStream os){
		boolean done =false;
		try{
			Thumbnails.of(src).sourceRegion(x, y, width, height).scale(1.0).toOutputStream(os);
			done = true;
		}catch(Exception e){
			e.printStackTrace();
		}		
		return done;
	}
	
	
	
	
	public static void main(String[] args) {
		try(OutputStream os = new FileOutputStream(new File("D://deng_new.jpg"))){
			boolean done = cut("D://deng.jpg",0,0,100,100,os);
			System.out.println(done);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
