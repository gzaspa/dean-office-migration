package ua.edu.chdtu.deanoffice;

import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ua.edu.chdtu.deanoffice.entity.Course;
import ua.edu.chdtu.deanoffice.entity.CourseForGroup;
import ua.edu.chdtu.deanoffice.entity.CourseName;
import ua.edu.chdtu.deanoffice.entity.CurrentYear;
import ua.edu.chdtu.deanoffice.entity.Degree;
import ua.edu.chdtu.deanoffice.entity.EctsGrade;
import ua.edu.chdtu.deanoffice.entity.EducationDocument;
import ua.edu.chdtu.deanoffice.entity.Faculty;
import ua.edu.chdtu.deanoffice.entity.Grade;
import ua.edu.chdtu.deanoffice.entity.KnowledgeControl;
import ua.edu.chdtu.deanoffice.entity.Payment;
import ua.edu.chdtu.deanoffice.entity.Speciality;
import ua.edu.chdtu.deanoffice.entity.Specialization;
import ua.edu.chdtu.deanoffice.entity.Student;
import ua.edu.chdtu.deanoffice.entity.StudentAcademicVacation;
import ua.edu.chdtu.deanoffice.entity.StudentDegree;
import ua.edu.chdtu.deanoffice.entity.StudentExpel;
import ua.edu.chdtu.deanoffice.entity.StudentGroup;
import ua.edu.chdtu.deanoffice.entity.TuitionForm;
import ua.edu.chdtu.deanoffice.entity.TuitionTerm;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;
import ua.edu.chdtu.deanoffice.oldentity.Subject;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;

public class Migration extends MigrationData {

    private static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static Date nullDateReplacer;

    private static String makeAbbreviation(String name) {
        return name.replaceAll("\\B.|\\P{L}", "").toUpperCase();
    }

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    public static boolean notEmpty(Object str) {
        return !isEmpty(str);
    }

    private static boolean equals(Object o1, Object o2) {
        return o1 != null && o2 != null && o1.equals(o2);
    }

