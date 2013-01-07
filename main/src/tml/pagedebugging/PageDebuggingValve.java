package tml.pagedebugging;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import tml.pagedebugging.core.PageDebuggingContext;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * used for turn on/off the page pagedebugging tool, switch value be saved on httpSession
 * 
 * @author tminglei
 */
public class PageDebuggingValve extends ValveBase
{	
	private static String SWITCH_KEY = PageDebuggingContext.class.getName() +"."+ PageDebuggingContext.switchKey();

	@Override
	public void invoke(Request request, Response response) 
	
		throws IOException, ServletException 
	{		
		PageDebuggingContext ctx = PageDebuggingContext.currentContext();
		
		String _oldSwitch = ctx.getDebuggingSwitch(); //backup old switch
		
		try {
			ctx.setDebuggingSwitch(getTrackingSwitch(request));
			
			//--continue
			getNext().invoke( request, response );
			//--
			
		}
		finally
		{
			ctx.setDebuggingSwitch(_oldSwitch); //restore old switch
		}
	}
	
	private String getTrackingSwitch(Request request) 
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
