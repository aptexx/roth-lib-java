package roth.lib.java.code.java;

@SuppressWarnings("serial")
public class JavaCatch extends JavaCode
{
	protected JavaParameter parameter;
	protected JavaBlock block;
	
	public JavaCatch()
	{
		
	}
	
	@Override
	public String toString()
	{
		return toSource(0);
	}
	
	@Override
	public String toSource(int tabs)
	{
		StringBuffer buffer = new StringBuffer();
		
		return buffer.toString();
	}
	
}
