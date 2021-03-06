package tml.pagedebugging.core;

public aspect FreemarkerPageHook {

	/**
	 * >> FreeMarker: insert pagedebugging code at first and last of the content generated by the requested resources
	 */
	void around(freemarker.core.Environment env, freemarker.core.TemplateElement rootElement)
	
			throws java.io.IOException : 
				
		//-- pointcuts
		this( env ) && args( rootElement )	
		
		&& call( void visit( freemarker.core.TemplateElement ) )
		
			&& ( 
					withincode( void process() ) || 
					
					withincode( void include( freemarker.template.Template ) ) 
					
			)
			
	{	//-- operations
		
		PageDebuggingContext ctx = PageDebuggingContext.currentContext();
		
    	String fileName = (rootElement != null && rootElement.getTemplate() != null) ? rootElement.getTemplate().getName() : null;
    	
		if( ctx.debug() && fileName != null && ctx.allow( fileName ) )
		{
			try 
			{
				ctx.enter( fileName );
				
		    	env.getOut().append( "\n<!-- " +ctx.currentDebuggingCode()+ " - start -->\n" );
		    	//--
				proceed( env, rootElement );
				//--
				env.getOut().append( "\n<!-- " +ctx.currentDebuggingCode()+ " - end -->\n" );
			}
			finally
			{
				ctx.exit();
			}
		}
		else
		{
			proceed( env, rootElement );
		}
		
	}
	
}
