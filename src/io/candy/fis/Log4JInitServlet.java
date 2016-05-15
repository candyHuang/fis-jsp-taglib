package io.candy.fis;

import java.io.File;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

public class Log4JInitServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	public Log4JInitServlet() {  
        super();  
    }  
	
	public void init(ServletConfig config) throws ServletException {  
        String log4jLocation = config.getInitParameter("log4j-properties-location");  
        ServletContext sc = config.getServletContext();  

        if (log4jLocation == null) {  
            BasicConfigurator.configure();  
        } else {  
            String webAppPath = sc.getRealPath("/");  
            String log4jProp = webAppPath + log4jLocation;  
            File file = new File(log4jProp);  
            if (file.exists()) {  
                PropertyConfigurator.configure(log4jProp);  
            } else {  
                BasicConfigurator.configure();  
            }  
        }  
        super.init(config);  
    }  
}
