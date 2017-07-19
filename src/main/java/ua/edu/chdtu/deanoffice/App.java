package ua.edu.chdtu.deanoffice;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ){
        Configuration config = new Configuration();
        config.configure("META-INF/hibernate-postgres.cfg.xml");
        SessionFactory sessionFactory = config.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.close();
        sessionFactory.close();


//        Degree degree = new Degree();
//        degree.setId(12);
//        degree.setName("jkhjkh");
//        System.out.println( "Hello World! "+degree.getName() );
    }
}
