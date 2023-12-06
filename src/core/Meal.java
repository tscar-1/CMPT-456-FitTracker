package core;

import java.time.LocalDate;

public class Meal {

    private LocalDate date;
    private String mainMeal;
    private String side;

    private String drink;
    private int Calories;



    public Meal(LocalDate date, String mainMeal, String side, String drink, int calories) {
        this.date = date;
        this.mainMeal = mainMeal;
        this.side = side;
        this.drink = drink;
        Calories = calories;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMainMeal() {
        return mainMeal;
    }

    public void setMainMeal(String mainMeal) {
        this.mainMeal = mainMeal;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getDrink() {
        return drink;
    }

    public void setDrink(String drink) {
        this.drink = drink;
    }

    public int getCalories() {
        return Calories;
    }

    public void setCalories(int calories) {
        Calories = calories;
    }



}
