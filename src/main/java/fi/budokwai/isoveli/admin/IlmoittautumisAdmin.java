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
import fi.budokwai.isoveli.malli.J�rjestelm�tilasto;
import fi.budokwai.isoveli.malli.Treenik�ynti;
import fi.budokwai.isoveli.malli.Treenisessio;

@Stateful
@Named
@SessionScoped
public class IlmoittautumisAdmin extends Perustoiminnallisuus
{
   @Inject
   private EntityManager entityManager;

   private List<Treenik�ynti> treenik�ynnit;
   private Treenik�ynti treenik�ynti;
   private Treenisessio treenisessio;
   private List<Treenisessio> treenisessiot;
   private List<Harrastaja> kaikkivet�j�t;
   private List<Harrastaja> sessiovet�j�t;

   private RowStateMap treenik�yntiRSM = new RowStateMap();
   private RowStateMap treenisessioRSM = new RowStateMap();

   public void treenisessioMuuttui(AjaxBehaviorEvent e)
   {
      treenik�ynti.p�ivit�Aikaleima();
   }

   @Produces
   @Named
   public J�rjestelm�tilasto getTilasto()
   {
      long harrastajia = entityManager.createQuery("select count(h) from Harrastaja h", Long.class).getSingleResult();
      long treenej� = entityManager.createQuery("select count(t) from Treenisessio t", Long.class).getSingleResult();
      long treenik�yntej� = entityManager.createQuery("select count(t) from Treenik�ynti t", Long.class)
         .getSingleResult();
      long vy�arvoja = entityManager.createQuery("select count(v) from Vy�koe v", Long.class).getSingleResult();
      return new J�rjestelm�tilasto(harrastajia, treenej�, treenik�yntej�, vy�arvoja);
   }

   @Produces
   @Named
   public Treenik�ynti getTreenik�ynti()
   {
      return treenik�ynti;
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
   public List<Harrastaja> getSessiovet�j�t()
   {
      if (treenisessio == null)
      {
         return new ArrayList<Harrastaja>();
      }
      if (kaikkivet�j�t == null)
      {
         kaikkivet�j�t = entityManager.createNamedQuery("treenivet�j�t", Harrastaja.class).getResultList();
         kaikkivet�j�t = kaikkivet�j�t.stream().filter(h -> h.isTreenienVet�j�()).collect(Collectors.toList());
      }
      if (sessiovet�j�t == null)
      {
         sessiovet�j�t = new ArrayList<Harrastaja>();
         sessiovet�j�t.addAll(kaikkivet�j�t);
         sessiovet�j�t.removeAll(treenisessio.getVet�j�t());
      }
      return sessiovet�j�t;
   }

   public void treenisessioValittu(SelectEvent e)
   {
      treenisessio = (Treenisessio) e.getObject();
      sessiovet�j�t = null;
   }

   public void treenik�yntiValittu(SelectEvent e)
   {
      treenik�ynti = (Treenik�ynti) e.getObject();
   }

   public void tabiMuuttui(ValueChangeEvent e)
   {
      int uusiTabi = (int) e.getNewValue();
      if (uusiTabi == 0)
      {
         treenik�ynnit = null;
         treenik�ynti = null;
         treenik�yntiRSM.setAllSelected(false);
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
   public List<Treenik�ynti> getTreenik�ynnit()
   {
      if (treenik�ynnit == null)
      {
         haeTreenik�ynnit();
      }
      return treenik�ynnit;
   }

   private void haeTreenik�ynnit()
   {
      treenik�ynnit = entityManager.createNamedQuery("treenik�ynnit", Treenik�ynti.class).getResultList();
   }

   private void haeTreenisessiot()
   {
      treenisessiot = entityManager.createNamedQuery("kaikki_treenisessiot", Treenisessio.class).getResultList();
   }

   public Treenik�ynti lis��Treenik�ynti()
   {
      treenik�ynti = new Treenik�ynti();
      info("Uusi treenik�ynti alustettu");
      treenik�yntiRSM.setAllSelected(false);
      return treenik�ynti;
   }

   public Treenisessio lis��Treenisessio()
   {
      treenisessio = new Treenisessio();
      info("Uusi treenisessio alustettu");
      treenisessioRSM.setAllSelected(false);
      return treenisessio;
   }

   public void poistaTreenik�ynti()
   {
      treenik�ynti = entityManager.merge(treenik�ynti);
      entityManager.remove(treenik�ynti);
      entityManager.flush();
      treenik�ynnit = null;
      treenisessiot = null;
      treenik�ynti = null;
      info("Treenik�ynti poistettu");
   }

   public void poistaTreenisessio()
   {
      treenisessio.tarkistaK�ytt�();
      treenisessio = entityManager.merge(treenisessio);
      entityManager.remove(treenisessio);
      entityManager.flush();
      treenisessio = null;
      treenisessiot = null;
      treenik�ynnit = null;
      treenik�ynti = null;
      info("Treenisessio poistettu");
   }

   public void tallennaTreenik�ynti()
   {
      treenik�ynti = entityManager.merge(treenik�ynti);
      entityManager.flush();
      treenik�yntiRSM.get(treenik�ynti).setSelected(true);
      haeTreenik�ynnit();
      info("Treenik�ynti tallennettu");
   }

   public void tallennaTreenisessio()
   {
      treenisessio = entityManager.merge(treenisessio);
      entityManager.flush();
      treenisessioRSM.get(treenisessio).setSelected(true);
      treenisessiot = null;
      info("Treenisessio tallennettu");
   }

   public RowStateMap getTreenik�yntiRSM()
   {
      return treenik�yntiRSM;
   }

   public void setTreenik�yntiRSM(RowStateMap treenik�yntiRSM)
   {
      this.treenik�yntiRSM = treenik�yntiRSM;
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

   public void setTreenik�ynti(Treenik�ynti treenik�ynti)
   {
      this.treenik�ynti = treenik�ynti;
   }

}
