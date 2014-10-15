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
import org.icefaces.application.PushRenderer;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenikäynti;
import fi.budokwai.isoveli.malli.Treenisessio;

@Stateful
@Named
@SessionScoped
public class IlmoittautumisAdmin extends Perustoiminnallisuus
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private List<Treenikäynti> treenikäynnit;
   private Treenikäynti treenikäynti;

   private RowStateMap treenikäyntiRSM = new RowStateMap();

   @Produces
   @Named
   public Treenikäynti getTreenikäynti()
   {
      return treenikäynti;
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
      PushRenderer.addCurrentSession("ilmoittautuminen");
   }

   public void harrastajaValittu(SelectEvent e)
   {
      treenikäynti.setHarrastaja((Harrastaja) e.getObject());
   }

   public void tabiMuuttui(ValueChangeEvent e)
   {
      int uusiTabi = (int) e.getNewValue();
      if (uusiTabi == 1)
      {
         treenikäynnit = null;
      }
   }

   @Produces
   @Named
   public List<Treenikäynti> getTreenikäynnit()
   {
      if (treenikäynnit == null)
      {
         haeTreenikäynnit();
      }
      return treenikäynnit;
   }

   private void haeTreenikäynnit()
   {
      treenikäynnit = entityManager.createNamedQuery("treenikäynnit", Treenikäynti.class).getResultList();
   }

   public void lisääIlmoittautuminen()
   {
      treenikäynti = new Treenikäynti();
      info("Uusi treenikäynti alustettu");
      treenikäyntiRSM.setAllSelected(false);
   }

   private Treenisessio haeTreenisessio(Treeni treeni)
   {
      Date tänään = haeTänäänPvm();
      List<Treenisessio> treenisessiot = entityManager.createNamedQuery("treenisessio", Treenisessio.class)
         .setParameter("treeni", treeni).setParameter("päivä", tänään).getResultList();
      Treenisessio treenisessio = new Treenisessio();
      if (!treenisessiot.isEmpty())
      {
         treenisessio = treenisessiot.iterator().next();
      } else
      {
         treenisessio.setPäivä(tänään);
         treenisessio.setTreeni(treeni);
         for (Harrastaja vetäjä : treeni.getVetäjät())
         {
            treenisessio.getVetäjät().add(vetäjä);
         }
      }
      return treenisessio;
   }

   private Date haeTänäänPvm()
   {
      Calendar kalenteri = Calendar.getInstance();
      kalenteri.setTimeZone(TimeZone.getTimeZone("EET"));
      kalenteri.set(Calendar.HOUR_OF_DAY, 0);
      kalenteri.set(Calendar.MINUTE, 0);
      kalenteri.set(Calendar.SECOND, 0);
      kalenteri.set(Calendar.MILLISECOND, 0);
      return kalenteri.getTime();
   }

   public void poistaTreenikäynti()
   {
      entityManager.remove(treenikäynti);
      entityManager.flush();
      treenikäynti = null;
      info("Treenikäynti poistettu");
   }

   public void tallennaTreenikäynti()
   {
      if (treenikäynti.getTreenisessio().isTallentamaton())
      {
         Treenisessio sessio = haeTreenisessio(treenikäynti.getTreenisessio().getTreeni());
         treenikäynti.setTreenisessio(sessio);
      }
      entityManager.persist(treenikäynti);
      entityManager.flush();
      treenikäyntiRSM.get(treenikäynti).setSelected(true);
      haeTreenikäynnit();
      info("Treenikäynti tallennettu");
   }

   public void treeniKäyntiValittu(SelectEvent e)
   {
      treenikäynti = (Treenikäynti) e.getObject();
   }

   public void peruutaMuutos()
   {
      if (treenikäynti.isPoistettavissa())
      {
         entityManager.refresh(treenikäynti);
      } else
      {
         treenikäynti = null;
      }
      virhe("Muutokset peruttu");
   }

   public RowStateMap getTreenikäyntiRSM()
   {
      return treenikäyntiRSM;
   }

   public void setTreenikäyntiRSM(RowStateMap treenikäyntiRSM)
   {
      this.treenikäyntiRSM = treenikäyntiRSM;
   }

}
