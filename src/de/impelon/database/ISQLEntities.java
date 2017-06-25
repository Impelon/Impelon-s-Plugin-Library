package de.impelon.database;

import java.util.List;
import java.util.Map;

/**
 * <p> Base Model for implementing an ORM-concept. </p>
 * 
 * @author Impelon
 *
 */
public interface ISQLEntities {
	
	/**
	 * <p> Returns these Entities' tablename. </p>
	 * 
	 * @return The tablename
	 */
	public abstract String getTableName();
	
	/**
	 * <p> Returns a {@linkplain List} of all the names of the columns these entities define. </p>
	 * 
	 * @return The tablename
	 */
	public abstract List<String> getColumnNames();
	
	/**
	 * <p> Returns the columnname of the ID of these entities. </p>
	 * 
	 * @return The ID's columnname
	 */
	public abstract String getIDName();
	
	/**
	 * <p> Performs actions for setting up a table. For example:</p>
	 * <p> {@code databasehandler.executeUpdate("CREATE TABLE IF NOT EXISTS "
	 *  + this.getTableName() + " ("
	 *  + this.getIDName() + "BIGINT NOT NULL, " 
	 *  + "name vARCHAR(60) NOT NULL, " 
	 *  + "PRIMARY KEY (" + this.getIDName() + ")"
	 *  + ")");} </p>
	 *  
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 */
	public abstract void onSetup(IDatabaseHandler databasehandler);
	
	/**
	 * <p> Returns the number of entities/rows in the database. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @return The number of entities/rows
	 */
	public long getRowCount(IDatabaseHandler databasehandler);
	
	/**
	 * <p> Returns the number of attributes/columns these entities define in the database. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @return The number of attributes/columns
	 */
	public long getColumnCount(IDatabaseHandler databasehandler);
	
	/**
	 * <p> Returns the last used ID. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @return The last ID
	 */
	public Object getLastID(IDatabaseHandler databasehandler);
	
	/**
	 * <p> Returns the next unused ID. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @return The next ID
	 */
	public abstract Object getNextID(IDatabaseHandler databasehandler);
	
	/**
	 * <p> Determines if the entity, represented by its ID, exists in the database. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @param id the entity's ID
	 * @return Whether the entity exists in the database
	 */
	public boolean existsInDatabase(IDatabaseHandler databasehandler, Object id);
	
	/**
	 * <p> Loads an entity, represented by a Map of its columns, from the database. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @param id the entity's ID
	 * @return A Map corresponding to the attributes of the entity
	 */
	public Map<String, Object> loadFromDatabase(IDatabaseHandler databasehandler, Object id);
	
	/**
	 * <p> Loads multiple entities, represented by a Map of their columns each, from the database. </p>
	 * <p> {@code attributes} defines properties these entities possess; pattern-matching is used (SQL: {@code LIKE}). </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @param attributes a Map corresponding to the properties of the entities
	 * @return A List of Maps corresponding to the attributes of the entities
	 */
	public List<Map<String, Object>> loadFromDatabase(IDatabaseHandler databasehandler, Map<String, Object> attributes);
    
	/**
	 * <p> Saves a valid entity, represented by a Map of its columns, to the database. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @param entity a Map corresponding to the attributes of the entity
	 * @return The number of rows/entities affected
	 * @see ISQLEntities#isValidEntity(IDatabaseHandler, Map)
	 */
	public long saveToDatabase(IDatabaseHandler databasehandler, Map<String, Object> entity);
	
	/**
	 * <p> Saves multiple valid entities, represented by a Map of their columns each, to the database. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @param entities a List of Maps corresponding to the attributes of the entities
	 * @return The number of rows/entities affected
	 * @see ISQLEntities#saveToDatabase(IDatabaseHandler, Map)
	 */
	public long saveToDatabase(IDatabaseHandler databasehandler, List<Map<String, Object>> entities);
	
	/**
	 * <p> Deletes an entity, represented by its ID, from the database. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @param id the entity's ID
	 * @return The number of rows/entities affected
	 */
	public long deleteFromDatabase(IDatabaseHandler databasehandler, Object id);
	
	/**
	 * <p> Deletes an entity, represented by a Map of its columns, from the database. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @param entity a Map corresponding to the attributes of the entity
	 * @return The number of rows/entities affected
	 */
	public long deleteFromDatabase(IDatabaseHandler databasehandler, Map<String, Object> entity);
	
	/**
	 * <p> Deletes multiple entities, represented by a Map of their columns each, from the database. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @param entities a List of Maps corresponding to the attributes of the entities
	 * @return The number of rows/entities affected
	 */
	public long deleteFromDatabase(IDatabaseHandler databasehandler, List<Map<String, Object>> entities);
    
	/**
	 * <p> Determines if the given Map represents a valid entity. </p>
	 * 
	 * @param databasehandler {@linkplain IDatabaseHandler}, which handles the requests
	 * @param entity a Map corresponding to the attributes of the entity
	 * @return Whether the Map represents a valid entity
	 */
    public abstract boolean isValidEntity(IDatabaseHandler databasehandler, Map<String, Object> entity);

}
