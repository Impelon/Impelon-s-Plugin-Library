package de.impelon.geotools.area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import de.impelon.geotools.Axis;

/**
 * <p> Interface that specifies the methods of any Implementation of IArea. </p>
 * <p> Used to keep track of a defined area of Locations. </p>
 * 
 * @author Impelon
 *
 */
public interface IArea {
		
	/**
	 * <p> Returns the World of this Area. </p>
	 * 
	 * @return The World
	 */
	public abstract World getWorld();
	
	/**
	 * <p> Returns the length of this Area on the given axis. </p>
	 * 
	 * @param axis the axis
	 * @param block If true the length will correspond to the number of Blocks that fit on that axis
	 * @return The length of the area on that axis
	 */
	public abstract double getLength(Axis axis);
	
	/**
	 * <p> Returns the amount of Blocks contained by this Area on the given axis. </p>
	 * 
	 * @param axis the axis
	 * @return The length of the area on that axis
	 */
	public abstract long getBlockLength(Axis axis);
	
	/**
	 * <p> Returns the surface area of this Area. </p>
	 * 
	 * @return The surface area 
	 */
	public abstract double getSurfaceArea();
	
	/**
	 * <p> Returns the amount of Blocks contained in this Area. </p>
	 * 
	 * @return The surface area 
	 */
	public abstract long getBlockSurfaceArea();
	
	/**
	 * <p> Determines if a Location is within this Area. </p>
	 * 
	 * @param pos Location to check
	 * @return Whether this Area overlaps with the given Location
	 */
	public abstract boolean getOverlap(Location pos);
	
	/**
	 * <p> Determines if a Vector is within this Area. </p>
	 * 
	 * @param pos Vector to check
	 * @return Whether this Area overlaps with the given Vector
	 */
	public abstract boolean getOverlap(Vector pos);
	
	/**
	 * <p> Determines if another Area is within this Area. </p>
	 * 
	 * @param area Area to check
	 * @return Whether this Area overlaps with the given Area
	 */
	public abstract boolean getOverlap(IArea area);
	
}
