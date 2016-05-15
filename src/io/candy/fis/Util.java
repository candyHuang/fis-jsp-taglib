package io.candy.fis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

public class Util {
	
	private static Logger logger = Logger.getLogger(Util.class);  
	
	/**
	 * 获取 Properties 文件内容
	 * @return
	 */
	public static Map<String, String> getProperties(String path) {
		Map<String, String> map = new HashMap<>();
		Properties prop = new Properties();
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(new File(path)));
			if(reader != null) {
				prop.load(reader);
				Iterator<String> it = prop.stringPropertyNames().iterator();
				while(it.hasNext()){
					String key=it.next();
					map.put(key, prop.getProperty(key));
				}
				reader.close();
			}
		} catch (IOException e) {
			logger.error(e);
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getJson(String path) {
		File file = new File(path);
		String jsonStr = "";
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
			BufferedReader reader = new BufferedReader(inputStreamReader);
			
			String line = "";  
			while((line = reader.readLine()) != null){
				jsonStr += line.trim();
			}
			reader.close();
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
		
		return JSON.parseObject(jsonStr, HashMap.class);
	}
}
