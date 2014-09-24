package fi.budokwai.isoveli.util;

import java.io.Serializable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ObjectStore implements Serializable
{
   private static final long serialVersionUID = 1L;

   private BiMap<String, Object> map = HashBiMap.create();
   private int key;

   public Object string2Object(String key)
   {
      return map.get(key);
   }

   public String object2String(Object object)
   {
      if (map.inverse().containsKey(object))
      {
         return map.inverse().get(object);
      } else
      {
         String strKey = String.valueOf(key++);
         map.put(strKey, object);
         return strKey;
      }
   }

}