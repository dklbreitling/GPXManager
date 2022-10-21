package softwaredesign.metrics;

import io.jenetics.jpx.WayPoint;

import java.util.List;

public class Elevation {
    public static boolean allWayPointsLogElevation(List<WayPoint> wayPoints) {
        return wayPoints
            .stream()
            .allMatch(wayPoint -> wayPoint.getElevation().isPresent());
    }

    public static double elevationBetweenTwoWayPoints(WayPoint start, WayPoint finish) {
        double startElevation = start.getElevation().get().doubleValue();
        double finishElevation = finish.getElevation().get().doubleValue();

        return finishElevation - startElevation;
    }
}
