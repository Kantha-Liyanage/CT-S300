package com.outlook.kanthal.cts300;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentControllers extends Fragment implements SeekBar.OnSeekBarChangeListener {

    Context context;

    public FragmentControllers(Context context){
        super();
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.controllers, container, false);

        SeekBar seekBarReverb = view.findViewById(R.id.seekBarReverb);
        seekBarReverb.setOnSeekBarChangeListener(this);
        SeekBar seekBarModulation = view.findViewById(R.id.seekBarModulation);
        seekBarModulation.setOnSeekBarChangeListener(this);
        SeekBar seekBarBrightness = view.findViewById(R.id.seekBarBrightness);
        seekBarBrightness.setOnSeekBarChangeListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
        if(seekBar.getId() == R.id.seekBarReverb) {
           MIDIMessenger.changeReverb(value);
        }
        else if(seekBar.getId() == R.id.seekBarModulation) {
            MIDIMessenger.changeModulation(value);
        }
        else if(seekBar.getId() == R.id.seekBarBrightness) {
            MIDIMessenger.changeBrightness(value);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
