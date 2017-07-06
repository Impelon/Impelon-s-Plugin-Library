package de.impelon.geotools.area;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import de.impelon.geotools.Axis;

public class RectangularArea implements IArea {
	
	protected final Vector startPos;
	protected final Vector endPos;
	protected final World world;
	
	/**
	 * <p> Create a RectangularArea from two given corner-{@linkplain Locations}. </p>
	 * 
	 * @param locationA determines first corner of the RectangularArea
	 * @param locationB determines second corner of the RectangularArea
	 * @param floor If true the corners will be converted to Block-Positions (aka. floored)
	 * @throws IllegalArgumentException if the two Locations given are in different {@linkplain Worlds}
	 */
	public RectangularArea(Location locationA, Location locationB, boolean floor) throws IllegalArgumentException {
		this(floor ? new Location(locationA.getWorld(), locationA.getBlockX(), locationA.getBlockY(), locationA.getBlockZ()) : locationA,
				floor ? new Location(locationB.getWorld(), locationB.getBlockX(), locationB.getBlockY(), locationB.getBlockZ()) : locationB);
	}
	
	/**
	 * <p> Create a RectangularArea from two given corner-{@linkplain Locations}. </p>
	 * 
	 * @param locationA determines first corner of the RectangularArea
	 * @param locationB determines second corner of the RectangularArea
	 * @throws IllegalArgumentException if the two Locations given are in different {@linkplain Worlds}
	 */
	public RectangularArea(Location locationA, Location locationB) throws IllegalArgumentException {
		this(locationA.toVector(), locationB.toVector(), locationA.getWorld());
		if (locationA.getWorld() != locationB.getWorld())
			throw new IllegalArgumentException("Cannot add Locations of different Worlds to an Area");
	}
	
	/**
	 * <p> Create a RectangularArea from two given Vectors (corners) and a {@linkplain World}. </p>
	 * 
	 * @param start determines first corner of the RectangularArea
	 * @param end determines second corner of the RectangularArea
	 * @param world the {@linkplain World} this RectangularArea is in
	 */
	public RectangularArea(Vector start, Vector end, World world) {
		this.startPos = Vector.getMinimum(start, end);
		this.endPos = Vector.getMaximum(start, end);
		this.world = world;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public World getWorld() {
		return this.world;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getLength(Axis axis) {
		switch (axis) {
		case X:
			return this.endPos.getX() - this.startPos.getX();
		case Z:
			return this.endPos.getZ() - this.startPos.getZ();
		case Y:
		default:
			return 0;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getBlockLength(Axis axis) {
		switch (axis) {
		case X:
			return this.endPos.getBlockX() - this.startPos.getBlockX() + 1;
		case Z:
			return this.endPos.getBlockZ() - this.startPos.getBlockZ() + 1;
		case Y:
		default:
			return 0;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getSurfaceArea() {
		return this.getLength(Axis.X) * this.getLength(Axis.Z);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getBlockSurfaceArea() {
		return this.getBlockLength(Axis.X) * this.getBlockLength(Axis.Z);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getOverlap(Location pos) {
		return pos.getWorld() == this.getWorld() && this.getOverlap(pos.toVector());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getOverlap(Vector pos) {
		return (pos.getX() <= this.endPos.getX() && pos.getX() >= this.startPos.getX()) &&
				(pos.getZ() <= this.endPos.getZ() && pos.getZ() >= this.startPos.getZ());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getOverlap(IArea area) {
		if (area instanceof RectangularArea)
			return this.getOverlap((RectangularArea) area);
		if (area.getWorld() != this.getWorld())
			return false;
		for (double x = this.startPos.getX(); x >= this.endPos.getX(); x++)
			for (double z = this.startPos.getZ(); z >= this.endPos.getZ(); z++)
				if (area.getOverlap(this.startPos.clone().add(new Vector(x, 0, z))))
						return true;
		return false;
	}
	
	/**
	 * <p> Returns a the smaller corner of this RectangularArea. </p>
	 * 
	 * @return The Vector of the corner
	 */
	public Vector getStartPosition() {
		return this.startPos;
	}
	
	/**
	 * <p> Returns a the bigger corner of this RectangularArea. </p>
	 * 
	 * @return The Vector of the corner
	 */
	public Vector getEndPosition() {
		return this.endPos;
	}
	
	/**
	 * <p> Determines if another RectangularArea is within this Area. </p>
	 * 
	 * @param area RectangularArea to check
	 * @return Whether this Area overlaps with the given RectangularArea
	 */
	public boolean getOverlap(RectangularArea area) {
		return area.getWorld() == this.getWorld() 
				&& (this.getOverlap(area.getStartPosition()) || this.getOverlap(area.getEndPosition()) || 
						area.getOverlap(this.startPos) || area.getOverlap(this.endPos));
	}

}
