package fi.budokwai.isoveli;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.icefaces.ace.component.datetimeentry.DateTimeEntry;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Rooli;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Treenik�ynti;
import fi.budokwai.isoveli.malli.Vy�koe;

@Named
@SessionScoped
@Stateful
public class HarrastajaAdmin extends Perustoiminnallisuus
{
   private List<Harrastaja> harrastajat;
   private List<Rooli> roolit;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private Harrastaja harrastaja;

   private RowStateMap rowStateMap = new RowStateMap();

   @PostConstruct
   public void init()
   {
      ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
   }

   @Produces
   @Named
   public List<Harrastaja> getHarrastajat()
   {
      if (harrastajat == null)
      {
         harrastajat = entityManager.createNamedQuery("harrastajat", Harrastaja.class).getResultList();
      }
      return harrastajat;
   }

   @Produces
   @Named
   public List<Rooli> getRoolit()
   {
      if (roolit == null)
      {
         roolit = entityManager.createNamedQuery("roolit", Rooli.class).getResultList();
         roolit.removeAll(harrastaja.getHenkil�().getRoolit());
      }
      return roolit;
   }

   @Produces
   @Named
   public List<SelectItem> getSukupuolet()
   {
      List<SelectItem> tulos = new ArrayList<SelectItem>();
      for (Sukupuoli sukupuoli : Sukupuoli.values())
      {
         tulos.add(new SelectItem(sukupuoli, sukupuoli.toString()));
      }
      return tulos;
   }

   @Produces
   @Named
   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void kopioiOsoitetiedot()
   {
      harrastaja.getHuoltaja().setSukunimi(harrastaja.getHenkil�().getSukunimi());
      harrastaja.getHuoltaja().getOsoite().setOsoite(harrastaja.getHenkil�().getOsoite().getOsoite());
      harrastaja.getHuoltaja().getOsoite().setPostinumero(harrastaja.getHenkil�().getOsoite().getPostinumero());
      harrastaja.getHuoltaja().getOsoite().setKaupunki(harrastaja.getHenkil�().getOsoite().getKaupunki());
      info("Osoitetiedot kopioitu");
   }

   public void kopioiOsoitetiedot(AjaxBehaviorEvent e)
   {
      kopioiOsoitetiedot();
   }

   public void tallennaHarrastaja()
   {
      entityManager.persist(harrastaja);
      entityManager.flush();
      harrastajat = null;
      info("Harrastaja tallennettu");
   }

   public void peruutaMuutos()
   {
      harrastaja = null;
      rowStateMap.setAllSelected(false);
      virhe("Muutos peruttu");
   }

   public void poistaHarrastaja()
   {
      entityManager.remove(harrastaja);
      entityManager.flush();
      harrastaja = null;
      harrastajat = null;
      rowStateMap.setAllSelected(false);
      info("Harrastaja poistettu");
   }

   public void tallennaVy�koe(Vy�koe vy�koe)
   {
      vy�koe.setHarrastaja(harrastaja);
      if (!harrastaja.getVy�kokeet().contains(vy�koe))
      {
         harrastaja.getVy�kokeet().add(vy�koe);
      }
      entityManager.persist(harrastaja);
      entityManager.flush();
      info("Vy�koe tallennettu");
   }

   public void poistaVy�koe(Vy�koe vy�koe)
   {
      harrastaja.getVy�kokeet().remove(vy�koe);
      entityManager.persist(harrastaja);
      entityManager.flush();
      info("Vy�koe poistettu");
   }

   public void syntym�AikaMuuttui(AjaxBehaviorEvent e)
   {
      DateTimeEntry dte = (DateTimeEntry) e.getComponent();
      Date val = (Date) dte.getValue();
      if (Harrastaja.alaik�inen(val))
      {
         harrastaja.setHuoltaja(new Henkil�());
      } else
      {
         harrastaja.setHuoltaja(null);
      }
   }

   public void lis��Harrastaja()
   {
      harrastaja = new Harrastaja();
      roolit = null;
      rowStateMap.setAllSelected(false);
      info("Uusi harrastaja alustettu");
   }

   public void harrastajaValittu(SelectEvent e)
   {
      harrastaja = (Harrastaja) e.getObject();
      roolit = null;
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
