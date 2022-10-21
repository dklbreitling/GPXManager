package softwaredesign.metrics;

import softwaredesign.data.User;

public class Calories {
    public static double caloriesBetweenTwoWayPoints(double minutes, double changeInElevation) {
        // MET calories burned formula
        double calories =
            minutes * (User.getInstance().getActivity().getMet() * 3.5 * User.getInstance().getWeight()) / 200;

        // Elevation factor
        calories = changeInElevation == 0.0
                   ? calories
                   : (changeInElevation > 0 ? calories * 1.12 : calories * 0.96);

        // Sex factor
        calories = User.getInstance().getSex() == User.Sex.MALE
                   ? calories
                   : calories * 0.88;

        // Age factor
        int ageDiff = User.getInstance().getAge() - User.DEFAULT_AGE;
        calories = calories * (1 - (double) ageDiff / 100);

        return calories;
    }
}
