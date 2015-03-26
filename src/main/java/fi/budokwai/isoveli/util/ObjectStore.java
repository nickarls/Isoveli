package fi.budokwai.isoveli.util;

import java.io.Serializable;
import java.util.Map.Entry;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import fi.budokwai.isoveli.malli.Perhe;

@SessionScoped
public class ObjectStore implements Serializable
{
   private static final long serialVersionUID = 1L;

   private BiMap<String, Object> map = HashBiMap.create();
   private int key;

   public void resetoi(@Observes @Muuttui EntityManager em)
   {
      map.clear();
   }

   public Object string2Object(String key)
   {
      Object result = map.get(key);
      dump();
      System.out.println(String.format("s2o: %s = %s", key, result));
      return result;
   }

   public String object2String(Object object)
   {
      String result = null;
      if (map.inverse().containsKey(object))
      {
         result = map.inverse().get(object);
      } else
      {
         String strKey = String.valueOf(key++);
         map.put(strKey, object);
         result = strKey;
      }
      dump();
      System.out.println(String.format("o2s: %s = %s", object, result));
      return result;
   }

   private void dump()
   {
      for (Entry<String, Object> rivi : map.entrySet())
      {
         System.out.println(String.format("%s=%s", rivi.getKey(), rivi.getValue()));
      }
   }

}