package softwaredesign.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityFactory {
    private static final String PACKAGE_NAME = "softwaredesign.activities";

    private final List<String> activityNames;

    public ActivityFactory() {
        InputStream inputStream = ClassLoader
            .getSystemClassLoader()
            .getResourceAsStream(PACKAGE_NAME.replaceAll("[.]", "/"));

        assert inputStream != null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

         activityNames = bufferedReader
            .lines()
            .filter(line -> line.endsWith(".class"))
            .map(className -> className.substring(0, className.lastIndexOf('.')))
            .filter(className -> !(className.equals("Activity") || className.equals("ActivityFactory")))
            .collect(Collectors.toList());
    }

    public Activity getActivity(String activityName) {
        try {
            return (Activity) Class
                .forName(PACKAGE_NAME + "." + activityName)
                .getDeclaredConstructor()
                .newInstance();
        } catch (ClassNotFoundException
            | NoSuchMethodException
            | InvocationTargetException
            | InstantiationException
            | IllegalAccessException e) {
            return null;
        }
    }

    public List<String> getActivityNames() {
        return activityNames;
    }
}
