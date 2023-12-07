package core;

public class Exercise {
    private String name;
    private int recordType; //1 weight-based, 2 distance-based
    private int muscleType; //1 triceps, 2 chest, 3 shoulders, 4 biceps, 5 core, 6 back, 7 forearms, 8 upper legs, 9 glutes, 10 cardio, 11 lower legs
    private int weight;
    private int set;
    private int reps;
    private double distance;
    private double duration;

    //Constructor
    public Exercise(String name, int recordType, int muscleType) {
        this.name = name;
        this.recordType = recordType;
        this.muscleType = muscleType;
    }
    
    //Weight-based Constructor
    public Exercise(String name, int recordType, int muscleType, int set, int weight, int reps) {
        this.name = name;
        this.recordType = recordType;
        this.muscleType = muscleType;
        this.set = set;
        this.weight = weight;
        this.reps = reps;
    }
    
    //Distance-based Constructor
    public Exercise(String name, int recordType, int muscleType, int set, double distance, double duration) {
        this.name = name;
        this.recordType = recordType;
        this.muscleType = muscleType;
        this.set = set;
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
    
    public void setSet(int set) {
        this.set = set;
    }
    
    public int getSet() {
        return set;
    }
    
    public void setReps(int reps) {
        this.reps = reps;
    }
    
    public int getReps() {
        return reps;
    }
    
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public void setDuration(double duration) {
        this.duration = duration;
    }
    
    public double getDuration() {
        return duration;
    }
}
