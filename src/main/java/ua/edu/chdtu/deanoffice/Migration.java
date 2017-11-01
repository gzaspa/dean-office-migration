package ua.edu.chdtu.deanoffice;

import com.sun.istack.internal.NotNull;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.oldentity.*;
import ua.edu.chdtu.deanoffice.oldentity.Department;
import ua.edu.chdtu.deanoffice.oldentity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.oldentity.OrderReason;
import ua.edu.chdtu.deanoffice.oldentity.Position;
import ua.edu.chdtu.deanoffice.oldentity.Privilege;
import ua.edu.chdtu.deanoffice.oldentity.Speciality;
import ua.edu.chdtu.deanoffice.oldentity.Student;
import ua.edu.chdtu.deanoffice.oldentity.Teacher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static ua.edu.chdtu.deanoffice.DatabaseConnector.getFirebirdSession;
import static ua.edu.chdtu.deanoffice.DatabaseConnector.getPostgresSession;

public class Migration {

    private static String makeAbbreviation(String s) {
        return s.replaceAll("\\B.|\\P{L}", "").toUpperCase();
    }

    private static void saveAllItems(@NotNull List<? extends BaseEntity> entities) {
        entities.forEach(e -> getPostgresSession().save(e));
    }

    private static boolean equals(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return false;
        } else {
            return o1.equals(o2);
        }
    }

    public static void migrate() {
        //Faculties
        List<Department> oldFaculties = getFirebirdSession().createQuery("from Department", Department.class).list();
        List<Faculty> newFaculties = new ArrayList<>();
        oldFaculties.forEach((oFaculty) -> {
            Faculty f = new Faculty();
            newFaculties.add(f);
            f.setName(oFaculty.getName());
            f.setNameEng("");
            f.setAbbr(oFaculty.getAbbreviation() == null ? makeAbbreviation(f.getName())
                    : oFaculty.getAbbreviation()
            );
            f.setActive(oFaculty.isActive());
        });
        saveAllItems(newFaculties);

        //Departments
        List<Cathedra> oldDepartments = getFirebirdSession().createQuery("from Cathedra", Cathedra.class).list();
        List<ua.edu.chdtu.deanoffice.entity.Department> newDepartments = new ArrayList<>();
        oldDepartments.forEach((oDep) -> {
            ua.edu.chdtu.deanoffice.entity.Department d = new ua.edu.chdtu.deanoffice.entity.Department();
            newDepartments.add(d);
            d.setName(oDep.getName());
            d.setAbbr(oDep.getAbbreviation() == null ? "" : oDep.getAbbreviation());
            d.setActive(oDep.isActive());
            d.setFaculty(
                    newFaculties.get(oldFaculties.indexOf(oldFaculties.stream().filter(
                            faculty -> faculty.getId() == oDep.getDepartment().getId()).findFirst().get())));
        });
        saveAllItems(newDepartments);

        //Positions
        List<Position> oldPositions = getFirebirdSession().createQuery("from Position", Position.class).list();
        List<ua.edu.chdtu.deanoffice.entity.Position> newPositions = new ArrayList<>();
        oldPositions.forEach((oPos) -> {
            ua.edu.chdtu.deanoffice.entity.Position p = new ua.edu.chdtu.deanoffice.entity.Position();
            newPositions.add(p);
            p.setName(oPos.getName());
        });
        saveAllItems(newPositions);

        //Order's Reasons
        List<OrderReason> oldReasons = getFirebirdSession().createQuery("from OrderReason", OrderReason.class).list();
        List<ua.edu.chdtu.deanoffice.entity.OrderReason> newReasons = new ArrayList<>();
        oldReasons.forEach(oldRes -> {
            ua.edu.chdtu.deanoffice.entity.OrderReason r = new ua.edu.chdtu.deanoffice.entity.OrderReason();
            newReasons.add(r);
            r.setActive(oldRes.isActive());
            r.setName(oldRes.getName());
            r.setKind(oldRes.getKind());
        });
        saveAllItems(newReasons);

        //Specialities
        List<Speciality> oldSpecialities = getFirebirdSession().createQuery("from Speciality", Speciality.class).list();
        List<ua.edu.chdtu.deanoffice.entity.Speciality> newSpecialities = new ArrayList<>();
        oldSpecialities.forEach(oldSpec -> {

            ua.edu.chdtu.deanoffice.entity.Speciality bachSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            bachSpec.setName(oldSpec.getBachelorName());
            bachSpec.setNameEng("");
            bachSpec.setActive(oldSpec.isActive());
            bachSpec.setCode(oldSpec.getBachelorCode());
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), bachSpec.getCode()))) {
                newSpecialities.add(bachSpec);
            }

            ua.edu.chdtu.deanoffice.entity.Speciality specialistsSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            specialistsSpec.setName(oldSpec.getName());
            specialistsSpec.setNameEng("");
            specialistsSpec.setActive(oldSpec.isActive());
            specialistsSpec.setCode(oldSpec.getCode());
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), specialistsSpec.getCode()))) {
                newSpecialities.add(specialistsSpec);
            }

            if (!(oldSpec.getMasterName() == null || oldSpec.getMasterName().isEmpty())) {
                ua.edu.chdtu.deanoffice.entity.Speciality masterSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
                masterSpec.setName(oldSpec.getMasterName());
                masterSpec.setNameEng("");
                masterSpec.setActive(oldSpec.isActive());
                masterSpec.setCode(oldSpec.getMasterCode());
                if (newSpecialities.stream().noneMatch(
                        speciality -> equals(speciality.getCode(), masterSpec.getCode()))) {
                    newSpecialities.add(masterSpec);
                }
            }
        });
        saveAllItems(newSpecialities);

        //Degrees
        List<Degree> degrees = new ArrayList<>();
        degrees.add(new Degree("Бакалавр", "Bachelor"));
        degrees.add(new Degree("Спеціаліст", "Specialist"));
        degrees.add(new Degree("Магістр", "Master"));
        saveAllItems(degrees);

        //Specializations
        List<Specialization> newSpecializations = new ArrayList<>();
        oldSpecialities.forEach(oldSpec -> {
            if (oldSpec.getBachelorCode() != null && !oldSpec.getBachelorCode().isEmpty()) {
                Specialization bachSpec = new Specialization();
                newSpecializations.add(bachSpec);
                bachSpec.setFaculty(newFaculties.get(oldFaculties.indexOf(oldFaculties.stream().filter(
                        faculty -> faculty.getId() == oldSpec.getDepartment().getId()).findFirst().get())));
                bachSpec.setDepartment(newDepartments.get(oldDepartments.indexOf(oldDepartments.stream().filter(
                        department -> department.getId() == oldSpec.getCathedra().getId()).findFirst().get())));
                bachSpec.setDegree(degrees.get(0));
                bachSpec.setStudySemesters(8);
                bachSpec.setStudyYears(new BigDecimal(4));
                bachSpec.setActive(oldSpec.isActive());
                bachSpec.setName(oldSpec.getBachelorName());
                bachSpec.setNameEng(oldSpec.getBachelorName());
                bachSpec.setSpeciality(newSpecialities.get(oldSpecialities.indexOf(oldSpecialities.stream().filter(
                        speciality -> speciality.getName() == oldSpec.getName()).findFirst().get())));
                bachSpec.setQualification("");
                bachSpec.setQualificationEng("");
            }

            if (oldSpec.getBachelorCode() != null && !oldSpec.getBachelorCode().isEmpty()) {
                Specialization masterSpec = new Specialization();
                newSpecializations.add(masterSpec);
                masterSpec.setFaculty(newFaculties.get(oldFaculties.indexOf(oldFaculties.stream().filter(
                        faculty -> faculty.getId() == oldSpec.getDepartment().getId()).findFirst().get())));
                masterSpec.setDepartment(newDepartments.get(oldDepartments.indexOf(oldDepartments.stream().filter(
                        department -> department.getId() == oldSpec.getCathedra().getId()).findFirst().get())));
                masterSpec.setDegree(degrees.get(2));
                masterSpec.setStudySemesters(3);
                masterSpec.setStudyYears(new BigDecimal(1.5));
                masterSpec.setActive(oldSpec.isActive());
                masterSpec.setName(oldSpec.getBachelorName());
                masterSpec.setNameEng(oldSpec.getBachelorName());
                masterSpec.setSpeciality(newSpecialities.get(oldSpecialities.indexOf(oldSpecialities.stream().filter(
                        speciality -> speciality.getName() == oldSpec.getName()).findFirst().get())));
                masterSpec.setQualification("");
                masterSpec.setQualificationEng("");
            }
        });
        saveAllItems(newSpecializations);

        //Groups
        List<Group> oldGroups = getFirebirdSession().createQuery("from Group", Group.class).list();
        List<StudentGroup> newGroups = new ArrayList<>();
        oldGroups.forEach(oldGroup -> {
            StudentGroup g = new StudentGroup();
            newGroups.add(g);
            g.setName(oldGroup.getName());
            g.setActive(oldGroup.isActive());
            g.setCreationYear(oldGroup.getCreationYear());
            g.setBeginYears(oldGroup.getStudyStartYear());
            //TODO
            //g.setSpecialization();
            g.setTuitionTerm(oldGroup.getFirstPartOfName().endsWith("С") ||
                    !oldGroup.getFirstPartOfName().endsWith("СКС") ?
                    's' :
                    'r');
            g.setTuitionForm(oldGroup.getModeOfStudy() == 'з' ? 'e' : 'f');
            if (oldGroup.getFirstPartOfName().startsWith("М") ||
                    oldGroup.getFirstPartOfName().startsWith("ЗМ")) {
                g.setStudySemesters(3);
                g.setStudyYears(new BigDecimal(1.5));
            } else {
                g.setStudySemesters(8);
                g.setStudyYears(new BigDecimal(4));
            }
        });
        saveAllItems(newGroups);

        //Privileges
        List<Privilege> oldPrivileges = getFirebirdSession().createQuery("from Privilege", Privilege.class).list();
        List<ua.edu.chdtu.deanoffice.entity.Privilege> newPrivileges = new ArrayList<>();
        oldPrivileges.forEach(oldP -> {
            ua.edu.chdtu.deanoffice.entity.Privilege p = new ua.edu.chdtu.deanoffice.entity.Privilege();
            newPrivileges.add(p);
            p.setActive(oldP.getActive());
            p.setName(oldP.getName());
        });
        saveAllItems(newPrivileges);

        //Students
        List<Student> oldStudents = getFirebirdSession().createQuery("from Student", Student.class).list();
        List<ua.edu.chdtu.deanoffice.entity.Student> newStudents = new ArrayList<>();
        oldStudents.forEach(oldStudent -> {
            ua.edu.chdtu.deanoffice.entity.Student s = new ua.edu.chdtu.deanoffice.entity.Student();
            newStudents.add(s);
            s.setActive(oldStudent.isInActive());
            s.setName(oldStudent.getName());
            s.setSurname(oldStudent.getSurname());
            s.setPatronimic(oldStudent.getPatronimic());
            s.setNameEng("");
            s.setSurnameEng("");
            s.setPatronimicEng("");
            s.setEmail("");
            s.setBirthDate(oldStudent.getBirthDate());
            s.setActualAddress(oldStudent.getAddress());
            s.setRegistrationAddress("");
            s.setMotherName(oldStudent.getMotherName());
            s.setMotherInfo(oldStudent.getMotherInfo());
            s.setMotherPhone(oldStudent.getMotherPhone());
            s.setFatherName(oldStudent.getFatherName());
            s.setFatherInfo(oldStudent.getFatherInfo());
            s.setFatherPhone(oldStudent.getFatherPhone());
            s.setStudentGroup(newGroups.get(oldGroups.indexOf(oldGroups.stream().filter(
                    group -> group.getId() == oldStudent.getGroup().getId()).findFirst().get())));
            s.setNotes(oldStudent.getNotes());
            if (s.getPrivilege() == null)
                s.setPrivilege(newPrivileges.get(0));
            else
                s.setPrivilege(newPrivileges.get(oldPrivileges.indexOf(oldPrivileges.stream().filter(privilege ->
                        equals(privilege.getId(), oldStudent.getPrivilege().getId())).findFirst().get())));
            s.setSex(oldStudent.getSex());
            s.setRecordBookNumber(oldStudent.getRecordBookNumber());
            s.setSchool(oldStudent.getSchool());
            s.setStudentCardNumber("");
        });
        saveAllItems(newStudents);

        //Teachers
        List<Teacher> oldTeachers = getFirebirdSession().createQuery("from Teacher ", Teacher.class).list();
        List<ua.edu.chdtu.deanoffice.entity.Teacher> newTeachers = new ArrayList<>();
        oldTeachers.forEach(oldT -> {
            ua.edu.chdtu.deanoffice.entity.Teacher t = new ua.edu.chdtu.deanoffice.entity.Teacher();
            newTeachers.add(t);
            t.setActive(oldT.isInActive());
            t.setName(oldT.getName());
            t.setSurname(oldT.getSurname());
            t.setPatronimic(oldT.getPatronimic());
            t.setSex(oldT.getSex());
            t.setDepartment(newDepartments.get(oldDepartments.indexOf(oldDepartments.stream().filter(department ->
                    equals(department.getId(), oldT.getCathedra().getId())).findFirst().get())));
            t.setPosition(newPositions.get(oldPositions.indexOf(oldPositions.stream().filter(position ->
                    equals(position.getId(), oldT.getPosition().getId())).findFirst().get())));
            t.setScientificDegree(oldT.getDegree());
        });
        saveAllItems(newTeachers);

        //Knowledge control kinds
        List<KnowledgeControl> oldKnowledgeControlKinds = getFirebirdSession().createQuery("from KnowledgeControl", KnowledgeControl.class).list();
        List<ua.edu.chdtu.deanoffice.entity.KnowledgeControl> newKnowledgeControlKinds = new ArrayList<>();
        oldKnowledgeControlKinds.forEach(oldKCKind -> {
            ua.edu.chdtu.deanoffice.entity.KnowledgeControl k = new ua.edu.chdtu.deanoffice.entity.KnowledgeControl();
            newKnowledgeControlKinds.add(k);
            k.setName(oldKCKind.getName());
            k.setNameEng("");
            k.setHasGrade(oldKCKind.getGrade());
        });
        saveAllItems(newKnowledgeControlKinds);

        //Subjects -> Courses/CourseNames
        List<Subject> oldSubjects = getFirebirdSession().createQuery("from Subject", Subject.class).list();
        oldSubjects.forEach(subject -> {
            subject.setName(subject.getName().trim().replaceAll(" +", " "));
            subject.setName(subject.getName().replace("- ", "-"));
            subject.setName(subject.getName().replace(" -", "-"));
            subject.setName(subject.getName().replace(" - ", "-"));
            subject.setName(subject.getName().replace(" ,", ","));
            subject.setName(subject.getName().replace("і", "i"));
            subject.setName(subject.getName().replace("\"", "'"));
            subject.setName(subject.getName().replace("\'", "'"));
            subject.setName(subject.getName().replace("( ", "("));
        });
        List<Course> newCourses = new ArrayList<>();
        List<CourseName> newCourseNames = new ArrayList<>();
        oldSubjects.forEach(oldSubj -> {
            Course c = new Course();
            newCourses.add(c);
            CourseName courseName;
            if (newCourseNames.stream().anyMatch(name -> equals(name.getName(), (oldSubj.getName())))) {
                courseName = newCourseNames.stream().filter(n -> equals(n.getName(), oldSubj.getName())).findFirst().get();
            } else {
                courseName = new CourseName();
                newCourseNames.add(courseName);
                courseName.setName(oldSubj.getName());
                courseName.setNameEng("");
                courseName.setAbbreviation(oldSubj.getAbbreviation() == null ? "" : oldSubj.getAbbreviation());
            }
            c.setCourseName(courseName);
            c.setHours(oldSubj.getHours());
            if (oldSubj.getCredits() == null)
                c.setCredits(new BigDecimal(c.getHours() / 36.0));
            else
                c.setCredits(new BigDecimal(oldSubj.getCredits()));
            try {
                c.setKnowledgeControl(newKnowledgeControlKinds.get(
                        oldKnowledgeControlKinds.indexOf(oldKnowledgeControlKinds.stream().filter(knowledgeControl ->
                                oldSubj.getKnowledgeControl() != null &&
                                        equals(knowledgeControl.getId(), oldSubj.getKnowledgeControl().getId())).findFirst().get())));
            } catch (NoSuchElementException e) {
                c.setKnowledgeControl(null);
            }
            c.setSemester(oldSubj.getSemester());
            //TODO
            //c.setSpecialization();
        });
        saveAllItems(newCourseNames);
        saveAllItems(newCourses);

        //Subjects for Groups -> Courses for Groups
        List<GroupSubject> oldCoursesForGroups = getFirebirdSession().createQuery("from GroupSubject ", GroupSubject.class).list();
        List<CoursesForGroups> newCoursesForGroups = new ArrayList<>();
        oldCoursesForGroups.forEach(oldCG -> {
            CoursesForGroups c = new CoursesForGroups();
            newCoursesForGroups.add(c);
            c.setCourse(newCourses.get(oldCoursesForGroups.indexOf(oldCoursesForGroups.stream().filter(groupSubject ->
                    equals(groupSubject.getSubject().getId(), oldCG.getSubject().getId())).findFirst().get())));
            c.setStudentGroup(newGroups.get(oldGroups.indexOf(oldGroups.stream().filter(group ->
                    equals(group.getId(), oldCG.getGroup().getId())).findFirst().get())));
            c.setExamDate(oldCG.getExamDate());
            try {
                c.setTeacher(newTeachers.get(oldTeachers.indexOf(oldTeachers.stream().filter(teacher ->
                        oldCG.getTeacher() != null &&
                                equals(teacher.getId(), oldCG.getTeacher().getId())).findFirst().get())));
            } catch (NoSuchElementException e) {
                c.setTeacher(null);
            }

        });
        saveAllItems(newCoursesForGroups);

    }
}
