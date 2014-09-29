package fi.budokwai.isoveli;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.JäljelläVyökokeeseen;
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.util.Muuttui;

@Named
@SessionScoped
@Stateful
public class VyökoeAdmin
{
   private Vyökoe vyökoe;

   private RowStateMap rowStateMap = new RowStateMap();

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   @Inject @Muuttui
   private Event<Object> harrastajaMuuttui;
   
   private List<Vyöarvo> vyöarvot;

   @PostConstruct
   public void alusta()
   {
      vyöarvot = entityManager.createNamedQuery("vyöarvot", Vyöarvo.class).getResultList();
   }

   @Produces
   @Named
   public List<Vyöarvo> getVyöarvot()
   {
      return vyöarvot;
   }

   @Produces
   @Named
   public Vyökoe getVyökoe()
   {
      return vyökoe;
   }

   public JäljelläVyökokeeseen laskeJäljelläVyökokeeseen(Vyöarvo vyöarvo)
   {
      Vyöarvo seuraavaArvo = entityManager.find(Vyöarvo.class, vyöarvo.getId() + 1);
      if (seuraavaArvo == null)
      {
         return new JäljelläVyökokeeseen(0, 0);
      }
      JäljelläVyökokeeseen tulos = new JäljelläVyökokeeseen(seuraavaArvo.getMinimikuukaudet() * 30,
         seuraavaArvo.getMinimitreenit());
      return tulos;
   }

   public void tallennaVyökoe(Harrastaja harrastaja)
   {
      harrastaja = entityManager.merge(harrastaja);
      vyökoe.setHarrastaja(harrastaja);
      harrastaja.getVyökokeet().add(vyökoe);
      entityManager.persist(harrastaja);
      vyökoe = null;
      rowStateMap.setAllSelected(false);
      harrastajaMuuttui.fire(new Object());
   }

   public void peruutaMuutos()
   {
      vyökoe = null;
      rowStateMap.setAllSelected(false);
   }

   public void poistaVyökoe(Harrastaja harrastaja)
   {
      harrastaja = entityManager.merge(harrastaja);
      harrastaja.getVyökokeet().remove(vyökoe);
      entityManager.persist(harrastaja);
      entityManager.flush();
      vyökoe = null;
      harrastajaMuuttui.fire(harrastaja);
   }

   public void lisääVyökoe(Harrastaja harrastaja)
   {
      vyökoe = new Vyökoe();
      Vyöarvo seuraavaVyöarvo = vyöarvot.iterator().next();
      if (!harrastaja.getVyökokeet().isEmpty())
      {
         seuraavaVyöarvo = vyöarvot.get(vyöarvot.indexOf(harrastaja.getTuoreinVyöarvo()) + 1);
      }
      vyökoe.setVyöarvo(seuraavaVyöarvo);
      vyökoe.setPäivä(new Date());
   }

   public boolean isVyökoePoistettavissa()
   {
      return vyökoe != null;
   }

   public void vyökoeValittu(SelectEvent e)
   {
      vyökoe = (Vyökoe) e.getObject();
   }

   public RowStateMap getRowStateMap()
   {
      return rowStateMap;
   }

   public void setRowStateMap(RowStateMap rowStateMap)
   {
      this.rowStateMap = rowStateMap;
   }

}
