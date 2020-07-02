package com.bakbijbel.bakbijbel;

public class Recept {
    public String pictureURL;
    public String title;
    public String description;
    public String ingredients;
    public int preparationTime;
    public int author; //voor aanpassen

    public Recept(String pictureURL, String title, String description, String ingredients, int preparationTime) {
        this.pictureURL = pictureURL;
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.preparationTime = preparationTime;
    }

    public String[] getArray()
    {
        String[] t = {title, description, ingredients, String.valueOf(preparationTime), pictureURL};
        return t;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }
}
