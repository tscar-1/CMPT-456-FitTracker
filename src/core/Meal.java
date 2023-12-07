package core;

import java.util.*;

public class Meal {
    private String name;
    private List<Food> foods;

    public Meal(String name) {
        this.name = name;
        this.foods = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void addFood(Food food) {
        foods.add(food);
    }
    
    public List<Food> getFoods() {
        return foods;
    }
}
