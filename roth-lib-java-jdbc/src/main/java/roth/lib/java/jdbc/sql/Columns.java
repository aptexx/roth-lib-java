package roth.lib.java.jdbc.sql;

import roth.lib.java.lang.List;

@SuppressWarnings("serial")
public abstract class Columns extends Sql
{
	protected List<Column> columns = new List<Column>();
	
	protected Columns()
	{
		
	}
	
	public Columns setColumns(List<Column> columns)
	{
		this.columns = columns;
		return this;
	}
	
	public Columns addColumns(Column... columns)
	{
		this.columns.array(columns);
		return this;
	}
	
	@Override
	public List<Object> getValues()
	{
		List<Object> values = new List<Object>().allowNull();
		for(Column column : columns)
		{
			values.addAll(column.getValues());
		}
		return values;
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		String seperator = "";
		for(Column column : columns)
		{
			builder.append(seperator);
			builder.append(column.toString());
			seperator = COLUMN;
		}
		return builder.toString();
	}
	
}
