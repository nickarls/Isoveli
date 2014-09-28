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
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Sukupuoli;

@Named
@SessionScoped
@Stateful
public class HarrastajaAdmin
{
   private List<Harrastaja> harrastajat;

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
      harrastaja.getHuoltaja().setSukunimi(harrastaja.getHenkilö().getSukunimi());
      harrastaja.getHuoltaja().getOsoite().setOsoite(harrastaja.getHenkilö().getOsoite().getOsoite());
      harrastaja.getHuoltaja().getOsoite().setPostinumero(harrastaja.getHenkilö().getOsoite().getPostinumero());
      harrastaja.getHuoltaja().getOsoite().setKaupunki(harrastaja.getHenkilö().getOsoite().getKaupunki());
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
   }

   public void peruutaMuutos()
   {
      harrastaja = null;
      rowStateMap.setAllSelected(false);
   }

   public void poistaHarrastaja()
   {
      entityManager.remove(harrastaja);
      entityManager.flush();
      harrastaja = null;
      harrastajat = null;
      rowStateMap.setAllSelected(false);
   }

   public void syntymäAikaMuuttui(AjaxBehaviorEvent e)
   {
      DateTimeEntry dte = (DateTimeEntry) e.getComponent();
      Date val = (Date) dte.getValue();
      if (Harrastaja.alaikäinen(val))
      {
         harrastaja.setHuoltaja(new Henkilö());
      } else
      {
         harrastaja.setHuoltaja(null);
      }
   }

   public void lisääHarrastaja()
   {
      harrastaja = new Harrastaja();
   }

   public void harrastajaValittu(SelectEvent e)
   {
      harrastaja = (Harrastaja) e.getObject();
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
