package ua.edu.chdtu.deanoffice;

import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.oldentity.Subject;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Migration extends MigrationData {

    private static String makeAbbreviation(String s) {
        return s.replaceAll("\\B.|\\P{L}", "").toUpperCase();
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

    private static void createStudentDegrees() {
        oldStudents.forEach(student -> {
            StudentDegree studentBachelorDegree = new StudentDegree();
            if (student.getBachelorWorkThesis() != null && !student.getBachelorWorkThesis().isEmpty()) {
                studentBachelorDegree.setStudent(newStudents.get(oldStudents.indexOf(student)));
                studentBachelorDegree.setAwarded(false);
                studentBachelorDegree.setDegree(newDegrees.get(0));
                studentBachelorDegree.setDiplomaDate(student.getBachelorDiplomaDate());
                studentBachelorDegree.setDiplomaNumber(student.getBachelorDiplomaNumber());
                studentBachelorDegree.setThesisName(student.getBachelorWorkThesis());

                newStudentDegrees.add(studentBachelorDegree);
            }

            StudentDegree studentSpecialistDegree = new StudentDegree();
            if (student.getSpecialistWorkThesis() != null && !student.getSpecialistWorkThesis().isEmpty()) {
                studentSpecialistDegree.setStudent(newStudents.get(oldStudents.indexOf(student)));
                studentSpecialistDegree.setAwarded(false);
                studentSpecialistDegree.setDegree(newDegrees.get(1));
                studentSpecialistDegree.setDiplomaDate(student.getSpecialistDiplomaDate());
                studentSpecialistDegree.setDiplomaNumber(student.getSpecialistDiplomaNumber());
                studentSpecialistDegree.setThesisName(student.getSpecialistWorkThesis());
                studentSpecialistDegree.setPreviousDiplomaNumber(studentBachelorDegree.getDiplomaNumber());
                studentSpecialistDegree.setPreviousDiplomaDate(studentBachelorDegree.getDiplomaDate());

                newStudentDegrees.add(studentSpecialistDegree);
            }

            StudentDegree studentMasterDegree = new StudentDegree();
            if (student.getMasterWorkThesis() != null && !student.getMasterWorkThesis().isEmpty()) {
                studentMasterDegree.setStudent(newStudents.get(oldStudents.indexOf(student)));
                studentMasterDegree.setAwarded(false);
                studentMasterDegree.setDegree(newDegrees.get(2));
                studentMasterDegree.setDiplomaDate(student.getMasterDiplomaDate());
                studentMasterDegree.setDiplomaNumber(student.getMasterDiplomaNumber());
                studentMasterDegree.setThesisName(student.getMasterWorkThesis());
                studentMasterDegree.setPreviousDiplomaNumber(studentBachelorDegree.getDiplomaNumber());
                studentMasterDegree.setPreviousDiplomaDate(studentBachelorDegree.getDiplomaDate());
                studentMasterDegree.setThesisNameEng(student.getMasterDiplomaWorkEngName());

                newStudentDegrees.add(studentMasterDegree);
            }
        });
    }

    private static void migrateGrades() {
        oldGrades.forEach(oldGrade -> {
            Grade g = new Grade();
            newGrades.add(g);
            g.setStudent(newStudents.get(oldStudents.indexOf(oldStudents.stream().filter(student ->
                    student.getId() == oldGrade.getStudent().getId()
            ).findFirst().get())));
            g.setCourse(newCourses.get(oldSubjects.indexOf(oldSubjects.stream().filter(course ->
                    course.getId() == oldGrade.getSubject().getId()
            ).findFirst().get())));
            g.setEcts(oldGrade.getGradeECTS() == null ? "0" : oldGrade.getGradeECTS());
            g.setPoints(oldGrade.getPoints() == null ? 0 : oldGrade.getPoints());
            g.setGrade(oldGrade.getGrade() == null ? 0 : oldGrade.getGrade());
        });
    }

    private static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static Date nullDateReplacer;

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
            StudentExpel e = new StudentExpel();
            newExpels.add(e);
            e.setStudent(newStudents.get(oldStudents.indexOf(oldStudents.stream().filter(
                    s -> s.getId() == oldExpel.getStudent().getId()).findFirst().get())));
            e.setGroup(newGroups.get(oldGroups.indexOf(oldGroups.stream().filter(
                    g -> g.getId() == oldExpel.getGroup().getId()).findFirst().get())));
            //Wrong value may be used!!!
            e.setOrderDate(oldExpel.getOrderDate() == null ? nullDateReplacer : oldExpel.getOrderDate());
            e.setReason(newReasons.get(oldReasons.indexOf(oldReasons.stream().filter(
                    r -> r.getId() == oldExpel.getOrderReason().getId()).findFirst().get())));
            e.setOrderNumber(oldExpel.getOrderNumber() == null ? "0" : oldExpel.getOrderNumber());
            //Wrong value may be used!!!
            e.setApplicationDate(oldExpel.getApplicationDate() == null ? nullDateReplacer : oldExpel.getApplicationDate());
            e.setExpelDate(oldExpel.getExpelDate());
        });
    }

    private static void migrateSubjectsForGroups() {
        oldCoursesForGroups.forEach(oldCG -> {
            CourseForGroup c = new CourseForGroup();
            newCourseForGroups.add(c);
            c.setCourse(newCourses.get(oldSubjects.indexOf(oldSubjects.stream().filter(
                    subject -> equals(subject.getId(), oldCG.getSubject().getId())).findFirst().get())));
            c.setStudentGroup(newGroups.get(oldGroups.indexOf(oldGroups.stream().filter(
                    g -> g.getId() == oldCG.getGroup().getId()).findFirst().get())));
            c.setExamDate(oldCG.getExamDate());
            try {
                c.setTeacher(newTeachers.get(oldTeachers.indexOf(oldTeachers.stream().filter(teacher ->
                        oldCG.getTeacher() != null &&
                                equals(teacher.getId(), oldCG.getTeacher().getId())).findFirst().get())));
            } catch (NoSuchElementException e) {
                c.setTeacher(null);
            }
        });
    }

    private static void migrateSubjects() {
        fixSemestersForOldSubjects();
        //fixSubjectsNames();
        sortSubjects();
        oldSubjects.forEach(oldSubj -> {
            Course c = new Course();
            newCourses.add(c);
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
            c.setCourseName(courseName);
            c.setHours(oldSubj.getHours());
            if (oldSubj.getCredits() == null)
                c.setCredits(new BigDecimal(c.getHours() / 30.0));
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
        });
    }

    private static void fixSemestersForOldSubjects() {
        Set<Subject> changedSubjects = new HashSet<>();
        oldGroups.forEach(studentGroup -> {
            Set<Subject> allSubjects = studentGroup.getSubjects();
            List<Subject> newSubjects = new ArrayList<>();
            allSubjects.forEach(subject -> {
                if (!changedSubjects.contains(subject))
                    newSubjects.add(subject);
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
                    } else
                        subject.setSemester(subject.getSemester() - 4);
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
            ua.edu.chdtu.deanoffice.entity.KnowledgeControl k = new ua.edu.chdtu.deanoffice.entity.KnowledgeControl();
            newKnowledgeControlKinds.add(k);
            k.setName(oldKCKind.getName());
            k.setNameEng("");
            k.setHasGrade(oldKCKind.getGrade());
        });

        KnowledgeControl kc = new KnowledgeControl();
        kc.setHasGrade(false);
        kc.setName("практика");
        kc.setNameEng("");
        newKnowledgeControlKinds.add(kc);
    }

    private static void migrateTeachers() {
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
    }

    private static void migrateStudents() {
        oldStudents.forEach(oldStudent -> {
            ua.edu.chdtu.deanoffice.entity.Student s = new ua.edu.chdtu.deanoffice.entity.Student();
            newStudents.add(s);
            s.setActive(oldStudent.isInActive());
            s.setName(oldStudent.getName());
            s.setSurname(oldStudent.getSurname());
            s.setPatronimic(oldStudent.getPatronimic());
            s.setNameEng(oldStudent.getNameEnglish());
            s.setSurnameEng(oldStudent.getSurnameEnglish());
            s.setPatronimicEng(oldStudent.getPatronimicEnglish());
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
    }

    private static void migrateGroups() {
        oldGroups.forEach(oldGroup -> {
            StudentGroup g = new StudentGroup();
            newGroups.add(g);
            g.setName(oldGroup.getName());
            g.setActive(oldGroup.isActive());
            g.setCreationYear(oldGroup.getCreationYear());
            g.setBeginYears(oldGroup.getStudyStartYear());
            g.setTuitionTerm(oldGroup.getFirstPartOfName().startsWith("СК") ? 's' : 'r');
            g.setTuitionForm(oldGroup.getModeOfStudy() == 'з' ? 'e' : 'f');
            Integer degreeId = 0;

            if (g.getTuitionTerm() == 's') { //shortened
                if (g.getTuitionForm() == 'f') { //full-time
                    g.setStudySemesters(4);
                    g.setStudyYears(new BigDecimal(2));
                } else { //extramural
                    g.setStudySemesters(6);
                    g.setStudyYears(new BigDecimal(3));
                }
            } else if (oldGroup.getStudyStartYear() == 5 || oldGroup.getStudyStartYear() == 6) { //masters or specialists
                g.setStudySemesters(3);
                g.setStudyYears(new BigDecimal(1.5));
                if (oldGroup.getSecondPartOfName().startsWith("0")) { //masters
                    degreeId = 2;
                } else if (!oldGroup.getSpeciality().isNew()) { //specialists
                    degreeId = 1;
                }
            } else if (g.getTuitionForm() == 'e') { //extramural bachelors
                g.setStudySemesters(10);
                g.setStudyYears(new BigDecimal(5));
                degreeId = 0;
            } else { //full-time bachelors
                g.setStudySemesters(8);
                g.setStudyYears(new BigDecimal(4));
                degreeId = 0;
            }
            Degree degree = newDegrees.get(degreeId);

            try {
                if (oldGroup.getSpeciality().isNew())
                    g.setSpecialization(newSpecializations.stream().filter(specialization ->
                            (equals(oldGroup.getSpeciality().getSpecialistCode().substring(0, 3), specialization.getSpeciality().getCode()))
                                    && stringEquals(specialization.getDegree().getName(), degree.getName())
                    ).findFirst().get());
                else
                    g.setSpecialization(newSpecializations.stream().filter(specialization ->
                            (equals(oldGroup.getSpeciality().getBachelorCode(), specialization.getSpeciality().getCode()) ||
                                    equals(oldGroup.getSpeciality().getSpecialistCode(), specialization.getSpeciality().getCode()) ||
                                    equals(oldGroup.getSpeciality().getMasterCode(), specialization.getSpeciality().getCode()))
                                    && stringEquals(specialization.getDegree().getName(), degree.getName())
                    ).findFirst().get());
            } catch (NoSuchElementException e) {
                System.out.println("Exception during specialization/speciality setting for group!");
            }
        });
    }

    private static void migratePrivileges() {
        oldPrivileges.forEach(oldP -> {
            ua.edu.chdtu.deanoffice.entity.Privilege p = new ua.edu.chdtu.deanoffice.entity.Privilege();
            newPrivileges.add(p);
            p.setActive(oldP.getActive());
            p.setName(oldP.getName());
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

    private static Specialization createNewBachelorsSpecialization(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec,
                                                                   Speciality sp) {
        if (oldSpec.getSpecialistCode() != null && !oldSpec.getSpecialistCode().isEmpty()) {
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

    private static Specialization createNewMastersSpecialization(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec,
                                                                 Speciality sp) {
        if (oldSpec.getSpecialistCode() != null && !oldSpec.getSpecialistCode().isEmpty()) {
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
        if (!(oldSpec.getBachelorCode() == null || oldSpec.getBachelorCode().isEmpty())) {
            ua.edu.chdtu.deanoffice.entity.Speciality bachSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            bachSpec.setName(oldSpec.getFirstPartOfNewName(oldSpec.getBachelorName().replace("_м", "")));
            bachSpec.setNameEng("");
            bachSpec.setActive(oldSpec.isActive());
            bachSpec.setCode(oldSpec.getSpecialistCode().substring(0, 3));
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), bachSpec.getCode()))) {
                newSpecialities.add(bachSpec);
                Specialization bachSpecialization = createNewBachelorsSpecialization(oldSpec, bachSpec);
                bachSpecialization.setName(oldSpec.getSecondPartOfNewName(oldSpec.getSpecialistName()));
            } else {
                Specialization s = createNewBachelorsSpecialization(oldSpec, newSpecialities.stream().filter(speciality -> speciality.getCode()
                        .equals(oldSpec.getSpecialistCode().substring(0, 3))).findFirst().get());
                s.setName(oldSpec.getSecondPartOfNewName(oldSpec.getBachelorName()));
            }
        }
    }

    private static void createSpecialistSpeciality(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec) {
        if (!(oldSpec.getSpecialistName() == null || oldSpec.getSpecialistName().isEmpty())) {
            ua.edu.chdtu.deanoffice.entity.Speciality specialistsSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            specialistsSpec.setName(oldSpec.getSpecialistName());
            specialistsSpec.setNameEng("");
            specialistsSpec.setActive(oldSpec.isActive());
            specialistsSpec.setCode(oldSpec.getSpecialistCode());
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), specialistsSpec.getCode()))) {
                createSpecialistsSpecialization(oldSpec, specialistsSpec);
                newSpecialities.add(specialistsSpec);
            } else
                createSpecialistsSpecialization(oldSpec, newSpecialities.stream().filter(speciality -> speciality.getCode()
                        .equals(oldSpec.getSpecialistCode())).findFirst().get());
        }
    }

    private static void createMasterSpeciality(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec) {
        if (!(oldSpec.getMasterName() == null || oldSpec.getMasterName().isEmpty())) {
            ua.edu.chdtu.deanoffice.entity.Speciality masterSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            masterSpec.setName(oldSpec.getMasterName());
            masterSpec.setNameEng("");
            masterSpec.setActive(oldSpec.isActive());
            masterSpec.setCode(oldSpec.getMasterCode());
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), masterSpec.getCode()))) {
                newSpecialities.add(masterSpec);
                createMastersSpecialization(oldSpec, masterSpec);
            }
        }
    }

    private static void createNewMasterSpeciality(ua.edu.chdtu.deanoffice.oldentity.Speciality oldSpec) {
        if (!(oldSpec.getSpecialistCode() == null || oldSpec.getSpecialistCode().isEmpty())) {
            ua.edu.chdtu.deanoffice.entity.Speciality masterSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            masterSpec.setName(oldSpec.getFirstPartOfNewName(oldSpec.getSpecialistName().replace("м_", "")));
            masterSpec.setNameEng("");
            masterSpec.setActive(oldSpec.isActive());
            masterSpec.setCode(oldSpec.getSpecialistCode().substring(0, 3));
            if (newSpecialities.stream().noneMatch(
                    speciality -> equals(speciality.getCode(), masterSpec.getCode()))) {
                newSpecialities.add(masterSpec);
                Specialization masterSpecialization = createNewMastersSpecialization(oldSpec, masterSpec);
                masterSpecialization.setName(oldSpec.getSecondPartOfNewName(oldSpec.getSpecialistName()));
            } else {
                Specialization s = createNewMastersSpecialization(oldSpec, newSpecialities.stream().filter(speciality -> speciality.getCode()
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
            ua.edu.chdtu.deanoffice.entity.OrderReason r = new ua.edu.chdtu.deanoffice.entity.OrderReason();
            newReasons.add(r);
            r.setActive(oldRes.isActive());
            r.setName(oldRes.getName());
            r.setKind(oldRes.getKind());
        });
    }

    private static void migratePositions() {
        oldPositions.forEach((oPos) -> {
            ua.edu.chdtu.deanoffice.entity.Position p = new ua.edu.chdtu.deanoffice.entity.Position();
            newPositions.add(p);
            p.setName(oPos.getName());
        });
    }

    private static void migrateDepartments() {
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
    }

    private static void migrateFaculties() {
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
    }
}
