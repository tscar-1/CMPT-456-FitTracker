package core;

import java.time.LocalDate;

public class Exercise {

    private LocalDate date;
    private String name;
    private int recordType; //1 weight-based, 2 distance-based
    private int muscleType; //1 triceps, 2 chest, 3 shoulders, 4 biceps, 5 core, 6 back, 7 forearms, 8 upper legs, 9 glutes, 10 cardio, 11 lower legs
    private int weight;
    private int sets;
    private int reps;
    private int distance;
    private int duration;

    //Constructor
    public Exercise(LocalDate date, String name, int recordType, int muscleType) {
        this.date = date;
        this.name = name;
        this.recordType = recordType;
        this.muscleType = muscleType;
    }
    
    //Weight-based Constructor
    public Exercise(LocalDate date, String name, int recordType, int muscleType, int weight, int sets, int reps) {
        this.date = date;
        this.name = name;
        this.recordType = recordType;
        this.muscleType = muscleType;
        this.weight = weight;
        this.sets = sets;
        this.reps = reps;
    }
    
    //Distance-based Constructor
    public Exercise(LocalDate date, String name, int recordType, int muscleType, int distance, int duration) {
        this.date = date;
        this.name = name;
        this.recordType = recordType;
        this.muscleType = muscleType;
        this.distance = distance;
        this.duration = duration;
    }

    //Setters and getters
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
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
}
