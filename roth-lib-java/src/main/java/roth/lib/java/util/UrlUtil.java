package roth.lib.java.util;

import java.net.URLDecoder;
import java.net.URLEncoder;

import roth.lib.java.Characters;

public class UrlUtil implements Characters
{
	
	protected UrlUtil()
	{
		
	}
	
	public static String encode(String value)
	{
		try
		{
			return URLEncoder.encode(value, UTF_8.toString());
		}
		catch(Exception e)
		{
			return value;
		}
	}
	
	public static String decode(String value)
	{
		try
		{
			return URLDecoder.decode(value, UTF_8.toString());
		}
		catch(Exception e)
		{
			return value;
		}
	}
	
}
