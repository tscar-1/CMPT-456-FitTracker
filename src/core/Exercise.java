package core;

import java.io.File;
import javax.imageio.ImageIO;

public class Exercise {
  private String name;
  private String recordType;
  private String muscleType;
  private String muscleGroup;
  private Image image;

  public Exercise(String name, String recordType, String muscleType, String muscleGroup, Image image) {
    this.name = name;
    this.recordType = recordType;
    this.muscleType = muscleType;
    this.muscleGroup = muscleGroup;
    this.image = image;
  }

  public String getName() {
    return name;
  }

  public setName(String name) {
    this.name = name;
  }

  public String getRecordType() {
    return recordType;
  }

  public setRecordType(String recordType) {
    this.recordType = recordType;
  }

  public String getMuscleType() {
    return muscleType;
  }

  public setMuscleType(String muscleType) {
    this.muscleType = muscleType;
  }

  public String getMuscleGroup() {
    return muscleGroup;
  }

  public setMuscleGroup(String muscleGroup) {
    this.muscleGroup = muscleGroup;
  }

  public String getImage() {
    return image;
  }

  public setImage(Image image) {
    this.image = ImageIO.read(new File("images/" + image + ".png"));
  }

  @Override
  public String toString() {
        return "Exercise{" +
                "name='" + name + '\'' +
                ", recordType='" + recordType + '\'' +
                ", muscleType='" + muscleType + '\'' +
                ", muscleGroup='" + muscleGroup + '\'' +
                '}';
    }
}
