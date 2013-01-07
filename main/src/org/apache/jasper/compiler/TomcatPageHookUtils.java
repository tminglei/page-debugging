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
		<%	if( com.infowise.pagedebugging.PageDebuggingContext.currentContext().debug()
				&& 
				com.infowise.pagedebugging.PageDebuggingContext.currentContext().allow( pathName ) ) {
				
				com.infowise.pagedebugging.PageDebuggingContext.currentContext().enter( pathName );
		%>
			<!-- <%=com.infowise.pagedebugging.PageDebuggingContext.currentContext().currentDebuggingCode() %> - start -->
			
		<%	} %>
		*/
		
		String beforeScriptlet = "if( com.infowise.pagedebugging.PageDebuggingContext.currentContext().debug() \n" +
								 " && " +
								 " com.infowise.pagedebugging.PageDebuggingContext.currentContext().allow(\""+pathName+"\") ) { \n" +
								 
								 " com.infowise.pagedebugging.PageDebuggingContext.currentContext().enter(\""+pathName+"\"); \n";
		
		String trackingCode = "com.infowise.pagedebugging.PageDebuggingContext.currentContext().currentDebuggingCode()";
		
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
		<%	if( com.infowise.pagedebugging.PageDebuggingContext.currentContext().debug()
				&& 
				com.infowise.pagedebugging.PageDebuggingContext.currentContext().allow( pathName ) ) {
		%>
			<!-- <%=com.infowise.pagedebugging.PageDebuggingContext.currentContext().currentDebuggingCode() %> - end -->
		<%	
				com.infowise.pagedebugging.PageDebuggingContext.currentContext().exit();
			} 
		%>
		 */
		
		String beforeScriptlet = "if( com.infowise.pagedebugging.PageDebuggingContext.currentContext().debug() \n" +
								 " && " +
								 " com.infowise.pagedebugging.PageDebuggingContext.currentContext().allow(\""+pathName+"\") ) { ";
		
		String trackingCode = "com.infowise.pagedebugging.PageDebuggingContext.currentContext().currentDebuggingCode()";
		
		String afterScriptlet = "com.infowise.pagedebugging.PageDebuggingContext.currentContext().exit(); \n } \n";
		
		//--
		new Node.Scriptlet( beforeScriptlet, new Mark(parent.getStart()), parent );
		
    	new Node.TemplateText( "\n<!-- ", new Mark(parent.getStart()), parent );
    	
    	new Node.Expression( trackingCode, new Mark(parent.getStart()), parent );
    	
    	new Node.TemplateText( " (static) - end -->\n", new Mark(parent.getStart()), parent );
    	
		new Node.Scriptlet( afterScriptlet, new Mark(parent.getStart()), parent );
		
	}
	
}
