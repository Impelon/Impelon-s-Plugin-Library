package de.impelon.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import org.bukkit.Sound;

/**
 * <p> Provides a basic structure to receive MidiMessages and broadcast Minecraft sound accordingly. </p>
 * 
 * @see Receiver
 * 
 * @author Impelon
 *
 */
public abstract class MinecraftSoundReceiver implements Receiver {
	
	protected Sound[] instruments = new Sound[16];
	protected float[] volumes = new float[16];
	protected float[] notes = new float[24];
	protected float mastervolume = 1.0f;
	protected boolean damped = false;
	protected int pitchbending = 8192;
	
	/**
	 * <p> Creates a new MinecraftSoundReceiver. </p>
	 */
	public MinecraftSoundReceiver() {
		this.setChannelVolumeMultiplicators(1.0f);
		for (int i = 0; i < this.notes.length; i++)
			this.notes[i] = (float) Math.pow(2, (i - 12.0) / 12.0);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p> Includes interpretation to convert to Minecraft's Sound engine. </p>
	 */
	@Override
	public void send(MidiMessage message, long timeStamp) {
		
        // ignore SysexMessage & MetaMessage (only useful for Sequencer/Lyrics)
		// https://de.wikipedia.org/wiki/Musical_Instrument_Digital_Interface
        
        if (message instanceof ShortMessage) {
            ShortMessage shortmessage = (ShortMessage) message;
            
            switch (shortmessage.getCommand()) {
            // ShortMessage.POLY_PRESSURE, ShortMessage.CHANNEL_PRESSURE is finetuning; probably overkill for the Minecraft sound engine
            // 0xFn is "system specific" control for sequencer
            case ShortMessage.NOTE_ON:
                if (shortmessage.getData2() > 0)
                    this.playNote(shortmessage.getChannel(), shortmessage.getData1(), shortmessage.getData2());
                else
                	this.stopNote(shortmessage.getChannel(), shortmessage.getData1(), shortmessage.getData2());
                break;
            case ShortMessage.NOTE_OFF:
            	this.stopNote(shortmessage.getChannel(), shortmessage.getData1(), shortmessage.getData2());
            	break;
            case ShortMessage.PITCH_BEND:
            	this.pitchbending = (shortmessage.getData2() * 128) + shortmessage.getData1();
            	break;
            case ShortMessage.PROGRAM_CHANGE:
                this.instruments[shortmessage.getChannel()] = this.getInstrument(shortmessage.getData1());
                break;
            case ShortMessage.CONTROL_CHANGE:
            	switch (shortmessage.getData1()) {
            	case 0x7: // channel volume / master volume
            	case 0x11: // expression volume
            		this.volumes[shortmessage.getChannel()] = shortmessage.getData2();
            		break;
            	case 0x43: // soft pedal
                	if (shortmessage.getData2() < 64)
                		this.damped = true;
                	else
                		this.damped = false;
            		break;
            	}
            	break;
            }
        }
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {}
	
	/**
	 * <p> Returns the Minecraft-pitch (ranging from 0.5f to 2.0f) corresponding to the give MIDI-key. </p>
	 * 
	 * @param key the MIDI-key
	 * @return The pitch ranging from 0.5f to 2.0f
	 */
	public float getPitchFromKey(int key) {
		return this.notes[(Math.abs(key) + 18) % this.notes.length] + ((this.pitchbending / 16384.0f) + 0.5f);
	}
	
	/**
	 * <p> Returns the actual volume that should be used when playing an instrument of the given channel with the given velocity. </p>
	 * 
	 * @param channel the MIDI-channel
	 * @param velocity the velocity
	 * @return The volume
	 */
	public float getActualVolume(int channel, int velocity) {
		return Math.min(this.mastervolume * this.getChannelVolumeMultiplicator(channel) * this.getVelocityVolumeMultiplicator(velocity), (this.damped ? 0.9f : Float.MAX_VALUE));
	}
	
	/**
	 * <p> Returns the volume multiplicator produced by a certain velocity. </p>
	 * 
	 * @param velocity the velocity
	 * @return The volume-multiplicator
	 */
	public float getVelocityVolumeMultiplicator(int velocity) {
		return 1.0f / (128.0f - (velocity % 128));
	}
	
	/**
	 * <p> Returns the Sound used for the given channel. </p>
	 * 
	 * @param channel the MIDI-channel
	 * @return The Sound
	 */
	public Sound getChannelInstrument(int channel) {
		return this.instruments[Math.abs(channel) % this.instruments.length];
	}
	
	/**
	 * <p> Returns the volume multiplicator used for the given channel. </p>
	 * 
	 * @param channel the MIDI-channel
	 * @return The volume-multiplicator
	 */
	public float getChannelVolumeMultiplicator(int channel) {
		return this.volumes[Math.abs(channel) % this.volumes.length];
	}
	
	/**
	 * <p> Set the volume multiplicator used for the given channel. </p>
	 * 
	 * @param volume the volume-multiplicator
	 */
	public void setChannelVolumeMultiplicators(float volume) {
		for (int i = 0; i < this.volumes.length; i++)
			this.volumes[i] = volume;
	}
	
	/**
	 * <p> Return the general volume multiplicator. </p>
	 * 
	 * @return The volume-multiplicator
	 */
	public float getMasterVolume() {
		return this.mastervolume;
	}
	
	/**
	 * <p> Set the general volume multiplicator. </p>
	 * 
	 * @param volume the volume-multiplicator
	 */
	public void setMasterVolume(float volume) {
		this.mastervolume = volume;
	}
	
	/**
	 * <p> Returns the Sound mapped to that MIDI-Instrument-ID. </p>
	 * 
	 * @param ID the MIDI-Instrument-ID
	 * @return The Sound
	 */
	protected abstract Sound getInstrument(int ID);
	
	/**
	 * <p> Plays a Note of a key with a velocity on a given channel. </p>
	 * 
	 * @param channel the MIDI-channel
	 * @param key the MIDI-key
	 * @param velocity the velocity
	 */
	protected abstract void playNote(int channel, int key, int velocity);
	
	/**
	 * <p> Stops a Note of a key with a velocity on a given channel. </p>
	 * 
	 * @param channel the MIDI-channel
	 * @param key the MIDI-key
	 * @param velocity the velocity
	 */
	protected abstract void stopNote(int channel, int key, int velocity);

}
