package net.projectbarks.easycache;

import lombok.Getter;
import lombok.Setter;

/**
 * Cached object is utilized in the {@link net.projectbarks.easycache.StaticCache}
 * to hold and serialize java objects. Gson is used to serialize the java object
 * to be de-serialize later.
 *
 * Note you should not need to use this class as it is really only used as a an
 * alternative to a hashmap and sorting by value. Furthermore, the class has the
 * compare function enabled to sort cached objects by importance.
 *
 * Created by brandon on 10/4/14.
 */
public class CachedObject implements Comparable<CachedObject> {

    /**
     * Get the lifetime of the cached object and how long it will live.
     *
     * @return the time the object will be deleted.
     */
    @Getter private final Long lifeTime;
    /**
     * Get the deletion time for an object if it has not been modified
     * or has not been used under a certain amount of time.
     *
     * @param time sets the time the object will be erased if not updated.
     * @return the time the object will be deleted.
     */
    @Getter @Setter private Long updateTime;
    /**
     * The serialized value stored in the cached object that can be
     * converted back to its original form.
     *
     * @return the raw json form of the cached object.
     */
    @Getter private final Object value;
    /**
     * The serialized form of {@link #getValue()}
     * of the cached object. This can be used to
     * rebuild the cached object later
     */
    @Getter private final String serializedValue;

    /**
     * Cached object stores a wide range of data
     * that is utilized by the {@link net.projectbarks.easycache.StaticCache} class
     * for processes such as memory management, cache
     * deletion and so forth.
     *
     * @param value serialized value of the object
     * @param lifeTime time the object will be deleted
     * @param serialized The serialized form of value
     */
    public CachedObject(Object value, Long lifeTime, String serialized) {
        this.lifeTime = lifeTime;
        this.value = value;
        this.serializedValue = serialized;
    }

    @Override
    public int compareTo(CachedObject o) {
        return lifeTime.compareTo(o.getLifeTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CachedObject that = (CachedObject) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
