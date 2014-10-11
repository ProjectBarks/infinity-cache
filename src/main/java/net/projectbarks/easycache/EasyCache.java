package net.projectbarks.easycache;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by brandon on 10/4/14.
 */
public class EasyCache {

    private static List<String> cacheKeys;
    private static List<CachedObject> cacheValues;
    private static long usedSpace;

    /**
     * Max size is responsible in limiting the amount of ram/data your objects
     * utilize. Max size is stored in bytes and can be converted using
     * {@link net.projectbarks.easycache.DiskUnit disk unit} to units.
     *
     * @return max size is the long presentation of max size.
     */
    @Getter private static long maxSize;
    /**
     * The default amount of time an object has to be used or
     * set before it is removed from cache. Default is substituted
     * in when not inputted in the storeCacheObject function. You can use the
     * {@link #setDefaultUpdateTime(java.util.concurrent.TimeUnit, long)} to change
     * the default update time.
     *
     * Note: This is possible to disable
     *
     * @return time in milliseconds
     */
    @Getter private static long defaultUpdateTime;
    /**
     * This is the amount of time an object has to be used before it is deleted/removed
     * from the cache. Default lifetime is only used when not inputted into the
     * storeCacheObject function. You can use the {@link #setDefaultLifetime(java.util.concurrent.TimeUnit, long)}
     * function to set the lifetime.
     *
     * @return time in milliseconds
     */
    @Getter private static long defaultLifetime;
    /**
     * todo fix later
     * Update time is optional and is not required to be used. Update Time can
     * not be null but will be ignored in the code and can be re-enabled at any
     * time.
     *
     * Note: When re-enabling the Update Time all objects will be effected.
     * For example if you add an object before update time is re-enabled the update
     * time inserted will be used.
     *
     * @param updateTime if update time is to be used.
     * @return if update time is enabled
     */
    //@Getter @Setter private static boolean allowUpdateTime;

    static {
        cacheKeys = new ArrayList<String>();
        cacheValues = new ArrayList<CachedObject>();
        maxSize = DiskUnit.Megabyte.toBytes(100);
        defaultLifetime = TimeUnit.HOURS.toMillis(1);
        defaultUpdateTime = TimeUnit.MINUTES.toMillis(5);
        usedSpace = 0;
        //allowUpdateTime = false; todo fix later
    }

    /**
     * This function adds string-object pairs into a ConcurrentHashMap. The object
     * will undergo a serialization process and inserted into the map. The serialization
     * hard clones an object removing all references to the prior object.
     * {@link #getCachedObject(String, Class) getCachedObject} can be called later to
     * demoralize the object and reuse it.
     *
     * Note it is important to have your object compatible with json or be a native object
     * otherwise the object may fail to serialize and an error will be thrown.
     * This function passes to {@link #storeCacheObject(String, Object, java.util.concurrent.TimeUnit, Long)}
     *
     * @param key the key to be used later
     * @param value the value to be found for later
     */
    public static void storeCacheObject(final String key, final Object value) {
        storeCacheObject(key, value, TimeUnit.MILLISECONDS, defaultLifetime, TimeUnit.MILLISECONDS, defaultUpdateTime);
    }

    /**
     * This function adds string-object pairs into a ConcurrentHashMap. The object
     * will undergo a serialization process and inserted into the map. The serialization
     * hard clones an object removing all references to the prior object.
     * {@link #getCachedObject(String, Class) getCachedObject} can be called later to
     * demoralize the object and reuse it.
     *
     * Note it is important to have your object compatible with json or be a native object
     * otherwise the object may fail to serialize and an error will be thrown.
     * This function passes to {@link #storeCacheObject(String, Object, java.util.concurrent.TimeUnit, Long, java.util.concurrent.TimeUnit, Long)}
     *
     * @param key the key to be used later
     * @param value the value to be found for later
     * @param lifetime set how long a object lives for to be removed from cache.
     * @param lifetimeUnit the unit in which the lifetime is measured
     */
    public static void storeCacheObject(final String key, final Object value, TimeUnit lifetimeUnit, Long lifetime) {
        storeCacheObject(key, value, lifetimeUnit, lifetime, TimeUnit.MILLISECONDS, defaultUpdateTime);
    }


    /**
     * This function adds string-object pairs into a ConcurrentHashMap. The object
     * will undergo a serialization process and inserted into the map. The serialization
     * hard clones an object removing all references to the prior object.
     * {@link #getCachedObject(String, Class) getCachedObject} can be called later to
     * demoralize the object and reuse it.
     *
     * Note it is important to have your object compatible with json or be a native object
     * otherwise the object may fail to serialize and an error will be thrown.
     *
     * @param key the key to be used later
     * @param value the value to be found for later
     * @param lifetime set how long a object lives for to be removed from cache.
     */
    public static void storeCacheObject(final String key, final Object value, TimeUnit lifetimeUnit, Long lifetime, TimeUnit updateUnit, Long updateTime) {
        isNull(key, value, lifetimeUnit, updateUnit, updateTime, updateTime);
        long finalLifeTime = System.currentTimeMillis() + lifetimeUnit.toMillis(lifetime);
        String serialized = new GsonBuilder()
            .serializeNulls()
            .create().toJson(value);
        if (usedSpace + serialized.getBytes().length > maxSize) {
            throw new RuntimeException("Max data has been used!");
        }
        usedSpace += serialized.getBytes().length;
        CachedObject cachedObject = new CachedObject(value, finalLifeTime, updateUnit.toMillis(updateTime));

        int index = Collections.binarySearch(cacheValues, cachedObject);
        if (index < 0) index = ~index;
        cacheValues.add(index, cachedObject);
        cacheKeys.add(index, key);
    }

