package fi.budokwai.isoveli.util;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@SessionScoped
public class ObjectStore implements Serializable
{
   private static final long serialVersionUID = 1L;

   @Inject
   private EntityManager entityManager;

   public Object string2Object(String key)
   {
      String[] osat = key.split("/");
      String luokkanimi = osat[0];
      String id = osat[1];
      Class<?> luokka = null;
      try
      {
         luokka = Class.forName(luokkanimi);
      } catch (ClassNotFoundException e)
      {
         return null;
      }
      return entityManager.find(luokka, Integer.parseInt(id));
   }

   public String object2String(Object object)
   {
      if (object == null)
      {
         return null;
      }
      String luokkanimi = object.getClass().getName();
      String id = getId(object).toString();
      return String.format("%s/%s", luokkanimi, id);
   }

   private Integer getId(Object object)
   {
      if (object == null)
      {
         return null;
      }
      try
      {
         Field idField = object.getClass().getDeclaredField("id");
         idField.setAccessible(true);
         return (Integer) idField.get(object);
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
      {
         return 0;
      }
   }

}