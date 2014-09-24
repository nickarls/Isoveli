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
import fi.budokwai.isoveli.malli.J�ljell�Vy�kokeeseen;
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;

@Named
@SessionScoped
@Stateful
public class Vy�koeAdmin
{
   private Vy�koe vy�koe;

   private RowStateMap rowStateMap = new RowStateMap();

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private List<Vy�arvo> vy�arvot;

   @PostConstruct
   public void alusta()
   {
      vy�arvot = entityManager.createNamedQuery("vy�arvot", Vy�arvo.class).getResultList();
   }

   @Produces
   @Named
   public List<Vy�arvo> getVy�arvot()
   {
      return vy�arvot;
   }

   @Produces
   @Named
   public Vy�koe getVy�koe()
   {
      return vy�koe;
   }

   public J�ljell�Vy�kokeeseen laskeJ�ljell�Vy�kokeeseen(Vy�arvo vy�arvo)
   {
      Vy�arvo seuraavaArvo = entityManager.find(Vy�arvo.class, vy�arvo.getId() + 1);
      J�ljell�Vy�kokeeseen tulos = new J�ljell�Vy�kokeeseen();
      tulos.setP�ivi�(seuraavaArvo.getMinimikuukaudet() * 30);
      tulos.setTreenikertoja(seuraavaArvo.getMinimitreenit());
      return tulos;
   }

   public void tallennaVy�koe(Harrastaja harrastaja)
   {
      harrastaja = entityManager.merge(harrastaja);
      vy�koe.setHarrastaja(harrastaja);
      harrastaja.getVy�kokeet().add(vy�koe);
      entityManager.persist(harrastaja);
      vy�koe = null;
      rowStateMap.setAllSelected(false);
   }

   public void peruutaMuutos()
   {
      vy�koe = null;
      rowStateMap.setAllSelected(false);
   }

   public void poistaVy�koe()
   {
      entityManager.remove(vy�koe);
      vy�koe = null;
   }

   public void lis��Vy�koe(Harrastaja harrastaja)
   {
      vy�koe = new Vy�koe();
      Vy�arvo seuraavaVy�arvo = vy�arvot.iterator().next();
      if (!harrastaja.getVy�kokeet().isEmpty())
      {
         seuraavaVy�arvo = vy�arvot.get(vy�arvot.indexOf(harrastaja.getTuoreinVy�arvo()) + 1);
      }
      vy�koe.setVy�arvo(seuraavaVy�arvo);
      vy�koe.setP�iv�(new Date());

   }

   public boolean isVy�koePoistettavissa()
   {
      return vy�koe != null && vy�koe.getId() > 0;
   }

   public void vyokoeValittu(SelectEvent e)
   {
      vy�koe = (Vy�koe) e.getObject();
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
