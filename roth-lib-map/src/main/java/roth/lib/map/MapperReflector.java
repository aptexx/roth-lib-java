package roth.lib.map;

import roth.lib.reflector.EntityReflector;

public abstract class MapperReflector extends EntityReflector
{
	
	public MapperReflector()
	{
		
	}
	
	public abstract Mapper getMapper();
	public abstract Mapper getMapper(MapperConfig mapperConfig);
	
}
