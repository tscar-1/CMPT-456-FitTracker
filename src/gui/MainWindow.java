package gui;

import java.beans.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import core.*;
import database.*;
import utils.*;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.uiDesigner.core.*;
import net.miginfocom.swing.*;

public class MainWindow {

    private static User currentUser = new User();
    private int currentMuscleType;
    private Exercise currentExercise;
    private Workout currentWorkout;
    private Food currentFood;
    private Meal currentMeal;
    private int weightSetRowCount = 1;
    private int distanceSetRowCount = 1;
    private int workoutWeightSetRowCount = 1;
    private int workoutDistanceSetRowCount = 1;

    public MainWindow() {
        initComponents();
    }

    public void show() {
        window.pack();
        window.setVisible(true);
    }

    //
    // Main Bottom Bar Buttons
    //
    private void logout(ActionEvent e) {
        currentUser = new User();
        loginPasswordField.setText("");
        loginUsernameField.setText("");
        mainWindowLogoutButton.setVisible(false);

        Component[] components = window.getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainWindowLogoutButton.getParent()) {
                window.getContentPane().remove(comp);
            }
        }

        startPanel.setBounds(new Rectangle(new Point(124, 48), startPanel.getPreferredSize()));
        window.getContentPane().add(startPanel);
        startPanel.setVisible(true);
        window.revalidate();
        window.repaint();
    }

    private void exit(ActionEvent e) {
        System.exit(0);
    }

    //
    // Start Panel Buttons
    //
    private void startLogin(ActionEvent e) {
        window.getContentPane().remove(startPanel);
        loginPanel.setBounds(new Rectangle(new Point(124, 48), loginPanel.getPreferredSize()));
        window.getContentPane().add(loginPanel);
        loginPanel.setVisible(true);
        window.revalidate();
        window.repaint();
    }

    private void startRegister(ActionEvent e) {
        window.getContentPane().remove(startPanel);
        registerPanel.setBounds(new Rectangle(new Point(124, 48), registerPanel.getPreferredSize()));
        window.getContentPane().add(registerPanel);
        registerPanel.setVisible(true);
        window.revalidate();
        window.repaint();
    }

    //
    // Login Panel Buttons
    //
    private void loginBack(ActionEvent e) {
        loginUsernameField.setText("");
        loginPasswordField.setText("");
        window.getContentPane().remove(loginPanel);
        startPanel.setBounds(new Rectangle(new Point(124, 48), startPanel.getPreferredSize()));
        window.getContentPane().add(startPanel);
        startPanel.setVisible(true);
        window.revalidate();
        window.repaint();
    }

    private void loginLogin(ActionEvent e) {
        String username = loginUsernameField.getText();
        String password = StringUtil.applySha256(loginPasswordField.getText());
         
        File usersFile = new File("users.json");
        Gson gson = new Gson();
        java.util.List<User> users;
        try (FileReader reader = new FileReader(usersFile)) {
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            users = gson.fromJson(reader, userListType);
        } catch (IOException ex) {
            users = new ArrayList<>();
        }
        
        User loggedInUser = users.stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
        
        if (loggedInUser == null) {
            loginUsernameField.setText("User does not exist");
            loginPasswordField.setText("");
            loginUsernameField.setForeground(Color.RED);
            return;
        }

        if (!loggedInUser.getPassword().equals(password)) {
            loginPasswordField.setText("Incorrect password");
            loginPasswordField.setForeground(Color.RED);
            return;
        }
        
        if (loggedInUser.getUsername().equals(username) && loggedInUser.getPassword().equals(password)) {
            notificationShow("Login Successful", "Actions.Red");
            currentUser = loggedInUser;
            mainWindowLogoutButton.setVisible(true);
            populateProfilePage();
            window.getContentPane().remove(loginPanel);
            mainMenuPanel.setBounds(new Rectangle(new Point(0, 0), mainMenuPanel.getPreferredSize()));
            window.getContentPane().add(mainMenuPanel);
            mainMenuPanel.setVisible(true);
            window.revalidate();
            window.repaint();
        }
        
    }

    //
    // Register Panel Buttons
    //
    private void registerBack(ActionEvent e) {
        registerUsernameField.setText("");
        registerEmailField.setText("");
        registerFNameField.setText("");
        registerLNameField.setText("");
        registerPasswordField.setText("");
        window.getContentPane().remove(registerPanel);
        startPanel.setBounds(new Rectangle(new Point(124, 48), startPanel.getPreferredSize()));
        window.getContentPane().add(startPanel);
        startPanel.setVisible(true);
        window.revalidate();
        window.repaint();
    }

    private void registerRegister(ActionEvent e) {
        String username = registerUsernameField.getText();
        String email = registerEmailField.getText();
        String fName = registerFNameField.getText();
        String lName = registerLNameField.getText();
        String password = registerPasswordField.getText();

        File usersFile = new File("users.json");
        Gson gson = new Gson();
        java.util.List<User> users;
        try (FileReader reader = new FileReader(usersFile)) {
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            users = gson.fromJson(reader, userListType);
        } catch (IOException ex) {
            users = new ArrayList<>();
        }

        boolean userExists = users.stream().anyMatch(user -> user.getUsername().equals(username));
        boolean usernameValid = usernameValid(username);
        boolean emailExists = users.stream().anyMatch(user -> user.getEmail().equals(email));
        boolean emailValid = emailValid(email);
        boolean passwordValid = passwordValid(password);

        if (userExists) {
            registerUsernameField.setText("Username already exists");
            registerUsernameField.setForeground(Color.RED);
        }

        if (!usernameValid) {
            registerUsernameField.setText("Username invalid");
            registerUsernameField.setForeground(Color.RED);
        }

        if (emailExists) {
            registerEmailField.setText("Email already exists");
            registerEmailField.setForeground(Color.RED);
        }

        if (!emailValid) {
            registerEmailField.setText("Email invalid");
            registerEmailField.setForeground(Color.RED);
        }

        if (!passwordValid) {
            registerPasswordField.setText("Password invalid");
            registerPasswordField.setForeground(Color.RED);
        }

        if (!userExists && usernameValid && !emailExists && emailValid && passwordValid) {
            notificationShow("Registration Successful", "Actions.Red");
            User newUser = new User(username, email, fName, lName, password);
            users.add(newUser);
            registerBack(null);
        }
    }

    //
    // Main Menu Top Bar Buttons
    //
    private void mainMenuLogo(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuStartPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuStartPanel.getPreferredSize()));
        mainMenuStartPanel.setVisible(true);
        mainMenuPanel.add(mainMenuStartPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void mainMenuExercises(ActionEvent e) {
        Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuExercisesPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuExercisesPanel.getPreferredSize()));
        mainMenuPanel.add(mainMenuExercisesPanel);
        mainMenuExercisesPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void mainMenuWorkouts(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuWorkoutsPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuWorkoutsPanel.getPreferredSize()));
        mainMenuPanel.add(mainMenuWorkoutsPanel);
        mainMenuWorkoutsPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void mainMenuFoods(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuFoodsPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuFoodsPanel.getPreferredSize()));
        mainMenuPanel.add(mainMenuFoodsPanel);
        mainMenuFoodsPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void mainMenuMeals(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuMealsPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuMealsPanel.getPreferredSize()));
        mainMenuPanel.add(mainMenuMealsPanel);
        mainMenuMealsPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void mainMenuProgress(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuProgressPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuProgressPanel.getPreferredSize()));
        mainMenuPanel.add(mainMenuProgressPanel);
        mainMenuProgressPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void mainMenuProfile(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuProfilePanel.setBounds(new Rectangle(new Point(6, 106), mainMenuProfilePanel.getPreferredSize()));
        mainMenuPanel.add(mainMenuProfilePanel);
        mainMenuProfilePanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    //
    // Read/Populate/Select Methods
    //
    private java.util.List<Exercise> exercisesRead(String filePath) {
        File ftExercisesFile = new File(filePath);
        Gson gson = new Gson();
        java.util.List<Exercise> exercises = new ArrayList<>();
        try (FileReader reader = new FileReader(ftExercisesFile)) {
            Type exerciseListType = new TypeToken<ArrayList<Exercise>>() {}.getType();
            exercises = gson.fromJson(reader, exerciseListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return exercises;
    }
    
    private java.util.List<Workout> workoutsRead(String filePath) {
        File ftWorkoutsFile = new File(filePath);
        Gson gson = new Gson();
        java.util.List<Workout> workouts = new ArrayList<>();
        try (FileReader reader = new FileReader(ftWorkoutsFile)) {
            Type workoutListType = new TypeToken<ArrayList<Workout>>() {
            }.getType();
            workouts = gson.fromJson(reader, workoutListType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workouts;
    }
    
    private java.util.List<Food> foodsRead(String filePath) {
        File ftFoodFile = new File(filePath);
        Gson gson = new Gson();
        java.util.List<Food> foods = new ArrayList<>();
        try (FileReader reader = new FileReader(ftFoodFile)) {
            Type foodListType = new TypeToken<ArrayList<Food>>() {}.getType();
            foods = gson.fromJson(reader, foodListType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foods;
    }
    
    private java.util.List<Meal> mealsRead(String filePath) {
        File ftWorkoutsFile = new File(filePath);
        Gson gson = new Gson();
        java.util.List<Meal> meals = new ArrayList<>();
        try (FileReader reader = new FileReader(ftWorkoutsFile)) {
            Type mealListType = new TypeToken<ArrayList<Meal>>() {
            }.getType();
            meals = gson.fromJson(reader, mealListType);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return meals;
    }
    
    private void exercisesPopulate(int source, int muscleType) {
        java.util.List<Exercise> exercises;
        
        if(source == 1) {
            exercises = Stream.concat(exercisesRead("fittracker_exercises.json").stream(),exercisesRead(currentUser.getUsername() + "_exercises.json").stream()).collect(Collectors.toList());
        }
        else if (source == 2) {
            exercises = exercisesRead("fittracker_exercises.json");
        }
        else {
            exercises = exercisesRead(currentUser.getUsername() + "_exercises.json");
        }
        
        java.util.List<Exercise> groupedExercises = exercises.stream().filter(e -> e.getMuscleType() == muscleType).collect(Collectors.toList());
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        int totalHeight = 0;
        for (Exercise exercise : groupedExercises) {
            JButton button = new JButton(exercise.getName());
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setForeground(Color.white);
	    button.setFont(button.getFont().deriveFont(button.getFont().getStyle() | Font.BOLD, button.getFont().getSize() + 10f));
            button.addActionListener(e -> exerciseSelection(exercise));
            listPanel.add(button);
            totalHeight += 100;
        }
        
        listPanel.setPreferredSize(new Dimension(exercisesExercisesScrollPanel.getViewport().getWidth(),totalHeight));
        
        exercisesExercisesScrollPanel.setViewportView(listPanel);
        exercisesExercisesScrollPanel.revalidate();
        exercisesExercisesScrollPanel.repaint();
    }
    
    private void workoutsPopulate(int source) {
        java.util.List<Workout> workouts;
        
        if(source == 1) {
            workouts = Stream.concat(workoutsRead("fittracker_workouts.json").stream(),workoutsRead(currentUser.getUsername() + "_workouts.json").stream()).collect(Collectors.toList());
        }
        else if (source == 2) {
            workouts = workoutsRead("fittracker_workouts.json");
        }
        else {
            workouts = workoutsRead(currentUser.getUsername() + "_workouts.json");
        }
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        int totalHeight = 0;
        for (Workout workout : workouts) {
            JButton button = new JButton(workout.getName());
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setForeground(Color.white);
	    button.setFont(button.getFont().deriveFont(button.getFont().getStyle() | Font.BOLD, button.getFont().getSize() + 10f));
            button.addActionListener(e -> workoutSelection(workout));
            listPanel.add(button);
            totalHeight += 100;
        }
        
        listPanel.setPreferredSize(new Dimension(workoutsWorkoutsScrollPanel.getViewport().getWidth(),totalHeight));
        
        workoutsWorkoutsScrollPanel.setViewportView(listPanel);
        workoutsWorkoutsScrollPanel.revalidate();
        workoutsWorkoutsScrollPanel.repaint();
    }
    
    private void workoutsExercisesPopulate() {
        java.util.List<Exercise> exercises = currentWorkout.getExercises();

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        int totalHeight = 0;
        for (Exercise exercise : exercises) {
            JButton button = new JButton(exercise.getName());
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setForeground(Color.white);
            button.setFont(button.getFont().deriveFont(button.getFont().getStyle() | Font.BOLD, button.getFont().getSize() + 10f));
            button.addActionListener(e -> workoutsExerciseSelection(exercise));
            listPanel.add(button);
            totalHeight += 100;
        }

        listPanel.setPreferredSize(new Dimension(workoutsExercisesScrollPanel.getViewport().getWidth(), totalHeight));

        workoutsExercisesScrollPanel.setViewportView(listPanel);
        workoutsExercisesScrollPanel.revalidate();
        workoutsExercisesScrollPanel.repaint();
    }
    
    private void foodsPopulate(int source) {
        java.util.List<Food> foods;

        if(source == 1) {
            foods = Stream.concat(foodsRead("fittracker_foods.json").stream(),foodsRead(currentUser.getUsername() + "_foods.json").stream()).collect(Collectors.toList());
        }
        else if (source == 2) {
            foods = foodsRead("fittracker_foods.json");
        }
        else {
            foods = foodsRead(currentUser.getUsername() + "_foods.json");
        }

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        int totalHeight = 0;
        for (Food food : foods) {
            JButton button = new JButton(food.getName());
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setForeground(Color.white);
            button.setFont(button.getFont().deriveFont(button.getFont().getStyle() | Font.BOLD, button.getFont().getSize() + 10f));
            button.addActionListener(e -> foodSelection(food));
            listPanel.add(button);
            totalHeight += 100;
        }

        listPanel.setPreferredSize(new Dimension(foodsFoodsScrollPanel.getViewport().getWidth(),totalHeight));

        foodsFoodsScrollPanel.setViewportView(listPanel);
        foodsFoodsScrollPanel.revalidate();
        foodsFoodsScrollPanel.repaint();
    }
    
    private void mealsPopulate(int source) {
        java.util.List<Meal> meals;
        
        if(source == 1) {
            meals = Stream.concat(mealsRead("fittracker_meals.json").stream(),mealsRead(currentUser.getUsername() + "_meals.json").stream()).collect(Collectors.toList());
        }
        else if (source == 2) {
            meals = mealsRead("fittracker_meals.json");
        }
        else {
            meals = mealsRead(currentUser.getUsername() + "_meals.json");
        }
        
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        int totalHeight = 0;
        for (Meal meal : meals) {
            JButton button = new JButton(meal.getName());
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setForeground(Color.white);
	    button.setFont(button.getFont().deriveFont(button.getFont().getStyle() | Font.BOLD, button.getFont().getSize() + 10f));
            button.addActionListener(e -> mealSelection(meal));
            listPanel.add(button);
            totalHeight += 100;
        }
        
        listPanel.setPreferredSize(new Dimension(workoutsWorkoutsScrollPanel.getViewport().getWidth(),totalHeight));
        
        mealsMealsScrollPanel.setViewportView(listPanel);
        mealsMealsScrollPanel.revalidate();
        mealsMealsScrollPanel.repaint();
    }
    
    private void mealsFoodsPopulate() {
        java.util.List<Food> foods = currentMeal.getFoods();

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        int totalHeight = 0;
        for (Food food : foods) {
            JButton button = new JButton(food.getName());
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.setForeground(Color.white);
            button.setFont(button.getFont().deriveFont(button.getFont().getStyle() | Font.BOLD, button.getFont().getSize() + 10f));
            button.addActionListener(e -> mealsFoodSelection(food));
            listPanel.add(button);
            totalHeight += 100;
        }

        listPanel.setPreferredSize(new Dimension(mealsFoodsScrollPanel.getViewport().getWidth(), totalHeight));

        mealsFoodsScrollPanel.setViewportView(listPanel);
        mealsFoodsScrollPanel.revalidate();
        mealsFoodsScrollPanel.repaint();
    }
    
    private void exerciseSelection(Exercise exercise) {
        int recordType = exercise.getRecordType();
        Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }
        
        if (recordType == 1) {
            currentExercise = exercise;
            exercisesWeightPanel.setBounds(new Rectangle(new Point(6, 106), exercisesWeightPanel.getPreferredSize()));
            exercisesWeightPanel.setVisible(true);
            exercisesWeightTitleLabel.setText(exercise.getName());
            mainMenuPanel.add(exercisesWeightPanel, BorderLayout.CENTER);
            mainMenuPanel.revalidate();
            mainMenuPanel.repaint();
        }
        else {
            currentExercise = exercise;
            exercisesDistancePanel.setBounds(new Rectangle(new Point(6, 106), exercisesDistancePanel.getPreferredSize()));
            exercisesDistancePanel.setVisible(true);
            exercisesDistanceTitleLabel.setText(exercise.getName());
            mainMenuPanel.add(exercisesDistancePanel, BorderLayout.CENTER);
            mainMenuPanel.revalidate();
            mainMenuPanel.repaint();
        }
    }
    
    private void workoutSelection(Workout workout) {
        Component[] components = mainMenuPanel.getComponents();

        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        currentWorkout = workout;
        workoutsExercisesPanel.setBounds(new Rectangle(new Point(6, 106), workoutsExercisesPanel.getPreferredSize()));
        workoutsExercisesPanel.setVisible(true);
        workoutsWorkoutTitleLabel.setText(workout.getName());
        workoutsExercisesPopulate();
        mainMenuPanel.add(workoutsExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    private void workoutsExerciseSelection(Exercise exercise) {
        int recordType = exercise.getRecordType();
        Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }
        
        if (recordType == 1) {
            currentExercise = exercise;
            workoutsExerciseWeightPanel.setBounds(new Rectangle(new Point(6, 106), workoutsExerciseWeightPanel.getPreferredSize()));
            workoutsExerciseWeightPanel.setVisible(true);
            workoutsExerciseWeightTitleLabel.setText(exercise.getName());
            mainMenuPanel.add(workoutsExerciseWeightPanel, BorderLayout.CENTER);
            mainMenuPanel.revalidate();
            mainMenuPanel.repaint();
        }
        else {
            currentExercise = exercise;
            workoutsExerciseDistancePanel.setBounds(new Rectangle(new Point(6, 106), workoutsExerciseDistancePanel.getPreferredSize()));
            workoutsExerciseDistancePanel.setVisible(true);
            workoutsExerciseDistanceTitleLabel.setText(exercise.getName());
            mainMenuPanel.add(workoutsExerciseDistancePanel, BorderLayout.CENTER);
            mainMenuPanel.revalidate();
            mainMenuPanel.repaint();
        }
    }
    
    private void foodSelection(Food food) {
        Component[] components = mainMenuPanel.getComponents();

        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        currentFood = food;
        foodsFoodsFoodsPanel.setBounds(new Rectangle(new Point(6, 106), foodsFoodsFoodsPanel.getPreferredSize()));
        foodsFoodsFoodsPanel.setVisible(true);
        foodsFoodsFoodsTitleLabel.setText(food.getName());
        foodsFoodsFoodsCaloriesLabel.setText(String.valueOf(food.getCalories()) + " kcal");
        foodsFoodsFoodsProteinLabel.setText(String.valueOf(food.getProteins()) + " g");
        foodsFoodsFoodsCarbsLabel.setText(String.valueOf(food.getCarbohydrates()) + " g");
        foodsFoodsFoodsFatsLabel.setText(String.valueOf(food.getFats()) + " g");
        mainMenuPanel.add(foodsFoodsFoodsPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    private void mealSelection(Meal meal) {
        Component[] components = mainMenuPanel.getComponents();

        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        currentMeal = meal;
        mealsFoodsPanel.setBounds(new Rectangle(new Point(6, 106), mealsFoodsPanel.getPreferredSize()));
        mealsFoodsPanel.setVisible(true);
        mealsMealTitleLabel.setText(meal.getName());
        mealsFoodsPopulate();
        mainMenuPanel.add(mealsFoodsPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    private void mealsFoodSelection(Food food) {
        Component[] components = mainMenuPanel.getComponents();

        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        currentFood = food;
        mealsFoodsFoodsPanel.setBounds(new Rectangle(new Point(6, 106), mealsFoodsFoodsPanel.getPreferredSize()));
        mealsFoodsFoodsPanel.setVisible(true);
        mealsFoodsFoodsTitleLabel.setText(food.getName());
        mainMenuPanel.add(mealsFoodsFoodsPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void populateProfilePage() {
        profileFieldsUsernameLabel.setText(currentUser.getUsername());
        profileFieldsEmailField.setText(currentUser.getEmail());
        profileFieldsFNameField.setText(currentUser.getFName());
        profileFieldsLNameField.setText(currentUser.getLName());
    }
    
    //
    // Exercises Panels Buttons
    //
    private void exercisesTriceps(ActionEvent e) {
        currentMuscleType = 1;
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesChest(ActionEvent e) {
	currentMuscleType = 2;
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesShoulders(ActionEvent e) {
	currentMuscleType = 3;
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }
        
        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesBiceps(ActionEvent e) {
	currentMuscleType = 4;
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesCore(ActionEvent e) {
	currentMuscleType = 5;
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesBack(ActionEvent e) {
	currentMuscleType = 6;
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesForearms(ActionEvent e) {
	currentMuscleType = 7;
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesUpperLegs(ActionEvent e) {
	currentMuscleType = 8;
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesGlutes(ActionEvent e) {
	currentMuscleType = 9;
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesCardio(ActionEvent e) {
	currentMuscleType = 10;
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesLowerLegs(ActionEvent e) {
	currentMuscleType = 11;
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesAddNew(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesCustomPanel.setBounds(new Rectangle(new Point(6, 106), exercisesCustomPanel.getPreferredSize()));
        exercisesCustomPanel.setVisible(true);
        mainMenuPanel.add(exercisesCustomPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    private void exercisesExercisesTopBarAll(ActionEvent e) {
	exercisesPopulate(1, currentMuscleType);
    }
    
    private void exercisesExercisesTopBarFT(ActionEvent e) {
	exercisesPopulate(2, currentMuscleType);
    }

    private void exercisesExercisesTopBarCus(ActionEvent e) {
	exercisesPopulate(3, currentMuscleType);
    }
    
    private void exercisesExercisesTopBarBack(ActionEvent e) {
        Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuExercisesPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuExercisesPanel.getPreferredSize()));
        mainMenuPanel.add(mainMenuExercisesPanel);
        mainMenuExercisesPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesWeightTopBarAdd(ActionEvent e) {
        weightSetRowCount++;
        
        JLabel setLabel = new JLabel();
        setLabel.setName("weightSet" + weightSetRowCount + "Label");
        setLabel.setText("" + weightSetRowCount);
        setLabel.setHorizontalAlignment(SwingConstants.CENTER);
        setLabel.setFont(weightSet1Label.getFont().deriveFont(weightSet1Label.getFont().getSize() + 2f));
        weightSetsPanel.add(setLabel, "cell 0 " + weightSetRowCount);

        JTextField weightField = new JTextField();
        weightField.setName("weightSet" + weightSetRowCount + "WeightField");
        weightField.setHorizontalAlignment(SwingConstants.CENTER);
        weightField.setFont(weightSet1WeightField.getFont().deriveFont(weightSet1WeightField.getFont().getStyle() | Font.BOLD, weightSet1WeightField.getFont().getSize() + 5f));
        weightField.setForeground(Color.white);
        weightSetsPanel.add(weightField, "cell 1 " + weightSetRowCount + ",alignx center,growx 0");

        JLabel xLabel = new JLabel();
        xLabel.setName("weightSet" + weightSetRowCount + "XLabel");
        xLabel.setText("X");
        xLabel.setHorizontalAlignment(SwingConstants.CENTER);
        xLabel.setFont(weightSet1XLabel.getFont().deriveFont(weightSet1XLabel.getFont().getSize() + 2f));
        weightSetsPanel.add(xLabel, "cell 2 " + weightSetRowCount);

        JTextField repsField = new JTextField();
        repsField.setName("weightSet" + weightSetRowCount + "RepsField");
        repsField.setHorizontalAlignment(SwingConstants.CENTER);
        repsField.setFont(weightSet1RepsField.getFont().deriveFont(weightSet1RepsField.getFont().getStyle() | Font.BOLD, weightSet1RepsField.getFont().getSize() + 5f));
        repsField.setForeground(Color.white);
        weightSetsPanel.add(repsField, "cell 3 " + weightSetRowCount + ",alignx center,growx 0");
        
        weightSetsPanel.revalidate();
        weightSetsPanel.repaint();
    }

    private void exercisesWeightTopBarRem(ActionEvent e) {
        if (weightSetRowCount > 1) {
            String[] componentsToRemove = {
                "weightSet" + weightSetRowCount + "Label",
                "weightSet" + weightSetRowCount + "WeightField",
                "weightSet" + weightSetRowCount + "XLabel",
                "weightSet" + weightSetRowCount + "RepsField"
            };

            for (String name : componentsToRemove) {
                for (Component comp : weightSetsPanel.getComponents()) {
                    if (name.equals(comp.getName())) {
                        weightSetsPanel.remove(comp);
                        break;
                    }
                }
            }

            weightSetRowCount--;

            weightSetsPanel.revalidate();
            weightSetsPanel.repaint();
        }
        else {
            notificationShow("Cannot remove last set", "Button.focusedBorderColor");
        }
    }
    
    private void exercisesWeightTopBarRec(ActionEvent e) {
        Exercise exercise = currentExercise;
        LocalDate date = LocalDate.now();

        for (int i = 1; i <= weightSetRowCount; i++) {
            String exerciseName = exercise.getName();
            int exerciseRecordType = exercise.getRecordType();
            int exerciseMuscleType = exercise.getMuscleType();
            int exerciseSetNum = i;
            int exerciseWeightAmt = 0;
            int exerciseRepAmt = 0;

            JTextField weightField = (JTextField) findComponentByName(weightSetsPanel, "weightSet" + i + "WeightField");
            JTextField repsField = (JTextField) findComponentByName(weightSetsPanel, "weightSet" + i + "RepsField");
            
            try {
                exerciseWeightAmt = Integer.parseInt(weightField.getText());
                weightField.setText("");
                exerciseRepAmt = Integer.parseInt(repsField.getText());
                repsField.setText("");
            } catch (NumberFormatException ex) {
                notificationShow("Invalid input in set " + i, "Button.focusedBorderColor");
                return;
            }

            currentUser.saveProgressExerciseWeight(date, exerciseName, exerciseRecordType, exerciseMuscleType, exerciseSetNum, exerciseWeightAmt, exerciseRepAmt);
        }
        
        notificationShow("Exercise Recorded", "Actions.Red");
    }

    private void exercisesWeightTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void exercisesDistanceTopBarAdd(ActionEvent e) {
	distanceSetRowCount++;
        
        JLabel setLabel = new JLabel();
        setLabel.setName("distanceSet" + distanceSetRowCount + "Label");
        setLabel.setText("" + distanceSetRowCount);
        setLabel.setHorizontalAlignment(SwingConstants.CENTER);
        setLabel.setFont(distanceSet1Label.getFont().deriveFont(distanceSet1Label.getFont().getSize() + 2f));
        distanceSetsPanel.add(setLabel, "cell 0 " + distanceSetRowCount);

        JTextField distanceField = new JTextField();
        distanceField.setName("distanceSet" + distanceSetRowCount + "WeightField");
        distanceField.setHorizontalAlignment(SwingConstants.CENTER);
        distanceField.setFont(distanceSet1DistanceField.getFont().deriveFont(distanceSet1DistanceField.getFont().getStyle() | Font.BOLD, distanceSet1DistanceField.getFont().getSize() + 5f));
        distanceField.setForeground(Color.white);
        distanceSetsPanel.add(distanceField, "cell 1 " + distanceSetRowCount + ",alignx center,growx 0");

        JTextField durationField = new JTextField();
        durationField.setName("distanceSet" + distanceSetRowCount + "RepsField");
        durationField.setHorizontalAlignment(SwingConstants.CENTER);
        durationField.setFont(distanceSet1DurationField.getFont().deriveFont(distanceSet1DurationField.getFont().getStyle() | Font.BOLD, distanceSet1DurationField.getFont().getSize() + 5f));
        durationField.setForeground(Color.white);
        distanceSetsPanel.add(durationField, "cell 3 " + distanceSetRowCount + ",alignx center,growx 0");
        
        distanceSetsPanel.revalidate();
        distanceSetsPanel.repaint();
    }

    private void exercisesDistanceTopBarRem(ActionEvent e) {
	if (distanceSetRowCount > 1) {
            String[] componentsToRemove = {
                "distanceSet" + distanceSetRowCount + "Label",
                "distanceSet" + distanceSetRowCount + "DistanceField",
                "distanceSet" + distanceSetRowCount + "DurationField"
            };

            for (String name : componentsToRemove) {
                for (Component comp : distanceSetsPanel.getComponents()) {
                    if (name.equals(comp.getName())) {
                        distanceSetsPanel.remove(comp);
                        break;
                    }
                }
            }

            distanceSetRowCount--;

            distanceSetsPanel.revalidate();
            distanceSetsPanel.repaint();
        }
        else {
            notificationShow("Cannot remove last set", "Button.focusedBorderColor");
        }
    }

    private void exercisesDistanceTopBarRec(ActionEvent e) {
	Exercise exercise = currentExercise;
        LocalDate date = LocalDate.now();

        for (int i = 1; i <= distanceSetRowCount; i++) {
            String exerciseName = exercise.getName();
            int exerciseRecordType = exercise.getRecordType();
            int exerciseMuscleType = exercise.getMuscleType();
            int exerciseSetNum = i;
            double exerciseDistanceAmt = 0.0;
            double exerciseDurationLen = 0.0;

            JTextField distanceField = (JTextField) findComponentByName(distanceSetsPanel, "distanceSet" + i + "DistanceField");
            JTextField durationField = (JTextField) findComponentByName(distanceSetsPanel, "distanceSet" + i + "DurationField");
            
            try {
                exerciseDistanceAmt = Integer.parseInt(distanceField.getText());
                distanceField.setText("");
                exerciseDurationLen = Integer.parseInt(durationField.getText());
                durationField.setText("");
            } catch (NumberFormatException ex) {
                notificationShow("Invalid input in set " + i, "Button.focusedBorderColor");
                return;
            }

            currentUser.saveProgressExerciseDistance(date, exerciseName, exerciseRecordType, exerciseMuscleType, exerciseSetNum, exerciseDistanceAmt, exerciseDurationLen);
        }
        
        notificationShow("Exercise Recorded", "Actions.Red");
    }

    private void exercisesDistanceTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        exercisesPopulate(1, currentMuscleType);
        exercisesExercisesPanel.setBounds(new Rectangle(new Point(6, 106), exercisesExercisesPanel.getPreferredSize()));
        exercisesExercisesPanel.setVisible(true);
        mainMenuPanel.add(exercisesExercisesPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    private void exercisesCustomTopBarAdd(ActionEvent e) {
        String username = currentUser.getUsername();
	String name = "(Custom) " + exerciseCustomNameField.getText();
        int recordType = 1;
        int muscleType = 1;
        
        File exercisesFile = new File(username + "_exercises.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        java.util.List<Exercise> exercises;
        try (FileReader reader = new FileReader(exercisesFile)) {
            Type exerciseListType = new TypeToken<ArrayList<Exercise>>() {}.getType();
            exercises = gson.fromJson(reader, exerciseListType);
        } catch (IOException ex) {
            exercises = new ArrayList<>();
        }
        
        if (exerciseCustomWeightRButton.isSelected()) {
            recordType = 1;
        }
        else if (exerciseCustomDistanceRButton.isSelected()) {
            recordType = 2;
        }
        
        if (exerciseCustomTricepsRButton.isSelected()) {
            muscleType = 1;
        }
        else if (exerciseCustomChestRButton.isSelected()) {
            muscleType = 2;
        }
        else if (exerciseCustomShouldersRButton.isSelected()) {
            muscleType = 3;
        }
        else if (exerciseCustomBicepsRButton.isSelected()) {
            muscleType = 4;
        }
        else if (exerciseCustomCoreRButton.isSelected()) {
            muscleType = 5;
        }
        else if (exerciseCustomBackRButton.isSelected()) {
            muscleType = 6;
        }
        else if (exerciseCustomForearmsRButton.isSelected()) {
            muscleType = 7;
        }
        else if (exerciseCustomUpperLegsRButton.isSelected()) {
            muscleType = 8;
        }
        else if (exerciseCustomGlutesRButton.isSelected()) {
            muscleType = 9;
        }
        else if (exerciseCustomCardioRButton.isSelected()) {
            muscleType = 10;
        }
        else if (exerciseCustomLowerLegsRButton.isSelected()) {
            muscleType = 11;
        }

        Exercise newExercise = new Exercise(name, recordType, muscleType);
        exercises.add(newExercise);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exercisesFile))) {
            gson.toJson(exercises, writer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        notificationShow("Exercise Added", "Actions.Red");
    }

    private void exercisesCustomTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuExercisesPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuExercisesPanel.getPreferredSize()));
        mainMenuPanel.add(mainMenuExercisesPanel);
        mainMenuExercisesPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    //
    // Workouts Panels Buttons
    //
    private void workoutSelect(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        workoutsPopulate(1);
        workoutsWorkoutsPanel.setBounds(new Rectangle(new Point(6, 106), workoutsWorkoutsPanel.getPreferredSize()));
        workoutsWorkoutsPanel.setVisible(true);
        mainMenuPanel.add(workoutsWorkoutsPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void workoutsWorkoutsTopBarAll(ActionEvent e) {
	workoutsPopulate(1);
    }

    private void workoutsWorkoutsTopBarFT(ActionEvent e) {
	workoutsPopulate(2);
    }

    private void workoutsWorkoutsTopBarCus(ActionEvent e) {
	workoutsPopulate(3);
    }

    private void workoutsWorkoutsTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuWorkoutsPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuWorkoutsPanel.getPreferredSize()));
        mainMenuPanel.add(mainMenuWorkoutsPanel);
        mainMenuWorkoutsPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void workoutsExercisesTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        workoutsWorkoutsPanel.setBounds(new Rectangle(new Point(6, 106), workoutsWorkoutsPanel.getPreferredSize()));
        mainMenuPanel.add(workoutsWorkoutsPanel);
        workoutsWorkoutsPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    private void workoutsExerciseWeightTopBarAdd(ActionEvent e) {
	workoutWeightSetRowCount++;
        
        JLabel setLabel = new JLabel();
        setLabel.setName("workoutsExerciseWeightSet" + workoutWeightSetRowCount + "Label");
        setLabel.setText("" + workoutWeightSetRowCount);
        setLabel.setHorizontalAlignment(SwingConstants.CENTER);
        setLabel.setFont(workoutsExerciseWeightSet1Label.getFont().deriveFont(workoutsExerciseWeightSet1Label.getFont().getSize() + 2f));
        workoutsExerciseWeightPanel.add(setLabel, "cell 0 " + workoutWeightSetRowCount);

        JTextField weightField = new JTextField();
        weightField.setName("workoutsExerciseWeightSet" + workoutWeightSetRowCount + "WeightField");
        weightField.setHorizontalAlignment(SwingConstants.CENTER);
        weightField.setFont(workoutsExerciseWeightSet1WeightField.getFont().deriveFont(workoutsExerciseWeightSet1WeightField.getFont().getStyle() | Font.BOLD, workoutsExerciseWeightSet1WeightField.getFont().getSize() + 5f));
        weightField.setForeground(Color.white);
        workoutsExerciseWeightPanel.add(weightField, "cell 1 " + workoutWeightSetRowCount + ",alignx center,growx 0");

        JLabel xLabel = new JLabel();
        xLabel.setName("workoutsExerciseWeightSet" + workoutWeightSetRowCount + "XLabel");
        xLabel.setText("X");
        xLabel.setHorizontalAlignment(SwingConstants.CENTER);
        xLabel.setFont(workoutsExerciseWeightSet1XLabel.getFont().deriveFont(workoutsExerciseWeightSet1XLabel.getFont().getSize() + 2f));
        workoutsExerciseWeightPanel.add(xLabel, "cell 2 " + workoutWeightSetRowCount);

        JTextField repsField = new JTextField();
        repsField.setName("workoutsExerciseWeightSet" + workoutWeightSetRowCount + "RepsField");
        repsField.setHorizontalAlignment(SwingConstants.CENTER);
        repsField.setFont(workoutsExerciseWeightSet1RepsField.getFont().deriveFont(workoutsExerciseWeightSet1RepsField.getFont().getStyle() | Font.BOLD, workoutsExerciseWeightSet1RepsField.getFont().getSize() + 5f));
        repsField.setForeground(Color.white);
        workoutsExerciseWeightPanel.add(repsField, "cell 3 " + workoutWeightSetRowCount + ",alignx center,growx 0");
        
        workoutsExerciseWeightPanel.revalidate();
        workoutsExerciseWeightPanel.repaint();
    }

    private void workoutsExerciseWeightTopBarRem(ActionEvent e) {
	if (workoutWeightSetRowCount > 1) {
            String[] componentsToRemove = {
                "workoutsExerciseWeightSet" + workoutWeightSetRowCount + "Label",
                "workoutsExerciseWeightSet" + workoutWeightSetRowCount + "WeightField",
                "workoutsExerciseWeightSet" + workoutWeightSetRowCount + "XLabel",
                "workoutsExerciseWeightSet" + workoutWeightSetRowCount + "RepsField"
            };

            for (String name : componentsToRemove) {
                for (Component comp : workoutsExerciseWeightSetsPanel.getComponents()) {
                    if (name.equals(comp.getName())) {
                        workoutsExerciseWeightSetsPanel.remove(comp);
                        break;
                    }
                }
            }

            workoutWeightSetRowCount--;

            workoutsExerciseWeightSetsPanel.revalidate();
            workoutsExerciseWeightSetsPanel.repaint();
        }
        else {
            notificationShow("Cannot remove last set", "Button.focusedBorderColor");
        }
    }

    private void workoutsExerciseWeightTopBarRec(ActionEvent e) {
	Exercise exercise = currentExercise;
        LocalDate date = LocalDate.now();

        for (int i = 1; i <= workoutWeightSetRowCount; i++) {
            String exerciseName = exercise.getName();
            int exerciseRecordType = exercise.getRecordType();
            int exerciseMuscleType = exercise.getMuscleType();
            int exerciseSetNum = i;
            int exerciseWeightAmt = 0;
            int exerciseRepAmt = 0;

            JTextField weightField = (JTextField) findComponentByName(workoutsExerciseWeightSetsPanel, "workoutsExerciseWeightSet" + i + "WeightField");
            JTextField repsField = (JTextField) findComponentByName(workoutsExerciseWeightSetsPanel, "workoutsExerciseWeightSet" + i + "RepsField");
            
            try {
                exerciseWeightAmt = Integer.parseInt(weightField.getText());
                weightField.setText("");
                exerciseRepAmt = Integer.parseInt(repsField.getText());
                repsField.setText("");
            } catch (NumberFormatException ex) {
                notificationShow("Invalid input in set " + i, "Button.focusedBorderColor");
                return;
            }

            currentUser.saveProgressExerciseWeight(date, exerciseName, exerciseRecordType, exerciseMuscleType, exerciseSetNum, exerciseWeightAmt, exerciseRepAmt);
        }
        
        notificationShow("Exercise Recorded", "Actions.Red");
    }
    
    private void workoutsExerciseWeightTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        workoutsExercisesPanel.setBounds(new Rectangle(new Point(6, 106), workoutsExercisesPanel.getPreferredSize()));
        mainMenuPanel.add(workoutsExercisesPanel);
        workoutsExercisesPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void workoutsExerciseDistanceTopBarAdd(ActionEvent e) {
	workoutDistanceSetRowCount++;
        
        JLabel setLabel = new JLabel();
        setLabel.setName("workoutsExerciseDistanceSet" + workoutDistanceSetRowCount + "Label");
        setLabel.setText("" + workoutDistanceSetRowCount);
        setLabel.setHorizontalAlignment(SwingConstants.CENTER);
        setLabel.setFont(workoutsExerciseDistanceSet1Label.getFont().deriveFont(workoutsExerciseDistanceSet1Label.getFont().getSize() + 2f));
        workoutsExerciseDistanceSetsPanel.add(setLabel, "cell 0 " + workoutDistanceSetRowCount);

        JTextField distanceField = new JTextField();
        distanceField.setName("workoutsExerciseDistanceSet" + workoutDistanceSetRowCount + "WeightField");
        distanceField.setHorizontalAlignment(SwingConstants.CENTER);
        distanceField.setFont(workoutsExerciseDistanceSet1DistanceField.getFont().deriveFont(workoutsExerciseDistanceSet1DistanceField.getFont().getStyle() | Font.BOLD, workoutsExerciseDistanceSet1DistanceField.getFont().getSize() + 5f));
        distanceField.setForeground(Color.white);
        workoutsExerciseDistanceSetsPanel.add(distanceField, "cell 1 " + workoutDistanceSetRowCount + ",alignx center,growx 0");

        JTextField durationField = new JTextField();
        durationField.setName("workoutsExerciseDistanceSet" + workoutDistanceSetRowCount + "RepsField");
        durationField.setHorizontalAlignment(SwingConstants.CENTER);
        durationField.setFont(workoutsExerciseDistanceSet1DurationField.getFont().deriveFont(workoutsExerciseDistanceSet1DurationField.getFont().getStyle() | Font.BOLD, workoutsExerciseDistanceSet1DurationField.getFont().getSize() + 5f));
        durationField.setForeground(Color.white);
        workoutsExerciseDistanceSetsPanel.add(durationField, "cell 3 " + workoutDistanceSetRowCount + ",alignx center,growx 0");
        
        workoutsExerciseDistanceSetsPanel.revalidate();
        workoutsExerciseDistanceSetsPanel.repaint();
    }

    private void workoutsExerciseDistanceTopBarRem(ActionEvent e) {
	if (workoutDistanceSetRowCount > 1) {
            String[] componentsToRemove = {
                "workoutsExerciseWeightSet" + workoutDistanceSetRowCount + "Label",
                "workoutsExerciseWeightSet" + workoutDistanceSetRowCount + "WeightField",
                "workoutsExerciseWeightSet" + workoutDistanceSetRowCount + "XLabel",
                "workoutsExerciseWeightSet" + workoutDistanceSetRowCount + "RepsField"
            };

            for (String name : componentsToRemove) {
                for (Component comp : workoutsExerciseDistanceSetsPanel.getComponents()) {
                    if (name.equals(comp.getName())) {
                        workoutsExerciseDistanceSetsPanel.remove(comp);
                        break;
                    }
                }
            }

            workoutDistanceSetRowCount--;

            workoutsExerciseDistanceSetsPanel.revalidate();
            workoutsExerciseDistanceSetsPanel.repaint();
        }
        else {
            notificationShow("Cannot remove last set", "Button.focusedBorderColor");
        }
    }

    private void workoutsExerciseDistanceTopBarRec(ActionEvent e) {
	Exercise exercise = currentExercise;
        LocalDate date = LocalDate.now();

        for (int i = 1; i <= workoutDistanceSetRowCount; i++) {
            String exerciseName = exercise.getName();
            int exerciseRecordType = exercise.getRecordType();
            int exerciseMuscleType = exercise.getMuscleType();
            int exerciseSetNum = i;
            double exerciseDistanceAmt = 0.0;
            double exerciseDurationLen = 0.0;

            JTextField distanceField = (JTextField) findComponentByName(workoutsExerciseDistanceSetsPanel, "workoutsExerciseDistanceSet" + i + "DistanceField");
            JTextField durationField = (JTextField) findComponentByName(workoutsExerciseDistanceSetsPanel, "workoutsExerciseDistanceSet" + i + "DurationField");
            
            try {
                exerciseDistanceAmt = Integer.parseInt(distanceField.getText());
                distanceField.setText("");
                exerciseDurationLen = Integer.parseInt(durationField.getText());
                durationField.setText("");
            } catch (NumberFormatException ex) {
                notificationShow("Invalid input in set " + i, "Button.focusedBorderColor");
                return;
            }

            currentUser.saveProgressExerciseDistance(date, exerciseName, exerciseRecordType, exerciseMuscleType, exerciseSetNum, exerciseDistanceAmt, exerciseDurationLen);
        }
        
        notificationShow("Exercise Recorded", "Actions.Red");
    }
    
    private void workoutsExerciseDistanceTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        workoutsExercisesPanel.setBounds(new Rectangle(new Point(6, 106), workoutsExercisesPanel.getPreferredSize()));
        mainMenuPanel.add(workoutsExercisesPanel);
        workoutsExercisesPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    //
    // Foods Panels Buttons
    //
    private void foodSelect(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        foodsPopulate(1);
        foodsFoodsPanel.setBounds(new Rectangle(new Point(6, 106), foodsFoodsPanel.getPreferredSize()));
        foodsFoodsPanel.setVisible(true);
        mainMenuPanel.add(foodsFoodsPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void foodsFoodsTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuFoodsPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuFoodsPanel.getPreferredSize()));
        mainMenuPanel.add(mainMenuFoodsPanel);
        mainMenuFoodsPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    private void foodsFoodsFoodsTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        foodsPopulate(1);
        foodsFoodsPanel.setBounds(new Rectangle(new Point(6, 106), foodsFoodsPanel.getPreferredSize()));
        foodsFoodsPanel.setVisible(true);
        mainMenuPanel.add(foodsFoodsPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    private void foodsFoodsFoodsTopBarRec(ActionEvent e) {
        Food food = currentFood;
        LocalDate date = LocalDate.now();

        String foodName = food.getName();
        int foodCalorieAmt = food.getCalories();
        double foodProteinAmt = food.getProteins();
        double foodCarbsAmt = food.getCarbohydrates();
        double foodFatsAmt = food.getFats();

        currentUser.saveProgressFood(date, foodName, foodCalorieAmt, foodProteinAmt, foodCarbsAmt, foodFatsAmt);

        notificationShow("Food Recorded", "Actions.Red");
    }
    
    //
    // Meals Panels Buttons
    //
    private void mealSelect(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mealsPopulate(1);
        mealsMealsPanel.setBounds(new Rectangle(new Point(6, 106), mealsMealsPanel.getPreferredSize()));
        mealsMealsPanel.setVisible(true);
        mainMenuPanel.add(mealsMealsPanel, BorderLayout.CENTER);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    private void mealsMealsTopBarAll(ActionEvent e) {
	mealsPopulate(1);
    }

    private void mealsMealsTopBarFT(ActionEvent e) {
	mealsPopulate(2);
    }

    private void mealsMealsTopBarCus(ActionEvent e) {
	mealsPopulate(3);
    }
    
    private void mealsMealsTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mainMenuMealsPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuMealsPanel.getPreferredSize()));
        mainMenuPanel.add(mainMenuMealsPanel);
        mainMenuMealsPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void mealsFoodsTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mealsMealsPanel.setBounds(new Rectangle(new Point(6, 106), mealsMealsPanel.getPreferredSize()));
        mainMenuPanel.add(mealsMealsPanel);
        mealsMealsPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }

    private void mealsFoodsFoodsTopBarRec(ActionEvent e) {
	Food food = currentFood;
        LocalDate date = LocalDate.now();

        String foodName = food.getName();
        int foodCalorieAmt = food.getCalories();
        double foodProteinAmt = food.getProteins();
        double foodCarbsAmt = food.getCarbohydrates();
        double foodFatsAmt = food.getFats();

        currentUser.saveProgressFood(date, foodName, foodCalorieAmt, foodProteinAmt, foodCarbsAmt, foodFatsAmt);

        notificationShow("Food Recorded", "Actions.Red");
    }
    
    private void mealsFoodsFoodsTopBarBack(ActionEvent e) {
	Component[] components = mainMenuPanel.getComponents();
        
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != mainMenuButtonPanel) {
                mainMenuPanel.remove(comp);
                break;
            }
        }

        mealsFoodsPanel.setBounds(new Rectangle(new Point(6, 106), mealsFoodsPanel.getPreferredSize()));
        mainMenuPanel.add(mealsFoodsPanel);
        mealsFoodsPanel.setVisible(true);
        mainMenuPanel.revalidate();
        mainMenuPanel.repaint();
    }
    
    //
    // Profile Panel Buttons
    //
    private void profileFieldsEditEmail(ActionEvent e) {
	profileFieldsEmailField.setEditable(true);
        profileFieldsSaveEmailButton.setVisible(true);
    }

    private void profileFieldsEditFName(ActionEvent e) {
	profileFieldsFNameField.setEditable(true);
        profileFieldsSaveFNameButton.setVisible(true);
    }

    private void profileFieldsEditLName(ActionEvent e) {
	profileFieldsLNameField.setEditable(true);
        profileFieldsSaveLNameButton.setVisible(true);
    }

    private void profileFieldsEditPassword(ActionEvent e) {
	profileFieldsPasswordField.setEditable(true);
        profileFieldsSavePasswordButton.setVisible(true);
        profileFieldsPasswordShowTButton.setVisible(true);
        profileFieldPasswordConfirmLabel.setVisible(true);
        profileFieldsPasswordConfirmField.setVisible(true);
        profileFieldsPasswordConfirmField.setEditable(true);
    }
    
    private void profileFieldsPasswordShowTButton(ActionEvent e) {
	if (profileFieldsPasswordShowTButton.isSelected()) {
            profileFieldsPasswordField.setEchoChar((char) 0);
            profileFieldsPasswordConfirmField.setEchoChar((char) 0);
        }
        else {
            profileFieldsPasswordField.setEchoChar('•');
            profileFieldsPasswordConfirmField.setEchoChar('•');
        }
    }
    
    private void profileFieldsSaveEmail(ActionEvent e) {
	String email = profileFieldsEmailField.getText();
        
        File usersFile = new File("users.json");
        Gson gson = new Gson();
        java.util.List<User> users;
        try (FileReader reader = new FileReader(usersFile)) {
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            users = gson.fromJson(reader, userListType);
        } catch (IOException ex) {
            users = new ArrayList<>();
        }
        
        boolean emailValid = emailValid(email);
        boolean emailExists = users.stream().anyMatch(user -> user.getEmail().equals(email));
        
        if (!emailValid) {
            profileFieldsEmailField.setText("Email invalid");
            profileFieldsEmailField.setForeground(Color.RED);
        }
        else if (emailExists) {
            profileFieldsEmailField.setText("Email already exists");
            profileFieldsEmailField.setForeground(Color.RED);
        }
        else {
            notificationShow("Email Changed", "Actions.Red");
            currentUser.setEmail(email);
            currentUser.updateEmail(currentUser.getUsername(), email);
            profileFieldsEmailField.setEditable(false);
            profileFieldsSaveEmailButton.setVisible(false);
        }
    }

    private void profileFieldsSaveFName(ActionEvent e) {
	String fName = profileFieldsFNameField.getText();
        
        File usersFile = new File("users.json");
        Gson gson = new Gson();
        java.util.List<User> users;
        try (FileReader reader = new FileReader(usersFile)) {
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            users = gson.fromJson(reader, userListType);
        } catch (IOException ex) {
            users = new ArrayList<>();
        }
        
        notificationShow("First Name Changed", "Actions.Red");
        currentUser.setFName(fName);
        currentUser.updateFName(currentUser.getUsername(), fName);
        profileFieldsFNameField.setEditable(false);
        profileFieldsSaveFNameButton.setVisible(false);
    }

    private void profileFieldsSaveLName(ActionEvent e) {
	String lName = profileFieldsLNameField.getText();
        
        File usersFile = new File("users.json");
        Gson gson = new Gson();
        java.util.List<User> users;
        try (FileReader reader = new FileReader(usersFile)) {
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            users = gson.fromJson(reader, userListType);
        } catch (IOException ex) {
            users = new ArrayList<>();
        }
        
        notificationShow("Last Name Changed", "Actions.Red");
        currentUser.setLName(lName);
        currentUser.updateLName(currentUser.getUsername(), lName);
        profileFieldsLNameField.setEditable(false);
        profileFieldsSaveLNameButton.setVisible(false);
    }

    private void profileFieldsSavePassword(ActionEvent e) {
	String password = profileFieldsPasswordField.getText();
        String passwordConfirm = profileFieldsPasswordConfirmField.getText();
        
        File usersFile = new File("users.json");
        Gson gson = new Gson();
        java.util.List<User> users;
        try (FileReader reader = new FileReader(usersFile)) {
            Type userListType = new TypeToken<ArrayList<User>>() {}.getType();
            users = gson.fromJson(reader, userListType);
        } catch (IOException ex) {
            users = new ArrayList<>();
        }
        
        boolean passwordMatch = password.equals(passwordConfirm);
        boolean passwordValid = passwordValid(password);
        
        if (!passwordMatch) {
            profileFieldsPasswordConfirmField.setText("Password do not match");
            profileFieldsPasswordConfirmField.setForeground(Color.RED);
        }
        if (!passwordValid) {
            profileFieldsPasswordField.setText("Password Invalid");
            profileFieldsPasswordField.setForeground(Color.RED);
        }
        if (passwordMatch && passwordValid) {
            notificationShow("Password Changed", "Actions.Red");
            currentUser.setPassword(password);
            currentUser.updatePassword(currentUser.getUsername(), password);
            profileFieldsPasswordField.setEditable(false);
            profileFieldsSavePasswordButton.setVisible(false);
            profileFieldsPasswordShowTButton.setVisible(false);
            profileFieldPasswordConfirmLabel.setVisible(false);
            profileFieldsPasswordConfirmField.setVisible(false);
            profileFieldsPasswordConfirmField.setEditable(false);
            profileFieldsPasswordShowTButton.setSelected(false);
        }
    }
    
    //
    // Utility & Validation Methods
    //
    private boolean usernameValid(String username) {
        if (username.length() < 4 || username.length() > 16) {
            return false;
        }

        boolean hasSpecial = true;
        for (char c : username.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                hasSpecial = false;
            }
        }

        return hasSpecial;
    }

    private boolean emailValid(String email) {
        if (email.length() < 7) {
            return false;
        }

        if (!email.contains("@")) {
            return false;
        }

        String[] parts = email.split("@");
        if (parts.length != 2 || parts[0].length() == 0 || parts[1].length() == 0) {
            return false;
        }

        if (parts[1].indexOf('.') == 0 || parts[1].lastIndexOf('.') == parts[1].length() - 1) {
            return false;
        }

        String domain = parts[1].substring(parts[1].lastIndexOf('.') + 1);
        if (domain.length() < 2 || domain.length() > 4) {
            return false;
        }

        return true;
    }

    private boolean passwordValid(String password) {
        if (password.length() < 8 && password.length() > 16) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasUpper && hasLower && hasDigit;
    }
    
    private void registerTextFieldFocusGained(FocusEvent e) {
        JTextField source = (JTextField) e.getSource();
        if (source.getForeground().equals(Color.RED)) {
            source.setText("");
            source.setForeground(Color.WHITE);
        }
    }

    private void notificationShow(String message, String color) {
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(new Color(0x2b3036));
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel notificationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(getBackground());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        notificationPanel.setOpaque(false);
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.PAGE_AXIS));

        int offset = 15;
        notificationPanel.add(Box.createRigidArea(new Dimension(0, offset)));
        notificationPanel.add(messageLabel);

        notificationPanel.setBackground(UIManager.getColor(color));

        int panelWidth = window.getWidth() / 5;
        int panelHeight = 80;
        int xPosStart = (window.getWidth() - panelWidth) / 2;
        notificationPanel.setBounds(xPosStart, window.getHeight(), panelWidth, panelHeight);
        window.getContentPane().add(notificationPanel);
        window.getContentPane().setComponentZOrder(notificationPanel, 0);
        window.getContentPane().repaint();

        javax.swing.Timer slideUpTimer = new javax.swing.Timer(5, new ActionListener() {
            int yPos = window.getHeight();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (yPos > window.getHeight() - panelHeight) {
                    yPos -= 1;
                    notificationPanel.setBounds(xPosStart, yPos, panelWidth, panelHeight);
                } else {
                    ((javax.swing.Timer) e.getSource()).stop();
                    new javax.swing.Timer(2000, ev -> {
                        javax.swing.Timer slideDownTimer = new javax.swing.Timer(5, new ActionListener() {
                            int yPosDown = window.getHeight() - panelHeight;

                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                if (yPosDown < window.getHeight()) {
                                    yPosDown += 1;
                                    notificationPanel.setBounds(xPosStart, yPosDown, panelWidth, panelHeight);
                                } else {
                                    ((javax.swing.Timer) evt.getSource()).stop();
                                    window.getContentPane().remove(notificationPanel);
                                    window.getContentPane().revalidate();
                                    window.getContentPane().repaint();
                                }
                            }
                        });
                        slideDownTimer.start();
                    }).start();
                }
            }
        });
        slideUpTimer.start();
    }
    
    private Component findComponentByName(Container container, String name) {
        for (Component comp : container.getComponents()) {
            if (name.equals(comp.getName())) {
                return comp;
            }
        }
        return null;
    }


    private void initComponents() {
	// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
	// Generated using JFormDesigner Educational license - Thomas Scardino (THOMAS A SCARDINO)
	window = new JFrame();
	mainWindowExitButton = new JButton();
	startPanel = new JPanel();
	startLogoLabel = new JLabel();
	startLoginButton = new JButton();
	startRegisterButton = new JButton();
	mainWindowLogoutButton = new JButton();
	loginPanel = new JPanel();
	loginLogoLabel = new JLabel();
	loginUsernameLabel = new JLabel();
	loginPasswordLabel = new JLabel();
	loginUsernameField = new JTextField();
	loginLoginButton = new JButton();
	loginBackButton = new JButton();
	loginPasswordField = new JPasswordField();
	registerPanel = new JPanel();
	registerLogoLabel = new JLabel();
	registerUsernameLabel = new JLabel();
	registerEmailLabel = new JLabel();
	registerUsernameField = new JTextField();
	registerEmailField = new JTextField();
	registerRegisterButton = new JButton();
	registerBackButton = new JButton();
	registerFNameLabel = new JLabel();
	registerFNameField = new JTextField();
	registerLNameField = new JTextField();
	registerLNameLabel = new JLabel();
	registerPasswordLabel = new JLabel();
	registerPasswordField = new JPasswordField();
	mainMenuPanel = new JPanel();
	mainMenuButtonPanel = new JPanel();
	mainMenuLogoButton = new JButton();
	mainMenuExercisesButton = new JButton();
	mainMenuWorkoutsButton = new JButton();
	mainMenuFoodsButton = new JButton();
	mainMenuMealsButton = new JButton();
	mainMenuProgressButton = new JButton();
	mainMenuProfileButton = new JButton();
	mainMenuStartPanel = new JPanel();
	startWelcomeLabel = new JLabel();
	label14 = new JLabel();
	mainMenuExercisesPanel = new JPanel();
	exercisesTricepsButton = new JButton();
	exercisesChestButton = new JButton();
	exercisesShouldersButton = new JButton();
	exercisesBicepsButton = new JButton();
	exercisesCoreButton = new JButton();
	exercisesBackButton = new JButton();
	exercisesForearmsButton = new JButton();
	exercisesUpperLegsButton = new JButton();
	exercisesGlutesButton = new JButton();
	exercisesCardioButton = new JButton();
	exercisesLowerLegsButton = new JButton();
	exercisesAddNewButton = new JButton();
	mainMenuWorkoutsPanel = new JPanel();
	workoutSelectButton = new JButton();
	workoutAddNewButton = new JButton();
	mainMenuFoodsPanel = new JPanel();
	foodSelectButton = new JButton();
	foodAddNewButton = new JButton();
	mainMenuMealsPanel = new JPanel();
	mealSelectButton = new JButton();
	mealAddNewButton = new JButton();
	mainMenuProgressPanel = new JPanel();
	exercisesExercisesPanel = new JPanel();
	exercisesExercisesTopPanel = new JPanel();
	exercisesExercisesTopBarBackButton = new JButton();
	exercisesExercisesTopBarAllButton = new JButton();
	exercisesExercisesTopBarFTButton = new JButton();
	exercisesExercisesTopBarCusButton = new JButton();
	exercisesExercisesScrollPanel = new JScrollPane();
	exercisesWeightPanel = new JPanel();
	exercisesWeightTopPanel = new JPanel();
	exercisesWeightTopBarBackButton = new JButton();
	exercisesWeightTopBarAddButton = new JButton();
	exercisesWeightTopBarRemButton = new JButton();
	exercisesWeightTopBarRecButton = new JButton();
	exercisesWeightTitleLabel = new JLabel();
	weightSetsPanel = new JPanel();
	label17 = new JLabel();
	label18 = new JLabel();
	label19 = new JLabel();
	weightSet1Label = new JLabel();
	weightSet1WeightField = new JTextField();
	weightSet1XLabel = new JLabel();
	weightSet1RepsField = new JTextField();
	exercisesDistancePanel = new JPanel();
	exercisesDistanceTopPanel = new JPanel();
	exercisesDistanceTopBarBackButton = new JButton();
	exercisesDistanceTopBarAddButton = new JButton();
	exercisesDistanceTopBarRemButton = new JButton();
	exercisesDistanceTopBarRecButton = new JButton();
	exercisesDistanceTitleLabel = new JLabel();
	distanceSetsPanel = new JPanel();
	label26 = new JLabel();
	label27 = new JLabel();
	label28 = new JLabel();
	distanceSet1Label = new JLabel();
	distanceSet1DistanceField = new JTextField();
	distanceSet1DurationField = new JTextField();
	exercisesCustomPanel = new JPanel();
	exercisesExercisesTopPanel2 = new JPanel();
	exercisesCustomTopBarBackButton = new JButton();
	exercisesCustomTopBarAddButton = new JButton();
	customFieldsPanel = new JPanel();
	label1 = new JLabel();
	exerciseCustomNameField = new JTextField();
	label2 = new JLabel();
	exerciseCustomWeightRButton = new JRadioButton();
	exerciseCustomDistanceRButton = new JRadioButton();
	label3 = new JLabel();
	exerciseCustomTricepsRButton = new JRadioButton();
	exerciseCustomChestRButton = new JRadioButton();
	exerciseCustomShouldersRButton = new JRadioButton();
	exerciseCustomBicepsRButton = new JRadioButton();
	exerciseCustomCoreRButton = new JRadioButton();
	exerciseCustomBackRButton = new JRadioButton();
	exerciseCustomForearmsRButton = new JRadioButton();
	exerciseCustomUpperLegsRButton = new JRadioButton();
	exerciseCustomGlutesRButton = new JRadioButton();
	exerciseCustomCardioRButton = new JRadioButton();
	exerciseCustomLowerLegsRButton = new JRadioButton();
	workoutsWorkoutsPanel = new JPanel();
	workoutsWorkoutsTopPanel = new JPanel();
	workoutsWorkoutsTopBarBackButton = new JButton();
	workoutsWorkoutsTopBarAllButton = new JButton();
	workoutsWorkoutsTopBarFTButton = new JButton();
	workoutsWorkoutsTopBarCusButton = new JButton();
	workoutsWorkoutsScrollPanel = new JScrollPane();
	workoutsExercisesPanel = new JPanel();
	workoutsExercisesTopPanel = new JPanel();
	workoutsExercisesTopBarBackButton = new JButton();
	workoutsWorkoutTitleLabel = new JLabel();
	workoutsExercisesScrollPanel = new JScrollPane();
	foodsFoodsPanel = new JPanel();
	foodsFoodsTopPanel = new JPanel();
	foodsFoodsTopBarBackButton = new JButton();
	foodsFoodsScrollPanel = new JScrollPane();
	mealsMealsPanel = new JPanel();
	mealsMealsTopPanel = new JPanel();
	mealsMealsTopBarBackButton = new JButton();
	mealsMealsTopBarAllButton = new JButton();
	mealsMealsTopBarFTButton = new JButton();
	mealsMealsTopBarCusButton = new JButton();
	mealsMealsScrollPanel = new JScrollPane();
	mealsFoodsPanel = new JPanel();
	mealsFoodsTopPanel = new JPanel();
	mealsFoodsTopBarBackButton = new JButton();
	mealsMealTitleLabel = new JLabel();
	mealsFoodsScrollPanel = new JScrollPane();
	workoutsExerciseWeightPanel = new JPanel();
	workoutsExerciseWeightTopPanel = new JPanel();
	workoutsExerciseWeightTopBarBackButton = new JButton();
	workoutsExerciseWeightTopBarAddButton = new JButton();
	workoutsExerciseWeightTopBarRemButton = new JButton();
	workoutsExerciseWeightTopBarRecButton = new JButton();
	workoutsExerciseWeightTitleLabel = new JLabel();
	workoutsExerciseWeightSetsPanel = new JPanel();
	label20 = new JLabel();
	label21 = new JLabel();
	label22 = new JLabel();
	workoutsExerciseWeightSet1Label = new JLabel();
	workoutsExerciseWeightSet1WeightField = new JTextField();
	workoutsExerciseWeightSet1XLabel = new JLabel();
	workoutsExerciseWeightSet1RepsField = new JTextField();
	workoutsExerciseDistancePanel = new JPanel();
	exercisesDistanceTopPanel2 = new JPanel();
	workoutsExerciseDistanceTopBarBackButton = new JButton();
	workoutsExerciseDistanceTopBarAddButton = new JButton();
	workoutsExerciseDistanceTopBarRemButton = new JButton();
	workoutsExerciseDistanceTopBarRecButton = new JButton();
	workoutsExerciseDistanceTitleLabel = new JLabel();
	workoutsExerciseDistanceSetsPanel = new JPanel();
	label29 = new JLabel();
	label30 = new JLabel();
	label31 = new JLabel();
	workoutsExerciseDistanceSet1Label = new JLabel();
	workoutsExerciseDistanceSet1DistanceField = new JTextField();
	workoutsExerciseDistanceSet1DurationField = new JTextField();
	mainMenuProfilePanel = new JPanel();
	profileFieldsPanel = new JPanel();
	label4 = new JLabel();
	profileFieldsUsernameLabel = new JLabel();
	label5 = new JLabel();
	profileFieldsEmailField = new JTextField();
	profileFieldsEditEmailButton = new JButton();
	profileFieldsSaveEmailButton = new JButton();
	label6 = new JLabel();
	profileFieldsFNameField = new JTextField();
	profileFieldsEditFNameButton = new JButton();
	profileFieldsSaveFNameButton = new JButton();
	label7 = new JLabel();
	profileFieldsLNameField = new JTextField();
	profileFieldsEditLNameButton = new JButton();
	profileFieldsSaveLNameButton = new JButton();
	label8 = new JLabel();
	profileFieldsPasswordField = new JPasswordField();
	profileFieldsPasswordShowTButton = new JToggleButton();
	profileFieldsEditPasswordButton = new JButton();
	profileFieldsSavePasswordButton = new JButton();
	profileFieldPasswordConfirmLabel = new JLabel();
	profileFieldsPasswordConfirmField = new JPasswordField();
	foodsFoodsFoodsPanel = new JPanel();
	foodsFoodsFoodsTopPanel = new JPanel();
	foodsFoodsFoodsTopBarBackButton = new JButton();
	foodsFoodsFoodsTopBarRecButton = new JButton();
	foodsFoodsFoodsTitleLabel = new JLabel();
	foodsFoodsFoodsInfoPanel = new JPanel();
	label23 = new JLabel();
	foodsFoodsFoodsCaloriesLabel = new JLabel();
	label24 = new JLabel();
	foodsFoodsFoodsProteinLabel = new JLabel();
	label25 = new JLabel();
	foodsFoodsFoodsCarbsLabel = new JLabel();
	label32 = new JLabel();
	foodsFoodsFoodsFatsLabel = new JLabel();
	mealsFoodsFoodsPanel = new JPanel();
	mealsFoodsFoodsTopPanel = new JPanel();
	mealsFoodsFoodsTopBarBackButton = new JButton();
	mealsFoodsFoodsTopBarRecButton = new JButton();
	mealsFoodsFoodsTitleLabel = new JLabel();
	mealsFoodsFoodsInfoPanel = new JPanel();
	label33 = new JLabel();
	mealsFoodsFoodsCaloriesLabel = new JLabel();
	label34 = new JLabel();
	mealsFoodsFoodsProteinLabel = new JLabel();
	label35 = new JLabel();
	mealsFoodsFoodsCarbsLabel = new JLabel();
	label36 = new JLabel();
	mealsFoodsFoodsFatsLabel = new JLabel();

	//======== window ========
	{
	    window.setName("frame1");
	    window.setPreferredSize(new Dimension(1000, 900));
	    window.setTitle("FitTracker");
	    window.setVisible(true);
	    window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	    window.setMinimumSize(new Dimension(1000, 900));
	    window.setMaximumSize(new Dimension(1000, 900));
	    window.setShape(null);
	    window.setIconImage(new ImageIcon(getClass().getResource("/assets/FitTrackerLogoSmall.png")).getImage());
	    var windowContentPane = window.getContentPane();
	    windowContentPane.setLayout(null);

	    //---- mainWindowExitButton ----
	    mainWindowExitButton.setText("EXIT");
	    mainWindowExitButton.setFont(mainWindowExitButton.getFont().deriveFont(mainWindowExitButton.getFont().getStyle() | Font.BOLD));
	    mainWindowExitButton.setForeground(Color.white);
	    mainWindowExitButton.setFocusPainted(false);
	    mainWindowExitButton.addActionListener(e -> exit(e));
	    windowContentPane.add(mainWindowExitButton);
	    mainWindowExitButton.setBounds(825, 815, 157, 40);

	    //======== startPanel ========
	    {
		startPanel.setPreferredSize(new Dimension(750, 750));

		//---- startLogoLabel ----
		startLogoLabel.setIcon(new ImageIcon(getClass().getResource("/assets/FitTrackerLogo.png")));
		startLogoLabel.setHorizontalAlignment(SwingConstants.CENTER);

		//---- startLoginButton ----
		startLoginButton.setText("LOGIN");
		startLoginButton.setForeground(Color.white);
		startLoginButton.setFont(startLoginButton.getFont().deriveFont(startLoginButton.getFont().getStyle() | Font.BOLD, startLoginButton.getFont().getSize() + 10f));
		startLoginButton.addActionListener(e -> startLogin(e));

		//---- startRegisterButton ----
		startRegisterButton.setText("REGISTER");
		startRegisterButton.setFont(startRegisterButton.getFont().deriveFont(startRegisterButton.getFont().getStyle() | Font.BOLD, startRegisterButton.getFont().getSize() + 10f));
		startRegisterButton.setForeground(Color.white);
		startRegisterButton.addActionListener(e -> startRegister(e));

		GroupLayout startPanelLayout = new GroupLayout(startPanel);
		startPanel.setLayout(startPanelLayout);
		startPanelLayout.setHorizontalGroup(
		    startPanelLayout.createParallelGroup()
			.addGroup(startPanelLayout.createSequentialGroup()
			    .addContainerGap(180, Short.MAX_VALUE)
			    .addGroup(startPanelLayout.createParallelGroup()
				.addComponent(startRegisterButton, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE)
				.addComponent(startLoginButton, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE))
			    .addGap(160, 160, 160))
			.addComponent(startLogoLabel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
		);
		startPanelLayout.setVerticalGroup(
		    startPanelLayout.createParallelGroup()
			.addGroup(startPanelLayout.createSequentialGroup()
			    .addGap(80, 80, 80)
			    .addComponent(startLogoLabel)
			    .addGap(108, 108, 108)
			    .addComponent(startLoginButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
			    .addGap(35, 35, 35)
			    .addComponent(startRegisterButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
			    .addContainerGap())
		);
	    }
	    windowContentPane.add(startPanel);
	    startPanel.setBounds(new Rectangle(new Point(124, 48), startPanel.getPreferredSize()));

	    //---- mainWindowLogoutButton ----
	    mainWindowLogoutButton.setText("LOGOUT");
	    mainWindowLogoutButton.setForeground(Color.white);
	    mainWindowLogoutButton.setFont(mainWindowLogoutButton.getFont().deriveFont(mainWindowLogoutButton.getFont().getStyle() | Font.BOLD));
	    mainWindowLogoutButton.setFocusPainted(false);
	    mainWindowLogoutButton.setVisible(false);
	    mainWindowLogoutButton.addActionListener(e -> logout(e));
	    windowContentPane.add(mainWindowLogoutButton);
	    mainWindowLogoutButton.setBounds(15, 815, 157, 40);

	    {
		// compute preferred size
		Dimension preferredSize = new Dimension();
		for(int i = 0; i < windowContentPane.getComponentCount(); i++) {
		    Rectangle bounds = windowContentPane.getComponent(i).getBounds();
		    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
		    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
		}
		Insets insets = windowContentPane.getInsets();
		preferredSize.width += insets.right;
		preferredSize.height += insets.bottom;
		windowContentPane.setMinimumSize(preferredSize);
		windowContentPane.setPreferredSize(preferredSize);
	    }
	    window.pack();
	    window.setLocationRelativeTo(null);
	}

	//======== loginPanel ========
	{
	    loginPanel.setPreferredSize(new Dimension(750, 750));
	    loginPanel.setVisible(false);

	    //---- loginLogoLabel ----
	    loginLogoLabel.setIcon(new ImageIcon(getClass().getResource("/assets/FitTrackerLogo.png")));
	    loginLogoLabel.setHorizontalAlignment(SwingConstants.CENTER);

	    //---- loginUsernameLabel ----
	    loginUsernameLabel.setText("USERNAME:");
	    loginUsernameLabel.setFont(loginUsernameLabel.getFont().deriveFont(loginUsernameLabel.getFont().getStyle() | Font.BOLD, loginUsernameLabel.getFont().getSize() + 10f));
	    loginUsernameLabel.setForeground(Color.white);

	    //---- loginPasswordLabel ----
	    loginPasswordLabel.setText("PASSWORD:");
	    loginPasswordLabel.setFont(loginPasswordLabel.getFont().deriveFont(loginPasswordLabel.getFont().getStyle() | Font.BOLD, loginPasswordLabel.getFont().getSize() + 10f));
	    loginPasswordLabel.setForeground(Color.white);

	    //---- loginUsernameField ----
	    loginUsernameField.setFont(loginUsernameField.getFont().deriveFont(loginUsernameField.getFont().getStyle() | Font.BOLD, loginUsernameField.getFont().getSize() + 10f));
	    loginUsernameField.setForeground(Color.white);
	    loginUsernameField.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    registerTextFieldFocusGained(e);
		}
	    });

	    //---- loginLoginButton ----
	    loginLoginButton.setText("LOGIN");
	    loginLoginButton.setForeground(Color.white);
	    loginLoginButton.setFont(loginLoginButton.getFont().deriveFont(loginLoginButton.getFont().getStyle() | Font.BOLD, loginLoginButton.getFont().getSize() + 10f));
	    loginLoginButton.setPreferredSize(new Dimension(275, 75));
	    loginLoginButton.addActionListener(e -> loginLogin(e));

	    //---- loginBackButton ----
	    loginBackButton.setText("BACK");
	    loginBackButton.setForeground(Color.white);
	    loginBackButton.setFont(loginBackButton.getFont().deriveFont(loginBackButton.getFont().getStyle() | Font.BOLD, loginBackButton.getFont().getSize() + 10f));
	    loginBackButton.setPreferredSize(new Dimension(275, 75));
	    loginBackButton.addActionListener(e -> loginBack(e));

	    //---- loginPasswordField ----
	    loginPasswordField.setFont(loginPasswordField.getFont().deriveFont(loginPasswordField.getFont().getStyle() | Font.BOLD, loginPasswordField.getFont().getSize() + 10f));
	    loginPasswordField.setForeground(Color.white);
	    loginPasswordField.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    registerTextFieldFocusGained(e);
		}
	    });

	    GroupLayout loginPanelLayout = new GroupLayout(loginPanel);
	    loginPanel.setLayout(loginPanelLayout);
	    loginPanelLayout.setHorizontalGroup(
		loginPanelLayout.createParallelGroup()
		    .addComponent(loginLogoLabel, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
		    .addGroup(GroupLayout.Alignment.TRAILING, loginPanelLayout.createSequentialGroup()
			.addContainerGap(46, Short.MAX_VALUE)
			.addGroup(loginPanelLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.TRAILING, loginPanelLayout.createSequentialGroup()
				.addComponent(loginBackButton, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE)
				.addGap(25, 25, 25)
				.addComponent(loginLoginButton, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE)
				.addGap(84, 84, 84))
			    .addGroup(GroupLayout.Alignment.TRAILING, loginPanelLayout.createSequentialGroup()
				.addGroup(loginPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				    .addGroup(loginPanelLayout.createSequentialGroup()
					.addComponent(loginPasswordLabel, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
					.addGap(30, 30, 30)
					.addComponent(loginPasswordField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
				    .addGroup(loginPanelLayout.createSequentialGroup()
					.addComponent(loginUsernameLabel, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
					.addGap(30, 30, 30)
					.addComponent(loginUsernameField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)))
				.addGap(41, 41, 41))))
	    );
	    loginPanelLayout.setVerticalGroup(
		loginPanelLayout.createParallelGroup()
		    .addGroup(loginPanelLayout.createSequentialGroup()
			.addGap(80, 80, 80)
			.addComponent(loginLogoLabel)
			.addGap(160, 160, 160)
			.addGroup(loginPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(loginUsernameField, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
			    .addComponent(loginUsernameLabel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
			.addGap(18, 18, 18)
			.addGroup(loginPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(loginPasswordLabel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
			    .addComponent(loginPasswordField, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 166, Short.MAX_VALUE)
			.addGroup(loginPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(loginBackButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
			    .addComponent(loginLoginButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
			.addGap(40, 40, 40))
	    );
	}

	//======== registerPanel ========
	{
	    registerPanel.setPreferredSize(new Dimension(750, 750));
	    registerPanel.setVisible(false);

	    //---- registerLogoLabel ----
	    registerLogoLabel.setIcon(new ImageIcon(getClass().getResource("/assets/FitTrackerLogo.png")));
	    registerLogoLabel.setHorizontalAlignment(SwingConstants.CENTER);

	    //---- registerUsernameLabel ----
	    registerUsernameLabel.setText("USERNAME:");
	    registerUsernameLabel.setFont(registerUsernameLabel.getFont().deriveFont(registerUsernameLabel.getFont().getStyle() | Font.BOLD, registerUsernameLabel.getFont().getSize() + 10f));
	    registerUsernameLabel.setForeground(Color.white);

	    //---- registerEmailLabel ----
	    registerEmailLabel.setText("EMAIL:");
	    registerEmailLabel.setFont(registerEmailLabel.getFont().deriveFont(registerEmailLabel.getFont().getStyle() | Font.BOLD, registerEmailLabel.getFont().getSize() + 10f));
	    registerEmailLabel.setForeground(Color.white);

	    //---- registerUsernameField ----
	    registerUsernameField.setFont(registerUsernameField.getFont().deriveFont(registerUsernameField.getFont().getStyle() | Font.BOLD, registerUsernameField.getFont().getSize() + 10f));
	    registerUsernameField.setForeground(Color.white);
	    registerUsernameField.setToolTipText("Must be between 4 and 16 characters long and may not contain any special characters.");
	    registerUsernameField.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    registerTextFieldFocusGained(e);
		}
	    });

	    //---- registerEmailField ----
	    registerEmailField.setFont(registerEmailField.getFont().deriveFont(registerEmailField.getFont().getStyle() | Font.BOLD, registerEmailField.getFont().getSize() + 10f));
	    registerEmailField.setForeground(Color.white);
	    registerEmailField.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    registerTextFieldFocusGained(e);
		}
	    });

	    //---- registerRegisterButton ----
	    registerRegisterButton.setText("REGISTER");
	    registerRegisterButton.setForeground(Color.white);
	    registerRegisterButton.setFont(registerRegisterButton.getFont().deriveFont(registerRegisterButton.getFont().getStyle() | Font.BOLD, registerRegisterButton.getFont().getSize() + 10f));
	    registerRegisterButton.setPreferredSize(new Dimension(275, 75));
	    registerRegisterButton.addActionListener(e -> registerRegister(e));

	    //---- registerBackButton ----
	    registerBackButton.setText("BACK");
	    registerBackButton.setForeground(Color.white);
	    registerBackButton.setFont(registerBackButton.getFont().deriveFont(registerBackButton.getFont().getStyle() | Font.BOLD, registerBackButton.getFont().getSize() + 10f));
	    registerBackButton.setPreferredSize(new Dimension(275, 75));
	    registerBackButton.addActionListener(e -> registerBack(e));

	    //---- registerFNameLabel ----
	    registerFNameLabel.setText("FIRST NAME:");
	    registerFNameLabel.setFont(registerFNameLabel.getFont().deriveFont(registerFNameLabel.getFont().getStyle() | Font.BOLD, registerFNameLabel.getFont().getSize() + 10f));
	    registerFNameLabel.setForeground(Color.white);

	    //---- registerFNameField ----
	    registerFNameField.setFont(registerFNameField.getFont().deriveFont(registerFNameField.getFont().getStyle() | Font.BOLD, registerFNameField.getFont().getSize() + 10f));
	    registerFNameField.setForeground(Color.white);

	    //---- registerLNameField ----
	    registerLNameField.setFont(registerLNameField.getFont().deriveFont(registerLNameField.getFont().getStyle() | Font.BOLD, registerLNameField.getFont().getSize() + 10f));
	    registerLNameField.setForeground(Color.white);

	    //---- registerLNameLabel ----
	    registerLNameLabel.setText("LAST NAME:");
	    registerLNameLabel.setFont(registerLNameLabel.getFont().deriveFont(registerLNameLabel.getFont().getStyle() | Font.BOLD, registerLNameLabel.getFont().getSize() + 10f));
	    registerLNameLabel.setForeground(Color.white);

	    //---- registerPasswordLabel ----
	    registerPasswordLabel.setText("PASSWORD:");
	    registerPasswordLabel.setFont(registerPasswordLabel.getFont().deriveFont(registerPasswordLabel.getFont().getStyle() | Font.BOLD, registerPasswordLabel.getFont().getSize() + 10f));
	    registerPasswordLabel.setForeground(Color.white);

	    //---- registerPasswordField ----
	    registerPasswordField.setFont(registerPasswordField.getFont().deriveFont(registerPasswordField.getFont().getStyle() | Font.BOLD, registerPasswordField.getFont().getSize() + 10f));
	    registerPasswordField.setForeground(Color.white);
	    registerPasswordField.setToolTipText("Must be between 8 and 16 characters long, contain atleast one uppercase character, one lowercase character, and one digit.");
	    registerPasswordField.addFocusListener(new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent e) {
		    registerTextFieldFocusGained(e);
		}
	    });

	    GroupLayout registerPanelLayout = new GroupLayout(registerPanel);
	    registerPanel.setLayout(registerPanelLayout);
	    registerPanelLayout.setHorizontalGroup(
		registerPanelLayout.createParallelGroup()
		    .addComponent(registerLogoLabel, GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
		    .addGroup(GroupLayout.Alignment.TRAILING, registerPanelLayout.createSequentialGroup()
			.addContainerGap(46, Short.MAX_VALUE)
			.addGroup(registerPanelLayout.createParallelGroup()
			    .addGroup(GroupLayout.Alignment.TRAILING, registerPanelLayout.createSequentialGroup()
				.addComponent(registerBackButton, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE)
				.addGap(25, 25, 25)
				.addComponent(registerRegisterButton, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE)
				.addGap(84, 84, 84))
			    .addGroup(GroupLayout.Alignment.TRAILING, registerPanelLayout.createSequentialGroup()
				.addGroup(registerPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
				    .addGroup(registerPanelLayout.createSequentialGroup()
					.addComponent(registerLNameLabel, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
					.addGap(18, 18, 18)
					.addComponent(registerLNameField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
				    .addGroup(registerPanelLayout.createParallelGroup()
					.addGroup(GroupLayout.Alignment.TRAILING, registerPanelLayout.createSequentialGroup()
					    .addComponent(registerEmailLabel, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
					    .addGap(30, 30, 30)
					    .addComponent(registerEmailField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
					.addGroup(GroupLayout.Alignment.TRAILING, registerPanelLayout.createSequentialGroup()
					    .addComponent(registerUsernameLabel, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
					    .addGap(30, 30, 30)
					    .addComponent(registerUsernameField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
					.addGroup(registerPanelLayout.createSequentialGroup()
					    .addComponent(registerFNameLabel, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
					    .addGap(18, 18, 18)
					    .addComponent(registerFNameField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)))
				    .addGroup(registerPanelLayout.createSequentialGroup()
					.addComponent(registerPasswordLabel, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
					.addGap(18, 18, 18)
					.addComponent(registerPasswordField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)))
				.addGap(41, 41, 41))))
	    );
	    registerPanelLayout.setVerticalGroup(
		registerPanelLayout.createParallelGroup()
		    .addGroup(registerPanelLayout.createSequentialGroup()
			.addGap(80, 80, 80)
			.addComponent(registerLogoLabel)
			.addGap(54, 54, 54)
			.addGroup(registerPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(registerUsernameField, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
			    .addComponent(registerUsernameLabel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
			.addGap(18, 18, 18)
			.addGroup(registerPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(registerEmailField, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
			    .addComponent(registerEmailLabel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
			.addGap(18, 18, 18)
			.addGroup(registerPanelLayout.createParallelGroup()
			    .addComponent(registerFNameField, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
			    .addComponent(registerFNameLabel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
			.addGap(18, 18, 18)
			.addGroup(registerPanelLayout.createParallelGroup()
			    .addComponent(registerLNameLabel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
			    .addComponent(registerLNameField, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
			.addGap(18, 18, 18)
			.addGroup(registerPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(registerPasswordLabel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
			    .addComponent(registerPasswordField, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE))
			.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
			.addGroup(registerPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			    .addComponent(registerBackButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
			    .addComponent(registerRegisterButton, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
			.addGap(40, 40, 40))
	    );
	}

	//======== mainMenuPanel ========
	{
	    mainMenuPanel.setPreferredSize(new Dimension(1000, 700));
	    mainMenuPanel.setVisible(false);
	    mainMenuPanel.setMinimumSize(new Dimension(1000, 700));
	    mainMenuPanel.setMaximumSize(new Dimension(1000, 700));
	    mainMenuPanel.setLayout(null);

	    //======== mainMenuButtonPanel ========
	    {
		mainMenuButtonPanel.setBackground(new Color(0x1e2428));
		mainMenuButtonPanel.setBorder(null);
		mainMenuButtonPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[center]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- mainMenuLogoButton ----
		mainMenuLogoButton.setHorizontalAlignment(SwingConstants.CENTER);
		mainMenuLogoButton.setIcon(new ImageIcon(getClass().getResource("/assets/FitTrackerLogoSmall.png")));
		mainMenuLogoButton.setMaximumSize(new Dimension(50, 50));
		mainMenuLogoButton.setMinimumSize(new Dimension(85, 66));
		mainMenuLogoButton.setPreferredSize(new Dimension(70, 66));
		mainMenuLogoButton.setBackground(new Color(0x1e2428));
		mainMenuLogoButton.setForeground(new Color(0x1e2428));
		mainMenuLogoButton.setBorderPainted(false);
		mainMenuLogoButton.setBorder(null);
		mainMenuLogoButton.addActionListener(e -> mainMenuLogo(e));
		mainMenuButtonPanel.add(mainMenuLogoButton, "cell 0 0,align center center,grow 0 0");

		//---- mainMenuExercisesButton ----
		mainMenuExercisesButton.setText("EXERCISES");
		mainMenuExercisesButton.setHorizontalAlignment(SwingConstants.CENTER);
		mainMenuExercisesButton.setFont(mainMenuExercisesButton.getFont().deriveFont(mainMenuExercisesButton.getFont().getStyle() | Font.BOLD, mainMenuExercisesButton.getFont().getSize() + 5f));
		mainMenuExercisesButton.setForeground(Color.white);
		mainMenuExercisesButton.addActionListener(e -> mainMenuExercises(e));
		mainMenuButtonPanel.add(mainMenuExercisesButton, "cell 1 0,alignx center,growx 0,width 140:140:140");

		//---- mainMenuWorkoutsButton ----
		mainMenuWorkoutsButton.setText("WORKOUTS");
		mainMenuWorkoutsButton.setHorizontalAlignment(SwingConstants.CENTER);
		mainMenuWorkoutsButton.setFont(mainMenuWorkoutsButton.getFont().deriveFont(mainMenuWorkoutsButton.getFont().getStyle() | Font.BOLD, mainMenuWorkoutsButton.getFont().getSize() + 5f));
		mainMenuWorkoutsButton.setForeground(Color.white);
		mainMenuWorkoutsButton.addActionListener(e -> mainMenuWorkouts(e));
		mainMenuButtonPanel.add(mainMenuWorkoutsButton, "cell 2 0,alignx center,growx 0,width 140:140:140");

		//---- mainMenuFoodsButton ----
		mainMenuFoodsButton.setText("FOODS");
		mainMenuFoodsButton.setHorizontalAlignment(SwingConstants.CENTER);
		mainMenuFoodsButton.setFont(mainMenuFoodsButton.getFont().deriveFont(mainMenuFoodsButton.getFont().getStyle() | Font.BOLD, mainMenuFoodsButton.getFont().getSize() + 5f));
		mainMenuFoodsButton.setForeground(Color.white);
		mainMenuFoodsButton.addActionListener(e -> mainMenuFoods(e));
		mainMenuButtonPanel.add(mainMenuFoodsButton, "cell 3 0,alignx center,growx 0,width 140:140:140");

		//---- mainMenuMealsButton ----
		mainMenuMealsButton.setText("MEALS");
		mainMenuMealsButton.setHorizontalAlignment(SwingConstants.CENTER);
		mainMenuMealsButton.setFont(mainMenuMealsButton.getFont().deriveFont(mainMenuMealsButton.getFont().getStyle() | Font.BOLD, mainMenuMealsButton.getFont().getSize() + 5f));
		mainMenuMealsButton.setForeground(Color.white);
		mainMenuMealsButton.addActionListener(e -> mainMenuMeals(e));
		mainMenuButtonPanel.add(mainMenuMealsButton, "cell 4 0,alignx center,growx 0,width 140:140:140");

		//---- mainMenuProgressButton ----
		mainMenuProgressButton.setText("PROGRESS");
		mainMenuProgressButton.setHorizontalAlignment(SwingConstants.CENTER);
		mainMenuProgressButton.setFont(mainMenuProgressButton.getFont().deriveFont(mainMenuProgressButton.getFont().getStyle() | Font.BOLD, mainMenuProgressButton.getFont().getSize() + 5f));
		mainMenuProgressButton.setForeground(Color.white);
		mainMenuProgressButton.addActionListener(e -> mainMenuProgress(e));
		mainMenuButtonPanel.add(mainMenuProgressButton, "cell 5 0,alignx center,growx 0,width 140:140:140");

		//---- mainMenuProfileButton ----
		mainMenuProfileButton.setText("PROFILE");
		mainMenuProfileButton.setHorizontalAlignment(SwingConstants.CENTER);
		mainMenuProfileButton.setFont(mainMenuProfileButton.getFont().deriveFont(mainMenuProfileButton.getFont().getStyle() | Font.BOLD, mainMenuProfileButton.getFont().getSize() + 5f));
		mainMenuProfileButton.setForeground(Color.white);
		mainMenuProfileButton.addActionListener(e -> mainMenuProfile(e));
		mainMenuButtonPanel.add(mainMenuProfileButton, "cell 6 0,alignx center,growx 0,width 140:140:140");
	    }
	    mainMenuPanel.add(mainMenuButtonPanel);
	    mainMenuButtonPanel.setBounds(0, 0, 1000, 100);

	    //======== mainMenuStartPanel ========
	    {
		mainMenuStartPanel.setPreferredSize(new Dimension(988, 638));
		mainMenuStartPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]" +
		    "[]" +
		    "[]" +
		    "[]"));

		//---- startWelcomeLabel ----
		startWelcomeLabel.setText("Welcome to FitTracker");
		startWelcomeLabel.setFont(startWelcomeLabel.getFont().deriveFont(startWelcomeLabel.getFont().getStyle() | Font.BOLD, startWelcomeLabel.getFont().getSize() + 10f));
		startWelcomeLabel.setForeground(Color.white);
		startWelcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		mainMenuStartPanel.add(startWelcomeLabel, "cell 0 1 3 1,align center bottom,grow 0 0");

		//---- label14 ----
		label14.setText("Please select an option above");
		label14.setForeground(Color.white);
		label14.setFont(label14.getFont().deriveFont(label14.getFont().getStyle() | Font.BOLD, label14.getFont().getSize() + 10f));
		label14.setHorizontalAlignment(SwingConstants.CENTER);
		label14.setVerticalAlignment(SwingConstants.TOP);
		mainMenuStartPanel.add(label14, "cell 1 2,aligny top,growy 0");
	    }
	    mainMenuPanel.add(mainMenuStartPanel);
	    mainMenuStartPanel.setBounds(new Rectangle(new Point(6, 106), mainMenuStartPanel.getPreferredSize()));

	    {
		// compute preferred size
		Dimension preferredSize = new Dimension();
		for(int i = 0; i < mainMenuPanel.getComponentCount(); i++) {
		    Rectangle bounds = mainMenuPanel.getComponent(i).getBounds();
		    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
		    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
		}
		Insets insets = mainMenuPanel.getInsets();
		preferredSize.width += insets.right;
		preferredSize.height += insets.bottom;
		mainMenuPanel.setMinimumSize(preferredSize);
		mainMenuPanel.setPreferredSize(preferredSize);
	    }
	}

	//======== mainMenuExercisesPanel ========
	{
	    mainMenuExercisesPanel.setPreferredSize(new Dimension(988, 638));
	    mainMenuExercisesPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]",
		// rows
		"[]" +
		"[]" +
		"[]" +
		"[]"));

	    //---- exercisesTricepsButton ----
	    exercisesTricepsButton.setText("TRICEPS");
	    exercisesTricepsButton.setForeground(Color.white);
	    exercisesTricepsButton.setFont(exercisesTricepsButton.getFont().deriveFont(exercisesTricepsButton.getFont().getStyle() | Font.BOLD, exercisesTricepsButton.getFont().getSize() + 10f));
	    exercisesTricepsButton.addActionListener(e -> exercisesTriceps(e));
	    mainMenuExercisesPanel.add(exercisesTricepsButton, "cell 0 0,height 200:200:200");

	    //---- exercisesChestButton ----
	    exercisesChestButton.setText("CHEST");
	    exercisesChestButton.setForeground(Color.white);
	    exercisesChestButton.setFont(exercisesChestButton.getFont().deriveFont(exercisesChestButton.getFont().getStyle() | Font.BOLD, exercisesChestButton.getFont().getSize() + 10f));
	    exercisesChestButton.addActionListener(e -> exercisesChest(e));
	    mainMenuExercisesPanel.add(exercisesChestButton, "cell 1 0,height 200:200:200");

	    //---- exercisesShouldersButton ----
	    exercisesShouldersButton.setText("SHOULDERS");
	    exercisesShouldersButton.setForeground(Color.white);
	    exercisesShouldersButton.setFont(exercisesShouldersButton.getFont().deriveFont(exercisesShouldersButton.getFont().getStyle() | Font.BOLD, exercisesShouldersButton.getFont().getSize() + 10f));
	    exercisesShouldersButton.addActionListener(e -> exercisesShoulders(e));
	    mainMenuExercisesPanel.add(exercisesShouldersButton, "cell 2 0,height 200:200:200");

	    //---- exercisesBicepsButton ----
	    exercisesBicepsButton.setText("BICEPS");
	    exercisesBicepsButton.setForeground(Color.white);
	    exercisesBicepsButton.setFont(exercisesBicepsButton.getFont().deriveFont(exercisesBicepsButton.getFont().getStyle() | Font.BOLD, exercisesBicepsButton.getFont().getSize() + 10f));
	    exercisesBicepsButton.addActionListener(e -> exercisesBiceps(e));
	    mainMenuExercisesPanel.add(exercisesBicepsButton, "cell 3 0,height 200:200:200");

	    //---- exercisesCoreButton ----
	    exercisesCoreButton.setText("CORE");
	    exercisesCoreButton.setForeground(Color.white);
	    exercisesCoreButton.setFont(exercisesCoreButton.getFont().deriveFont(exercisesCoreButton.getFont().getStyle() | Font.BOLD, exercisesCoreButton.getFont().getSize() + 10f));
	    exercisesCoreButton.addActionListener(e -> exercisesCore(e));
	    mainMenuExercisesPanel.add(exercisesCoreButton, "cell 0 1,height 200:200:200");

	    //---- exercisesBackButton ----
	    exercisesBackButton.setText("BACK");
	    exercisesBackButton.setForeground(Color.white);
	    exercisesBackButton.setFont(exercisesBackButton.getFont().deriveFont(exercisesBackButton.getFont().getStyle() | Font.BOLD, exercisesBackButton.getFont().getSize() + 10f));
	    exercisesBackButton.addActionListener(e -> exercisesBack(e));
	    mainMenuExercisesPanel.add(exercisesBackButton, "cell 1 1,height 200:200:200");

	    //---- exercisesForearmsButton ----
	    exercisesForearmsButton.setText("FOREARMS");
	    exercisesForearmsButton.setForeground(Color.white);
	    exercisesForearmsButton.setFont(exercisesForearmsButton.getFont().deriveFont(exercisesForearmsButton.getFont().getStyle() | Font.BOLD, exercisesForearmsButton.getFont().getSize() + 10f));
	    exercisesForearmsButton.addActionListener(e -> exercisesForearms(e));
	    mainMenuExercisesPanel.add(exercisesForearmsButton, "cell 2 1,height 200:200:200");

	    //---- exercisesUpperLegsButton ----
	    exercisesUpperLegsButton.setText("UPPER LEGS");
	    exercisesUpperLegsButton.setForeground(Color.white);
	    exercisesUpperLegsButton.setFont(exercisesUpperLegsButton.getFont().deriveFont(exercisesUpperLegsButton.getFont().getStyle() | Font.BOLD, exercisesUpperLegsButton.getFont().getSize() + 10f));
	    exercisesUpperLegsButton.addActionListener(e -> exercisesUpperLegs(e));
	    mainMenuExercisesPanel.add(exercisesUpperLegsButton, "cell 3 1,height 200:200:200");

	    //---- exercisesGlutesButton ----
	    exercisesGlutesButton.setText("GLUTES");
	    exercisesGlutesButton.setForeground(Color.white);
	    exercisesGlutesButton.setFont(exercisesGlutesButton.getFont().deriveFont(exercisesGlutesButton.getFont().getStyle() | Font.BOLD, exercisesGlutesButton.getFont().getSize() + 10f));
	    exercisesGlutesButton.addActionListener(e -> exercisesGlutes(e));
	    mainMenuExercisesPanel.add(exercisesGlutesButton, "cell 0 2,height 200:200:200");

	    //---- exercisesCardioButton ----
	    exercisesCardioButton.setText("CARDIO");
	    exercisesCardioButton.setForeground(Color.white);
	    exercisesCardioButton.setFont(exercisesCardioButton.getFont().deriveFont(exercisesCardioButton.getFont().getStyle() | Font.BOLD, exercisesCardioButton.getFont().getSize() + 10f));
	    exercisesCardioButton.addActionListener(e -> exercisesCardio(e));
	    mainMenuExercisesPanel.add(exercisesCardioButton, "cell 1 2,height 200:200:200");

	    //---- exercisesLowerLegsButton ----
	    exercisesLowerLegsButton.setText("LOWER LEGS");
	    exercisesLowerLegsButton.setForeground(Color.white);
	    exercisesLowerLegsButton.setFont(exercisesLowerLegsButton.getFont().deriveFont(exercisesLowerLegsButton.getFont().getStyle() | Font.BOLD, exercisesLowerLegsButton.getFont().getSize() + 10f));
	    exercisesLowerLegsButton.addActionListener(e -> exercisesLowerLegs(e));
	    mainMenuExercisesPanel.add(exercisesLowerLegsButton, "cell 2 2,height 200:200:200");

	    //---- exercisesAddNewButton ----
	    exercisesAddNewButton.setText("ADD NEW");
	    exercisesAddNewButton.setFont(exercisesAddNewButton.getFont().deriveFont(exercisesAddNewButton.getFont().getStyle() | Font.BOLD, exercisesAddNewButton.getFont().getSize() + 10f));
	    exercisesAddNewButton.setForeground(Color.white);
	    exercisesAddNewButton.addActionListener(e -> exercisesAddNew(e));
	    mainMenuExercisesPanel.add(exercisesAddNewButton, "cell 3 2,height 200:200:200");
	}

	//======== mainMenuWorkoutsPanel ========
	{
	    mainMenuWorkoutsPanel.setPreferredSize(new Dimension(988, 638));
	    mainMenuWorkoutsPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]",
		// rows
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]"));

	    //---- workoutSelectButton ----
	    workoutSelectButton.setText("Select Workout");
	    workoutSelectButton.setFont(workoutSelectButton.getFont().deriveFont(workoutSelectButton.getFont().getStyle() | Font.BOLD, workoutSelectButton.getFont().getSize() + 5f));
	    workoutSelectButton.setForeground(Color.white);
	    workoutSelectButton.addActionListener(e -> workoutSelect(e));
	    mainMenuWorkoutsPanel.add(workoutSelectButton, "cell 2 3,height 100:100:100");

	    //---- workoutAddNewButton ----
	    workoutAddNewButton.setText("Add New Workout");
	    workoutAddNewButton.setForeground(Color.white);
	    workoutAddNewButton.setFont(workoutAddNewButton.getFont().deriveFont(workoutAddNewButton.getFont().getStyle() | Font.BOLD, workoutAddNewButton.getFont().getSize() + 5f));
	    mainMenuWorkoutsPanel.add(workoutAddNewButton, "cell 5 3,height 100:100:100");
	}

	//======== mainMenuFoodsPanel ========
	{
	    mainMenuFoodsPanel.setPreferredSize(new Dimension(988, 638));
	    mainMenuFoodsPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]",
		// rows
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]"));

	    //---- foodSelectButton ----
	    foodSelectButton.setText("Select Food");
	    foodSelectButton.setForeground(Color.white);
	    foodSelectButton.setFont(foodSelectButton.getFont().deriveFont(foodSelectButton.getFont().getStyle() | Font.BOLD, foodSelectButton.getFont().getSize() + 5f));
	    foodSelectButton.addActionListener(e -> foodSelect(e));
	    mainMenuFoodsPanel.add(foodSelectButton, "cell 2 3,height 100:100:100");

	    //---- foodAddNewButton ----
	    foodAddNewButton.setText("Add New Food");
	    foodAddNewButton.setForeground(Color.white);
	    foodAddNewButton.setFont(foodAddNewButton.getFont().deriveFont(foodAddNewButton.getFont().getStyle() | Font.BOLD, foodAddNewButton.getFont().getSize() + 5f));
	    mainMenuFoodsPanel.add(foodAddNewButton, "cell 5 3,height 100:100:100");
	}

	//======== mainMenuMealsPanel ========
	{
	    mainMenuMealsPanel.setPreferredSize(new Dimension(988, 638));
	    mainMenuMealsPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]",
		// rows
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]"));

	    //---- mealSelectButton ----
	    mealSelectButton.setText("Select Meal");
	    mealSelectButton.setFont(mealSelectButton.getFont().deriveFont(mealSelectButton.getFont().getStyle() | Font.BOLD, mealSelectButton.getFont().getSize() + 5f));
	    mealSelectButton.setForeground(Color.white);
	    mealSelectButton.addActionListener(e -> mealSelect(e));
	    mainMenuMealsPanel.add(mealSelectButton, "cell 2 3,height 100:100:100");

	    //---- mealAddNewButton ----
	    mealAddNewButton.setText("Add New Meal");
	    mealAddNewButton.setForeground(Color.white);
	    mealAddNewButton.setFont(mealAddNewButton.getFont().deriveFont(mealAddNewButton.getFont().getStyle() | Font.BOLD, mealAddNewButton.getFont().getSize() + 5f));
	    mainMenuMealsPanel.add(mealAddNewButton, "cell 5 3,height 100:100:100");
	}

	//======== mainMenuProgressPanel ========
	{
	    mainMenuProgressPanel.setPreferredSize(new Dimension(988, 638));
	    mainMenuProgressPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]" +
		"[fill]",
		// rows
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]" +
		"[]"));
	}

	//======== exercisesExercisesPanel ========
	{
	    exercisesExercisesPanel.setPreferredSize(new Dimension(988, 638));
	    exercisesExercisesPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[grow,fill]"));

	    //======== exercisesExercisesTopPanel ========
	    {
		exercisesExercisesTopPanel.setBackground(new Color(0x1e2428));
		exercisesExercisesTopPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[left]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- exercisesExercisesTopBarBackButton ----
		exercisesExercisesTopBarBackButton.setText("BACK");
		exercisesExercisesTopBarBackButton.setFont(exercisesExercisesTopBarBackButton.getFont().deriveFont(exercisesExercisesTopBarBackButton.getFont().getStyle() | Font.BOLD));
		exercisesExercisesTopBarBackButton.setForeground(Color.white);
		exercisesExercisesTopBarBackButton.addActionListener(e -> exercisesExercisesTopBarBack(e));
		exercisesExercisesTopPanel.add(exercisesExercisesTopBarBackButton, "cell 0 0");

		//---- exercisesExercisesTopBarAllButton ----
		exercisesExercisesTopBarAllButton.setText("ALL EXERCISES");
		exercisesExercisesTopBarAllButton.setForeground(Color.white);
		exercisesExercisesTopBarAllButton.setFont(exercisesExercisesTopBarAllButton.getFont().deriveFont(exercisesExercisesTopBarAllButton.getFont().getStyle() | Font.BOLD));
		exercisesExercisesTopBarAllButton.addActionListener(e -> exercisesExercisesTopBarAll(e));
		exercisesExercisesTopPanel.add(exercisesExercisesTopBarAllButton, "cell 1 0");

		//---- exercisesExercisesTopBarFTButton ----
		exercisesExercisesTopBarFTButton.setText("FITTRACKER EXERCISES");
		exercisesExercisesTopBarFTButton.setForeground(Color.white);
		exercisesExercisesTopBarFTButton.setFont(exercisesExercisesTopBarFTButton.getFont().deriveFont(exercisesExercisesTopBarFTButton.getFont().getStyle() | Font.BOLD));
		exercisesExercisesTopBarFTButton.addActionListener(e -> exercisesExercisesTopBarFT(e));
		exercisesExercisesTopPanel.add(exercisesExercisesTopBarFTButton, "cell 2 0");

		//---- exercisesExercisesTopBarCusButton ----
		exercisesExercisesTopBarCusButton.setText("CUSTOM EXERCISES");
		exercisesExercisesTopBarCusButton.setForeground(Color.white);
		exercisesExercisesTopBarCusButton.setFont(exercisesExercisesTopBarCusButton.getFont().deriveFont(exercisesExercisesTopBarCusButton.getFont().getStyle() | Font.BOLD));
		exercisesExercisesTopBarCusButton.addActionListener(e -> exercisesExercisesTopBarCus(e));
		exercisesExercisesTopPanel.add(exercisesExercisesTopBarCusButton, "cell 3 0");
	    }
	    exercisesExercisesPanel.add(exercisesExercisesTopPanel, "cell 0 0");

	    //======== exercisesExercisesScrollPanel ========
	    {
		exercisesExercisesScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    }
	    exercisesExercisesPanel.add(exercisesExercisesScrollPanel, "cell 0 1");
	}

	//======== exercisesWeightPanel ========
	{
	    exercisesWeightPanel.setPreferredSize(new Dimension(988, 638));
	    exercisesWeightPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[]" +
		"[]" +
		"[grow,fill]"));

	    //======== exercisesWeightTopPanel ========
	    {
		exercisesWeightTopPanel.setBackground(new Color(0x1e2428));
		exercisesWeightTopPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- exercisesWeightTopBarBackButton ----
		exercisesWeightTopBarBackButton.setText("BACK");
		exercisesWeightTopBarBackButton.setForeground(Color.white);
		exercisesWeightTopBarBackButton.setFont(exercisesWeightTopBarBackButton.getFont().deriveFont(exercisesWeightTopBarBackButton.getFont().getStyle() | Font.BOLD));
		exercisesWeightTopBarBackButton.addActionListener(e -> exercisesWeightTopBarBack(e));
		exercisesWeightTopPanel.add(exercisesWeightTopBarBackButton, "cell 0 0,alignx left,growx 0");

		//---- exercisesWeightTopBarAddButton ----
		exercisesWeightTopBarAddButton.setText("ADD SET");
		exercisesWeightTopBarAddButton.setFont(exercisesWeightTopBarAddButton.getFont().deriveFont(exercisesWeightTopBarAddButton.getFont().getStyle() | Font.BOLD));
		exercisesWeightTopBarAddButton.setForeground(Color.white);
		exercisesWeightTopBarAddButton.addActionListener(e -> exercisesWeightTopBarAdd(e));
		exercisesWeightTopPanel.add(exercisesWeightTopBarAddButton, "cell 2 0");

		//---- exercisesWeightTopBarRemButton ----
		exercisesWeightTopBarRemButton.setText("REMOVE SET");
		exercisesWeightTopBarRemButton.setFont(exercisesWeightTopBarRemButton.getFont().deriveFont(exercisesWeightTopBarRemButton.getFont().getStyle() | Font.BOLD));
		exercisesWeightTopBarRemButton.setForeground(Color.white);
		exercisesWeightTopBarRemButton.addActionListener(e -> exercisesWeightTopBarRem(e));
		exercisesWeightTopPanel.add(exercisesWeightTopBarRemButton, "cell 3 0");

		//---- exercisesWeightTopBarRecButton ----
		exercisesWeightTopBarRecButton.setText("RECORD EXERCISE");
		exercisesWeightTopBarRecButton.setForeground(Color.white);
		exercisesWeightTopBarRecButton.setFont(exercisesWeightTopBarRecButton.getFont().deriveFont(exercisesWeightTopBarRecButton.getFont().getStyle() | Font.BOLD));
		exercisesWeightTopBarRecButton.addActionListener(e -> exercisesWeightTopBarRec(e));
		exercisesWeightTopPanel.add(exercisesWeightTopBarRecButton, "cell 4 0");
	    }
	    exercisesWeightPanel.add(exercisesWeightTopPanel, "cell 0 0");

	    //---- exercisesWeightTitleLabel ----
	    exercisesWeightTitleLabel.setText("Exercise");
	    exercisesWeightTitleLabel.setFont(exercisesWeightTitleLabel.getFont().deriveFont(exercisesWeightTitleLabel.getFont().getStyle() | Font.BOLD, exercisesWeightTitleLabel.getFont().getSize() + 10f));
	    exercisesWeightTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    exercisesWeightTitleLabel.setForeground(Color.white);
	    exercisesWeightPanel.add(exercisesWeightTitleLabel, "cell 0 2");

	    //======== weightSetsPanel ========
	    {
		weightSetsPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[center]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]"));

		//---- label17 ----
		label17.setText("Sets");
		label17.setHorizontalAlignment(SwingConstants.CENTER);
		label17.setForeground(UIManager.getColor("#545556"));
		label17.setFont(label17.getFont().deriveFont(label17.getFont().getStyle() | Font.BOLD, label17.getFont().getSize() + 2f));
		weightSetsPanel.add(label17, "cell 0 0");

		//---- label18 ----
		label18.setText("Weight (lbs)");
		label18.setHorizontalAlignment(SwingConstants.CENTER);
		label18.setForeground(UIManager.getColor("#545556"));
		label18.setFont(label18.getFont().deriveFont(label18.getFont().getStyle() | Font.BOLD, label18.getFont().getSize() + 2f));
		weightSetsPanel.add(label18, "cell 1 0");

		//---- label19 ----
		label19.setText("Reps");
		label19.setHorizontalAlignment(SwingConstants.CENTER);
		label19.setForeground(UIManager.getColor("#545556"));
		label19.setFont(label19.getFont().deriveFont(label19.getFont().getStyle() | Font.BOLD, label19.getFont().getSize() + 2f));
		weightSetsPanel.add(label19, "cell 3 0");

		//---- weightSet1Label ----
		weightSet1Label.setText("1");
		weightSet1Label.setHorizontalAlignment(SwingConstants.CENTER);
		weightSet1Label.setFont(weightSet1Label.getFont().deriveFont(weightSet1Label.getFont().getSize() + 2f));
		weightSetsPanel.add(weightSet1Label, "cell 0 1");

		//---- weightSet1WeightField ----
		weightSet1WeightField.setHorizontalAlignment(SwingConstants.CENTER);
		weightSet1WeightField.setFont(weightSet1WeightField.getFont().deriveFont(weightSet1WeightField.getFont().getStyle() | Font.BOLD, weightSet1WeightField.getFont().getSize() + 5f));
		weightSet1WeightField.setForeground(Color.white);
		weightSet1WeightField.setName("weightSet1WeightField");
		weightSetsPanel.add(weightSet1WeightField, "cell 1 1,alignx center,growx 0");

		//---- weightSet1XLabel ----
		weightSet1XLabel.setText("X");
		weightSet1XLabel.setHorizontalAlignment(SwingConstants.CENTER);
		weightSet1XLabel.setFont(weightSet1XLabel.getFont().deriveFont(weightSet1XLabel.getFont().getSize() + 2f));
		weightSetsPanel.add(weightSet1XLabel, "cell 2 1");

		//---- weightSet1RepsField ----
		weightSet1RepsField.setHorizontalAlignment(SwingConstants.CENTER);
		weightSet1RepsField.setFont(weightSet1RepsField.getFont().deriveFont(weightSet1RepsField.getFont().getStyle() | Font.BOLD, weightSet1RepsField.getFont().getSize() + 5f));
		weightSet1RepsField.setForeground(Color.white);
		weightSet1RepsField.setName("weightSet1RepsField");
		weightSetsPanel.add(weightSet1RepsField, "cell 3 1,alignx center,growx 0");
	    }
	    exercisesWeightPanel.add(weightSetsPanel, "cell 0 3");
	}

	//======== exercisesDistancePanel ========
	{
	    exercisesDistancePanel.setPreferredSize(new Dimension(988, 638));
	    exercisesDistancePanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[]" +
		"[]" +
		"[grow,fill]"));

	    //======== exercisesDistanceTopPanel ========
	    {
		exercisesDistanceTopPanel.setBackground(new Color(0x1e2428));
		exercisesDistanceTopPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- exercisesDistanceTopBarBackButton ----
		exercisesDistanceTopBarBackButton.setText("BACK");
		exercisesDistanceTopBarBackButton.setForeground(Color.white);
		exercisesDistanceTopBarBackButton.setFont(exercisesDistanceTopBarBackButton.getFont().deriveFont(exercisesDistanceTopBarBackButton.getFont().getStyle() | Font.BOLD));
		exercisesDistanceTopBarBackButton.addActionListener(e -> exercisesDistanceTopBarBack(e));
		exercisesDistanceTopPanel.add(exercisesDistanceTopBarBackButton, "cell 0 0,alignx left,growx 0");

		//---- exercisesDistanceTopBarAddButton ----
		exercisesDistanceTopBarAddButton.setText("ADD SET");
		exercisesDistanceTopBarAddButton.setFont(exercisesDistanceTopBarAddButton.getFont().deriveFont(exercisesDistanceTopBarAddButton.getFont().getStyle() | Font.BOLD));
		exercisesDistanceTopBarAddButton.setForeground(Color.white);
		exercisesDistanceTopBarAddButton.addActionListener(e -> exercisesDistanceTopBarAdd(e));
		exercisesDistanceTopPanel.add(exercisesDistanceTopBarAddButton, "cell 2 0");

		//---- exercisesDistanceTopBarRemButton ----
		exercisesDistanceTopBarRemButton.setText("REMOVE SET");
		exercisesDistanceTopBarRemButton.setFont(exercisesDistanceTopBarRemButton.getFont().deriveFont(exercisesDistanceTopBarRemButton.getFont().getStyle() | Font.BOLD));
		exercisesDistanceTopBarRemButton.setForeground(Color.white);
		exercisesDistanceTopBarRemButton.addActionListener(e -> exercisesDistanceTopBarRem(e));
		exercisesDistanceTopPanel.add(exercisesDistanceTopBarRemButton, "cell 3 0");

		//---- exercisesDistanceTopBarRecButton ----
		exercisesDistanceTopBarRecButton.setText("RECORD EXERCISE");
		exercisesDistanceTopBarRecButton.setFont(exercisesDistanceTopBarRecButton.getFont().deriveFont(exercisesDistanceTopBarRecButton.getFont().getStyle() | Font.BOLD));
		exercisesDistanceTopBarRecButton.setForeground(Color.white);
		exercisesDistanceTopBarRecButton.addActionListener(e -> exercisesDistanceTopBarRec(e));
		exercisesDistanceTopPanel.add(exercisesDistanceTopBarRecButton, "cell 4 0");
	    }
	    exercisesDistancePanel.add(exercisesDistanceTopPanel, "cell 0 0");

	    //---- exercisesDistanceTitleLabel ----
	    exercisesDistanceTitleLabel.setText("Exercise");
	    exercisesDistanceTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    exercisesDistanceTitleLabel.setFont(exercisesDistanceTitleLabel.getFont().deriveFont(exercisesDistanceTitleLabel.getFont().getStyle() | Font.BOLD, exercisesDistanceTitleLabel.getFont().getSize() + 10f));
	    exercisesDistanceTitleLabel.setForeground(Color.white);
	    exercisesDistancePanel.add(exercisesDistanceTitleLabel, "cell 0 2");

	    //======== distanceSetsPanel ========
	    {
		distanceSetsPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[center]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]"));

		//---- label26 ----
		label26.setText("Sets");
		label26.setHorizontalAlignment(SwingConstants.CENTER);
		label26.setForeground(UIManager.getColor("#545556"));
		label26.setFont(label26.getFont().deriveFont(label26.getFont().getStyle() | Font.BOLD, label26.getFont().getSize() + 2f));
		distanceSetsPanel.add(label26, "cell 0 0");

		//---- label27 ----
		label27.setText("Distance (mi)");
		label27.setHorizontalAlignment(SwingConstants.CENTER);
		label27.setForeground(UIManager.getColor("#545556"));
		label27.setFont(label27.getFont().deriveFont(label27.getFont().getStyle() | Font.BOLD, label27.getFont().getSize() + 2f));
		distanceSetsPanel.add(label27, "cell 1 0");

		//---- label28 ----
		label28.setText("Duration (min)");
		label28.setHorizontalAlignment(SwingConstants.CENTER);
		label28.setForeground(UIManager.getColor("#545556"));
		label28.setFont(label28.getFont().deriveFont(label28.getFont().getStyle() | Font.BOLD, label28.getFont().getSize() + 2f));
		distanceSetsPanel.add(label28, "cell 2 0");

		//---- distanceSet1Label ----
		distanceSet1Label.setText("1");
		distanceSet1Label.setHorizontalAlignment(SwingConstants.CENTER);
		distanceSet1Label.setFont(distanceSet1Label.getFont().deriveFont(distanceSet1Label.getFont().getSize() + 2f));
		distanceSetsPanel.add(distanceSet1Label, "cell 0 1");

		//---- distanceSet1DistanceField ----
		distanceSet1DistanceField.setHorizontalAlignment(SwingConstants.CENTER);
		distanceSet1DistanceField.setFont(distanceSet1DistanceField.getFont().deriveFont(distanceSet1DistanceField.getFont().getStyle() | Font.BOLD, distanceSet1DistanceField.getFont().getSize() + 5f));
		distanceSet1DistanceField.setForeground(Color.white);
		distanceSetsPanel.add(distanceSet1DistanceField, "cell 1 1,alignx center,growx 0");

		//---- distanceSet1DurationField ----
		distanceSet1DurationField.setHorizontalAlignment(SwingConstants.CENTER);
		distanceSet1DurationField.setFont(distanceSet1DurationField.getFont().deriveFont(distanceSet1DurationField.getFont().getStyle() | Font.BOLD, distanceSet1DurationField.getFont().getSize() + 5f));
		distanceSet1DurationField.setForeground(Color.white);
		distanceSetsPanel.add(distanceSet1DurationField, "cell 2 1,alignx center,growx 0");
	    }
	    exercisesDistancePanel.add(distanceSetsPanel, "cell 0 3");
	}

	//======== exercisesCustomPanel ========
	{
	    exercisesCustomPanel.setPreferredSize(new Dimension(988, 638));
	    exercisesCustomPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[grow,fill]"));

	    //======== exercisesExercisesTopPanel2 ========
	    {
		exercisesExercisesTopPanel2.setBackground(new Color(0x1e2428));
		exercisesExercisesTopPanel2.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[left]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- exercisesCustomTopBarBackButton ----
		exercisesCustomTopBarBackButton.setText("BACK");
		exercisesCustomTopBarBackButton.setFont(exercisesCustomTopBarBackButton.getFont().deriveFont(exercisesCustomTopBarBackButton.getFont().getStyle() | Font.BOLD));
		exercisesCustomTopBarBackButton.setForeground(Color.white);
		exercisesCustomTopBarBackButton.addActionListener(e -> exercisesCustomTopBarBack(e));
		exercisesExercisesTopPanel2.add(exercisesCustomTopBarBackButton, "cell 0 0");

		//---- exercisesCustomTopBarAddButton ----
		exercisesCustomTopBarAddButton.setText("ADD EXERCISE");
		exercisesCustomTopBarAddButton.setFont(exercisesCustomTopBarAddButton.getFont().deriveFont(exercisesCustomTopBarAddButton.getFont().getStyle() | Font.BOLD));
		exercisesCustomTopBarAddButton.setForeground(Color.white);
		exercisesCustomTopBarAddButton.addActionListener(e -> exercisesCustomTopBarAdd(e));
		exercisesExercisesTopPanel2.add(exercisesCustomTopBarAddButton, "cell 3 0");
	    }
	    exercisesCustomPanel.add(exercisesExercisesTopPanel2, "cell 0 0");

	    //======== customFieldsPanel ========
	    {
		customFieldsPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[center]" +
		    "[center]" +
		    "[center]" +
		    "[center]" +
		    "[center]" +
		    "[center]" +
		    "[center]" +
		    "[center]" +
		    "[center]"));

		//---- label1 ----
		label1.setText("Name:");
		label1.setFont(label1.getFont().deriveFont(label1.getFont().getStyle() | Font.BOLD, label1.getFont().getSize() + 2f));
		label1.setForeground(Color.white);
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		customFieldsPanel.add(label1, "cell 0 0");

		//---- exerciseCustomNameField ----
		exerciseCustomNameField.setText("Exercise");
		customFieldsPanel.add(exerciseCustomNameField, "cell 1 0 4 1");

		//---- label2 ----
		label2.setText("Record Type:");
		label2.setFont(label2.getFont().deriveFont(label2.getFont().getStyle() | Font.BOLD, label2.getFont().getSize() + 2f));
		label2.setForeground(Color.white);
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		customFieldsPanel.add(label2, "cell 0 1");

		//---- exerciseCustomWeightRButton ----
		exerciseCustomWeightRButton.setText("Weight-based");
		exerciseCustomWeightRButton.setSelected(true);
		customFieldsPanel.add(exerciseCustomWeightRButton, "cell 1 1 2 1,alignx center,growx 0");

		//---- exerciseCustomDistanceRButton ----
		exerciseCustomDistanceRButton.setText("Distance-based");
		customFieldsPanel.add(exerciseCustomDistanceRButton, "cell 3 1 2 1,alignx center,growx 0");

		//---- label3 ----
		label3.setText("Muscle Group:");
		label3.setFont(label3.getFont().deriveFont(label3.getFont().getStyle() | Font.BOLD, label3.getFont().getSize() + 2f));
		label3.setForeground(Color.white);
		label3.setHorizontalAlignment(SwingConstants.CENTER);
		customFieldsPanel.add(label3, "cell 0 2");

		//---- exerciseCustomTricepsRButton ----
		exerciseCustomTricepsRButton.setText("Triceps");
		exerciseCustomTricepsRButton.setSelected(true);
		customFieldsPanel.add(exerciseCustomTricepsRButton, "cell 1 2,alignx center,growx 0");

		//---- exerciseCustomChestRButton ----
		exerciseCustomChestRButton.setText("Chest");
		customFieldsPanel.add(exerciseCustomChestRButton, "cell 2 2,alignx center,growx 0");

		//---- exerciseCustomShouldersRButton ----
		exerciseCustomShouldersRButton.setText("Shoulders");
		customFieldsPanel.add(exerciseCustomShouldersRButton, "cell 3 2,alignx center,growx 0");

		//---- exerciseCustomBicepsRButton ----
		exerciseCustomBicepsRButton.setText("Biceps");
		customFieldsPanel.add(exerciseCustomBicepsRButton, "cell 4 2,alignx center,growx 0");

		//---- exerciseCustomCoreRButton ----
		exerciseCustomCoreRButton.setText("Core");
		customFieldsPanel.add(exerciseCustomCoreRButton, "cell 1 3,alignx center,growx 0");

		//---- exerciseCustomBackRButton ----
		exerciseCustomBackRButton.setText("Back");
		customFieldsPanel.add(exerciseCustomBackRButton, "cell 2 3,alignx center,growx 0");

		//---- exerciseCustomForearmsRButton ----
		exerciseCustomForearmsRButton.setText("Forearms");
		customFieldsPanel.add(exerciseCustomForearmsRButton, "cell 3 3,alignx center,growx 0");

		//---- exerciseCustomUpperLegsRButton ----
		exerciseCustomUpperLegsRButton.setText("Upper Legs");
		customFieldsPanel.add(exerciseCustomUpperLegsRButton, "cell 4 3,alignx center,growx 0");

		//---- exerciseCustomGlutesRButton ----
		exerciseCustomGlutesRButton.setText("Glutes");
		customFieldsPanel.add(exerciseCustomGlutesRButton, "cell 1 4,alignx center,growx 0");

		//---- exerciseCustomCardioRButton ----
		exerciseCustomCardioRButton.setText("Cardio");
		customFieldsPanel.add(exerciseCustomCardioRButton, "cell 2 4,alignx center,growx 0");

		//---- exerciseCustomLowerLegsRButton ----
		exerciseCustomLowerLegsRButton.setText("Lower Legs");
		customFieldsPanel.add(exerciseCustomLowerLegsRButton, "cell 3 4,alignx center,growx 0");
	    }
	    exercisesCustomPanel.add(customFieldsPanel, "cell 0 1");
	}

	//======== workoutsWorkoutsPanel ========
	{
	    workoutsWorkoutsPanel.setPreferredSize(new Dimension(988, 638));
	    workoutsWorkoutsPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[grow,fill]"));

	    //======== workoutsWorkoutsTopPanel ========
	    {
		workoutsWorkoutsTopPanel.setBackground(new Color(0x1e2428));
		workoutsWorkoutsTopPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[left]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- workoutsWorkoutsTopBarBackButton ----
		workoutsWorkoutsTopBarBackButton.setText("BACK");
		workoutsWorkoutsTopBarBackButton.setFont(workoutsWorkoutsTopBarBackButton.getFont().deriveFont(workoutsWorkoutsTopBarBackButton.getFont().getStyle() | Font.BOLD));
		workoutsWorkoutsTopBarBackButton.setForeground(Color.white);
		workoutsWorkoutsTopBarBackButton.addActionListener(e -> workoutsWorkoutsTopBarBack(e));
		workoutsWorkoutsTopPanel.add(workoutsWorkoutsTopBarBackButton, "cell 0 0");

		//---- workoutsWorkoutsTopBarAllButton ----
		workoutsWorkoutsTopBarAllButton.setText("ALL WORKOUTS");
		workoutsWorkoutsTopBarAllButton.setForeground(Color.white);
		workoutsWorkoutsTopBarAllButton.setFont(workoutsWorkoutsTopBarAllButton.getFont().deriveFont(workoutsWorkoutsTopBarAllButton.getFont().getStyle() | Font.BOLD));
		workoutsWorkoutsTopBarAllButton.addActionListener(e -> workoutsWorkoutsTopBarAll(e));
		workoutsWorkoutsTopPanel.add(workoutsWorkoutsTopBarAllButton, "cell 1 0");

		//---- workoutsWorkoutsTopBarFTButton ----
		workoutsWorkoutsTopBarFTButton.setText("FITTRACKER WORKOUTS");
		workoutsWorkoutsTopBarFTButton.setForeground(Color.white);
		workoutsWorkoutsTopBarFTButton.setFont(workoutsWorkoutsTopBarFTButton.getFont().deriveFont(workoutsWorkoutsTopBarFTButton.getFont().getStyle() | Font.BOLD));
		workoutsWorkoutsTopBarFTButton.addActionListener(e -> workoutsWorkoutsTopBarFT(e));
		workoutsWorkoutsTopPanel.add(workoutsWorkoutsTopBarFTButton, "cell 2 0");

		//---- workoutsWorkoutsTopBarCusButton ----
		workoutsWorkoutsTopBarCusButton.setText("CUSTOM WORKOUTS");
		workoutsWorkoutsTopBarCusButton.setForeground(Color.white);
		workoutsWorkoutsTopBarCusButton.setFont(workoutsWorkoutsTopBarCusButton.getFont().deriveFont(workoutsWorkoutsTopBarCusButton.getFont().getStyle() | Font.BOLD));
		workoutsWorkoutsTopBarCusButton.addActionListener(e -> workoutsWorkoutsTopBarCus(e));
		workoutsWorkoutsTopPanel.add(workoutsWorkoutsTopBarCusButton, "cell 3 0");
	    }
	    workoutsWorkoutsPanel.add(workoutsWorkoutsTopPanel, "cell 0 0");

	    //======== workoutsWorkoutsScrollPanel ========
	    {
		workoutsWorkoutsScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    }
	    workoutsWorkoutsPanel.add(workoutsWorkoutsScrollPanel, "cell 0 1");
	}

	//======== workoutsExercisesPanel ========
	{
	    workoutsExercisesPanel.setPreferredSize(new Dimension(988, 638));
	    workoutsExercisesPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[grow,fill]"));

	    //======== workoutsExercisesTopPanel ========
	    {
		workoutsExercisesTopPanel.setBackground(new Color(0x1e2428));
		workoutsExercisesTopPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[left]" +
		    "[right]" +
		    "[center]" +
		    "[right]",
		    // rows
		    "[]"));

		//---- workoutsExercisesTopBarBackButton ----
		workoutsExercisesTopBarBackButton.setText("BACK");
		workoutsExercisesTopBarBackButton.setFont(workoutsExercisesTopBarBackButton.getFont().deriveFont(workoutsExercisesTopBarBackButton.getFont().getStyle() | Font.BOLD));
		workoutsExercisesTopBarBackButton.setForeground(Color.white);
		workoutsExercisesTopBarBackButton.addActionListener(e -> workoutsExercisesTopBarBack(e));
		workoutsExercisesTopPanel.add(workoutsExercisesTopBarBackButton, "cell 0 0");

		//---- workoutsWorkoutTitleLabel ----
		workoutsWorkoutTitleLabel.setText("Workout");
		workoutsWorkoutTitleLabel.setFont(workoutsWorkoutTitleLabel.getFont().deriveFont(workoutsWorkoutTitleLabel.getFont().getStyle() | Font.BOLD, workoutsWorkoutTitleLabel.getFont().getSize() + 5f));
		workoutsWorkoutTitleLabel.setForeground(Color.white);
		workoutsExercisesTopPanel.add(workoutsWorkoutTitleLabel, "cell 1 0");
	    }
	    workoutsExercisesPanel.add(workoutsExercisesTopPanel, "cell 0 0");

	    //======== workoutsExercisesScrollPanel ========
	    {
		workoutsExercisesScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    }
	    workoutsExercisesPanel.add(workoutsExercisesScrollPanel, "cell 0 1");
	}

	//======== foodsFoodsPanel ========
	{
	    foodsFoodsPanel.setPreferredSize(new Dimension(988, 638));
	    foodsFoodsPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[grow,fill]"));

	    //======== foodsFoodsTopPanel ========
	    {
		foodsFoodsTopPanel.setBackground(new Color(0x1e2428));
		foodsFoodsTopPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[left]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- foodsFoodsTopBarBackButton ----
		foodsFoodsTopBarBackButton.setText("BACK");
		foodsFoodsTopBarBackButton.setFont(foodsFoodsTopBarBackButton.getFont().deriveFont(foodsFoodsTopBarBackButton.getFont().getStyle() | Font.BOLD));
		foodsFoodsTopBarBackButton.setForeground(Color.white);
		foodsFoodsTopBarBackButton.addActionListener(e -> foodsFoodsTopBarBack(e));
		foodsFoodsTopPanel.add(foodsFoodsTopBarBackButton, "cell 0 0");
	    }
	    foodsFoodsPanel.add(foodsFoodsTopPanel, "cell 0 0");

	    //======== foodsFoodsScrollPanel ========
	    {
		foodsFoodsScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    }
	    foodsFoodsPanel.add(foodsFoodsScrollPanel, "cell 0 1");
	}

	//======== mealsMealsPanel ========
	{
	    mealsMealsPanel.setPreferredSize(new Dimension(988, 638));
	    mealsMealsPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[grow,fill]"));

	    //======== mealsMealsTopPanel ========
	    {
		mealsMealsTopPanel.setBackground(new Color(0x1e2428));
		mealsMealsTopPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[left]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- mealsMealsTopBarBackButton ----
		mealsMealsTopBarBackButton.setText("BACK");
		mealsMealsTopBarBackButton.setFont(mealsMealsTopBarBackButton.getFont().deriveFont(mealsMealsTopBarBackButton.getFont().getStyle() | Font.BOLD));
		mealsMealsTopBarBackButton.setForeground(Color.white);
		mealsMealsTopBarBackButton.addActionListener(e -> mealsMealsTopBarBack(e));
		mealsMealsTopPanel.add(mealsMealsTopBarBackButton, "cell 0 0");

		//---- mealsMealsTopBarAllButton ----
		mealsMealsTopBarAllButton.setText("ALL MEALS");
		mealsMealsTopBarAllButton.setForeground(Color.white);
		mealsMealsTopBarAllButton.setFont(mealsMealsTopBarAllButton.getFont().deriveFont(mealsMealsTopBarAllButton.getFont().getStyle() | Font.BOLD));
		mealsMealsTopBarAllButton.addActionListener(e -> mealsMealsTopBarAll(e));
		mealsMealsTopPanel.add(mealsMealsTopBarAllButton, "cell 1 0");

		//---- mealsMealsTopBarFTButton ----
		mealsMealsTopBarFTButton.setText("FITTRACKER MEALS");
		mealsMealsTopBarFTButton.setForeground(Color.white);
		mealsMealsTopBarFTButton.setFont(mealsMealsTopBarFTButton.getFont().deriveFont(mealsMealsTopBarFTButton.getFont().getStyle() | Font.BOLD));
		mealsMealsTopBarFTButton.addActionListener(e -> mealsMealsTopBarFT(e));
		mealsMealsTopPanel.add(mealsMealsTopBarFTButton, "cell 2 0");

		//---- mealsMealsTopBarCusButton ----
		mealsMealsTopBarCusButton.setText("CUSTOM MEALS");
		mealsMealsTopBarCusButton.setForeground(Color.white);
		mealsMealsTopBarCusButton.setFont(mealsMealsTopBarCusButton.getFont().deriveFont(mealsMealsTopBarCusButton.getFont().getStyle() | Font.BOLD));
		mealsMealsTopBarCusButton.addActionListener(e -> mealsMealsTopBarCus(e));
		mealsMealsTopPanel.add(mealsMealsTopBarCusButton, "cell 3 0");
	    }
	    mealsMealsPanel.add(mealsMealsTopPanel, "cell 0 0");

	    //======== mealsMealsScrollPanel ========
	    {
		mealsMealsScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    }
	    mealsMealsPanel.add(mealsMealsScrollPanel, "cell 0 1");
	}

	//======== mealsFoodsPanel ========
	{
	    mealsFoodsPanel.setPreferredSize(new Dimension(988, 638));
	    mealsFoodsPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[grow,fill]"));

	    //======== mealsFoodsTopPanel ========
	    {
		mealsFoodsTopPanel.setBackground(new Color(0x1e2428));
		mealsFoodsTopPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[left]" +
		    "[right]" +
		    "[center]" +
		    "[right]",
		    // rows
		    "[]"));

		//---- mealsFoodsTopBarBackButton ----
		mealsFoodsTopBarBackButton.setText("BACK");
		mealsFoodsTopBarBackButton.setFont(mealsFoodsTopBarBackButton.getFont().deriveFont(mealsFoodsTopBarBackButton.getFont().getStyle() | Font.BOLD));
		mealsFoodsTopBarBackButton.setForeground(Color.white);
		mealsFoodsTopBarBackButton.addActionListener(e -> mealsFoodsTopBarBack(e));
		mealsFoodsTopPanel.add(mealsFoodsTopBarBackButton, "cell 0 0");

		//---- mealsMealTitleLabel ----
		mealsMealTitleLabel.setText("Meal");
		mealsMealTitleLabel.setFont(mealsMealTitleLabel.getFont().deriveFont(mealsMealTitleLabel.getFont().getStyle() | Font.BOLD, mealsMealTitleLabel.getFont().getSize() + 5f));
		mealsMealTitleLabel.setForeground(Color.white);
		mealsFoodsTopPanel.add(mealsMealTitleLabel, "cell 1 0");
	    }
	    mealsFoodsPanel.add(mealsFoodsTopPanel, "cell 0 0");

	    //======== mealsFoodsScrollPanel ========
	    {
		mealsFoodsScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    }
	    mealsFoodsPanel.add(mealsFoodsScrollPanel, "cell 0 1");
	}

	//======== workoutsExerciseWeightPanel ========
	{
	    workoutsExerciseWeightPanel.setPreferredSize(new Dimension(988, 638));
	    workoutsExerciseWeightPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[]" +
		"[]" +
		"[grow,fill]"));

	    //======== workoutsExerciseWeightTopPanel ========
	    {
		workoutsExerciseWeightTopPanel.setBackground(new Color(0x1e2428));
		workoutsExerciseWeightTopPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- workoutsExerciseWeightTopBarBackButton ----
		workoutsExerciseWeightTopBarBackButton.setText("BACK");
		workoutsExerciseWeightTopBarBackButton.setForeground(Color.white);
		workoutsExerciseWeightTopBarBackButton.setFont(workoutsExerciseWeightTopBarBackButton.getFont().deriveFont(workoutsExerciseWeightTopBarBackButton.getFont().getStyle() | Font.BOLD));
		workoutsExerciseWeightTopBarBackButton.addActionListener(e -> workoutsExerciseWeightTopBarBack(e));
		workoutsExerciseWeightTopPanel.add(workoutsExerciseWeightTopBarBackButton, "cell 0 0,alignx left,growx 0");

		//---- workoutsExerciseWeightTopBarAddButton ----
		workoutsExerciseWeightTopBarAddButton.setText("ADD SET");
		workoutsExerciseWeightTopBarAddButton.setFont(workoutsExerciseWeightTopBarAddButton.getFont().deriveFont(workoutsExerciseWeightTopBarAddButton.getFont().getStyle() | Font.BOLD));
		workoutsExerciseWeightTopBarAddButton.setForeground(Color.white);
		workoutsExerciseWeightTopBarAddButton.addActionListener(e -> workoutsExerciseWeightTopBarAdd(e));
		workoutsExerciseWeightTopPanel.add(workoutsExerciseWeightTopBarAddButton, "cell 2 0");

		//---- workoutsExerciseWeightTopBarRemButton ----
		workoutsExerciseWeightTopBarRemButton.setText("REMOVE SET");
		workoutsExerciseWeightTopBarRemButton.setFont(workoutsExerciseWeightTopBarRemButton.getFont().deriveFont(workoutsExerciseWeightTopBarRemButton.getFont().getStyle() | Font.BOLD));
		workoutsExerciseWeightTopBarRemButton.setForeground(Color.white);
		workoutsExerciseWeightTopBarRemButton.addActionListener(e -> workoutsExerciseWeightTopBarRem(e));
		workoutsExerciseWeightTopPanel.add(workoutsExerciseWeightTopBarRemButton, "cell 3 0");

		//---- workoutsExerciseWeightTopBarRecButton ----
		workoutsExerciseWeightTopBarRecButton.setText("RECORD EXERCISE");
		workoutsExerciseWeightTopBarRecButton.setForeground(Color.white);
		workoutsExerciseWeightTopBarRecButton.setFont(workoutsExerciseWeightTopBarRecButton.getFont().deriveFont(workoutsExerciseWeightTopBarRecButton.getFont().getStyle() | Font.BOLD));
		workoutsExerciseWeightTopBarRecButton.addActionListener(e -> workoutsExerciseWeightTopBarRec(e));
		workoutsExerciseWeightTopPanel.add(workoutsExerciseWeightTopBarRecButton, "cell 4 0");
	    }
	    workoutsExerciseWeightPanel.add(workoutsExerciseWeightTopPanel, "cell 0 0");

	    //---- workoutsExerciseWeightTitleLabel ----
	    workoutsExerciseWeightTitleLabel.setText("Exercise");
	    workoutsExerciseWeightTitleLabel.setFont(workoutsExerciseWeightTitleLabel.getFont().deriveFont(workoutsExerciseWeightTitleLabel.getFont().getStyle() | Font.BOLD, workoutsExerciseWeightTitleLabel.getFont().getSize() + 10f));
	    workoutsExerciseWeightTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    workoutsExerciseWeightTitleLabel.setForeground(Color.white);
	    workoutsExerciseWeightPanel.add(workoutsExerciseWeightTitleLabel, "cell 0 2");

	    //======== workoutsExerciseWeightSetsPanel ========
	    {
		workoutsExerciseWeightSetsPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[center]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]"));

		//---- label20 ----
		label20.setText("Sets");
		label20.setHorizontalAlignment(SwingConstants.CENTER);
		label20.setForeground(UIManager.getColor("#545556"));
		label20.setFont(label20.getFont().deriveFont(label20.getFont().getStyle() | Font.BOLD, label20.getFont().getSize() + 2f));
		workoutsExerciseWeightSetsPanel.add(label20, "cell 0 0");

		//---- label21 ----
		label21.setText("Weight (lbs)");
		label21.setHorizontalAlignment(SwingConstants.CENTER);
		label21.setForeground(UIManager.getColor("#545556"));
		label21.setFont(label21.getFont().deriveFont(label21.getFont().getStyle() | Font.BOLD, label21.getFont().getSize() + 2f));
		workoutsExerciseWeightSetsPanel.add(label21, "cell 1 0");

		//---- label22 ----
		label22.setText("Reps");
		label22.setHorizontalAlignment(SwingConstants.CENTER);
		label22.setForeground(UIManager.getColor("#545556"));
		label22.setFont(label22.getFont().deriveFont(label22.getFont().getStyle() | Font.BOLD, label22.getFont().getSize() + 2f));
		workoutsExerciseWeightSetsPanel.add(label22, "cell 3 0");

		//---- workoutsExerciseWeightSet1Label ----
		workoutsExerciseWeightSet1Label.setText("1");
		workoutsExerciseWeightSet1Label.setHorizontalAlignment(SwingConstants.CENTER);
		workoutsExerciseWeightSet1Label.setFont(workoutsExerciseWeightSet1Label.getFont().deriveFont(workoutsExerciseWeightSet1Label.getFont().getSize() + 2f));
		workoutsExerciseWeightSetsPanel.add(workoutsExerciseWeightSet1Label, "cell 0 1");

		//---- workoutsExerciseWeightSet1WeightField ----
		workoutsExerciseWeightSet1WeightField.setHorizontalAlignment(SwingConstants.CENTER);
		workoutsExerciseWeightSet1WeightField.setFont(workoutsExerciseWeightSet1WeightField.getFont().deriveFont(workoutsExerciseWeightSet1WeightField.getFont().getStyle() | Font.BOLD, workoutsExerciseWeightSet1WeightField.getFont().getSize() + 5f));
		workoutsExerciseWeightSet1WeightField.setForeground(Color.white);
		weightSet1WeightField.setName("weightSet1WeightField");
		workoutsExerciseWeightSetsPanel.add(workoutsExerciseWeightSet1WeightField, "cell 1 1,alignx center,growx 0");

		//---- workoutsExerciseWeightSet1XLabel ----
		workoutsExerciseWeightSet1XLabel.setText("X");
		workoutsExerciseWeightSet1XLabel.setHorizontalAlignment(SwingConstants.CENTER);
		workoutsExerciseWeightSet1XLabel.setFont(workoutsExerciseWeightSet1XLabel.getFont().deriveFont(workoutsExerciseWeightSet1XLabel.getFont().getSize() + 2f));
		workoutsExerciseWeightSetsPanel.add(workoutsExerciseWeightSet1XLabel, "cell 2 1");

		//---- workoutsExerciseWeightSet1RepsField ----
		workoutsExerciseWeightSet1RepsField.setHorizontalAlignment(SwingConstants.CENTER);
		workoutsExerciseWeightSet1RepsField.setFont(workoutsExerciseWeightSet1RepsField.getFont().deriveFont(workoutsExerciseWeightSet1RepsField.getFont().getStyle() | Font.BOLD, workoutsExerciseWeightSet1RepsField.getFont().getSize() + 5f));
		workoutsExerciseWeightSet1RepsField.setForeground(Color.white);
		weightSet1RepsField.setName("weightSet1RepsField");
		workoutsExerciseWeightSetsPanel.add(workoutsExerciseWeightSet1RepsField, "cell 3 1,alignx center,growx 0");
	    }
	    workoutsExerciseWeightPanel.add(workoutsExerciseWeightSetsPanel, "cell 0 3");
	}

	//======== workoutsExerciseDistancePanel ========
	{
	    workoutsExerciseDistancePanel.setPreferredSize(new Dimension(988, 638));
	    workoutsExerciseDistancePanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[]" +
		"[]" +
		"[grow,fill]"));

	    //======== exercisesDistanceTopPanel2 ========
	    {
		exercisesDistanceTopPanel2.setBackground(new Color(0x1e2428));
		exercisesDistanceTopPanel2.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- workoutsExerciseDistanceTopBarBackButton ----
		workoutsExerciseDistanceTopBarBackButton.setText("BACK");
		workoutsExerciseDistanceTopBarBackButton.setForeground(Color.white);
		workoutsExerciseDistanceTopBarBackButton.setFont(workoutsExerciseDistanceTopBarBackButton.getFont().deriveFont(workoutsExerciseDistanceTopBarBackButton.getFont().getStyle() | Font.BOLD));
		workoutsExerciseDistanceTopBarBackButton.addActionListener(e -> workoutsExerciseDistanceTopBarBack(e));
		exercisesDistanceTopPanel2.add(workoutsExerciseDistanceTopBarBackButton, "cell 0 0,alignx left,growx 0");

		//---- workoutsExerciseDistanceTopBarAddButton ----
		workoutsExerciseDistanceTopBarAddButton.setText("ADD SET");
		workoutsExerciseDistanceTopBarAddButton.setFont(workoutsExerciseDistanceTopBarAddButton.getFont().deriveFont(workoutsExerciseDistanceTopBarAddButton.getFont().getStyle() | Font.BOLD));
		workoutsExerciseDistanceTopBarAddButton.setForeground(Color.white);
		workoutsExerciseDistanceTopBarAddButton.addActionListener(e -> workoutsExerciseDistanceTopBarAdd(e));
		exercisesDistanceTopPanel2.add(workoutsExerciseDistanceTopBarAddButton, "cell 2 0");

		//---- workoutsExerciseDistanceTopBarRemButton ----
		workoutsExerciseDistanceTopBarRemButton.setText("REMOVE SET");
		workoutsExerciseDistanceTopBarRemButton.setFont(workoutsExerciseDistanceTopBarRemButton.getFont().deriveFont(workoutsExerciseDistanceTopBarRemButton.getFont().getStyle() | Font.BOLD));
		workoutsExerciseDistanceTopBarRemButton.setForeground(Color.white);
		workoutsExerciseDistanceTopBarRemButton.addActionListener(e -> workoutsExerciseDistanceTopBarRem(e));
		exercisesDistanceTopPanel2.add(workoutsExerciseDistanceTopBarRemButton, "cell 3 0");

		//---- workoutsExerciseDistanceTopBarRecButton ----
		workoutsExerciseDistanceTopBarRecButton.setText("RECORD EXERCISE");
		workoutsExerciseDistanceTopBarRecButton.setFont(workoutsExerciseDistanceTopBarRecButton.getFont().deriveFont(workoutsExerciseDistanceTopBarRecButton.getFont().getStyle() | Font.BOLD));
		workoutsExerciseDistanceTopBarRecButton.setForeground(Color.white);
		workoutsExerciseDistanceTopBarRecButton.addActionListener(e -> workoutsExerciseDistanceTopBarRec(e));
		exercisesDistanceTopPanel2.add(workoutsExerciseDistanceTopBarRecButton, "cell 4 0");
	    }
	    workoutsExerciseDistancePanel.add(exercisesDistanceTopPanel2, "cell 0 0");

	    //---- workoutsExerciseDistanceTitleLabel ----
	    workoutsExerciseDistanceTitleLabel.setText("Exercise");
	    workoutsExerciseDistanceTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    workoutsExerciseDistanceTitleLabel.setFont(workoutsExerciseDistanceTitleLabel.getFont().deriveFont(workoutsExerciseDistanceTitleLabel.getFont().getStyle() | Font.BOLD, workoutsExerciseDistanceTitleLabel.getFont().getSize() + 10f));
	    workoutsExerciseDistanceTitleLabel.setForeground(Color.white);
	    workoutsExerciseDistancePanel.add(workoutsExerciseDistanceTitleLabel, "cell 0 2");

	    //======== workoutsExerciseDistanceSetsPanel ========
	    {
		workoutsExerciseDistanceSetsPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[center]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]" +
		    "[top]"));

		//---- label29 ----
		label29.setText("Sets");
		label29.setHorizontalAlignment(SwingConstants.CENTER);
		label29.setForeground(UIManager.getColor("#545556"));
		label29.setFont(label29.getFont().deriveFont(label29.getFont().getStyle() | Font.BOLD, label29.getFont().getSize() + 2f));
		workoutsExerciseDistanceSetsPanel.add(label29, "cell 0 0");

		//---- label30 ----
		label30.setText("Distance (mi)");
		label30.setHorizontalAlignment(SwingConstants.CENTER);
		label30.setForeground(UIManager.getColor("#545556"));
		label30.setFont(label30.getFont().deriveFont(label30.getFont().getStyle() | Font.BOLD, label30.getFont().getSize() + 2f));
		workoutsExerciseDistanceSetsPanel.add(label30, "cell 1 0");

		//---- label31 ----
		label31.setText("Duration (min)");
		label31.setHorizontalAlignment(SwingConstants.CENTER);
		label31.setForeground(UIManager.getColor("#545556"));
		label31.setFont(label31.getFont().deriveFont(label31.getFont().getStyle() | Font.BOLD, label31.getFont().getSize() + 2f));
		workoutsExerciseDistanceSetsPanel.add(label31, "cell 2 0");

		//---- workoutsExerciseDistanceSet1Label ----
		workoutsExerciseDistanceSet1Label.setText("1");
		workoutsExerciseDistanceSet1Label.setHorizontalAlignment(SwingConstants.CENTER);
		workoutsExerciseDistanceSet1Label.setFont(workoutsExerciseDistanceSet1Label.getFont().deriveFont(workoutsExerciseDistanceSet1Label.getFont().getSize() + 2f));
		workoutsExerciseDistanceSetsPanel.add(workoutsExerciseDistanceSet1Label, "cell 0 1");

		//---- workoutsExerciseDistanceSet1DistanceField ----
		workoutsExerciseDistanceSet1DistanceField.setHorizontalAlignment(SwingConstants.CENTER);
		workoutsExerciseDistanceSet1DistanceField.setFont(workoutsExerciseDistanceSet1DistanceField.getFont().deriveFont(workoutsExerciseDistanceSet1DistanceField.getFont().getStyle() | Font.BOLD, workoutsExerciseDistanceSet1DistanceField.getFont().getSize() + 5f));
		workoutsExerciseDistanceSet1DistanceField.setForeground(Color.white);
		workoutsExerciseDistanceSetsPanel.add(workoutsExerciseDistanceSet1DistanceField, "cell 1 1,alignx center,growx 0");

		//---- workoutsExerciseDistanceSet1DurationField ----
		workoutsExerciseDistanceSet1DurationField.setHorizontalAlignment(SwingConstants.CENTER);
		workoutsExerciseDistanceSet1DurationField.setFont(workoutsExerciseDistanceSet1DurationField.getFont().deriveFont(workoutsExerciseDistanceSet1DurationField.getFont().getStyle() | Font.BOLD, workoutsExerciseDistanceSet1DurationField.getFont().getSize() + 5f));
		workoutsExerciseDistanceSet1DurationField.setForeground(Color.white);
		workoutsExerciseDistanceSetsPanel.add(workoutsExerciseDistanceSet1DurationField, "cell 2 1,alignx center,growx 0");
	    }
	    workoutsExerciseDistancePanel.add(workoutsExerciseDistanceSetsPanel, "cell 0 3");
	}

	//======== mainMenuProfilePanel ========
	{
	    mainMenuProfilePanel.setPreferredSize(new Dimension(988, 638));
	    mainMenuProfilePanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[grow,fill]"));

	    //======== profileFieldsPanel ========
	    {
		profileFieldsPanel.setLayout(new MigLayout(
		    "fill,insets 0,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[right]" +
		    "[right]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[]" +
		    "[fill]"));

		//---- label4 ----
		label4.setIcon(new ImageIcon(getClass().getResource("/assets/profilePicture.png")));
		label4.setHorizontalAlignment(SwingConstants.CENTER);
		profileFieldsPanel.add(label4, "cell 1 1");

		//---- profileFieldsUsernameLabel ----
		profileFieldsUsernameLabel.setText("Username");
		profileFieldsUsernameLabel.setForeground(Color.white);
		profileFieldsUsernameLabel.setFont(profileFieldsUsernameLabel.getFont().deriveFont(profileFieldsUsernameLabel.getFont().getStyle() | Font.BOLD, profileFieldsUsernameLabel.getFont().getSize() + 10f));
		profileFieldsPanel.add(profileFieldsUsernameLabel, "cell 2 1 3 1");

		//---- label5 ----
		label5.setText("Email:");
		label5.setHorizontalAlignment(SwingConstants.CENTER);
		label5.setFont(label5.getFont().deriveFont(label5.getFont().getStyle() | Font.BOLD, label5.getFont().getSize() + 2f));
		profileFieldsPanel.add(label5, "cell 1 2");

		//---- profileFieldsEmailField ----
		profileFieldsEmailField.setEditable(false);
		profileFieldsEmailField.addFocusListener(new FocusAdapter() {
		    @Override
		    public void focusGained(FocusEvent e) {
			registerTextFieldFocusGained(e);
		    }
		});
		profileFieldsPanel.add(profileFieldsEmailField, "cell 2 2 4 1,height 40:40:40");

		//---- profileFieldsEditEmailButton ----
		profileFieldsEditEmailButton.setText("EDIT");
		profileFieldsEditEmailButton.setFont(profileFieldsEditEmailButton.getFont().deriveFont(profileFieldsEditEmailButton.getFont().getStyle() | Font.BOLD));
		profileFieldsEditEmailButton.setForeground(Color.white);
		profileFieldsEditEmailButton.addActionListener(e -> profileFieldsEditEmail(e));
		profileFieldsPanel.add(profileFieldsEditEmailButton, "cell 7 2,alignx center,growx 0");

		//---- profileFieldsSaveEmailButton ----
		profileFieldsSaveEmailButton.setText("SAVE");
		profileFieldsSaveEmailButton.setFont(profileFieldsSaveEmailButton.getFont().deriveFont(profileFieldsSaveEmailButton.getFont().getStyle() | Font.BOLD));
		profileFieldsSaveEmailButton.setForeground(Color.white);
		profileFieldsSaveEmailButton.setVisible(false);
		profileFieldsSaveEmailButton.addActionListener(e -> profileFieldsSaveEmail(e));
		profileFieldsPanel.add(profileFieldsSaveEmailButton, "cell 8 2,alignx center,growx 0");

		//---- label6 ----
		label6.setText("First Name:");
		label6.setFont(label6.getFont().deriveFont(label6.getFont().getStyle() | Font.BOLD, label6.getFont().getSize() + 2f));
		label6.setHorizontalAlignment(SwingConstants.CENTER);
		profileFieldsPanel.add(label6, "cell 1 3");

		//---- profileFieldsFNameField ----
		profileFieldsFNameField.setEditable(false);
		profileFieldsPanel.add(profileFieldsFNameField, "cell 2 3 4 1,height 40:40:40");

		//---- profileFieldsEditFNameButton ----
		profileFieldsEditFNameButton.setText("EDIT");
		profileFieldsEditFNameButton.setFont(profileFieldsEditFNameButton.getFont().deriveFont(profileFieldsEditFNameButton.getFont().getStyle() | Font.BOLD));
		profileFieldsEditFNameButton.setForeground(Color.white);
		profileFieldsEditFNameButton.addActionListener(e -> profileFieldsEditFName(e));
		profileFieldsPanel.add(profileFieldsEditFNameButton, "cell 7 3,alignx center,growx 0");

		//---- profileFieldsSaveFNameButton ----
		profileFieldsSaveFNameButton.setText("SAVE");
		profileFieldsSaveFNameButton.setFont(profileFieldsSaveFNameButton.getFont().deriveFont(profileFieldsSaveFNameButton.getFont().getStyle() | Font.BOLD));
		profileFieldsSaveFNameButton.setForeground(Color.white);
		profileFieldsSaveFNameButton.setVisible(false);
		profileFieldsSaveFNameButton.addActionListener(e -> profileFieldsSaveFName(e));
		profileFieldsPanel.add(profileFieldsSaveFNameButton, "cell 8 3,alignx center,growx 0");

		//---- label7 ----
		label7.setText("Last Name:");
		label7.setFont(label7.getFont().deriveFont(label7.getFont().getStyle() | Font.BOLD, label7.getFont().getSize() + 2f));
		label7.setHorizontalAlignment(SwingConstants.CENTER);
		profileFieldsPanel.add(label7, "cell 1 4");

		//---- profileFieldsLNameField ----
		profileFieldsLNameField.setEditable(false);
		profileFieldsPanel.add(profileFieldsLNameField, "cell 2 4 4 1,height 40:40:40");

		//---- profileFieldsEditLNameButton ----
		profileFieldsEditLNameButton.setText("EDIT");
		profileFieldsEditLNameButton.setFont(profileFieldsEditLNameButton.getFont().deriveFont(profileFieldsEditLNameButton.getFont().getStyle() | Font.BOLD));
		profileFieldsEditLNameButton.setForeground(Color.white);
		profileFieldsEditLNameButton.addActionListener(e -> profileFieldsEditLName(e));
		profileFieldsPanel.add(profileFieldsEditLNameButton, "cell 7 4,alignx center,growx 0");

		//---- profileFieldsSaveLNameButton ----
		profileFieldsSaveLNameButton.setText("SAVE");
		profileFieldsSaveLNameButton.setFont(profileFieldsSaveLNameButton.getFont().deriveFont(profileFieldsSaveLNameButton.getFont().getStyle() | Font.BOLD));
		profileFieldsSaveLNameButton.setForeground(Color.white);
		profileFieldsSaveLNameButton.setVisible(false);
		profileFieldsSaveLNameButton.addActionListener(e -> profileFieldsSaveLName(e));
		profileFieldsPanel.add(profileFieldsSaveLNameButton, "cell 8 4,alignx center,growx 0");

		//---- label8 ----
		label8.setText("Password:");
		label8.setFont(label8.getFont().deriveFont(label8.getFont().getStyle() | Font.BOLD, label8.getFont().getSize() + 2f));
		label8.setHorizontalAlignment(SwingConstants.CENTER);
		profileFieldsPanel.add(label8, "cell 1 5");

		//---- profileFieldsPasswordField ----
		profileFieldsPasswordField.setEditable(false);
		profileFieldsPasswordField.addFocusListener(new FocusAdapter() {
		    @Override
		    public void focusGained(FocusEvent e) {
			registerTextFieldFocusGained(e);
		    }
		});
		profileFieldsPanel.add(profileFieldsPasswordField, "cell 2 5 4 1,height 40:40:40");

		//---- profileFieldsPasswordShowTButton ----
		profileFieldsPasswordShowTButton.setIcon(UIManager.getIcon("PasswordField.revealIcon"));
		profileFieldsPasswordShowTButton.setBorder(null);
		profileFieldsPasswordShowTButton.setBorderPainted(false);
		profileFieldsPasswordShowTButton.setContentAreaFilled(false);
		profileFieldsPasswordShowTButton.setVisible(false);
		profileFieldsPasswordShowTButton.addActionListener(e -> profileFieldsPasswordShowTButton(e));
		profileFieldsPanel.add(profileFieldsPasswordShowTButton, "cell 6 5,alignx left,growx 0");

		//---- profileFieldsEditPasswordButton ----
		profileFieldsEditPasswordButton.setText("EDIT");
		profileFieldsEditPasswordButton.setFont(profileFieldsEditPasswordButton.getFont().deriveFont(profileFieldsEditPasswordButton.getFont().getStyle() | Font.BOLD));
		profileFieldsEditPasswordButton.setForeground(Color.white);
		profileFieldsEditPasswordButton.addActionListener(e -> profileFieldsEditPassword(e));
		profileFieldsPanel.add(profileFieldsEditPasswordButton, "cell 7 5,alignx center,growx 0");

		//---- profileFieldsSavePasswordButton ----
		profileFieldsSavePasswordButton.setText("SAVE");
		profileFieldsSavePasswordButton.setFont(profileFieldsSavePasswordButton.getFont().deriveFont(profileFieldsSavePasswordButton.getFont().getStyle() | Font.BOLD));
		profileFieldsSavePasswordButton.setForeground(Color.white);
		profileFieldsSavePasswordButton.setVisible(false);
		profileFieldsSavePasswordButton.addActionListener(e -> profileFieldsSavePassword(e));
		profileFieldsPanel.add(profileFieldsSavePasswordButton, "cell 8 5,alignx center,growx 0");

		//---- profileFieldPasswordConfirmLabel ----
		profileFieldPasswordConfirmLabel.setText("Confirm Password:");
		profileFieldPasswordConfirmLabel.setFont(profileFieldPasswordConfirmLabel.getFont().deriveFont(profileFieldPasswordConfirmLabel.getFont().getStyle() | Font.BOLD, profileFieldPasswordConfirmLabel.getFont().getSize() + 2f));
		profileFieldPasswordConfirmLabel.setHorizontalAlignment(SwingConstants.CENTER);
		profileFieldPasswordConfirmLabel.setVisible(false);
		profileFieldsPanel.add(profileFieldPasswordConfirmLabel, "cell 1 6");

		//---- profileFieldsPasswordConfirmField ----
		profileFieldsPasswordConfirmField.setEditable(false);
		profileFieldsPasswordConfirmField.setVisible(false);
		profileFieldsPasswordConfirmField.addFocusListener(new FocusAdapter() {
		    @Override
		    public void focusGained(FocusEvent e) {
			registerTextFieldFocusGained(e);
		    }
		});
		profileFieldsPanel.add(profileFieldsPasswordConfirmField, "cell 2 6 4 1,height 40:40:40");
	    }
	    mainMenuProfilePanel.add(profileFieldsPanel, "cell 0 0");
	}

	//======== foodsFoodsFoodsPanel ========
	{
	    foodsFoodsFoodsPanel.setPreferredSize(new Dimension(988, 638));
	    foodsFoodsFoodsPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[]" +
		"[]" +
		"[grow,fill]"));

	    //======== foodsFoodsFoodsTopPanel ========
	    {
		foodsFoodsFoodsTopPanel.setBackground(new Color(0x1e2428));
		foodsFoodsFoodsTopPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- foodsFoodsFoodsTopBarBackButton ----
		foodsFoodsFoodsTopBarBackButton.setText("BACK");
		foodsFoodsFoodsTopBarBackButton.setForeground(Color.white);
		foodsFoodsFoodsTopBarBackButton.setFont(foodsFoodsFoodsTopBarBackButton.getFont().deriveFont(foodsFoodsFoodsTopBarBackButton.getFont().getStyle() | Font.BOLD));
		foodsFoodsFoodsTopBarBackButton.addActionListener(e -> foodsFoodsFoodsTopBarBack(e));
		foodsFoodsFoodsTopPanel.add(foodsFoodsFoodsTopBarBackButton, "cell 0 0,alignx left,growx 0");

		//---- foodsFoodsFoodsTopBarRecButton ----
		foodsFoodsFoodsTopBarRecButton.setText("RECORD FOOD");
		foodsFoodsFoodsTopBarRecButton.setForeground(Color.white);
		foodsFoodsFoodsTopBarRecButton.setFont(foodsFoodsFoodsTopBarRecButton.getFont().deriveFont(foodsFoodsFoodsTopBarRecButton.getFont().getStyle() | Font.BOLD));
		foodsFoodsFoodsTopBarRecButton.addActionListener(e -> foodsFoodsFoodsTopBarRec(e));
		foodsFoodsFoodsTopPanel.add(foodsFoodsFoodsTopBarRecButton, "cell 4 0");
	    }
	    foodsFoodsFoodsPanel.add(foodsFoodsFoodsTopPanel, "cell 0 0");

	    //---- foodsFoodsFoodsTitleLabel ----
	    foodsFoodsFoodsTitleLabel.setText("Food");
	    foodsFoodsFoodsTitleLabel.setFont(foodsFoodsFoodsTitleLabel.getFont().deriveFont(foodsFoodsFoodsTitleLabel.getFont().getStyle() | Font.BOLD, foodsFoodsFoodsTitleLabel.getFont().getSize() + 10f));
	    foodsFoodsFoodsTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    foodsFoodsFoodsTitleLabel.setForeground(Color.white);
	    foodsFoodsFoodsPanel.add(foodsFoodsFoodsTitleLabel, "cell 0 2");

	    //======== foodsFoodsFoodsInfoPanel ========
	    {
		foodsFoodsFoodsInfoPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[center]" +
		    "[center]" +
		    "[center]" +
		    "[center]"));

		//---- label23 ----
		label23.setText("Calories");
		label23.setHorizontalAlignment(SwingConstants.CENTER);
		label23.setForeground(UIManager.getColor("#545556"));
		label23.setFont(label23.getFont().deriveFont(label23.getFont().getStyle() | Font.BOLD, label23.getFont().getSize() + 5f));
		foodsFoodsFoodsInfoPanel.add(label23, "cell 1 0");

		//---- foodsFoodsFoodsCaloriesLabel ----
		foodsFoodsFoodsCaloriesLabel.setText("text");
		foodsFoodsFoodsCaloriesLabel.setFont(foodsFoodsFoodsCaloriesLabel.getFont().deriveFont(foodsFoodsFoodsCaloriesLabel.getFont().getSize() + 5f));
		foodsFoodsFoodsCaloriesLabel.setForeground(Color.white);
		foodsFoodsFoodsInfoPanel.add(foodsFoodsFoodsCaloriesLabel, "cell 2 0,alignx center,growx 0");

		//---- label24 ----
		label24.setText("Protein");
		label24.setHorizontalAlignment(SwingConstants.CENTER);
		label24.setForeground(UIManager.getColor("#545556"));
		label24.setFont(label24.getFont().deriveFont(label24.getFont().getStyle() | Font.BOLD, label24.getFont().getSize() + 5f));
		foodsFoodsFoodsInfoPanel.add(label24, "cell 1 1");

		//---- foodsFoodsFoodsProteinLabel ----
		foodsFoodsFoodsProteinLabel.setText("text");
		foodsFoodsFoodsProteinLabel.setFont(foodsFoodsFoodsProteinLabel.getFont().deriveFont(foodsFoodsFoodsProteinLabel.getFont().getSize() + 5f));
		foodsFoodsFoodsProteinLabel.setForeground(Color.white);
		foodsFoodsFoodsInfoPanel.add(foodsFoodsFoodsProteinLabel, "cell 2 1,alignx center,growx 0");

		//---- label25 ----
		label25.setText("Carbohydrates");
		label25.setHorizontalAlignment(SwingConstants.CENTER);
		label25.setForeground(UIManager.getColor("#545556"));
		label25.setFont(label25.getFont().deriveFont(label25.getFont().getStyle() | Font.BOLD, label25.getFont().getSize() + 5f));
		foodsFoodsFoodsInfoPanel.add(label25, "cell 1 2");

		//---- foodsFoodsFoodsCarbsLabel ----
		foodsFoodsFoodsCarbsLabel.setText("text");
		foodsFoodsFoodsCarbsLabel.setFont(foodsFoodsFoodsCarbsLabel.getFont().deriveFont(foodsFoodsFoodsCarbsLabel.getFont().getSize() + 5f));
		foodsFoodsFoodsCarbsLabel.setForeground(Color.white);
		foodsFoodsFoodsInfoPanel.add(foodsFoodsFoodsCarbsLabel, "cell 2 2,alignx center,growx 0");

		//---- label32 ----
		label32.setText("Fats");
		label32.setHorizontalAlignment(SwingConstants.CENTER);
		label32.setForeground(UIManager.getColor("#545556"));
		label32.setFont(label32.getFont().deriveFont(label32.getFont().getStyle() | Font.BOLD, label32.getFont().getSize() + 5f));
		foodsFoodsFoodsInfoPanel.add(label32, "cell 1 3");

		//---- foodsFoodsFoodsFatsLabel ----
		foodsFoodsFoodsFatsLabel.setText("text");
		foodsFoodsFoodsFatsLabel.setFont(foodsFoodsFoodsFatsLabel.getFont().deriveFont(foodsFoodsFoodsFatsLabel.getFont().getSize() + 5f));
		foodsFoodsFoodsFatsLabel.setForeground(Color.white);
		foodsFoodsFoodsInfoPanel.add(foodsFoodsFoodsFatsLabel, "cell 2 3,alignx center,growx 0");
	    }
	    foodsFoodsFoodsPanel.add(foodsFoodsFoodsInfoPanel, "cell 0 3");
	}

	//======== mealsFoodsFoodsPanel ========
	{
	    mealsFoodsFoodsPanel.setPreferredSize(new Dimension(988, 638));
	    mealsFoodsFoodsPanel.setLayout(new MigLayout(
		"fill,hidemode 3",
		// columns
		"[fill]",
		// rows
		"[37,top]" +
		"[]" +
		"[]" +
		"[grow,fill]"));

	    //======== mealsFoodsFoodsTopPanel ========
	    {
		mealsFoodsFoodsTopPanel.setBackground(new Color(0x1e2428));
		mealsFoodsFoodsTopPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[]"));

		//---- mealsFoodsFoodsTopBarBackButton ----
		mealsFoodsFoodsTopBarBackButton.setText("BACK");
		mealsFoodsFoodsTopBarBackButton.setForeground(Color.white);
		mealsFoodsFoodsTopBarBackButton.setFont(mealsFoodsFoodsTopBarBackButton.getFont().deriveFont(mealsFoodsFoodsTopBarBackButton.getFont().getStyle() | Font.BOLD));
		mealsFoodsFoodsTopBarBackButton.addActionListener(e -> mealsFoodsFoodsTopBarBack(e));
		mealsFoodsFoodsTopPanel.add(mealsFoodsFoodsTopBarBackButton, "cell 0 0,alignx left,growx 0");

		//---- mealsFoodsFoodsTopBarRecButton ----
		mealsFoodsFoodsTopBarRecButton.setText("RECORD FOOD");
		mealsFoodsFoodsTopBarRecButton.setForeground(Color.white);
		mealsFoodsFoodsTopBarRecButton.setFont(mealsFoodsFoodsTopBarRecButton.getFont().deriveFont(mealsFoodsFoodsTopBarRecButton.getFont().getStyle() | Font.BOLD));
		mealsFoodsFoodsTopBarRecButton.addActionListener(e -> mealsFoodsFoodsTopBarRec(e));
		mealsFoodsFoodsTopPanel.add(mealsFoodsFoodsTopBarRecButton, "cell 4 0");
	    }
	    mealsFoodsFoodsPanel.add(mealsFoodsFoodsTopPanel, "cell 0 0");

	    //---- mealsFoodsFoodsTitleLabel ----
	    mealsFoodsFoodsTitleLabel.setText("Food");
	    mealsFoodsFoodsTitleLabel.setFont(mealsFoodsFoodsTitleLabel.getFont().deriveFont(mealsFoodsFoodsTitleLabel.getFont().getStyle() | Font.BOLD, mealsFoodsFoodsTitleLabel.getFont().getSize() + 10f));
	    mealsFoodsFoodsTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    mealsFoodsFoodsTitleLabel.setForeground(Color.white);
	    mealsFoodsFoodsPanel.add(mealsFoodsFoodsTitleLabel, "cell 0 2");

	    //======== mealsFoodsFoodsInfoPanel ========
	    {
		mealsFoodsFoodsInfoPanel.setLayout(new MigLayout(
		    "fill,hidemode 3",
		    // columns
		    "[fill]" +
		    "[fill]" +
		    "[fill]" +
		    "[fill]",
		    // rows
		    "[center]" +
		    "[center]" +
		    "[center]" +
		    "[center]"));

		//---- label33 ----
		label33.setText("Calories");
		label33.setHorizontalAlignment(SwingConstants.CENTER);
		label33.setForeground(UIManager.getColor("#545556"));
		label33.setFont(label33.getFont().deriveFont(label33.getFont().getStyle() | Font.BOLD, label33.getFont().getSize() + 5f));
		mealsFoodsFoodsInfoPanel.add(label33, "cell 1 0");

		//---- mealsFoodsFoodsCaloriesLabel ----
		mealsFoodsFoodsCaloriesLabel.setText("text");
		mealsFoodsFoodsCaloriesLabel.setFont(mealsFoodsFoodsCaloriesLabel.getFont().deriveFont(mealsFoodsFoodsCaloriesLabel.getFont().getSize() + 5f));
		mealsFoodsFoodsCaloriesLabel.setForeground(Color.white);
		mealsFoodsFoodsInfoPanel.add(mealsFoodsFoodsCaloriesLabel, "cell 2 0,alignx center,growx 0");

		//---- label34 ----
		label34.setText("Protein");
		label34.setHorizontalAlignment(SwingConstants.CENTER);
		label34.setForeground(UIManager.getColor("#545556"));
		label34.setFont(label34.getFont().deriveFont(label34.getFont().getStyle() | Font.BOLD, label34.getFont().getSize() + 5f));
		mealsFoodsFoodsInfoPanel.add(label34, "cell 1 1");

		//---- mealsFoodsFoodsProteinLabel ----
		mealsFoodsFoodsProteinLabel.setText("text");
		mealsFoodsFoodsProteinLabel.setFont(mealsFoodsFoodsProteinLabel.getFont().deriveFont(mealsFoodsFoodsProteinLabel.getFont().getSize() + 5f));
		mealsFoodsFoodsProteinLabel.setForeground(Color.white);
		mealsFoodsFoodsInfoPanel.add(mealsFoodsFoodsProteinLabel, "cell 2 1,alignx center,growx 0");

		//---- label35 ----
		label35.setText("Carbohydrates");
		label35.setHorizontalAlignment(SwingConstants.CENTER);
		label35.setForeground(UIManager.getColor("#545556"));
		label35.setFont(label35.getFont().deriveFont(label35.getFont().getStyle() | Font.BOLD, label35.getFont().getSize() + 5f));
		mealsFoodsFoodsInfoPanel.add(label35, "cell 1 2");

		//---- mealsFoodsFoodsCarbsLabel ----
		mealsFoodsFoodsCarbsLabel.setText("text");
		mealsFoodsFoodsCarbsLabel.setFont(mealsFoodsFoodsCarbsLabel.getFont().deriveFont(mealsFoodsFoodsCarbsLabel.getFont().getSize() + 5f));
		mealsFoodsFoodsCarbsLabel.setForeground(Color.white);
		mealsFoodsFoodsInfoPanel.add(mealsFoodsFoodsCarbsLabel, "cell 2 2,alignx center,growx 0");

		//---- label36 ----
		label36.setText("Fats");
		label36.setHorizontalAlignment(SwingConstants.CENTER);
		label36.setForeground(UIManager.getColor("#545556"));
		label36.setFont(label36.getFont().deriveFont(label36.getFont().getStyle() | Font.BOLD, label36.getFont().getSize() + 5f));
		mealsFoodsFoodsInfoPanel.add(label36, "cell 1 3");

		//---- mealsFoodsFoodsFatsLabel ----
		mealsFoodsFoodsFatsLabel.setText("text");
		mealsFoodsFoodsFatsLabel.setFont(mealsFoodsFoodsFatsLabel.getFont().deriveFont(mealsFoodsFoodsFatsLabel.getFont().getSize() + 5f));
		mealsFoodsFoodsFatsLabel.setForeground(Color.white);
		mealsFoodsFoodsInfoPanel.add(mealsFoodsFoodsFatsLabel, "cell 2 3,alignx center,growx 0");
	    }
	    mealsFoodsFoodsPanel.add(mealsFoodsFoodsInfoPanel, "cell 0 3");
	}

	//---- buttonGroup1 ----
	var buttonGroup1 = new ButtonGroup();
	buttonGroup1.add(exerciseCustomWeightRButton);
	buttonGroup1.add(exerciseCustomDistanceRButton);

	//---- buttonGroup2 ----
	var buttonGroup2 = new ButtonGroup();
	buttonGroup2.add(exerciseCustomTricepsRButton);
	buttonGroup2.add(exerciseCustomChestRButton);
	buttonGroup2.add(exerciseCustomShouldersRButton);
	buttonGroup2.add(exerciseCustomBicepsRButton);
	buttonGroup2.add(exerciseCustomCoreRButton);
	buttonGroup2.add(exerciseCustomBackRButton);
	buttonGroup2.add(exerciseCustomForearmsRButton);
	buttonGroup2.add(exerciseCustomUpperLegsRButton);
	buttonGroup2.add(exerciseCustomGlutesRButton);
	buttonGroup2.add(exerciseCustomCardioRButton);
	buttonGroup2.add(exerciseCustomLowerLegsRButton);
	// JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    // Generated using JFormDesigner Educational license - Thomas Scardino (THOMAS A SCARDINO)
    private JFrame window;
    private JButton mainWindowExitButton;
    private JPanel startPanel;
    private JLabel startLogoLabel;
    private JButton startLoginButton;
    private JButton startRegisterButton;
    private JButton mainWindowLogoutButton;
    private JPanel loginPanel;
    private JLabel loginLogoLabel;
    private JLabel loginUsernameLabel;
    private JLabel loginPasswordLabel;
    private JTextField loginUsernameField;
    private JButton loginLoginButton;
    private JButton loginBackButton;
    private JPasswordField loginPasswordField;
    private JPanel registerPanel;
    private JLabel registerLogoLabel;
    private JLabel registerUsernameLabel;
    private JLabel registerEmailLabel;
    private JTextField registerUsernameField;
    private JTextField registerEmailField;
    private JButton registerRegisterButton;
    private JButton registerBackButton;
    private JLabel registerFNameLabel;
    private JTextField registerFNameField;
    private JTextField registerLNameField;
    private JLabel registerLNameLabel;
    private JLabel registerPasswordLabel;
    private JPasswordField registerPasswordField;
    private JPanel mainMenuPanel;
    private JPanel mainMenuButtonPanel;
    private JButton mainMenuLogoButton;
    private JButton mainMenuExercisesButton;
    private JButton mainMenuWorkoutsButton;
    private JButton mainMenuFoodsButton;
    private JButton mainMenuMealsButton;
    private JButton mainMenuProgressButton;
    private JButton mainMenuProfileButton;
    private JPanel mainMenuStartPanel;
    private JLabel startWelcomeLabel;
    private JLabel label14;
    private JPanel mainMenuExercisesPanel;
    private JButton exercisesTricepsButton;
    private JButton exercisesChestButton;
    private JButton exercisesShouldersButton;
    private JButton exercisesBicepsButton;
    private JButton exercisesCoreButton;
    private JButton exercisesBackButton;
    private JButton exercisesForearmsButton;
    private JButton exercisesUpperLegsButton;
    private JButton exercisesGlutesButton;
    private JButton exercisesCardioButton;
    private JButton exercisesLowerLegsButton;
    private JButton exercisesAddNewButton;
    private JPanel mainMenuWorkoutsPanel;
    private JButton workoutSelectButton;
    private JButton workoutAddNewButton;
    private JPanel mainMenuFoodsPanel;
    private JButton foodSelectButton;
    private JButton foodAddNewButton;
    private JPanel mainMenuMealsPanel;
    private JButton mealSelectButton;
    private JButton mealAddNewButton;
    private JPanel mainMenuProgressPanel;
    private JPanel exercisesExercisesPanel;
    private JPanel exercisesExercisesTopPanel;
    private JButton exercisesExercisesTopBarBackButton;
    private JButton exercisesExercisesTopBarAllButton;
    private JButton exercisesExercisesTopBarFTButton;
    private JButton exercisesExercisesTopBarCusButton;
    private JScrollPane exercisesExercisesScrollPanel;
    private JPanel exercisesWeightPanel;
    private JPanel exercisesWeightTopPanel;
    private JButton exercisesWeightTopBarBackButton;
    private JButton exercisesWeightTopBarAddButton;
    private JButton exercisesWeightTopBarRemButton;
    private JButton exercisesWeightTopBarRecButton;
    private JLabel exercisesWeightTitleLabel;
    private JPanel weightSetsPanel;
    private JLabel label17;
    private JLabel label18;
    private JLabel label19;
    private JLabel weightSet1Label;
    private JTextField weightSet1WeightField;
    private JLabel weightSet1XLabel;
    private JTextField weightSet1RepsField;
    private JPanel exercisesDistancePanel;
    private JPanel exercisesDistanceTopPanel;
    private JButton exercisesDistanceTopBarBackButton;
    private JButton exercisesDistanceTopBarAddButton;
    private JButton exercisesDistanceTopBarRemButton;
    private JButton exercisesDistanceTopBarRecButton;
    private JLabel exercisesDistanceTitleLabel;
    private JPanel distanceSetsPanel;
    private JLabel label26;
    private JLabel label27;
    private JLabel label28;
    private JLabel distanceSet1Label;
    private JTextField distanceSet1DistanceField;
    private JTextField distanceSet1DurationField;
    private JPanel exercisesCustomPanel;
    private JPanel exercisesExercisesTopPanel2;
    private JButton exercisesCustomTopBarBackButton;
    private JButton exercisesCustomTopBarAddButton;
    private JPanel customFieldsPanel;
    private JLabel label1;
    private JTextField exerciseCustomNameField;
    private JLabel label2;
    private JRadioButton exerciseCustomWeightRButton;
    private JRadioButton exerciseCustomDistanceRButton;
    private JLabel label3;
    private JRadioButton exerciseCustomTricepsRButton;
    private JRadioButton exerciseCustomChestRButton;
    private JRadioButton exerciseCustomShouldersRButton;
    private JRadioButton exerciseCustomBicepsRButton;
    private JRadioButton exerciseCustomCoreRButton;
    private JRadioButton exerciseCustomBackRButton;
    private JRadioButton exerciseCustomForearmsRButton;
    private JRadioButton exerciseCustomUpperLegsRButton;
    private JRadioButton exerciseCustomGlutesRButton;
    private JRadioButton exerciseCustomCardioRButton;
    private JRadioButton exerciseCustomLowerLegsRButton;
    private JPanel workoutsWorkoutsPanel;
    private JPanel workoutsWorkoutsTopPanel;
    private JButton workoutsWorkoutsTopBarBackButton;
    private JButton workoutsWorkoutsTopBarAllButton;
    private JButton workoutsWorkoutsTopBarFTButton;
    private JButton workoutsWorkoutsTopBarCusButton;
    private JScrollPane workoutsWorkoutsScrollPanel;
    private JPanel workoutsExercisesPanel;
    private JPanel workoutsExercisesTopPanel;
    private JButton workoutsExercisesTopBarBackButton;
    private JLabel workoutsWorkoutTitleLabel;
    private JScrollPane workoutsExercisesScrollPanel;
    private JPanel foodsFoodsPanel;
    private JPanel foodsFoodsTopPanel;
    private JButton foodsFoodsTopBarBackButton;
    private JScrollPane foodsFoodsScrollPanel;
    private JPanel mealsMealsPanel;
    private JPanel mealsMealsTopPanel;
    private JButton mealsMealsTopBarBackButton;
    private JButton mealsMealsTopBarAllButton;
    private JButton mealsMealsTopBarFTButton;
    private JButton mealsMealsTopBarCusButton;
    private JScrollPane mealsMealsScrollPanel;
    private JPanel mealsFoodsPanel;
    private JPanel mealsFoodsTopPanel;
    private JButton mealsFoodsTopBarBackButton;
    private JLabel mealsMealTitleLabel;
    private JScrollPane mealsFoodsScrollPanel;
    private JPanel workoutsExerciseWeightPanel;
    private JPanel workoutsExerciseWeightTopPanel;
    private JButton workoutsExerciseWeightTopBarBackButton;
    private JButton workoutsExerciseWeightTopBarAddButton;
    private JButton workoutsExerciseWeightTopBarRemButton;
    private JButton workoutsExerciseWeightTopBarRecButton;
    private JLabel workoutsExerciseWeightTitleLabel;
    private JPanel workoutsExerciseWeightSetsPanel;
    private JLabel label20;
    private JLabel label21;
    private JLabel label22;
    private JLabel workoutsExerciseWeightSet1Label;
    private JTextField workoutsExerciseWeightSet1WeightField;
    private JLabel workoutsExerciseWeightSet1XLabel;
    private JTextField workoutsExerciseWeightSet1RepsField;
    private JPanel workoutsExerciseDistancePanel;
    private JPanel exercisesDistanceTopPanel2;
    private JButton workoutsExerciseDistanceTopBarBackButton;
    private JButton workoutsExerciseDistanceTopBarAddButton;
    private JButton workoutsExerciseDistanceTopBarRemButton;
    private JButton workoutsExerciseDistanceTopBarRecButton;
    private JLabel workoutsExerciseDistanceTitleLabel;
    private JPanel workoutsExerciseDistanceSetsPanel;
    private JLabel label29;
    private JLabel label30;
    private JLabel label31;
    private JLabel workoutsExerciseDistanceSet1Label;
    private JTextField workoutsExerciseDistanceSet1DistanceField;
    private JTextField workoutsExerciseDistanceSet1DurationField;
    private JPanel mainMenuProfilePanel;
    private JPanel profileFieldsPanel;
    private JLabel label4;
    private JLabel profileFieldsUsernameLabel;
    private JLabel label5;
    private JTextField profileFieldsEmailField;
    private JButton profileFieldsEditEmailButton;
    private JButton profileFieldsSaveEmailButton;
    private JLabel label6;
    private JTextField profileFieldsFNameField;
    private JButton profileFieldsEditFNameButton;
    private JButton profileFieldsSaveFNameButton;
    private JLabel label7;
    private JTextField profileFieldsLNameField;
    private JButton profileFieldsEditLNameButton;
    private JButton profileFieldsSaveLNameButton;
    private JLabel label8;
    private JPasswordField profileFieldsPasswordField;
    private JToggleButton profileFieldsPasswordShowTButton;
    private JButton profileFieldsEditPasswordButton;
    private JButton profileFieldsSavePasswordButton;
    private JLabel profileFieldPasswordConfirmLabel;
    private JPasswordField profileFieldsPasswordConfirmField;
    private JPanel foodsFoodsFoodsPanel;
    private JPanel foodsFoodsFoodsTopPanel;
    private JButton foodsFoodsFoodsTopBarBackButton;
    private JButton foodsFoodsFoodsTopBarRecButton;
    private JLabel foodsFoodsFoodsTitleLabel;
    private JPanel foodsFoodsFoodsInfoPanel;
    private JLabel label23;
    private JLabel foodsFoodsFoodsCaloriesLabel;
    private JLabel label24;
    private JLabel foodsFoodsFoodsProteinLabel;
    private JLabel label25;
    private JLabel foodsFoodsFoodsCarbsLabel;
    private JLabel label32;
    private JLabel foodsFoodsFoodsFatsLabel;
    private JPanel mealsFoodsFoodsPanel;
    private JPanel mealsFoodsFoodsTopPanel;
    private JButton mealsFoodsFoodsTopBarBackButton;
    private JButton mealsFoodsFoodsTopBarRecButton;
    private JLabel mealsFoodsFoodsTitleLabel;
    private JPanel mealsFoodsFoodsInfoPanel;
    private JLabel label33;
    private JLabel mealsFoodsFoodsCaloriesLabel;
    private JLabel label34;
    private JLabel mealsFoodsFoodsProteinLabel;
    private JLabel label35;
    private JLabel mealsFoodsFoodsCarbsLabel;
    private JLabel label36;
    private JLabel mealsFoodsFoodsFatsLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
