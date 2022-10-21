package softwaredesign.processing;

import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import softwaredesign.data.User;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static softwaredesign.metrics.Calories.caloriesBetweenTwoWayPoints;
import static softwaredesign.metrics.Distance.distanceBetweenTwoWayPoints;
import static softwaredesign.metrics.Elevation.allWayPointsLogElevation;
import static softwaredesign.metrics.Elevation.elevationBetweenTwoWayPoints;
import static softwaredesign.metrics.Time.allWayPointsLogTime;
import static softwaredesign.metrics.Time.timeBetweenTwoWayPoints;

public class Calculator {
    private double totalDistance = 0.0;
    private Double totalElevationUp = null;
    private Double totalElevationDown = null;
    private Long totalSeconds = null;
    private Double totalCaloriesBurned = null;
    private Double velocity = null;

    public Calculator() {
        List<WayPoint> wayPoints = User.getInstance()
            .getGpx()
            .tracks()
            .flatMap(Track::segments)
            .findFirst()
            .map(TrackSegment::points)
            .orElse(Stream.empty())
            .collect(Collectors.toList());

        if (wayPoints.size() > 1) {
            boolean hasCompleteElevationLogs = allWayPointsLogElevation(wayPoints);
            boolean hasCompleteTimeLogs = allWayPointsLogTime(wayPoints);

            for (int i = 1; i < wayPoints.size(); ++i) {
                WayPoint start = wayPoints.get(i - 1);
                WayPoint finish = wayPoints.get(i);

                // Distance metrics are calculated unconditionally
                totalDistance += distanceBetweenTwoWayPoints(start, finish);

                double changeInElevation = 0.0;

                // Elevation metrics are calculated only if all waypoints contain elevation logs
                if (hasCompleteElevationLogs) {
                    if (totalElevationUp == null) {
                        totalElevationUp = 0.0;
                    }
                    if (totalElevationDown == null) {
                        totalElevationDown = 0.0;
                    }
                    changeInElevation = elevationBetweenTwoWayPoints(start, finish);
                    if (changeInElevation < 0) {
                        totalElevationDown -= changeInElevation; // totalElevationDown > 0, watch sign, hence -=
                    } else if (changeInElevation > 0) {
                        totalElevationUp += changeInElevation;
                    }
                } else {
                    System.err.println("Incomplete elevation logs.");
                }

                // Time and Calorie metrics are calculated only if all waypoints contain time logs
                if (hasCompleteTimeLogs) {
                    if (totalSeconds == null && totalCaloriesBurned == null) {
                        totalSeconds = 0L;
                        totalCaloriesBurned = 0.0;
                    }

                    long increaseInSeconds = timeBetweenTwoWayPoints(start, finish);
                    double minutes = increaseInSeconds / 60.0;
                    double increaseInCaloriesBurned = caloriesBetweenTwoWayPoints(minutes, changeInElevation);

                    totalSeconds += increaseInSeconds;
                    totalCaloriesBurned += increaseInCaloriesBurned;
                } else {
                    System.err.println("Incomplete time logs.");
                }
            }

            // Velocity metrics are calculated only if all waypoints contain time logs
            if (hasCompleteTimeLogs && totalSeconds > 0) {
                velocity = totalDistance / totalSeconds;
            }
        }
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public Double getTotalElevationUp() {
        return totalElevationUp;
    }

    public Double getTotalElevationDown() {
        return totalElevationDown;
    }

    public Long getTotalSeconds() {
        return totalSeconds;
    }

    public Double getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public Double getVelocity() {
        return velocity;
    }
}
