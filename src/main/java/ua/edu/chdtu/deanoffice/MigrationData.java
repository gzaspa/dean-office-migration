package ua.edu.chdtu.deanoffice;

import com.sun.istack.internal.NotNull;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.CourseName;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.oldentity.AcademicVacation;
import ua.edu.chdtu.deanoffice.oldentity.Cathedra;
import ua.edu.chdtu.deanoffice.oldentity.Department;
import ua.edu.chdtu.deanoffice.oldentity.Expel;
import ua.edu.chdtu.deanoffice.oldentity.Grade;
import ua.edu.chdtu.deanoffice.oldentity.Group;
import ua.edu.chdtu.deanoffice.oldentity.GroupSubject;
import ua.edu.chdtu.deanoffice.oldentity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.oldentity.OrderReason;
import ua.edu.chdtu.deanoffice.oldentity.Position;
import ua.edu.chdtu.deanoffice.oldentity.Privilege;
import ua.edu.chdtu.deanoffice.oldentity.Speciality;
import ua.edu.chdtu.deanoffice.oldentity.Student;
import ua.edu.chdtu.deanoffice.oldentity.Subject;
import ua.edu.chdtu.deanoffice.oldentity.Teacher;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static ua.edu.chdtu.deanoffice.DatabaseConnector.getFirebirdSession;
import static ua.edu.chdtu.deanoffice.DatabaseConnector.getPostgresSession;

public class MigrationData {

    static List<Department> oldFaculties = getFirebirdSession().createQuery("from Department", Department.class).list();
    static List<Faculty> newFaculties = new ArrayList<>();

    static List<Cathedra> oldDepartments = getFirebirdSession().createQuery("from Cathedra", Cathedra.class).list();
    static List<ua.edu.chdtu.deanoffice.entity.Department> newDepartments = new ArrayList<>();

    static List<Position> oldPositions = getFirebirdSession().createQuery("from Position", Position.class).list();
    static List<ua.edu.chdtu.deanoffice.entity.Position> newPositions = new ArrayList<>();

    static List<OrderReason> oldReasons = getFirebirdSession().createQuery("from OrderReason", OrderReason.class).list();
    static List<ua.edu.chdtu.deanoffice.entity.OrderReason> newReasons = new ArrayList<>();

    static List<Speciality> oldSpecialities = getFirebirdSession().createQuery("from Speciality s order by s.specialistCode", Speciality.class).list();
    static List<ua.edu.chdtu.deanoffice.entity.Speciality> newSpecialities = new ArrayList<>();

    static List<Specialization> newSpecializations = new ArrayList<>();

    static List<Degree> newDegrees = new ArrayList<>();

    static List<Group> oldGroups = getFirebirdSession().createQuery("from Group", Group.class).list();
    static List<StudentGroup> newGroups = new ArrayList<>();

    static List<Privilege> oldPrivileges = getFirebirdSession().createQuery("from Privilege", Privilege.class).list();
    static List<ua.edu.chdtu.deanoffice.entity.Privilege> newPrivileges = new ArrayList<>();

    static List<Student> oldStudents = getFirebirdSession().createQuery("from Student", Student.class).list();
    static List<ua.edu.chdtu.deanoffice.entity.Student> newStudents = new ArrayList<>();
    static List<StudentDegree> newStudentDegrees = new ArrayList<>();

    static List<Teacher> oldTeachers = getFirebirdSession().createQuery("from Teacher ", Teacher.class).list();
    static List<ua.edu.chdtu.deanoffice.entity.Teacher> newTeachers = new ArrayList<>();

    static List<KnowledgeControl> oldKnowledgeControlKinds = getFirebirdSession().createQuery("from KnowledgeControl", KnowledgeControl.class).list();
    static List<ua.edu.chdtu.deanoffice.entity.KnowledgeControl> newKnowledgeControlKinds = new ArrayList<>();

    static List<Subject> oldSubjects = getFirebirdSession().createQuery("from Subject", Subject.class).list();
    static List<Course> newCourses = new ArrayList<>();
    static List<CourseName> newCourseNames = new ArrayList<>();

    static List<GroupSubject> oldCoursesForGroups = getFirebirdSession().createQuery("from GroupSubject ", GroupSubject.class).list();
    static List<CourseForGroup> newCourseForGroups = new ArrayList<>();

    static List<Grade> oldGrades = getFirebirdSession().createQuery("from Grade", Grade.class).list();
    static List<ua.edu.chdtu.deanoffice.entity.Grade> newGrades = new ArrayList<>();

    static List<Expel> oldExpels = getFirebirdSession().createQuery("from Expel", Expel.class).list();
    static List<StudentExpel> newExpels = new ArrayList<>();

    static List<AcademicVacation> oldAcademicVacations = getFirebirdSession().createQuery("from AcademicVacation", AcademicVacation.class).list();
    static List<StudentAcademicVacation> newAcademicVacations = new ArrayList<>();

    @Transactional
    private static void saveAllItems(@NotNull List<? extends BaseEntity> entities) {
        entities.forEach(e -> {
            try {
                e.setId((Integer) getPostgresSession().save(e));
            } catch (Exception exception) {
                exception.printStackTrace();
                System.out.println("Failed to save entity " + e.getClass());
            }
        });
    }

    @Transactional
    public static void updateEntity(@NotNull BaseEntity entity) {
        getPostgresSession().update(entity);
    }

    public static void saveAllNewEntities() {
        //Order matters!
        saveAllItems(newFaculties);
        System.out.println("Faculties saved");

        saveAllItems(newDepartments);
        System.out.println("Departments saved");

        saveAllItems(newPositions);
        System.out.println("Positions saved");

        saveAllItems(newReasons);
        System.out.println("Reasons saved");

        saveAllItems(newDegrees);
        System.out.println("Degrees saved");

        saveAllItems(newSpecialities);
        System.out.println("Specialities saved");

        saveAllItems(newSpecializations);
        System.out.println("Specializations saved");

        saveAllItems(newGroups);
        System.out.println("Groups saved");

        saveAllItems(newPrivileges);
        System.out.println("Privileges saved");

        saveAllItems(newStudents);
        System.out.println("Students saved");

        saveAllItems(newTeachers);
        System.out.println("Teachers saved");

        saveAllItems(newKnowledgeControlKinds);
        System.out.println("Knowledge control kinds saved");

        saveAllItems(newCourseNames);
        System.out.println("Course names saved");

        saveAllItems(newCourses);
        System.out.println("Courses saved");

        saveAllItems(newCourseForGroups);
        System.out.println("Courses for groups saved");

        saveAllItems(newExpels);
        System.out.println("Expels saved");

        saveAllItems(newStudentDegrees);
        System.out.println("Student degrees saved");

        saveAllItems(newAcademicVacations);
        System.out.println("Academic vacations saved");

        saveAllItems(newGrades);
        System.out.println("Grades saved");
    }
}
