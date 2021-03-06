package io.candy.fis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;

public class Resource {
	
	private static Logger logger = Logger.getLogger(Resource.class); 
	
	public static final String CONTEXT_ATTR_NAME = "io.candy.fis.resource";
    public static final String STYLE_PLACEHOLDER = "<!--FIS_STYLE_PLACEHOLDER-->";
    public static final String SCRIPT_PLACEHOLDER = "<!--FIS_SCRIPT_PLACEHOLDER-->";
    
    public static enum ScriptPoolRenderOrder {
        important,
        normal, 
        optional;
    }
    
    // 资源表地址
    private String mapDir;
    @SuppressWarnings("rawtypes")
    // 各子系统资源表综合
	private Map<String, Map> map;
    // 加载过的资源 id,url
    private Map<String, String> loaded;
    // 收集到的资源 type: []
    private Map<String, ArrayList<String>> collection;
    // 收集到的零散的脚本
    private Map<String, StringBuilder> scriptPool;
    
    private ScriptPoolRenderOrder[] scriptPoolRenderOrder = {
    		ScriptPoolRenderOrder.important,
            ScriptPoolRenderOrder.normal, 
            ScriptPoolRenderOrder.optional
            };
    
    
    @SuppressWarnings("rawtypes")
	public Resource(){
        this.map = new HashMap<String, Map>();
        this.loaded = new HashMap<String, String>();
        this.collection = new HashMap<String, ArrayList<String>>();
        this.scriptPool = new HashMap<String, StringBuilder>();
    }
    
    public Resource(String dir){
        this();
        this.setMapDir(dir);
    }
    
    /**
     * 
     */
    public void reset() {
        map.clear();
        loaded.clear();
        collection.clear();
        scriptPool.clear();
    }
   
    
    /**
     * 添加依赖
     * @param id
     * @return
     * @throws IOException 
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public String require(String id) throws IOException {
    	String uri = loaded.get(id);
    	if(uri != null) {
    		return uri;
    	}
    	Map<String, Map> map = this.getMap(id);
        Map<String, Map> res = map.get("res");
        Map<String, Object> info = res.get(id);
        if(info == null){
        	logger.error("missing resource [" + id + "]");
        } else {
        	uri = (String) info.get("uri");
            loaded.put(id, uri);
            
            if(info.containsKey("deps")){
                Object[] deps = ((JSONArray)info.get("deps")).toArray();
                for(Object dep:deps){
                    this.require((String) dep);
                }
            }
            
            String type = (String) info.get("type");
            ArrayList<String> list = collection.get(type);
            if(list == null){
                list = new ArrayList<String>();
                collection.put(type, list);
            }
            list.add(uri);
        }
    	return uri;
    }
    
    /**
     * 添加零散脚本
     * @param code
     * @param type
     */
    public void addScriptPool(String code, String type) {
        StringBuilder pool = scriptPool.get(type);
        if (pool == null) {
            pool = new StringBuilder();
            scriptPool.put(type, pool);
        }
        pool.append("<script type=\"text/javascript\">!function(){");
        pool.append(code);
        pool.append("}()</script>\n");
    }

    public void addScriptPool(String code) {
        addScriptPool(code, "normal");
    }
    
    /**
     * 替换输出
     * @param html
     * @return
     */
    public String replace(String html){
        html = html.replace(Resource.STYLE_PLACEHOLDER, this.render("css"));
        html = html.replace(Resource.SCRIPT_PLACEHOLDER, this.render("js") + this.renderScriptPool());
        return html;
    }
    
    private String renderScriptPool() {
        StringBuilder code = new StringBuilder();
        for (int i = 0, len = scriptPoolRenderOrder.length; i < len; i++) {
            String type = scriptPoolRenderOrder[i].name();
            StringBuilder pool = scriptPool.get(type);
            if (pool != null) {
                code.append(pool);
            }
        }
        scriptPool = new HashMap<String, StringBuilder>();
        return code.toString();
    }
    
    private String render(String type){
        return render(type, true);
    }
    
    private String render(String type, Boolean reset){
        ArrayList<String> list = this.getCollection(type);
        StringBuffer buffer = new StringBuffer();
        if(list != null){
            if(type.equals("js")){
                for(String uri : list){
                    buffer.append("<script src=\"").append(uri).append("\"></script>\n");
                }
            } else if(type.equals("css")){
                for(String uri : list){
                    buffer.append("<link rel=\"stylesheet\" href=\"").append(uri).append("\"/>\n");
                }
            }
            if(reset){
                list.clear();
            }
        }
        return buffer.toString();
    }
    
    private ArrayList<String> getCollection(String type){
        return collection.get(type);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, Map> getMap(String id) throws IOException{
        String namespace = "__global__";
        int pos = id.indexOf(':');
        if (pos != -1) {
            namespace = id.substring(0, pos);
        }
        if(!map.containsKey(namespace)){
            String filename = namespace.equals("__global__") ? "map.json" : namespace + "-map.json";
            map.put(namespace, Util.getJson(mapDir + "/" + filename));
        }
        return map.get(namespace);
    }
    
    

	public String getMapDir() {
		return mapDir;
	}

	public void setMapDir(String mapDir) {
		this.mapDir = mapDir;
	}
}
