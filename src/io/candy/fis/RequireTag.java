package io.candy.fis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;


public class RequireTag extends TagSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	public int doStartTag() {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		Resource resource = (Resource) request.getAttribute(Resource.CONTEXT_ATTR_NAME);
		try {
			resource.require(this.id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
