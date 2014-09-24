package fi.budokwai.isoveli.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("objectConverter")
public class ObjectConverter implements Converter
{
   private ObjectStore objectStore = new ObjectStore();

   public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string)
   {
      return objectStore.string2Object(string);
   }

   public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object)
   {
      return objectStore.object2String(object);
   }

}