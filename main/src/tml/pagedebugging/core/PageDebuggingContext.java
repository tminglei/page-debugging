package tml.pagedebugging.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * context for the page debugging tool
 * 
 * @author tminglei
 */
public class PageDebuggingContext
{	
	private static final String _SWITCH_KEY ;
	
	private static final List<String> _EXECLUDE_PATTERNS ;
	
	private static final ThreadLocal<PageDebuggingContext> _instance = new ThreadLocal<PageDebuggingContext>();
	
	//-- instance variables
	private String _switch = null;
	
	private Stack<TraceItem> traceStack = new Stack<TraceItem>();	
	private int sequence = 100;	
	
	//--
	static {
		String _switchKey = System.getProperty( "pageDebugging.switchKey" );
		_SWITCH_KEY = ( _switchKey == null || _switchKey.trim().equals("") ) ? "pageDebugging" : _switchKey;
	}
	
	static {
		_EXECLUDE_PATTERNS = new ArrayList<String>();
		_EXECLUDE_PATTERNS.add( "WEB-INF/tags/".toLowerCase() );
		
		String _excludeds = System.getProperty( "pageDebugging.excluded" );
		if( _excludeds != null && !_excludeds.trim().equals("") ) 
		{
			_EXECLUDE_PATTERNS.clear();
			for( String pattern : _excludeds.split(",|;") ) {
				_EXECLUDE_PATTERNS.add( pattern.trim().toLowerCase() );
			}
		}
		
		String _appendExcludeds = System.getProperty( "pageDebugging.excluded_append" );
		if( _appendExcludeds != null && !_appendExcludeds.trim().equals("") ) 
		{
			for( String pattern : _appendExcludeds.split(",|;") ) {
				_EXECLUDE_PATTERNS.add( pattern.trim().toLowerCase() );
			}
		}
	}
	
	public static PageDebuggingContext currentContext()
	{
		if( _instance.get() == null ) 
		{
			_instance.set( new PageDebuggingContext() );
		}
		return _instance.get();
	}
	
	public static String switchKey()
	{
		return _SWITCH_KEY;
	}
	
	//--------------------------------------------------- instance methods ---
	
	public String getDebuggingSwitch()
	{
		return _switch;
	}
	
	public void setDebuggingSwitch(String debuggingSwitch)
	{
		_switch = (debuggingSwitch == null) ? null : debuggingSwitch.trim().toLowerCase();
	}
	
	/** 
	 * return whether the debug switch was turned on 
	 **/
	public boolean debug()
	{
		return ( "on".equals(_switch) || "true".equals(_switch) );
	}
	
	/**
	 * return whether the path name was allowed to be tagged
	 */
	public boolean allow(String fileName)
	{
		if( fileName == null ) return false;
		
		fileName = fileName.trim().toLowerCase();
		
		for( String pattern : _EXECLUDE_PATTERNS )
		{
			if( fileName.contains(pattern) ) return false;
		}
		
		return true;
		
	}
	
	/** 
	 * enter a page
	 **/
	public void enter(String fileName)
	{
		if(sequence ==1000) sequence = 100; //recycle
		
		traceStack.push( new TraceItem( fileName, sequence++ ) );
	}
	
	/** 
	 * get current pagedebugging code, eg: '[prefix] L[2]#[103]: [xxx/xxxx.jsp]'
	 **/
	public String currentDebuggingCode()
	{
		if( traceStack.isEmpty() ) return "";
		
		TraceItem item = traceStack.peek();
		return prefix() +" " +"L"+ traceStack.size() +"#"+ item.getSequence() +": "+ item.getFileName();
	}
	
	private String prefix() 
	{
		String pl = (sequence %2 == 0) ? "*" : "+";
		
		StringBuffer sb = new StringBuffer("-");
		for( int i = 0; i < traceStack.size(); i++ ) 
			sb.append( pl+"-"+pl+"-" );
		
		return sb.toString();
	}
	
	/** 
	 * exit current page 
	 **/
	public void exit()
	{
		traceStack.pop();
	}
	
	//------------------------------------------------------------- inner classes ---
	
	static class TraceItem {
		private String fileName;
		private int    sequence;
		
		TraceItem(String fileName, int sequence) {
			this.fileName = fileName;
			this.sequence = sequence;
		}
		
		public String getFileName() {
			return this.fileName;
		}
		
		public int getSequence() {
			return this.sequence;
		}
		
	}
	
}
