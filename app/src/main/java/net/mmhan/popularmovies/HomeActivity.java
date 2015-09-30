package net.mmhan.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

/**
 * Created by mmhan on 30/9/15.
 */
public class HomeActivity extends AppCompatActivity{


    FrameLayout frameLayout;

    boolean isPhone = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        frameLayout = (FrameLayout) findViewById(R.id.phone_container);

        if(frameLayout == null){
            //this is tablet
            isPhone = false;
        }else{
            if(savedInstanceState == null){
                MainFragment mainFragment = MainFragment.newInstance(isPhone);
                getFragmentManager().beginTransaction()
                        .add(R.id.phone_container, mainFragment)
                        .commit();
            }
        }
    }
}
