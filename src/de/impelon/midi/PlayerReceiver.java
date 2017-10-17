package de.impelon.midi;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

/**
 * <p> Receives MidiMessages and broadcast Minecraft sound to given Players accordingly. </p>
 * 
 * @see MinecraftSoundReceiver
 * 
 * @author Impelon
 *
 */
public class PlayerReceiver extends MinecraftSoundReceiver {
	
	protected List<Player> players;
	protected InstrumentMap instrumentmap;
	protected boolean serverwide = false;
	
	/**
	 * <p> Creates a new PlayerReceiver with the given Players and the given InstrumentMap. </p>
	 * 
	 * @param players a List<Player> to use to play Sounds
	 * @param serverwide whether the sound is broadcast to all Players
	 * @param instrumentmap an InstrumentMap
	 */
	public PlayerReceiver(List<Player> players, boolean serverwide, InstrumentMap instrumentmap) {
		super();
		this.players = players;
		this.serverwide = serverwide;
		this.instrumentmap = instrumentmap;
	}
	
	/**
	 * <p> Creates a new PlayerReceiver with the given Players. </p>
	 * 
	 * @param players a List<Player> to use to play Sounds
	 */
	public PlayerReceiver(List<Player> players) {
		this(players, false, InstrumentMap.DEFAULT_MAP);
	}
	
	/**
	 * <p> Creates a new PlayerReceiver. </p>
	 * 
	 * @param serverwide whether the sound is broadcast to all Players
	 */
	public PlayerReceiver(boolean serverwide) {
		this(new ArrayList<Player>(), serverwide, InstrumentMap.DEFAULT_MAP);
	}
	
	/**
	 * <p> Returns an Interable<Player> of all Players the Sounds are broadcasted to. 
	 * If {@linkplain #getServerwideBroadcast()} returns false this is the same as {@linkplain #getPlayers()}. </p>
	 * 
	 * @return The Iterable<Player>
	 */
	@SuppressWarnings("unchecked")
	public Iterable<Player> getActualPlayers() {
		return (this.serverwide ? (Iterable<Player>) Bukkit.getOnlinePlayers() : this.players);
	}	
	
	/**
	 * <p> Returns a List of the Players. </p>
	 * 
	 * @return The List<Player>
	 */
	public List<Player> getPlayers() {
		return this.players;
	}
	
	/**
	 * <p> Returns whether the sound is broadcast to all Players. </p>
	 * 
	 * @return Whether the sound is broadcast to all Players
	 */
	public boolean getServerwideBroadcast() {
		return this.serverwide;
	}
	
	/**
	 * <p> Set whether the sound is broadcast to all Players. </p>
	 * 
	 * @param serverwide whether the sound is broadcast to all Players
	 */
	public void setServerwideBroadcast(boolean serverwide) {
		this.serverwide = serverwide;
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
		for (Player player : this.getActualPlayers())
			player.playSound(player.getLocation(), this.getChannelInstrument(channel), SoundCategory.MASTER, 
					this.getActualVolume(channel, velocity), this.getPitchFromKey(key));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void stopNote(int channel, int key, int velocity) {
		for (Player player : this.getActualPlayers())
			player.stopSound(this.getChannelInstrument(channel), SoundCategory.MASTER);
	}

}
