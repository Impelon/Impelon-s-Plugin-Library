package de.impelon.configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * <p> Used for managing multiple configurations as objects of {@linkplain FileConfiguration} inside a single folder. </p>
 * 
 * @author Impelon
 *
 */
public class ConfigurationFolder {
	
	private IFileConfigurationLoader defaultLoader = new YamlConfigurationLoader();
	private Plugin plugin;
	private File rootDirectory;
	private HashMap<String, FileConfiguration> configurationCache = new HashMap<String, FileConfiguration>();
	private HashMap<String, IFileConfigurationLoader> loaderCache = new HashMap<String, IFileConfigurationLoader>();
	
	/**
	 * <p> Creates a new ConfigurationFolder as the root-directory of a given Plugin. </p>
	 * 
	 * @param plugin the Plugin using the Configurations
	 */
	public ConfigurationFolder(Plugin plugin) {
		this(plugin, "");
	}
	
	/**
	 * <p> Creates a new ConfigurationFolder inside a directory in the root-directory of a given Plugin. </p>
	 * 
	 * @param plugin the Plugin using the Configurations
	 * @param directory the foldername to use
	 */
	public ConfigurationFolder(Plugin plugin, String directory) {
		this(plugin, new File(plugin.getDataFolder(), directory));
	}
	
	/**
	 * <p> Creates a new ConfigurationFolder inside a given root-directory. </p>
	 * 
	 * @param plugin the Plugin using the Configurations
	 * @param rootDirectory the folder to use
	 */
	public ConfigurationFolder(Plugin plugin, File rootDirectory) {
		this.plugin = plugin;
		this.rootDirectory = rootDirectory;
	}
	
	/**
	 * <p> Returns the default {@linkplain IFileConfigurationLoader} used when none is specified. </p>
	 * 
	 * @return The default IFileConfigurationLoader
	 */
	public IFileConfigurationLoader getDefaultFileConfigurationLoader() {
		return this.defaultLoader;
	}
	
	/**
	 * <p> Sets the default {@linkplain IFileConfigurationLoader} used when none is specified. </p>
	 * 
	 * @param loader the new default IFileConfigurationLoader
	 */
	public void setDefaultFileConfigurationLoader(IFileConfigurationLoader loader) {
		this.defaultLoader = loader;
	}
	
	/**
	 * <p> Returns the FileConfiguration affiliated with that name. </p>
	 * 
	 * @param name name of the FileConfiguration (on disk)
	 * @return The FileConfiguration found or null if none was found
	 */
	public FileConfiguration getConfig(String name) {
		return this.getConfig(name, this.defaultLoader);
	}
	
	/**
	 * <p> Returns the FileConfiguration affiliated with that name. </p>
	 * 
	 * @param name name of the FileConfiguration (on disk)
	 * @param loader IFileConfigurationLoader to use to load the FileConfiguration
	 * @return The FileConfiguration found or null if none was found
	 */
	public FileConfiguration getConfig(String name, IFileConfigurationLoader loader) {
		this.reloadConfig(name, loader);
		return this.configurationCache.get(name);
	}
	
	/**
	 * <p> Reloads the FileConfiguration affiliated with that name. </p>
	 * 
	 * @param name name of the FileConfiguration (on disk)
	 * @return Whether the action was successful
	 */
	public boolean reloadConfig(String name) {
		IFileConfigurationLoader loader = this.loaderCache.get(name);
		if (loader == null)
			loader = this.defaultLoader;
		try {
			this.configurationCache.put(name, loader.loadConfiguration(new File(this.rootDirectory, name)));
			return true;
		} catch (Exception ex) {
			this.plugin.getLogger().log(Level.SEVERE, "Could not reload config from " + name, ex);
		}
		return false;
	}
	
	/**
	 * <p> Reloads the FileConfiguration affiliated with that name. </p>
	 * <p> Specifies a {@linkplain IFileConfigurationLoader} to use instead of the default one. 
	 * This will be cached and used on future calls of {@linkplain ConfigurationFolder#reloadConfig(String)} </p>
	 * 
	 * @param name name of the FileConfiguration (on disk)
	 * @param loader IFileConfigurationLoader to use to load the FileConfiguration
	 * @return Whether the action was successful
	 */
	public boolean reloadConfig(String name, IFileConfigurationLoader loader) {
		this.loaderCache.put(name, loader);
		return this.reloadConfig(name);
	}
	
	/**
	 * <p> Reloads all FileConfigurations in the cache. </p>
	 * 
	 * @return Whether the action was successful
	 */
	public boolean reloadAllCached() {
		boolean sucessful = false;
		for (String k : this.configurationCache.keySet())
			sucessful &= this.reloadConfig(k, this.loaderCache.get(k));
		return sucessful;
	}
	
	/**
	 * <p> Saves the FileConfiguration affiliated with that name. </p>
	 * <p> This will only work on cached configs. </p>
	 * 
	 * @param name name of the FileConfiguration (on disk)
	 * @return Whether the action was successful
	 */
	public boolean saveConfig(String name) {
		FileConfiguration config = this.configurationCache.get(name);
		if (config == null)
			return false;
		return this.saveConfig(config, name);
	}
	
	/**
	 * <p> Saves the FileConfiguration with its affiliated name. </p>
	 * <p> This will only work on cached configs. </p>
	 * 
	 * @param config cached FileConfiguration to save
	 * @return Whether the action was successful
	 */
	public boolean saveConfig(FileConfiguration config) {
		String name = null;
		for (Entry<String, FileConfiguration> e : this.configurationCache.entrySet())
			if (e.getValue().equals(config))
				name = e.getKey();
		if (name == null)
			return false;
		return this.saveConfig(config, name);
	}
	
	/**
	 * <p> Saves the FileConfiguration with the given name. </p>
	 * 
	 * @param config FileConfiguration to save
	 * @param name name of the FileConfiguration (on disk)
	 * @return Whether the action was successful
	 */
	public boolean saveConfig(FileConfiguration config, String name) {
		try {
			config.save(new File(this.rootDirectory, name));
			return true;
		} catch (IOException ex) {
			this.plugin.getLogger().log(Level.SEVERE, "Could not save config (" + config + ") to " + name, ex);
		}
		return false;
	}
	
	/**
	 * <p> Saves all FileConfigurations in the cache. </p>
	 * 
	 * @return Whether the action was successful
	 */
	public boolean saveAllCached() {
		boolean sucessful = false;
		for (Entry<String, FileConfiguration> e : this.configurationCache.entrySet())
			sucessful &= this.saveConfig(e.getValue(), e.getKey());
		return sucessful;
	}
	
	/**
	 * <p> Registers the FileConfiguration with the given name in the cache. </p>
	 * 
	 * @param config FileConfiguration to register
	 * @param name name of the FileConfiguration (on disk)
	 */
	public void registerConfig(FileConfiguration config, String name) {
		this.configurationCache.put(name, config);
	}
	
	/**
	 * <p> Clears the cache storing the Configurations. </p>
	 * 
	 * @param config FileConfiguration to register
	 * @param name name of the FileConfiguration (on disk)
	 */
	public void clearCache() {
		this.configurationCache.clear();
	}

}
