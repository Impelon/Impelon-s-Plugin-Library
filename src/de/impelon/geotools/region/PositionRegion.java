package de.impelon.geotools.region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import de.impelon.geotools.Axis;
import de.impelon.geotools.RegionFormat;
import de.impelon.geotools.area.IArea;

/**
 * <p> Implementation of IRegion for a free-style region. </p>
 * <p> Currently uses a {@linkplain HashSet} to store Positions (as {@linkplain Vector}). </p>
 * 
 * @author Impelon
 *
 */
public class PositionRegion implements IRegion {
	
	protected final World world;
	protected final HashSet<Vector> positions;
	protected Double xLength = null;
	protected Double yLength = null;
	protected Double zLength = null;
	protected Double surfaceArea = null;
	protected Long blockSurfaceArea = null;
	protected Long blockVolume = null;
	
	/**
	 * <p> Create a PositionRegion from another Region. </p>
	 * 
	 * @param region Region to create the PositionRegion from
	 */
	public PositionRegion(IRegion region) {
		this(region.getWorld(), region.getVectors());
	}
	
	/**
	 * <p> Create a PositionRegion from a Collection of Locations. </p>
	 * 
	 * @param positions a Collection with all Locations this Region should contain
	 */
	public PositionRegion(Collection<Location> positions) {
		this(positions.iterator().next().getWorld(), convertToVectorHashSet(positions));
	}
	
	/**
	 * <p> Create a PositionRegion from a Collection of Vectors. </p>
	 * 
	 * @param positions a Collection with all Vectors this Region should contain
	 */
	public PositionRegion(World world, Collection<Vector> positions) {
		this(world, new HashSet<Vector>(positions));
	}
	
	/**
	 * <p> Create a PositionRegion from a HashSet of Vectors. </p>
	 * 
	 * @param positions a HashSet with all Vectors this Region should contain
	 */
	public PositionRegion(World world, HashSet<Vector> positions) {
		this.world = world;
		if (positions == null)
			this.positions = new HashSet<Vector>();
		else
			this.positions = positions;
	}
	
	/**
	 * <p> Adds all Vectors of the given Region to this PositionRegion. </p>
	 * 
	 * @see PositionRegion#addAll(Collection)
	 * @param region add Positions of that Region
	 * @return Whether this Region changed as a result of the call
	 */
	public boolean add(IRegion region) {
		return this.addAll(region.getVectors());
	}
	
	/**
	 * <p> Removes all Vectors of the given Region from this PositionRegion. </p>
	 * 
	 * @see PositionRegion#removeAll(Collection)
	 * @param region remove Positions of that Region
	 * @return Whether this Region changed as a result of the call
	 */
	public boolean remove(IRegion region) {
		return this.removeAll(region.getVectors());
	}
	
	/**
	 * <p> Only keeps those Vectors that intersect from the two regions. </p>
	 * 
	 * @see PositionRegion#retainAll(Collection)
	 * @param region Region to intersect with
	 * @return Whether this Region changed as a result of the call
	 */
	public boolean retainIntersecting(IRegion region) {
		return this.retainAll(region.getVectors());
	}
	
	/**
	 * <p> Adds all Vectors of the given Collection to this PositionRegion. </p>
	 * 
	 * @see Collection#addAll(Collection)
	 * @param vectors collection containing elements to be added
	 * @return Whether this Region changed as a result of the call
	 */
	public boolean addAll(Collection<Vector> vectors) {
		this.invalidate();
		return this.positions.addAll(vectors);
	}
	
	/**
	 * <p> Removes all Vectors of the given Collection from this PositionRegion. </p>
	 * 
	 * @see Collection#removeAll(Collection)
	 * @param vectors collection containing elements to be removed
	 * @return Whether this Region changed as a result of the call
	 */
	public boolean removeAll(Collection<Vector> vectors) {
		this.invalidate();
		return this.positions.removeAll(vectors);
	}
	
