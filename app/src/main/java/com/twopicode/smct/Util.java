package com.twopicode.smct;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

/*************************************
 * Created by Mikel-LAPTOP on 4/12/2015.
 *************************************/
public class Util {

    public static String toJson(ArrayList<String> stringArrayList) {
        return new GsonBuilder().create().toJson(new ArrayListStringWrapper(stringArrayList));
    }

    public static ArrayList<String> fromJson(String json) {
        return json != null ? new Gson().fromJson(json, ArrayListStringWrapper.class).getObjects()
                : null;
    }

    public static class ArrayListStringWrapper {
        private ArrayList<String> objects;
        public ArrayListStringWrapper(ArrayList<String> objects) {
            this.objects = objects;
        }
        public ArrayList<String> getObjects() {
            return objects;
        }
    }

}
