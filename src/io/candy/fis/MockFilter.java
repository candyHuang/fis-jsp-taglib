package io.candy.fis;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class MockFilter implements Filter {
	
	private static String ROUTE_CONF = "server.properties";
	
	private static String TEST_DIR = "/test/";
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request; 
		
		ServletContext servletContext = request.getServletContext();
		
		// 获取 routeMap 
		String path = servletContext.getRealPath(TEST_DIR + ROUTE_CONF);
		Map<String, String> routeMap = Util.getProperties(path);
		
		String contextPath = httpRequest.getContextPath();
		String url = httpRequest.getRequestURI();
		url = url.replaceFirst(contextPath, "");
		
		if(routeMap != null && routeMap.containsKey(url) ) {
			String dataStr = routeMap.get(url);
			String[] data = dataStr.split("\\|");
			String jspUrl = data[0];
			
			if(jspUrl != null && !"".equals(jspUrl)) {
				if(!jspUrl.startsWith("/")) {
					jspUrl = "/" + jspUrl;
				}
				// mock data
				if(data.length >= 2) {
					String jsonName = data[1];
					Map<String, Object> dataMap = Util.getJson(servletContext.getRealPath(TEST_DIR + jsonName));
					
					if(dataMap != null) {
						for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
							httpRequest.setAttribute(entry.getKey(), entry.getValue());
						}
					}
				}
				request.getRequestDispatcher(jspUrl).forward(request, response); 
				return;
			}
		}
		
		chain.doFilter(request, response);
	}
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		// 动态添加无法读取
//		ServletContext servletContext = config.getServletContext();
//		String path = servletContext.getRealPath(TEST_DIR + ROUTE_CONF);
//		routeMap = Util.getProperties(path);
	}
}
