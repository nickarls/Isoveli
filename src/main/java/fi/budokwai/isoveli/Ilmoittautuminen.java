package fi.budokwai.isoveli;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.admin.Perustoiminnallisuus;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimustarkistukset;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenik�ynti;
import fi.budokwai.isoveli.malli.Treenisessio;
import fi.budokwai.isoveli.malli.Viikonp�iv�;

@Named
@SessionScoped
@Stateful
public class Ilmoittautuminen extends Perustoiminnallisuus
{
   @Inject
   private EntityManager entityManager;

   private String korttinumero;
   private Treeni treeni;
   private Harrastaja harrastaja;
   private List<Treeni> tulevatTreenit;
   private RowStateMap stateMap = new RowStateMap();
   private boolean treenioikeus = false;

   private class Aikaraja
   {
      public Aikaraja(Viikonp�iv� p�iv�, Date aika)
      {
         this.p�iv� = p�iv�;
         this.aika = aika;
      }

      private Viikonp�iv� p�iv�;
      private Date aika;

      public Viikonp�iv� getP�iv�()
      {
         return p�iv�;
      }

      public Date getAika()
      {
         return aika;
      }
   }

   @PostConstruct
   public void init()
   {
      // PushRenderer.addCurrentSession("ilmoittautuminen");
   }

   public boolean isMies()
   {
      return harrastaja != null && harrastaja.isMies() && !harrastaja.isKuvallinen();
   }

   public boolean isNainen()
   {
      return harrastaja != null && harrastaja.isNainen() && !harrastaja.isKuvallinen();
   }

   public boolean isOmaKuva()
   {
      return harrastaja != null && harrastaja.isKuvallinen();
   }

   @Produces
   @Named
   public Harrastaja getTreeniharrastaja()
   {
      return harrastaja;
   }

   @Produces
   @Named
   public List<Treeni> getTulevatTreenit()
   {
      if (harrastaja == null)
      {
         return new ArrayList<Treeni>();
      }
      if (tulevatTreenit == null && treenioikeus)
      {
         haeTulevatTreenit();
      }
      return tulevatTreenit;

   }

   private void haeTulevatTreenit()
   {
      Aikaraja aikaraja = haeAikaraja();
      tulevatTreenit = entityManager.createNamedQuery("tulevat_treenit", Treeni.class)
         .setParameter("p�iv�", aikaraja.getP�iv�()).setParameter("kello", aikaraja.getAika())
         .setParameter("harrastaja", harrastaja).setParameter("t�n��n", haeT�n��nPvm()).getResultList();
   }

   private Aikaraja haeAikaraja()
   {
      Calendar kalenteri = Calendar.getInstance();
      int tunti = kalenteri.get(Calendar.HOUR_OF_DAY);
      int minuutti = kalenteri.get(Calendar.MINUTE);
      kalenteri.setFirstDayOfWeek(Calendar.SUNDAY);
      Viikonp�iv� p�iv� = Viikonp�iv�.values()[kalenteri.get(Calendar.DAY_OF_WEEK) - 1];
      kalenteri.clear();
      kalenteri.set(Calendar.HOUR, tunti);
      kalenteri.set(Calendar.MINUTE, minuutti);
      return new Aikaraja(p�iv�, kalenteri.getTime());
   }

   public void lueKortti()
   {
      harrastaja = haeHarrastaja();
      if (harrastaja == null)
      {
         virhe("Tuntematon kortti %s", korttinumero);
         fokusoi("formi:korttinumero");
         korttinumero = null;
         return;
      }
      if (harrastaja.isInfotiskille())
      {
         virhe("Voisitko tulla infotiskille k�ym��n?");
      }
      Sopimustarkistukset sopimustarkistukset = harrastaja.getSopimusTarkistukset();
      treenioikeus = sopimustarkistukset.isOK();
      if (!treenioikeus)
      {
         sopimustarkistukset.getViestit().forEach(v -> virhe(v));
         virhe("Voisitko tulla infotiskille tarkistamaan maksut?");
         return;
      }
      if (harrastaja.isTauollaNyt())
      {
         virhe("Voisitko tulla infotiskille tarkistamaan treenitauon?");
         return;
      }
      haeTulevatTreenit();
      String nimi = harrastaja.getNimi();
      if (tulevatTreenit.isEmpty())
      {
         virhe("Tervetuloa %s, valitettavasti t�n��n ei en�� ole treenej�", nimi);
         return;
      }
      info("Tervetuloa %s, valitse treeni", nimi);
   }

