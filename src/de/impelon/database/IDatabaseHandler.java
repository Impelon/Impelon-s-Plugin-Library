package de.impelon.database;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * <p> Used for managing SQL-requests to a database. </p>
 * 
 * @author Impelon
 *
 */
public interface IDatabaseHandler {
	
	/**
	 * <p> Executes a Query and returns the result. </p>
	 * 
	 * @param sql the SQL-String statement to execute
	 * @param preparationAttributes a Collection of the variables to pass with a {@linkplain PreparedStatement}
	 * @return A List of Maps corresponding to the rows of the result
	 * @see java.sql.Statement#executeQuery(String)
	 */
	public abstract List<Map<String, Object>> executeQuery(String sql, Collection<Object> preparationAttributes);
	
	/**
	 * <p> Executes an Update and returns the result </p>
	 * 
	 * @param sql the SQL-String statement to execute
	 * @param preparationAttributes a Collection of the variables to pass with a {@linkplain PreparedStatement}
	 * @return The number of rows affected
	 * @see java.sql.Statement#executeUpdate(String)
	 */
	public abstract long executeUpdate(String sql, Collection<Object> preparationAttributes);

}
