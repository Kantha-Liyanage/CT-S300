package com.outlook.kanthal.cts300;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentDialPad extends Fragment {

    Context context;
    DataHelper dataHelper;

    char[] patchNumber = {'0','0','1'};
    int position = 2;

    public FragmentDialPad(Context context, DataHelper dataHelper){
        super();
        this.context = context;
        this.dataHelper = dataHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.dial_pad, container, false);

        // Get all buttons
        Button[] quickAccessButtons = new Button[10];
        for(int i=0; i<10; i++) {
            String buttonID = "button" + (i + 1);
            int resID = getResources().getIdentifier(buttonID, "id", context.getPackageName());
            quickAccessButtons[i] = ((Button) view.findViewById(resID));
            quickAccessButtons[i].setTextSize(30);
            quickAccessButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialPadButtonClicked(view);
                }
            });
        }

        ImageButton buttonUp = (ImageButton) view.findViewById(R.id.buttonUp);
        buttonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(1);
            }
        });
        ImageButton buttonDown = (ImageButton) view.findViewById(R.id.buttonDown);
        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate(-1);
            }
        });

        return view;
    }

    private void navigate(int direction){
        setPatch(direction);
    }

    private void dialPadButtonClicked(View view) {
        Button button = (Button)view;
        char key = button.getText().toString().toCharArray()[0];
        patchNumber[0] = patchNumber[1];
        patchNumber[1] = patchNumber[2];
        patchNumber[2] = key;

        position--;
        if(position==-1){
            position = 2;
        }

        //Set patch
        setPatch(0);
    }

    private void setPatch(int navigate){
        //Convert patch number
        String val = new String(patchNumber);
        val = val.replaceFirst("^0+(?!$)", "");
        int number = Integer.parseInt(val);

        if (navigate !=0){
            number += navigate;
        }

        if(number == 0){
            patchNumber = new char[]{'4','0','0'};
            number = 400;
        }
        else if(number==401){
            patchNumber = new char[]{'0','0','1'};
            number = 1;
        }
        else{
            String noStr = number + "";
            switch (noStr.length()){
                case 1:
                    patchNumber[0] = '0';
                    patchNumber[1] = '0';
                    patchNumber[2] = noStr.charAt(0);
                    break;
                case 2:
                    patchNumber[0] = '0';
                    patchNumber[1] = noStr.charAt(0);
                    patchNumber[2] = noStr.charAt(1);
                    break;
                case 3:
                    patchNumber[0] = noStr.charAt(0);
                    patchNumber[1] = noStr.charAt(1);
                    patchNumber[2] = noStr.charAt(2);
                    break;
            }

        }

        Tone tone = dataHelper.getTone(number);
        //Call patch
        if(MIDIMessenger.changePatch(tone.programChange, tone.bankSelect)){
            MainActivity.setDisplayTone(tone.patchNumber,tone.patchName);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
