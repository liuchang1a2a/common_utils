package com.utils;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import com.alibaba.fastjson.JSONObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * @jar fastjson
 */
public class JSUtil {
	
	
	/**
	 * @param js 文件路径(含文件名)
	 * @param func	方法名
	 * @return	调用不含参数的全局方法
	 */
	public static Object invoke(String js,String func){
		Object[] params = {};
		return invoke(js, func, params);
	}
	
	
	
	/**
	 * @param js 文件路径(含文件名)	func 方法名	params 所需参数
	 * @return	调用全局方法
	 * @ps	JS返回数字默认Double类型
	 */
	public static Object invoke(String js,String func,Object[] params){
		Object res = null;
		try(FileReader fr = new FileReader(js);){
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("javascript");
			engine.eval(fr);
			Invocable inv = (Invocable) engine;
			res = (params==null||params.length==0)?inv.invokeFunction(func):(Object)inv.invokeFunction(func,params);
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
	}
	
	
	
	/**
	 * @return	调用对象中无参方法
	 */
	public static Object invoke(String js,String obj,String func){
		return invoke(js, obj, func, null);
	}
	
	
	/**
	 * @param js 文件路径(含文件名)	obj	对象名称	 func 方法名	 params 参数
	 * @return	调用对象中定义的方法
	 */
	public static Object invoke(String js,String obj,String func,Object[] params){
		Object res = null;
		try(FileReader fr = new FileReader(js);){
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("javascript");
			engine.eval(fr);
			Object item = engine.get(obj); 
			Invocable inv = (Invocable) engine;
			res = (params==null||params.length==0)?inv.invokeMethod(item,func):(Object)inv.invokeMethod(item,func,params);
		}catch(Exception e){
			e.printStackTrace();
		}			
		return res;		
	}
	
	
	/**
	 * @param js 文件路径(含文件名)	param 变量名
	 * @return	获取变量值 (全局 基本类型)
	 */
	public static Object param(String js,String param){
		Object res = null;
		try(FileReader fr = new FileReader(js);){
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("javascript");
			engine.eval(fr);
			res = engine.get(param); 			
		}catch(Exception e){
			e.printStackTrace();
		}			
		return res;			
	}
	
	
	
	
	/**
	 * @param js 文件路径(含文件名)	obj 对象名	param 变量
	 * @return	获取对象中变量值 (基本类型)
	 */
	public static Object param(String js,String obj,String param){
		Object result = null;
		try(FileReader fr = new FileReader(js);){
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("javascript");
			engine.eval(fr);
			ScriptObjectMirror som = (ScriptObjectMirror)engine.get(obj); 
			result = som.get(param);
		}catch(Exception e){
			e.printStackTrace();
		}				
		return result;		
	}
	
	
	/**
	 * @param js 文件路径(含文件名)	arry 数组变量名
	 * @return	获取数组变量值 (全局)
	 */
	public static Object[] array(String js,String arry){
		Object[] res = null;
		try(FileReader fr = new FileReader(js);){
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("javascript");
			engine.eval(fr);
			ScriptObjectMirror som = (ScriptObjectMirror)engine.get(arry); 
			res = new Object[som.size()];
			for(int i=0;i<som.size();i++){
				res[i] = som.getSlot(i);
			}
		}catch(Exception e){
			e.printStackTrace();
		}				
		return res;
	}
	
	
	/**
	 * @param js 文件路径(含文件名)	obj 对象名	arry 数组变量名
	 * @return	获取对象中数组变量值 
	 */
	public static Object[] array(String js,String obj,String arry){
		Object[] res = null;
		try(FileReader fr = new FileReader(js);){
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("javascript");
			engine.eval(fr);
			ScriptObjectMirror som = (ScriptObjectMirror)engine.get(obj); 
			som = (ScriptObjectMirror)som.get(arry);
			res = new Object[som.size()];
			for(int i=0;i<som.size();i++){
				res[i] = som.getSlot(i);
			}
		}catch(Exception e){
			e.printStackTrace();
		}				
		return res;
	}
	
	
	/**
	 * @param js  文件路径(含文件名)	 obj  json对象变量名
	 * @return	获取json对象 (全局)
	 */
	public static JSONObject json(String js,String obj){
		JSONObject jsobject = new JSONObject();
		try(FileReader fr = new FileReader(js);){
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("javascript");
			engine.eval(fr);
			ScriptObjectMirror som = (ScriptObjectMirror)engine.get(obj); 
			for(String key:som.keySet()){
				jsobject.put(key, som.get(key));
			}
		}catch(Exception e){
			e.printStackTrace();
		}				
		return jsobject;		
	}
	
	
	
	/**
	 * @param js  文件路径(含文件名)	 obj  对象名	jso json变量名
	 * @return	获取对象中定义的json对象
	 */
	public static JSONObject json(String js,String obj,String jso){
		JSONObject jsobject = new JSONObject();
		try(FileReader fr = new FileReader(js);){
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("javascript");
			engine.eval(fr);
			ScriptObjectMirror som = (ScriptObjectMirror)engine.get(obj);
			som = (ScriptObjectMirror)som.get(jso);
			for(String key:som.keySet()){
				jsobject.put(key, som.get(key));
			}
		}catch(Exception e){
			e.printStackTrace();
		}				
		return jsobject;		
	}
	
	
	
	/**
	 * @param js 文件路径(含文件名)	jso	写入JSON对象	{"attr":"","obj":{}}
	 * @return	追加写入JSON对象
	 */
	public static boolean write(String js,JSONObject jso){
		boolean done = false;
		try(
			FileWriter fw = new FileWriter(js, true);
			BufferedWriter bw = new BufferedWriter(fw);){
			for(String key:jso.keySet()){
				bw.write("var "+key+"=");
				Object value = jso.get(key);
				if (value instanceof JSONObject) {
					bw.write(JSONObject.toJSONString(value));					
				}else{
					bw.write(String.valueOf(value));
				}
				bw.write(";");
				bw.newLine();
			}					
			done = true;
		}catch (Exception e) {
			e.printStackTrace();
		}		
		return done;
	}
	
	
	
	
	
	public static void main(String[] args){
		
	}
	
}
