package core;

import java.io.File;
import javax.imageio.ImageIO;

public class Exercise {

    private String name;
    private String recordType;
    private String muscleType;
    private String muscleGroup;
    //private Image image;

    //Constructor
    public Exercise(String name, String recordType, String muscleType, String muscleGroup/*, Image image*/) {
        this.name = name;
        this.recordType = recordType;
        this.muscleType = muscleType;
        this.muscleGroup = muscleGroup;
        //this.image = image;
    }

    //Setters and getters
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setMuscleType(String muscleType) {
        this.muscleType = muscleType;
    }
    
    public String getMuscleType() {
        return muscleType;
    }
    
    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getMuscleGroup() {
        return muscleGroup;
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
    }*/

    @Override
    public String toString() {
        return "Exercise{"
                + "name='" + name + '\''
                + ", recordType='" + recordType + '\''
                + ", muscleType='" + muscleType + '\''
                + ", muscleGroup='" + muscleGroup + '\''
                + '}';
    }
}
