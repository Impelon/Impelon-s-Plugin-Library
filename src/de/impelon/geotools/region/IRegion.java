package de.impelon.geotools.region;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import de.impelon.geotools.RegionFormat;
import de.impelon.geotools.area.IArea;

public abstract interface IRegion extends IArea, Iterable<Vector> {
	
	/**
	 * <p> Returns the volume of this Region. </p>
	 * 
	 * @return The volume
	 */
	public abstract double getVolume();
	
	/**
	 * <p> Returns the amount of Blocks this Region contains. </p>
	 * 
	 * @return The volume
	 */
	public abstract long getBlockVolume();
	
	/**
	 * <p> Returns a sub-region of this Region. </p>
	 * 
	 * @param format Format of the sub-region
	 * @return The Iterator<Location>
	 */
	public abstract IRegion getSubRegion(RegionFormat format);
	
	/**
	 * <p> Returns a Iterator over the Locations within this Region. </p>
	 * 
	 * @return The Iterator<Location>
	 */
	public abstract Iterator<Location> getLocationIterator();
	
	/**
	 * <p> Returns a List of the Vectors within this Region. </p>
	 * 
	 * @return The List<Vector>
	 */
	public abstract List<Vector> getVectors();
	
	/**
	 * <p> Returns a List of the Locations within this Region. </p>
	 * 
	 * @return The List<Location>
	 */
	public abstract List<Location> getLocations();
	
	/**
	 * <p> Determines if another Region is within this Region. </p>
	 * 
	 * @param region Region to check
	 * @return Whether this Region overlaps with the given Region
	 */
	public abstract boolean getOverlap(IRegion region);

}