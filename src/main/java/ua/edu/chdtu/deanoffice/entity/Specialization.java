package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.NameWithEngAndActiveEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Setter
@Getter
//@Table(uniqueConstraints = {
//        @UniqueConstraint(columnNames = {"name", "speciality_id", "degree_id"})
//})
public class Specialization extends NameWithEngAndActiveEntity {
    @ManyToOne
    @JoinColumn(name = "speciality_id", nullable = false)
    private Speciality speciality;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Degree degree;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Faculty faculty;
    @ManyToOne
    private Department department;
    @Column(name = "qualification", length = 100)
    private String qualification;
    @Column(name = "qualification_eng", length = 100)
    private String qualificationEng;
    @Column(name = "payment_fulltime", precision = 15, scale = 2)
    private BigDecimal paymentFulltime;
    @Column(name = "payment_extramural", precision = 15, scale = 2)
    private BigDecimal paymentExtramural;
    @Column(name = "program_head_name", nullable = false)
    private String educationalProgramHeadName;
    @Column(name = "program_head_name_eng", nullable = false)
    private String educationalProgramHeadNameEng;
    @Column(name = "program_head_info", nullable = false)
    private String educationalProgramHeadInfo;
    @Column(name = "program_head_info_eng", nullable = false)
    private String educationalProgramHeadInfoEng;
    @Column(name = "knowledge_and_understanding_outcomes", columnDefinition = "character varying(1200)", length = 1200)
    private String knowledgeAndUnderstandingOutcomes;
    @Column(name = "knowledge_and_understanding_outcomes_eng", columnDefinition = "character varying(1200)", length = 1200)
    private String knowledgeAndUnderstandingOutcomesEng;
    @Column(name = "applying_knowledge_and_understanding_outcomes", columnDefinition = "character varying(1200)", length = 1200)
    private String applyingKnowledgeAndUnderstandingOutcomes;
    @Column(name = "applying_knowledge_and_understanding_outcomes_eng", columnDefinition = "character varying(1200)", length = 1200)
    private String applyingKnowledgeAndUnderstandingOutcomesEng;
    @Column(name = "making_judgements_outcomes", columnDefinition = "character varying(1200)", length = 1200)
    private String makingJudgementsOutcomes;
    @Column(name = "making_judgements_outcomes_eng", columnDefinition = "character varying(1200)", length = 1200)
    private String makingJudgementsOutcomesEng;
    @Column(name = "certificate_number", nullable = false, length = 20, columnDefinition = "varchar(20) default ''")
    private String certificateNumber;
    @Temporal(TemporalType.DATE)
    @Column(name = "certificate_date", nullable = false, columnDefinition = "date default '1980-01-01'")
    private Date certificateDate;

    public Specialization() {
        educationalProgramHeadName = "";
        educationalProgramHeadNameEng = "";
        educationalProgramHeadInfo = "";
        educationalProgramHeadInfoEng = "";
        certificateNumber = "";
        try {
            certificateDate = new SimpleDateFormat("yyyy-MM-dd").parse("1980-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
