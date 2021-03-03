package com.outlook.kanthal.cts300;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentDialPad extends Fragment {

    Context context;
    DataHelper dataHelper;

    char[] patchNumber = {'0','0','0'};
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

        return view;
    }

    public void dialPadButtonClicked(View view) {
        Button button = (Button)view;
        char key = button.getText().toString().toCharArray()[0];
        patchNumber[0] = patchNumber[1];
        patchNumber[1] = patchNumber[2];
        patchNumber[2] = key;

        position--;
        if(position==-1){
            position = 2;
        }

        //Get patch
        String val = new String(patchNumber);
        val = val.replaceFirst("^0+(?!$)", "");
        int number = Integer.parseInt(val);
        if(number > 0 && number <= 400){
            Tone tone = dataHelper.getTone(Integer.parseInt(val));
            //Call patch
            if(MIDIMessenger.changePatch(tone.programChange, tone.bankSelect)){
                MainActivity.setDisplayTone(tone.patchNumber,tone.patchName);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
