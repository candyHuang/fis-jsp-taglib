package io.candy.fis;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class RootTag extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mapDir = "/";
	private Resource resource;
	
	public int doStartTag()  {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String path = request.getSession().getServletContext().getRealPath(mapDir);
		resource = new Resource(path);
		request.setAttribute(Resource.CONTEXT_ATTR_NAME, resource);
		return EVAL_BODY_BUFFERED;
	}
	
	public int doEndTag(){
		BodyContent body = this.getBodyContent();
		String html = body.getString();
		html = resource.replace(html);
		JspWriter out = pageContext.getOut();
		try {
			out.write(html);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}
	
	
	public String getMapDir() {
		return mapDir;
	}
	public void setMapDir(String mapDir) {
		this.mapDir = mapDir;
	}
	public Resource getResource() {
		return resource;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	
	
}
