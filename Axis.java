package de.impelon.geotools;

import org.bukkit.block.BlockFace;

/**
 * <p> Enumeration for referencing directions. </p>
 * 
 * @author Impelon
 *
 */
public enum Axis {
	
	X,
	Y,
	Z;


	public static Axis fromBlockFace(BlockFace blockface) {
		if (blockface.getModX() != 0)
			return X;
		if (blockface.getModY() != 0)
			return Y;
		if (blockface.getModZ() != 0)
			return Z;
		return null;
	}
	
}
