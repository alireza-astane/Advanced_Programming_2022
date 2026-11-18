package logic.pageDataClasses;

import logic.enums.ResponseType;


import network.Response;


import java.util.ArrayList;
import java.util.Objects;


public class MohseniStudentSearchData extends Response {
    ArrayList<ArrayList<String>> searchResult;


    //arrayList: id  entryYear name studentShipNumber grade


    public ArrayList<ArrayList<String>> getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(ArrayList<ArrayList<String>> searchResult) {
        this.searchResult = searchResult;
    }
}
