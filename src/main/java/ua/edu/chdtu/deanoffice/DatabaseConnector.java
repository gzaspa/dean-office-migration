package ua.edu.chdtu.deanoffice;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseConnector {

    private static SessionFactory firebirdSessionFactory;
    private static SessionFactory postgresSessionFactory;
    private static Session firebirdSession;
    private static Session postgresSession;

    private static void configFirebird() {
        Configuration config = new Configuration();
        config.configure("META-INF/hibernate-firebird.cfg.xml");
        firebirdSessionFactory = config.buildSessionFactory();
        firebirdSession = firebirdSessionFactory.openSession();
    }

    private static void configPostgres() {
        Configuration config = new Configuration();
        config.configure("META-INF/hibernate-postgres.cfg.xml");
        postgresSessionFactory = config.buildSessionFactory();
        postgresSession = postgresSessionFactory.openSession();
    }

    public static void configDatabases() {
        configFirebird();
        configPostgres();
    }

    private static SessionFactory getFirebirdSessionFactory() {
        if (firebirdSessionFactory == null) {
            configFirebird();
        }
        return firebirdSessionFactory;
    }

    private static SessionFactory getPostgresSessionFactory() {
        if (postgresSessionFactory == null) {
            configPostgres();
        }
        return postgresSessionFactory;
    }

    public static Session getFirebirdSession() {
        if (firebirdSession == null) {
            firebirdSession = getFirebirdSessionFactory().openSession();
        }
        return firebirdSession;
    }

    public static Session getPostgresSession() {
        if (postgresSession == null) {
            postgresSession = getPostgresSessionFactory().openSession();
        }
        return postgresSession;
    }

    private static void closeSessions() {
        getFirebirdSession().close();
        getPostgresSession().close();
    }

    private static void closeFactories() {
        getFirebirdSessionFactory().close();
        getPostgresSessionFactory().close();
    }

    public static void finishWorking() {
        closeSessions();
        closeFactories();
    }
}
