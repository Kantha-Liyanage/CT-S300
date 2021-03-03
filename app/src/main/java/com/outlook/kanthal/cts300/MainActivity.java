package com.outlook.kanthal.cts300;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static TextView textViewPatch;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        dataHelper = new DataHelper(this);
        //Initialize MIDI
        new MIDIMessenger(this);

        textViewPatch = findViewById(R.id.textViewPatch);
        viewPager = findViewById(R.id.viewPager);

        //Get Page Count
        int pages = Integer.parseInt(dataHelper.getSetting("QUICK_ACCESS_PAGES"));
        setPages(pages);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setPages(int pagesCount){
        //Quick Access Pages
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        for(int i=0;i<pagesCount;i++){
            viewPagerAdapter.addFragment(new FragmentQuickAccess(this, dataHelper, i));
        }
        //Fixed Pages
        viewPagerAdapter.addFragment(new FragmentControllers(this));
        viewPagerAdapter.addFragment(new FragmentDialPad(this, dataHelper));

        viewPager.setAdapter(viewPagerAdapter);
    }

    public static void setDisplay(String text){
        MainActivity.textViewPatch.setText(text);
    }

    public static void setDisplayTone(int patchNumber, String patchName){
        MainActivity.textViewPatch.setText(patchNumber + " | " + patchName);
    }

    public void settingsButtonClicked(View view){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(view.getContext());
        builderSingle.setTitle("Quick Access Pages");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(new String[]{"1","2","3","4","5","6","7","8","9","10"});

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        //Save
                        dataHelper.saveSetting("QUICK_ACCESS_PAGES", (index+1) + "");

                        //Remove old
                        for(int i=0;i<viewPagerAdapter.getCount();i++){
                            Fragment fragment = viewPagerAdapter.getItem(i);
                            fragment = null;
                        }
                        viewPagerAdapter = null;

                        //add pages
                        setPages(index+1);

                        //Clode dialog
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }
}