package net.mmhan.popularmovies.model;

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
    }
}
