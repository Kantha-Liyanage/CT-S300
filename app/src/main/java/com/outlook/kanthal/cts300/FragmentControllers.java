package com.outlook.kanthal.cts300;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import it.beppi.knoblibrary.Knob;

public class FragmentControllers extends Fragment{

    Context context;
    Knob knobMasterVolume;
    Knob knobReverb;
    Knob knobModulation;
    Knob knobBrightness;

    public FragmentControllers(Context context){
        super();
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.controllers, container, false);

        knobMasterVolume = view.findViewById(R.id.knobMasterVolume);
        knobMasterVolume.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MIDIMessenger.changeMasterValume(convertKnowStateToMidiValue(state));
            }
        });

        knobReverb = view.findViewById(R.id.knobReverb);
        knobReverb.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MIDIMessenger.changeReverb(convertKnowStateToMidiValue(state));
            }
        });

        knobModulation = view.findViewById(R.id.knobModulation);
        knobModulation.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MIDIMessenger.changeModulation(convertKnowStateToMidiValue(state));
            }
        });

        knobBrightness = view.findViewById(R.id.knobBrightness);
        knobBrightness.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int state) {
                MIDIMessenger.changeBrightness(convertKnowStateToMidiValue(state));
            }
        });

        Switch switchLock = view.findViewById(R.id.switchLock);
        switchLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                FragmentControllers.this.knobMasterVolume.setEnabled(!checked);
                FragmentControllers.this.knobReverb.setEnabled(!checked);
                FragmentControllers.this.knobModulation.setEnabled(!checked);
                FragmentControllers.this.knobBrightness.setEnabled(!checked);
            }
        });

        return view;
    }

    private int convertKnowStateToMidiValue(int state){
        return ( 127 / 20 ) * ( state + 1 );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