	/**
	 * <p> Removes all Vectors that match the given filter. </p>
	 * 
	 * @see Collection#removeIf(Predicate)
	 * @param filter a predicate which returns true for elements to be removed
	 * @return Whether this Region changed as a result of the call
	 */
	public boolean removeIf(Predicate<? super Vector> filter) {
		this.invalidate();
		return this.positions.removeIf(filter);
	}
	
	/**
	 * <p> Only keeps those Vectors that intersect with the Collection. </p>
	 * 
	 * @see Collection#retainAll(Collection)
	 * @param vectors collection containing elements to be retained
	 * @return Whether this Region changed as a result of the call
	 */
	public boolean retainAll(Collection<Vector> vectors) {
		this.invalidate();
		return this.positions.retainAll(vectors);
	}
	
	/**
	 * <p> Invalidates all cached results. </p>
	 */
	public void invalidate() {
		this.xLength = null;
		this.yLength = null;
		this.zLength = null;
		this.surfaceArea = null;
		this.blockSurfaceArea = null;
		this.blockVolume = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public World getWorld() {
		return world;
	}
	
	/**
	 * <p> Calculates the length of this Area on the given axis. </p>
	 * 
	 * @see PositionRegion#getLength(Axis)
	 * @return The length of the area on that axis
	 */
	protected double calculateLength(Axis axis) {
		if (this.positions.isEmpty())
			return 0;
		Vector smallest = this.positions.iterator().next();
		Vector largest = smallest;
		for (Vector v : this.positions) {
			smallest = Vector.getMinimum(smallest, v);
			largest = Vector.getMaximum(largest, v);
		}
		switch (axis) {
		case X:
			this.xLength = largest.getX() - smallest.getX();
			return this.xLength;
		case Y:
			this.yLength = largest.getY() - smallest.getY();
			return this.yLength;
		case Z:
			this.zLength = largest.getZ() - smallest.getZ();
			return this.zLength;
		default:
			return 0;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p> The result is cached. </p>
	 */
	@Override
	public double getLength(Axis axis) {
		switch (axis) {
		case X:
			if (this.xLength == null)
				this.calculateLength(Axis.X);
			return this.xLength;
		case Y:
			if (this.yLength == null)
				this.calculateLength(Axis.Y);
			return this.yLength;
		case Z:
			if (this.zLength == null)
				this.calculateLength(Axis.Z);
			return this.zLength;
		default:
			return 0;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p> The result is cached. </p>
	 */
	@Override
	public long getBlockLength(Axis axis) {
		return NumberConversions.ceil(this.getLength(axis));
	}
	
	/**
	 * <p> Calculates the surface area of this Area. </p>
	 * 
	 * @see PositionRegion#getSurfaceArea()
	 * @return The surface area
	 */
	protected double calculateSurfaceArea() {
		HashSet<Vector> uniques = new HashSet<Vector>();
		for (Iterator<Vector> iterator = this.iterator(); iterator.hasNext();)
			uniques.add(iterator.next().setY(0));
		this.surfaceArea = (double) uniques.size();
		return this.surfaceArea;
	}
	
	/**
	 * {@inheritDoc}
	 * <p> The result is cached. </p>
	 */
	@Override
	public double getSurfaceArea() {
		if (this.surfaceArea == null)
			this.calculateSurfaceArea();
		return this.surfaceArea;
	}
	
	/**
	 * <p> Calculates the amount of Blocks contained in this Area. </p>
	 * 
	 * @see PositionRegion#getBlockSurfaceArea()
	 * @return The surface area
	 */
	protected double calculateBlockSurfaceArea() {
		HashSet<BlockVector> uniques = new HashSet<BlockVector>();
		for (Iterator<Vector> iterator = this.iterator(); iterator.hasNext();)
			uniques.add(iterator.next().setY(0).toBlockVector());
		this.blockSurfaceArea = (long) uniques.size();
		return this.blockSurfaceArea;
	}
	
	/**
	 * {@inheritDoc}
	 * <p> The result is cached. </p>
	 */
	@Override
	public long getBlockSurfaceArea() {
		if (this.blockSurfaceArea == null)
			this.calculateBlockSurfaceArea();
		return this.blockSurfaceArea;
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
		return this.positions.contains(pos);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getOverlap(IArea area) {
		if (area.getWorld() != this.getWorld())
			return false;
		for (Iterator<Vector> iterator = this.iterator(); iterator.hasNext();)
			if (area.getOverlap(iterator.next()))
				return true;
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean getOverlap(IRegion region) {
		if (region.getWorld() != this.getWorld())
			return false;
		for (Iterator<Vector> iterator = this.iterator(); iterator.hasNext();)
			if (region.getOverlap(iterator.next()))
				return true;
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getVolume() {
		return this.positions.size();
	}
	
	/**
	 * <p> Calculates the amount of Blocks this Region contains. </p>
	 * 
	 * @see PositionRegion#getBlockVolume()
	 * @return The volume
	 */
	protected long calculateBlockVolume() {
		this.blockVolume = (long) this.getSubRegion(RegionFormat.FLOORED).getVolume();
		return this.blockVolume;
	}
	
	/**
	 * {@inheritDoc}
	 * <p> The result is cached. </p>
	 */
	@Override
	public long getBlockVolume() {
		if (this.blockVolume == null)
			this.calculateBlockVolume();
		return this.blockVolume;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IRegion getSubRegion(RegionFormat format) {
		switch (format) {
		case ENCLOSED:
			HashSet<Vector> enclosed = new HashSet<Vector>();
			for (Iterator<Vector> iterator = this.iterator(); iterator.hasNext();) {
				Vector help = iterator.next();
				Vector v = help.clone();
				if (this.getOverlap(v.setX(help.getX() + 1)) && this.getOverlap(v.setX(help.getX() - 1)) &&
					this.getOverlap(v.setY(help.getY() + 1)) && this.getOverlap(v.setY(help.getY() - 1)) &&
					this.getOverlap(v.setZ(help.getZ() + 1)) && this.getOverlap(v.setZ(help.getZ() - 1)))
					enclosed.add(help);
			}
			return new PositionRegion(this.getWorld(), enclosed);
		case FLOORED:
			HashSet<Vector> floored = new HashSet<Vector>();
			for (Iterator<Vector> iterator = this.iterator(); iterator.hasNext();) {
				Vector v = iterator.next();
				floored.add(new Vector(v.getBlockX(), v.getBlockY(), v.getBlockZ()));
			}
			return new PositionRegion(this.getWorld(), floored);
		case SURROUNDING:
			HashSet<Vector> surrounding = new HashSet<Vector>();
			for (Iterator<Vector> iterator = this.iterator(); iterator.hasNext();) {
				Vector help = iterator.next();
				Vector v = help.clone();
				surrounding.add(v);
				surrounding.add(v.setX(help.getX() + 1));
				surrounding.add(v.setX(help.getX() - 1));
				surrounding.add(v.setY(help.getY() + 1));
				surrounding.add(v.setY(help.getY() - 1));
				surrounding.add(v.setZ(help.getZ() + 1));
				surrounding.add(v.setZ(help.getZ() - 1));
			}
			return new PositionRegion(this.getWorld(), surrounding);
		case WIREFRAME:
			PositionRegion wireframe = new PositionRegion(this.getWorld(), this.positions);
			IRegion inside = this.getSubRegion(RegionFormat.ENCLOSED).getSubRegion(RegionFormat.SURROUNDING);
			wireframe.remove(inside);
			return wireframe;
		case HOLLOW:
			PositionRegion hollow = new PositionRegion(this.getWorld(), this.positions);
			hollow.remove(this.getSubRegion(RegionFormat.ENCLOSED));
			return hollow;
		case FULL:
		default:
			return new PositionRegion(this.getWorld(), this.positions);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Vector> iterator() {
		return this.positions.iterator();
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
	
	/**
	 * <p> Converts a Collection of Locations to a HashSet of Vectors. </p>
	 * 
	 * @return The HashSet<Vector>
	 */
	protected static HashSet<Vector> convertToVectorHashSet(Collection<Location> collection) {
		HashSet<Vector> set = new HashSet<Vector>();
		for (Location object : collection)
			set.add(object.toVector());
		return set;
	}

}
