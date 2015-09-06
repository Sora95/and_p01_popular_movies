package net.mmhan.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.mmhan.popularmovies.model.Movie;
import net.mmhan.popularmovies.model.MovieService;
import net.mmhan.popularmovies.model.MoviesResult;
import net.mmhan.popularmovies.model.FavoriteMovie;
import net.mmhan.popularmovies.ui.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {

    private static final int GRIDVIEW_COLUMN_COUNT = 3;
    private static final String MOVIES_KEY = "MOVIES";
    private static final String SORT_ORDER_KEY = "SORT_ORDER";
    private static final String FILTER_KEY = "FILTER";
    private static final String PAGE_KEY = "CURRENT_PAGE";
    private final String LOG_TAG = this.getClass().getName();
    private int mPage = 1;
    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;

    private enum Filter{
        Popularity,
        Rating,
        Favorites
    }


    Filter mFilter = Filter.Popularity;

    private enum SortOrder{
        Descending,
        Ascending
    }

    SortOrder mOrder = SortOrder.Descending;

    public void setFilter(Filter mFilter) {
        this.mFilter = mFilter;
    }

    public void setOrder(SortOrder mOrder) {
        this.mOrder = mOrder;
    }

    public int getSortOrderIcon(){
        //TODO replace two images with one by programmatically reflecting the drawable
        if(mOrder == SortOrder.Ascending){
            return R.drawable.ic_action_sort_r;
        }else{
            return R.drawable.ic_action_sort;
        }
    }

    public void swapOrder(){
        setOrder((mOrder == SortOrder.Descending) ? SortOrder.Ascending : SortOrder.Descending);
        Log.e(LOG_TAG, "SortOrder changed to " + mOrder);
    }

    @Bind(R.id.rview_grid)
    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;
    GridLayoutManager mLayoutManager;
    private Toolbar mActionBarToolbar;
    private boolean mToolbarSetupCompleted = false;
    private boolean mSkipResetAndLoad = false;

    ArrayList<Movie> mMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            loadData(savedInstanceState);
        } else {
            mMovies = new ArrayList<>();
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpToolbarSpinner();

        setUpRecyclerView();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveData(outState);
    }

    private void saveData(Bundle outState) {
        outState.putSerializable(MOVIES_KEY, mMovies);
        outState.putSerializable(SORT_ORDER_KEY, mOrder);
        outState.putSerializable(FILTER_KEY, mFilter);
        outState.putInt(PAGE_KEY, mEndlessRecyclerOnScrollListener.getCurrentPage());
    }

    @SuppressWarnings("unchecked")
    private void loadData(Bundle savedInstanceState) {
        mMovies = (ArrayList<Movie>) savedInstanceState.getSerializable(MOVIES_KEY);
        mFilter = (Filter) savedInstanceState.getSerializable(FILTER_KEY);
        mOrder = (SortOrder) savedInstanceState.getSerializable(SORT_ORDER_KEY);
        mPage = savedInstanceState.getInt(PAGE_KEY);
        mSkipResetAndLoad = true;
    }


    private void setUpToolbarSpinner() {
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar == null) {
//            Log.e(LOG_TAG, "Not configuring action bar");
            return;
        }
        if(!mToolbarSetupCompleted) {
//            Log.e(LOG_TAG, "Setting up spinner");
            final ToolbarSpinnerAdapter spinnerAdapter = new ToolbarSpinnerAdapter();
            spinnerAdapter.addItems(getToolbarItems());

            Spinner spinner = (Spinner) toolbar.findViewById(R.id.spinner_nav);
            spinner.setAdapter(spinnerAdapter);
            int selection = 0;
            switch (mFilter){
                case Popularity:
                    selection = 0;
                    break;
                case Rating:
                    selection = 1;
                    break;
                case Favorites:
                    selection = 2;
            }
            spinner.setSelection(selection);

            spinner.setOnItemSelectedListener(new SpinnerItemSelectedListener(spinnerAdapter));
        }
    }

    private Toolbar getActionBarToolbar() {
        //referenced from https://github.com/google/iosched/blob/master/android/src/main/java/com/google/samples/apps/iosched/ui/BaseActivity.java
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        if(getSupportActionBar() != null){
           getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        return mActionBarToolbar;
    }

    private List<FilterItem> getToolbarItems(){
        List<FilterItem> items = new ArrayList<>();
        items.add(new FilterItem(Filter.Popularity, mOrder));
        items.add(new FilterItem(Filter.Rating, mOrder));
        items.add(new FilterItem(Filter.Favorites, mOrder));
        return items;
    }

    private void resetData(){
        mPage = 1;
        mMovies.clear();
        mAdapter.notifyDataSetChanged();
    }

    private void getData() {
        getData(1);
    }
    private void getData(int page) {
        if(mFilter == Filter.Favorites){
            RealmResults<FavoriteMovie> result = Realm.getInstance(this)
                    .where(FavoriteMovie.class)
                    .findAllSorted("favoritedAt",
                            mOrder == SortOrder.Ascending ?
                                    RealmResults.SORT_ORDER_ASCENDING : RealmResults.SORT_ORDER_DESCENDING
                    );
            for(FavoriteMovie m : result){
                mMovies.add(new Movie(m));
            }
            mAdapter.notifyDataSetChanged();
        }else {
            MovieService service = MovieService.Implementation
                    .get(getString(R.string.api_key));
            Callback<MoviesResult> cb = new Callback<MoviesResult>() {
                @Override
                public void success(MoviesResult moviesResult, Response response) {
                    for (Movie m : moviesResult.getMovies()) {
                        mMovies.add(m);
//                    Log.e(LOG_TAG, "Movie: " + m.title);
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void failure(RetrofitError error) {
//                Log.e(LOG_TAG, error.toString());
                }
            };
            switch (mOrder) {
                case Descending:
                    switch (mFilter) {
                        case Popularity:
                            service.popular(page, cb);
                            break;
                        case Rating:
                            service.highestRated(page, cb);
                            break;
                    }
                    break;
                case Ascending:
                    switch (mFilter) {
                        case Popularity:
                            service.unpopular(page, cb);
                            break;
                        case Rating:
                            service.lowestRated(page, cb);
                            break;
                    }
                    break;
            }
        }
    }


    private void setUpRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, GRIDVIEW_COLUMN_COUNT);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieThumbnailAdapter(mMovies);
        mRecyclerView.setAdapter(mAdapter);
        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(mLayoutManager, mPage) {
            @Override
            public void onLoadMore(int current_page) {
                Log.e(LOG_TAG, "onLoadMore Called");
                MainActivity.this.getData(current_page);
            }
        };
        mRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);
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
        if (id == R.id.action_sort_order) {
            swapOrder();
            item.setIcon(ContextCompat.getDrawable(this, getSortOrderIcon()));
            mToolbarSetupCompleted = false;
            setUpToolbarSpinner();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MovieThumbnailAdapter extends RecyclerView.Adapter<MainActivity.MovieThumbnailAdapter.MovieThumbnailViewHolder>{

        List<Movie> mData;

        class MovieThumbnailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            @Bind(R.id.tv_title)
            public TextView mTextView;
            @Bind(R.id.iv_thumbnail)
            public ImageView mImageView;


            private Movie mMovie;

            public MovieThumbnailViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
                mTextView.getText();
                v.setOnClickListener(this);
            }

            public void setmMovie(Movie mMovie) {
                this.mMovie = mMovie;
                mTextView.setText(mMovie.title);
                //IMAGE
                Glide.with(mImageView.getContext())
                        .load(mMovie.getPosterUrl())
                        .centerCrop()
                                // TODO: Find a way to add those back in without ruining how the posters look
//                    .error(R.drawable.cloud_err)
//                    .placeholder(R.drawable.cloud_placeholder)
                        .crossFade()
                        .into(mImageView);
            }

            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, MovieDetailsActivity.class);
                it.putExtra(MovieDetailsActivity.EXTRA_MOVIE, mMovie);
                startActivity(it);
            }
        }


        public MovieThumbnailAdapter(List<Movie> movies){
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
            holder.setmMovie(mData.get(position));

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    /**
     * Referenced from https://blog.danielbetts.net/2015/01/02/material-design-spinner-toolbar-style-fix/
     */
    private class ToolbarSpinnerAdapter extends BaseAdapter {

        List<FilterItem> mItems = new ArrayList<>();

        public void addItems(List<FilterItem> items){
            mItems.addAll(items);
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public FilterItem getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        public String getTitle(int i){
            return i >= 0 && i <= getCount() ? mItems.get(i).getName() : "";
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

    private class FilterItem {

        private Filter filter;
        private SortOrder order;

        public FilterItem(Filter filter, SortOrder order){
            this.filter = filter;
            this.order = order;
        }

        public String getName() {
            int string_id = R.string.filter_highest_rated;
            if(Filter.Rating == filter)
                string_id = SortOrder.Descending == order ? R.string.filter_highest_rated : R.string.filter_lowest_rated;
            else if(Filter.Popularity == filter)
                string_id = SortOrder.Descending == order ? R.string.filter_popular : R.string.filter_unpopular;
            else if(Filter.Favorites == filter)
                string_id = R.string.filter_favorites;

            return getString(string_id);
        }

        public Filter getFilter() {
            return filter;
        }
    }

    private class SpinnerItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private final ToolbarSpinnerAdapter spinnerAdapter;

        public SpinnerItemSelectedListener(ToolbarSpinnerAdapter spinnerAdapter) {
            this.spinnerAdapter = spinnerAdapter;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.e(LOG_TAG, "Selected item " + i);
            if(!mSkipResetAndLoad) {
                setFilter(spinnerAdapter.getItem(i).getFilter());
                resetData();
                getData();
            }else{
                mSkipResetAndLoad = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {}
    }
}
