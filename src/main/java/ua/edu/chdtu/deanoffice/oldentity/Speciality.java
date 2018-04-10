package ua.edu.chdtu.deanoffice.oldentity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "SPECIALITIES")
public class Speciality {
    @Id
    @Column(name = "ID")
    private int id;

    @Column(name = "NAME")
    private String specialistName;

    @Column(name = "NAME_BACH")
    private String bachelorName;

    @Column(name = "NAME_MASTER")
    private String masterName;

    @ManyToOne
    @JoinColumn(name = "KAFEDRA_ID")
    private Cathedra cathedra;

    @OneToMany(mappedBy = "speciality")
    private Set<Subject> subjects;

    @ManyToOne
    @JoinColumn(name = "FAKULTET_ID")
    private Department department;

    @Column(name = "ACTIVE1")
    @Type(type = "true_false")
    private boolean active;

    @Column(name = "SHIFR")
    private String specialistCode;

    @Column(name = "SHIFR_BACH")
    private String bachelorCode;

    @Column(name = "SHIFR_MASTER")
    private String masterCode;

    public boolean isNew() {
        return !(getSpecialistCode().contains(".") ||
                getBachelorCode().contains(".") ||
                getMasterCode().contains("."));
    }

    public boolean isOld() {
        return !isNew();
    }

    public String getFirstPartOfNewName(String name) {
        if (isNew() && name != null) {
            //Unique exception ІТ-проектами
            if (name.contains("ІТ")) {
                return name;
            }
            String result = "";
            name = name.trim();
            String[] words = name.split(" ");
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (!word.toLowerCase().equals(word) && i > 0) {
                    break;
                }
                result += word + " ";
            }
            return result.trim();
        } else {
            return name;
        }
    }

    public String getSecondPartOfNewName(String name) {
        if (isNew() && name != null) {
            //Unique exceptions
            if (name.contains(" ІТ")) {
                return "";
            }
            if (name.contains("Кібербезпека")) {
                return "Кібербезпека";
            }
            if (name.contains("Автоматизація та комп'ютерно-інтегровані технології")) {
                return "Автоматизація та комп'ютерно-інтегровані технології";
            }
            if (name.contains("Інформаційні системи та технології")) {
                return "Інформаційні системи та технології";
            }
            String result = "";
            name = name.trim();
            String[] words = name.split(" ");
            int firstWordIndex = 0;
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if (firstWordIndex != 0 && firstWordIndex < i) {
                    result += word + " ";
                    continue;
                }
                if (!word.toLowerCase().equals(word)
                        && i > 0) {
                    firstWordIndex = i;
                    result += word + " ";
                }
            }
            return result.trim();
        } else {
            return name;
        }
    }

    public String getSpecialityNameFromNew(String name) {
        if (isNew()) {
            if (specialistCode.contains("-")) {
                return "";
            } else {
                return getFirstPartOfNewName(name);
            }
        } else {
            return name;
        }
    }

    public String getSpecializationNameFromNew(String name) {
        if (isNew()) {
            if (specialistCode.contains("-")) {
                return name;
            } else {
                return getSecondPartOfNewName(name);
            }
        } else {
            return name;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSpecialistName() {
        return specialistName;
    }

    public void setSpecialistName(String name) {
        this.specialistName = name;
    }

    public String getBachelorName() {
        return bachelorName;
    }

    public void setBachelorName(String bachelorName) {
        this.bachelorName = bachelorName;
    }

    public Cathedra getCathedra() {
        return cathedra;
    }

    public void setCathedra(Cathedra cathedra) {
        this.cathedra = cathedra;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSpecialistCode() {
        return specialistCode;
    }

    public void setSpecialistCode(String code) {
        this.specialistCode = code;
    }

    public String getMasterName() {
        if (masterName == null) {
            return "";
        } else {
            return masterName;
        }
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getBachelorCode() {
        return bachelorCode;
    }

    public void setBachelorCode(String bachelorCode) {
        this.bachelorCode = bachelorCode;
    }

    public String getMasterCode() {
        if (masterCode == null) {
            return "";
        } else {
            return masterCode;
        }
    }

    public void setMasterCode(String masterCode) {
        this.masterCode = masterCode;
    }
}
