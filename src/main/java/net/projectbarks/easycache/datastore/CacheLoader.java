package net.projectbarks.easycache.datastore;

import lombok.Getter;
import lombok.Setter;

/**
 * Object will mange the loading of objects into the cache array.
 * The cache loader can retrieve values from any source based on what
 * is developed. Refer to the {@link net.projectbarks.easycache.datastore.CacheLoader.Result} class
 * and the {@link #load(net.projectbarks.easycache.datastore.CacheLoader.Result)} method for more
 * information relevant to this class.
 *
 * Created by brandon on 10/12/14.
 */
public abstract class CacheLoader {

    /**
     * Result class manages the result of one object being loaded
     * using the {@link #load(net.projectbarks.easycache.datastore.CacheLoader.Result)} function
     * Remaining values is defaulted to false, value to null and key to null. When wanting to
     * terminate the load recursive method set remaining values to false (it is false by
     * default)
     */
    public static class Result {
        /**
         * Remaining values dictates if there are remaining objects
         * to be loaded from the database to be inserted into cache.
         *
         * @param remaining true if there are values in the database/storage.
         * @return if there are values remaining
         */
        @Getter @Setter private boolean remainingValues = false;
        /**
         * Value is the string loaded from the database/storage. If
         * there was a key/value that failed to load null is an acceptable
         * value. The loader will continue to load until remainingValues
         * is set to false.
         *
         * @param v the string value loaded from storage/databse
         * @return the value associated to the key
         */
        @Getter @Setter private String value = null;
        /**
         * Key is the string loaded from the database/storage. If
         * there was a key/value that failed to load null is an acceptable
         * value. The loader will continue to load until remainingValues
         * is set to false.
         *
         * @param k the key associated with the value
         * @return the key associated with the value.
         */
        @Getter @Setter private String key = null;
    }

    /**
     * Load passes in a instantiated result class and expects that
     * class to be returned. {@link net.projectbarks.easycache.datastore.CacheLoader.Result} changes
     * what next event happens. For example when setting remaining values to false load will not be run
     * again. However when setting key or value to null that value will just be ignored and will not be
     * added to cache but will continue the loop.
     *
     * @param result the result of the function.
     * @return the result returned from the object passed in.
     */
    public abstract Result load(Result result);
}
