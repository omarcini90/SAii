package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import mx.org.fimpes.core.mbean.InstitucionesController;
import mx.org.fimpes.saii.model.core.Acreditadoras;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(forClass = Acreditadoras.class, value = "acreditadoraConverter")
public class AcreditadorasConverter implements Converter<Acreditadoras>{

    @Override
    public Acreditadoras getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        var controller = (InstitucionesController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "institucionesController");
        return controller.getAcreditadoraById(getKey(value));
    }

    Integer getKey(String value) {
        var key = Integer.parseInt(value);
        return key;
    }

    String getStringKey(Integer value) {
        return value.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Acreditadoras object) {
        if (object == null) {
            return null;
        }
        return getStringKey(object.getIdAcreditadora());
    }

}