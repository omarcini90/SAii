package mx.org.fimpes.saii.model.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Entity
@Table(name = "seccion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Seccion.findByDescripcion", query = "SELECT s FROM Seccion s WHERE s.descripcion = :descripcion")})
public class Seccion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_seccion")
    private Integer idSeccion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "descripcion")
    private String descripcion;
    
    @JsonBackReference
    @JoinColumn(name = "id_tipo", referencedColumnName = "id_tipo")
    @ManyToOne(optional = false)
    private TipoIndicador idTipo;
    
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idSeccion")
    private List<Indicadores> indicadoresList;
    
    @Transient
    private List<RegistroIndicadores> registroList;
    @Transient
    private int totalIndicadoresRequeridos;
    @Transient
    private int totalIndicadoresCompletados;

    public Seccion() {
    }

    public Seccion(Integer idSeccion) {
        this.idSeccion = idSeccion;
    }

    public Seccion(Integer idSeccion, String descripcion) {
        this.idSeccion = idSeccion;
        this.descripcion = descripcion;
    }

    public Integer getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(Integer idSeccion) {
        this.idSeccion = idSeccion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoIndicador getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(TipoIndicador idTipo) {
        this.idTipo = idTipo;
    }

    @XmlTransient
    public List<Indicadores> getIndicadoresList() {
        return indicadoresList;
    }

    public void setIndicadoresList(List<Indicadores> indicadoresList) {
        this.indicadoresList = indicadoresList;
    }

    public List<RegistroIndicadores> getRegistroList() {
        return registroList;
    }

    public void setRegistroList(List<RegistroIndicadores> registroList) {
        this.registroList = registroList;
    }
    
	public int getTotalIndicadoresRequeridos() {
		return totalIndicadoresRequeridos;
	}

	public void setTotalIndicadoresRequeridos(int totalIndicadoresRequeridos) {
		this.totalIndicadoresRequeridos = totalIndicadoresRequeridos;
	}

	public int getTotalIndicadoresCompletados() {
		return totalIndicadoresCompletados;
	}

	public void setTotalIndicadoresCompletados(int totalIndicadoresCompletados) {
		this.totalIndicadoresCompletados = totalIndicadoresCompletados;
	}

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idSeccion != null ? idSeccion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Seccion)) {
            return false;
        }
        Seccion other = (Seccion) object;
        if ((this.idSeccion == null && other.idSeccion != null) || (this.idSeccion != null && !this.idSeccion.equals(other.idSeccion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return idSeccion + " - " + descripcion;
    }
    
}
