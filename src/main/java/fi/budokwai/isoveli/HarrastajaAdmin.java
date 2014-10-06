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
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.JäljelläVyökokeeseen;
import fi.budokwai.isoveli.malli.Rooli;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sukupuoli;
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoe;

@Named
@SessionScoped
@Stateful
public class HarrastajaAdmin extends Perustoiminnallisuus
{
   private List<Harrastaja> harrastajat;
   private List<Rooli> roolit;
   private List<Vyöarvo> vyöarvot;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private Harrastaja harrastaja;
   private Sopimus sopimus;
   private Vyökoe vyökoe;

   private RowStateMap rowStateMap = new RowStateMap();
   private RowStateMap vyökoeRSM = new RowStateMap();
   private RowStateMap sopimusRSM = new RowStateMap();

   @PostConstruct
   public void init()
   {
      ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
      vyöarvot = entityManager.createNamedQuery("vyöarvot", Vyöarvo.class).getResultList();
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
         roolit.removeAll(harrastaja.getHenkilö().getRoolit());
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
      harrastaja.getHuoltaja().setSukunimi(harrastaja.getHenkilö().getSukunimi());
      harrastaja.getHuoltaja().getOsoite().setOsoite(harrastaja.getHenkilö().getOsoite().getOsoite());
      harrastaja.getHuoltaja().getOsoite().setPostinumero(harrastaja.getHenkilö().getOsoite().getPostinumero());
      harrastaja.getHuoltaja().getOsoite().setKaupunki(harrastaja.getHenkilö().getOsoite().getKaupunki());
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

   public void peruutaVyökoemuutos()
   {
      if (vyökoe.isPoistettavissa())
      {
         entityManager.refresh(vyökoe);
      } else
      {
         vyökoe = null;
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

   public void tallennaVyökoe()
   {
      if (!harrastaja.getVyökokeet().contains(vyökoe))
      {
         vyökoe.setHarrastaja(harrastaja);
         harrastaja.getVyökokeet().add(vyökoe);
      }
      entityManager.persist(harrastaja);
      entityManager.flush();
      vyökoeRSM.get(vyökoe).setSelected(true);
      info("Vyökoe tallennettu");
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

   public void poistaVyökoe()
   {
      harrastaja.getVyökokeet().remove(vyökoe);
      entityManager.persist(harrastaja);
      entityManager.flush();
      info("Vyökoe poistettu");
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

   public void piilotaVyökoe()
   {
      vyökoe = null;
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
      roolit = null;
      rowStateMap.setAllSelected(false);
      info("Uusi harrastaja alustettu");
   }

   public void lisääSopimus()
   {
      sopimus = new Sopimus();
      rowStateMap.setAllSelected(false);      
      info("Uusi sopimus alustettu");
   }

   public void lisääVyökoe()
   {
      vyökoe = new Vyökoe();
      Vyöarvo seuraavaVyöarvo = vyöarvot.iterator().next();
      if (!harrastaja.getVyökokeet().isEmpty())
      {
         seuraavaVyöarvo = vyöarvot.get(vyöarvot.indexOf(harrastaja.getTuoreinVyöarvo()) + 1);
      }
      vyökoe.setVyöarvo(seuraavaVyöarvo);
      vyökoe.setPäivä(new Date());
      vyökoe.setHarrastaja(harrastaja);
      vyökoeRSM.setAllSelected(false);
      info("Uusi vyökoe alustettu");
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

   public void harrastajaValittu(SelectEvent e)
   {
      harrastaja = (Harrastaja) e.getObject();
      roolit = null;
      vyökoe = null;
      vyökoeRSM = new RowStateMap();
      sopimusRSM = new RowStateMap();
   }

   public void vyökoeValittu(SelectEvent e)
   {
      vyökoe = (Vyökoe) e.getObject();
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

   public RowStateMap getVyökoeRSM()
   {
      return vyökoeRSM;
   }

   public void setVyökoeRSM(RowStateMap vyökoeRSM)
   {
      this.vyökoeRSM = vyökoeRSM;
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
