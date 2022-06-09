package mx.org.fimpes.core.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import mx.org.fimpes.core.mbean.IndicadoresController;
import mx.org.fimpes.saii.model.core.TipoIndicador;

/**
*
* @author Universidad Latinoamericana | Omar.Rendon@ula.edu.mx | omar.josue@gmail.com
*/
@FacesConverter(forClass = TipoIndicador.class, value = "tipoIndicadorConverter")
public class TipoIndicadorConverter implements Converter<TipoIndicador> {

    @Override
    public TipoIndicador getAsObject(FacesContext facesContext, UIComponent component, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        var controller = (IndicadoresController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "indicadoresController");
        return controller.getTipoIndicadorById(getKey(value));
    }

    Integer getKey(String value) {
        var key = Integer.parseInt(value);
        return key;
    }

    String getStringKey(Integer value) {
        return value.toString();
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, TipoIndicador object) {
        if (object == null) {
            return null;
        }
        return getStringKey(object.getIdTipo());
    }
}
