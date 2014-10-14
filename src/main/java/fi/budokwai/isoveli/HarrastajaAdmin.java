package fi.budokwai.isoveli;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
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
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Perhe;
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
   private List<Perhe> perheet;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private Harrastaja harrastaja;
   private Sopimus sopimus;
   private Vyökoe vyökoe;

   private RowStateMap harrastajaRSM = new RowStateMap();
   private RowStateMap vyökoeRSM = new RowStateMap();
   private RowStateMap sopimusRSM = new RowStateMap();

   public void perheMuuttui(ValueChangeEvent e)
   {
      Perhe uusiPerhe = (Perhe) e.getNewValue();
      if (uusiPerhe == null)
      {
         harrastaja.setOsoite(new Osoite());
      } else
      {
         harrastaja.setOsoite(null);
      }
      harrastaja.setOsoiteMuuttunut(true);
      fokusoi("form:osoite");
   }

   public void lisääPerhe()
   {
      Perhe perhe = new Perhe();
      perhe.setNimi(harrastaja.getSukunimi());
      perhe.getPerheenjäsenet().add(harrastaja);
      perhe.setOsoite(harrastaja.getOsoite());
      harrastaja.setOsoite(null);
      harrastaja.setPerhe(perhe);
      entityManager.persist(perhe);
      entityManager.flush();
      perheet.forEach(p -> entityManager.refresh(p));
      haePerheet();
   }

   @PostConstruct
   public void init()
   {
      ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
      vyöarvot = entityManager.createNamedQuery("vyöarvot", Vyöarvo.class).getResultList();
      haePerheet();
      haeHarrastajat();
   }

   @Produces
   @Named
   public List<Perhe> getPerheet()
   {
      return perheet;
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
         roolit.removeAll(harrastaja.getRoolit());
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

   private void haeHarrastajat()
   {
      harrastajat = entityManager.createNamedQuery("harrastajat", Harrastaja.class).getResultList();
   }

   private void haePerheet()
   {
      perheet = entityManager.createNamedQuery("perheet", Perhe.class).getResultList();
   }

   public void tallennaHarrastaja()
   {
      harrastaja.siivoa();
      entityManager.persist(harrastaja);
      entityManager.flush();
      harrastajaRSM.get(harrastaja).setSelected(true);
      if (harrastaja.isOsoiteMuuttunut())
      {
         poistaTyhjätPerheetJaOsoitteet();
      }
      harrastaja.setOsoiteMuuttunut(false);
      haePerheet();
      perheet.forEach(p -> entityManager.refresh(p));
      haeHarrastajat();
      info("Harrastaja tallennettu");
   }

   private void poistaTyhjätPerheetJaOsoitteet()
   {
      entityManager.createNamedQuery("poista_tyhjät_perheet").executeUpdate();
      entityManager.createNamedQuery("poista_tyhjät_osoitteet").executeUpdate();
   }

   public void peruutaPerustietomuutos()
   {
      if (harrastaja.isPoistettavissa())
      {
         harrastaja.siivoa();
         entityManager.refresh(harrastaja);
      } else
      {
         harrastaja = null;
      }
      virhe("Muutokset peruttu");
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
      virhe("Muutokset peruttu");
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
      virhe("Muutokset peruttu");
   }

   public void poistaHarrastaja()
   {
      entityManager.remove(harrastaja);
      entityManager.flush();
      harrastaja = null;
      haeHarrastajat();
      harrastajaRSM.setAllSelected(false);
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

   public void piilotaHarrastaja()
   {
      harrastaja = null;
   }

   public void syntymäaikaMuuttui(AjaxBehaviorEvent e)
   {
      DateTimeEntry dte = (DateTimeEntry) e.getComponent();
      Date val = (Date) dte.getValue();
      if (Harrastaja.alaikäinen(val))
      {
         harrastaja.setHuoltaja(new Henkilö());
         harrastaja.setPerhe(new Perhe());
      } else
      {
         harrastaja.setHuoltaja(null);
      }
   }

   public void lisääHuoltaja()
   {
      Henkilö huoltaja = new Henkilö();
      Perhe perhe = harrastaja.getPerhe();
      if (perhe != null)
      {
         perhe.getPerheenjäsenet().add(huoltaja);
         huoltaja.setSukunimi(harrastaja.getSukunimi());
         huoltaja.setPerhe(perhe);
         harrastaja.setHuoltaja(huoltaja);
      }
   }

   public void lisääHarrastaja()
   {
      harrastaja = new Harrastaja();
      roolit = null;
      harrastajaRSM.setAllSelected(false);
      info("Uusi harrastaja alustettu");
      fokusoi("form:etunimi");
   }

   public void lisääSopimus()
   {
      sopimus = new Sopimus();
      sopimusRSM.setAllSelected(false);
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

   public void sopimustyyppiMuuttui()
   {
      sopimus.setTreenikertoja(sopimus.getTyyppi().getOletusTreenikerrat());
      sopimus.setMaksuväli(sopimus.getTyyppi().getOletusMaksuväli());
      if (sopimus.getTyyppi().getOletusKuukaudetVoimassa() > 0)
      {
         sopimus.setUmpeutuu(Date.from(LocalDate.now()
            .plus(sopimus.getTyyppi().getOletusKuukaudetVoimassa(), ChronoUnit.MONTHS).atStartOfDay()
            .atZone(ZoneId.systemDefault()).toInstant()));
      } else
      {
         sopimus.setUmpeutuu(null);
      }
      if (sopimus.getTyyppi().isJäsenmaksu())
      {
         LocalDate päivä = LocalDate.now();
         päivä = päivä.plus(1, ChronoUnit.YEARS);
         päivä = päivä.withMonth(1).with(TemporalAdjusters.lastDayOfMonth());
         sopimus.setUmpeutuu(Date.from(päivä.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
      }
   }

   public JäljelläVyökokeeseen laskeJäljelläVyökokeeseen(Vyöarvo vyöarvo)
   {
      List<Vyöarvo> seuraavatArvot = entityManager.createNamedQuery("vyöarvo", Vyöarvo.class)
         .setParameter("järjestys", vyöarvo.getJärjestys() + 1).getResultList();
      if (seuraavatArvot.size() == 0)
      {
         return JäljelläVyökokeeseen.EI_OOTA;
      }
      Vyöarvo seuraavaVyöarvo = seuraavatArvot.iterator().next();
      long treenit = seuraavaVyöarvo.getMinimitreenit() - harrastaja.getTreenejäViimeVyökokeesta();
      Period aika = harrastaja.getAikaaViimeVyökokeesta();
      return new JäljelläVyökokeeseen(aika, treenit);
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

   public RowStateMap getHarrastajaRSM()
   {
      return harrastajaRSM;
   }

   public void setHarrastajaRSM(RowStateMap harrastajaRSM)
   {
      this.harrastajaRSM = harrastajaRSM;
   }

}
