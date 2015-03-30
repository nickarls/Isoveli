package fi.budokwai.isoveli.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.PropertyUtils;

@SessionScoped
public class ObjectStore implements Serializable
{
   private static final long serialVersionUID = 1L;

   @Inject
   private EntityManager entityManager;

   public Object string2Object(String key)
   {
      String[] osat = key.split("/");
      if (osat.length != 2)
      {
         return null;
      }
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
      Object tulos = entityManager.find(luokka, Integer.parseInt(id));
      // System.out.println(String.format(">>> %s => %s", key, tulos));
      return tulos;
   }

   public String object2String(Object object)
   {
      if (object == null)
      {
         return null;
      }
      String luokkanimi = object.getClass().getName();
      int id = getId(object);
      String result = String.format("%s/%s", luokkanimi, id);
      // System.out.println(String.format(">>> %s => %s", object, result));
      return result;
   }

   private Integer getId(Object object)
   {
      if (object == null)
      {
         return null;
      }
      try
      {
         return (Integer) PropertyUtils.getProperty(object, "id");
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1)
      {
         return null;
      }
   }

}