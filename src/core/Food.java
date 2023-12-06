package core;

import java.time.LocalDate;

public class Food {
    private LocalDate date;

    private String name;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return Calories;
    }

    public void setCalories(int calories) {
        Calories = calories;
    }

    private int Calories;
    public Food(LocalDate date, String name, int calories) {
        this.date = date;
        this.name = name;
        Calories = calories;
    }

}
