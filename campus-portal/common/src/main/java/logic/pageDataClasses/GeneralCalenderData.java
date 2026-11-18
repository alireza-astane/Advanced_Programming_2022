package logic.pageDataClasses;

import logic.enums.ResponseType;

import network.Response;


import java.util.ArrayList;
import java.util.Arrays;

public class GeneralCalenderData extends Response {
    ArrayList<ArrayList<String>> arrayLists;


    public ArrayList<ArrayList<String>> getArrayLists() {
        return arrayLists;
    }

    public void setArrayLists(ArrayList<ArrayList<String>> arrayLists) {
        this.arrayLists = arrayLists;
    }
}
