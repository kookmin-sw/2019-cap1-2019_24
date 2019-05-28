package com.example.WITTYPHOTOS;

import android.app.Application;


public class App extends Application {

    public static DBHelper mDB;

    @Override
    public void onCreate() {
        super.onCreate();
        mDB = new DBHelper(this);


    }

}
