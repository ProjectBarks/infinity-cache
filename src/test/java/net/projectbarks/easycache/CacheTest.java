package net.projectbarks.easycache;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by brandon on 10/4/14.
 */
public class CacheTest {

    @Test
    public void testLifetime() {
        String key_a = "Alpha", key_b = "Beta", key_c = "Charlie";
        Object value_a = 1, value_b = "String", value_c = Arrays.asList("One", "Two", "Three");

        StaticCache.storeCacheObject(key_a, value_a, TimeUnit.MILLISECONDS, 5000L);
        StaticCache.storeCacheObject(key_b, value_b, TimeUnit.SECONDS, 5L);
        StaticCache.storeCacheObject(key_c, value_c);

        StaticCache.checkLifetime();
        Assert.assertNotNull(StaticCache.getCacheObject(key_a, Integer.class));
        Assert.assertNotNull(StaticCache.getCacheObject(key_b, String.class));
        Assert.assertNotNull(StaticCache.getCacheObject(key_c, List.class));

        try {
            Thread.sleep(5001L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        StaticCache.checkLifetime();
        Assert.assertNull(StaticCache.getCacheObject(key_a, Integer.class));
        Assert.assertNull(StaticCache.getCacheObject(key_b, String.class));
        Assert.assertNotNull(StaticCache.getCacheObject(key_c, List.class));
    }

}
