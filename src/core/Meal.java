package core;

import java.time.LocalDate;
import java.util.List;

public class Meal {


    private String name;

    public Meal(String name, List<Food> meal) {
        this.name = name;
        this.meal = meal;
    }

    private List<Food> meal;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Food> getMeal() {
        return meal;
    }

    public void setMeal(List<Food> meal) {
        this.meal = meal;
    }


}
