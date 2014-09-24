package fi.budokwai.isoveli;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Osoite;

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
   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   public void yhteinenOsoite(AjaxBehaviorEvent e)
   {
      HtmlSelectBooleanCheckbox control = (HtmlSelectBooleanCheckbox) e.getComponent();
      if (control.isSelected())
      {
         harrastaja.getHenkilö().setOsoite(harrastaja.getHuoltaja().getOsoite());
      }
   }

   public void huoltajaMuuttui(AjaxBehaviorEvent e)
   {
      HtmlSelectBooleanCheckbox control = (HtmlSelectBooleanCheckbox) e.getComponent();
      if (control.isSelected())
      {
         Henkilö huoltaja = new Henkilö();
         huoltaja.setOsoite(new Osoite());
         harrastaja.setHuoltaja(huoltaja);
      } else
      {
         harrastaja.setHuoltaja(null);
      }
   }

   public void tallennaHarrastaja()
   {
      entityManager.persist(harrastaja);
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
      harrastaja = null;
      harrastajat = null;
      rowStateMap.setAllSelected(false);
   }

   public boolean isHarrastajaPoistettavissa()
   {
      return harrastaja != null && harrastaja.getId() > 0;
   }

   public String lisääHarrastaja()
   {
      harrastaja = new Harrastaja();
      return "admin.xhtml";
   }

   public void harrastajaValittu(SelectEvent e)
   {
      harrastaja = (Harrastaja) e.getObject();
      boolean vaatiiHuoltajan = (harrastaja.getHuoltaja() != null || harrastaja.isAlaikainen());
      harrastaja.setVaatiiHuoltajan(vaatiiHuoltajan);
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