   private Harrastaja haeHarrastaja()
   {
      List<Harrastaja> kortinOmistaja = entityManager.createNamedQuery("kortti", Harrastaja.class)
         .setParameter("kortti", korttinumero).getResultList();
      if (kortinOmistaja.isEmpty())
      {
         return null;
      }
      return kortinOmistaja.iterator().next();
   }

   public void valitseTreeni(Treeni treeni)
   {
      this.treeni = treeni;
   }

   public boolean isTreeniValittu(Treeni treeni)
   {
      if (this.treeni == null || treeni == null)
      {
         return false;
      }
      return this.treeni.getId() == treeni.getId();
   }

   public void tallenna()
   {
      Treenisessio treenisessio = haeTreenisessio();
      if (treenisessio.isTallentamaton())
      {
         for (Harrastaja h : treeni.getVet�j�t())
         {
            treenisessio.getVet�j�t().add(h);
         }
         treenisessio = entityManager.merge(treenisessio);
      }
      Treenik�ynti treenikaynti = new Treenik�ynti(harrastaja, treenisessio);
      harrastaja.getTreenik�ynnit().add(treenikaynti);
      Sopimus sopimus = harrastaja.getHarjoitteluoikeusSopimus();
      if (sopimus.getTyyppi().isTreenikertoja())
      {
         sopimus.k�yt�Treenikerta();
         sopimus = entityManager.merge(sopimus);
      }
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      nollaa();
      info("K�ynti rekister�ity");
      fokusoi("formi:korttinumero");
   }

   private Treenisessio haeTreenisessio()
   {
      Date t�n��n = haeT�n��nPvm();
      List<Treenisessio> treenisessiot = entityManager.createNamedQuery("treenisessio", Treenisessio.class)
         .setParameter("treeni", treeni).setParameter("p�iv�", t�n��n).getResultList();
      Treenisessio treenisessio = new Treenisessio();
      if (!treenisessiot.isEmpty())
      {
         treenisessio = treenisessiot.iterator().next();
      } else
      {
         treenisessio.setP�iv�(t�n��n);
         treenisessio.setTreeni(treeni);
      }
      return treenisessio;
   }

   private Date haeT�n��nPvm()
   {
      Calendar kalenteri = Calendar.getInstance();
      kalenteri.setTimeZone(TimeZone.getTimeZone("EET"));
      kalenteri.set(Calendar.HOUR_OF_DAY, 0);
      kalenteri.set(Calendar.MINUTE, 0);
      kalenteri.set(Calendar.SECOND, 0);
      kalenteri.set(Calendar.MILLISECOND, 0);
      return kalenteri.getTime();
   }

   public String getKorttinumero()
   {
      return korttinumero;
   }

   public void setKorttinumero(String korttinumero)
   {
      this.korttinumero = korttinumero;
   }

   public Treeni getTreeni()
   {
      return treeni;
   }

   public void setTreeni(Treeni treeni)
   {
      this.treeni = treeni;
   }

   public boolean isTreeniValittu()
   {
      return treeni != null;
   }

   public RowStateMap getStateMap()
   {
      return stateMap;
   }

   public void setStateMap(RowStateMap stateMap)
   {
      this.stateMap = stateMap;
   }

   public String peruuta()
   {
      nollaa();
      virhe("Toiminto peruttu");
      return "ilmoittautuminen?faces-redirect=true";
   }

   private void nollaa()
   {
      korttinumero = null;
      treeni = null;
      harrastaja = null;
      stateMap = new RowStateMap();
      tulevatTreenit = null;
      treenioikeus = false;
   }
}
