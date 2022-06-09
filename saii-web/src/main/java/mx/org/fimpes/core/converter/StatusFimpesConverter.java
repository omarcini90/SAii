package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import mx.org.fimpes.core.mbean.CampusController;
import mx.org.fimpes.saii.model.core.StatusFimpes;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(forClass = StatusFimpes.class)
public class StatusFimpesConverter implements Converter<StatusFimpes> {

    @Override
    public StatusFimpes getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        var controller = (CampusController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "campusController");
        return controller.getStatusById(getKey(value));
    }

    Integer getKey(String value) {
        var key = Integer.parseInt(value);
        return key;
    }

    String getStringKey(Integer value) {
        return value.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, StatusFimpes object) {
        if (object == null) {
            return null;
        }
        return getStringKey(object.getIdStatus());
    }
}
