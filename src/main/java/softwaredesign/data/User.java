package softwaredesign.data;

import io.jenetics.jpx.GPX;
import softwaredesign.activities.Activity;

import java.io.File;
import java.io.IOException;

public class User {
    public enum Sex {
        MALE, FEMALE
    }

    public static final Double DEFAULT_WEIGHT = 80.0;
    public static final Integer DEFAULT_AGE = 30;
    public static final Sex DEFAULT_SEX = Sex.MALE;

    private GPX gpx;
    private Activity activity;
    private Double weight;
    private Integer age;
    private Sex sex;

    private static User userInstance = null;

    private User() {
        gpx = null;
        activity = null;
        weight = DEFAULT_WEIGHT;
        age = DEFAULT_AGE;
        sex = DEFAULT_SEX;
    }

    public static User getInstance() {
        if (userInstance == null) {
            userInstance = new User();
        }

        return userInstance;
    }

    public void setGpx(File file) throws IOException {
        gpx = GPX.read(file.getPath());
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setWeight(Double weight) {
        if (weight != null) {
            this.weight = weight;
        }
    }

    public void setAge(Integer age) {
        if (age != null) {
            this.age = age;
        }
    }

    public void setSex(String sex) {
        if (sex != null) {
            if (sex.equals("Male")) {
                this.sex = Sex.MALE;
            } else {
                this.sex = Sex.FEMALE;
            }
        }
    }

    public GPX getGpx() {
        return gpx;
    }

    public Activity getActivity() {
        return activity;
    }

    public Double getWeight() {
        return weight;
    }

    public Integer getAge() {
        return age;
    }

    public Sex getSex() {
        return sex;
    }
}
