package ua.edu.chdtu.deanoffice.entity;

import lombok.Getter;
import lombok.Setter;
import ua.edu.chdtu.deanoffice.entity.superclasses.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "student_expel")
public class StudentExpel extends BaseEntity {
    @ManyToOne
    @JoinColumn(nullable = false, name = "student_degree_id")
    private StudentDegree studentDegree;
    @Column(name = "expel_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date expelDate;
    @Column(name = "order_number", nullable = false, length = 15)
    private String orderNumber;
    @Column(name = "order_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date orderDate;
    @ManyToOne
    @JoinColumn(nullable = false, name = "order_reason_id")
    private OrderReason orderReason;
    @Column(name = "application_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date applicationDate;
}
