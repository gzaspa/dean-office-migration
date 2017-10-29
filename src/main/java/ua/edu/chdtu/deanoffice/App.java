package ua.edu.chdtu.deanoffice;

import static ua.edu.chdtu.deanoffice.DatabaseConnector.configDatabases;
import static ua.edu.chdtu.deanoffice.DatabaseConnector.finishWorking;
import static ua.edu.chdtu.deanoffice.Migration.migrate;


public class App {
    public static void main(String[] args) {
        configDatabases();
        try {
            migrate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            finishWorking();
        }
    }
}
