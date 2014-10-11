package net.projectbarks.easycache;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by brandon on 10/4/14.
 */
public class CacheTest {

    @Rule
    public TestRule watcher = new FancyWatcher();

    @Test
    @UnitInfo(description = "Ensures lifetime works correctly!")
    public void testLifetime() {
        String key_a = "Alpha", key_b = "Beta", key_c = "Charlie";
        Object value_a = 1, value_b = "String", value_c = Arrays.asList("One", "Two", "Three");

        EasyCache.storeCacheObject(key_a, value_a, TimeUnit.MILLISECONDS, 5000L);
        EasyCache.storeCacheObject(key_b, value_b, TimeUnit.SECONDS, 5L);
        EasyCache.storeCacheObject(key_c, value_c);

        EasyCache.checkLifetime();
        Assert.assertNotNull(EasyCache.getCachedObject(key_a, Integer.class));
        Assert.assertNotNull(EasyCache.getCachedObject(key_b, String.class));
        Assert.assertNotNull(EasyCache.getCachedObject(key_c, List.class));

        try {
            Thread.sleep(5001L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EasyCache.checkLifetime();
        Assert.assertNull(EasyCache.getCachedObject(key_a, Integer.class));
        Assert.assertNull(EasyCache.getCachedObject(key_b, String.class));
        Assert.assertNotNull(EasyCache.getCachedObject(key_c, List.class));
    }

    @Test
    @UnitInfo(description = "Ensures lifetime works correctly")
    public void testMaxStorage() {
        EasyCache.setMaxSize(DiskUnit.Byte, 1);
        try {
            EasyCache.storeCacheObject("MaxSizeExceeded", 100000);
        } catch (Exception e) {
            EasyCache.setMaxSize(DiskUnit.Megabyte, 100);
            return;
        }
        Assert.fail("Failed to throw an exception for exceeding max storage!");
    }

    @Test
    @UnitInfo(description = "Inserts null into and random values into primitives every function")
    public void testNullKeys() {
            for (Method method : EasyCache.class.getMethods()) {
                if (method.getDeclaringClass() != EasyCache.class) continue;
                Object[] params = new Object[method.getParameterTypes().length];
                int index = 0;
                for (Class<?> param : method.getParameterTypes()) {
                    params[index] = Defaults.getForClass(param);
                    index++;
                }
                try {
                    method.invoke(null, params);
                } catch (Exception e) {
                    if (!e.getCause().getMessage().equals("Null input!")) {
                        e.printStackTrace();
                        Assert.fail("Bad exception!");
                    }
                }
            }
    }

    @Test
    @UnitInfo(description = "Insert a non existent key")
    public void testKeyNotFound() {
       if(EasyCache.getCachedObject("Invalid Key", java.lang.Class.class) != null){
           Assert.fail("Invalid Key not recognized!");
       }
    }

    @Test
    @UnitInfo(description = "Test a problematic cast")
    public void testClassCastException() {
        try{
            EasyCache.storeCacheObject("Key", 1000);
            EasyCache.getCachedObject("Key", List.class);
        } catch(ClassCastException e) {
            return;
        }
        Assert.fail("Null Class not detected!");
    }
}
