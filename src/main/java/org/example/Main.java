package org.example;

import org.example.DataBase.DataBaseService;
import org.example.DataBase.DataBase_Module;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        DataBaseService DBS = new DataBaseService(new DataBase_Module());
        DBS.displayTable(DBS.retrieveAllData("User"));
    }
}