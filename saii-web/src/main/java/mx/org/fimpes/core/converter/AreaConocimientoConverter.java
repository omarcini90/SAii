package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import mx.org.fimpes.core.mbean.ProgramasController;
import mx.org.fimpes.saii.model.core.AreaConocimiento;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(forClass = AreaConocimiento.class)
public class AreaConocimientoConverter implements Converter<AreaConocimiento> {

    @Override
    public AreaConocimiento getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        var controller = (ProgramasController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "programasController");
        return controller.getAreaConocimientoById(getKey(value));
    }

    Integer getKey(String value) {
        var key = Integer.parseInt(value);
        return key;
    }

    String getStringKey(Integer value) {
        return value.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, AreaConocimiento object) {
        if (object == null) {
            return null;
        }
        return getStringKey(object.getIdAreaConocimiento());
    }

}
