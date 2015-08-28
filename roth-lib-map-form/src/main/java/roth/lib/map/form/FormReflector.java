package roth.lib.map.form;

import roth.lib.accessor.GetPropertyNameAccessor;
import roth.lib.annotation.Property;
import roth.lib.map.MapperConfig;
import roth.lib.map.MapperReflector;

public class FormReflector extends MapperReflector
{
	protected static FormReflector instance;
	
	public FormReflector()
	{
		addGetPropertyNameAccessor(new GetPropertyNameAccessor<Property>(Property.class)
		{
			@Override
			public String getPropertyName(Property property)
			{
				if(property.form())
				{
					if(isValid(property.formName()))
					{
						return property.formName();
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
	
	public static FormReflector get()
	{
		if(instance == null)
		{
			instance = new FormReflector();
		}
		return instance;
	}
	
	public static void set(FormReflector newInstance)
	{
		instance = newInstance;
	}
	
	@Override
	public FormMapper getMapper()
	{
		return new FormMapper(this);
	}
	
	@Override
	public FormMapper getMapper(MapperConfig mapperConfig)
	{
		return new FormMapper(this, mapperConfig);
	}
	
}
