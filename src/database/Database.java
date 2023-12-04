package database;

import core.Workout;
import core.Meal;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

  public static Database {

    public static void saveWorkout(String filepath, Workout workout) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      String json = gson.toJson(workout);

      try ( BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        writer.write(json);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public static void saveMeal(String filepath, Meal meal) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      String json = gson.toJson(meal);

      try ( BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        writer.write(json);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public static void saveExercise(String filepath, Exercise exercise) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      String json = gson.toJson(exercise);

      try ( BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        writer.write(json);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public static void saveWorkout(String filepath, Food food) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      String json = gson.toJson(food);

      try ( BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        writer.write(json);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    public Workout loadWorkout(String filepath) {
      Gson gson = new Gson();

      try ( BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        return gson.fromJson(reader, Blockchain.class);
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
  }
