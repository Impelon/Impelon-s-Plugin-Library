package de.impelon.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> Used for managing SQL-requests to a database. </p>
 * <p> Is able to connect with different JDBC-Drivers. </p>
 * 
 * @author Impelon
 *
 */
public class JDBCDatabaseHandler implements IDatabaseHandler {

	protected final String url, user, password, driverClasspath;
	protected Connection connection = null;

	/**
	 * <p> Create a DatabaseHandler with a JDBC-MySQL-Driver. </p>
	 * 
	 * @param host the host's address
	 * @param port the port to access the database
	 * @param database the database's name
	 * @param user the database's username
	 * @param password the user's password
	 */
	public JDBCDatabaseHandler(String host, String port, String database, String user, String password) {
		this("jdbc:mysql://" + host + ":" + port + "/" + database, "com.mysql.jdbc.Driver", user, password);

	}
	
	/**
	 * <p> Create a DatabaseHandler with any Driver. </p>
	 * 
	 * @param url the jdbc-driver-url
	 * @param driverClasspath the path to the driverclass
	 * @param user the database's username
	 * @param password the user's password
	 */
	public JDBCDatabaseHandler(String url, String driverClasspath, String user, String password) {
		this.url = url;
		this.driverClasspath = driverClasspath;
		this.user = user;
		this.password = password;
	}
	
	/**
	 * <p> Opens a new {@linkplain Connection}. </p>
	 */
	public void connect() {
		try {
			Class.forName(driverClasspath);
			connection = DriverManager.getConnection(url, user, password);
			this.onConnect();
		} catch (ClassNotFoundException e) {
			System.err.println("Unable to find the driver-class!");
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("Unable to access database!");
			e.printStackTrace();
		}
	}
	
	/**
	 * <p> Closes the current {@linkplain Connection}. </p>
	 */
	public void disconnect() {
		try {
			this.onDisconnect();
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			System.err.println("Unable to close database-connection!");
			e.printStackTrace();
		}
	}
	
	/**
	 * <p> Returns the {@linkplain Connection} of this DatabaseHandler. </p>
	 * 
	 * @return The connection
	 */
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * <p> Registers a new datatype to the current Connection. </p>
	 * 
	 * @param typeReference SQL-reference for the datatype
	 * @param typeClass class of the datatype
	 * @see java.sql.SQLData
	 */
	public void registerDataType(String typeReference, Class<? extends SQLData> typeClass) {
		if (this.connection == null)
			this.connect();
		try {
			Map<String, Class<?>> typemap = this.connection.getTypeMap();
			typemap.put(typeReference, typeClass);
			this.connection.setTypeMap(typemap);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * {@inheritDoc}
	 * <p> If {@code preparationAttributes == null} is true, then a static {@linkplain Statement} will be created rather than a {@linkplain PreparedStatement}. </p> 
	 */
	public List<Map<String, Object>> executeQuery(String sql, Collection<Object> preparationAttributes) {
		if (this.connection == null)
			this.connect();
		Statement statement = null;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			if (preparationAttributes == null) {
				statement = this.connection.createStatement();
				result.addAll(this.resultSetToMapList(statement.executeQuery(sql)));
			}
			else {
				statement = this.connection.prepareStatement(sql);
				int i = 1;
				for (Object attribute : preparationAttributes) {
					((PreparedStatement) statement).setObject(i, attribute);
					i++;
				}
				result.addAll(this.resultSetToMapList(((PreparedStatement) statement).executeQuery()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (!this.connection.getAutoCommit())
					this.connection.rollback();
			} catch (SQLException ex) {}
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return result;
	}
	
	/** 
	 * {@inheritDoc}
	 * <p> If {@code preparationAttributes == null} is true, then a static {@linkplain Statement} will be created rather than a {@linkplain PreparedStatement}. </p> 
	 */
	public long executeUpdate(String sql, Collection<Object> preparationAttributes) {
		if (this.connection == null)
			this.connect();
		Statement statement = null;
		long result = -1;
		try {
			if (preparationAttributes == null) {
				statement = this.connection.createStatement();
				result = statement.executeUpdate(sql);
			}
			else {
				statement = this.connection.prepareStatement(sql);
				int i = 1;
				for (Object attribute : preparationAttributes) {
					((PreparedStatement) statement).setObject(i, attribute);
					i++;
				}
				result = ((PreparedStatement) statement).executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (!this.connection.getAutoCommit())
					this.connection.rollback();
			} catch (SQLException ex) {}
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return result;
	}
	
	/**
	 * <p> Executes an Update and returns the result. </p>
	 * 
	 * @param statement the Statement to execute
	 * @param sql the SQL-String statement to execute
	 * @return Whether the Execution produced a ResultSet or not
	 * @see java.sql.Statement#execute(sql)
	 */
	protected boolean execute(Statement statement, String sql) {
		boolean result = false;
		try {
			if (statement instanceof PreparedStatement)
				result = ((PreparedStatement) statement).execute();
			else
				result = statement.execute(sql);
			if (!this.connection.getAutoCommit())
				this.connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (!this.connection.getAutoCommit())
					this.connection.rollback();
			} catch (SQLException ex) {}
		}
		return result;
	}
	
	/**
	 * <p> Performs actions after the DatabaseHandler has been connected. </p>
	 */
	protected void onConnect() {}
	
	/**
	 * <p> Performs actions before the DatabaseHandler is disconnected. </p>
	 */
	protected void onDisconnect() {}
	
	/**
	 * <p> Converts a {@linkplain ResultSet} to a List of Maps. </p>
	 * 
	 * @param resultset Any ResultSet
	 * @return A List of Maps corresponding to the rows of the result
	 */
	protected List<Map<String, Object>> resultSetToMapList(ResultSet resultset) {
		List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
		if (resultset != null)
			try {
			    Map<String, Object> row = null;
			    ResultSetMetaData metaData = resultset.getMetaData();
			    Integer columnCount = metaData.getColumnCount();
		
			    while (resultset.next()) {
			        row = new HashMap<String, Object>();
			        for (int i = 1; i <= columnCount; i++)
			            row.put(metaData.getColumnLabel(i), resultset.getObject(i));
			        resultlist.add(row);
			    }
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    return resultlist;
	}

}
