package net.projectbarks.easycache;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandon on 10/11/14.
 */
public class FancyWatcher extends TestWatcher {
    private final static String ARG = "%s";
    private final static String STARTING = "Starting test: " + ARG;
    private final static String INDENT = "    - ";
    private final static String DESCRIPTION = "Description ~ " + ARG;
    private final static String PASSED = "Passed!";
    private final static String FAILED = "Failed!";
    private final static String SPLIT = "406c743b5a";

    private Map<String, Long> times;
    private Map<String, String> logs;

    public FancyWatcher() {
        times = new HashMap<String, Long>();
        logs = new HashMap<String, String>();
    }

    @Override
    protected void starting(Description description) {
        log(description, STARTING, getName(description));
        times.put(description.getDisplayName(), System.nanoTime());

        if (getUnitInfo(description) != null) {
            log(description, INDENT + DESCRIPTION, getUnitInfo(description).description());
        }
    }

    @Override
    protected void failed(Throwable e, Description description) {
        log(description, INDENT + ARG, FAILED);
    }

    @Override
    protected void succeeded(Description description) {
        if (times.containsKey(description.getDisplayName())) {
            log(description, INDENT + "Took %sns", System.nanoTime() - times.get(description.getDisplayName()));
        }
        log(description, INDENT + ARG, PASSED);
    }

    @Override
    protected void finished(Description description) {
        if (!logs.containsKey(description.getDisplayName())) {
            return;
        }
        for (String val : logs.get(description.getDisplayName()).split(SPLIT)) {
            System.out.println(val);
        }
        logs.remove(description.getDisplayName());
    }

    private String getName(Description description) {
        String nameOfTest = description.getMethodName();
        nameOfTest = nameOfTest.replaceAll("(.)([A-Z])", "$1 $2");
        return Character.toUpperCase(nameOfTest.charAt(0)) + nameOfTest.substring(1);
    }

    private UnitInfo getUnitInfo(Description description) {
        return description.getAnnotation(UnitInfo.class);
    }

    private void log(Description desc, String s, Object... args) {
        if (!logs.containsKey(desc.getDisplayName())) {
            logs.put(desc.getDisplayName(), "");
        }
        logs.put(desc.getDisplayName(), logs.get(desc.getDisplayName()) + SPLIT + String.format(s, args));
    }
}
