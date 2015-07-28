package net.mmhan.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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

    private static final int GRIDVIEW_COLUMN_COUNT = 3;
    private final String LOG_TAG = this.getClass().getName();

    @Bind(R.id.rview_grid)
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;

    ArrayList<MoviesResult.Movie> mMovies;
    private Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpToolbarSpinner();

        mMovies = new ArrayList<>();

        setUpRecyclerView();

        getData();
    }

    private void setUpToolbarSpinner() {
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar == null) {
            Log.e(LOG_TAG, "Not configuring action bar");
            return;
        }
        Log.e(LOG_TAG, "Setting up spinner");
        ToolbarSpinnerAdapter spinnerAdapter = new ToolbarSpinnerAdapter();
        spinnerAdapter.addItems(getToolbarItems());

        Spinner spinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
        spinner.setAdapter(spinnerAdapter);
    }

    private Toolbar getActionBarToolbar() {
        //referenced from https://github.com/google/iosched/blob/master/android/src/main/java/com/google/samples/apps/iosched/ui/BaseActivity.java
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }else{
                Log.e(LOG_TAG, "No toolbar");
            }
        }
        if(getSupportActionBar() != null){
           getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        return mActionBarToolbar;
    }

    private List<String> getToolbarItems(){
        List<String> items = new ArrayList<>();
        items.add("Most Popular");
        items.add("Highest Rated");
        return items;
    }

    private void getData() {
        MovieService.Implementation
                .get(getString(R.string.api_key))
                .popular(new Callback<MoviesResult>() {
                    @Override
                    public void success(MoviesResult moviesResult, Response response) {
                        for (MoviesResult.Movie m : moviesResult.getMovies()) {
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

    private void setUpRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, GRIDVIEW_COLUMN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieThumbnailAdapter(mMovies);
        mRecyclerView.setAdapter(mAdapter);
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
                    .centerCrop()
                    // TODO: Find a way to add those back in without ruining how the posters look
//                    .error(R.drawable.cloud_err)
//                    .placeholder(R.drawable.cloud_placeholder)
                    .crossFade()
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    /**
     * Referenced from https://blog.danielbetts.net/2015/01/02/material-design-spinner-toolbar-style-fix/
     */
    class ToolbarSpinnerAdapter extends BaseAdapter {

        List<String> mItems = new ArrayList<>();

        public void addItems(List<String> items){
            mItems.addAll(items);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public String getTitle(int i){
            return i >= 0 && i <= getCount() ? mItems.get(i) : "";
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
                view = getLayoutInflater().inflate(R.layout.toolbar_spinner_item_actionbar, viewGroup, false);
                view.setTag("NON_DROPDOWN");
            }
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getTitle(i));
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null || !convertView.getTag().toString().equals("DROPDOWN")) {
                convertView = getLayoutInflater().inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
                convertView.setTag("DROPDOWN");
            }

            TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
            textView.setText(getTitle(position));

            return convertView;
        }

    }
}
