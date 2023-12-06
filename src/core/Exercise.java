package core;

import java.io.File;
import javax.imageio.ImageIO;

public class Exercise {

    private String name;
    private int recordType; //1 weight-based, 2 distance-based
    private int muscleType; //1 triceps, 2 chest, 3 shoulders, 4 biceps, 5 core, 6 back, 7 forearms, 8 upper legs, 9 glutes, 10 cardio, 11 lower legs
    private int weight;
    private int sets;
    private int reps;
    private int distance;
    private int duration;
    

    //Constructor
    public Exercise(String name, int recordType, int muscleType) {
        this.name = name;
        this.recordType = recordType;
        this.muscleType = muscleType;
    }
    
    //Weight-based Constructor
    public Exercise(String name, int recordType, int muscleType, int weight, int sets, int reps) {
        this.name = name;
        this.recordType = recordType;
        this.muscleType = muscleType;
        this.weight = weight;
        this.sets = sets;
        this.reps = reps;
    }
    
    //Distance-based Constructor
    public Exercise(String name, int recordType, int muscleType, int distance, int duration) {
        this.name = name;
        this.recordType = recordType;
        this.muscleType = muscleType;
        this.distance = distance;
        this.duration = duration;
    }

    //Setters and getters
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setRecordType(int recordType) {
        this.recordType = recordType;
    }

    public int getRecordType() {
        return recordType;
    }

    public void setMuscleType(int muscleType) {
        this.muscleType = muscleType;
    }
    
    public int getMuscleType() {
        return muscleType;
    }
    
    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public void setSets(int sets) {
        this.sets = sets;
    }
    
    public int getSets() {
        return sets;
    }
    
    public void setReps(int reps) {
        this.reps = reps;
    }
    
    public int getReps() {
        return reps;
    }
    
    public void setDistance(int distance) {
        this.distance = distance;
    }
    
    public int getDistance() {
        return distance;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public int getDuration() {
        return duration;
    }

    /*public String getImage() {
        return image;
    }*/

    /*public setImage(Image image) {
        this.image = ImageIO.read(new File("images/" + image + ".png"));
    }*/

    /*public void addExercise(Exercise exercise, String username) {
        exercise.add(exercise);
        database.saveExercise(username + "_exercise_" + this.name + ".json", this);
    }

    public void removeExercise(Exercise exercise, String username) {
        workout.remove(exercise);
    }

    @Override
    public String toString() {
        return "Exercise{"
                + "name='" + name + '\''
                + ", recordType='" + recordType + '\''
                + ", muscleType='" + muscleType + '\''
                + ", muscleGroup='" + muscleGroup + '\''
                + '}';
    }*/
}
