package roth.lib.java.map.json;

import roth.lib.java.accessor.GetPropertyNameAccessor;
import roth.lib.java.annotation.Property;
import roth.lib.java.map.MapperConfig;
import roth.lib.java.map.MapperReflector;

public class JsonReflector extends MapperReflector
{
	protected static JsonReflector instance;
	
	public JsonReflector()
	{
		super();
		addGetPropertyNameAccessor(new GetPropertyNameAccessor<Property>(Property.class)
		{
			@Override
			public String getPropertyName(Property property)
			{
				if(property.json())
				{
					if(isValid(property.jsonName()))
					{
						return property.jsonName();
					}
					else if(isValid(property.name()))
					{
						return property.name();
					}
				}
				return null;
			}
		});
	}
	
	public static JsonReflector get()
	{
		if(instance == null)
		{
			instance = new JsonReflector();
		}
		return instance;
	}
	
	public static void set(JsonReflector newInstance)
	{
		instance = newInstance;
	}
	
	@Override
	public JsonMapper getMapper()
	{
		return new JsonMapper(this);
	}
	
	@Override
	public JsonMapper getMapper(MapperConfig mapperConfig)
	{
		return new JsonMapper(this, mapperConfig);
	}
	
}