package com.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DateUtil {
	
	
	/**
	 * @param fmt  yyyy-MM-dd
	 * @return
	 */
	public static String today(String fmt){
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		return sdf.format(new Date());
	}
	
	/**
	 * @param fmt yyyy-MM-dd
	 * @return
	 */
	public static String yesterday(String fmt){
		return beforeTodayByDay(fmt, -1);
	}
	
	/**
	 * @param fmt yyyy-MM-dd
	 * @param num  >0  未来       <0 过去
	 * @return	获取距今几天的日期
	 */
	public static String beforeTodayByDay(String fmt,int days){
		return beforeDateByDay(new Date(), fmt, days);
	}
	
	
	/**
	 * @param fmt yyyy-MM-dd
	 * @param months >0  未来       <0 过去
	 * @return  获取距今几月的日期
	 */
	public static String beforeTodayByMonth(String fmt,int months){
		return beforeDateByMonth(new Date(),fmt, months);		
	}
	
	
	/**
	 * @param fmt yyyy-MM-dd
	 * @param months >0  未来       <0 过去
	 * @return 获取距今几年的日期
	 */
	public static String beforeTodayByYear(String fmt,int years){
		return beforeDateByYear(new Date(), fmt, years);	
	}
	
	
	/**
	 * @param date  
	 * @param fmt	yyyy-MM-dd
	 * @param days 	>0  未来       <0 过去
	 * @return	获取距离某日期几天的日期
	 */
	public static String beforeDateByDay(Date date,String fmt,int days){
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return sdf.format(cal.getTime());		
	}
	
	public static String beforeDateByDay(String date,String fmt,int days){
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Calendar cal = Calendar.getInstance();
		try{
			cal.setTime(sdf.parse(date));
			cal.add(Calendar.DATE, days);
			result = sdf.format(cal.getTime());
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;				
	}
	
	
	/**
	 * @param date  
	 * @param fmt	yyyy-MM-dd
	 * @param days 	>0  未来       <0 过去
	 * @return	获取距离某日期几月的日期
	 */
	public static String beforeDateByMonth(Date date,String fmt,int months){
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months);
		return sdf.format(cal.getTime());		
	}
	
	public static String beforeDateByMonth(String date,String fmt,int months){
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Calendar cal = Calendar.getInstance();
		try{
			cal.setTime(sdf.parse(date));
			cal.add(Calendar.MONTH, months);
			result = sdf.format(cal.getTime());
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;				
	}
	
	
	
	/**
	 * @param date  
	 * @param fmt	yyyy-MM-dd
	 * @param days 	>0  未来       <0 过去
	 * @return	获取距离某日期几年的日期
	 */
	public static String beforeDateByYear(Date date,String fmt,int years){
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, years);
		return sdf.format(cal.getTime());		
	}
	
	public static String beforeDateByYear(String date,String fmt,int years){
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Calendar cal = Calendar.getInstance();
		try{
			cal.setTime(sdf.parse(date));
			cal.add(Calendar.YEAR, years);
			result = sdf.format(cal.getTime());
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;				
	}
	
	
	
	/**
	 * @param day  星期 1-7
	 * @return	获取上一个星期几的日期
	 */
	public static String lastDayOfWeek(String fmt, int day){
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int today = cal.get(Calendar.DAY_OF_WEEK);
		if(day>=1&&day<7){
			if(today==day+1){
				cal.add(Calendar.DATE, -7);
			}else if(today>day+1){
				cal.set(Calendar.DAY_OF_WEEK, day+1);
			}else{
				cal.set(Calendar.DAY_OF_WEEK, day+1);
				cal.add(Calendar.DATE, -7);
			}
			result = sdf.format(cal.getTime());
		}else if(day==7){
			if(today==1){
				cal.add(Calendar.DATE, -7);
			}else{
				cal.set(Calendar.DAY_OF_WEEK, 1);
			}			
			result = sdf.format(cal.getTime());
		}
		return result;
	}
	
	
	/**
	 * @param day  星期 1-7
	 * @return	获取下一个星期几的日期
	 */
	public static String nextDayOfWeek(String fmt, int day){
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int today = cal.get(Calendar.DAY_OF_WEEK);
		if(day>=1&&day<7){
			if(today==day+1){
				cal.add(Calendar.DATE, 7);
			}else if(today>day+1){
				cal.set(Calendar.DAY_OF_WEEK, day+1);
			}else{
				cal.set(Calendar.DAY_OF_WEEK, day+1);
				cal.add(Calendar.DATE, 7);
			}
			result = sdf.format(cal.getTime());
		}else if(day==7){
			if(today==1){
				cal.add(Calendar.DATE, 7);
			}else{
				cal.set(Calendar.DAY_OF_WEEK, 1);
				cal.add(Calendar.DATE, 7);
			}			
			result = sdf.format(cal.getTime());			
		}
		
		return result;
	}
	
	
	/**
	 *
	 * @return 获取两日期间相差天数 (只计算日期差, 忽略时间差)
	 */
	public static long daysBetween(Date startDate,Date endDate){
		long days = -9999;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try{
			days = (sdf.parse(sdf.format(endDate)).getTime()-sdf.parse(sdf.format(startDate)).getTime())/(60*60*24*1000);
		}catch(Exception e){
			e.printStackTrace();
		}
		return days;
	}
	
	public static long daysBetween(String startDate,String endDate,String fmt){
		long days = -9999;
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		try{
			Date start = sdf.parse(startDate);
			Date end = sdf.parse(endDate);
			days = (end.getTime() - start.getTime())/(60*60*24*1000);
		}catch(Exception e){
			e.printStackTrace();
		}
		return days;
	}
	
	
	/**
	 * @return 获取指定日期距今天数  (只计算日期差, 忽略时间差)
	 */
	public static long daysTillToday(Date date){
		return daysBetween(date, new Date());
	}
	
	public static long daysTillToday(String date,String fmt){
		long days = -9999;
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		try{
			days= daysBetween(sdf.parse(date), new Date());
		}catch(Exception e){
			e.printStackTrace();
		}
		return days;
	}
	
	
	
	/**
	 * fmt: yyyy-MM-dd
	 * @return 获取sqlDate
	 */
	public static java.sql.Date getSqlDate(String date,String fmt){
		java.sql.Date sdate = null;
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		try{
			Date udate = sdf.parse(date);
			sdate = new java.sql.Date(udate.getTime());
		}catch(Exception e){
			e.printStackTrace();
		}
		return sdate;
	}
	
	/**
	 * @return	判断日期是否符合fmt格式
	 */
	public static boolean isValidDate(String date,String fmt){
		boolean valid = false;
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		try{
			sdf.parse(date);
			valid=true;
		}catch(Exception e){
		}
		return valid;
	}
	
	
	/**
	 * @return 日期集合排序
	 */
	public static List<Date> sortDateList(List<Date> list){
		list.sort(new Comparator<Date>() {
			@Override
			public int compare(Date o1, Date o2){
				return o1.compareTo(o2);
			}
		});
		return list;
	}
	
	
	public static List<String> sortDateList(List<String> list,String fmt){
		List<String> result = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		try{
			List<Date> dates = new ArrayList<Date>();
			for(String str:list){
				dates.add(sdf.parse(str));
			}
			dates = sortDateList(dates);
			for(Date d:dates){
				result.add(sdf.format(d));
			}
		}catch(Exception e){
			e.printStackTrace();
			result =null;
		}
		return result;
	}
	
	

}
