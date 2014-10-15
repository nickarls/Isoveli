package fi.budokwai.isoveli;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenik‰ynti;
import fi.budokwai.isoveli.malli.Treenisessio;

@Stateful
@Named
@SessionScoped
public class IlmoittautumisAdmin extends Perustoiminnallisuus
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private List<Treenik‰ynti> treenik‰ynnit;
   private Treenik‰ynti treenik‰ynti;

   private RowStateMap treenik‰yntiRSM = new RowStateMap();

   @Produces
   @Named
   public Treenik‰ynti getTreenik‰ynti()
   {
      return treenik‰ynti;
   }

   @Produces
   @Named
   public List<Treenisessio> getKaikkiTreenisessiot()
   {
      return entityManager.createNamedQuery("kaikki_treenisessiot", Treenisessio.class).getResultList();
   }

   @PostConstruct
   public void init()
   {
      ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
//      PushRenderer.addCurrentSession("ilmoittautuminen");
   }

   public void harrastajaValittu(SelectEvent e)
   {
      treenik‰ynti.setHarrastaja((Harrastaja) e.getObject());
   }

   public void tabiMuuttui(ValueChangeEvent e)
   {
      int uusiTabi = (int) e.getNewValue();
      if (uusiTabi == 1)
      {
         treenik‰ynnit = null;
      }
//      entityManager.clear();
   }

   @Produces
   @Named
   public List<Treenik‰ynti> getTreenik‰ynnit()
   {
      if (treenik‰ynnit == null)
      {
         haeTreenik‰ynnit();
      }
      return treenik‰ynnit;
   }

   private void haeTreenik‰ynnit()
   {
      treenik‰ynnit = entityManager.createNamedQuery("treenik‰ynnit", Treenik‰ynti.class).getResultList();
   }

   public void lis‰‰Ilmoittautuminen()
   {
      treenik‰ynti = new Treenik‰ynti();
      info("Uusi treenik‰ynti alustettu");
      treenik‰yntiRSM.setAllSelected(false);
   }

   private Treenisessio haeTreenisessio(Treeni treeni)
   {
      Date t‰n‰‰n = haeT‰n‰‰nPvm();
      List<Treenisessio> treenisessiot = entityManager.createNamedQuery("treenisessio", Treenisessio.class)
         .setParameter("treeni", treeni).setParameter("p‰iv‰", t‰n‰‰n).getResultList();
      Treenisessio treenisessio = new Treenisessio();
      if (!treenisessiot.isEmpty())
      {
         treenisessio = treenisessiot.iterator().next();
      } else
      {
         treenisessio.setP‰iv‰(t‰n‰‰n);
         treenisessio.setTreeni(treeni);
         for (Harrastaja vet‰j‰ : treeni.getVet‰j‰t())
         {
            treenisessio.getVet‰j‰t().add(vet‰j‰);
         }
      }
      return treenisessio;
   }

   private Date haeT‰n‰‰nPvm()
   {
      Calendar kalenteri = Calendar.getInstance();
      kalenteri.setTimeZone(TimeZone.getTimeZone("EET"));
      kalenteri.set(Calendar.HOUR_OF_DAY, 0);
      kalenteri.set(Calendar.MINUTE, 0);
      kalenteri.set(Calendar.SECOND, 0);
      kalenteri.set(Calendar.MILLISECOND, 0);
      return kalenteri.getTime();
   }

   public void poistaTreenik‰ynti()
   {
      entityManager.remove(treenik‰ynti);
      entityManager.flush();
      treenik‰ynti = null;
      treenik‰ynnit = null;
      info("Treenik‰ynti poistettu");
   }

   public void piilotaTreenik‰ynti()
   {
      treenik‰ynti = null;
   }

   public void tallennaTreenik‰ynti()
   {
      if (treenik‰ynti.getTreenisessio().isTallentamaton())
      {
         Treenisessio sessio = haeTreenisessio(treenik‰ynti.getTreenisessio().getTreeni());
         treenik‰ynti.setTreenisessio(sessio);
      }
      entityManager.persist(treenik‰ynti);
      entityManager.flush();
      treenik‰yntiRSM.get(treenik‰ynti).setSelected(true);
      haeTreenik‰ynnit();
      info("Treenik‰ynti tallennettu");
   }

   public void treeniK‰yntiValittu(SelectEvent e)
   {
      treenik‰ynti = (Treenik‰ynti) e.getObject();
   }

   public void peruutaMuutos()
   {
      if (treenik‰ynti.isPoistettavissa())
      {
         entityManager.refresh(treenik‰ynti);
      } else
      {
         treenik‰ynti = null;
      }
      virhe("Muutokset peruttu");
   }

   public RowStateMap getTreenik‰yntiRSM()
   {
      return treenik‰yntiRSM;
   }

   public void setTreenik‰yntiRSM(RowStateMap treenik‰yntiRSM)
   {
      this.treenik‰yntiRSM = treenik‰yntiRSM;
   }

}
