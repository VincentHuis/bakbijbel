package com.bakbijbel.bakbijbel;

public class User {
    public String accesToken;
    public String name;

    public User(String accesToken, String name) {
            this.accesToken = accesToken;
            this.name = name;
    }

    public String[] getArray()
    {
        String[] t = {accesToken, name};
        return t;
    }
}
