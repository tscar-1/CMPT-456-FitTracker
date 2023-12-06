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
import com.google.gson.reflect.TypeToken;
import com.intellij.uiDesigner.core.*;
import net.miginfocom.swing.*;

public class MainWindow {

    private static User currentUser = new User();
    private int currentMuscleType;
    private Exercise currentExercise;
    private Workout currentWorkout;
    private int weightSetRowCount = 1;
    private int distanceSetRowCount = 1;

    public MainWindow() {
        initComponents();
    }

    public void show() {
        window.pack();
        window.setVisible(true);
    }

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
            window.getContentPane().remove(loginPanel);
            mainMenuPanel.setBounds(new Rectangle(new Point(0, 0), mainMenuPanel.getPreferredSize()));
            window.getContentPane().add(mainMenuPanel);
            mainMenuPanel.setVisible(true);
            window.revalidate();
            window.repaint();
        }
        
    }

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
            mainMenuPanel.add(exercisesDistancePanel, BorderLayout.CENTER);
            mainMenuPanel.revalidate();
            mainMenuPanel.repaint();
        }
    }
    
    private void exercisesExercisesTopBarAll(ActionEvent e) {
	exercisesPopulate(1, currentMuscleType);
    }
    
    private void exercisesExercisesTopBarFT(ActionEvent e) {
	exercisesPopulate(2, currentMuscleType);
    }

    private void exercisesExercisesTopBarMy(ActionEvent e) {
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

        for (int i = 1; i <= weightSetRowCount; i++) {
            LocalDate date = LocalDate.now();
            String exerciseName = exercise.getName();
            int exerciseRecordType = exercise.getRecordType();
            int exerciseMuscleType = exercise.getMuscleType();
            int exerciseSetNum = i;
            int exerciseWeightAmt = 0;
            int exerciseRepAmt = 0;

            String[] componentsToRecord = {
                "weightSet" + i + "WeightField",
                "weightSet" + i + "RepsField"
            };

            for (String name : componentsToRecord) {
                for (Component comp : weightSetsPanel.getComponents()) {
                    if (name.equals(comp.getName()) && comp instanceof JTextField) {
                        JTextField textField = (JTextField) comp;
                        try {
                            if (name.contains("WeightField")) {
                                exerciseWeightAmt = Integer.parseInt(textField.getText());
                            }
                            else if (name.contains("RepsField")) {
                                exerciseRepAmt = Integer.parseInt(textField.getText());
                            }
                        } catch (NumberFormatException ex) {
                            notificationShow("Error", "Button.focusedBorderColor");
                        }
                    }
                }
            }
            
            currentUser.saveProgressExerciseWeight(date, exerciseName, exerciseRecordType, exerciseMuscleType, exerciseSetNum, exerciseWeightAmt, exerciseRepAmt);
            notificationShow("Exercise Recorded", "Actions.Red");
        }
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
        durationField.setName("weightSet" + distanceSetRowCount + "RepsField");
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

        for (int i = 1; i <= distanceSetRowCount; i++) {
            LocalDate date = LocalDate.now();
            String exerciseName = exercise.getName();
            int exerciseRecordType = exercise.getRecordType();
            int exerciseMuscleType = exercise.getMuscleType();
            int exerciseSetNum = i;
            double exerciseDistanceAmt = 0.0;
            double exerciseDurationLen = 0.0;

            String[] componentsToRecord = {
                "distanceSet" + i + "DistanceField",
                "distanceSet" + i + "DurationField"
            };

            for (String name : componentsToRecord) {
                for (Component comp : distanceSetsPanel.getComponents()) {
                    if (name.equals(comp.getName()) && comp instanceof JTextField) {
                        JTextField textField = (JTextField) comp;
                        try {
                            if (name.contains("DistanceField")) {
                                exerciseDistanceAmt = Double.parseDouble(textField.getText());
                            }
                            else if (name.contains("DurationField")) {
                                exerciseDurationLen = Double.parseDouble(textField.getText());
                            }
                        } catch (NumberFormatException ex) {
                            notificationShow("Error", "Button.focusedBorderColor");
                        }
                    }
                }
            }
            
            currentUser.saveProgressExerciseDistance(date, exerciseName, exerciseRecordType, exerciseMuscleType, exerciseSetNum, exerciseDistanceAmt, exerciseDurationLen);
            notificationShow("Exercise Recorded", "Actions.Red");
        }
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
	mainMenuProfilePanel = new JPanel();
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
	textField1 = new JTextField();
	label2 = new JLabel();
	toggleButton1 = new JToggleButton();
	toggleButton2 = new JToggleButton();
	label3 = new JLabel();
	toggleButton3 = new JToggleButton();
	toggleButton4 = new JToggleButton();
	toggleButton5 = new JToggleButton();
	toggleButton6 = new JToggleButton();
	toggleButton7 = new JToggleButton();
	toggleButton8 = new JToggleButton();
	toggleButton9 = new JToggleButton();
	toggleButton10 = new JToggleButton();
	toggleButton11 = new JToggleButton();
	toggleButton12 = new JToggleButton();
	toggleButton13 = new JToggleButton();

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
	    window.setIconImage(new ImageIcon("D:\\Documents\\NetBeansProjects\\FitTracker\\src\\assets\\FitTrackerLogoSmall.png").getImage());
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
		startLogoLabel.setIcon(new ImageIcon("D:\\Documents\\NetBeansProjects\\FitTracker\\src\\assets\\FitTrackerLogo.png"));
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
	    loginLogoLabel.setIcon(new ImageIcon("D:\\Documents\\NetBeansProjects\\FitTracker\\src\\assets\\FitTrackerLogo.png"));
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
	    registerLogoLabel.setIcon(new ImageIcon("D:\\Documents\\NetBeansProjects\\FitTracker\\src\\assets\\FitTrackerLogo.png"));
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
		mainMenuLogoButton.setIcon(new ImageIcon("D:\\Documents\\NetBeansProjects\\FitTracker\\src\\assets\\FitTrackerLogoSmall.png"));
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

	//======== mainMenuProfilePanel ========
	{
	    mainMenuProfilePanel.setPreferredSize(new Dimension(988, 638));
	    mainMenuProfilePanel.setLayout(new MigLayout(
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
		exercisesExercisesTopBarCusButton.addActionListener(e -> exercisesExercisesTopBarMy(e));
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
		exercisesWeightTopBarBackButton.addActionListener(e -> {
			exercisesExercisesTopBarAll(e);
			exercisesWeightTopBarBack(e);
		});
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
		exercisesDistanceTopBarBackButton.addActionListener(e -> {
			exercisesExercisesTopBarAll(e);
			exercisesDistanceTopBarBack(e);
		});
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
		customFieldsPanel.add(textField1, "cell 1 0 4 1");

		//---- label2 ----
		label2.setText("Record Type:");
		label2.setFont(label2.getFont().deriveFont(label2.getFont().getStyle() | Font.BOLD, label2.getFont().getSize() + 2f));
		label2.setForeground(Color.white);
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		customFieldsPanel.add(label2, "cell 0 1");

		//---- toggleButton1 ----
		toggleButton1.setText("Weight-based");
		customFieldsPanel.add(toggleButton1, "cell 1 1 2 1");

		//---- toggleButton2 ----
		toggleButton2.setText("Distance-based");
		customFieldsPanel.add(toggleButton2, "cell 3 1 2 1");

		//---- label3 ----
		label3.setText("Muscle Group:");
		label3.setFont(label3.getFont().deriveFont(label3.getFont().getStyle() | Font.BOLD, label3.getFont().getSize() + 2f));
		label3.setForeground(Color.white);
		label3.setHorizontalAlignment(SwingConstants.CENTER);
		customFieldsPanel.add(label3, "cell 0 2");

		//---- toggleButton3 ----
		toggleButton3.setText("Triceps");
		customFieldsPanel.add(toggleButton3, "cell 1 2");

		//---- toggleButton4 ----
		toggleButton4.setText("Chest");
		customFieldsPanel.add(toggleButton4, "cell 2 2");

		//---- toggleButton5 ----
		toggleButton5.setText("Shoulders");
		customFieldsPanel.add(toggleButton5, "cell 3 2");

		//---- toggleButton6 ----
		toggleButton6.setText("Biceps");
		customFieldsPanel.add(toggleButton6, "cell 4 2");

		//---- toggleButton7 ----
		toggleButton7.setText("Core");
		customFieldsPanel.add(toggleButton7, "cell 1 3");

		//---- toggleButton8 ----
		toggleButton8.setText("Back");
		customFieldsPanel.add(toggleButton8, "cell 2 3");

		//---- toggleButton9 ----
		toggleButton9.setText("Forearms");
		customFieldsPanel.add(toggleButton9, "cell 3 3");

		//---- toggleButton10 ----
		toggleButton10.setText("Upper Legs");
		customFieldsPanel.add(toggleButton10, "cell 4 3");

		//---- toggleButton11 ----
		toggleButton11.setText("Glutes");
		customFieldsPanel.add(toggleButton11, "cell 1 4");

		//---- toggleButton12 ----
		toggleButton12.setText("Cardio");
		customFieldsPanel.add(toggleButton12, "cell 2 4");

		//---- toggleButton13 ----
		toggleButton13.setText("Lower Legs");
		customFieldsPanel.add(toggleButton13, "cell 3 4");
	    }
	    exercisesCustomPanel.add(customFieldsPanel, "cell 0 1");
	}
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
    private JPanel mainMenuProfilePanel;
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
    private JTextField textField1;
    private JLabel label2;
    private JToggleButton toggleButton1;
    private JToggleButton toggleButton2;
    private JLabel label3;
    private JToggleButton toggleButton3;
    private JToggleButton toggleButton4;
    private JToggleButton toggleButton5;
    private JToggleButton toggleButton6;
    private JToggleButton toggleButton7;
    private JToggleButton toggleButton8;
    private JToggleButton toggleButton9;
    private JToggleButton toggleButton10;
    private JToggleButton toggleButton11;
    private JToggleButton toggleButton12;
    private JToggleButton toggleButton13;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
