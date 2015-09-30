package net.mmhan.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mmhan on 30/9/15.
 */
public class HomeActivity extends AppCompatActivity{


    @Bind(R.id.phone_container)
    FrameLayout frameLayout;

    boolean isPhone = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        if(frameLayout == null){
            //this is tablet
            isPhone = false;
        }else{
            if(savedInstanceState == null){
                MainFragment mainFragment = new MainFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.phone_container, mainFragment)
                        .commit();
            }
        }
    }
}
