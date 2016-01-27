package roth.lib.java.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import roth.lib.java.lang.Map;
import roth.lib.java.util.UrlUtil;

public class HttpUrl
{
	public static final int HTTP_PORT				= 80;
	public static final int HTTPS_PORT				= 443;
	public static final String PROTOCOL 			= "protocol";
	public static final String HOST 				= "host";
	public static final String PORT	 				= "port";
	public static final String PATH	 				= "path";
	public static final String PARAM		 		= "param";
	public static final String HASH	 				= "hash";
	public static final String NAME	 				= "name";
	public static final String VALUE	 			= "value";
	
	public static final String ALPHA_CHARS			= "A-Za-z";
	public static final String NUM_CHARS			= "0-9";
	public static final String ALPHA_NUM_CHARS		= ALPHA_CHARS + NUM_CHARS;
	public static final String DOT_DASH_CHARS		= ".\\-";
	public static final String SPECIAL_CHARS		= DOT_DASH_CHARS + "_~%/";
	
	public static final String PROTOCOL_CHARS		= ALPHA_NUM_CHARS;
	public static final String HOST_CHARS			= ALPHA_NUM_CHARS + DOT_DASH_CHARS;
	public static final String PORT_CHARS			= NUM_CHARS;
	public static final String PATH_CHARS			= ALPHA_NUM_CHARS + SPECIAL_CHARS;
	public static final String PARAM_CHARS			= ALPHA_NUM_CHARS + SPECIAL_CHARS + "=&";
	public static final String HASH_CHARS			= ALPHA_NUM_CHARS + SPECIAL_CHARS;
	public static final String PARAM_NAME_CHARS		= ALPHA_NUM_CHARS + SPECIAL_CHARS;
	public static final String PARAM_VALUE_CHARS	= ALPHA_NUM_CHARS + SPECIAL_CHARS;
	
	public static final Pattern URL_PATTERN;
	public static final Pattern PARAM_PATTERN;	
	
	static
	{
		URL_PATTERN = Pattern.compile
		(
			"^(?<" 			+ PROTOCOL 	+ ">[" 		+ PROTOCOL_CHARS 		+ "]+)://" +
			 "(?<" 			+ HOST 		+ ">[" 		+ HOST_CHARS 			+ "]+)" +
			 "(?::(?<" 		+ PORT 		+ ">[" 		+ PORT_CHARS 			+ "]+))?" +
			 "(?<" 			+ PATH 		+ ">/["		+ PATH_CHARS 			+ "]+)?" +
			 "(?:\\?(?<" 	+ PARAM 	+ ">[" 		+ PARAM_CHARS 			+ "]+))?" + 
			 "(?:#(?<" 		+ HASH 		+ ">[" 		+ HASH_CHARS 			+ "]+))?$"
		);
		PARAM_PATTERN = Pattern.compile
		(
			"(?<" 			+ NAME 		+ ">[" 		+ PARAM_NAME_CHARS 		+ "]+)=" + 
			"(?<" 			+ VALUE 	+ ">[" 		+ PARAM_VALUE_CHARS 	+ "]*)(?:&|$)"
		);
	}
	
	protected HttpProtocol protocol;
	protected InetAddress inetAddress;
	protected int port;
	protected String path = "/";
	protected Map<String, String> paramMap = new Map<String, String>();
	protected String hash;
	
	public HttpUrl()
	{
		
	}
	
	public HttpUrl(String url)
	{
		parse(url);
	}
	
	public HttpUrl(boolean secure)
	{
		setProtocol(secure ? HttpProtocol.HTTPS : HttpProtocol.HTTP);
	}
	
