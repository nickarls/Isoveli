package fi.budokwai.isoveli.util;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.JäljelläVyökokeeseen;
import fi.budokwai.isoveli.malli.Vyöarvo;
import fi.budokwai.isoveli.malli.Vyökoe;
import fi.budokwai.isoveli.malli.Vyökoetilaisuus;

@SessionScoped
@Named
public class Vyökoehelper implements Serializable
{
   private static final long serialVersionUID = 1L;

   private List<Vyöarvo> vyöarvot;

   @Inject
   private EntityManager entityManager;

   @PostConstruct
   public void init()
   {
      haeVyöarvot();
   }

   public void vyöarvotMuuttui(@Observes @Muuttui Vyöarvo vyöarvo)
   {
      haeVyöarvot();
   }

   private void haeVyöarvot()
   {
      vyöarvot = entityManager.createNamedQuery("vyöarvot", Vyöarvo.class).getResultList();
   }

   @Produces
   @Named
   public List<Vyöarvo> getVyöarvot()
   {
      return vyöarvot;
   }

   public JäljelläVyökokeeseen getJäljelläVyökokeeseen(Harrastaja harrastaja)
   {
      Vyöarvo nykyinenVyöarvo = harrastaja.getTuoreinVyöarvo();
      Vyöarvo seuraavaVyöarvo = Vyöarvo.EI_OOTA;
      if (nykyinenVyöarvo == Vyöarvo.EI_OOTA)
      {
         seuraavaVyöarvo = haeEnsimmäinenVyöarvo();
      } else
      {
         seuraavaVyöarvo = haeSeuraavaVyöarvo(harrastaja);
      }
      if (seuraavaVyöarvo == Vyöarvo.EI_OOTA)
      {
         return JäljelläVyökokeeseen.EI_OOTA;
      }
      long tarvittavaTreenimäärä = seuraavaVyöarvo.getMinimitreenit() - harrastaja.getTreenejäViimeVyökokeesta()
         - harrastaja.getSiirtotreenejä();
      LocalDate koskaViimeisinKoe = null;
      if (harrastaja.getTuoreinVyökoe() == Vyökoe.EI_OOTA)
      {
         koskaViimeisinKoe = DateUtil.Date2LocalDate(harrastaja.getLuotu());
      } else
      {
         koskaViimeisinKoe = harrastaja.getTuoreinVyökoe().getKoska();
      }
      Period tarvittavaAika = Period.between(DateUtil.tänään(),
         koskaViimeisinKoe.plus(seuraavaVyöarvo.getMinimikuukaudet(), ChronoUnit.MONTHS));
      long tarvittavatPäivät = ChronoUnit.DAYS.between(DateUtil.tänään(),
         koskaViimeisinKoe.plus(seuraavaVyöarvo.getMinimikuukaudet(), ChronoUnit.MONTHS));
      return new JäljelläVyökokeeseen(tarvittavaAika, tarvittavaTreenimäärä, tarvittavatPäivät, seuraavaVyöarvo);
   }

   public Vyöarvo haeSeuraavaVyöarvo(Harrastaja harrastaja)
   {
      if (harrastaja.getTuoreinVyöarvo() == Vyöarvo.EI_OOTA)
      {
         return haeEnsimmäinenVyöarvo();
      }
      List<Vyöarvo> vyöt = null;
      if (harrastaja.getIkä() < 16)
      {
         vyöt = vyöarvot.stream().filter(v -> !v.isDan()).collect(Collectors.toList());
      } else
      {
         vyöt = vyöarvot.stream().filter(v -> !v.isPoom()).collect(Collectors.toList());
      }
      Optional<Vyöarvo> seuraavaVyöarvo = vyöt.stream()
         .filter(va -> va.getJärjestys() > harrastaja.getTuoreinVyöarvo().getJärjestys()).findFirst();
      if (seuraavaVyöarvo.isPresent())
      {
         return seuraavaVyöarvo.get();
      } else
      {
         return Vyöarvo.EI_OOTA;
      }
   }

   private Vyöarvo haeEnsimmäinenVyöarvo()
   {
      return vyöarvot.iterator().next();
   }

   public List<String> onkoKokelas(Harrastaja harrastaja, Vyökoetilaisuus vyökoetilaisuus)
   {
      List<String> huomautukset = new ArrayList<>();
      JäljelläVyökokeeseen jäljellä = getJäljelläVyökokeeseen(harrastaja);
      if (jäljellä.getTreenikertoja() > 0)
      {
         huomautukset.add(String.format("Treenikertoja puuttuu %d", jäljellä.getTreenikertoja()));
      }
      if (jäljellä.getPäiviä() > 0)
      {
         huomautukset.add(String.format("Treenipäiviä puuttuu %d", jäljellä.getPäiviä()));
      }
      if (vyökoetilaisuus.getIkäAlaraja() != null && harrastaja.getIkä() < vyökoetilaisuus.getIkäAlaraja())
      {
         huomautukset.add(String.format("Alaikäraja on %d mutta ikää %s", vyökoetilaisuus.getIkäAlaraja(),
            harrastaja.getIkä()));
      }
      if (vyökoetilaisuus.getIkäYläraja() != null && harrastaja.getIkä() > vyökoetilaisuus.getIkäYläraja())
      {
         huomautukset.add(String.format("Yläikäraja on %d mutta ikää %s", vyökoetilaisuus.getIkäYläraja(),
            harrastaja.getIkä()));
      }
      Vyöarvo uusiVyöarvo = haeSeuraavaVyöarvo(harrastaja);
      if (vyökoetilaisuus.getVyöAlaraja() != null
         && uusiVyöarvo.getJärjestys() < vyökoetilaisuus.getVyöAlaraja().getJärjestys())
      {
         huomautukset.add(String.format("Alavyöraja on %s mutta vyöarvo on %s", vyökoetilaisuus.getVyöAlaraja()
            .getNimi(), uusiVyöarvo.getNimi()));
      }
      if (vyökoetilaisuus.getVyöYläraja() != null
         && uusiVyöarvo.getJärjestys() > vyökoetilaisuus.getVyöYläraja().getJärjestys())
      {
         huomautukset.add(String.format("Ylävyöraja on %s mutta vyöarvo on %s", vyökoetilaisuus.getVyöYläraja()
            .getNimi(), uusiVyöarvo.getNimi()));
      }
      return huomautukset;
   }
}
