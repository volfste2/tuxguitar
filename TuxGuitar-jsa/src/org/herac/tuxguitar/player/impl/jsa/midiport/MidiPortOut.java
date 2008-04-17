package org.herac.tuxguitar.player.impl.jsa.midiport;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import org.herac.tuxguitar.player.base.MidiControllers;
import org.herac.tuxguitar.player.base.MidiOut;
import org.herac.tuxguitar.player.base.MidiPlayerException;
import org.herac.tuxguitar.player.base.MidiPort;
import org.herac.tuxguitar.player.impl.jsa.utils.MidiMessageUtils;

public class MidiPortOut extends MidiPort{

	private MidiOutImpl out;

	public MidiPortOut(MidiDevice device){
		super(device.getDeviceInfo().getName(),device.getDeviceInfo().getName());
		this.out = new MidiOutImpl(device);
	}
	
	public void open(){
		this.out.getReceiver();
	}
	
	public void close(){
		this.out.close();
	}
	
	public MidiOut out(){
		return this.out;
	}
	
	public void check() throws MidiPlayerException{
		try {
			this.out.open();
		} catch (Throwable throwable) {
			throw new MidiPlayerException(throwable.getMessage(),throwable);
		}
	}	
}

class MidiOutImpl implements MidiOut{
	
	private MidiDevice device;
	private Receiver receiver;

	public MidiOutImpl(MidiDevice device){
		this.device = device;		
	}

	protected synchronized void open() throws MidiUnavailableException{
		if(!this.device.isOpen()){
			this.device.open();
		}
		if(this.receiver == null){
			this.receiver = this.device.getReceiver();
		}
	}

	protected synchronized void close(){
		if(this.receiver != null){
			this.receiver.close();
			this.receiver = null;
		}
		if(this.device.isOpen()){
			this.device.close();
		}
	}
	
	protected Receiver getReceiver(){
		try {
			this.open();
		} catch (Throwable throwable) {
			// Do nothing
		}
		return this.receiver;
	}

	public void sendSystemReset(){
		if(getReceiver() != null){
			getReceiver().send(MidiMessageUtils.systemReset(),-1);
		}
	}
	
	public void sendAllNotesOff(){
		if(getReceiver() != null){
			for(int channel = 0; channel < 16; channel ++){
				getReceiver().send(MidiMessageUtils.controlChange(channel, MidiControllers.ALL_NOTES_OFF,0),-1);
			}
		}
	}
	
	public void sendNoteOn(int channel, int key, int velocity) {
		if(getReceiver() != null){
			getReceiver().send(MidiMessageUtils.noteOn(channel, key, velocity),-1);
		}		
	}
	
	public void sendNoteOff(int channel, int key, int velocity) {
		if(getReceiver() != null){
			getReceiver().send(MidiMessageUtils.noteOff(channel, key, velocity),-1);
		}
	}

	public void sendControlChange(int channel, int controller, int value) {
		if(getReceiver() != null){
			getReceiver().send(MidiMessageUtils.controlChange(channel,controller, value),-1);
		}	
	}
	
	public void sendProgramChange(int channel, int value) {
		if(getReceiver() != null){
			getReceiver().send(MidiMessageUtils.programChange(channel, value),-1);			
		}	
	}
	public void sendPitchBend(int channel, int value) {
		if(getReceiver() != null){
			getReceiver().send(MidiMessageUtils.pitchBend(channel, value),-1);
		}
	}
}