	protected void parse(String url)
	{
		Matcher matcher = URL_PATTERN.matcher(url);
		if(matcher.matches())
		{
			String protocol = matcher.group(PROTOCOL);
			if(protocol != null)
			{
				try
				{
					this.protocol = HttpProtocol.valueOf(protocol.toUpperCase());
				}
				catch(IllegalArgumentException e)
				{
					throw new HttpUrlException("invalid protocol - " + protocol, e);
				}
			}
			else
			{
				throw new HttpUrlException("invalid protocol");
			}
			String host = matcher.group(HOST);
			if(host != null)
			{
				try
				{
					inetAddress = InetAddress.getByName(host);
				}
				catch(UnknownHostException e)
				{
					throw new HttpUrlException("invalid host - " + host, e);
				}
			}
			else
			{
				throw new HttpUrlException("invalid host");
			}
			String port = matcher.group(PORT);
			if(port != null)
			{
				try
				{
					this.port = Integer.parseInt(port);
				}
				catch(NumberFormatException e)
				{
					throw new HttpUrlException("invalid port - " + port, e);
				}
			}
			else if(HttpProtocol.HTTP.equals(this.protocol))
			{
				this.port = HTTP_PORT;
			}
			else if(HttpProtocol.HTTPS.equals(this.protocol))
			{
				this.port = HTTPS_PORT;
			}
			else
			{
				throw new HttpUrlException("invalid port");
			}
			String path = matcher.group(PATH);
			this.path = path != null ? path : "/";
			String parameters = matcher.group(PARAM);
			if(parameters != null)
			{
				Matcher parameterMatcher = PARAM_PATTERN.matcher(parameters);
				while(parameterMatcher.find())
				{
					
						String name = UrlUtil.decode(parameterMatcher.group(NAME));
						String value = UrlUtil.decode(parameterMatcher.group(VALUE));
						this.paramMap.put(name, value);
					
				}
			}
			String hash = matcher.group(HASH);
			this.hash = hash;
		}
		else
		{
			throw new HttpUrlException("invalid url format - " + url);
		}
	}
	
	public HttpProtocol getProtocol()
	{
		return protocol;
	}
	
	public InetAddress getInetAddress()
	{
		return inetAddress;
	}
	
	public String getHost()
	{
		return inetAddress.getHostName();
	}
	
	public int getPort()
	{
		return port;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public Map<String, String> getParamMap()
	{
		return paramMap;
	}
	
	public String getHash()
	{
		return hash;
	}
	
	public HttpHeaders getHeaders()
	{
		return new HttpHeaders().setHeader("Host", getHost());
	}
	
	public HttpUrl setProtocol(HttpProtocol protocol)
	{
		this.protocol = protocol;
		if(HttpProtocol.HTTP.equals(protocol))
		{
			this.port = HTTP_PORT;
		}
		else if(HttpProtocol.HTTPS.equals(protocol))
		{
			this.port = HTTPS_PORT;
		}
		return this;
	}
	
	public HttpUrl setHost(String host)
	{
		try
		{
			this.inetAddress = InetAddress.getByName(host);
		}
		catch(UnknownHostException e)
		{
			throw new HttpUrlException(e);
		}
		return this;
	}
	
	public HttpUrl setInetAddress(InetAddress inetAddress)
	{
		this.inetAddress = inetAddress;
		return this;
	}
	
	public HttpUrl setPort(int port)
	{
		this.port = port;
		return this;
	}
	
	public HttpUrl setPath(String path)
	{
		this.path = path;
		return this;
	}
	
	public HttpUrl addPath(String path)
	{
		this.path += path;
		return this;
	}
	
	public HttpUrl addParam(String name, String value)
	{
		this.paramMap.put(name, value);
		return this;
	}
	
	public HttpUrl addParams(Map<String, String> paramMap)
	{
		this.paramMap.putAll(paramMap);
		return this;
	}
	
	public HttpUrl setParams(Map<String, String> paramMap)
	{
		this.paramMap = paramMap;
		return this;
	}
	
	public HttpUrl setHash(String hash)
	{
		this.hash = hash;
		return this;
	}
	
	public boolean isSecure()
	{
		return HttpProtocol.HTTPS.equals(protocol);
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(protocol.toString().toLowerCase());
		builder.append("://");
		builder.append(inetAddress.getHostName());
		builder.append(port != HTTP_PORT && port != HTTPS_PORT ? ":" + port : "");
		builder.append(toResourcePath());
		return builder.toString();
	}
	
	public String toResourcePath()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(path);
		if(!paramMap.isEmpty())
		{
			builder.append("?");
			String seperator = "";
			for(Entry<String, String> parameterEntry : paramMap.entrySet())
			{
				builder.append(seperator);
				builder.append(UrlUtil.encode(parameterEntry.getKey()));
				builder.append("=");
				if(parameterEntry.getValue() != null)
				{
					builder.append(UrlUtil.encode(parameterEntry.getValue()));
				}
				if("".equals(seperator))
				{
					seperator = "&";
				}
			}
		}
		if(hash != null)
		{
			builder.append("#");
			builder.append(hash);
		}
		return builder.toString();
	}
	
}
