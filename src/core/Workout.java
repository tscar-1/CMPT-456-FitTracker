package core;

import java.util.*;

public class Workout {
    private String name;
    private List<Exercise> exercises;

    public Workout(String name) {
        this.name = name;
        this.exercises = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
    }
    
    public List<Exercise> getExercises() {
        return exercises;
    }
}
