package fi.budokwai.isoveli.util;

import java.util.ArrayList;
import java.util.List;

public class Item
{
   private String data;
   private List<Item> subItems = new ArrayList<>();

   public Item()
   {
   }

   public Item(String data)
   {
      this.data = data;
   }

   public String getData()
   {
      return data;
   }

   public void setData(String data)
   {
      this.data = data;
   }

   public List<Item> getSubItems()
   {
      return subItems;
   }

   public void setSubItems(List<Item> subItems)
   {
      this.subItems = subItems;
   }

   public void addSubItem(Item detail)
   {
      subItems.add(detail);
   }

}
