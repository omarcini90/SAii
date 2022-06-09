package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import mx.org.fimpes.core.mbean.ProcesoController;
import mx.org.fimpes.saii.model.core.RegistroIndicadores;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(forClass = RegistroIndicadores.class)
public class RegistroIndicadoresConverter implements Converter<RegistroIndicadores> {

    @Override
    public RegistroIndicadores getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        var controller = (ProcesoController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "procesoController");
        return controller.getIndicadorById(getKey(value));
    }

    Integer getKey(String value) {
        var key = Integer.parseInt(value);
        return key;
    }

    String getStringKey(Integer value) {
        return value.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, RegistroIndicadores object) {
        if (object == null) {
            return null;
        }
        return getStringKey(object.getIdRegistro());
    }
}
