package fi.budokwai.isoveli.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
   private Treenisessio treenisessio;
   private List<Treenisessio> treenisessiot;
   private List<Harrastaja> kaikkivet‰j‰t;
   private List<Harrastaja> sessiovet‰j‰t;
   

   private RowStateMap treenik‰yntiRSM = new RowStateMap();
   private RowStateMap treenisessioRSM = new RowStateMap();

   @Produces
   @Named
   public Treenik‰ynti getTreenik‰ynti()
   {
      return treenik‰ynti;
   }

   @Produces
   @Named
   public Treenisessio getTreenisessio()
   {
      return treenisessio;
   }

   @PostConstruct
   public void init()
   {
      ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
      // PushRenderer.addCurrentSession("ilmoittautuminen");
   }

   @Produces
   @Named
   public List<Harrastaja> getSessiovet‰j‰t()
   {
      if (treenisessio == null)
      {
         return new ArrayList<Harrastaja>();
      }
      if (kaikkivet‰j‰t == null)
      {
         kaikkivet‰j‰t = entityManager.createNamedQuery("treenivet‰j‰t", Harrastaja.class).getResultList();
         kaikkivet‰j‰t = kaikkivet‰j‰t.stream().filter(h -> h.isTreenienVet‰j‰()).collect(Collectors.toList());
      }
      if (sessiovet‰j‰t == null)
      {
         sessiovet‰j‰t = new ArrayList<Harrastaja>();
         sessiovet‰j‰t.addAll(kaikkivet‰j‰t);
         sessiovet‰j‰t.removeAll(treenisessio.getVet‰j‰t());
      }
      return sessiovet‰j‰t;
   }   
   
   public void treenisessioValittu(SelectEvent e)
   {
      treenisessio = (Treenisessio) e.getObject();
      sessiovet‰j‰t = null;
   }

   public void treenik‰yntiValittu(SelectEvent e)
   {
      treenik‰ynti = (Treenik‰ynti) e.getObject();
   }

   public void tabiMuuttui(ValueChangeEvent e)
   {
      int uusiTabi = (int) e.getNewValue();
      if (uusiTabi == 0)
      {
         treenik‰ynnit = null;
         treenik‰ynti = null;
         treenik‰yntiRSM.setAllSelected(false);
      } else if (uusiTabi == 1)
      {
         treenisessiot = null;
         treenisessio = null;
         treenisessioRSM.setAllSelected(false);
      }
      // entityManager.clear();
   }

   @Produces
   @Named
   public List<Treenisessio> getKaikkiTreenisessiot()
   {
      if (treenisessiot == null)
      {
         haeTreenisessiot();
         treenisessiot.forEach(t -> entityManager.refresh(t));
      }
      return treenisessiot;
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

   private void haeTreenisessiot()
   {
      treenisessiot = entityManager.createNamedQuery("kaikki_treenisessiot", Treenisessio.class).getResultList();
   }

   public void lis‰‰Treenik‰ynti()
   {
      treenik‰ynti = new Treenik‰ynti();
      info("Uusi treenik‰ynti alustettu");
      treenik‰yntiRSM.setAllSelected(false);
   }

   public void lis‰‰Treenisessio()
   {
      treenisessio = new Treenisessio();
      info("Uusi treenisessio alustettu");
      treenisessioRSM.setAllSelected(false);
   }

   public void poistaTreenik‰ynti()
   {
      entityManager.remove(treenik‰ynti);
      entityManager.flush();
      treenik‰ynnit = null;
      info("Treenik‰ynti poistettu");
   }

   public void poistaTreenisessio()
   {
      entityManager.remove(treenisessio);
      entityManager.flush();
      treenisessio = null;
      treenisessiot = null;
      treenik‰ynnit = null;
      info("Treenisessio poistettu");
   }

   public void piilotaTreenik‰ynti()
   {
      treenik‰ynti = null;
      entityManager.createNamedQuery("poista_tyhj‰t_treenisessiot").executeUpdate();
      entityManager.flush();
   }

   public void piilotaTreenisessio()
   {
      treenisessio = null;
   }

   public void tallennaTreenik‰ynti()
   {
      entityManager.persist(treenik‰ynti);
      entityManager.flush();
      treenik‰yntiRSM.get(treenik‰ynti).setSelected(true);
      haeTreenik‰ynnit();
      info("Treenik‰ynti tallennettu");
   }

   public void tallennaTreenisessio()
   {
      entityManager.persist(treenisessio);
      entityManager.flush();
      treenisessioRSM.get(treenisessio).setSelected(true);
      haeTreenisessiot();
      info("Treenisessio tallennettu");
   }

   public void peruutaTreenik‰yntimuutos()
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

   public void peruutaTreenisessiomuutos()
   {
      if (treenisessio.isPoistettavissa())
      {
         entityManager.refresh(treenisessio);
      } else
      {
         treenisessio = null;
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

   public RowStateMap getTreenisessioRSM()
   {
      return treenisessioRSM;
   }

   public void setTreenisessioRSM(RowStateMap treenisessioRSM)
   {
      this.treenisessioRSM = treenisessioRSM;
   }

}
