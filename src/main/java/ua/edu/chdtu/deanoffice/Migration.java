package ua.edu.chdtu.deanoffice;

import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.entity.superclasses.Sex;
import ua.edu.chdtu.deanoffice.oldentity.Subject;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Migration extends MigrationData {

    private static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static Date nullDateReplacer;

    private static String makeAbbreviation(String name) {
        return name.replaceAll("\\B.|\\P{L}", "").toUpperCase();
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
        createStudentDegrees();
        migrateTeachers();
        migrateKnowledgeControlKinds();
        migrateSubjects();
        migrateSubjectsForGroups();
        migrateGrades();
        migrateExpels();
        migrateAcademicVacations();
        addCurrentYear();

        saveAllNewEntities();
    }

    private static void addCurrentYear() {
        CurrentYear year = new CurrentYear();
        year.setCurrYear(2017);
        DatabaseConnector.getPostgresSession().save(year);
    }

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    private static void createStudentDegrees() {
        oldStudents.forEach(oldStudent -> {
            StudentDegree studentBachelorDegree = new StudentDegree();
            if (!isEmpty(oldStudent.getBachelorWorkThesis())) {
                studentBachelorDegree.setStudent(newStudents.get(oldStudents.indexOf(oldStudent)));
                studentBachelorDegree.setDegree(newDegrees.get(0));
                studentBachelorDegree.setDiplomaDate(oldStudent.getBachelorDiplomaDate());
                studentBachelorDegree.setDiplomaNumber(oldStudent.getBachelorDiplomaNumber());
                studentBachelorDegree.setThesisName(oldStudent.getBachelorWorkThesis());
                studentBachelorDegree.setAwarded(oldStudent.isBachalorSucceeded());
                studentBachelorDegree.setStudentGroup(newGroups.stream().filter(studentGroup ->
                                studentGroup.getName().equals(oldStudent.getGroup().getName())
                        //&& studentGroup.getSpecialization().getDegree().equals(newDegrees.get(0))
                ).findFirst().orElse(null));

                newStudentDegrees.add(studentBachelorDegree);
            }

            StudentDegree studentSpecialistDegree = new StudentDegree();
            if (!isEmpty(oldStudent.getSpecialistWorkThesis())) {
                studentSpecialistDegree.setStudent(newStudents.get(oldStudents.indexOf(oldStudent)));
                studentSpecialistDegree.setDegree(newDegrees.get(1));
                studentSpecialistDegree.setDiplomaDate(oldStudent.getSpecialistDiplomaDate());
                studentSpecialistDegree.setDiplomaNumber(oldStudent.getSpecialistDiplomaNumber());
                studentSpecialistDegree.setThesisName(oldStudent.getSpecialistWorkThesis());
                studentSpecialistDegree.setPreviousDiplomaNumber(studentBachelorDegree.getDiplomaNumber());
                studentSpecialistDegree.setPreviousDiplomaDate(studentBachelorDegree.getDiplomaDate());
                studentBachelorDegree.setAwarded(oldStudent.isSpecialistSucceeded());
                studentBachelorDegree.setStudentGroup(newGroups.stream().filter(studentGroup ->
                                studentGroup.getName().equals(oldStudent.getGroup().getName())
                        //&& studentGroup.getSpecialization().getDegree().equals(newDegrees.get(1))
                ).findFirst().orElse(null));

                newStudentDegrees.add(studentSpecialistDegree);
            }

            StudentDegree studentMasterDegree = new StudentDegree();
            if (isEmpty(oldStudent.getMasterWorkThesis())) {
                studentMasterDegree.setStudent(newStudents.get(oldStudents.indexOf(oldStudent)));
                studentMasterDegree.setDegree(newDegrees.get(2));
                studentMasterDegree.setDiplomaDate(oldStudent.getMasterDiplomaDate());
                studentMasterDegree.setDiplomaNumber(oldStudent.getMasterDiplomaNumber());
                studentMasterDegree.setThesisName(oldStudent.getMasterWorkThesis());
                studentMasterDegree.setPreviousDiplomaNumber(studentBachelorDegree.getDiplomaNumber());
                studentMasterDegree.setPreviousDiplomaDate(studentBachelorDegree.getDiplomaDate());
                studentMasterDegree.setThesisNameEng(oldStudent.getMasterDiplomaWorkEngName());
                studentBachelorDegree.setAwarded(oldStudent.isMasterSucceeded());
                try {
                    studentBachelorDegree.setStudentGroup(newGroups.stream().filter(studentGroup ->
                                    studentGroup.getName().equals(oldStudent.getGroup().getName())
                            //&& studentGroup.getSpecialization().getDegree().equals(newDegrees.get(2))
                    ).findFirst().orElse(null));
                } catch (NullPointerException e) {
                    System.out.println(oldStudent.getGroup().getName() + " has not enough data");
                }


                newStudentDegrees.add(studentMasterDegree);
            }
        });
    }

    private static void migrateGrades() {
        oldGrades.forEach(oldGrade -> {
            Grade grade = new Grade();
            newGrades.add(grade);
            grade.setStudent(newStudents.get(oldStudents.indexOf(oldStudents.stream().filter(student ->
                    student.getId() == oldGrade.getStudent().getId()
            ).findFirst().get())));
            grade.setCourse(newCourses.get(oldSubjects.indexOf(oldSubjects.stream().filter(course ->
                    course.getId() == oldGrade.getSubject().getId()
            ).findFirst().get())));
            grade.setEcts(convertEctsGrade(oldGrade));
            grade.setPoints(oldGrade.getPoints() == null ? 0 : oldGrade.getPoints());
            grade.setGrade(oldGrade.getGrade() == null ? 0 : oldGrade.getGrade());
        });
    }

    private static EctsGrade convertEctsGrade(ua.edu.chdtu.deanoffice.oldentity.Grade grade) {
        if (grade.getGradeECTS() == null) {
            return null;
        } else {
            if (grade.getSubject().getKnowledgeControl().getGrade()) {
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
            } else {
                if (grade.getGradeECTS() == null || grade.getGradeECTS().trim().equals("")) {
                    return null;
                }
                if ("ABCDE".contains(grade.getGradeECTS())) {
                    return EctsGrade.P;
                }
                if ("FX".contains(grade.getGradeECTS())) {
                    return EctsGrade.F;
                } else {
                    return null;
                }
            }
        }
    }

    private static void migrateAcademicVacations() {
        oldAcademicVacations.forEach(oldVacation -> {
            StudentAcademicVacation vacation = new StudentAcademicVacation();
            newAcademicVacations.add(vacation);
            vacation.setStudent(newStudents.get(oldStudents.indexOf(oldStudents.stream().filter(
                    s -> s.getId() == oldVacation.getStudent().getId()).findFirst().get())));
            vacation.setGroup(newGroups.get(oldGroups.indexOf(oldGroups.stream().filter(
                    g -> g.getId() == oldVacation.getGroup().getId()).findFirst().get())));
            vacation.setOrderDate(oldVacation.getOrderDate() == null ? new Date() : oldVacation.getOrderDate());
            vacation.setReason(newReasons.get(oldReasons.indexOf(oldReasons.stream().filter(
                    r -> r.getId() == oldVacation.getOrderReason().getId()).findFirst().get())));
            vacation.setOrderNumber(oldVacation.getOrderNumber() == null ? "0" : oldVacation.getOrderNumber());
            //Wrong value may be used!!!
            vacation.setApplicationDate(oldVacation.getApplicationDate() == null ? nullDateReplacer : oldVacation.getApplicationDate());
            vacation.setVacationStartDate(oldVacation.getVacationStart());
            //Wrong value used!!!
            vacation.setVacationEndDate(nullDateReplacer);
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
            expel.setStudent(newStudents.get(oldStudents.indexOf(oldStudents.stream().filter(
                    s -> s.getId() == oldExpel.getStudent().getId()).findFirst().get())));
            expel.setGroup(newGroups.get(oldGroups.indexOf(oldGroups.stream().filter(
                    g -> g.getId() == oldExpel.getGroup().getId()).findFirst().get())));
            //Wrong value may be used!!!
            expel.setOrderDate(oldExpel.getOrderDate() == null ? nullDateReplacer : oldExpel.getOrderDate());
            expel.setReason(newReasons.get(oldReasons.indexOf(oldReasons.stream().filter(
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
            courseForGroup.setCourse(newCourses.get(oldSubjects.indexOf(oldSubjects.stream().filter(
                    subject -> equals(subject.getId(), oldCG.getSubject().getId())).findFirst().get())));
            courseForGroup.setStudentGroup(newGroups.stream().filter(group ->
                    equals(group.getName(), oldCG.getGroup().getName())).findFirst().get());
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
        fixSemestersForOldSubjects();
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
            if (oldSubj.getCredits() == null) {
                course.setCredits(new BigDecimal(course.getHours() / 30.0));
            } else {
                course.setCredits(new BigDecimal(oldSubj.getCredits()));
            }
            try {
                course.setKnowledgeControl(newKnowledgeControlKinds.get(
                        oldKnowledgeControlKinds.indexOf(oldKnowledgeControlKinds.stream().filter(knowledgeControl ->
                                oldSubj.getKnowledgeControl() != null &&
                                        equals(knowledgeControl.getId(), oldSubj.getKnowledgeControl().getId())).findFirst().get())));
            } catch (NoSuchElementException e) {
                course.setKnowledgeControl(null);
            }
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
                if (studentGroup.getFirstPartOfName().startsWith("М")) {
                    //masters
                    subject.setSemester(subject.getSemester() - 8);
                } else if (studentGroup.getFirstPartOfName().endsWith("С") &&
                        !studentGroup.getFirstPartOfName().endsWith("СКС")) {
                    //shortened full time
                    if (subject.getSemester() <= 4) {
                        subject.setSemester(1);
                    } else {
                        subject.setSemester(subject.getSemester() - 4);
                    }
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
            s.setName(s.getName().replace(",", ", "));
            s.setName(s.getName().trim().replaceAll(" +", " "));
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
            s.setName(s.getName().replace("Інженерга", "Інженерна"));
            s.setName(s.getName().replace("WEB", "Web"));
            s.setName(s.getName().replace("Web ", "Web-"));
            s.setName(s.getName().replace("ком'п", "комп"));
            s.setName(s.getName().replace("матемитки", "математики"));
            s.setName(s.getName().replace("ии", "и"));
            s.setName(s.getName().replace("системпи", "системи"));
            s.setName(s.getName().replace("Охорони", "Охорона"));
            s.setName(s.getName().replace("діялбності", "діяльності"));
            s.setName(s.getName().replace("охоронип", "охорони п"));
            s.setName(s.getName().replace("житте", "життє"));
            s.setName(s.getName().replace("данних", "даних"));
            s.setName(s.getName().replace("ФІзика", "Фізика"));
            s.setName(s.getName().replace(" І ", " і "));
            s.setName(s.getName().replace("ссемблер", "семблер"));
            s.setName(s.getName().replace("невизначенності", "невизначеності"));
            s.setName(s.getName().replace("іБ", "і Б"));
            s.setName(s.getName().replace("болонський", "Болонський"));
            s.setName(s.getName().replace("Дискретна аналіз", "Дискретний аналіз"));
            s.setName(s.getName().replace("програмног ", "програмного "));
            s.setName(s.getName().replace("порофес", "профес"));
            s.setName(s.getName().replace("властність", "власність"));
            s.setName(s.getName().replace("мернжі", "мережі"));
            s.setName(s.getName().replace("розподіленні", "розподілені"));
            s.setName(s.getName().replace("Корс-", "Крос-"));
            s.setName(s.getName().replace("алгобра", "алгебра"));
            s.setName(s.getName().replace("сиситем", "систем"));
            s.setName(s.getName().replace("Макро-", "Макро "));
            s.setName(s.getName().replace(" т а", " та "));
            s.setName(s.getName().replace("Психологіл", "Психологія"));
            s.setName(s.getName().replace("засобт", "засоби"));
            s.setName(s.getName().replace("С ++", "С++"));
            s.setName(s.getName().replace("С + +", "С++"));
            s.setName(s.getName().replace("еконоліки", "економіки"));
            s.setName(s.getName().replace("еконоліки", "економіки"));
            s.setName(s.getName().replace("Мультимедія", "Мультимедіа"));
            s.setName(s.getName().replace("Науковов", "Науково"));
            s.setName(s.getName().replace("технологї", "технології"));
            s.setName(s.getName().replace("прогрмування", "програмування"));
            s.setName(s.getName().replace("оріентован", "орієнтован"));
            s.setName(s.getName().replace("Операційні систем", "Операційні системи"));
            s.setName(s.getName().replace("фукціонування", "функціонування"));
            s.setName(s.getName().replace("екологіі", "екології"));
            s.setName(s.getName().replace("соціцо", "соціо"));
            s.setName(s.getName().replace("електороніки", "електроніки"));
            s.setName(s.getName().replace("Первінні", "Первинні"));
            s.setName(s.getName().replace("Перифериіїні", "Периферійні"));
            s.setName(s.getName().replace("Політеконоиія", "Політекономія"));
            s.setName(s.getName().replace("теория", "теорія"));
            s.setName(s.getName().replace("пердачі", "передачі"));
            s.setName(s.getName().replace("передачи", "передачі"));
            s.setName(s.getName().replace("вбудоваваних", "вбудованих"));
            s.setName(s.getName().replace("м'ю", "мп'ю"));
            s.setName(s.getName().replace("dovs", "dows"));
            s.setName(s.getName().replace("сситеми", "системи"));
            s.setName(s.getName().replace("новоої", "нової"));
            s.setName(s.getName().replace("\n", ""));
            s.setName(s.getName().replace("проетування", "проектування"));
            s.setName(s.getName().replace("Спеціализовані", "Спеціалізовані"));
            s.setName(s.getName().replace("Спеіалізовані", "Спеціалізовані"));
            s.setName(s.getName().replace("Сснови", "Основи"));
            s.setName(s.getName().replace("інформаціних", "інформаційних"));
            s.setName(s.getName().replace("организація", "організація"));
            s.setName(s.getName().replace("Сучанна", "Сучасна"));
            s.setName(s.getName().replace("Сучасний світова", "Сучасна світова"));
            s.setName(s.getName().replace("Теоритичні", "Теоретичні"));
            s.setName(s.getName().replace("інфрмаційних", "інформаційних"));
            s.setName(s.getName().replace("процкси", "процеси"));
            s.setName(s.getName().replace("імовірністні", "імовірнісні"));
            s.setName(s.getName().replace("ймовірністні", "імовірнісні"));
            s.setName(s.getName().replace("експерімент", "експеримент"));
            s.setName(s.getName().replace("засобиобчислювальної", "засоби обчислювальної"));
            s.setName(s.getName().replace("автоматизованиї", "автоматизованої"));
            s.setName(s.getName().replace("крептозихсті", "криптозахисту"));
            s.setName(s.getName().replace("продктів", "продуктів"));
            s.setName(s.getName().replace(" рограмних", " програмних"));
            s.setName(s.getName().replace("сворення", "створення"));
            s.setName(s.getName().replace("інфомаційних", "інформаційних"));
            s.setName(s.getName().replace("прфесійною", "професійною"));
            s.setName(s.getName().replace("професійною спрямуванням", "професійним спрямуванням"));
            s.setName(s.getName().replace("за (", "(за"));
            s.setName(s.getName().replace(" за професійним спрямуванням", " (за професійним спрямуванням)"));
            s.setName(s.getName().replace("запрофесійним", "за професійним"));
            s.setName(s.getName().replace("релігіознавство", "релігієзнавство"));
            s.setName(s.getName().replace("Цивільна захист", "Цивільний захист"));
            s.setName(s.getName().replace("''", "'"));
            s.setName(s.getName().replace("комерції та бізнесу", "комерції та бізнесі"));
            s.setName(s.getName().replace("Периферіїні", "Периферійні"));
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
            knowledgeControl.setHasGrade(oldKCKind.getGrade());
        });

        KnowledgeControl kc = new KnowledgeControl();
        kc.setHasGrade(false);
        kc.setName("практика");
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
            student.setActive(oldStudent.isInActive());
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
            student.setStudentCardNumber("");
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
            studentGroup.setTuitionTerm(oldGroup.getFirstPartOfName().endsWith("С") &&
                    !oldGroup.getFirstPartOfName().endsWith("СКС") ?
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
                if (oldGroup.getFirstPartOfName().startsWith("М")
                        || oldGroup.getFirstPartOfName().startsWith("ЗМ")) { //masters
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
                        (equals(oldGroup.getSpeciality().getBachelorCode().split("-")[0], specialization.getSpeciality().getCode()) ||
                                equals(oldGroup.getSpeciality().getSpecialistCode().split("-")[0], specialization.getSpeciality().getCode()) ||
                                equals(oldGroup.getSpeciality().getMasterCode().split("-")[0], specialization.getSpeciality().getCode()))
                                && stringEquals(specialization.getDegree().getName(), degree.getName())
                                && (oldGroup.getSpeciality().getBachelorName().contains(specialization.getName()) ||
                                oldGroup.getSpeciality().getMasterName().contains(specialization.getName()) ||
                                oldGroup.getSpeciality().getSpecialistName().contains(specialization.getName()))
                ).findFirst().get());
            } catch (NoSuchElementException e) {
                System.out.printf("Exception during specialization/speciality setting for %s!\n", oldGroup.getName());
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
        if (oldSpec.getBachelorCode() != null && !oldSpec.getBachelorCode().isEmpty()) {
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
        if (oldSpec.getBachelorCode() != null && !oldSpec.getBachelorCode().isEmpty()) {
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
        if (oldSpec.getMasterCode() != null && !oldSpec.getMasterCode().isEmpty()) {
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
        if (!(oldSpec.getBachelorName() == null || oldSpec.getBachelorName().isEmpty())) {
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
        if (!(oldSpec.getBachelorName() == null || oldSpec.getBachelorName().isEmpty())) {
            ua.edu.chdtu.deanoffice.entity.Speciality bachSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            bachSpec.setName(oldSpec.getFirstPartOfNewName(oldSpec.getBachelorName()));
            bachSpec.setNameEng("");
            bachSpec.setActive(oldSpec.isActive());
            if (oldSpec.getBachelorCode().contains("-")) {
                bachSpec.setCode(oldSpec.getBachelorCode().split("-")[0].trim());
            } else {
                bachSpec.setCode(oldSpec.getBachelorCode());
            }
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), bachSpec.getCode()))) {
                newSpecialities.add(bachSpec);
                if (!oldSpec.getSecondPartOfNewName(oldSpec.getBachelorName()).isEmpty()) {
                    Specialization bachSpecialization = createBachelorsSpecialization(oldSpec, bachSpec);
                    bachSpecialization.setName(oldSpec.getSecondPartOfNewName(oldSpec.getBachelorName()));
                }
            } else {
                createBachelorsSpecialization(oldSpec, newSpecialities.stream().filter(speciality -> speciality.getCode()
                        .equals(oldSpec.getBachelorCode())).findFirst().get())
                        .setName(oldSpec.getSecondPartOfNewName(oldSpec.getBachelorName()));
            }
        }
    }

    private static void createSpecialistSpeciality(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec) {
        if (!(oldSpec.getSpecialistName() == null || oldSpec.getSpecialistName().isEmpty())) {
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

    private static void createNewSpecialistSpeciality(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec) {
        if (!(oldSpec.getSpecialistName() == null || oldSpec.getSpecialistName().isEmpty())) {
            ua.edu.chdtu.deanoffice.entity.Speciality specialistSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            specialistSpec.setName(oldSpec.getFirstPartOfNewName(oldSpec.getSpecialistName()));
            specialistSpec.setNameEng("");
            specialistSpec.setActive(oldSpec.isActive());
            if (oldSpec.getSpecialistCode().contains("-")) {
                specialistSpec.setCode(oldSpec.getSpecialistCode().split("-")[0].trim());
            } else {
                specialistSpec.setCode(oldSpec.getSpecialistCode());
            }
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), specialistSpec.getCode()))) {
                newSpecialities.add(specialistSpec);
                if (!oldSpec.getSecondPartOfNewName(oldSpec.getBachelorName()).isEmpty()) {
                    Specialization specSpecialization = createSpecialistsSpecialization(oldSpec, specialistSpec);
                    specSpecialization.setName(oldSpec.getSecondPartOfNewName(oldSpec.getSpecialistName()));
                }
            } else {
                createSpecialistsSpecialization(oldSpec, newSpecialities.stream().filter(speciality -> speciality.getCode()
                        .equals(oldSpec.getSpecialistCode().split("-")[0])).findFirst().get())
                        .setName(oldSpec.getSecondPartOfNewName(oldSpec.getSpecialistName()));
            }
        }
    }

    private static void createMasterSpeciality(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec) {
        if (!(oldSpec.getMasterName() == null || oldSpec.getMasterName().isEmpty())) {
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
        if (!(oldSpec.getMasterName() == null || oldSpec.getMasterName().isEmpty())) {
            ua.edu.chdtu.deanoffice.entity.Speciality masterSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            masterSpec.setName(oldSpec.getFirstPartOfNewName(oldSpec.getMasterName()));
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
                if (!oldSpec.getSecondPartOfNewName(oldSpec.getMasterName()).isEmpty()) {
                    Specialization masterSpecialization = createMastersSpecialization(oldSpec, masterSpec);
                    masterSpecialization.setName(oldSpec.getSecondPartOfNewName(oldSpec.getMasterName()));
                }
            } else {
                Specialization mastersSpecialization = createMastersSpecialization(oldSpec, newSpecialities.stream().filter(speciality -> speciality.getCode()
                        .equals(oldSpec.getMasterCode().split("-")[0])).findFirst().get());
                mastersSpecialization.setName(oldSpec.getSecondPartOfNewName(oldSpec.getMasterName()));
            }
        }
    }

    private static void migrateSpecialities() {
        oldSpecialities.forEach(oldSpec -> {
            oldSpec.setBachelorName(oldSpec.getBachelorName().replaceAll(" +", " "));
            oldSpec.setBachelorName(oldSpec.getBachelorName().trim());
            oldSpec.setSpecialistName(oldSpec.getSpecialistName().replaceAll(" +", " "));
            oldSpec.setSpecialistName(oldSpec.getSpecialistName().trim());
            oldSpec.setMasterName(oldSpec.getMasterName().replaceAll(" +", " "));
            oldSpec.setMasterName(oldSpec.getMasterName().trim());

            if (oldSpec.isOld()) {
                createBachelorSpeciality(oldSpec);
                createSpecialistSpeciality(oldSpec);
                createMasterSpeciality(oldSpec);
            } else {
                createNewBachelorSpeciality(oldSpec);
                //Should not be necessary
                //createNewSpecialistSpeciality(oldSpec);
                createNewMasterSpeciality(oldSpec);
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
        oldPositions.forEach((oPos) -> {
            ua.edu.chdtu.deanoffice.entity.Position position = new ua.edu.chdtu.deanoffice.entity.Position();
            newPositions.add(position);
            position.setName(oPos.getName());
        });
    }

    private static void migrateDepartments() {
        oldDepartments.forEach((oDep) -> {
            ua.edu.chdtu.deanoffice.entity.Department department = new ua.edu.chdtu.deanoffice.entity.Department();
            newDepartments.add(department);
            department.setName(oDep.getName());
            department.setAbbr(oDep.getAbbreviation() == null ? "" : oDep.getAbbreviation());
            department.setActive(oDep.isActive());
            department.setFaculty(
                    newFaculties.get(oldFaculties.indexOf(oldFaculties.stream().filter(
                            faculty -> faculty.getId() == oDep.getDepartment().getId()).findFirst().get())));
        });
    }

    private static void migrateFaculties() {
        oldFaculties.forEach((oldFaculty) -> {
            Faculty faculty = new Faculty();
            newFaculties.add(faculty);
            faculty.setName(oldFaculty.getName());
            faculty.setNameEng("");
            faculty.setAbbr(oldFaculty.getAbbreviation() == null ? makeAbbreviation(faculty.getName())
                    : oldFaculty.getAbbreviation()
            );
            faculty.setActive(oldFaculty.isActive());
        });
    }
}
