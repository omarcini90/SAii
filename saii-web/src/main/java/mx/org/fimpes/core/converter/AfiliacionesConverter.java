package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import mx.org.fimpes.core.mbean.InstitucionesController;
import mx.org.fimpes.saii.model.core.Afiliaciones;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(forClass = Afiliaciones.class, value = "afiliacionesConverter")
public class AfiliacionesConverter implements Converter<Afiliaciones> {

    @Override
    public Afiliaciones getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        var controller = (InstitucionesController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "institucionesController");
        return controller.getAfiliadoraById(getKey(value));
    }

    Integer getKey(String value) {
        var key = Integer.parseInt(value);
        return key;
    }

    String getStringKey(Integer value) {
        return value.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Afiliaciones object) {
        if (object == null) {
            return null;
        }
        return getStringKey(object.getIdAfiliacion());
    }

}