package de.impelon.midi;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

/**
 * <p> Receives MidiMessages and broadcast Minecraft sound at given Locations accordingly. </p>
 * 
 * @see MinecraftSoundReceiver
 * 
 * @author Impelon
 *
 */
public class LocationReceiver extends MinecraftSoundReceiver {
	
	protected List<Location> locations;
	protected InstrumentMap instrumentmap;
	
	/**
	 * <p> Creates a new LocationReceiver with the given Locations and the given InstrumentMap. </p>
	 * 
	 * @param locations a List<Location> to use to play Sounds
	 * @param instrumentmap an InstrumentMap
	 */
	public LocationReceiver(List<Location> locations, InstrumentMap instrumentmap) {
		super();
		this.locations = locations;
		this.instrumentmap = instrumentmap;
	}
	
	/**
	 * <p> Creates a new LocationReceiver with the given Locations and the default InstrumentMap. </p>
	 * 
	 * @param locations a List<Location> to use to play Sounds
	 */
	public LocationReceiver(List<Location> locations) {
		this(locations, InstrumentMap.DEFAULT_MAP);
	}
	
	/**
	 * <p> Returns a List of the Locations. </p>
	 * 
	 * @return The List<Location>
	 */
	public List<Location> getLocations() {
		return this.locations;
	}
	
	/**
	 * {@inheritDoc}
	 * <p> This uses the given InstrumentMap. </p>
	 */
	@Override
	protected Sound getInstrument(int ID) {
		return instrumentmap.getInstrument(ID);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void playNote(int channel, int key, int velocity) {
		for (Location location : this.locations)
			location.getWorld().playSound(location, this.getChannelInstrument(channel), SoundCategory.MASTER, 
					this.getActualVolume(channel, velocity), this.getPitchFromKey(key));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void stopNote(int channel, int key, int velocity) {}

}
