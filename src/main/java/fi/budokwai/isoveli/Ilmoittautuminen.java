package fi.budokwai.isoveli;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.admin.Perustoiminnallisuus;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Ilmoittautumistulos;
import fi.budokwai.isoveli.malli.Ilmoittautumistulos.Tila;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimustarkistukset;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenik‰ynti;
import fi.budokwai.isoveli.malli.Treenisessio;
import fi.budokwai.isoveli.malli.Viikonp‰iv‰;
import fi.budokwai.isoveli.util.Muuttui;

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
      public Aikaraja(Viikonp‰iv‰ p‰iv‰, Date aika)
      {
         this.p‰iv‰ = p‰iv‰;
         this.aika = aika;
      }

      private Viikonp‰iv‰ p‰iv‰;
      private Date aika;

      public Viikonp‰iv‰ getP‰iv‰()
      {
         return p‰iv‰;
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
      if (harrastaja == null || !treenioikeus)
      {
         return new ArrayList<Treeni>();
      }
      if (tulevatTreenit == null)
      {
         haeTulevatTreenit();
      }
      return tulevatTreenit;

   }

   public void treenimuutos(@Observes @Muuttui Treeni treeni)
   {
      tulevatTreenit = null;
   }

   private void haeTulevatTreenit()
   {
      Aikaraja aikaraja = haeAikaraja();
      tulevatTreenit = entityManager.createNamedQuery("tulevat_treenit", Treeni.class)
         .setParameter("p‰iv‰", aikaraja.getP‰iv‰()).setParameter("kello", aikaraja.getAika())
         .setParameter("ik‰", harrastaja.getIk‰()).setParameter("harrastaja", harrastaja)
         .setParameter("t‰n‰‰n", haeT‰n‰‰nPvm()).getResultList();
      tulevatTreenit = tulevatTreenit
         .stream()
         .filter(
            t -> t.getVyˆAlaraja() == null ? true : harrastaja.getTuoreinVyˆarvo().getJ‰rjestys() >= t.getVyˆAlaraja()
               .getJ‰rjestys()).collect(Collectors.toList());
      tulevatTreenit = tulevatTreenit
         .stream()
         .filter(
            t -> t.getVyˆYl‰raja() == null ? true : harrastaja.getTuoreinVyˆarvo().getJ‰rjestys() <= t.getVyˆYl‰raja()
               .getJ‰rjestys()).collect(Collectors.toList());

   }

   private Aikaraja haeAikaraja()
   {
      Calendar kalenteri = Calendar.getInstance();
      int tunti = kalenteri.get(Calendar.HOUR_OF_DAY);
      int minuutti = kalenteri.get(Calendar.MINUTE);
      kalenteri.setFirstDayOfWeek(Calendar.SUNDAY);
      Viikonp‰iv‰ p‰iv‰ = Viikonp‰iv‰.values()[kalenteri.get(Calendar.DAY_OF_WEEK) - 1];
      kalenteri.clear();
      kalenteri.set(Calendar.HOUR, tunti);
      kalenteri.set(Calendar.MINUTE, minuutti);
      return new Aikaraja(p‰iv‰, kalenteri.getTime());
   }

   private Ilmoittautumistulos tarkistaKortti(String korttinumero)
   {
      List<Harrastaja> omistaja = entityManager.createNamedQuery("kortti", Harrastaja.class)
         .setParameter("kortti", korttinumero).getResultList();
      if (omistaja.isEmpty())
      {
         return new Ilmoittautumistulos(null, Tila.KƒYTTƒJƒ_EI_L÷YTYNYT, String.format("Tuntematon korttinumero '%s'",
            korttinumero));
      }
      Harrastaja harrastaja = omistaja.iterator().next();
      if (harrastaja.isTauolla())
      {
         return new Ilmoittautumistulos(harrastaja, Tila.TAUOLLA,
            "Voisitko tulla infotiskille tarkistamaan treenitaukosi?");
      }
      Sopimustarkistukset tarkistukset = harrastaja.getSopimusTarkistukset();
      if (!tarkistukset.isOK())
      {
         return new Ilmoittautumistulos(harrastaja, Tila.SOPIMUSVIRHE,
            "Voisitko tulla infotiskille tarkistamaan sopimukset/maksut?");
      }
      return new Ilmoittautumistulos(harrastaja, Tila.OK, String.format("Tervetuloa %s, valitse treeni",
         harrastaja.getEtunimi()));
   }

   public Ilmoittautumistulos lueKortti()
   {
      harrastaja = null;
      Ilmoittautumistulos tulos = tarkistaKortti(korttinumero);
      switch (tulos.getTila())
      {
      case KƒYTTƒJƒ_EI_L÷YTYNYT:
         virhe(tulos.getViesti());
         fokusoi("formi:korttinumero");
         korttinumero = null;
         return tulos;
      case SOPIMUSVIRHE:
         virhe(tulos.getViesti());
         tulos.getSopimusvirheet().forEach(v -> virhe(v));
         break;
      case TAUOLLA:
         virhe(tulos.getViesti());
         break;
      case OK:
         break;
      default:
         break;
      }
      harrastaja = tulos.getHarrastaja();
      treenioikeus = tulos.treenioikeus();
      if (!tulos.OK())
      {
         return tulos;
      }
      if (tulos.infotiskille())
      {
         virhe("Voisitko tulla infotiskille k‰ym‰‰n?");
      }
      haeTulevatTreenit();
      if (tulevatTreenit.isEmpty())
      {
         virhe("Tervetuloa %s, valitettavasti t‰n‰‰n ei en‰‰ ole treenej‰", harrastaja.getEtunimi());
      } else
      {
         info("Tervetuloa %s, valitse treeni", harrastaja.getEtunimi());
      }
      return tulos;
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
         for (Harrastaja h : treeni.getVet‰j‰t())
         {
            treenisessio.getVet‰j‰t().add(h);
         }
      }
      Treenik‰ynti treenik‰ynti = new Treenik‰ynti();
      treenisessio.lis‰‰Treenik‰ynti(treenik‰ynti);
      harrastaja.lis‰‰Treenik‰ynti(treenik‰ynti);
      Sopimus sopimus = harrastaja.getHarjoitteluoikeusSopimus();
      if (sopimus.getTyyppi().isTreenikertoja())
      {
         sopimus.k‰yt‰Treenikerta();
         sopimus = entityManager.merge(sopimus);
      }
      treenisessio = entityManager.merge(treenisessio);
      harrastaja = entityManager.merge(harrastaja);
      entityManager.flush();
      nollaa();
      info("K‰ynti rekisterˆity");
      fokusoi("formi:korttinumero");
   }

   private Treenisessio haeTreenisessio()
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
