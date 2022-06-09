package mx.org.fimpes.saii.ejb.core;

import java.math.BigInteger;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;

import com.ibm.icu.math.BigDecimal;

import mx.org.fimpes.saii.ejb.AbstractFacade;
import mx.org.fimpes.saii.exceptions.ValidationException;
import mx.org.fimpes.saii.model.core.Campus;
import mx.org.fimpes.saii.model.core.Indicadores;
import mx.org.fimpes.saii.model.core.ProcesoCaptura;
import mx.org.fimpes.saii.model.core.Programas;
import mx.org.fimpes.saii.model.core.RegistroIndicadores;
import mx.org.fimpes.saii.model.core.Seccion;
import mx.org.fimpes.saii.util.MxDateTime;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@Stateless
@LocalBean
public class RegistroIndicadoresFacade extends AbstractFacade<RegistroIndicadores> {
	private static final Logger log = Logger.getLogger(RegistroIndicadoresFacade.class);

	@PersistenceContext(unitName = "SAiiPersistanceUnit")
	private EntityManager em;
	@EJB
	private SeccionFacade secciones;
    @EJB
    private IndicadoresFacade indicadoresFacade;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public RegistroIndicadoresFacade() {
		super(RegistroIndicadores.class);
	}

	@Override
	public void create(RegistroIndicadores indicador) {
		indicador.setFechaActualizacion(MxDateTime.date());
		super.create(indicador);
		log.info("Se creó indicador " + indicador.getIdInstitucion().getSiglas() + " " + indicador.getAnio() + " " + indicador.getIdCiclo().getIdTemporalidad().getDescripcion() + " " + indicador.getIdIndicador().getIdSeccion().getDescripcion());
	}

	@Override
	public void edit(RegistroIndicadores indicador) {
		     log.info("ANTES DE ACTUALIZAR");
			  log.info(MxDateTime.date());
			  log.info("Se Actualizara indicador " 
			  + indicador.getIdInstitucion().getSiglas() 
		+ " " + indicador.getAnio()
		+ " " + indicador.getIdCiclo().getIdTemporalidad().getDescripcion() 
		+ " " + indicador.getIdIndicador().getIdSeccion().getDescripcion()
			  );
		indicador.setFechaActualizacion(MxDateTime.date());
		super.edit(indicador);
		log.info("REGISTRO ACTUALIZADO RDM");

	}

	// CASO: INSTITUCIÓN
	public boolean isInstituteInitialized(ProcesoCaptura proceso) {
		var builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = builder.createQuery();
		Root<RegistroIndicadores> root = cq.from(RegistroIndicadores.class);

		cq.select(builder.count(root))
				.where(builder.and(builder.equal(root.get("idInstitucion"), proceso.getIdInstitucion()),
						builder.equal(root.get("idCiclo"), proceso.getIdCiclo()),
						builder.equal(root.get("anio"), proceso.getAnio()), 
						builder.isNull(root.get("idCampus")),
						builder.isNull(root.get("idPrograma")))
		);

		Query q = getEntityManager().createQuery(cq);
		int r = ((Long) q.getSingleResult()).intValue();
		
		return r > 0;
	}

	public List<RegistroIndicadores> findByInstitute(ProcesoCaptura proceso, List<Indicadores> indicadores) {
		var builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RegistroIndicadores> cq = builder.createQuery(RegistroIndicadores.class);
		Root<RegistroIndicadores> root = cq.from(RegistroIndicadores.class);
		// Indicadores de institución:
		cq.select(root)
				.where(builder.and(builder.equal(root.get("idInstitucion"), proceso.getIdInstitucion()),
						builder.equal(root.get("idCiclo"), proceso.getIdCiclo()),
						builder.equal(root.get("anio"), proceso.getAnio()),
						builder.isNull(root.get("idCampus")),
						builder.isNull(root.get("idPrograma")),
						root.get("idIndicador").in(indicadores))
		);

		TypedQuery<RegistroIndicadores> q = getEntityManager().createQuery(cq);
		var l = q.getResultList();
		log.info("findByInstitute: " + l.size());

		return l;
	}
	
