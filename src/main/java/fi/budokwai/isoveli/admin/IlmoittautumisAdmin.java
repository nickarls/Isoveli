package fi.budokwai.isoveli.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Järjestelmätilasto;
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

   public void treenisessioMuuttui(AjaxBehaviorEvent e)
   {
      treenikäynti.päivitäAikaleima();
   }

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
      return treenikäynti;
   }

   public Treenisessio lisääTreenisessio()
   {
      treenisessio = new Treenisessio();
      info("Uusi treenisessio alustettu");
      treenisessioRSM.setAllSelected(false);
      return treenisessio;
   }

   public void poistaTreenikäynti()
   {
      treenikäynti = entityManager.merge(treenikäynti);
      entityManager.remove(treenikäynti);
      entityManager.flush();
      treenikäynnit = null;
      treenisessiot = null;
      treenikäynti = null;
      info("Treenikäynti poistettu");
   }

   public void poistaTreenisessio()
   {
      treenisessio.tarkistaKäyttö();
      treenisessio = entityManager.merge(treenisessio);
      entityManager.remove(treenisessio);
      entityManager.flush();
      treenisessio = null;
      treenisessiot = null;
      treenikäynnit = null;
      treenikäynti = null;
      info("Treenisessio poistettu");
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
      treenisessiot = null;
      info("Treenisessio tallennettu");
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
