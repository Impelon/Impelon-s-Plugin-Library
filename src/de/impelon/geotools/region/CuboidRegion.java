package de.impelon.geotools.region;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import de.impelon.geotools.Axis;
import de.impelon.geotools.RegionFormat;
import de.impelon.geotools.area.RectangularArea;

/**
 * <p> Implementation of IRegion for a cuboid region. </p>
 * 
 * @author Impelon
 *
 */
public class CuboidRegion extends RectangularArea implements IRegion {
	
	/**
	 * <p> Create a CuboidRegion from two given corner-{@linkplain Locations}. </p>
	 * 
	 * @param locationA determines first corner of the CuboidRegion
	 * @param locationB determines second corner of the CuboidRegion
	 * @param floor If true the corners will be converted to Block-Positions (aka. floored)
	 * @throws IllegalArgumentException if the two Locations given are in different {@linkplain Worlds}
	 */
	public CuboidRegion(Location locationA, Location locationB, boolean floor) throws IllegalArgumentException {
		this(locationA.toVector(), locationB.toVector(), locationA.getWorld(), floor);
		if (locationA.getWorld() != locationB.getWorld())
			throw new IllegalArgumentException("Cannot add Locations of different Worlds to an Region");
	}
	
	/**
	 * <p> Create a CuboidRegion from two given corner-{@linkplain Locations}. </p>
	 * 
	 * @param locationA determines first corner of the CuboidRegion
	 * @param locationB determines second corner of the CuboidRegion
	 * @throws IllegalArgumentException if the two Locations given are in different {@linkplain Worlds}
	 */
	public CuboidRegion(Location locationA, Location locationB) throws IllegalArgumentException {
		this(locationA, locationB, false);
	}
	
	/**
	 * <p> Create a CuboidRegion from two given Vectors (corners) and a {@linkplain World}. </p>
	 * 
	 * @param start determines first corner of the CuboidRegion
	 * @param end determines second corner of the CuboidRegion
	 * @param floor If true the corners will be converted to Block-Positions (aka. floored)
	 * @param world the {@linkplain World} this CuboidRegion is in
	 */
	public CuboidRegion(Vector start, Vector end, World world, boolean floor) {
		this(floor ? new Vector(start.getBlockX(), start.getBlockY(), start.getBlockZ()) : start,
				floor ? new Vector(end.getBlockX(), end.getBlockY(), end.getBlockZ()) : end, world);
	}
	
