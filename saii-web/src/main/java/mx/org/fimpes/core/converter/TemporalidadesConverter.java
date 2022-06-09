package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.log4j.Logger;

import mx.org.fimpes.core.mbean.ProgramasController;
import mx.org.fimpes.saii.model.core.Temporalidades;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(forClass = Temporalidades.class, value = "temporalidadesConverter")
public class TemporalidadesConverter implements Converter<Temporalidades> {
	private static final Logger log = Logger.getLogger(TemporalidadesConverter.class);

    @Override
    public Temporalidades getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        var controller = (ProgramasController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "programasController");
        return controller.getTemporalidadById(getKey(value));
    }

    Integer getKey(String value) {
        var key = Integer.parseInt(value);
        return key;
    }

    String getStringKey(Integer value) {
        return value.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Temporalidades object) {
        if (object == null) {
            return null;
        }
        return getStringKey(object.getIdTemporalidad());
    }
}
