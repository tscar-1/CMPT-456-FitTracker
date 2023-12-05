package gui;

import java.beans.*;
import javax.swing.border.*;
import com.jgoodies.forms.factories.*;
import core.*;
import database.*;
import utils.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
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

    public MainWindow() {
        initComponents();
    }

    public void show() {
        window.pack();
        window.setVisible(true);
    }

    private void createUIComponents() {
        // TODO: add custom component creation code here
    }

    private void logout(ActionEvent e) {

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
        notificationShow("Click", "Actions.Red");
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
        
        /*if (loggedInUser == user && loggedInUser.getPassword().equals(password)) {
            
        }*/
        
        mainWindowLogoutButton.setVisible(true);
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
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center label horizontally

        // Custom JPanel with rounded corners
        JPanel notificationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(getBackground());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25); // 25 is the arc width and height
            }
        };
        notificationPanel.setOpaque(false); // Make JPanel non-opaque to allow custom painting
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.PAGE_AXIS)); // Use BoxLayout for vertical alignment
        notificationPanel.add(Box.createVerticalGlue()); // Glue at the top for spacing
        notificationPanel.add(messageLabel); // Add label, which will be centered vertically
        notificationPanel.add(Box.createVerticalGlue()); // Glue at the bottom for spacing

        notificationPanel.setBackground(UIManager.getColor(color));

        int panelWidth = window.getWidth() / 5; // Fifth the width of the window
        int panelHeight = 80; // Adjust the height as needed
        int xPosStart = (window.getWidth() - panelWidth) / 2; // Horizontally centered
        notificationPanel.setBounds(xPosStart, window.getHeight(), panelWidth, panelHeight);
        window.getContentPane().add(notificationPanel);
        window.getContentPane().setComponentZOrder(notificationPanel, 0);
        window.getContentPane().repaint();

        javax.swing.Timer slideUpTimer = new javax.swing.Timer(5, new ActionListener() {
            int yPos = window.getHeight();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (yPos > window.getHeight() - panelHeight) {
                    yPos -= 1; // Slide-up speed
                    notificationPanel.setBounds(xPosStart, yPos, panelWidth, panelHeight);
                } else {
                    ((javax.swing.Timer) e.getSource()).stop();
                    // Delay before slide-down
                    new javax.swing.Timer(1000, ev -> {
                        javax.swing.Timer slideDownTimer = new javax.swing.Timer(5, new ActionListener() {
                            int yPosDown = window.getHeight() - panelHeight;

                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                if (yPosDown < window.getHeight()) {
                                    yPosDown += 1; // Slide-down speed
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

	//======== window ========
	{
	    window.setName("frame1");
	    window.setPreferredSize(new Dimension(1000, 900));
	    window.setTitle("FitTracker");
	    window.setVisible(true);
	    window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
	    mainMenuPanel.setPreferredSize(new Dimension(1000, 750));
	    mainMenuPanel.setVisible(false);

	    GroupLayout mainMenuPanelLayout = new GroupLayout(mainMenuPanel);
	    mainMenuPanel.setLayout(mainMenuPanelLayout);
	    mainMenuPanelLayout.setHorizontalGroup(
		mainMenuPanelLayout.createParallelGroup()
		    .addGap(0, 1000, Short.MAX_VALUE)
	    );
	    mainMenuPanelLayout.setVerticalGroup(
		mainMenuPanelLayout.createParallelGroup()
		    .addGap(0, 750, Short.MAX_VALUE)
	    );
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
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
