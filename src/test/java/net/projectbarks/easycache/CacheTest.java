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

        EasyCache.storeCacheObject(key_a, value_a, TimeUnit.MILLISECONDS, 5000L);
        EasyCache.storeCacheObject(key_b, value_b, TimeUnit.SECONDS, 5L);
        EasyCache.storeCacheObject(key_c, value_c);

        EasyCache.checkLifetime();
        Assert.assertNotNull(EasyCache.getCacheObject(key_a, Integer.class));
        Assert.assertNotNull(EasyCache.getCacheObject(key_b, String.class));
        Assert.assertNotNull(EasyCache.getCacheObject(key_c, List.class));

        try {
            Thread.sleep(5001L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis();
        EasyCache.checkLifetime();
        Assert.assertNull(EasyCache.getCacheObject(key_a, Integer.class));
        Assert.assertNull(EasyCache.getCacheObject(key_b, String.class));
        Assert.assertNotNull(EasyCache.getCacheObject(key_c, List.class));
    }

}
