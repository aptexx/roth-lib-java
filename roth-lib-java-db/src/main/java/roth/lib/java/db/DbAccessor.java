package roth.lib.java.db;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import roth.lib.java.Callback;
import roth.lib.java.db.sql.Select;

public abstract class DbAccessor
{
	
	public DbAccessor()
	{
		
	}
	
	public abstract DbDataSource getDb();
	
	public <T> T findBy(Select select, Class<T> klass)
	{
		return getDb().query(select, klass);
	}
	
	public <T> T findBy(String sql, Class<T> klass)
	{
		return getDb().query(sql, klass);
	}
	
	public <T> T findBy(String sql, Collection<Object> values, Class<T> klass)
	{
		return getDb().query(sql, values, klass);
	}
	
	public <T> T findBy(String sql, Map<String, Object> valueMap, Class<T> klass)
	{
		return getDb().query(sql, valueMap, klass);
	}
	
	public <T> LinkedList<T> findAllBy(Select select, Class<T> klass)
	{
		return getDb().queryAll(select, klass);
	}
	
	public <T> LinkedList<T> findAllBy(String sql, Class<T> klass)
	{
		return getDb().queryAll(sql, klass);
	}
	
	public <T> LinkedList<T> findAllBy(String sql, Collection<Object> values, Class<T> klass)
	{
		return getDb().queryAll(sql, values, klass);
	}
	
	public <T> LinkedList<T> findAllBy(String sql, Map<String, Object> valueMap, Class<T> klass)
	{
		return getDb().queryAll(sql, valueMap, klass);
	}
	
	public <T> void callback(Select select, Callback<T> callback, Class<T> klass)
	{
		getDb().queryAll(select, callback.setKlass(klass));
	}
	
	public <T> void callback(String sql, Callback<T> callback, Class<T> klass)
	{
		getDb().queryAll(sql, callback.setKlass(klass));
	}
	
	public <T> void callback(String sql, Collection<Object> values, Callback<T> callback, Class<T> klass)
	{
		getDb().queryAll(sql, values, callback.setKlass(klass));
	}
	
	public <T> void callback(String sql, Map<String, Object> valueMap, Callback<T> callback, Class<T> klass)
	{
		getDb().queryAll(sql, valueMap, callback.setKlass(klass));
	}
	
}
