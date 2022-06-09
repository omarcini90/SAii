package mx.org.fimpes.saii.model.reports;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
public enum Report {
	AVANCE,
	INSTITUCION,
	CAMPUS,	
	VINCULACION,
	PROGRAMAS,
	DOCENTES,
	PROCESO,
    RESULTADOS;
    
    public String jasper() {
    	return this.toString().concat(".jasper");
    }
}
