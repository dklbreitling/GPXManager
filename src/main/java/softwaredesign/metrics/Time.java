package softwaredesign.metrics;

import io.jenetics.jpx.WayPoint;

import java.time.Duration;
import java.util.List;

public class Time {
    public static boolean allWayPointsLogTime(List<WayPoint> wayPoints) {
        return wayPoints
            .stream()
            .allMatch(wayPoint -> wayPoint.getTime().isPresent());
    }

    public static long timeBetweenTwoWayPoints(WayPoint start, WayPoint finish) {
        return Duration
            .between(start.getTime().get(), finish.getTime().get())
            .toSeconds();
    }
}
