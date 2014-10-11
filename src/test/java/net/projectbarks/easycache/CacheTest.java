package net.projectbarks.easycache;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by brandon on 10/4/14.
 */
public class CacheTest {

    @Rule
    public TestRule watcher = new TestWatcher() {
        @Override
        protected void starting(Description description) {
            String nameOfTest = description.getMethodName();
            nameOfTest = nameOfTest.replaceAll("(.)([A-Z])", "$1 $2");
            nameOfTest = Character.toUpperCase(nameOfTest.charAt(0)) + nameOfTest.substring(1);
            System.out.println("Starting test: " + nameOfTest);
        }

        @Override
        protected void finished(Description description) {
            System.out.println("Finished!");
        }
    };

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
        EasyCache.checkLifetime();
        Assert.assertNull(EasyCache.getCacheObject(key_a, Integer.class));
        Assert.assertNull(EasyCache.getCacheObject(key_b, String.class));
        Assert.assertNotNull(EasyCache.getCacheObject(key_c, List.class));
    }

    @Test
    public void testMaxStorage() {
        EasyCache.setMaxSize(DiskUnit.Byte, 1);
        try {
            EasyCache.storeCacheObject("MaxSizeExceeded", 100000);
        } catch (Exception e) {
            System.out.println("Passed with" + e.getClass().toString() + " and message " + e.getMessage());
            EasyCache.setMaxSize(DiskUnit.Megabyte, 100);
            return;
        }
        Assert.fail("Failed to throw an exception for exceeding max storage!");
    }

    @Test
    public void testNullKeys() {
        try{
        EasyCache.storeCacheObject(null, null, null, null, null, null);
        } catch(Exception e) {
            System.out.println("Null keys detected!");
            return;
        }
        Assert.fail("Failed to detect null keys!");
    }

    @Test
    public void testKeyNotFound() {
       if(EasyCache.getCacheObject("Invalid Key", java.lang.Class.class) != null){
           Assert.fail("Invalid Key not recognized!");
       }
       System.out.println("Invalid Key recognized!");
    }

    @Test
    public void testClassCastException() {
        try{
            EasyCache.storeCacheObject("Key", 1000);
            EasyCache.getCacheObject("Key", null);
        } catch(Exception e) {
            System.out.println("Null Class detected!");
            return;
        }
        Assert.fail("Null Class not detected!");
    }
}
