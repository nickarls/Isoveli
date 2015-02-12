package fi.budokwai.isoveli.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Treenikäynti;
import fi.budokwai.isoveli.malli.Treenisessio;

@Stateful
@Named
@SessionScoped
public class IlmoittautumisAdmin extends Perustoiminnallisuus
{
   @Inject
   private EntityManager entityManager;

   private List<Treenikäynti> treenikäynnit;
   private Treenikäynti treenikäynti;
   private Treenisessio treenisessio;
   private List<Treenisessio> treenisessiot;
   private List<Harrastaja> kaikkivetäjät;
   private List<Harrastaja> sessiovetäjät;
   

   private RowStateMap treenikäyntiRSM = new RowStateMap();
   private RowStateMap treenisessioRSM = new RowStateMap();

   @Produces
   @Named
   public Treenikäynti getTreenikäynti()
   {
      return treenikäynti;
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
   public List<Harrastaja> getSessiovetäjät()
   {
      if (treenisessio == null)
      {
         return new ArrayList<Harrastaja>();
      }
      if (kaikkivetäjät == null)
      {
         kaikkivetäjät = entityManager.createNamedQuery("treenivetäjät", Harrastaja.class).getResultList();
         kaikkivetäjät = kaikkivetäjät.stream().filter(h -> h.isTreenienVetäjä()).collect(Collectors.toList());
      }
      if (sessiovetäjät == null)
      {
         sessiovetäjät = new ArrayList<Harrastaja>();
         sessiovetäjät.addAll(kaikkivetäjät);
         sessiovetäjät.removeAll(treenisessio.getVetäjät());
      }
      return sessiovetäjät;
   }   
   
   public void treenisessioValittu(SelectEvent e)
   {
      treenisessio = (Treenisessio) e.getObject();
      sessiovetäjät = null;
   }

   public void treenikäyntiValittu(SelectEvent e)
   {
      treenikäynti = (Treenikäynti) e.getObject();
   }

   public void tabiMuuttui(ValueChangeEvent e)
   {
      int uusiTabi = (int) e.getNewValue();
      if (uusiTabi == 0)
      {
         treenikäynnit = null;
         treenikäynti = null;
         treenikäyntiRSM.setAllSelected(false);
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

   private void haeTreenisessiot()
   {
      treenisessiot = entityManager.createNamedQuery("kaikki_treenisessiot", Treenisessio.class).getResultList();
   }

   public void lisääTreenikäynti()
   {
      treenikäynti = new Treenikäynti();
      info("Uusi treenikäynti alustettu");
      treenikäyntiRSM.setAllSelected(false);
   }

   public void lisääTreenisessio()
   {
      treenisessio = new Treenisessio();
      info("Uusi treenisessio alustettu");
      treenisessioRSM.setAllSelected(false);
   }

   public void poistaTreenikäynti()
   {
      treenikäynti = entityManager.merge(treenikäynti);
      entityManager.remove(treenikäynti);
      entityManager.flush();
      treenikäynnit = null;
      info("Treenikäynti poistettu");
   }

   public void poistaTreenisessio()
   {
      treenisessio = entityManager.merge(treenisessio);
      entityManager.remove(treenisessio);
      entityManager.flush();
      treenisessio = null;
      treenisessiot = null;
      treenikäynnit = null;
      info("Treenisessio poistettu");
   }

   public void piilotaTreenikäynti()
   {
      treenikäynti = null;
      entityManager.createNamedQuery("poista_tyhjät_treenisessiot").executeUpdate();
      entityManager.flush();
   }

   public void piilotaTreenisessio()
   {
      treenisessio = null;
   }

   public void tallennaTreenikäynti()
   {
      treenikäynti = entityManager.merge(treenikäynti);
      entityManager.flush();
      treenikäyntiRSM.get(treenikäynti).setSelected(true);
      haeTreenikäynnit();
      info("Treenikäynti tallennettu");
   }

   public void tallennaTreenisessio()
   {
      treenisessio = entityManager.merge(treenisessio);
      entityManager.flush();
      treenisessioRSM.get(treenisessio).setSelected(true);
      haeTreenisessiot();
      info("Treenisessio tallennettu");
   }

   public void peruutaTreenikäyntimuutos()
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

   public RowStateMap getTreenikäyntiRSM()
   {
      return treenikäyntiRSM;
   }

   public void setTreenikäyntiRSM(RowStateMap treenikäyntiRSM)
   {
      this.treenikäyntiRSM = treenikäyntiRSM;
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