	public RegistroIndicadores findByInstitute(ProcesoCaptura proceso, Integer indicador) {
		var builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RegistroIndicadores> cq = builder.createQuery(RegistroIndicadores.class);
		Root<RegistroIndicadores> root = cq.from(RegistroIndicadores.class);
		cq.select(root)
				.where(builder.and(builder.equal(root.get("idInstitucion"), proceso.getIdInstitucion()),
						builder.equal(root.get("idCiclo"), proceso.getIdCiclo()),
						builder.equal(root.get("anio"), proceso.getAnio()),
						builder.isNull(root.get("idCampus")),
						builder.isNull(root.get("idPrograma")),
						builder.equal(root.get("idIndicador").get("idIndicador"), indicador))
		);

	    TypedQuery<RegistroIndicadores> q = getEntityManager().createQuery(cq);
		var r = q.getSingleResult();
		log.info("findByInstitute: " + r);

		return r;
	}

	// CASO: CAMPUS
	public boolean isCampusInitialized(ProcesoCaptura proceso, Campus campus) {
		var builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = builder.createQuery();
		Root<RegistroIndicadores> root = cq.from(RegistroIndicadores.class);

		cq.select(builder.count(root))
				.where(builder.and(builder.equal(root.get("idInstitucion"), proceso.getIdInstitucion()),
						builder.equal(root.get("idCiclo"), proceso.getIdCiclo()),
						builder.equal(root.get("anio"), proceso.getAnio()), 
						builder.equal(root.get("idCampus"), campus),
						builder.isNull(root.get("idPrograma")))
		);

		Query q = getEntityManager().createQuery(cq);
		int r = ((Long) q.getSingleResult()).intValue();

		return r > 0;
	}

	public List<RegistroIndicadores> findByCampus(ProcesoCaptura proceso, Campus campus, List<Indicadores> indicadores) {
		var builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RegistroIndicadores> cq = builder.createQuery(RegistroIndicadores.class);
		Root<RegistroIndicadores> root = cq.from(RegistroIndicadores.class);
		// Indicadores de campus:
		cq.select(root)
			.where(builder.and(builder.equal(root.get("idInstitucion"), proceso.getIdInstitucion()),
				builder.equal(root.get("idCiclo"), proceso.getIdCiclo()),
				builder.equal(root.get("anio"), proceso.getAnio()), 
				builder.equal(root.get("idCampus"), campus),
				builder.isNull(root.get("idPrograma")), 
				root.get("idIndicador").in(indicadores))
		);

		TypedQuery<RegistroIndicadores> q = getEntityManager().createQuery(cq);
		var l = q.getResultList();

		return l;
	}
	
	public RegistroIndicadores findByCampus(ProcesoCaptura proceso, Campus campus, Integer indicador) {
		var builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RegistroIndicadores> cq = builder.createQuery(RegistroIndicadores.class);
		Root<RegistroIndicadores> root = cq.from(RegistroIndicadores.class);
		cq.select(root)
				.where(builder.and(builder.equal(root.get("idInstitucion"), proceso.getIdInstitucion()),
						builder.equal(root.get("idCiclo"), proceso.getIdCiclo()),
						builder.equal(root.get("anio"), proceso.getAnio()),
						builder.equal(root.get("idCampus"), campus),
						builder.isNull(root.get("idPrograma")), 
						builder.equal(root.get("idIndicador").get("idIndicador"), indicador))
		);

	    TypedQuery<RegistroIndicadores> q = getEntityManager().createQuery(cq);
		var r = q.getSingleResult();

		return r;
	}

