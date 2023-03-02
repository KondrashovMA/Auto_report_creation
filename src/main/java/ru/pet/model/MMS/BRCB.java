package ru.pet.model.MMS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "brcb")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BRCB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String alarm;

    @Column
    private Timestamp date;

    @Column
    private String ld;

    @Column
    private String path;

    @Column
    private String status;

    @Column
    private String value;

    @Column
    private String vied;
}

