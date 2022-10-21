package softwaredesign.data;

import softwaredesign.activities.Activity;
import softwaredesign.processing.Calculator;

import java.text.DecimalFormat;

public class Report {
    private final Activity activity;
    private final double distance;
    private final Double elevationUp;
    private final Double elevationDown;
    private final Long seconds;
    private final Double caloriesBurned;
    private final Double velocity;

    public Report() {
        activity = User.getInstance().getActivity();

        Calculator calculator = new Calculator();

        distance = calculator.getTotalDistance();
        elevationUp = calculator.getTotalElevationUp();
        elevationDown = calculator.getTotalElevationDown();
        seconds = calculator.getTotalSeconds();
        caloriesBurned = calculator.getTotalCaloriesBurned();
        velocity = calculator.getVelocity();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);

        stringBuilder
            .append("Activity: ")
            .append(activity)
            .append("\n");

        stringBuilder
            .append("Distance: ")
            .append(decimalFormat.format(distance))
            .append("\u00a0m\n");

        stringBuilder.append("Elevation: ");
        if (elevationUp == null || elevationDown == null) {
            stringBuilder.append("N/A\n");
        } else {
            stringBuilder
                .append("\n  - Incline: ")
                .append(elevationUp.intValue())
                .append("\u00a0m\n")
                .append("  - Decline: ")
                .append(elevationDown.intValue())
                .append("\u00a0m\n");
        }

        stringBuilder.append("Time elapsed: ");
        if (seconds == null) {
            stringBuilder.append("N/A\n");
            stringBuilder.append("Calories burned: N/A\n");
            stringBuilder.append("Velocity: N/A");
        } else {
            long hoursPart = seconds / 3600;
            long minutesPart = (seconds % 3600) / 60;
            long secondsPart = seconds % 60;

            stringBuilder
                .append(hoursPart)
                .append("h ")
                .append(minutesPart)
                .append("m ")
                .append(secondsPart)
                .append("s\n");

            stringBuilder
                .append("Calories burned: ")
                .append(decimalFormat.format(caloriesBurned))
                .append("\u00a0kcal\n");

            if (velocity == null) {
                stringBuilder.append("Velocity: N/A");
            } else {
                stringBuilder
                    .append("Velocity: ")
                    .append(decimalFormat.format(velocity))
                    .append("\u00a0m/s");
            }
        }

        return stringBuilder.toString();
    }
}
