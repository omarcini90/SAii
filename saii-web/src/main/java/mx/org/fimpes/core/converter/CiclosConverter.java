package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import mx.org.fimpes.core.mbean.PeriodoAccesoController;
import mx.org.fimpes.saii.model.core.Ciclos;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(forClass = Ciclos.class)
public class CiclosConverter implements Converter<Ciclos>{
    @Override
    public Ciclos getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        var controller = (PeriodoAccesoController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "periodoAccesoController");
        return controller.getCicloById(getKey(value));
    }

    Integer getKey(String value) {
        var key = Integer.parseInt(value);
        return key;
    }

    String getStringKey(Integer value) {
        return value.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Ciclos object) {
        if (object == null) {
            return null;
        }
        return getStringKey(object.getIdCiclo());
    }
}
