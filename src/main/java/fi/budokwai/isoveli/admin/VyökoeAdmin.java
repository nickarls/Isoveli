package fi.budokwai.isoveli.admin;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;

import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.malli.Vyökoetilaisuus;
import fi.budokwai.isoveli.malli.Vyökokelas;
import fi.budokwai.isoveli.util.Vyökoehelper;

@Stateful
@SessionScoped
@Named
public class VyökoeAdmin extends Perustoiminnallisuus
{
   private List<Vyökoetilaisuus> vyökoetilaisuudet = null;
   private Vyökoetilaisuus vyökoetilaisuus = null;
   private RowStateMap vyökoetilaisuusRSM = new RowStateMap();

   @Inject
   private EntityManager entityManager;

   @Produces
   @Named
   public List<Vyökoetilaisuus> getVyökoetilaisuudet()
   {
      if (vyökoetilaisuudet == null)
      {
         vyökoetilaisuudet = entityManager.createNamedQuery("vyökoetilaisuudet", Vyökoetilaisuus.class).getResultList();
      }
      return vyökoetilaisuudet;
   }

   @Produces
   @Named
   public List<Harrastaja> getVyökoepitäjät()
   {
      return entityManager.createNamedQuery("vyökokeiden_pitäjät", Harrastaja.class)
         .setParameter("nimi", "Vyökokeen pitäjä").getResultList();
   }

   @Produces
   @Named
   public Vyökoetilaisuus getVyökoetilaisuus()
   {
      return vyökoetilaisuus;
   }

   @Inject
   @Named(value = "harrastajat")
   private Instance<List<Harrastaja>> harrastajat;

   @Inject
   private Vyökoehelper vyökoehelper;

   public void myönnäVyöarvot()
   {
      vyökoetilaisuus = entityManager.merge(vyökoetilaisuus);
      vyökoetilaisuus.getVyökokelaat().stream().filter(v -> v.isOnnistui()).forEach(v -> {
         Harrastaja harrastaja = v.getHarrastaja();
         Vyöarvo vyöarvo = vyökoehelper.haeSeuraavaVyöarvo(harrastaja);
         Vyökoe vyökoe = new Vyökoe(harrastaja, vyöarvo, v.getVyökoetilaisuus().getKoska());
         harrastaja.lisääVyökoe(vyökoe);
         harrastaja = entityManager.merge(harrastaja);
         loggaaja.loggaa("Myönsi vyöarvon '%s' harrastajalle '%s'", harrastaja.getNimi(), vyöarvo.getNimi());
      });
      entityManager.flush();
   }

   public void harrastajaMuuttui(AjaxBehaviorEvent e)
   {
      Optional<Harrastaja> harrastaja = harrastajat.get().stream()
         .filter(h -> h.getNimi().equals(vyökoetilaisuus.getHarrastajaHaku())).findFirst();
      if (harrastaja.isPresent())
      {
         Vyöarvo uusiVyöarvo = vyökoehelper.haeSeuraavaVyöarvo(harrastaja.get());
         List<String> huomautukset = vyökoehelper.onkoKokelas(harrastaja.get(), vyökoetilaisuus);
         huomautukset.forEach(h -> {
            virhe(h);
         });
         vyökoetilaisuus.lisääVyökokelas(harrastaja.get(), uusiVyöarvo);
         vyökoetilaisuus = entityManager.merge(vyökoetilaisuus);
         info("Harrastaja %s lisätty ja tilaisuus tallennettu", harrastaja.get().getNimi());
         loggaaja.loggaa("Harrastaja '%s' lisätty vyökoetilaisuuteen", harrastaja.get().getNimi());
      }
   }

   public void poistaVyökokelas(Vyökokelas vyökokelas)
   {
      vyökokelas = entityManager.merge(vyökokelas);
      entityManager.remove(vyökokelas);
      vyökoetilaisuus.getVyökokelaat().remove(vyökokelas);
      vyökoetilaisuus = entityManager.merge(vyökoetilaisuus);
      entityManager.flush();
      info("Vyökokelas %s poistettu", vyökokelas.getHarrastaja().getNimi());
      fokusoi("form:etunimi");
      loggaaja.loggaa("Poisti vyökokelaan '%s'", vyökokelas.getHarrastaja().getNimi());
   }

   public void vyökoetilaisuusValittu(SelectEvent e)
   {
      vyökoetilaisuus = (Vyökoetilaisuus) e.getObject();
   }

   public RowStateMap getVyökoetilaisuusRSM()
   {
      return vyökoetilaisuusRSM;
   }

   public void setVyökoetilaisuusRSM(RowStateMap vyökoetilaisuusRSM)
   {
      this.vyökoetilaisuusRSM = vyökoetilaisuusRSM;
   }

   public void lisääVyökoetilaisuus()
   {
      resetoi();
      vyökoetilaisuus = new Vyökoetilaisuus();
      info("Uusi vyökoetilaisuus alustettu");
      fokusoi("form:etunimi");
      loggaaja.loggaa("Lisäsi vyökoetilaisuuden");
   }

   public void tallennaVyökoetilaisuus()
   {
      if (!validointiOK())
      {
         return;
      }
      vyökoetilaisuus = entityManager.merge(vyökoetilaisuus);
      entityManager.flush();
      vyökoetilaisuusRSM.get(vyökoetilaisuus).setSelected(true);
      vyökoetilaisuudet = null;
      info("Vyökoetilaisuus tallennettu");
      loggaaja.loggaa("Tallensi vyökoetilaisuuden '%s'", vyökoetilaisuus);
   }

   private boolean validointiOK()
   {
      Set<ConstraintViolation<Vyökoetilaisuus>> validointivirheet = validator.validate(vyökoetilaisuus);
      validointivirheet.forEach(v -> {
         virhe(v.getMessage());
      });
      return validointivirheet.isEmpty();
   }

   public void poistaVyökoetilaisuus()
   {
      vyökoetilaisuus.poistotarkistus();
      vyökoetilaisuus = entityManager.merge(vyökoetilaisuus);
      entityManager.remove(vyökoetilaisuus);
      entityManager.flush();
      vyökoetilaisuus = null;
      vyökoetilaisuudet = null;
      vyökoetilaisuusRSM.setAllSelected(false);
      info("Vyökoetilaisuus poistettu");
      loggaaja.loggaa("Poisti vyökoetilaisuuden '%s'", vyökoetilaisuus);
   }

   private void resetoi()
   {
      entityManager.clear();
      vyökoetilaisuudet = null;
      vyökoetilaisuus = null;
      vyökoetilaisuusRSM = new RowStateMap();
   }

   public void lisääKokelaita()
   {
      List<Harrastaja> kokelaat = entityManager.createNamedQuery("harrastajat", Harrastaja.class).getResultList()
         .stream().filter(h -> vyökoehelper.onkoKokelas(h, vyökoetilaisuus).isEmpty()).collect(Collectors.toList());
      kokelaat.forEach(h -> {
         Vyöarvo vyöarvo = vyökoehelper.haeSeuraavaVyöarvo(h);
         vyökoetilaisuus.lisääVyökokelas(h, vyöarvo);
      });
      if (kokelaat.size() > 0)
      {
         vyökoetilaisuus = entityManager.merge(vyökoetilaisuus);
         entityManager.flush();
         info("%d kokelasta lisätty ja vyökoetilaisuus tallennettu", kokelaat.size());
      } else
      {
         info("Kokelaita ei löytynyt");
      }
   }
}
