package io.candy.fis;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class StylesTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int doStartTag() {
		JspWriter out = pageContext.getOut();
		try {
			out.append(Resource.STYLE_PLACEHOLDER);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SKIP_BODY;
	}
}