	// CASO: PROGRAMAS
	public boolean isProgramInitialized(ProcesoCaptura proceso, Programas programa) {
		var builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery cq = builder.createQuery();
		Root<RegistroIndicadores> root = cq.from(RegistroIndicadores.class);

		cq.select(builder.count(root))
				.where(builder.and(builder.equal(root.get("idInstitucion"), proceso.getIdInstitucion()),
						builder.equal(root.get("idCiclo"), proceso.getIdCiclo()),
						builder.equal(root.get("anio"), proceso.getAnio()),
						builder.equal(root.get("idPrograma"), programa),
						builder.equal(root.get("idCampus"), programa.getIdCampus()))
		);

		Query q = getEntityManager().createQuery(cq);
		int r = ((Long) q.getSingleResult()).intValue();

		return r > 0;
	}

	public List<RegistroIndicadores> findByProgram(ProcesoCaptura proceso, Programas programa, List<Indicadores> indicadores) {
		var builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RegistroIndicadores> cq = builder.createQuery(RegistroIndicadores.class);
		Root<RegistroIndicadores> root = cq.from(RegistroIndicadores.class);
		// Indicadores de programa:
		cq.select(root)
				.where(builder.and(builder.equal(root.get("idInstitucion"), proceso.getIdInstitucion()),
						builder.equal(root.get("idCiclo"), proceso.getIdCiclo()),
						builder.equal(root.get("anio"), proceso.getAnio()),
						builder.equal(root.get("idCampus"), programa.getIdCampus()),
						builder.equal(root.get("idPrograma"), programa),
						root.get("idIndicador").in(indicadores))
		);

		TypedQuery<RegistroIndicadores> q = getEntityManager().createQuery(cq);
		var l = q.getResultList();
		log.info("findByPrograma: " + l.size());

		return l;
	}
	
	public RegistroIndicadores findByProgram(ProcesoCaptura proceso, Programas programa, Integer indicador) {
		var builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<RegistroIndicadores> cq = builder.createQuery(RegistroIndicadores.class);
		Root<RegistroIndicadores> root = cq.from(RegistroIndicadores.class);
		cq.select(root)
				.where(builder.and(builder.equal(root.get("idInstitucion"), proceso.getIdInstitucion()),
						builder.equal(root.get("idCiclo"), proceso.getIdCiclo()),
						builder.equal(root.get("anio"), proceso.getAnio()),
						builder.equal(root.get("idCampus"), programa.getIdCampus()),
						builder.equal(root.get("idPrograma"), programa),
						builder.equal(root.get("idIndicador").get("idIndicador"), indicador))
		);

	    TypedQuery<RegistroIndicadores> q = getEntityManager().createQuery(cq);
		var r = q.getSingleResult();
		log.info("findByPrograma: " + r);

		return r;
	}

	// AVANCE:
	public List<Seccion> getProgress(ProcesoCaptura proceso) {
		List<Seccion> p = secciones.findAll();
		Query q = getEntityManager().createNativeQuery("SELECT s.id_seccion, count(s.id_seccion) "
				+ "FROM registro_indicadores r INNER JOIN ( "
				+ " SELECT i.id_indicador, s.id_seccion, s.descripcion FROM indicadores i "
				+ " LEFT JOIN seccion s ON i.id_seccion = s.id_seccion WHERE i.requerido IS true "
				+ ") AS s ON r.id_indicador = s.id_indicador WHERE r.id_institucion = :idInstitucion "
				+ "AND r.id_ciclo = :idCiclo AND r.anio = :anio GROUP BY s.id_seccion;");
		q.setParameter("idInstitucion", proceso.getIdInstitucion().getIdInstitucion());
		q.setParameter("idCiclo", proceso.getIdCiclo().getIdCiclo());
		q.setParameter("anio", proceso.getAnio());
		List<Object[]> r = q.getResultList();
		
		p.stream().forEach(s -> {
			for(Object[] o : r) {
				if(s.getIdSeccion() == (Integer)o[0]) {
					s.setTotalIndicadoresRequeridos(((BigInteger)o[1]).intValue());
					break;
				}
			}
		});

		q = getEntityManager().createNativeQuery("SELECT s.id_seccion, count(s.id_seccion) "
				+ "FROM registro_indicadores r INNER JOIN ( "
				+ " SELECT i.id_indicador, s.id_seccion, s.descripcion FROM indicadores i "
				+ " LEFT JOIN seccion s ON i.id_seccion = s.id_seccion WHERE i.requerido IS true "
				+ ") AS s ON r.id_indicador = s.id_indicador WHERE r.id_institucion = :idInstitucion "
				+ "AND r.id_ciclo = :idCiclo AND r.anio = :anio AND r.valor IS NOT NULL GROUP BY s.id_seccion;");
		q.setParameter("idInstitucion", proceso.getIdInstitucion().getIdInstitucion());
		q.setParameter("idCiclo", proceso.getIdCiclo().getIdCiclo());
		q.setParameter("anio", proceso.getAnio());
		List<Object[]> c = q.getResultList();
		
		p.stream().forEach(s -> {
			for(Object[] o : c) {
				if(s.getIdSeccion() == (Integer)o[0]) {
					s.setTotalIndicadoresCompletados(((BigInteger)o[1]).intValue());
					break;
				}
			}
		});

		return p;
	}
	
