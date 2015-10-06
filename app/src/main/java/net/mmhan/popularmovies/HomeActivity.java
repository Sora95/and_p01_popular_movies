package net.mmhan.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

/**
 * Created by mmhan on 30/9/15.
 */
public class HomeActivity extends AppCompatActivity{


    FrameLayout frameLayout;


    private boolean isSinglePane = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        frameLayout = (FrameLayout) findViewById(R.id.phone_container);

        if(frameLayout == null){
            //this is tablet
            isSinglePane = false;
        }else{
            if(savedInstanceState == null){
                MainFragment mainFragment = new MainFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.phone_container, mainFragment)
                        .addToBackStack(mainFragment.getClass().getName())
                        .commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public boolean isSinglePane() {
        return isSinglePane;
    }
}
