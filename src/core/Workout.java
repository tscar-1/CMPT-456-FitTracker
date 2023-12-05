package core;

import java.util.*;

public class Workout {

    private String name;
    private List<Exercise> workout;

    public Workout() {
        workout = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }


    public int getLength() {
        return workout.size();
    }

    /*public void addExercise(Exercise exercise, String username) {
        workout.add(exercise);
        database.saveWorkout(username + "_workout_" + name + ".json", this);
    }

    public void removeExercise(Exercise exercise, String username) {
        workout.remove(exercise);
    }*/
}
