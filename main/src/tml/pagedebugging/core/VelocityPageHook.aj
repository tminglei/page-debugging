package tml.pagedebugging.core;

public aspect VelocityPageHook {

	/**
	 * >> Velocity: insert page debugging code at first and last of the content generated by the requested resources
	 */
	void around(org.apache.velocity.Template template, java.io.Writer writer)
	
			throws java.io.IOException :
				
		//-- pointcuts
		this( template ) && args( *, writer, .. ) 
		
		&& (
				execution( void merge( *, * ) ) || 
				
				( execution( void merge( *, *, * ) ) && 
						
						! within( org.apache.velocity.Template ) 
						
				) 
			
		)
		
	{	//-- operations
		
		PageDebuggingContext ctx = PageDebuggingContext.currentContext();
		
		String fileName = (template != null) ? template.getName() : null;
		
		if( ctx.debug() && fileName != null && ctx.allow( fileName ) )
		{
			try 
			{
				ctx.enter( fileName );
				
				writer.append( "\n<!-- " +ctx.currentDebuggingCode()+ " - start -->\n" );
				//--
				proceed( template, writer );
				//--
				writer.append( "\n<!-- " +ctx.currentDebuggingCode()+ " - end -->\n" );
			}
			finally
			{
				ctx.exit();
			}
		}
		else
		{
			proceed( template, writer );
		}
		
	}
	
}
