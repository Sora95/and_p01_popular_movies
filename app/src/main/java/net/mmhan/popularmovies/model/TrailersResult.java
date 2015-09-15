package net.mmhan.popularmovies.model;

import android.net.Uri;

import java.util.List;

/**
 * Created by mmhan on 6/9/15.
 */
public class TrailersResult {

    public List<Trailer> results;

    public class Trailer{
        private String id;
        private String key;
        private String site;
        private int size;
        private String type;
        private String name;

        public String getId() {
            return id;
        }

        public String getKey() {
            return key;
        }

        public String getSite() {
            return site;
        }

        public int getSize() {
            return size;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getYoutubeUrl(){
            return "https://www.youtube.com/watch?v=" + key;
        }

        public Uri getYoutubeUri(){
            return Uri.parse(getYoutubeUrl());
        }
    }
}
