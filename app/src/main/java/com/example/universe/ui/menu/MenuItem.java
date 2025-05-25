package com.example.universe.ui.menu;

public class MenuItem {
    private final String name;
    private final int calories;

    public MenuItem(String name, int calories) {
        this.name = name;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }
}
