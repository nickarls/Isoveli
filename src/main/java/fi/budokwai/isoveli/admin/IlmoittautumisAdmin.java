package fi.budokwai.isoveli.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Järjestelmätilasto;
import fi.budokwai.isoveli.malli.Treenikäynti;
import fi.budokwai.isoveli.malli.Treenisessio;
import fi.budokwai.isoveli.util.Loggaaja;

@Stateful
@Named
@SessionScoped
public class IlmoittautumisAdmin extends Perustoiminnallisuus
{
   @Inject
   private EntityManager entityManager;

   @Inject
   private Loggaaja loggaaja;

   private List<Treenikäynti> treenikäynnit;
   private Treenikäynti treenikäynti;
   private Treenisessio treenisessio;
   private List<Treenisessio> treenisessiot;
   private List<Harrastaja> kaikkivetäjät;
   private List<Harrastaja> sessiovetäjät;

   private RowStateMap treenikäyntiRSM = new RowStateMap();
   private RowStateMap treenisessioRSM = new RowStateMap();

   public void treenisessioMuuttui(AjaxBehaviorEvent e)
   {
      treenikäynti.päivitäAikaleima();
   }

   @Inject
   @Named("harrastajat")
   private Instance<List<Harrastaja>> harrastajat;

   @Produces
   @Named
   public Järjestelmätilasto getTilasto()
   {
      long harrastajia = entityManager.createQuery("select count(h) from Harrastaja h", Long.class).getSingleResult();
      long treenejä = entityManager.createQuery("select count(t) from Treenisessio t", Long.class).getSingleResult();
      long treenikäyntejä = entityManager.createQuery("select count(t) from Treenikäynti t", Long.class)
         .getSingleResult();
      long vyöarvoja = entityManager.createQuery("select count(v) from Vyökoe v", Long.class).getSingleResult();
      return new Järjestelmätilasto(harrastajia, treenejä, treenikäyntejä, vyöarvoja);
   }

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
         kaikkivetäjät = entityManager.createNamedQuery("harrastajat_roolissa", Harrastaja.class).setParameter("rooli", "Treenien vetäjä").getResultList();
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
      treenikäynti.alusta();
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

   public Treenikäynti lisääTreenikäynti()
   {
      treenikäynti = new Treenikäynti();
      info("Uusi treenikäynti alustettu");
      treenikäyntiRSM.setAllSelected(false);
      loggaaja.loggaa("Lisäsi uuden treenikäynnin");
      return treenikäynti;
   }

   public Treenisessio lisääTreenisessio()
   {
      treenisessio = new Treenisessio();
      info("Uusi treenisessio alustettu");
      treenisessioRSM.setAllSelected(false);
      loggaaja.loggaa("Lisäsi uuden treenisession");
      return treenisessio;
   }

   public void poistaTreenikäynti()
   {
      if (treenikäynti.isTallentamaton())
      {
         treenikäynti = null;
         return;
      }
      treenikäynti = entityManager.merge(treenikäynti);
      entityManager.remove(treenikäynti);
      entityManager.flush();
      treenikäynnit = null;
      treenisessiot = null;
      treenikäynti = null;
      loggaaja.loggaa("Poisti treenikäynnin '%s'", treenikäynti);
      info("Treenikäynti poistettu");
   }

   public void poistaTreenisessio()
   {
      if (treenisessio.isTallentamaton())
      {
         treenisessio = null;
         return;
      }
      treenisessio.tarkistaKäyttö();
      treenisessio = entityManager.merge(treenisessio);
      entityManager.remove(treenisessio);
      entityManager.flush();
      treenisessio = null;
      treenisessiot = null;
      treenikäynnit = null;
      treenikäynti = null;
      loggaaja.loggaa("Poisti treenisession '%s'", treenisessio);
      info("Treenisessio poistettu");
   }

   public void tallennaTreenikäynti()
   {
      treenikäynti = entityManager.merge(treenikäynti);
      entityManager.flush();
      treenikäyntiRSM.get(treenikäynti).setSelected(true);
      haeTreenikäynnit();
      loggaaja.loggaa("Tallensi treenikäynnin '%s'", treenikäynti);
      info("Treenikäynti tallennettu");
   }

   public void tallennaTreenisessio()
   {
      treenisessio = entityManager.merge(treenisessio);
      entityManager.flush();
      treenisessioRSM.get(treenisessio).setSelected(true);
      treenisessiot = null;
      loggaaja.loggaa("Tallensi treenisession '%s'", treenisessio);
      info("Treenisessio tallennettu");
   }

   public void harrastajaMuuttui(AjaxBehaviorEvent e)
   {
      Optional<Harrastaja> harrastaja = harrastajat.get().stream()
         .filter(h -> h.getNimi().equals(treenikäynti.getHarrastajaHaku())).findFirst();
      if (harrastaja.isPresent())
      {
         treenikäynti.setHarrastaja(harrastaja.get());
      }
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

   public void setTreenisessio(Treenisessio treenisessio)
   {
      this.treenisessio = treenisessio;
   }

   public void setTreenikäynti(Treenikäynti treenikäynti)
   {
      this.treenikäynti = treenikäynti;
   }

}
