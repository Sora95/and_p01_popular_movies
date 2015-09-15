package net.mmhan.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mmhan.popularmovies.model.FavoriteMovie;
import net.mmhan.popularmovies.model.Movie;
import net.mmhan.popularmovies.model.MovieService;
import net.mmhan.popularmovies.model.TrailersResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MovieDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "net.mmhan.popularmovies.extra_movie";
    private final String LOG_TAG = this.getClass().getName();
    Movie mMovie;

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
    @Bind(R.id.fab_watch)
    FloatingActionButton fabWatch;
    @Bind(R.id.rview_trailers)
    RecyclerView rviewTrailers;
    TrailersAdapter rviewAdapterTrailers;

    @Bind(R.id.nsv)
    NestedScrollView nestedScrollView;

    @Bind(R.id.lv_reviews)
    ListView lvReviews;

    boolean mIsFavorited = false;

    Menu mMenu;

    private List<TrailersResult.Trailer> mTrailers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        mMovie = (Movie) getIntent().getSerializableExtra(EXTRA_MOVIE);

        ViewCompat.setTransitionName(appbar, EXTRA_MOVIE);
        supportPostponeEnterTransition();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkIsInFavorite();

        updateUI();

        loadTrailers();

//        MovieService.Implementation
//                .get(getString(R.string.api_key))
//                .reviews(mMovie.getId(), new Callback<ReviewsResult>() {
//                    @Override
//                    public void success(ReviewsResult reviewsResult, Response response) {
//                        for(ReviewsResult.Review r : reviewsResult.results){
//                            Log.e(LOG_TAG, "Reviews received");
//                            Log.e(LOG_TAG, r.getAuthor() + " : " + r.getUrl());
//                        }
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        Snackbar
//                            .make(nestedScrollView, "There was an error in loading Reviews", Snackbar.LENGTH_SHORT)
//                            .show();
//                        Log.e(LOG_TAG, error.toString());
//                    }
//                });
    }



    private void checkIsInFavorite() {
        FavoriteMovie result = Realm.getInstance(this).where(FavoriteMovie.class)
                .equalTo("id", mMovie.getId())
                .findFirst();
        mIsFavorited = result != null;
    }

    private void updateUI() {
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

        updateMenuItem();

        RecyclerView.LayoutManager trailersLayout = new LinearLayoutManager(this);
        rviewTrailers.setLayoutManager(trailersLayout);
        rviewAdapterTrailers = new TrailersAdapter();
        rviewTrailers.setAdapter(rviewAdapterTrailers);
    }

    private void updateMenuItem(MenuItem item) {
        MenuItem menuItem;
        if(item == null) {
            if (mMenu == null) return;
            menuItem = mMenu.findItem(R.id.action_favorite);
        }else {
            menuItem = item;
        }

        if (mIsFavorited) {
            menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_toggle_star));
            menuItem.setTitle(getString(R.string.action_remove_from_favorites));
        } else {
            menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_toggle_star_outline));
            menuItem.setTitle(getString(R.string.action_add_to_favorites));
        }
    }
    private void updateMenuItem() {
        updateMenuItem(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        mMenu = menu;
        updateMenuItem();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            toggleFavorite(item);
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleFavorite(MenuItem item) {
        Realm realmObj = Realm.getInstance(this);
        realmObj.beginTransaction();
        if(mIsFavorited){
            Log.e(LOG_TAG, "Removing movie #" + mMovie.getId());
            realmObj.where(FavoriteMovie.class)
                    .equalTo("id", mMovie.getId())
                    .findFirst()
                    .removeFromRealm();
        }else {
            Log.e(LOG_TAG, "Saving movie #" + mMovie.getId());
            FavoriteMovie movie = mMovie.getRealmObject();
            realmObj.copyToRealmOrUpdate(movie);
        }

        realmObj.commitTransaction();
        Log.e(LOG_TAG, "Transaction completed with movie #" + mMovie.getId());
        mIsFavorited = !mIsFavorited;

        updateMenuItem(item);
    }

    class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersViewHolder>{

        class TrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            @Bind(R.id.rlayout_trailer_item)
            RelativeLayout mRelativeLayout;
            @Bind(R.id.lbl_trailer_item)
            TextView tvTrailerLabel;

            private TrailersResult.Trailer mTrailer;


            public TrailersViewHolder(View itemView) {
                super(itemView);

                setupView(itemView);
            }

            public void setTrailer(TrailersResult.Trailer mTrailer) {
                this.mTrailer = mTrailer;
                tvTrailerLabel.setText(mTrailer.getName());
            }

            private void setupView(View itemView) {
                ButterKnife.bind(this, itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setData(mTrailer.getYoutubeUri());
                startActivity(it);
            }
        }

        public TrailersAdapter() {
        }

        @Override
        public TrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trailer_item, parent, false);
            TrailersViewHolder vh = new TrailersViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(TrailersViewHolder holder, int position) {
            holder.setTrailer(mTrailers.get(position));
        }

        @Override
        public int getItemCount() {
            return mTrailers.size();
        }
    }

    private void loadTrailers() {
        mTrailers = new ArrayList<>();

        MovieService.Implementation
                .get(getString(R.string.api_key))
                .trailers(mMovie.getId(), new Callback<TrailersResult>() {
                    @Override
                    public void success(TrailersResult trailersResult, Response response) {
                        for(TrailersResult.Trailer t : trailersResult.results){
                            if(t.getType().equals("Trailer")){
                                mTrailers.add(t);
                            }
                        }
                        rviewAdapterTrailers.notifyDataSetChanged();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Snackbar
                                .make(nestedScrollView, "There was an error in loading Trailers", Snackbar.LENGTH_SHORT)
                                .show();
                        Log.e(LOG_TAG, error.toString());
                    }
                });
    }
}
