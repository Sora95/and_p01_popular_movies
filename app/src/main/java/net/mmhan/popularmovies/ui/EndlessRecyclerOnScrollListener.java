package net.mmhan.popularmovies.ui;

/**
 * Created by mmhan on 18/8/15.
 * referenced from https://gist.github.com/ssinss/e06f12ef66c51252563e
 * TODO: Threshold calculation seems off. It's loading even before it's needed.
 */
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 3; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private GridLayoutManager mLayoutManager;

    public EndlessRecyclerOnScrollListener(GridLayoutManager linearLayoutManager, int page) {
        this.mLayoutManager = linearLayoutManager;
        this.current_page = page;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLayoutManager.getItemCount();
        firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                Log.e(TAG, "Loaded");
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading &&
                (totalItemCount - visibleItemCount) // 20 - 9  = 11
                <= (firstVisibleItem + visibleThreshold) // 6 + 5 = 11
                ) {
            // End has been reached

            // Do something
            current_page++;
            Log.e(TAG, "Loading more. Page : " + current_page);
            onLoadMore(current_page);

            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);

    public int getCurrentPage(){ return current_page; }
}
