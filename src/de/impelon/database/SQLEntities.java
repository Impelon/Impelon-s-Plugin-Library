package de.impelon.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p> Base Model for implementing an ORM-concept. </p>
 * <p> Implements most functionalities. </p>
 * 
 * @author Impelon
 *
 */
public abstract class SQLEntities implements ISQLEntities {
	
	/**
	 * {@inheritDoc}
	 */
	public long getRowCount(IDatabaseHandler databasehandler) {
		List<Map<String, Object>> result = databasehandler.executeQuery("SELECT COUNT(*) AS rowcount FROM " + this.getTableName(), null);
		return (long) (result.size() > 0 ? Long.parseLong(result.get(0).get("rowcount").toString()) : 0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public long getColumnCount(IDatabaseHandler databasehandler) {
		List<Map<String, Object>> result = databasehandler.executeQuery("SELECT COUNT(*) AS columncount FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = "  + this.getTableName(), null);
		return (long) (result.size() > 0 ? Long.parseLong(result.get(0).get("columncount").toString()) : 0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Object getLastID(IDatabaseHandler databasehandler) {
		List<Map<String, Object>> result = databasehandler.executeQuery("SELECT MAX(" + this.getIDName() + ") as last_id FROM " + this.getTableName(), null);
		return (result.size() > 0 ? result.get(0).get("last_id") : null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean existsInDatabase(IDatabaseHandler databasehandler, Object id) {
		return !databasehandler.executeQuery("SELECT * FROM " + this.getTableName() + " WHERE " + this.getIDName() + " = ?", Collections.singletonList(id)).isEmpty();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Map<String, Object> loadFromDatabase(IDatabaseHandler databasehandler, Object id) {
		List<Map<String, Object>> result = databasehandler.executeQuery("SELECT * FROM " + this.getTableName() + " WHERE " + this.getIDName() + " = ?", Collections.singletonList(id));
		return result.size() > 0 ? result.get(0) : new HashMap<String, Object>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Map<String, Object>> loadFromDatabase(IDatabaseHandler databasehandler, Map<String, Object> attributes) {
		StringBuilder searchstring = new StringBuilder();
		ArrayList<Object> values = new ArrayList<Object>();
		for (String column : this.getColumnNames())
			if (attributes.containsKey(column)) {
				searchstring.append(" AND " + column + " LIKE ?");
				values.add(attributes.get(column));
			}
		searchstring.delete(0, 5);
		
		return databasehandler.executeQuery("SELECT * FROM " + this.getTableName() + " WHERE " + searchstring.toString(), values);
	}
    
	/**
	 * {@inheritDoc}
	 */
	public long saveToDatabase(IDatabaseHandler databasehandler, Map<String, Object> entity) {
		if (this.isValidEntity(databasehandler, entity)) {
			long result;
			ArrayList<Object> values = new ArrayList<Object>();
			if (this.existsInDatabase(databasehandler, entity.get(this.getIDName()))) {
				StringBuilder setstring = new StringBuilder();
				for (String column : this.getColumnNames()) {
					setstring.append(", " + column + " = ?");
					values.add(entity.get(column));
				}
				setstring.delete(0, 2);
				values.add(entity.get(this.getIDName()));
				result = databasehandler.executeUpdate("UPDATE " + getTableName() + " SET " + setstring.toString() + " WHERE " + this.getIDName() + " = ?", values);
			} else {
				StringBuilder columns = new StringBuilder();
				StringBuilder placeholder = new StringBuilder();
				for (String column : this.getColumnNames()) {
					columns.append(", " + column);
					placeholder.append(", ?");
					values.add(entity.get(column));
				}
				columns.delete(0, 2);
				placeholder.delete(0, 2);
				result = databasehandler.executeUpdate("INSERT INTO " + getTableName() + " (" + columns.toString() + ") VALUES (" + placeholder.toString() + ")", values);
			}
			return result;
		}
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public long saveToDatabase(IDatabaseHandler databasehandler, List<Map<String, Object>> entities) {
		long affected = 0;
		for (Map<String, Object> entity : entities)
			affected += this.saveToDatabase(databasehandler, entity);
		return affected;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public long deleteFromDatabase(IDatabaseHandler databasehandler, Object id) {
		return databasehandler.executeUpdate("DELETE FROM "+ getTableName() + " WHERE " + this.getIDName() + " = ?", Collections.singletonList(id));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public long deleteFromDatabase(IDatabaseHandler databasehandler, Map<String, Object> entity) {
		if (entity.containsKey(this.getIDName()))
			return this.deleteFromDatabase(databasehandler, entity.get(this.getIDName()));
		return 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public long deleteFromDatabase(IDatabaseHandler databasehandler, List<Map<String, Object>> entities) {
		long affected = 0;
		for (Map<String, Object> entity : entities)
			affected += this.deleteFromDatabase(databasehandler, entity);
		return affected;
	}
    
}
