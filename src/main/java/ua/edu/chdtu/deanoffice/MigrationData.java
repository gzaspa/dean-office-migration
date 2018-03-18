package ua.edu.chdtu.deanoffice;

import com.sun.istack.internal.NotNull;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;
import ua.edu.chdtu.deanoffice.oldentity.*;
import ua.edu.chdtu.deanoffice.oldentity.Department;
import ua.edu.chdtu.deanoffice.oldentity.Grade;
import ua.edu.chdtu.deanoffice.oldentity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.oldentity.OrderReason;
import ua.edu.chdtu.deanoffice.oldentity.Position;
import ua.edu.chdtu.deanoffice.oldentity.Privilege;
import ua.edu.chdtu.deanoffice.oldentity.Speciality;
import ua.edu.chdtu.deanoffice.oldentity.Student;
import ua.edu.chdtu.deanoffice.oldentity.Teacher;

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

    private static void saveAllItems(@NotNull List<? extends BaseEntity> entities) {
        entities.forEach(e -> {
            try {
                getPostgresSession().saveOrUpdate(e);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public static void updateEntity(@NotNull BaseEntity entity) {
        try {
            getPostgresSession().update(entity);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void saveAllNewEntities() {
        //Order matters!
        saveAllItems(newFaculties);
        saveAllItems(newDepartments);
        saveAllItems(newPositions);
        saveAllItems(newReasons);
        saveAllItems(newDegrees);
        saveAllItems(newSpecialities);
        saveAllItems(newSpecializations);
        saveAllItems(newGroups);
        saveAllItems(newPrivileges);
        saveAllItems(newStudents);
        saveAllItems(newTeachers);
        saveAllItems(newKnowledgeControlKinds);
        saveAllItems(newCourseNames);
        saveAllItems(newCourses);
        saveAllItems(newCourseForGroups);
        saveAllItems(newGrades);
        saveAllItems(newExpels);
        saveAllItems(newStudentDegrees);
        saveAllItems(newAcademicVacations);
    }
}
