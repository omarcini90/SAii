package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import mx.org.fimpes.core.mbean.CampusController;
import mx.org.fimpes.saii.model.core.Estados;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(forClass = Estados.class)
public class EstadosConverter implements Converter<Estados> {

    @Override
    public Estados getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        var controller = (CampusController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "campusController");
        return controller.getEstadoById(getKey(value));
    }

    Integer getKey(String value) {
        var key = Integer.parseInt(value);
        return key;
    }

    String getStringKey(Integer value) {
        return value.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Estados object) {
        if (object == null) {
            return null;
        }
        return getStringKey(object.getIdEstado());
    }
}
