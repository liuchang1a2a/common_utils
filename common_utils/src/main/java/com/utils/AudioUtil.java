package com.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.flac.FlacFileWriter;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.audio.mp3.MP3FileWriter;
import org.jaudiotagger.audio.ogg.OggFileReader;
import org.jaudiotagger.audio.ogg.OggFileWriter;
import org.jaudiotagger.audio.wav.WavFileReader;
import org.jaudiotagger.audio.wav.WavFileWriter;
import org.jaudiotagger.audio.wav.WavTag;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;

import com.alibaba.fastjson.JSONObject;

/**
 * @jar	fastjson  jaudiotagger	metaflac
 *
 */
public class AudioUtil {
	
	
	/**
	 * @jar	jaudiotagger
	 * @param path	文件路径(含文件名)
	 * @ps wav不支持tag
	 * @return	获取音频信息
	 */
	public static JSONObject info(String path){
		JSONObject result = new JSONObject();
		String ext = FileUtil.getExt(path).toLowerCase();
		try{
			JSONObject info = new JSONObject();
			JSONObject detail = new JSONObject();
			AudioFile audio = new AudioFile();
			if("mp3".equals(ext)){
				MP3File mp3 = new MP3File(path);
				MP3AudioHeader header = mp3.getMP3AudioHeader();
				info.put("duration", header.getTrackLength());	// 时长 s
				info.put("bitrate", header.getBitRate()); // 比特率
				info.put("format", header.getFormat());	// 格式
				info.put("channel", header.getChannels());	// 声道
				info.put("samplerate", header.getSampleRate()); // 采样率
				info.put("mpeg", header.getMpegLayer());  //MPEG
				info.put("startbyte", header.getMp3StartByte()); // 起始字节
				info.put("extlen", header.getPreciseTrackLength());	// 精确时长
				AbstractID3v2Tag tag = mp3.getID3v2Tag(); 
				detail.put("title", tag.getFirst(FieldKey.TITLE));	// 标题
				detail.put("genre", tag.getFirst(FieldKey.GENRE)); // 流派
				detail.put("artist", tag.getFirst(FieldKey.ARTIST));	// 歌手
				detail.put("album", tag.getFirst(FieldKey.ALBUM));	//专辑
				detail.put("album_artist", tag.getFirst(FieldKey.ALBUM_ARTIST)); // 专辑歌手
				detail.put("no", tag.getFirst(FieldKey.TRACK));	// 曲目编号
				detail.put("year", tag.getFirst(FieldKey.YEAR));	//年份
				detail.put("publisher", tag.getFirst(FieldKey.RECORD_LABEL));	//发布者
				detail.put("artist_site", tag.getFirst(FieldKey.URL_OFFICIAL_ARTIST_SITE)); //作者网站
				detail.put("comment", tag.getFirst(FieldKey.COMMENT));	// 标签
				result.put("code", "0");
				result.put("msg", "ok");
			}else if("flac".equals(ext)){				
				FlacFileReader ffr = new FlacFileReader();
				audio = ffr.read(new File(path));
				AudioHeader header = audio.getAudioHeader();
				info.put("duration", header.getTrackLength());	// 时长 s
				info.put("bitrate", header.getBitRate()); // 比特率
				info.put("format", header.getFormat());	// 格式
				info.put("channel", header.getChannels());	// 声道
				info.put("samplerate", header.getSampleRate()); // 采样率
				Tag tag = audio.getTag();
				detail.put("title", tag.getFirst(FieldKey.TITLE));	// 标题
				detail.put("genre", tag.getFirst(FieldKey.GENRE)); // 流派
				detail.put("artist", tag.getFirst(FieldKey.ARTIST));	// 歌手
				detail.put("album", tag.getFirst(FieldKey.ALBUM));	//专辑
				detail.put("album_artist", tag.getFirst(FieldKey.ALBUM_ARTIST)); // 专辑歌手
				detail.put("no", tag.getFirst(FieldKey.TRACK));	// 曲目编号
				detail.put("year", tag.getFirst(FieldKey.YEAR));	//年份
				detail.put("publisher", tag.getFirst(FieldKey.RECORD_LABEL));	//发布者
				detail.put("artist_site", tag.getFirst(FieldKey.URL_OFFICIAL_ARTIST_SITE)); //作者网站
				detail.put("comment", tag.getFirst(FieldKey.COMMENT));	// 标签
				result.put("code", "0");
				result.put("msg", "ok");
			}else if("wav".equals(ext)){
				WavFileReader wfr = new WavFileReader();
				audio = wfr.read(new File(path));
				AudioHeader header = audio.getAudioHeader();
				info.put("duration", header.getTrackLength());	// 时长 s
				info.put("bitrate", header.getBitRate()); // 比特率
				info.put("format", header.getFormat());	// 格式
				info.put("channel", header.getChannels());	// 声道
				info.put("samplerate", header.getSampleRate()); // 采样率
				result.put("code", "0");
				result.put("msg", "ok");
			}else if("ogg".equals(ext)){
				OggFileReader ofr = new OggFileReader();
				audio = ofr.read(new File(path)); 
				AudioHeader header = audio.getAudioHeader();
				info.put("duration", header.getTrackLength());	// 时长 s
				info.put("bitrate", header.getBitRate()); // 比特率
				info.put("format", header.getFormat());	// 格式
				info.put("channel", header.getChannels());	// 声道
				info.put("samplerate", header.getSampleRate()); // 采样率
				Tag tag = audio.getTag();
				detail.put("title", tag.getFirst(FieldKey.TITLE));	// 标题
				detail.put("genre", tag.getFirst(FieldKey.GENRE)); // 流派
				detail.put("artist", tag.getFirst(FieldKey.ARTIST));	// 歌手
				detail.put("album", tag.getFirst(FieldKey.ALBUM));	//专辑
				detail.put("no", tag.getFirst(FieldKey.TRACK));	// 曲目编号
				detail.put("year", tag.getFirst(FieldKey.YEAR));	//年份
				detail.put("comment", tag.getFirst(FieldKey.COMMENT));	// 标签
				result.put("code", "0");
				result.put("msg", "ok");
			}else{
				result.put("code", "-9");
				result.put("msg", "不支持当前音频格式");
			}
			info.put("detail", detail);
			info.put("ext", ext);
			result.put("info", info);
		}catch(Exception e){
			e.printStackTrace();
			result.put("code", "-1");
			result.put("msg", "解析文件失败");
		}		
		return result;
	}
	
	
	/**
	 * @jar	jaudiotagger
	 * @param path	文件路径(含文件名)	tags 标签信息{ title 标题, genre 流派, artist 歌手, album 专辑, no 曲目编号, year 年份,comment 备注}
	 * @ps wav不支持tag
	 * @return	写入标签信息
	 */
	public static boolean writeTag(String path,JSONObject tags){
		boolean done = false;
		String ext = FileUtil.getExt(path).toLowerCase();
		try{
			if("mp3".equals(ext)){
				MP3File mp3 = new MP3File(path);
				AbstractID3v2Tag tag = mp3.getID3v2Tag(); 
				if(tags.containsKey("title")){ 
					tag.setField(FieldKey.TITLE, tags.getString("title"));
				}
				if(tags.containsKey("genre")){
					tag.setField(FieldKey.GENRE, tags.getString("genre"));
				}
				if(tags.containsKey("artist")){
					tag.setField(FieldKey.ARTIST, tags.getString("artist"));
				}
				if(tags.containsKey("album")){
					tag.setField(FieldKey.ALBUM, tags.getString("album"));
				}
				if(tags.containsKey("no")){
					tag.setField(FieldKey.TRACK,tags.getString("no"));
				}
				if(tags.containsKey("year")){
					tag.setField(FieldKey.YEAR,tags.getString("year"));
				}
				if(tags.containsKey("comment")){
					tag.setField(FieldKey.COMMENT,tags.getString("comment"));
				}		
				MP3FileWriter mfw = new MP3FileWriter();
				mfw.writeFile(mp3);						
				done= true;
			}else if("flac".equals(ext)){
				FlacFileReader ffr = new FlacFileReader();
				AudioFile flac = ffr.read(new File(path));
				Tag tag = flac.getTag();
				if(tags.containsKey("title")){
					tag.setField(FieldKey.TITLE, tags.getString("title"));
				}
				if(tags.containsKey("genre")){
					tag.setField(FieldKey.GENRE, tags.getString("genre"));
				}
				if(tags.containsKey("artist")){
					tag.setField(FieldKey.ARTIST, tags.getString("artist"));
				}
				if(tags.containsKey("album")){
					tag.setField(FieldKey.ALBUM, tags.getString("album"));
				}
				if(tags.containsKey("no")){
					tag.setField(FieldKey.TRACK,tags.getString("no"));
				}
				if(tags.containsKey("year")){
					tag.setField(FieldKey.YEAR,tags.getString("year"));
				}
				if(tags.containsKey("comment")){
					tag.setField(FieldKey.COMMENT,tags.getString("comment"));
				}		
				FlacFileWriter ffw = new FlacFileWriter();
				ffw.write(flac);
				done= true;
			}else if("ogg".equals(ext)){
				OggFileReader ofr = new OggFileReader();
				AudioFile ogg = ofr.read(new File(path));
				Tag tag = ogg.getTag();
				if(tags.containsKey("title")){
					tag.setField(FieldKey.TITLE, tags.getString("title"));
				}
				if(tags.containsKey("genre")){
					tag.setField(FieldKey.GENRE, tags.getString("genre"));
				}
				if(tags.containsKey("artist")){
					tag.setField(FieldKey.ARTIST, tags.getString("artist"));
				}
				if(tags.containsKey("album")){
					tag.setField(FieldKey.ALBUM, tags.getString("album"));
				}
				if(tags.containsKey("no")){
					tag.setField(FieldKey.TRACK,tags.getString("no"));
				}
				if(tags.containsKey("year")){
					tag.setField(FieldKey.YEAR,tags.getString("year"));
				}
				if(tags.containsKey("comment")){
					tag.setField(FieldKey.COMMENT,tags.getString("comment"));
				}		
				OggFileWriter ofw = new OggFileWriter();
				ofw.write(ogg);
				done= true;			
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
		return done;
	}
	
	
	
	
	/**
	 * @param src	文件路径(含文件名)	dst	 目标文件路径(含文件名, 同后缀)
	 * @param	start 开始时间		end	 结束时间  秒
	 * @return	剪切音频 (仅支持mp3,wav)
	 */
	public static boolean cut(String src,String dst,long start,long end){
		boolean done = false;
		String ext = FileUtil.getExt(src).toLowerCase();
		File audio = new File(src);
		File target = new File(dst);
		File parent = target.getParentFile();
		if(!parent.exists()||!parent.isDirectory()){
			parent.mkdirs();
		}
		try{
			if("mp3".equals(ext)){
				MP3File mp3 = new MP3File(audio);
				MP3AudioHeader header = (MP3AudioHeader)mp3.getAudioHeader();
				long bitRateKbps = header.getBitRateAsNumber();
				long firstFrameByte = header.getMp3StartByte();
				long length = header.getTrackLength()*1000;
				long startTime = start*1000;
				if(startTime>=length) return done;
				long endTime = end*1000<=length?end*1000:length;
				long beginBitRateBpm = (bitRateKbps*1024L/8L/1000L) * startTime;
				long beginByte = firstFrameByte + beginBitRateBpm;
				long endByte = beginByte + (bitRateKbps * 1024L/8L/1000L)* (endTime-startTime);
				RandomAccessFile draf = new RandomAccessFile(target, "rw"); 
				RandomAccessFile sraf = new RandomAccessFile(audio, "rw");
				for(long i=0;i<firstFrameByte;i++){
					int m = sraf.read();
					draf.write(m);						
				}
				sraf.seek(beginByte);
				for(long i=0;i<=endByte-beginByte;i++){
					int m = sraf.read();
						draf.write(m);
				}
				sraf.close();
				draf.close();
				done = true;
			}else if("wav".equals(ext)){
				WavFileReader wfr = new WavFileReader();
				AudioHeader header =  wfr.read(audio).getAudioHeader();
				long length = header.getTrackLength();
				if(start>=length) return done;
				AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(audio);
				AudioFormat format = fileFormat.getFormat();
				float bytesPerSecond = format.getFrameSize() * format.getFrameRate()/1000;
				AudioInputStream ais = AudioSystem.getAudioInputStream(audio);
				end = end<=length?end:length;
				ais.skip((long)(start*1000*bytesPerSecond));
				long framesOfAudioToCopy = (long)((end-start)*format.getFrameRate());
				AudioInputStream shortStream = new AudioInputStream(ais,format,framesOfAudioToCopy);
				AudioSystem.write(shortStream, fileFormat.getType(), target);
				shortStream.close();
				ais.close();				
				done = true;
			}		
		}catch(Exception e){
			e.printStackTrace();
		}
		return done;
	}
	
	
	
	/**
	 * @jar	jaudiotagger
	 * @param path	文件路径(含文件名)	seconds	拆分片段时长 秒
	 * @return 拆分音频文件 (仅支持mp3/wav)
	 */
	public static List<String> split(String path,long seconds){
		String ext = FileUtil.getExt(path).toLowerCase();
		String org = FileUtil.getNameWithoutExt(path);
		File src = new File(path);
		List<String> list = new ArrayList<String>();
		try{
			if("mp3".equals(ext)){
				MP3File mp3 = new MP3File(src);
				MP3AudioHeader header = (MP3AudioHeader)mp3.getAudioHeader();
				long bitRateKbps = header.getBitRateAsNumber();
				long firstFrameByte = header.getMp3StartByte();
				long length = header.getTrackLength()*1000;
				long beginTime = 0;
				int idx = 0;
				while(beginTime<length){
					long endTime = (beginTime+seconds*1000)<=length?(beginTime+seconds*1000):length;
					long beginBitRateBpm = (bitRateKbps*1024L/8L/1000L) * beginTime;
					long beginByte = firstFrameByte + beginBitRateBpm;
					long endByte = beginByte + (bitRateKbps * 1024L/8L/1000L)* (idx==0?(endTime-beginTime):(endTime-beginTime+1000));
					String dpath = src.getParent()+File.separator+org+"_"+idx+"."+ext;
					File dst = new File(dpath);
					RandomAccessFile draf = new RandomAccessFile(dst, "rw"); 
					RandomAccessFile sraf = new RandomAccessFile(src, "rw");
					for(long i=0;i<firstFrameByte;i++){
						int m = sraf.read();
						draf.write(m);						
					}
					sraf.seek(beginByte);
					for(long i=0;i<=endByte-beginByte;i++){
						int m = sraf.read();
							draf.write(m);
					}
					sraf.close();
					draf.close();
					beginTime = endTime;
					idx++;
					list.add(dpath);
				}
				
			}else if("wav".equals(ext)){  
				WavFileReader wfr = new WavFileReader();
				AudioHeader header =  wfr.read(src).getAudioHeader();
				long length = header.getTrackLength();
				AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(src);
				AudioFormat format = fileFormat.getFormat();
				float bytesPerSecond = format.getFrameSize() * format.getFrameRate()/1000;
				long start = 0;
				int idx = 0;
				while(start<length){
					AudioInputStream ais = AudioSystem.getAudioInputStream(src);
					long end = (start+seconds)<length?(start+seconds):length;
					ais.skip((long)(start*1000*bytesPerSecond));
					long framesOfAudioToCopy = (long)((end-start)*format.getFrameRate());
					AudioInputStream shortStream = new AudioInputStream(ais,format,framesOfAudioToCopy);
					String dpath = src.getParent()+File.separator+org+"_"+idx+"."+ext;
					File dst = new File(dpath);
					AudioSystem.write(shortStream, fileFormat.getType(), dst);
					start = end;
					list.add(dpath);
					shortStream.close();
					ais.close();
					idx++;
				}			
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return list;
	}
	
		
	/**
	 * @exe ffmpeg
	 * @param src  原文件路径(含文件名)	 dst(mp3保存路径)	 ffmpeg(应用路径)
	 * @return	转换为mp3文件
	 */
	public static boolean mp3(String src,String dst,String ffmpeg){
		boolean done = false;
		String ext = FileUtil.getExt(src).toLowerCase();
		File parent = new File(dst).getParentFile();
		if(!parent.exists()||!parent.isDirectory()){
			parent.mkdirs();
		}
		List<String> command = new ArrayList<>();
		command.add(ffmpeg);
		command.add("-i");
		command.add(src);
		if("flac".equals(ext)){
			command.add("-ab");
			command.add("320k");
			command.add("-map_metadata");
			command.add("0");
			command.add("-id3v2_version");
			command.add("3");
			command.add(dst);
		}else{
//			ffmpeg -i audio.wav -acodec libmp3lame audio.mp3			
			command.add("-acodec");
			command.add("libmp3lame");
			command.add(dst);
		}
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
	
	
	
	/**
	 * @exe	metaflac
	 * @param flac 文件路径(含文件名)		cover  封面路径(含文件名) 	metaflac 应用路径
	 * @return	添加/修改专辑封面 (flac文件)
	 */
	public static boolean flacCover(String flac,String cover,String metaflac){
		boolean done = false;
		List<String> command = new ArrayList<>();
		command.add(metaflac);
		command.add("--import-picture-from="+cover);
		command.add(flac);
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
		return done;
	}
	
	
	/**
	 * 
	 * @return	添加/修改专辑封面 (mp3)
	 */
	public static boolean mp3Cover(String path,String cover){
		boolean done = false;
		File src = new File(path);
		try(FileInputStream fis = new FileInputStream(cover);){
			MP3File mp3 = (MP3File)AudioFileIO.read(src);
			AbstractID3v2Tag tag = mp3.getID3v2Tag();
			byte[] data = new byte[fis.available()];
			fis.read(data);
			Artwork artwork = Artwork.createArtworkFromFile(src);
			tag.deleteArtworkField();
			artwork.setBinaryData(data);
			tag.setField(artwork);
			mp3.setTag(tag);
			mp3.commit();
			done = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return done;
	}
	
	
	/**
	 * @exe ffmpeg
	 * @param srcs	源文件(含文件名)		dst	保存路径(含文件名)	ffmpeg	应用路径
	 * @return	合并多个音频 (仅限mp3)
	 */
	public static boolean concat(List<String> srcs,String dst,String ffmpeg){
		boolean done = false;
		List<String> command = new ArrayList<>();
		command.add(ffmpeg);
		command.add("-i");
		String cont = "\""+"concat:"+ArrayUtil.join(srcs.toArray(), "|")+"\"";
		command.add(cont);
		command.add("-acodec");
		command.add("copy");
		command.add(dst);
		done = CmdUtil.execute(command);
		return done;
	}
	
	
	
	
	
	
	
	public static void main(String[] args){
		List<String> srcs = new ArrayList<>();
		Collections.addAll(srcs,"D://music/青花瓷.wav","D://music/枫.wav"); 
		
	}
	
	
	
}
