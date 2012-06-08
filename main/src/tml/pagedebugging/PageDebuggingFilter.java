package tml.pagedebugging;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import tml.pagedebugging.core.PageDebuggingContext;

import java.io.IOException;

/**
 * used for turn on/off the page pagedebugging tool, switch value be saved on httpSession
 * 
 * @author tminglei
 */
public class PageDebuggingFilter implements Filter
{
	private static String SWITCH_KEY = PageDebuggingContext.class.getName() +"."+ PageDebuggingContext.switchKey();

	public void init(FilterConfig filterConfig) throws ServletException {}

	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response, 
			
			FilterChain chain) throws IOException, ServletException 
	{
		PageDebuggingContext ctx = PageDebuggingContext.currentContext();
		
		String _oldSwitch = ctx.getDebuggingSwitch(); //backup old switch
		
		try {
			ctx.setDebuggingSwitch(getTrackingSwitch((HttpServletRequest) request));
			
			//--continue
			chain.doFilter( request, response );
			//--
			
		}
		finally
		{
			ctx.setDebuggingSwitch(_oldSwitch); //restore old switch
		}
	}

	private String getTrackingSwitch(HttpServletRequest request)
	{
        String reqSwitchKey = "req_" + PageDebuggingContext.switchKey();
        String reqSwitchVal = request.getParameter( reqSwitchKey );
        if (reqSwitchVal != null && !reqSwitchVal.trim().isEmpty()) {
            return reqSwitchVal;
        }

		String _newSwitch = request.getParameter( PageDebuggingContext.switchKey() );
		
		if( _newSwitch != null ) 
		{
			request.getSession(true).setAttribute( SWITCH_KEY, _newSwitch );
		}
		
		if( request.getSession() != null )
		{
			_newSwitch = (String)request.getSession().getAttribute( SWITCH_KEY );
		}
		
		return _newSwitch;
	}

}
