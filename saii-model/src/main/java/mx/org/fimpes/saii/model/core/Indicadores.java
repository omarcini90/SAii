package mx.org.fimpes.saii.model.core;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Entity
@Table(name = "indicadores")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Indicadores.findByEtiqueta", query = "SELECT i FROM Indicadores i WHERE i.etiqueta = :etiqueta")})
public class Indicadores implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_indicador")
    private Integer idIndicador;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "etiqueta")
    private String etiqueta;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "descripcion")
    private String descripcion;
    @Lob
    @Size(max = 65535)
    @Column(name = "explicacion")
    private String explicacion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requerido")
    private boolean requerido;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "tipo_dato")
    private String tipoDato;
    @Basic(optional = false)
    @NotNull
    @Column(name = "es_operador")
    private boolean esOperador;
    @Column(name = "id_indicador_total")
    private Integer idIndicadorTotal;
    @Basic(optional = false)
    @NotNull
    @Column(name = "activo")
    private boolean activo;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha_actualizacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaActualizacion;
    
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idIndicador")
    private List<RegistroIndicadores> registroIndicadoresList;
    
    @JsonManagedReference
    @JoinColumn(name = "id_seccion", referencedColumnName = "id_seccion")
    @ManyToOne(optional = false)
    private Seccion idSeccion;
    
    @JsonManagedReference
    @JoinColumn(name = "id_tipo", referencedColumnName = "id_tipo")
    @ManyToOne(optional = false)
    private TipoIndicador idTipo;

    public Indicadores() {
    }

    public Indicadores(Integer idIndicador) {
        this.idIndicador = idIndicador;
    }

    public Indicadores(Integer idIndicador, String etiqueta, String descripción, boolean requerido, boolean esOperador, boolean activo, Date fechaActualizacion) {
        this.idIndicador = idIndicador;
        this.etiqueta = etiqueta;
        this.descripcion = descripción;
        this.requerido = requerido;
        this.esOperador = esOperador;
        this.activo = activo;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getIdIndicador() {
        return idIndicador;
    }

    public void setIdIndicador(Integer idIndicador) {
        this.idIndicador = idIndicador;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getExplicacion() {
        return explicacion;
    }

    public void setExplicacion(String explicacion) {
        this.explicacion = explicacion;
    }

    public boolean getRequerido() {
        return requerido;
    }

    public void setRequerido(boolean requerido) {
        this.requerido = requerido;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public boolean getEsOperador() {
        return esOperador;
    }

    public void setEsOperador(boolean esOperador) {
        this.esOperador = esOperador;
    }

    public Integer getIdIndicadorTotal() {
        return idIndicadorTotal;
    }

    public void setIdIndicadorTotal(Integer idIndicadorTotal) {
        this.idIndicadorTotal = idIndicadorTotal;
    }

    public boolean getActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Date getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Date fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @XmlTransient
    public List<RegistroIndicadores> getRegistroIndicadoresList() {
        return registroIndicadoresList;
    }

    public void setRegistroIndicadoresList(List<RegistroIndicadores> registroIndicadoresList) {
        this.registroIndicadoresList = registroIndicadoresList;
    }

    public Seccion getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(Seccion idSeccion) {
        this.idSeccion = idSeccion;
    }

    public TipoIndicador getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(TipoIndicador idTipo) {
        this.idTipo = idTipo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idIndicador != null ? idIndicador.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Indicadores)) {
            return false;
        }
        Indicadores other = (Indicadores) object;
        if ((this.idIndicador == null && other.idIndicador != null) || (this.idIndicador != null && !this.idIndicador.equals(other.idIndicador))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idIndicador + ": " + etiqueta + " (Sección: " + idSeccion.getDescripcion() + ")";
    }
    
}