	/**
	 * <p> Create a CuboidRegion from two given Vectors (corners) and a {@linkplain World}. </p>
	 * 
	 * @param start determines first corner of the CuboidRegion
	 * @param end determines second corner of the CuboidRegion
	 * @param world the {@linkplain World} this CuboidRegion is in
	 */
	public CuboidRegion(Vector start, Vector end, World world) {
		super(start, end, world);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getLength(Axis axis) {
		switch (axis) {
		case X:
		case Z:
			return super.getLength(axis);
		case Y:
			return this.endPos.getY() - this.startPos.getY();
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
		case Z:
			return super.getBlockLength(axis);
		case Y:
			return this.endPos.getBlockY() - this.startPos.getBlockY() + 1;
		default:
			return 0;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getOverlap(Vector pos) {
		return (pos.getX() <= this.endPos.getX() && pos.getX() >= this.startPos.getX()) &&
				(pos.getY() <= this.endPos.getY() && pos.getY() >= this.startPos.getY()) &&
				(pos.getZ() <= this.endPos.getZ() && pos.getZ() >= this.startPos.getZ());
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getOverlap(IRegion region) {
		if (region instanceof CuboidRegion)
			return this.getOverlap((CuboidRegion) region);
		if (region.getWorld() != this.getWorld())
			return false;
		for (Iterator<Vector> iterator = this.iterator(); iterator.hasNext();)
			if (region.getOverlap(iterator.next()))
				return true;
		return false;
	}
	
	/**
	 * <p> Determines if another CuboidRegion is within this Region. </p>
	 * 
	 * @param region CuboidRegion to check
	 * @return Whether this Region overlaps with the given CuboidRegion
	 */
	public boolean getOverlap(CuboidRegion region) {
		return region.getWorld() == this.getWorld() 
				&& (this.getOverlap(region.getStartPosition()) || this.getOverlap(region.getEndPosition()) || 
						region.getOverlap(this.startPos) || region.getOverlap(this.endPos));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getVolume() {
		return this.getSurfaceArea() * this.getLength(Axis.Y);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getBlockVolume() {
		return this.getBlockSurfaceArea() * this.getBlockLength(Axis.Y);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IRegion getSubRegion(RegionFormat format) {
		switch (format) {
		case HOLLOW:
			PositionRegion region = new PositionRegion(this);
			region.remove(this.getSubRegion(RegionFormat.ENCLOSED));
			return region;
		case WIREFRAME:
			HashSet<Vector> set = new HashSet<Vector>();
			iterator().forEachRemaining(new Consumer<Vector>() {

				@Override
				public void accept(Vector v) {
					if (((v.getBlockX() == getStartPosition().getBlockX() || v.getBlockX() == getEndPosition().getBlockX()) &&
						(v.getBlockY() == getStartPosition().getBlockY() || v.getBlockY() == getEndPosition().getBlockY())) ||
						((v.getBlockX() == getStartPosition().getBlockX() || v.getBlockX() == getEndPosition().getBlockX()) &&
						(v.getBlockZ() == getStartPosition().getBlockZ() || v.getBlockZ() == getEndPosition().getBlockZ())) ||
						((v.getBlockY() == getStartPosition().getBlockY() || v.getBlockY() == getEndPosition().getBlockY()) &&
						(v.getBlockZ() == getStartPosition().getBlockZ() || v.getBlockZ() == getEndPosition().getBlockZ())))
						set.add(v);
				}
			});
			return new PositionRegion(this.getWorld(), set);
		case FLOORED:
			return new CuboidRegion(this.getStartPosition().toLocation(this.getWorld()), this.getEndPosition().toLocation(this.getWorld()), true);
		case ENCLOSED:
			return new CuboidRegion(this.getStartPosition().clone().add(new Vector(1, 1, 1)), this.getEndPosition().clone().add(new Vector(-1, -1, -1)), this.getWorld());
		case SURROUNDING:
			return new CuboidRegion(this.getStartPosition().clone().add(new Vector(-1, -1, -1)), this.getEndPosition().clone().add(new Vector(1, 1, 1)), this.getWorld());
		case FULL:
		default:
			return new CuboidRegion(this.getStartPosition().clone(), this.getEndPosition().clone(), this.getWorld());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Vector> iterator() {
		Iterator<Vector> iterator = new Iterator<Vector>() {
			
			private long x = 0;
			private long y = 0;
			private long z = 0;
			private final Vector direction = endPos.clone().subtract(startPos).divide(new Vector(getBlockLength(Axis.X) - 1, getBlockLength(Axis.Y) - 1, getBlockLength(Axis.Z) - 1));

			@Override
			public boolean hasNext() {
				return this.z < getBlockLength(Axis.Z);
			}

			@Override
			public Vector next() {
				Vector vector = startPos.clone().add(new Vector(x, y, z).multiply(direction));
				x = (x + 1) % getBlockLength(Axis.X);
				if (x == 0) {
					y = (y + 1) % getBlockLength(Axis.Y);
					if (y == 0)
						z++;
				}	
				return vector;
			}
		};
		return iterator;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Location> getLocationIterator() {
		Iterator<Vector> vectoriterator = this.iterator();
		Iterator<Location> iterator = new Iterator<Location>() {
			
			@Override
			public boolean hasNext() {
				return vectoriterator.hasNext();
			}

			@Override
			public Location next() {
				return vectoriterator.next().toLocation(getWorld());
			}
		};
		return iterator;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Vector> getVectors() {
		ArrayList<Vector> vectors = new ArrayList<Vector>((int) this.getBlockVolume());
		for (Iterator<Vector> iterator = this.iterator(); iterator.hasNext();)
				vectors.add(iterator.next());
		return vectors;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Location> getLocations() {
		ArrayList<Location> locations = new ArrayList<Location>((int) this.getBlockVolume());
		for (Iterator<Location> iterator = this.getLocationIterator(); iterator.hasNext();)
			locations.add(iterator.next());
		return locations;
	}

}
