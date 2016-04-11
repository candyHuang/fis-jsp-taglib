package io.candy.fis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONReader;

public class MockProvider {
	
	private static String SERVER_CONF = "server.properties";
	private static String TEST_DIR = "/test/";
	
	
	private Map<String, String> map = null;
	private String testDir = TEST_DIR;
	private String serverConf = SERVER_CONF;
	
	
	public MockProvider() {
		this.map = null;
		this.testDir = TEST_DIR;
		this.serverConf = SERVER_CONF;
	}
	
	public MockProvider(String testDir, String serverConf) {
		this();
		this.setTestDir(testDir);
		this.setServerConf(serverConf);
	}

	@SuppressWarnings({ "resource", "unchecked" })
	public void mock(HttpServletRequest request) {
		Map<String, String> map = this.map;
		if (map == null) {
			map = new HashMap<>();
			Properties prop = new Properties();
			InputStreamReader reader = getFileReader(request, this.testDir + SERVER_CONF);
			try {
				if(reader != null) {
					prop.load(reader);
					Iterator<String> it = prop.stringPropertyNames().iterator();
					while(it.hasNext()){
						String key=it.next();
						map.put(key, prop.getProperty(key));
					}
					reader.close();
					this.setMap(map);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		String contextPath = request.getContextPath();
		String url = request.getRequestURI();
		url = url.replaceFirst(contextPath, "");
		if(map.containsKey(url)) {
			String fileName = map.get(url);
			
            InputStreamReader reader = getFileReader(request, this.testDir + fileName);
            if(reader != null) {
            	 JSONReader json = new JSONReader(reader);
                 Map<String, Object> dataMap = json.readObject(HashMap.class);
                 for (Map.Entry<String, Object> entry : dataMap.entrySet()) { 
                 	request.setAttribute(entry.getKey(), entry.getValue());
                 }
            }
            
		}
		
	}
	
	private static InputStreamReader getFileReader(HttpServletRequest request, String fileName) {
		String path = request.getSession().getServletContext().getRealPath(fileName);
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(new File(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return reader;
	}

	public String getTestDir() {
		return testDir;
	}

	public void setTestDir(String testDir) {
		this.testDir = testDir;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public String getServerConf() {
		return serverConf;
	}

	public void setServerConf(String serverConf) {
		this.serverConf = serverConf;
	}
	
	
	
	
	
}
