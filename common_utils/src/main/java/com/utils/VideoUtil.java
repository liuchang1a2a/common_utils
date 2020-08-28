package com.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import ws.schild.jave.AudioAttributes;
import ws.schild.jave.AudioInfo;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncodingAttributes;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.VideoAttributes;
import ws.schild.jave.VideoInfo;
import ws.schild.jave.VideoSize;

import com.alibaba.fastjson.JSONObject;

/**
 * @jar fastjson  javacv  opencv  ffmpeg  ffmpeg.exe
 * @author Francois
 *
 */
public class VideoUtil {
	
	
	/**
	 * @param path	文件路径(含文件名)
	 * @return	判断是否为video
	 */
	public static boolean isVideo(String path){
		boolean valid = false;
		if(FileUtil.getContType(path).contains("video/")){
			valid = true;
		}		
		return valid;
	}
	
	
	
	/**
	 * @jar fastjson  javacv  opencv  ffmpeg
	 * @param filepath	视频路径(含文件名)
	 * @return	获取视频信息
	 */
	public static JSONObject info(String filepath){
		JSONObject result = new JSONObject();
		try{
			FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(filepath);
			grabber.start();
			Frame frame = null;
			for(int i=0;i<5;i++){
				frame = grabber.grabImage();
			}			
			String rotate = grabber.getVideoMetadata("rotate");
			Java2DFrameConverter converter = new Java2DFrameConverter();
			BufferedImage bi = converter.getBufferedImage(frame);
			if(rotate !=null){
				bi = ImageUtil.rotate(bi, Integer.parseInt(rotate));				
			}			
			result.put("width", bi.getWidth());
			result.put("height", bi.getHeight());
			result.put("rotate", StringUtils.isEmpty(rotate)?"0":rotate);
			result.put("format", grabber.getFormat());			
			result.put("duration", grabber.getLengthInTime()/(1000*1000));
			result.put("size", new File(filepath).length());
			grabber.stop();			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	/**
	 * @jar	 ffmpeg.exe
	 * @return	剪辑视频
	 */
	public static String cut(String filepath,String start,String end,String ffmpeg){
		File file = new File(filepath);
		String savepath = file.getParent()+File.separator+FileUtil.newFileName(file.getName());
		if(cut(filepath,start,end,savepath,ffmpeg)){
			return savepath;
		}else{
			return null;
		}		
	}
	
	

	/**
	 * @jar	 ffmpeg.exe
	 * @param filepath	视频文件路径(含文件名)
	 * @param start	end	起止截取时间  HH:mm:ss
	 * @param savepath	保存路径(含文件名)	ffmpeg  执行文件路径(含文件名)
	 * @return	剪辑视频  (非特别精确)
	 */
	public static boolean cut(String filepath,String start,String end,String savepath,String ffmpeg){
		boolean done = false;
		List<String> convert = new ArrayList<>(); 
		convert.add(ffmpeg);
		convert.add("-ss");
		convert.add(start);
		convert.add("-to");
		convert.add(end);
		convert.add("-accurate_seek");
		convert.add("-i");
		convert.add(filepath);
		convert.add("-codec");
		convert.add("copy");
		convert.add("-avoid_negative_ts");
		convert.add("1");
		convert.add(savepath);
		
		ProcessBuilder builder = new ProcessBuilder();
		try{
			builder.command(convert);
			builder.redirectErrorStream(true);
			Process proc = builder.start();
			consumeInputStream(proc.getInputStream());
			consumeInputStream(proc.getErrorStream());
			int exit = proc.waitFor();
			if(exit==0){
				done = true;
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
		return done;
	}
	
	
	
	/**
	 * @return	获取截屏 (封面)
	 */
	public static boolean screenShot(String filepath,Integer mod,OutputStream os){
		return screenShot(filepath, mod, null,os);
	}
	

	/**
	 * @jar javacv  opencv  ffmpeg
	 * @param filepath	视频文件路径(含文件名)		mod	开始帧数 (建议从5帧开始,防黑屏)	fmt 图片格式
	 * @return	获取截屏 (封面)
	 */
	public static boolean screenShot(String filepath,Integer mod,String fmt,OutputStream os){
		boolean done = false;
		try{
			FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(filepath);
			grabber.start();
			Frame frame = null;
			for(int i=0;i<(mod==null?5:mod);i++){
				frame = grabber.grabImage();
			}
			
			String rotate = grabber.getVideoMetadata("rotate");
			Java2DFrameConverter converter = new Java2DFrameConverter();
			BufferedImage bi = converter.getBufferedImage(frame);
			if(rotate !=null){
				bi = ImageUtil.rotate(bi, Integer.parseInt(rotate));				
			}
			
			ImageIO.write(bi, fmt==null?"jpg":fmt, os);
			grabber.stop();
			done = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return done;
	}
	
	
	
	
	// 视频添加水印	（耗时长,需求少）
	//https://blog.csdn.net/qq_42418169/article/details/103293843
	
	
	/**
	 * @return	转为m3u8格式 (每片60s 保存全部)
	 */
	public static boolean m3u8(String src,String target,String ffmpeg){
		return 	m3u8(src, target, ffmpeg, 60, 0);
	}
	
	
	/**
	 * @param src 源文件路径(含文件名)	target	保存m3u8路径(含文件名)	ffmpeg 软件路径(.exe)	
	 * @param hls_time	每片ts片段长度(秒)	 hls_list_size 	播放列表保存最多条目 (默认 5, 全部 0)
	 * @return	转码为m3u8格式
	 */
	public static boolean m3u8(String src,String target,String ffmpeg,Integer hls_time,Integer hls_list_size){
		boolean done = false;
		if(new File(src).exists()){
			File parent = new File(target).getParentFile();
			if(!parent.exists()||!parent.isDirectory()){
				parent.mkdirs();
			}
			List<String> command = new ArrayList<>();
			command.add(ffmpeg);
			command.add("-i");
			command.add(src);
			command.add("-c:v");
			command.add("libx264");
			command.add("-hls_time");
			command.add(String.valueOf(hls_time));
			command.add("-hls_list_size");
			command.add(String.valueOf(hls_list_size));
			command.add("-c:a");
			command.add("aac");
			command.add("-strict");
			command.add("-2");
			command.add("-f");
			command.add("hls");
			command.add(target);
			ProcessBuilder builder = new ProcessBuilder();
			try{
				builder.command(command);
				builder.redirectErrorStream(true);
				Process proc = builder.start();
				consumeInputStream(proc.getInputStream());
				consumeInputStream(proc.getErrorStream());
				int exit = proc.waitFor();
				if(exit==0){
					done = true;
				}				
			}catch(Exception e){
				e.printStackTrace();
			}			
		}	
		
		return done;
	}
	
	
	private static String consumeInputStream(InputStream is){
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    String s ;
	    StringBuilder sb = new StringBuilder();
	    try{
		    while((s=br.readLine())!=null){
		        System.out.println(s);
		        sb.append(s);
		    }
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    return sb.toString();
	}
	
	//  secondSize	 每秒大小
	
	
	/**
	 * @param src 文件路径(含文件名)		dst	  保存路径(含文件名)		maxSize  最大大小
	 * @param config {
	 * 	maxBitRate: 比特率越高, 清晰度/音质越好， 文件也越大	  单位:b
	 *  maxSamplingRate:  采样率越高, 声音还原度越好, 文件越大	单位: 赫兹 hz
	 *  bitRate: 比特率越高, 清晰度/画质越好, 文件也越大  单位:b
	 *  maxFrameRate: 视频帧率越低, 视频会出现断层, 越高让人感觉越连续	 测量单位为每秒显示帧数 	FPS /	赫兹 HZ
	 *  maxWidth: 限制视频宽高
	 *  audioCodec:  音频通用代码格式
	 *  channel: 重新编码的音频流使用的声道数 (1 单声道	2  双声道/立体声  0 未设置)
	 *  volume: 音量值   (未设置 0  音量值不变  256)
	 *  videoCodec: 视频通用代码格式
	 *  format: 压缩格式	mp4..
	 *  threads: 压缩线程数	
	 * }
	 * @return	压缩视频 (指定大小)
	 */
	public static boolean compress(String src,String dst, int maxSize,JSONObject config){
		boolean done = false;
		int maxBitRate = (config!=null&&config.containsKey("maxBitRate"))?config.getInteger("maxBitRate"):128000;
		int maxSamplingRate = (config!=null&&config.containsKey("maxSamplingRate"))?config.getInteger("maxSamplingRate"):44100;
		int bitRate = (config!=null&&config.containsKey("bitRate"))?config.getInteger("bitRate"):800000;
		int maxFrameRate = (config!=null&&config.containsKey("maxFrameRate"))?config.getInteger("maxFrameRate"):20;
		int maxWidth = (config!=null&&config.containsKey("maxWidth"))?config.getInteger("maxWidth"):640; // 1024 1280
		String audioCodec = (config!=null&&config.containsKey("audioCodec"))?config.getString("audioCodec"):"aac";
		int channel = (config!=null&&config.containsKey("channel"))?config.getInteger("channel"):0;
		int volume = (config!=null&&config.containsKey("volume"))?config.getInteger("volume"):256;
		String videoCodec = (config!=null&&config.containsKey("videoCodec"))?config.getString("videoCodec"):"h264";
		String format =  (config!=null&&config.containsKey("format"))?config.getString("format"):FileUtil.getExt(dst);
		int threads = (config!=null&&config.containsKey("threads"))?config.getInteger("threads"):0;
		
		File parent = new File(dst).getParentFile();
		if(!parent.exists()||!parent.isDirectory()){
			parent.mkdirs();
		}
		try{
			File source = new File(src);
			MultimediaObject object = new MultimediaObject(source);
			AudioInfo audioInfo = object.getInfo().getAudio();
			double mb = Math.ceil(source.length()/(1024*1024));
			if(mb>maxSize){
				AudioAttributes audio = new AudioAttributes();
				audio.setCodec(audioCodec);
				if(audioInfo.getBitRate()>maxBitRate){
					audio.setBitRate(new Integer(maxBitRate));
				}
				audio.setChannels(channel==0?audioInfo.getChannels():channel);
				audio.setVolume(volume);
				if(audioInfo.getSamplingRate()>maxSamplingRate){
					audio.setSamplingRate(maxSamplingRate);					
				}
				
				VideoInfo videoInfo = object.getInfo().getVideo();
				VideoAttributes video = new VideoAttributes();
				video.setCodec(videoCodec);
				if(videoInfo.getBitRate()>bitRate){
					video.setBitRate(bitRate);
				}
				
				if(videoInfo.getFrameRate()>maxFrameRate){
					video.setFrameRate(maxFrameRate);
				}
				
				int width = videoInfo.getSize().getWidth();
				int height = videoInfo.getSize().getHeight();
				if(width > maxWidth){
					float rat = (float) width/maxWidth;
					video.setSize(new VideoSize(maxWidth, (int)(height/rat)));
				}
				
				EncodingAttributes attr = new EncodingAttributes();
				attr.setFormat(format);
				attr.setAudioAttributes(audio);
				attr.setVideoAttributes(video);
				if(threads!=0){
					attr.setEncodingThreads(threads);
				}
				Encoder encoder = new Encoder();
				encoder.encode(new MultimediaObject(source), new File(dst), attr);
				done = true;
			}			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return done;
	}
	
	
	/**
	 * @param src	视频路径(含文件名)	dst	mp3路径(含文件名)
	 * @param ffmpeg 软件路径
	 * @return	提取MP3音频
	 */
	public static boolean mp3(String src,String dst,String ffmpeg){
		boolean done = false;
		if(new File(src).exists()){
			File parent = new File(dst).getParentFile();
			if(!parent.exists()||!parent.isDirectory()){
				parent.mkdirs();
			}
			List<String> command = new ArrayList<>();
			command.add(ffmpeg);
			command.add("-i");
			command.add(src);
			command.add("-f");
			command.add("mp3");
			command.add(dst);
			ProcessBuilder builder = new ProcessBuilder();
			try{
				builder.command(command);
				builder.redirectErrorStream(true);
				Process proc = builder.start();
				consumeInputStream(proc.getInputStream());
				consumeInputStream(proc.getErrorStream());
				int exit = proc.waitFor();
				if(exit==0){
					done = true;
				}				
			}catch(Exception e){
				e.printStackTrace();
			}			
		}	
		return done;
	}
	
	
	public static void main(String[] args){
		
//		m3u8("d://video/test.mp4", "d://video/test.m3u8", "D://ffmpeg.exe", 20, 0);
		boolean done = mp3("d://video/test0.ts","d://video/test0.mp3","D://ffmpeg.exe");
		System.out.println(done);
		
	}
	
	
}
