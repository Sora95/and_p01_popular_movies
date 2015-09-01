package net.mmhan.popularmovies;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mmhan.popularmovies.model.MoviesResult;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MovieDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "net.mmhan.popularmovies.extra_movie";
    private final String LOG_TAG = this.getClass().getName();
    MoviesResult.Movie mMovie;

    @Bind(R.id.appbar)
    AppBarLayout appbar;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.backdrop)
    ImageView iv_backDrop;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_poster)
    ImageView iv_poster;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.tv_release_date)
    TextView tv_releaseDate;
    @Bind(R.id.tv_voteAvg)
    TextView tv_voteAvg;
    @Bind(R.id.tv_sypnosis)
    TextView tv_sypnosis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        mMovie = (MoviesResult.Movie) getIntent().getSerializableExtra(EXTRA_MOVIE);

        ViewCompat.setTransitionName(appbar, EXTRA_MOVIE);
        supportPostponeEnterTransition();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout.setTitle(mMovie.title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        Glide.with(iv_backDrop.getContext())
                .load(mMovie.getBackdropUrl())
                .centerCrop()
                // TODO: Find a way to add those back in without ruining how the posters look
//              .error(R.drawable.cloud_err)
//              .placeholder(R.drawable.cloud_placeholder)
                .crossFade()
                .into(iv_backDrop);

        Glide.with(iv_poster.getContext())
                .load(mMovie.getPosterUrl())
                .centerCrop()
                .crossFade()
                .into(iv_poster);

        tv_title.setText(mMovie.title);
        tv_releaseDate.setText(mMovie.release_date);
        tv_voteAvg.setText(mMovie.vote_average.toString());
        tv_sypnosis.setText(mMovie.overview);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_favorite) {
//            return true;
//        }else

        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
