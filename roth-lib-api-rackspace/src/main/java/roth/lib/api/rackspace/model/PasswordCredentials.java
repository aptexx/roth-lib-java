package roth.lib.api.rackspace.model;

import java.io.Serializable;

import roth.lib.annotation.Property;

@SuppressWarnings("serial")
public class PasswordCredentials implements Serializable
{
	@Property(name = "username")
	protected String username;
	
	@Property(name = "password")
	protected String password;
	
	public PasswordCredentials(String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public PasswordCredentials setUsername(String username)
	{
		this.username = username;
		return this;
	}
	
	public PasswordCredentials setPassword(String password)
	{
		this.password = password;
		return this;
	}
	
}