	// VALIDACIÓN DE OPERANDOS:
	public void validateSumOfOperands(ProcesoCaptura proceso, RegistroIndicadores operador) throws Exception {
		Indicadores o = operador.getIdIndicador();
		List<Indicadores> ops = indicadoresFacade.findOperands(o.getIdIndicadorTotal());
		RegistroIndicadores total;
		List<RegistroIndicadores> operands;
		switch (o.getIdTipo().getIdTipo()) {
        	case 1:
        		// INSTITUCIÓN:
        		total = findByInstitute(proceso, o.getIdIndicadorTotal());
        		operands = findByInstitute(proceso, ops);
        		break;
        	case 2:
        		// CAMPUS:
        		total = findByCampus(proceso, operador.getIdCampus(), o.getIdIndicadorTotal());
        		operands = findByCampus(proceso, operador.getIdCampus(), ops);
        		break;
        	case 3:
        		// PROGRAMA:
        		total = findByProgram(proceso, operador.getIdPrograma(), o.getIdIndicadorTotal());
        		operands = findByProgram(proceso, operador.getIdPrograma(), ops);
        		break;
        	default:
        		throw new ValidationException("El tipo de indicador no existe dentro del sistema");
		}
		
		if(total.getValor() != null) {	
			var t = new BigDecimal(total.getValor());
			var v = new BigDecimal(operador.getValor());
			var sum = v;
			
			// Si sum > t:
			if(sum.compareTo(t) > 0) {
				throw new ValidationException("La cantidad excede el Total del indicador No. " + o.getIdIndicadorTotal());
			}else {
				boolean completed = true;
				for(RegistroIndicadores ro : operands) {
					if(!ro.equals(operador)) {
						if(ro.getValor() != null) {
							var s = new BigDecimal(ro.getValor());
							// Si s == 0:
							if(s.compareTo(BigDecimal.ZERO) == 0) {
								completed = false;
							}
							// sum += s:
							sum = sum.add(s);
						}else {
							completed = false;
						}
					}
				}
				// Si sum > t:
				if(completed && sum.compareTo(t) > 0) {
					throw new ValidationException("La suma excede el Total del indicador No. " + o.getIdIndicadorTotal());
				}else {
					// Si v != 0 y sum < t:
					if(v.compareTo(BigDecimal.ZERO) > 0 && completed && sum.compareTo(t) < 0) {
						throw new ValidationException("La suma es menor al Total del indicador No. " + o.getIdIndicadorTotal());
					}
				}
			}			
		}else {
			throw new ValidationException("Primero deberá digitar la cantidad Total en el indicador No. " + o.getIdIndicadorTotal());
		}
	}
}