    private static boolean stringEquals(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return false;
        }
        Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
        return ukrainianCollator.equals(s1, s2);
    }

    public static void migrate() {
        //Order does matter!
        migrateFaculties();
        migrateDepartments();
        migratePositions();
        migrateOrderReasons();
        createDegrees();
        migrateSpecialities();
        migrateGroups();
        migratePrivileges();
        migrateStudents();
        migrateTeachers();
        migrateKnowledgeControlKinds();
        migrateSubjects();
        migrateSubjectsForGroups();
        migrateExpels();
        createStudentDegrees();
        migrateGrades();
        migrateAcademicVacations();
        addCurrentYear();

        saveAllNewEntities();
        mergeCourses();
    }

    private static void mergeCourses() {
        System.out.println("Started merging courses");
        List<Course> courses = DatabaseConnector.getPostgresSession()
                .createQuery("from Course c order by c.courseName.id,c.knowledgeControl.id,c.semester,c.hours", Course.class).list();
        List<Course> equalCourses = new ArrayList<>();
        List<Course> coursesToDelete = new ArrayList<>();

        for (Course course : courses) {
            if (equalCourses.isEmpty()) {
                equalCourses.add(course);
            } else if (course.equals(equalCourses.get(0))) {
                equalCourses.add(course);
            } else {
                Course uniqueCourse = equalCourses.get(0);
                equalCourses.remove(uniqueCourse);

                List<CourseForGroup> courseForGroups = new ArrayList<>();
                equalCourses.forEach(course1 -> courseForGroups.addAll(DatabaseConnector.getPostgresSession()
                        .createQuery("from CourseForGroup cFg where cFg.course.id = :courseId", CourseForGroup.class)
                        .setParameter("courseId", course1.getId()).list()));
                courseForGroups.forEach(courseForGroup -> {
                    updateCourseForGroup(courseForGroup, uniqueCourse);
                });


                List<Grade> grades = new ArrayList<>();
                equalCourses.forEach(course1 -> grades.addAll(DatabaseConnector.getPostgresSession()
                        .createQuery("from Grade g where g.course.id = :courseId", Grade.class)
                        .setParameter("courseId", course1.getId()).list()));
                grades.forEach(grade -> {
                    updateGrade(grade, uniqueCourse);
                });


                coursesToDelete.addAll(equalCourses);

                equalCourses = new ArrayList<>();
                equalCourses.add(course);

            }
        }

        deleteCourses(coursesToDelete);
        System.out.println("Courses merged successfully");

    }

    private static void updateCourseForGroup(CourseForGroup courseForGroup, Course newCourse) {
        courseForGroup.setCourse(newCourse);
        updateEntity(courseForGroup);
    }

    private static void updateGrade(Grade grade, Course newCourse) {
        grade.setCourse(newCourse);
        updateEntity(grade);
    }

    private static void deleteCourses(List<Course> coursesToDelete) {
        coursesToDelete.forEach(course -> {
            Transaction tx = DatabaseConnector.getPostgresSession().getTransaction();
            try {
                tx.begin();

                Query deleteGradesQuery = DatabaseConnector.getPostgresSession().createQuery("delete Grade g where g.course.id = :courseId");
                deleteGradesQuery.setParameter("courseId", course.getId());
                deleteGradesQuery.executeUpdate();

                Query query = DatabaseConnector.getPostgresSession().createQuery("delete Course where id = :ID");
                query.setParameter("ID", course.getId());
                query.executeUpdate();

                tx.commit();
            } catch (Exception ex) {
                tx.rollback();
            }
        });
    }

    private static void addCurrentYear() {
        CurrentYear year = new CurrentYear();
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        if (calendar.get(Calendar.MONTH) >= 9) {
            year.setCurrYear(currentYear);
        } else {
            year.setCurrYear(currentYear - 1);
        }
        DatabaseConnector.getPostgresSession().save(year);
    }

    private static void createStudentDegrees() {
        oldStudents.forEach(oldStudent -> {
            StudentGroup newStudentGroup = newGroups.get(oldGroups.indexOf(oldStudent.getGroup()));
            Student newStudent = newStudents.get(oldStudents.indexOf(oldStudent));
            if (newStudentDegrees.stream().noneMatch(
                    studentDegree -> studentDegree.getStudent().equals(newStudent)
                            && studentDegree.getStudentGroup().equals(newStudentGroup))) {
                StudentDegree studentDegree = new StudentDegree();
                studentDegree.setStudent(newStudent);
                newStudent.getDegrees().add(studentDegree);
                studentDegree.setStudentGroup(newStudentGroup);
                studentDegree.setSpecialization(studentDegree.getStudentGroup().getSpecialization());
                studentDegree.setDegree(newStudentGroup.getSpecialization().getDegree());
                studentDegree.setActive(oldStudent.isInActive());
                studentDegree.setRecordBookNumber(oldStudent.getRecordBookNumber());
                if (studentDegree.getDegree().equals(newDegrees.get(0))) {
                    studentDegree.setDiplomaDate(oldStudent.getBachelorDiplomaDate());
                    studentDegree.setDiplomaNumber(oldStudent.getBachelorDiplomaNumber());
                    studentDegree.setThesisName(oldStudent.getBachelorWorkThesis());
                    if (newStudentGroup.getTuitionTerm().equals(TuitionTerm.SHORTENED)) {
                        studentDegree.setPreviousDiplomaType(EducationDocument.JUNIOR_BACHELOR_DIPLOMA);
                    } else {
                        studentDegree.setPreviousDiplomaType(EducationDocument.SECONDARY_SCHOOL_CERTIFICATE);
                    }
                } else if (studentDegree.getDegree().equals(newDegrees.get(1))) {
                    studentDegree.setDiplomaDate(oldStudent.getSpecialistDiplomaDate());
                    studentDegree.setDiplomaNumber(oldStudent.getSpecialistDiplomaNumber());
                    studentDegree.setThesisName(oldStudent.getSpecialistWorkThesis());
                    studentDegree.setPreviousDiplomaType(EducationDocument.BACHELOR_DIPLOMA);
                } else {
                    studentDegree.setDiplomaDate(oldStudent.getMasterDiplomaDate());
                    studentDegree.setDiplomaNumber(oldStudent.getMasterDiplomaNumber());
                    studentDegree.setThesisName(oldStudent.getMasterWorkThesis());
                    studentDegree.setThesisNameEng(oldStudent.getMasterThesisEng());
                    studentDegree.setPreviousDiplomaType(EducationDocument.BACHELOR_DIPLOMA);
                }
            }
        });
    }

    private static void migrateGrades() {
        oldGrades.forEach(oldGrade -> {
            Grade grade = new Grade();
            newGrades.add(grade);
            grade.setStudentDegree(getStudentDegree(oldGrade));
            grade.setCourse(newCourses.get(oldSubjects.indexOf(oldGrade.getSubject())));
            grade.setEcts(convertEctsGrade(oldGrade));
            grade.setPoints(oldGrade.getPoints());
            grade.setGrade(oldGrade.getGrade());
        });
        newStudentDegrees.addAll(additionalStudentDegrees);
    }

    private static Set<StudentDegree> additionalStudentDegrees = new HashSet<>();
    private static Set<Student> studentsWithAdditionalDegrees = new HashSet<>();

    private static StudentDegree getStudentDegree(ua.edu.chdtu.deanoffice.oldentity.Grade grade) {
        Student student = newStudents.get(oldStudents.indexOf(grade.getStudent()));
        StudentDegree result = student.getDegrees().stream()
                .filter(studentDegree -> oldGroups.get(newGroups.indexOf(studentDegree.getStudentGroup())).getSubjects().contains(grade.getSubject())).findFirst().orElse(null);
        if (result == null) {
            if (studentsWithAdditionalDegrees.contains(student)) {
                return additionalStudentDegrees.stream().filter(studentDegree -> studentDegree.getStudent().equals(student)).findFirst().get();
            } else {
                StudentDegree newStudentDegree = new StudentDegree();
                newStudentDegree.setStudent(student);
                newStudentDegree.setActive(false);
                newStudentDegree.setDegree(newDegrees.get(0));
                newStudentDegree.setSpecialization(newSpecializations.get(0));
                studentsWithAdditionalDegrees.add(student);
                additionalStudentDegrees.add(newStudentDegree);
                return newStudentDegree;
            }
        }
        return result;
    }

    private static void migrateAcademicVacations() {
        oldAcademicVacations.forEach(oldVacation -> {
            StudentAcademicVacation vacation = new StudentAcademicVacation();
            newAcademicVacations.add(vacation);
            Student student = newStudents.get(oldStudents.indexOf(oldVacation.getStudent()));
            StudentGroup group = newGroups.get(oldGroups.indexOf(oldVacation.getGroup()));

            if (newStudentDegrees.stream().noneMatch(studentDegree -> studentDegree.getStudent().equals(student)
                    && group.equals(studentDegree.getStudentGroup()))) {
                StudentDegree vacationStudentDegree = new StudentDegree();
                vacationStudentDegree.setStudent(student);
                student.getDegrees().add(vacationStudentDegree);
                vacationStudentDegree.setStudentGroup(group);
                vacationStudentDegree.setSpecialization(vacationStudentDegree.getStudentGroup().getSpecialization());
                vacationStudentDegree.setDegree(vacationStudentDegree.getStudentGroup().getSpecialization().getDegree());
                vacationStudentDegree.setActive(false);
                vacationStudentDegree.setRecordBookNumber(oldVacation.getStudent().getRecordBookNumber());
                newStudentDegrees.add(vacationStudentDegree);
                vacation.setStudentDegree(vacationStudentDegree);
            } else {
                vacation.setStudentDegree(newStudentDegrees.stream().filter(studentDegree -> studentDegree.getStudent().equals(student)
                        && studentDegree.getStudentGroup().equals(group)).findFirst().get());
            }

            vacation.setOrderDate(oldVacation.getOrderDate() == null ? new Date() : oldVacation.getOrderDate());
            vacation.setOrderReason(newReasons.get(oldReasons.indexOf(oldReasons.stream().filter(
                    r -> r.getId() == oldVacation.getOrderReason().getId()).findFirst().get())));
            vacation.setOrderNumber(oldVacation.getOrderNumber() == null ? "0" : oldVacation.getOrderNumber());
            //Wrong value may be used!!!
            vacation.setApplicationDate(oldVacation.getApplicationDate() == null ? nullDateReplacer : oldVacation.getApplicationDate());
            vacation.setVacationStartDate(oldVacation.getVacationStart());
            //Wrong value used!!!
            vacation.setVacationEndDate(nullDateReplacer);

            vacation.setStudentGroup(vacation.getStudentDegree().getStudentGroup());
            vacation.setStudyYear(0);
            vacation.setExtraInformation("");
        });

    }

    private static void migrateExpels() {
        try {
            nullDateReplacer = dateFormat.parse("01-01-1980");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        oldExpels.forEach(oldExpel -> {
            StudentExpel expel = new StudentExpel();
            newExpels.add(expel);
            Student student = newStudents.get(oldStudents.indexOf(oldExpel.getStudent()));

            StudentDegree expelStudentDegree = new StudentDegree();
            expelStudentDegree.setStudent(student);
            student.getDegrees().add(expelStudentDegree);
            expelStudentDegree.setStudentGroup(newGroups.get(oldGroups.indexOf(oldExpel.getGroup())));
            expelStudentDegree.setSpecialization(expelStudentDegree.getStudentGroup().getSpecialization());
            expelStudentDegree.setDegree(expelStudentDegree.getStudentGroup().getSpecialization().getDegree());
            expelStudentDegree.setActive(false);
            expelStudentDegree.setRecordBookNumber(oldExpel.getStudent().getRecordBookNumber());
            switch (oldExpel.getOrderReason().getId()) {
                case 7: {
                    expelStudentDegree.setDiplomaDate(oldExpel.getStudent().getBachelorDiplomaDate());
                    expelStudentDegree.setDiplomaNumber(oldExpel.getStudent().getBachelorDiplomaNumber());
                    expelStudentDegree.setThesisName(oldExpel.getStudent().getBachelorWorkThesis());
                    if (expelStudentDegree.getStudentGroup().getTuitionTerm().equals(TuitionTerm.SHORTENED)) {
                        expelStudentDegree.setPreviousDiplomaType(EducationDocument.JUNIOR_BACHELOR_DIPLOMA);
                    } else {
                        expelStudentDegree.setPreviousDiplomaType(EducationDocument.SECONDARY_SCHOOL_CERTIFICATE);
                    }
                    break;
                }
                case 8: {
                    expelStudentDegree.setDiplomaDate(oldExpel.getStudent().getSpecialistDiplomaDate());
                    expelStudentDegree.setDiplomaNumber(oldExpel.getStudent().getSpecialistDiplomaNumber());
                    expelStudentDegree.setThesisName(oldExpel.getStudent().getSpecialistWorkThesis());
                    expelStudentDegree.setPreviousDiplomaType(EducationDocument.BACHELOR_DIPLOMA);
                    break;
                }
                case 18: {
                    expelStudentDegree.setDiplomaDate(oldExpel.getStudent().getMasterDiplomaDate());
                    expelStudentDegree.setDiplomaNumber(oldExpel.getStudent().getMasterDiplomaNumber());
                    expelStudentDegree.setThesisName(oldExpel.getStudent().getMasterWorkThesis());
                    expelStudentDegree.setThesisNameEng(oldExpel.getStudent().getMasterThesisEng());
                    expelStudentDegree.setPreviousDiplomaType(EducationDocument.BACHELOR_DIPLOMA);
                    break;
                }
                default: {

                }
            }
            newStudentDegrees.add(expelStudentDegree);
            expel.setStudentDegree(expelStudentDegree);

            expel.setStudyYear(0);
            expel.setPayment(Payment.BUDGET);
            expel.setStudentGroup(expel.getStudentDegree().getStudentGroup());

            //Wrong value may be used!!!
            expel.setOrderDate(oldExpel.getOrderDate() == null ? nullDateReplacer : oldExpel.getOrderDate());
            expel.setOrderReason(newReasons.get(oldReasons.indexOf(oldReasons.stream().filter(
                    r -> r.getId() == oldExpel.getOrderReason().getId()).findFirst().get())));
            expel.setOrderNumber(oldExpel.getOrderNumber() == null ? "0" : oldExpel.getOrderNumber());
            //Wrong value may be used!!!
            expel.setApplicationDate(oldExpel.getApplicationDate() == null ? nullDateReplacer : oldExpel.getApplicationDate());
            expel.setExpelDate(oldExpel.getExpelDate());
        });
    }

    private static void migrateSubjectsForGroups() {
        oldCoursesForGroups.forEach(oldCG -> {
            CourseForGroup courseForGroup = new CourseForGroup();
            newCourseForGroups.add(courseForGroup);
            courseForGroup.setCourse(newCourses.get(oldSubjects.indexOf(oldCG.getSubject())));
            courseForGroup.setStudentGroup(newGroups.get(oldGroups.indexOf(oldCG.getGroup())));
            courseForGroup.setExamDate(oldCG.getExamDate());
            try {
                courseForGroup.setTeacher(newTeachers.get(oldTeachers.indexOf(oldTeachers.stream().filter(teacher ->
                        oldCG.getTeacher() != null &&
                                equals(teacher.getId(), oldCG.getTeacher().getId())).findFirst().get())));
            } catch (NoSuchElementException e) {
                courseForGroup.setTeacher(null);
            }
        });
    }

    private static void migrateSubjects() {
        //fixSemestersForOldSubjects();
        fixSubjectsNames();
        sortSubjects();
        oldSubjects.forEach(oldSubj -> {
            Course course = new Course();
            newCourses.add(course);
            CourseName courseName;
            if (newCourseNames.stream().anyMatch(name -> stringEquals(name.getName(), (oldSubj.getName())))) {
                courseName = newCourseNames.stream().filter(n -> stringEquals(n.getName(), oldSubj.getName())).findFirst().get();
            } else {
                courseName = new CourseName();
                newCourseNames.add(courseName);
                courseName.setName(oldSubj.getName());
                courseName.setNameEng("");
                courseName.setAbbreviation(oldSubj.getAbbreviation() == null ? "" : oldSubj.getAbbreviation());
            }
            course.setCourseName(courseName);
            course.setHours(oldSubj.getHours());
            course.setHoursPerCredit(course.getHours() % 36 == 0 ? 36 : 30);
            if (oldSubj.getCredits() == null) {
                course.setCredits(new BigDecimal(course.getHours() / course.getHoursPerCredit()));
            } else {
                course.setCredits(new BigDecimal(oldSubj.getCredits()));
            }

                course.setKnowledgeControl(newKnowledgeControlKinds.get(
                        oldKnowledgeControlKinds.indexOf(oldKnowledgeControlKinds.stream().filter(knowledgeControl ->
                                oldSubj.getKnowledgeControl() != null &&
                                        equals(knowledgeControl.getId(), oldSubj.getKnowledgeControl().getId())).findFirst()
                                .orElse(oldKnowledgeControlKinds.get(0)))));
            course.setSemester(oldSubj.getSemester());
        });
    }

    private static void fixSemestersForOldSubjects() {
        Set<Subject> changedSubjects = new HashSet<>();
        oldGroups.forEach(studentGroup -> {
            Set<Subject> allSubjects = studentGroup.getSubjects();
            List<Subject> newSubjects = new ArrayList<>();
            allSubjects.forEach(subject -> {
                if (!changedSubjects.contains(subject)) {
                    newSubjects.add(subject);
                }
            });
            newSubjects.forEach(subject -> {
                if (studentGroup.getFirstPartOfName().startsWith("М")
                        && !studentGroup.getFirstPartOfName().equals("М")) {
                    //masters
                    subject.setSemester(subject.getSemester() - 8);
                }
            });
            changedSubjects.addAll(newSubjects);
        });
        changedSubjects.forEach(changedSubject -> {
            oldSubjects.stream().filter(subject -> subject.getId() == changedSubject.getId()).findFirst()
                    .get().setSemester(changedSubject.getSemester());
        });
    }

    private static void sortSubjects() {
        oldSubjects.sort((o1, o2) -> {
            Collator ukrainianCollator = Collator.getInstance(new Locale("uk", "UA"));
            return ukrainianCollator.compare(o1.getName(), o2.getName());
        });
    }

    private static void fixSubjectsNames() {
        oldSubjects.forEach(s -> {
            s.setName(s.getName().replaceAll(" +", " "));
            s.setName(s.getName().replaceAll("[Ґ|ґ]+", ""));
            s.setName(s.getName().trim());
            s.setName(s.getName().replace(",", ", "));
            s.setName(s.getName().replace("- ", "-"));
            s.setName(s.getName().replace(" -", "-"));
            s.setName(s.getName().replace(" - ", "-"));
            s.setName(s.getName().replace(" ,", ","));
            s.setName(s.getName().replace("i", "і"));
            s.setName(s.getName().replace("c", "с"));
            s.setName(s.getName().replace("I", "І"));
            s.setName(s.getName().replace("\"", "'"));
            s.setName(s.getName().replace("\'", "'"));
            s.setName(s.getName().replace("( ", "("));
            s.setName(s.getName().replace("и", "и"));
            s.setName(s.getName().replace("пю", "п'ю"));
            s.setName(s.getName().replace("п ю", "п'ю"));
            s.setName(s.getName().replace("`", "'"));
            s.setName(s.getName().replace("гое", "го"));
            s.setName(s.getName().replace("прауі", "праці"));
            s.setName(s.getName().replace("ы", "і"));
            s.setName(s.getName().replace("(нім)", "(німецька)"));
            s.setName(s.getName().replace("(нім.)", "(німецька)"));
            s.setName(s.getName().replace("проф. ", "професійним "));
            s.setName(s.getName().replace("проф ", "професійним "));
            s.setName(s.getName().replace("мед.", "медичних"));
            s.setName(s.getName().replace("вихованя", "виховання"));
            s.setName(s.getName().replace("ознайомлювальнапрактика", "ознайомлювальна практика"));
            s.setName(s.getName().replace("проблеминаукового", "проблеми наукового"));
            s.setName(s.getName().replace("комп.юторних", "комп'ютерних"));
            s.setName(s.getName().replace("Віртуальнівимірювальні", "Віртуальні вимірювальні"));
            s.setName(s.getName().replace("обєктів", "об'єктів"));
            s.setName(s.getName().replace(" ультура ", " культура "));
            s.setName(s.getName().replace("авторську", "авторське"));
            if (s.getName().endsWith("вихованн")) {
                s.setName(s.getName().replace("вихованн", "виховання"));
            }
            if (s.getName().endsWith("спрям")) {
                s.setName(s.getName().replace("спрям", "спрямуванням"));
            }
            s.setName(s.getName().replace("культуцра", "культура"));
            s.setName(s.getName().replace("телекомунікацйних", "телекомунікаційних"));
            s.setName(s.getName().replace("ом'п", "омп"));
            s.setName(s.getName().replace(" І ", " і "));
            s.setName(s.getName().replace("\n", ""));
            s.setName(s.getName().replace("''", "'"));
            s.setName(s.getName().replaceAll("[0-9]+", ""));
            s.setName(s.getName().substring(0, 1).toUpperCase() +
                    s.getName().substring(1, s.getName().length()));
        });
    }

    private static void migrateKnowledgeControlKinds() {
        oldKnowledgeControlKinds.forEach(oldKCKind -> {
            ua.edu.chdtu.deanoffice.entity.KnowledgeControl knowledgeControl = new ua.edu.chdtu.deanoffice.entity.KnowledgeControl();
            newKnowledgeControlKinds.add(knowledgeControl);
            knowledgeControl.setName(oldKCKind.getName());
            knowledgeControl.setNameEng("");
            knowledgeControl.setGraded(oldKCKind.getGrade());
        });

        KnowledgeControl kc = new KnowledgeControl();
        kc.setGraded(false);
        kc.setName("практика (як залік)");
        kc.setNameEng("");
        newKnowledgeControlKinds.add(kc);
    }

    private static void migrateTeachers() {
        oldTeachers.forEach(oldT -> {
            ua.edu.chdtu.deanoffice.entity.Teacher teacher = new ua.edu.chdtu.deanoffice.entity.Teacher();
            newTeachers.add(teacher);
            teacher.setActive(oldT.isInActive());
            teacher.setName(oldT.getName());
            teacher.setSurname(oldT.getSurname());
            teacher.setPatronimic(oldT.getPatronimic());
            teacher.setSex(oldT.getSex() == 'M' ? Sex.MALE : Sex.FEMALE);
            teacher.setDepartment(newDepartments.get(oldDepartments.indexOf(oldDepartments.stream().filter(department ->
                    equals(department.getId(), oldT.getCathedra().getId())).findFirst().get())));
            teacher.setPosition(newPositions.get(oldPositions.indexOf(oldPositions.stream().filter(position ->
                    equals(position.getId(), oldT.getPosition().getId())).findFirst().get())));
            teacher.setScientificDegree(oldT.getDegree());
        });
    }

    private static void migrateStudents() {
        oldStudents.forEach(oldStudent -> {
            ua.edu.chdtu.deanoffice.entity.Student student = new ua.edu.chdtu.deanoffice.entity.Student();
            newStudents.add(student);
            student.setName(oldStudent.getName());
            student.setSurname(oldStudent.getSurname());
            student.setPatronimic(oldStudent.getPatronimic());
            student.setNameEng(oldStudent.getNameEnglish());
            student.setSurnameEng(oldStudent.getSurnameEnglish());
            student.setPatronimicEng(oldStudent.getPatronimicEnglish());
            student.setEmail("");
            student.setBirthDate(oldStudent.getBirthDate());
            student.setActualAddress(oldStudent.getAddress());
            student.setRegistrationAddress("");
            student.setMotherName(oldStudent.getMotherName());
            student.setMotherInfo(oldStudent.getMotherInfo());
            student.setMotherPhone(oldStudent.getMotherPhone());
            student.setFatherName(oldStudent.getFatherName());
            student.setFatherInfo(oldStudent.getFatherInfo());
            student.setFatherPhone(oldStudent.getFatherPhone());
            student.setNotes(oldStudent.getNotes());
            if (student.getPrivilege() == null) {
                student.setPrivilege(newPrivileges.get(0));
            } else {
                student.setPrivilege(newPrivileges.get(oldPrivileges.indexOf(oldPrivileges.stream().filter(privilege ->
                        equals(privilege.getId(), oldStudent.getPrivilege().getId())).findFirst().get())));
            }
            student.setSex(oldStudent.getSex() == 'M' ? Sex.MALE : Sex.FEMALE);
            student.setSchool(oldStudent.getSchool());
        });
    }

    private static void migrateGroups() {
        oldGroups.forEach(oldGroup -> {
            StudentGroup studentGroup = new StudentGroup();
            newGroups.add(studentGroup);
            studentGroup.setName(oldGroup.getName());
            studentGroup.setActive(oldGroup.isActive());
            studentGroup.setCreationYear(oldGroup.getCreationYear());
            studentGroup.setBeginYears(oldGroup.getStudyStartYear());
            studentGroup.setTuitionTerm(oldGroup.getFirstPartOfName().startsWith("СК") ?
                    TuitionTerm.SHORTENED :
                    TuitionTerm.REGULAR);
            studentGroup.setTuitionForm(oldGroup.getModeOfStudy() == 'з' ? TuitionForm.EXTRAMURAL : TuitionForm.FULL_TIME);
            Integer degreeId = 0;


            if (studentGroup.getTuitionTerm() == TuitionTerm.SHORTENED) {
                if (studentGroup.getTuitionForm() == TuitionForm.FULL_TIME) {
                    studentGroup.setStudySemesters(4);
                    studentGroup.setStudyYears(new BigDecimal(2));
                } else { //extramural
                    studentGroup.setStudySemesters(6);
                    studentGroup.setStudyYears(new BigDecimal(3));
                }
            } else if (oldGroup.getStudyStartYear() == 5 || oldGroup.getStudyStartYear() == 6) { //masters or specialists
                studentGroup.setStudySemesters(3);
                studentGroup.setStudyYears(new BigDecimal(1 + 5.0 / 12.0));
                if (oldGroup.getFirstPartOfName().startsWith("М") &&
                        !oldGroup.getFirstPartOfName().equals("М")) { //masters
                    degreeId = 2;
                } else if (!oldGroup.getSpeciality().isNew()) { //specialists
                    degreeId = 1;
                }
            } else if (studentGroup.getTuitionForm() == TuitionForm.EXTRAMURAL) { //extramural bachelors
                studentGroup.setStudySemesters(10);
                studentGroup.setStudyYears(new BigDecimal(5));
                degreeId = 0;
            } else { //full-time bachelors
                studentGroup.setStudySemesters(8);
                studentGroup.setStudyYears(new BigDecimal(4));
                degreeId = 0;
            }
            Degree degree = newDegrees.get(degreeId);

            try {
                studentGroup.setSpecialization(newSpecializations.stream().filter(specialization ->
                        (equals(oldGroup.getSpeciality().getBachelorCode(), specialization.getSpeciality().getCode()) ||
                                equals(oldGroup.getSpeciality().getSpecialistCode(), specialization.getSpeciality().getCode()) ||
                                equals(oldGroup.getSpeciality().getMasterCode(), specialization.getSpeciality().getCode()))
                                && stringEquals(specialization.getDegree().getName(), degree.getName())
                ).findFirst().get());
            } catch (NoSuchElementException e) {
                //Todo: wrong specialization is set here. Each group, that causes exception will have first specialization in table
                studentGroup.setSpecialization(newSpecializations.get(0));
                System.out.printf("Could not set specialization/speciality for %s!\n", oldGroup.getName());
            }
        });
    }

    private static void migratePrivileges() {
        oldPrivileges.forEach(oldP -> {
            ua.edu.chdtu.deanoffice.entity.Privilege privilege = new ua.edu.chdtu.deanoffice.entity.Privilege();
            newPrivileges.add(privilege);
            privilege.setActive(oldP.getActive());
            privilege.setName(oldP.getName());
        });
    }

    private static Specialization createBachelorsSpecialization(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec,
                                                                Speciality sp) {
        if (notEmpty(oldSpec.getBachelorCode())) {
            Specialization bachSpec = new Specialization();
            newSpecializations.add(bachSpec);
            bachSpec.setFaculty(newFaculties.get(oldFaculties.indexOf(oldFaculties.stream().filter(
                    faculty -> faculty.getId() == oldSpec.getDepartment().getId()).findFirst().get())));
            bachSpec.setDepartment(newDepartments.get(oldDepartments.indexOf(oldDepartments.stream().filter(
                    department -> department.getId() == oldSpec.getCathedra().getId()).findFirst().get())));
            bachSpec.setDegree(newDegrees.get(0));
            bachSpec.setActive(oldSpec.isActive());
            bachSpec.setName("");
            bachSpec.setNameEng("");
            bachSpec.setSpeciality(sp);
            bachSpec.setQualification("");
            bachSpec.setQualificationEng("");
            return bachSpec;
        }
        return null;
    }

    private static Specialization createSpecialistsSpecialization(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec,
                                                                  Speciality sp) {
        if (notEmpty(oldSpec.getBachelorCode())) {
            Specialization specialistsSpec = new Specialization();
            newSpecializations.add(specialistsSpec);
            specialistsSpec.setFaculty(newFaculties.get(oldFaculties.indexOf(oldFaculties.stream().filter(
                    faculty -> faculty.getId() == oldSpec.getDepartment().getId()).findFirst().get())));
            specialistsSpec.setDepartment(newDepartments.get(oldDepartments.indexOf(oldDepartments.stream().filter(
                    department -> department.getId() == oldSpec.getCathedra().getId()).findFirst().get())));
            specialistsSpec.setDegree(newDegrees.get(1));
            specialistsSpec.setActive(oldSpec.isActive());
            specialistsSpec.setName("");
            specialistsSpec.setNameEng("");
            specialistsSpec.setSpeciality(sp);
            specialistsSpec.setQualification("");
            specialistsSpec.setQualificationEng("");
            return specialistsSpec;
        }
        return null;
    }

    private static Specialization createMastersSpecialization(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec,
                                                              Speciality sp) {
        if (notEmpty(oldSpec.getMasterCode()) || (oldSpec.isNew() && notEmpty(oldSpec.getSpecialistCode()))) {
            Specialization masterSpec = new Specialization();
            newSpecializations.add(masterSpec);
            masterSpec.setFaculty(newFaculties.get(oldFaculties.indexOf(oldFaculties.stream().filter(
                    faculty -> faculty.getId() == oldSpec.getDepartment().getId()).findFirst().get())));
            masterSpec.setDepartment(newDepartments.get(oldDepartments.indexOf(oldDepartments.stream().filter(
                    department -> department.getId() == oldSpec.getCathedra().getId()).findFirst().get())));
            masterSpec.setDegree(newDegrees.get(2));
            masterSpec.setActive(oldSpec.isActive());
            masterSpec.setName("");
            masterSpec.setNameEng("");
            masterSpec.setSpeciality(sp);
            masterSpec.setQualification("");
            masterSpec.setQualificationEng("");
            return masterSpec;
        }
        return null;
    }


    private static void createDegrees() {
        newDegrees.add(new Degree("Бакалавр", "Bachelor"));
        newDegrees.add(new Degree("Спеціаліст", "Specialist"));
        newDegrees.add(new Degree("Магістр", "Master"));
    }

    private static void createBachelorSpeciality(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec) {
        if (notEmpty(oldSpec.getBachelorName())) {
            ua.edu.chdtu.deanoffice.entity.Speciality bachSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            bachSpec.setName(oldSpec.getBachelorName());
            bachSpec.setNameEng("");
            bachSpec.setActive(oldSpec.isActive());
            bachSpec.setCode(oldSpec.getBachelorCode());
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), bachSpec.getCode()))) {
                newSpecialities.add(bachSpec);
                createBachelorsSpecialization(oldSpec, bachSpec);
            } else {
                createBachelorsSpecialization(oldSpec, newSpecialities.stream().filter(speciality -> speciality.getCode()
                        .equals(oldSpec.getBachelorCode())).findFirst().get());
            }
        }
    }

    private static void createNewBachelorSpeciality(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec) {
        if (notEmpty(oldSpec.getBachelorCode())) {
            ua.edu.chdtu.deanoffice.entity.Speciality bachSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            bachSpec.setName(oldSpec.getFirstPartOfNewName(oldSpec.getBachelorName().replace("_м", "")));
            bachSpec.setNameEng("");
            bachSpec.setActive(oldSpec.isActive());
            if (oldSpec.getBachelorCode().contains("-")) {
                bachSpec.setCode(oldSpec.getBachelorCode().split("-")[0].trim());
            } else {
                bachSpec.setCode(oldSpec.getBachelorCode());
            }
            bachSpec.setCode(oldSpec.getSpecialistCode().substring(0, 3));
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), bachSpec.getCode()))) {
                newSpecialities.add(bachSpec);
                Specialization bachSpecialization = createBachelorsSpecialization(oldSpec, bachSpec);
                bachSpecialization.setName(oldSpec.getSecondPartOfNewName(oldSpec.getSpecialistName()));
            } else {
                Specialization s = createBachelorsSpecialization(oldSpec, newSpecialities.stream().filter(speciality -> speciality.getCode()
                        .equals(oldSpec.getSpecialistCode().substring(0, 3))).findFirst().get());
                s.setName(oldSpec.getSecondPartOfNewName(oldSpec.getBachelorName()));
            }
        }
    }

    private static void createSpecialistSpeciality(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec) {
        if (notEmpty(oldSpec.getSpecialistName())) {
            ua.edu.chdtu.deanoffice.entity.Speciality specialistsSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            specialistsSpec.setName(oldSpec.getSpecialistName());
            specialistsSpec.setNameEng("");
            specialistsSpec.setActive(oldSpec.isActive());
            if (oldSpec.getSpecialistCode().contains("-")) {
                specialistsSpec.setCode(oldSpec.getSpecialistCode().split("-")[0].trim());
            } else {
                specialistsSpec.setCode(oldSpec.getSpecialistCode());
            }
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), specialistsSpec.getCode()))) {
                createSpecialistsSpecialization(oldSpec, specialistsSpec);
                newSpecialities.add(specialistsSpec);
            } else {
                createSpecialistsSpecialization(oldSpec, newSpecialities.stream().filter(speciality -> speciality.getCode()
                        .equals(oldSpec.getSpecialistCode().split("-")[0])).findFirst().get());
            }
        }
    }

    private static void createMasterSpeciality(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec) {
        if (notEmpty(oldSpec.getMasterName())) {
            ua.edu.chdtu.deanoffice.entity.Speciality masterSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            masterSpec.setName(oldSpec.getMasterName());
            masterSpec.setNameEng("");
            masterSpec.setActive(oldSpec.isActive());
            if (oldSpec.getMasterCode().contains("-")) {
                masterSpec.setCode(oldSpec.getMasterCode().split("-")[0].trim());
            } else {
                masterSpec.setCode(oldSpec.getMasterCode());
            }
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), masterSpec.getCode()))) {
                newSpecialities.add(masterSpec);
                createMastersSpecialization(oldSpec, masterSpec);
            }
        }
    }

    private static void createNewMasterSpeciality(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec) {
        if (notEmpty(oldSpec.getSpecialistCode())) {
            ua.edu.chdtu.deanoffice.entity.Speciality masterSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            masterSpec.setName(oldSpec.getFirstPartOfNewName(oldSpec.getSpecialistName().replace("м_", "")));
            masterSpec.setNameEng("");
            masterSpec.setActive(oldSpec.isActive());
            masterSpec.setCode(oldSpec.getSpecialistCode().substring(0, 3));
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), masterSpec.getCode()))) {
                newSpecialities.add(masterSpec);
                Specialization masterSpecialization = createMastersSpecialization(oldSpec, masterSpec);
                masterSpecialization.setName(oldSpec.getSecondPartOfNewName(oldSpec.getSpecialistName()));
            } else {
                Specialization s = createMastersSpecialization(oldSpec, newSpecialities.stream().filter(speciality -> speciality.getCode()
                        .equals(oldSpec.getSpecialistCode().substring(0, 3))).findFirst().get());
                s.setName(oldSpec.getSecondPartOfNewName(oldSpec.getSpecialistName()));
            }
        }
    }

    private static void migrateSpecialities() {
        oldSpecialities.forEach(oldSpec -> {
            if (oldSpec.getBachelorName() != null) {
                oldSpec.setBachelorName(oldSpec.getBachelorName().replaceAll(" +", " "));
                oldSpec.setBachelorName(oldSpec.getBachelorName().trim());
            } else {
                oldSpec.setBachelorName("");
                oldSpec.setBachelorCode("   ");
            }

            if (oldSpec.getSpecialistName() != null) {
                oldSpec.setSpecialistName(oldSpec.getSpecialistName().replaceAll(" +", " "));
                oldSpec.setSpecialistName(oldSpec.getSpecialistName().trim());
            } else {
                oldSpec.setSpecialistName("");
                oldSpec.setSpecialistCode("   ");
            }

            if (oldSpec.getMasterName() != null) {
                oldSpec.setMasterName(oldSpec.getMasterName().replaceAll(" +", " "));
                oldSpec.setMasterName(oldSpec.getMasterName().trim());
            } else {
                oldSpec.setMasterName("");
                oldSpec.setMasterCode("   ");
            }

            if (oldSpec.isOld()) {
                createBachelorSpeciality(oldSpec);
                createSpecialistSpeciality(oldSpec);
                createMasterSpeciality(oldSpec);
            } else {
                if (oldSpec.getSpecialistName().startsWith("м_"))
                    createNewMasterSpeciality(oldSpec);
                else
                    createNewBachelorSpeciality(oldSpec);
            }
        });
    }

    private static void migrateOrderReasons() {
        oldReasons.forEach(oldRes -> {
            ua.edu.chdtu.deanoffice.entity.OrderReason orderReason = new ua.edu.chdtu.deanoffice.entity.OrderReason();
            newReasons.add(orderReason);
            orderReason.setActive(oldRes.isActive());
            orderReason.setName(oldRes.getName());
            orderReason.setKind(oldRes.getKind());
        });
    }

    private static void migratePositions() {
        oldPositions.forEach((oldPosition) -> {
            ua.edu.chdtu.deanoffice.entity.Position position = new ua.edu.chdtu.deanoffice.entity.Position();
            newPositions.add(position);
            position.setName(oldPosition.getName());
        });
    }

    private static void migrateDepartments() {
        oldDepartments.forEach((oldDepartment) -> {
            ua.edu.chdtu.deanoffice.entity.Department department = new ua.edu.chdtu.deanoffice.entity.Department();
            newDepartments.add(department);
            department.setName(oldDepartment.getName());
            department.setAbbr(oldDepartment.getAbbreviation() == null ? "" : oldDepartment.getAbbreviation());
            department.setActive(oldDepartment.isActive());
            department.setFaculty(
                    newFaculties.get(oldFaculties.indexOf(oldFaculties.stream().filter(
                            faculty -> faculty.getId() == oldDepartment.getDepartment().getId()).findFirst().get())));
        });
    }

    private static void migrateFaculties() {
        oldFaculties.forEach((oldFaculty) -> {
            Faculty faculty = new Faculty();
            newFaculties.add(faculty);
            faculty.setName(oldFaculty.getName());
            faculty.setNameEng("");
            ua.edu.chdtu.deanoffice.oldentity.Teacher dean = oldFaculty.getDean();
            if (dean == null) {
                if (oldFaculty.getAbbreviation() != null && oldFaculty.getAbbreviation().equals("ФІТІС")) {
                    faculty.setDean("Трегубенко Ірина Борисівна");
                } else if ("ФЕТ".equals(oldFaculty.getAbbreviation())) {
                    faculty.setDean("Гончаров Артем Володимирович");
                } else {
                    faculty.setDean(null);
                }
            } else {
                faculty.setDean(dean.getSurname() + " " + dean.getName() + " " + dean.getPatronimic());
            }

            faculty.setAbbr(oldFaculty.getAbbreviation() == null ? makeAbbreviation(faculty.getName())
                    : oldFaculty.getAbbreviation()
            );
            faculty.setActive(oldFaculty.isActive());
        });
    }

    private static EctsGrade convertEctsGrade(ua.edu.chdtu.deanoffice.oldentity.Grade grade) {
        if (grade.getGradeECTS() == null) {
            return null;
        } else {
            switch (grade.getGradeECTS().trim()) {
                case "A": {
                    return EctsGrade.A;
                }
                case "B": {
                    return EctsGrade.B;
                }
                case "C": {
                    return EctsGrade.C;
                }
                case "D": {
                    return EctsGrade.D;
                }
                case "E": {
                    return EctsGrade.E;
                }
                case "FX": {
                    return EctsGrade.FX;
                }
                case "F": {
                    return EctsGrade.F;
                }
                default: {
                    return null;
                }
            }
        }
    }
}
