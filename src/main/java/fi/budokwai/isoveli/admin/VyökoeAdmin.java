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
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;
import fi.budokwai.isoveli.malli.Vy�koetilaisuus;
import fi.budokwai.isoveli.malli.Vy�kokelas;
import fi.budokwai.isoveli.util.Vy�koehelper;

@Stateful
@SessionScoped
@Named
public class Vy�koeAdmin extends Perustoiminnallisuus
{
   private List<Vy�koetilaisuus> vy�koetilaisuudet = null;
   private Vy�koetilaisuus vy�koetilaisuus = null;
   private RowStateMap vy�koetilaisuusRSM = new RowStateMap();

   @Inject
   private EntityManager entityManager;

   @Produces
   @Named
   public List<Vy�koetilaisuus> getVy�koetilaisuudet()
   {
      if (vy�koetilaisuudet == null)
      {
         vy�koetilaisuudet = entityManager.createNamedQuery("vy�koetilaisuudet", Vy�koetilaisuus.class).getResultList();
      }
      return vy�koetilaisuudet;
   }

   @Produces
   @Named
   public List<Harrastaja> getVy�koepit�j�t()
   {
      return entityManager.createNamedQuery("vy�kokeiden_pit�j�t", Harrastaja.class)
         .setParameter("nimi", "Vy�kokeen pit�j�").getResultList();
   }

   @Produces
   @Named
   public Vy�koetilaisuus getVy�koetilaisuus()
   {
      return vy�koetilaisuus;
   }

   @Inject
   @Named(value = "harrastajat")
   private Instance<List<Harrastaja>> harrastajat;

   @Inject
   private Vy�koehelper vy�koehelper;

   public void my�nn�Vy�arvot()
   {
      vy�koetilaisuus = entityManager.merge(vy�koetilaisuus);
      vy�koetilaisuus.getVy�kokelaat().stream().filter(v -> v.isOnnistui()).forEach(v -> {
         Harrastaja harrastaja = v.getHarrastaja();
         Vy�arvo vy�arvo = vy�koehelper.haeSeuraavaVy�arvo(harrastaja);
         Vy�koe vy�koe = new Vy�koe(harrastaja, vy�arvo, v.getVy�koetilaisuus().getKoska());
         harrastaja.lis��Vy�koe(vy�koe);
         harrastaja = entityManager.merge(harrastaja);
         loggaaja.loggaa("My�nsi vy�arvon '%s' harrastajalle '%s'", harrastaja.getNimi(), vy�arvo.getNimi());
      });
      entityManager.flush();
   }

   public void harrastajaMuuttui(AjaxBehaviorEvent e)
   {
      Optional<Harrastaja> harrastaja = harrastajat.get().stream()
         .filter(h -> h.getNimi().equals(vy�koetilaisuus.getHarrastajaHaku())).findFirst();
      if (harrastaja.isPresent())
      {
         Vy�arvo uusiVy�arvo = vy�koehelper.haeSeuraavaVy�arvo(harrastaja.get());
         List<String> huomautukset = vy�koehelper.onkoKokelas(harrastaja.get(), vy�koetilaisuus);
         huomautukset.forEach(h -> {
            virhe(h);
         });
         vy�koetilaisuus.lis��Vy�kokelas(harrastaja.get(), uusiVy�arvo);
         vy�koetilaisuus = entityManager.merge(vy�koetilaisuus);
         info("Harrastaja %s lis�tty ja tilaisuus tallennettu", harrastaja.get().getNimi());
         loggaaja.loggaa("Harrastaja '%s' lis�tty vy�koetilaisuuteen", harrastaja.get().getNimi());
      }
   }

   public void poistaVy�kokelas(Vy�kokelas vy�kokelas)
   {
      vy�kokelas = entityManager.merge(vy�kokelas);
      entityManager.remove(vy�kokelas);
      vy�koetilaisuus.getVy�kokelaat().remove(vy�kokelas);
      vy�koetilaisuus = entityManager.merge(vy�koetilaisuus);
      entityManager.flush();
      info("Vy�kokelas %s poistettu", vy�kokelas.getHarrastaja().getNimi());
      fokusoi("form:etunimi");
      loggaaja.loggaa("Poisti vy�kokelaan '%s'", vy�kokelas.getHarrastaja().getNimi());
   }

   public void vy�koetilaisuusValittu(SelectEvent e)
   {
      vy�koetilaisuus = (Vy�koetilaisuus) e.getObject();
   }

   public RowStateMap getVy�koetilaisuusRSM()
   {
      return vy�koetilaisuusRSM;
   }

   public void setVy�koetilaisuusRSM(RowStateMap vy�koetilaisuusRSM)
   {
      this.vy�koetilaisuusRSM = vy�koetilaisuusRSM;
   }

   public void lis��Vy�koetilaisuus()
   {
      resetoi();
      vy�koetilaisuus = new Vy�koetilaisuus();
      info("Uusi vy�koetilaisuus alustettu");
      fokusoi("form:etunimi");
      loggaaja.loggaa("Lis�si vy�koetilaisuuden");
   }

   public void tallennaVy�koetilaisuus()
   {
      if (!validointiOK())
      {
         return;
      }
      vy�koetilaisuus = entityManager.merge(vy�koetilaisuus);
      entityManager.flush();
      vy�koetilaisuusRSM.get(vy�koetilaisuus).setSelected(true);
      vy�koetilaisuudet = null;
      info("Vy�koetilaisuus tallennettu");
      loggaaja.loggaa("Tallensi vy�koetilaisuuden '%s'", vy�koetilaisuus);
   }

   private boolean validointiOK()
   {
      Set<ConstraintViolation<Vy�koetilaisuus>> validointivirheet = validator.validate(vy�koetilaisuus);
      validointivirheet.forEach(v -> {
         virhe(v.getMessage());
      });
      return validointivirheet.isEmpty();
   }

   public void poistaVy�koetilaisuus()
   {
      vy�koetilaisuus.poistotarkistus();
      vy�koetilaisuus = entityManager.merge(vy�koetilaisuus);
      entityManager.remove(vy�koetilaisuus);
      entityManager.flush();
      vy�koetilaisuus = null;
      vy�koetilaisuudet = null;
      vy�koetilaisuusRSM.setAllSelected(false);
      info("Vy�koetilaisuus poistettu");
      loggaaja.loggaa("Poisti vy�koetilaisuuden '%s'", vy�koetilaisuus);
   }

   private void resetoi()
   {
      entityManager.clear();
      vy�koetilaisuudet = null;
      vy�koetilaisuus = null;
      vy�koetilaisuusRSM = new RowStateMap();
   }

   public void lis��Kokelaita()
   {
      List<Harrastaja> kokelaat = entityManager.createNamedQuery("harrastajat", Harrastaja.class).getResultList()
         .stream().filter(h -> vy�koehelper.onkoKokelas(h, vy�koetilaisuus).isEmpty()).collect(Collectors.toList());
      kokelaat.forEach(h -> {
         Vy�arvo vy�arvo = vy�koehelper.haeSeuraavaVy�arvo(h);
         vy�koetilaisuus.lis��Vy�kokelas(h, vy�arvo);
      });
      if (kokelaat.size() > 0)
      {
         vy�koetilaisuus = entityManager.merge(vy�koetilaisuus);
         entityManager.flush();
         info("%d kokelasta lis�tty ja vy�koetilaisuus tallennettu", kokelaat.size());
      } else
      {
         info("Kokelaita ei l�ytynyt");
      }
   }
}
