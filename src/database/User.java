package database;

import core.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import utils.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.*;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.annotations.Expose;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class User {
    @Expose
    private String username;
    @Expose
    private String email;
    @Expose
    private String fName;
    @Expose
    private String lName;
    @Expose
    private String password;
    
    public User() {
        username = "";
        email = "";
        fName = "";
        lName = "";
        password = "";
    }
    
    public User(String username, String email, String fName, String lName, String password) {
        this.username = username;
        this.email = email;
        this.fName = fName;
        this.lName = lName;
        this.password = StringUtil.applySha256(password);
        
        File usersFile = new File("users.json");
        if (!usersFile.exists()) {
            try ( FileWriter writer = new FileWriter(usersFile)) {
                writer.write("[]");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File exercisesFile = new File(username + "_exercises.json");
        if (!exercisesFile.exists()) {
            try ( FileWriter writer = new FileWriter(exercisesFile)) {
                writer.write("[]");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File workoutsFile = new File(username + "_workouts.json");
        if (!workoutsFile.exists()) {
            try ( FileWriter writer = new FileWriter(workoutsFile)) {
                writer.write("[]");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File foodsFile = new File(username + "_foods.json");
        if (!foodsFile.exists()) {
            try ( FileWriter writer = new FileWriter(foodsFile)) {
                writer.write("[]");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File mealsFile = new File(username + "_meals.json");
        if (!mealsFile.exists()) {
            try ( FileWriter writer = new FileWriter(mealsFile)) {
                writer.write("[]");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File progressFile = new File(username + "_progress.json");
        if (!mealsFile.exists()) {
            try ( FileWriter writer = new FileWriter(progressFile)) {
                writer.write("[]");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).setPrettyPrinting().create();
        List<User> userList;
        try ( FileReader reader = new FileReader(usersFile)) {
            userList = gson.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            if (userList == null) {
                userList = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            userList = new ArrayList<>();
        }
        
        User currentUser = this;
        userList.add(currentUser);
        
        try ( BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile))) {
            gson.toJson(userList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void updateEmail(String username, String newEmail) {
        File usersFile = new File("users.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<User> users;

        try (FileReader reader = new FileReader(usersFile)) {
            Type userListType = new TypeToken<List<User>>() {
            }.getType();
            users = gson.fromJson(reader, userListType);
            if (users == null) {
                users = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setEmail(newEmail);
                break;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile))) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setFName(String fName) {
        this.fName = fName;
    }
    
    public String getFName() {
        return fName;
    }
    
    public void updateFName(String username, String newFName) {
        File usersFile = new File("users.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<User> users;

        try (FileReader reader = new FileReader(usersFile)) {
            Type userListType = new TypeToken<List<User>>() {
            }.getType();
            users = gson.fromJson(reader, userListType);
            if (users == null) {
                users = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setFName(newFName);
                break;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile))) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setLName(String lName) {
        this.lName = lName;
    }
    
    public String getLName() {
        return lName;
    }
    
    public void updateLName(String username, String newLName) {
        File usersFile = new File("users.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<User> users;

        try (FileReader reader = new FileReader(usersFile)) {
            Type userListType = new TypeToken<List<User>>() {
            }.getType();
            users = gson.fromJson(reader, userListType);
            if (users == null) {
                users = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setLName(newLName);
                break;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile))) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setPassword(String password) {
        this.password = StringUtil.applySha256(password);
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public void updatePassword(String username, String newPassword) {
        File usersFile = new File("users.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<User> users;

        try (FileReader reader = new FileReader(usersFile)) {
            Type userListType = new TypeToken<List<User>>() {
            }.getType();
            users = gson.fromJson(reader, userListType);
            if (users == null) {
                users = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setPassword(newPassword);
                break;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile))) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private List<ProgressEntry> readProgress() {
        File progressFile = new File(username + "_progress.json");
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        if (!progressFile.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(progressFile)) {
            Type progressListType = new TypeToken<List<ProgressEntry>>(){}.getType();
            return gson.fromJson(reader, progressListType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private void writeProgress(List<ProgressEntry> progressEntries) {
        File progressFile = new File(username + "_progress.json");
        Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(progressFile)) {
            gson.toJson(progressEntries, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void saveProgressExerciseWeight(LocalDate date, String exerciseName, int exerciseRecordType, int exerciseMuscleType, int exerciseSetNum, int exerciseWeightAmt, int exerciseRepsAmt) {
        Exercise exercise = new Exercise(exerciseName, exerciseRecordType, exerciseMuscleType, exerciseSetNum, exerciseWeightAmt, exerciseRepsAmt);
        List<ProgressEntry> progressEntries = readProgress();

        ProgressEntry entry = progressEntries.stream()
                .filter(e -> e.date.equals(date))
                .findFirst()
                .orElseGet(() -> {
                    ProgressEntry newEntry = new ProgressEntry(date);
                    progressEntries.add(newEntry);
                    return newEntry;
                });

        entry.addExercise(exercise);
        writeProgress(progressEntries);
    }
    
    public void saveProgressExerciseDistance(LocalDate date, String exerciseName, int exerciseRecordType, int exerciseMuscleType, int exerciseSetNum, double exerciseDistanceAmt, double exerciseDurationLen) {
        Exercise exercise = new Exercise(exerciseName, exerciseRecordType, exerciseMuscleType, exerciseSetNum, exerciseDistanceAmt, exerciseDurationLen);
        List<ProgressEntry> progressEntries = readProgress();

        ProgressEntry entry = progressEntries.stream()
                .filter(e -> e.date.equals(date))
                .findFirst()
                .orElseGet(() -> {
                    ProgressEntry newEntry = new ProgressEntry(date);
                    progressEntries.add(newEntry);
                    return newEntry;
                });

        entry.addExercise(exercise);
        writeProgress(progressEntries);
    }
    
    public void saveProgressFood(LocalDate date, String foodName, int foodCalorieAmt, double foodProteinAmt, double foodCarbsAmt, double foodFatsAmt) {
        Food food = new Food(foodName, foodCalorieAmt, foodProteinAmt, foodCarbsAmt, foodFatsAmt);
        List<ProgressEntry> progressEntries = readProgress();

        ProgressEntry entry = progressEntries.stream()
                .filter(e -> e.date.equals(date))
                .findFirst()
                .orElseGet(() -> {
                    ProgressEntry newEntry = new ProgressEntry(date);
                    progressEntries.add(newEntry);
                    return newEntry;
                });

        entry.addFood(food);
        writeProgress(progressEntries);
    }
    
    private class ProgressEntry {
        private LocalDate date;
        private List<Exercise> exercises;
        private List<Food> foods;
        
        public ProgressEntry(LocalDate date) {
            this.date = date;
            this.exercises = new ArrayList<>();
        }
        
        public void addExercise(Exercise exercise) {
            exercises.add(exercise);
        }
        
        public void addFood(Food food) {
            foods.add(food);
        }
        
        public void setDate(LocalDate date) {
            this.date = date;
        }
        
        public LocalDate getDate() {
            return date;
        }
    }

    public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString()); // Convert LocalDate to JSON
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString()); // Convert JSON to LocalDate
        }
    }
}
