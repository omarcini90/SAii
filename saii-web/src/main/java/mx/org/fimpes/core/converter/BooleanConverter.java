package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(value = "boolconverter")
public class BooleanConverter implements Converter<Boolean> {

    @Override
    public Boolean getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }        
        return Boolean.valueOf(value);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Boolean object) {
        if (object == null) {
            return null;
        }        
        return object.toString();
    }

}