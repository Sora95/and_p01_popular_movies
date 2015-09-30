package net.mmhan.popularmovies.model;

import java.util.List;

/**
 * Created by mmhan on 6/9/15.
 */
public class ReviewsResult {
    int id;
    int page;
    public List<Review> results;

    public class Review{
        private String id;
        private String author;
        private String content;
        private String url;

        public String getId() {
            return id;
        }

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }

        public String getUrl() {
            return url;
        }
    }
}
