package softwaredesign.metrics;

import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.geom.Geoid;

import java.util.stream.Stream;

public class Distance {
    public static double distanceBetweenTwoWayPoints(WayPoint start, WayPoint finish) {
        return Stream
            .of(start, finish)
            .collect(Geoid.WGS84.toPathLength())
            .doubleValue();
    }
}
