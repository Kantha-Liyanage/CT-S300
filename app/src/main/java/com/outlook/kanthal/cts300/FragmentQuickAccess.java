package com.outlook.kanthal.cts300;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import yuku.ambilwarna.AmbilWarnaDialog;

public class FragmentQuickAccess extends Fragment{

    private Context context;
    private DataHelper dataHelper;
    private int pageNumer;

    public FragmentQuickAccess(Context context, DataHelper dataHelper, int pageNumer){
        super();
        this.context = context;
        this.dataHelper = dataHelper;
        this.pageNumer = pageNumer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.quick_access, container, false);

        // Get saved Quick Access details
        ArrayList<QuickAccessTone> quickAccessTones = this.dataHelper.getQuickAccessTones();

        // Get all buttons
        Button[] quickAccessButtons = new Button[25];
        for(int i=0; i<quickAccessButtons.length; i++) {
            String buttonID = "button" + (i + 1);
            int pageKeyNumer = (i + pageNumer * 100 );
            int resID = getResources().getIdentifier(buttonID, "id", context.getPackageName());
            quickAccessButtons[i] = ((Button) view.findViewById(resID));
            quickAccessButtons[i].setTag(R.string.KEY_NUMBER, pageKeyNumer);

            // Assign saved tone
            if(quickAccessTones != null){
                for (QuickAccessTone quickAccessTone: quickAccessTones) {
                    if(quickAccessTone.keyNumber == pageKeyNumer){
                        quickAccessButtons[i].setTag(R.string.QUICK_ACCESS_TONE, quickAccessTone);
                        quickAccessButtons[i].setText(quickAccessTone.patchName);
                        if(!quickAccessTone.color.equals("")) {
                            int color = Integer.parseInt(quickAccessTone.color);
                            quickAccessButtons[i].getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                            quickAccessButtons[i].setTextColor(FragmentQuickAccess.getForegroundColor(color));
                        }
                        break;
                    }
                }
            }

            quickAccessButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    quickAccessButtonClicked(view);
                }
            });
            quickAccessButtons[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    quickAccessButtonLongClicked(view);
                    return false;
                }
            });
        }

        return view;
    }

    public void quickAccessButtonClicked(View view) {
        Button button = (Button)view;
        QuickAccessTone tone = (QuickAccessTone)button.getTag(R.string.QUICK_ACCESS_TONE);
        if(tone != null){
            if(MIDIMessenger.changePatch(tone.programChange, tone.bankSelect)){
                MainActivity.setDisplayTone(tone.patchNumber, tone.patchName);
            }
        }
    }

    public void quickAccessButtonLongClicked(View view){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());
        builderSingle.setTitle("Tones");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.context, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(this.dataHelper.getTonesList());

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        MyDialogInterfaceOnClickListener ear = new MyDialogInterfaceOnClickListener(view);
        builderSingle.setAdapter(arrayAdapter, ear);
        builderSingle.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private static int getForegroundColor(int color){
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        if ( (red*0.299) + (green*0.587) + (blue*0.114) > 186){
            return Color.BLACK;
        }
        else{
            return Color.WHITE;
        }
    }

    class MyDialogInterfaceOnClickListener implements DialogInterface.OnClickListener{

        private View view;
        private Button selectedButton;

        public MyDialogInterfaceOnClickListener(View view){
            this.view = view;
        }

        @Override
        public void onClick(DialogInterface dialog, int selectedIndex) {
            //Get Tone
            Tone tone = dataHelper.getTone(selectedIndex + 1);

            //Clicked button
            Button button = (Button)view;

            //Old color if available
            QuickAccessTone quickAccessToneOld = (QuickAccessTone)button.getTag(R.string.QUICK_ACCESS_TONE);
            int colorOld = 0;
            if(quickAccessToneOld != null){
                if(!quickAccessToneOld.color.equals("")){
                    colorOld = Integer.parseInt(quickAccessToneOld.color);
                }
            }
            if(colorOld == 0){
                colorOld = Integer.parseInt("EAE9E9", 16);
            }

            //Save
            QuickAccessTone quickAccessTone = new QuickAccessTone();
            quickAccessTone.keyNumber = Integer.parseInt(button.getTag(R.string.KEY_NUMBER).toString());
            quickAccessTone.patchNumber = tone.patchNumber;
            quickAccessTone.color = "";
            quickAccessTone.patchName = tone.patchName;
            quickAccessTone.programChange = tone.programChange;
            quickAccessTone.bankSelect = tone.bankSelect;
            if(dataHelper.saveQuickAccessTone(quickAccessTone)){
                button.setText(tone.patchName);
                button.setTag(R.string.QUICK_ACCESS_TONE, quickAccessTone);
            }
            else{
                MainActivity.setDisplay("Quick Access Save Error!");
            }

            //Close
            dialog.dismiss();

            //Set Color
            this.selectedButton = button;
            new AmbilWarnaDialog(this.view.getContext(), colorOld,
                    new AmbilWarnaDialog.OnAmbilWarnaListener(){
                        @Override
                        public void onCancel(AmbilWarnaDialog dialog) {

                        }

                        @Override
                        public void onOk(AmbilWarnaDialog dialog, int color) {
                            //Set color and save
                            Button button = MyDialogInterfaceOnClickListener.this.selectedButton;
                            QuickAccessTone quickAccessTone = (QuickAccessTone)button.getTag(R.string.QUICK_ACCESS_TONE);
                            quickAccessTone.color = color + "";
                            if(dataHelper.saveQuickAccessTone(quickAccessTone)){
                                button.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                                button.setTextColor(FragmentQuickAccess.getForegroundColor(color));
                            }
                            else{
                                MainActivity.setDisplay("Quick Access Color Save Error!");
                            }
                        }
                    }).show();
        }
    }
}
