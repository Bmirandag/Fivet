package cl.ucn.disc.isof.fivet.domain.model;

import com.durrutia.ebean.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Bryan Miranda on 12/19/2016.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Control extends BaseModel {

    /**
     * Persona que ha realizado el control
     */
    @Getter
    @Column
    @ManyToOne
    private Persona veterinario;

    /**
     * Id del control
     */
    @Getter
    @Column
    private String Identificador;

    /**
     * Fecha del proximo control
     */
    @Getter
    @Column
    private Date fechaProximoControl;

    /**
     * Fecha de realizacion del control
     */
    @Getter
    @Column
    private Date fecha;

    /**
     * Peso del paciente
     */
    @Getter
    @Column
    private Integer peso;

    /**
     * Temperatura del paciente
     */
    @Getter
    @Column
    private Integer temperatura;
}
