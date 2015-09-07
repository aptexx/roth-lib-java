package roth.lib.api.cloudflare.modify;

import java.io.Serializable;
import java.util.Calendar;

import roth.lib.annotation.Entity;
import roth.lib.annotation.Property;
import roth.lib.api.cloudflare.model.Zone;

@Entity
@SuppressWarnings("serial")
public class SetDevModeResponse implements Serializable
{
	@Property(name = "zone")
	protected Zone zone;
	
	@Property(name = "expires_on")
	protected Calendar expiresOn;
	
	@Property(name = "fpurge_ts")
	protected Calendar fpurgeTs;
	
	public SetDevModeResponse()
	{
		
	}
	
	public Zone getZone()
	{
		return zone;
	}
	
	public Calendar getExpiresOn()
	{
		return expiresOn;
	}
	
	public Calendar getFpurgeTs()
	{
		return fpurgeTs;
	}
	
}
