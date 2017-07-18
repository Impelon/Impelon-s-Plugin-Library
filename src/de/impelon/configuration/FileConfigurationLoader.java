package de.impelon.configuration;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * <p> Used for loading a FileConfiguration from a File. Therefore different formats can be used. </p>
 * 
 * @author Impelon
 *
 */
public interface FileConfigurationLoader {
	
	/**
	 * <p> Loads a FileConfiguration from the given file. </p>
	 * 
	 * @param file File to load from
	 * @return the FileConfiguration
	 */
	public FileConfiguration loadConfiguration(File file);
	
}
