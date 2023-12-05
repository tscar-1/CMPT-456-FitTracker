package database;

import core.*;
import utils.*;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
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
    
    public User(String Username, String email, String fName, String lName, String password) {
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
    
    public void setFName(String fName) {
        this.fName = fName;
    }
    
    public String getFName() {
        return fName;
    }
    
    public void setLName(String lName) {
        this.lName = lName;
    }
    
    public void setPassword(String password) {
        this.password = StringUtil.applySha256(password);
    }
    
    public String getPassword() {
        return this.password;
    }
}
