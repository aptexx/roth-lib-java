package roth.lib.java.db;

import java.sql.Connection;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public abstract class DbConnection implements Connection, DbWrapper
{
	protected Connection connection;
	protected DbCloseHandler closeHandler;
	
	public DbConnection(Connection connection)
	{
		this.connection = connection;
	}
	
	public DbConnection setCloseHandler(DbCloseHandler closeHandler)
	{
		this.closeHandler = closeHandler;
		return this;
	}
	
	@Override
	public DbDatabaseMetaData getMetaData() throws SQLException
	{
		return wrap(connection.getMetaData());
	}
	
	@Override
	public DbStatement createStatement() throws SQLException
	{
		return wrap(connection.createStatement());
	}
	
	@Override
	public DbStatement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
	{
		return wrap(connection.createStatement(resultSetType, resultSetConcurrency));
	}
	
	@Override
	public DbStatement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
	{
		return wrap(connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
	}
	
	@Override
	public DbPreparedStatement prepareStatement(String sql) throws SQLException
	{
		return wrap(connection.prepareStatement(sql));
	}
	
	@Override
	public DbPreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
	{
		return wrap(connection.prepareStatement(sql, autoGeneratedKeys));
	}
	
	@Override
	public DbPreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
	{
		return wrap(connection.prepareStatement(sql, columnIndexes));
	}
	
	@Override
	public DbPreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
	{
		return wrap(connection.prepareStatement(sql, columnNames));
	}
	
	@Override
	public DbPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
	{
		return wrap(connection.prepareStatement(sql, resultSetType, resultSetConcurrency));
	}
	
	@Override
	public DbPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
	{
		return wrap(connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}
	
	@Override
	public DbCallableStatement prepareCall(String sql) throws SQLException
	{
		return wrap(connection.prepareCall(sql));
	}
	
	@Override
	public DbCallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
	{
		return wrap(connection.prepareCall(sql, resultSetType, resultSetConcurrency));
	}
	
	@Override
	public DbCallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
	{
		return wrap(connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
	}
	
	@Override
	public DbSavepoint setSavepoint() throws SQLException
	{
		return wrap(connection.setSavepoint());
	}
	
	@Override
	public DbSavepoint setSavepoint(String name) throws SQLException
	{
		return wrap(connection.setSavepoint(name));
	}
	
	@Override
	public DbBlob createBlob() throws SQLException
	{
		return wrap(connection.createBlob());
	}
	
	@Override
	public DbClob createClob() throws SQLException
	{
		return wrap(connection.createClob());
	}
	
	@Override
	public DbNClob createNClob() throws SQLException
	{
		return wrap(connection.createNClob());
	}
	
	@Override
	public DbSQLXML createSQLXML() throws SQLException
	{
		return wrap(connection.createSQLXML());
	}
	
	@Override
	public DbArray createArrayOf(String typeName, Object[] elements) throws SQLException
	{
		return wrap(connection.createArrayOf(typeName, elements));
	}
	
	@Override
	public DbStruct createStruct(String typeName, Object[] attributes) throws SQLException
	{
		return wrap(connection.createStruct(typeName, attributes));
	}
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException
	{
		return connection.unwrap(iface);
	}
	
	public Connection unwrap()
	{
		return connection;
	}
	
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException
	{
		return connection.isWrapperFor(iface);
	}
	
	@Override
	public String nativeSQL(String sql) throws SQLException
	{
		return connection.nativeSQL(sql);
	}
	
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException
	{
		connection.setAutoCommit(autoCommit);
	}
	
	@Override
	public boolean getAutoCommit() throws SQLException
	{
		return connection.getAutoCommit();
	}
	
	@Override
	public void commit() throws SQLException
	{
		connection.commit();
	}
	
	@Override
	public void rollback() throws SQLException
	{
		connection.rollback();
	}
	
	@Override
	public void close() throws SQLException
	{
		if(closeHandler != null)
		{
			closeHandler.close(this);
		}
		else
		{
			closeWrapped();
		}
	}
	
	public void closeWrapped()
	{
		try
		{
			connection.close();
		}
		catch(Exception e)
		{
			
		}
	}
	
	@Override
	public boolean isClosed() throws SQLException
	{
		return connection.isClosed();
	}
	
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException
	{
		connection.setReadOnly(readOnly);
	}
	
	@Override
	public boolean isReadOnly() throws SQLException
	{
		return connection.isReadOnly();
	}
	
	@Override
	public void setCatalog(String catalog) throws SQLException
	{
		connection.setCatalog(catalog);
	}
	
	@Override
	public String getCatalog() throws SQLException
	{
		return connection.getCatalog();
	}
	
	@Override
	public void setTransactionIsolation(int level) throws SQLException
	{
		connection.setTransactionIsolation(level);
	}
	
	@Override
	public int getTransactionIsolation() throws SQLException
	{
		return connection.getTransactionIsolation();
	}
	
	@Override
	public SQLWarning getWarnings() throws SQLException
	{
		return connection.getWarnings();
	}
	
	@Override
	public void clearWarnings() throws SQLException
	{
		connection.clearWarnings();
	}
	
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException
	{
		return connection.getTypeMap();
	}
	
	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException
	{
		connection.setTypeMap(map);
	}
	
	@Override
	public void setHoldability(int holdability) throws SQLException
	{
		connection.setHoldability(holdability);
	}
	
	@Override
	public int getHoldability() throws SQLException
	{
		return connection.getHoldability();
	}
	
	@Override
	public void rollback(Savepoint savepoint) throws SQLException
	{
		connection.rollback(savepoint);
	}
	
	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException
	{
		connection.releaseSavepoint(savepoint);
	}
	
	@Override
	public boolean isValid(int timeout) throws SQLException
	{
		return connection.isValid(timeout);
	}
	
	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException
	{
		connection.setClientInfo(name, value);
	}
	
	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException
	{
		connection.setClientInfo(properties);
	}
	
	@Override
	public String getClientInfo(String name) throws SQLException
	{
		return connection.getClientInfo(name);
	}
	
	@Override
	public Properties getClientInfo() throws SQLException
	{
		return connection.getClientInfo();
	}
	
	@Override
	public void setSchema(String schema) throws SQLException
	{
		connection.setSchema(schema);
	}
	
	@Override
	public String getSchema() throws SQLException
	{
		return connection.getSchema();
	}
	
	@Override
	public void abort(Executor executor) throws SQLException
	{
		connection.abort(executor);
	}
	
	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException
	{
		connection.setNetworkTimeout(executor, milliseconds);
	}
	
	@Override
	public int getNetworkTimeout() throws SQLException
	{
		return connection.getNetworkTimeout();
	}
	
}
