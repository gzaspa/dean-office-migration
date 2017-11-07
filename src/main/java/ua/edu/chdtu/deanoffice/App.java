package ua.edu.chdtu.deanoffice;

import static ua.edu.chdtu.deanoffice.DatabaseConnector.configDatabases;
import static ua.edu.chdtu.deanoffice.DatabaseConnector.finishWorking;
import static ua.edu.chdtu.deanoffice.Migration.migrate;


public class App {

    public static void main(String[] args) {
        configDatabases();
        try {
            migrate();
            System.out.println("Migration finished successful!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Migration error!");
        } finally {
            finishWorking();
        }
    }
}
