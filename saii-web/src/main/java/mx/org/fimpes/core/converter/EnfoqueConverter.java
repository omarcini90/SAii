package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import mx.org.fimpes.core.mbean.ProgramasController;
import mx.org.fimpes.saii.model.core.Enfoque;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(forClass = Enfoque.class)
public class EnfoqueConverter implements Converter<Enfoque> {

    @Override
    public Enfoque getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        var controller = (ProgramasController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "programasController");
        return controller.getEnfoqueById(getKey(value));
    }

    Integer getKey(String value) {
        var key = Integer.parseInt(value);
        return key;
    }

    String getStringKey(Integer value) {
        return value.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Enfoque object) {
        if (object == null) {
            return null;
        }
        return getStringKey(object.getIdEnfoque());
    }
}