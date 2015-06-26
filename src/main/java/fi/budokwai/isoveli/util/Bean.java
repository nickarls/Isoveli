package fi.budokwai.isoveli.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

@Named
@SessionScoped
public class Bean implements Serializable
{
   private List<Item> items;

   @PostConstruct
   public void init()
   {
      Item item = new Item("master");
      Item detail = new Item("detail");
      item.addSubItem(detail);
      items = Arrays.asList(new Item[]
      { item });

   }

   @Produces
   @Named
   public List<Item> getItems()
   {
      return items;
   }

   public void action(Item item)
   {
      System.out.println(String.format("Performing action on %s", item));
   }
}
