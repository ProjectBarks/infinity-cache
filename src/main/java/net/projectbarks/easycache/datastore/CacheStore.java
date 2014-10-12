package net.projectbarks.easycache.datastore;

/**
 * Cache store is a abstract class used as template to store
 * cache objects in a serialized from. Any form of storing the data
 * may be used as long as there is a {@link net.projectbarks.easycache.datastore.CacheLoader} class
 * to reverse the process. Please refer to the {@link #store(String, String, Object, int, int)} for
 * more info on using the class.
 *
 * Created by brandon on 10/12/14.
 */
public abstract class CacheStore {
    /**
     * You can dump the cache in EasyCache to a server or a local serialization. The key
     * is the key inputted into the function and the value is serialized version of
     * rawValue. To correctly de-serialize both key and value must be stored. EasyCache
     * will run the store function a-sync from the rest of the functions. Delays between a
     * store is to be expected as serialization happens live.
     *
     * Note: Store runs async and uses a snapshot of cache any objects added or removed while
     * store is running will not be added to the store que.
     *
     * @param key the key of the object that was set in the Easy cache store functions.
     * @param value The serialized value associated with the key
     * @param rawValue the raw/non-serialized value associated with the key
     * @param index the index within the cache
     * @param total the total objects being stored.
     */
    public abstract void store(String key, String value, Object rawValue, int index, int total);
}
