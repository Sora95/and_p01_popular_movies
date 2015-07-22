package net.mmhan.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.mmhan.popularmovies.model.MovieService;
import net.mmhan.popularmovies.model.MoviesResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {

    public final String LOG_TAG = this.getClass().getName();

    @Bind(R.id.rview_grid)
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    ArrayList<MoviesResult.Movie> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMovies = new ArrayList<>();
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieThumbnailAdapter(mMovies);
        mRecyclerView.setAdapter(mAdapter);


        MovieService.Implementation
                .get(getString(R.string.api_key))
                .popular(new Callback<MoviesResult>() {
                    @Override
                    public void success(MoviesResult moviesResult, Response response) {
                        for(MoviesResult.Movie m : moviesResult.getMovies()){
                            mMovies.add(m);
                            mAdapter.notifyDataSetChanged();
                            Log.e(LOG_TAG, "Movie: " + m.title);
                        }
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(LOG_TAG, error.toString());
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MovieThumbnailAdapter extends RecyclerView.Adapter<MainActivity.MovieThumbnailAdapter.MovieThumbnailViewHolder>{

        List<MoviesResult.Movie> mData;

        class MovieThumbnailViewHolder extends RecyclerView.ViewHolder{

            @Bind(R.id.tv_title)
            public TextView mTextView;
            @Bind(R.id.iv_thumbnail)
            public ImageView mImageView;

            public MovieThumbnailViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
                mTextView.getText();
            }
        }


        public MovieThumbnailAdapter(List<MoviesResult.Movie> movies){
            mData = movies;
        }

        @Override
        public MovieThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // inflate a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.thumbnail_view, parent, false);
            MovieThumbnailViewHolder vh = new MovieThumbnailViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MovieThumbnailViewHolder holder, int position) {
            holder.mTextView.setText(mData.get(position).title);
            //IMAGE
            Glide.with(holder.mImageView.getContext())
                    .load(mData.get(position).getPosterUrl())
//                    .error(R.drawable.placeholder) TODO
//                    .placeholder(R.drawable.placeholder) TODO
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}
