package mx.org.fimpes.saii.model.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Entity
@Table(name = "registro_indicadores")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RegistroIndicadores.findByAnio", query = "SELECT r FROM RegistroIndicadores r WHERE r.anio = :anio")})
public class RegistroIndicadores implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_registro")
    private Integer idRegistro;
    @Basic(optional = false)
    @NotNull
    @Column(name = "anio")
    private short anio;
    @Size(min = 1, max = 45)
    @Column(name = "valor")
    private String valor;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_actualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;
    
    @JsonManagedReference
    @JoinColumn(name = "id_ciclo", referencedColumnName = "id_ciclo")
    @ManyToOne(optional = false)
    private Ciclos idCiclo;
    
    @JsonBackReference
    @JoinColumn(name = "id_indicador", referencedColumnName = "id_indicador")
    @ManyToOne(optional = false)
    private Indicadores idIndicador;
    
    @JsonManagedReference
    @JoinColumn(name = "id_institucion", referencedColumnName = "id_institucion")
    @ManyToOne(optional = false)
    private Instituciones idInstitucion;
    
    @JsonManagedReference
    @JoinColumn(name = "id_campus", referencedColumnName = "id_campus")
    @ManyToOne
    private Campus idCampus;
    
    @JsonManagedReference
    @JoinColumn(name = "id_programa", referencedColumnName = "id_programa")
    @ManyToOne
    private Programas idPrograma;

    public RegistroIndicadores() {
    }

    public RegistroIndicadores(Integer idRegistro) {
        this.idRegistro = idRegistro;
    }

    public RegistroIndicadores(ProcesoCaptura proceso, Indicadores indicador) {
        this.idInstitucion = proceso.getIdInstitucion();
        this.idCiclo = proceso.getIdCiclo();
        this.anio = proceso.getAnio();
        this.idIndicador = indicador;
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Integer idRegistro) {
        this.idRegistro = idRegistro;
    }

    public short getAnio() {
        return anio;
    }

    public void setAnio(short anio) {
        this.anio = anio;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Campus getIdCampus() {
        return idCampus;
    }

    public void setIdCampus(Campus idCampus) {
        this.idCampus = idCampus;
    }

    public Ciclos getIdCiclo() {
        return idCiclo;
    }

    public void setIdCiclo(Ciclos idCiclo) {
        this.idCiclo = idCiclo;
    }

    public Indicadores getIdIndicador() {
        return idIndicador;
    }

    public void setIdIndicador(Indicadores idIndicador) {
        this.idIndicador = idIndicador;
    }

    public Instituciones getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(Instituciones idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public Programas getIdPrograma() {
        return idPrograma;
    }

    public void setIdPrograma(Programas idPrograma) {
        this.idPrograma = idPrograma;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idRegistro != null ? idRegistro.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RegistroIndicadores)) {
            return false;
        }
        RegistroIndicadores other = (RegistroIndicadores) object;
        if ((this.idRegistro == null && other.idRegistro != null) || (this.idRegistro != null && !this.idRegistro.equals(other.idRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idIndicador.getEtiqueta() + " [R= " + valor + "]";
    }
    
}
