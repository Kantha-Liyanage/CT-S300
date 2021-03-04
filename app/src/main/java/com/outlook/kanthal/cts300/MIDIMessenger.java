package com.outlook.kanthal.cts300;

import android.app.Activity;
import android.content.Context;
import android.media.midi.MidiDevice;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiDeviceStatus;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiOutputPort;
import android.media.midi.MidiReceiver;
import android.os.Bundle;
import android.os.Looper;
import java.io.IOException;

public class MIDIMessenger {

    private MidiManager midiManager;
    private String manufacturer;
    private String product;
    private String name;
    private static MidiInputPort midiInputPort;
    private static MidiOutputPort midiOutputPort;

    private static int reverb;
    private static boolean reverbSet;

    private static int brightness;
    private static boolean brightnessSet;

    private static int modulation;
    private static boolean modulationSet;

    private static int resonance;
    private static boolean resonanceSet;

    public MIDIMessenger(Activity context){
        this.midiManager = (MidiManager)context.getSystemService(Context.MIDI_SERVICE);

        midiManager.registerDeviceCallback(new MidiManager.DeviceCallback() {
            @Override
            public void onDeviceAdded( MidiDeviceInfo info ) {
                int numInputs = info.getInputPortCount();

                Bundle properties = info.getProperties();
                MIDIMessenger.this.manufacturer = properties.getString(MidiDeviceInfo.PROPERTY_MANUFACTURER);
                MIDIMessenger.this.product = properties.getString(MidiDeviceInfo.PROPERTY_PRODUCT);
                MIDIMessenger.this.name = properties.getString(MidiDeviceInfo.PROPERTY_NAME);

                MidiDeviceInfo.PortInfo[] portInfos = info.getPorts();
                String portName = portInfos[0].getName();
                if (portInfos[0].getType() == MidiDeviceInfo.PortInfo.TYPE_INPUT) {
                }

                midiManager.openDevice(info, new MidiManager.OnDeviceOpenedListener() {
                    @Override
                    public void onDeviceOpened(MidiDevice device) {
                        if (device == null) {
                            MainActivity.setDisplay("MIDI Device Failed");
                        }
                        else {
                            MIDIMessenger.midiInputPort = device.openInputPort(0);
                            MIDIMessenger.midiOutputPort = device.openOutputPort(0);
                            MIDIMessenger.midiOutputPort.onConnect(new MIDIThrough());
                            MainActivity.setDisplay(MIDIMessenger.this.manufacturer + " | " + MIDIMessenger.this.product);
                        }
                    }}, new android.os.Handler(Looper.getMainLooper()));
            }

            @Override
            public void onDeviceRemoved(final MidiDeviceInfo info) {
                MainActivity.setDisplay("MIDI Device Not Connected");
            }

            // Update port open counts so user knows if the device is in use.
            @Override
            public void onDeviceStatusChanged(final MidiDeviceStatus status) {
                MidiDeviceInfo info = status.getDeviceInfo();
            }
        }, new android.os.Handler(Looper.getMainLooper()));
    }

    public class MIDIThrough extends MidiReceiver{
        @Override
        public void onSend(byte[] bytes, int offset, int count, long time) throws IOException {
            MIDIMessenger.midiInputPort.send(bytes, offset, count, time);
        }
    }

    public static boolean changePatch(int programChange, int bankSelect){
        if(MIDIMessenger.midiInputPort == null){
            return false;
        }

        byte[] buffer = new byte[3];

        buffer[0] = (byte)(0xB0 + 0);
        buffer[1] = (byte)0;
        buffer[2] = (byte)bankSelect;
        try{
            MIDIMessenger.midiInputPort.send(buffer, 0, 3);
        }
        catch (Exception er){
            return false;
        }

        buffer[0] = (byte)(0xB0 + 0);
        buffer[1] = (byte)32;
        buffer[2] = (byte)0;
        try{
            MIDIMessenger.midiInputPort.send(buffer, 0, 3);
        }
        catch (Exception er){
            return false;
        }

        buffer[0] = (byte)(0xC0 + 0);
        buffer[1] = (byte)programChange;
        try{
            MIDIMessenger.midiInputPort.send(buffer, 0, 2);

            if(MIDIMessenger.reverbSet){
                MIDIMessenger.changeReverb(MIDIMessenger.reverb);
            }

            if(MIDIMessenger.brightnessSet){
                MIDIMessenger.changeBrightness(MIDIMessenger.brightness);
            }

            if(MIDIMessenger.modulationSet){
                MIDIMessenger.changeModulation(MIDIMessenger.modulation);
            }

            if(MIDIMessenger.resonanceSet){
                MIDIMessenger.changeResonance(MIDIMessenger.resonance);
            }

            return true;
        }
        catch (Exception er){
            return false;
        }
    }

    public static boolean changeReverb(int value){
        if(MIDIMessenger.midiInputPort == null){
            return false;
        }

        byte[] buffer = new byte[3];

        buffer[0] = (byte)(0xB0 + 0);
        buffer[1] = (byte)91;
        buffer[2] = (byte)value;
        try{
            MIDIMessenger.midiInputPort.send(buffer, 0, 3);
            MIDIMessenger.reverbSet = true;
            MIDIMessenger.reverb = value;
            return  true;
        }
        catch (Exception er){
            return false;
        }
    }

    public static boolean changeBrightness(int value){
        if(MIDIMessenger.midiInputPort == null){
            return false;
        }

        byte[] buffer = new byte[3];

        buffer[0] = (byte)(0xB0 + 0);
        buffer[1] = (byte)74;
        buffer[2] = (byte)value;
        try{
            MIDIMessenger.midiInputPort.send(buffer, 0, 3);
            MIDIMessenger.brightness = value;
            MIDIMessenger.brightnessSet = true;
            return  true;
        }
        catch (Exception er){
            return false;
        }
    }

    public static boolean changeModulation(int value){
        if(MIDIMessenger.midiInputPort == null){
            return false;
        }

        byte[] buffer = new byte[3];

        buffer[0] = (byte)(0xB0 + 0);
        buffer[1] = (byte)1;
        buffer[2] = (byte)value;
        try{
            MIDIMessenger.midiInputPort.send(buffer, 0, 3);
            MIDIMessenger.modulation = value;
            MIDIMessenger.modulationSet = true;
            return  true;
        }
        catch (Exception er){
            return false;
        }
    }

    public static boolean changeResonance(int value){
        if(MIDIMessenger.midiInputPort == null){
            return false;
        }

        byte[] buffer = new byte[3];

        buffer[0] = (byte)(0xB0 + 0);
        buffer[1] = (byte)71;
        buffer[2] = (byte)value;
        try{
            MIDIMessenger.midiInputPort.send(buffer, 0, 3);
            MIDIMessenger.resonance = value;
            MIDIMessenger.resonanceSet = true;
            return  true;
        }
        catch (Exception er){
            return false;
        }
    }

    public static boolean changeMasterValume(int value){
        if(MIDIMessenger.midiInputPort == null){
            return false;
        }

        byte[] buffer = new byte[3];

        buffer[0] = (byte)(0xB0 + 0);
        buffer[1] = (byte)7;
        buffer[2] = (byte)value;
        try{
            MIDIMessenger.midiInputPort.send(buffer, 0, 3);
            return  true;
        }
        catch (Exception er){
            return false;
        }
    }

}