    /**
     * This function will attempt to find a cached object with the type inserted.
     * An exception will be thrown when a key cannot be found or when a type is
     * invalid.
     *
     * @param key the id you used for store
     * @param type the type you stored the object as
     * @param <T> the return type you used in type
     * @return will return the casted value if it has not failed
     */
    public static <T> T getCachedObject(final String key, Class<T> type) {
        isNull(key);
        if (!cacheKeys.contains(key)) {
            return null;
        }
        T value;
        try {
            CachedObject cachedObject = cacheValues.get(cacheKeys.indexOf(key));
            cachedObject.update();
            value = type.cast(cachedObject.getValue());
        } catch (ClassCastException exception) {
            throw new ClassCastException("Invalid type " + type.getName() + " for value!");
        }
        return value;
    }

    /**
     * This function will clear all previously stored cache. This will free up additional
     * memory depending on how many objects are stored.
     */
    public static void clearCache() {
        cacheKeys.clear();
        cacheValues.clear();
        usedSpace = 0;
    }

    /**
     * This function will delete a single entry from the cache. This also frees up
     * space allowing for more objects to be stored. The delete function will
     * throw an error if the key is return null and false if the key is not found.
     *
     * @param key the key to delete along with its associated value
     * @return true if the object has been successfully found and removed
     */
    public static boolean deleteEntryFromCache(final String key) {
        isNull(key);
        if(cacheKeys.contains(key)) {
            int i = cacheKeys.indexOf(key);
            int size = new GsonBuilder().serializeNulls().create().toJson(cacheKeys.get(i)).getBytes().length;
            usedSpace -= size;
            cacheKeys.remove(i);
            cacheValues.remove(i);
        } else {
            return false;
        }
        return true;
    }

    /**
     * This will limit the amount of space given to your cache
     * MaxSize is not always accurate and may sometimes take more
     * space the actually measured. Do not rely on MaxSize for amount
     * of dedicated ram.
     *
     * @param unit Unit of bytes given
     * @param amount of bytes in the specified unit.
     */
    public static void setMaxSize(DiskUnit unit, int amount) {
        isNull(unit);
        maxSize = unit.toBytes(amount);
    }

    /**
     * Avoid redundant code by utilizing default update time. This will
     * be substituted in storeCacheObject functions when left empty.
     * When retrieving the default it will be in Milliseconds and can be
     * converted back using the {@link java.util.concurrent.TimeUnit} class.
     *
     * @param unit the time unit time is left in
     * @param time the amount of time in the specified unit.
     */
    public static void setDefaultUpdateTime(TimeUnit unit, long time) {
        isNull(unit);
        defaultUpdateTime = unit.toMillis(time);
    }

    /**
     * Avoid redundant code by utilizing default lifetime. This will
     * be substituted in thestoreCacheObject functions when left empty.
     * When retrieving the default it will be in Milliseconds and can be
     * converted back using the {@link java.util.concurrent.TimeUnit} class.
     *
     * @param unit the time unit time is left in
     * @param time the amount of time in the specified unit.
     */
    public static void setDefaultLifetime(TimeUnit unit, long time) {
        isNull(unit);
        defaultLifetime = unit.toMillis(time);
    }

    /**
     * Objects in cache are sorted using the mathematical log function
     * and placing it in the correct index. This allows for a faster
     * lifetime check. The process works as follows: The objects are iterated
     * through in descending order. It then checks if the object is outdated and
     * if so removes it from the cache. When a object past the current time is
     * reached the code is terminated. The code can be terminated because an
     * object is first sorted by whichever time is closer to the deadline, the
     * order helps because any objects past the first object with a future time
     * will also be future. The {@link #deleteEntryFromCache(String)} function
     * is used to remove objects.
     */
    protected static void checkLifetime() {
        List<String> keysToRemove = new ArrayList<String>();
        int index = 0;
        for (CachedObject entry : cacheValues) {
            if (entry.getLifeTime() <= -1) {
                break;
            }
            if (entry.getLower() > System.currentTimeMillis()) {
                break;
            }
            keysToRemove.add(cacheKeys.get(index));
            index++;
        }
        for (String key : keysToRemove) {
            deleteEntryFromCache(key);
        }
    }

    /**
     * isNull gets rid of annoyingly long checks for if
     * objects are null. It just iterates through the list and
     * if a null object is thrown true is returned
     *
     * @param nulls list of objects to be checked.
     * @return true if a null object is found in the list.
     */
    private static void isNull(Object... nulls) {
        boolean failed = false;
        for (Object o : nulls) {
            if (o != null) {
                continue;
            }
            failed = true;
            break;
        }
        if (failed) {
            throw new NullPointerException("Null input!");
        }
    }
}
