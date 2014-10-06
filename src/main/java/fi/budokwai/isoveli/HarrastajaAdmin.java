package fi.budokwai.isoveli;

import java.util.ArrayList;
import java.util.Collections;
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
import fi.budokwai.isoveli.malli.J�ljell�Vy�kokeeseen;
import fi.budokwai.isoveli.malli.Rooli;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;

@Named
@SessionScoped
@Stateful
public class HarrastajaAdmin extends Perustoiminnallisuus
{
   private List<Harrastaja> harrastajat;
   private List<Rooli> roolit;
   private List<Vy�arvo> vy�arvot;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private Harrastaja harrastaja;
   private Sopimus sopimus;
   private Vy�koe vy�koe;

   private RowStateMap rowStateMap = new RowStateMap();
   private RowStateMap vy�koeRSM = new RowStateMap();
   private RowStateMap sopimusRSM = new RowStateMap();

   @PostConstruct
   public void init()
   {
      ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
      vy�arvot = entityManager.createNamedQuery("vy�arvot", Vy�arvo.class).getResultList();
   }

   @Produces
   @Named
   public Sopimus getSopimus()
   {
      return sopimus;
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

   @Produces
   @Named
   public List<Rooli> getRoolit()
   {
      if (harrastaja == null)
      {
         return Collections.emptyList();
      }
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

   public void peruutaPerustietomuutos()
   {
      if (harrastaja.isPoistettavissa())
      {
         entityManager.refresh(harrastaja);
      } else
      {
         harrastaja = null;
      }
      virhe("Muutos peruttu");
   }

   public void peruutaSopimusmuutos()
   {
      if (sopimus.isPoistettavissa())
      {
         entityManager.refresh(sopimus);
      } else
      {
         sopimus = null;
      }
      virhe("Muutos peruttu");
   }

   public void peruutaVy�koemuutos()
   {
      if (vy�koe.isPoistettavissa())
      {
         entityManager.refresh(vy�koe);
      } else
      {
         vy�koe = null;
      }
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

   public void tallennaVy�koe()
   {
      if (!harrastaja.getVy�kokeet().contains(vy�koe))
      {
         vy�koe.setHarrastaja(harrastaja);
         harrastaja.getVy�kokeet().add(vy�koe);
      }
      entityManager.persist(harrastaja);
      entityManager.flush();
      vy�koeRSM.get(vy�koe).setSelected(true);
      info("Vy�koe tallennettu");
   }

   public void tallennaSopimus()
   {
      if (!harrastaja.getSopimukset().contains(sopimus))
      {
         sopimus.setHarrastaja(harrastaja);         
         harrastaja.getSopimukset().add(sopimus);
      }
      entityManager.persist(harrastaja);
      entityManager.flush();
      sopimusRSM.get(sopimus).setSelected(true);
      info("Sopimus tallennettu");
   }

   public void poistaVy�koe()
   {
      harrastaja.getVy�kokeet().remove(vy�koe);
      entityManager.persist(harrastaja);
      entityManager.flush();
      info("Vy�koe poistettu");
   }

   public void poistaSopimus()
   {
      harrastaja.getSopimukset().remove(sopimus);
      entityManager.persist(harrastaja);
      entityManager.flush();
      info("Sopimus poistettu");
   }

   public void piilotaSopimus()
   {
      sopimus = null;
   }

   public void piilotaVy�koe()
   {
      vy�koe = null;
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

   public void lis��Sopimus()
   {
      sopimus = new Sopimus();
      rowStateMap.setAllSelected(false);      
      info("Uusi sopimus alustettu");
   }

   public void lis��Vy�koe()
   {
      vy�koe = new Vy�koe();
      Vy�arvo seuraavaVy�arvo = vy�arvot.iterator().next();
      if (!harrastaja.getVy�kokeet().isEmpty())
      {
         seuraavaVy�arvo = vy�arvot.get(vy�arvot.indexOf(harrastaja.getTuoreinVy�arvo()) + 1);
      }
      vy�koe.setVy�arvo(seuraavaVy�arvo);
      vy�koe.setP�iv�(new Date());
      vy�koe.setHarrastaja(harrastaja);
      vy�koeRSM.setAllSelected(false);
      info("Uusi vy�koe alustettu");
   }

   public J�ljell�Vy�kokeeseen laskeJ�ljell�Vy�kokeeseen(Vy�arvo vy�arvo)
   {
      Vy�arvo seuraavaArvo = entityManager.find(Vy�arvo.class, vy�arvo.getId() + 1);
      if (seuraavaArvo == null)
      {
         return new J�ljell�Vy�kokeeseen(0, 0);
      }
      J�ljell�Vy�kokeeseen tulos = new J�ljell�Vy�kokeeseen(seuraavaArvo.getMinimikuukaudet() * 30,
         seuraavaArvo.getMinimitreenit());
      return tulos;
   }

   public void harrastajaValittu(SelectEvent e)
   {
      harrastaja = (Harrastaja) e.getObject();
      roolit = null;
      vy�koe = null;
      vy�koeRSM = new RowStateMap();
      sopimusRSM = new RowStateMap();
   }

   public void vy�koeValittu(SelectEvent e)
   {
      vy�koe = (Vy�koe) e.getObject();
   }

   public void sopimusValittu(SelectEvent e)
   {
      sopimus = (Sopimus) e.getObject();
   }

   public RowStateMap getRowStateMap()
   {
      return rowStateMap;
   }

   public void setRowStateMap(RowStateMap rowStateMap)
   {
      this.rowStateMap = rowStateMap;
   }

   public RowStateMap getVy�koeRSM()
   {
      return vy�koeRSM;
   }

   public void setVy�koeRSM(RowStateMap vy�koeRSM)
   {
      this.vy�koeRSM = vy�koeRSM;
   }

   public RowStateMap getSopimusRSM()
   {
      return sopimusRSM;
   }

   public void setSopimusRSM(RowStateMap sopimusRSM)
   {
      this.sopimusRSM = sopimusRSM;
   }

}
