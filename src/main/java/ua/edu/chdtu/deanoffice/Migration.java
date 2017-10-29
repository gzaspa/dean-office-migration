package ua.edu.chdtu.deanoffice;

import com.sun.istack.internal.NotNull;
import ua.edu.chdtu.deanoffice.entity.*;
import ua.edu.chdtu.deanoffice.oldentity.*;
import ua.edu.chdtu.deanoffice.oldentity.Department;
import ua.edu.chdtu.deanoffice.oldentity.OrderReason;
import ua.edu.chdtu.deanoffice.oldentity.Position;
import ua.edu.chdtu.deanoffice.oldentity.Speciality;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static ua.edu.chdtu.deanoffice.DatabaseConnector.getFirebirdSession;
import static ua.edu.chdtu.deanoffice.DatabaseConnector.getPostgresSession;

public class Migration {

    private static String makeAbbreviation(String s) {
        return s.replaceAll("\\B.|\\P{L}", "").toUpperCase();
    }

    private static void saveAllItems(@NotNull List<? extends BaseEntity> entities) {
        entities.forEach(e -> getPostgresSession().save(e));
    }

    private static boolean stringEquals(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return false;
        } else return s1.equals(s2);
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
            f.setAbbr(oFaculty.getAbbreviation() == null ? makeAbbreviation(f.getName()) + "*"
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
            d.setAbbr(oDep.getAbbreviation() == null ? makeAbbreviation(oDep.getName()) + "*" + oDep.getId() : oDep.getAbbreviation());
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
                    speciality -> stringEquals(speciality.getCode(), bachSpec.getCode()))) {
                newSpecialities.add(bachSpec);
            }

            ua.edu.chdtu.deanoffice.entity.Speciality specialistsSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
            specialistsSpec.setName(oldSpec.getName());
            specialistsSpec.setNameEng("");
            specialistsSpec.setActive(oldSpec.isActive());
            specialistsSpec.setCode(oldSpec.getCode());
            if (newSpecialities.stream().noneMatch(
                    speciality -> stringEquals(speciality.getCode(), specialistsSpec.getCode()))) {
                newSpecialities.add(specialistsSpec);
            }

            if (!(oldSpec.getMasterName() == null || oldSpec.getMasterName().isEmpty())) {
                ua.edu.chdtu.deanoffice.entity.Speciality masterSpec = new ua.edu.chdtu.deanoffice.entity.Speciality();
                masterSpec.setName(oldSpec.getMasterName());
                masterSpec.setNameEng("");
                masterSpec.setActive(oldSpec.isActive());
                masterSpec.setCode(oldSpec.getMasterCode());
                if (newSpecialities.stream().noneMatch(
                        speciality -> stringEquals(speciality.getCode(), masterSpec.getCode()))) {
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
                bachSpec.setQualification("Бакалавр");
                bachSpec.setQualificationEng("Bachelor");
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
                masterSpec.setQualification("Магістр");
                masterSpec.setQualificationEng("Master");
            }
        });
        saveAllItems(newSpecializations);

        //Groups
//        List<Group> oldGroups = getFirebirdSession().createQuery("from Group", Group.class).list();
//        List<StudentGroup> newGroups = new ArrayList<>();
//        oldGroups.forEach(oldGroup -> {
//            StudentGroup g = new StudentGroup();
//            newGroups.add(g);
//            g.setName(oldGroup.getName());
//            g.setActive(oldGroup.isActive());
//            g.setCreationYear(oldGroup.getCreationYear());
//            g.setSpecialization();
//            g.setStudySemesters(oldGroup.get);
//            g.setStudyYears();
//            g.setTuitionForm();
//            g.setTuitionTerm();
//        });


        //Students
    }
}
