package fi.budokwai.isoveli;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
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

@Named
@SessionScoped
@Stateful
public class VyökoeAdmin
{
   private Vyökoe vyökoe;

   private RowStateMap rowStateMap = new RowStateMap();

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

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
      JäljelläVyökokeeseen tulos = new JäljelläVyökokeeseen();
      tulos.setPäiviä(seuraavaArvo.getMinimikuukaudet() * 30);
      tulos.setTreenikertoja(seuraavaArvo.getMinimitreenit());
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
   }

   public void peruutaMuutos()
   {
      vyökoe = null;
      rowStateMap.setAllSelected(false);
   }

   public void poistaVyökoe()
   {
      entityManager.remove(vyökoe);
      vyökoe = null;
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
      return vyökoe != null && vyökoe.getId() > 0;
   }

   public void vyokoeValittu(SelectEvent e)
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
