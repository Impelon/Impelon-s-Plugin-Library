package de.impelon.configuration;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlConfigurationLoader implements IFileConfigurationLoader {

	/**
	 * {@inheritDoc}
	 * <p> This uses the build-in YamlConfiguration-Format </p>
	 * @see YamlConfiguration#loadConfiguration(File)
	 */
	@Override
	public FileConfiguration loadConfiguration(File file) {
		return YamlConfiguration.loadConfiguration(file);
	}
	
}
