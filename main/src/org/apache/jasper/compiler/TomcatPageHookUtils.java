package org.apache.jasper.compiler;

/**
 * help to add page debugging code to static included resources
 * (why need this class? because {@link org.apache.jasper.compiler.Node Node} cannot be accessed publicly)
 * 
 * @author tminglei
 */
public class TomcatPageHookUtils {

	/**
	 * helper method for JSP static include page debugging
	 */
	public static void tracingBegin(String pathName, Node parent)
	{
		/*
		-- added a comment to current page node, equivalent to:
		<%	if( tml.pagedebugging.core.PageDebuggingContext.currentContext().debug()
				&& 
				tml.pagedebugging.core.PageDebuggingContext.currentContext().allow( pathName ) ) {
				
				tml.pagedebugging.core.PageDebuggingContext.currentContext().enter( pathName );
		%>
			<!-- <%=tml.pagedebugging.core.PageDebuggingContext.currentContext().currentDebuggingCode() %> - start -->
			
		<%	} %>
		*/
		
		String beforeScriptlet = "if( tml.pagedebugging.core.PageDebuggingContext.currentContext().debug() \n" +
								 " && " +
								 " tml.pagedebugging.core.PageDebuggingContext.currentContext().allow(\""+pathName+"\") ) { \n" +
								 
								 " tml.pagedebugging.core.PageDebuggingContext.currentContext().enter(\""+pathName+"\"); \n";
		
		String trackingCode = "tml.pagedebugging.core.PageDebuggingContext.currentContext().currentDebuggingCode()";
		
		String afterScriptlet = "} \n";
		
		//--
		new Node.Scriptlet( beforeScriptlet, new Mark(parent.getStart()), parent );
		
    	new Node.TemplateText( "\n<!-- ", new Mark(parent.getStart()), parent );
    	
    	new Node.Expression( trackingCode, new Mark(parent.getStart()), parent );
    	
    	new Node.TemplateText( " (static) - start -->\n", new Mark(parent.getStart()), parent );
    	
		new Node.Scriptlet( afterScriptlet, new Mark(parent.getStart()), parent );
		
	}

	/**
	 * helper method for JSP static include pagedebugging
	 */
	public static void tracingEnd(String pathName, Node parent)
	{
		/*
		 -- added a comment to current page node, equivalent to:		 
		<%	if( tml.pagedebugging.core.PageDebuggingContext.currentContext().debug()
				&& 
				tml.pagedebugging.core.PageDebuggingContext.currentContext().allow( pathName ) ) {
		%>
			<!-- <%=tml.pagedebugging.core.PageDebuggingContext.currentContext().currentDebuggingCode() %> - end -->
		<%	
				tml.pagedebugging.core.PageDebuggingContext.currentContext().exit();
			} 
		%>
		 */
		
		String beforeScriptlet = "if( tml.pagedebugging.core.PageDebuggingContext.currentContext().debug() \n" +
								 " && " +
								 " tml.pagedebugging.core.PageDebuggingContext.currentContext().allow(\""+pathName+"\") ) { ";
		
		String trackingCode = "tml.pagedebugging.core.PageDebuggingContext.currentContext().currentDebuggingCode()";
		
		String afterScriptlet = "tml.pagedebugging.core.PageDebuggingContext.currentContext().exit(); \n } \n";
		
		//--
		new Node.Scriptlet( beforeScriptlet, new Mark(parent.getStart()), parent );
		
    	new Node.TemplateText( "\n<!-- ", new Mark(parent.getStart()), parent );
    	
    	new Node.Expression( trackingCode, new Mark(parent.getStart()), parent );
    	
    	new Node.TemplateText( " (static) - end -->\n", new Mark(parent.getStart()), parent );
    	
		new Node.Scriptlet( afterScriptlet, new Mark(parent.getStart()), parent );
		
	}
	
}
