package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import mx.org.fimpes.core.mbean.IndicadoresController;
import mx.org.fimpes.saii.model.core.Seccion;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(forClass = Seccion.class, value = "seccionConverter")
public class SeccionConverter implements Converter<Seccion> {

    @Override
    public Seccion getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        var controller = (IndicadoresController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "indicadoresController");
        return controller.getSeccionById(getKey(value));
    }

    Integer getKey(String value) {
        var key = Integer.parseInt(value);
        return key;
    }

    String getStringKey(Integer value) {
        return value.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Seccion object) {
        if (object == null) {
            return null;
        }
        return getStringKey(object.getIdSeccion());
    }
}
