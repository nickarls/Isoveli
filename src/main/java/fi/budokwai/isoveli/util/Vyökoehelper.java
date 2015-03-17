package fi.budokwai.isoveli.util;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.J�ljell�Vy�kokeeseen;
import fi.budokwai.isoveli.malli.Vy�arvo;
import fi.budokwai.isoveli.malli.Vy�koe;

@SessionScoped
@Named
public class Vy�koehelper implements Serializable
{
   private static final long serialVersionUID = 1L;

   private List<Vy�arvo> vy�arvot;

   @Inject
   private EntityManager entityManager;

   @Inject
   @Named("harrastaja")
   private Instance<Harrastaja> harrastaja;

   @PostConstruct
   public void init()
   {
      haeVy�arvot();
   }

   public void vy�arvotMuuttui(@Observes @Muuttui Vy�arvo vy�arvo)
   {
      haeVy�arvot();
   }

   private void haeVy�arvot()
   {
      vy�arvot = entityManager.createNamedQuery("vy�arvot", Vy�arvo.class).getResultList();
   }

   @Produces
   @Named
   public List<Vy�arvo> getVy�arvot()
   {
      return vy�arvot;
   }

   public J�ljell�Vy�kokeeseen getJ�ljell�Vy�kokeeseen(Harrastaja harrastaja)
   {
      Vy�arvo nykyinenVy�arvo = harrastaja.getTuoreinVy�arvo();
      Vy�arvo seuraavaVy�arvo = Vy�arvo.EI_OOTA;
      if (nykyinenVy�arvo == Vy�arvo.EI_OOTA)
      {
         seuraavaVy�arvo = haeEnsimm�inenVy�arvo();
      } else
      {
         seuraavaVy�arvo = haeSeuraavaVy�arvo(nykyinenVy�arvo);
      }
      if (seuraavaVy�arvo == Vy�arvo.EI_OOTA)
      {
         return J�ljell�Vy�kokeeseen.EI_OOTA;
      }
      long tarvittavaTreenim��r� = seuraavaVy�arvo.getMinimitreenit() - harrastaja.getTreenej�ViimeVy�kokeesta()
         - harrastaja.getSiirtotreenej�();
      LocalDate koskaViimeisinKoe = null;
      if (harrastaja.getTuoreinVy�koe() == Vy�koe.EI_OOTA)
      {
         koskaViimeisinKoe = DateUtil.Date2LocalDate(harrastaja.getLuotu());
      } else
      {
         koskaViimeisinKoe = harrastaja.getTuoreinVy�koe().getKoska();
      }
      Period tarvittavaAika = Period.between(DateUtil.t�n��n(),
         koskaViimeisinKoe.plus(seuraavaVy�arvo.getMinimikuukaudet(), ChronoUnit.MONTHS));
      long tarvittavatP�iv�t = ChronoUnit.DAYS.between(DateUtil.t�n��n(),
         koskaViimeisinKoe.plus(seuraavaVy�arvo.getMinimikuukaudet(), ChronoUnit.MONTHS));
      return new J�ljell�Vy�kokeeseen(tarvittavaAika, tarvittavaTreenim��r�, tarvittavatP�iv�t, seuraavaVy�arvo);
   }

   public Vy�arvo haeSeuraavaVy�arvo(Vy�arvo nykyinenVy�arvo)
   {
      if (nykyinenVy�arvo == Vy�arvo.EI_OOTA)
      {
         return haeEnsimm�inenVy�arvo();
      }
      Optional<Vy�arvo> seuraavaVy�arvo = vy�arvot.stream()
         .filter(va -> harrastaja.get().getIk�() < 16 ? va.isPoom() : va.isDan())
         .filter(va -> va.getJ�rjestys() > nykyinenVy�arvo.getJ�rjestys()).findFirst();
      if (seuraavaVy�arvo.isPresent())
      {
         return seuraavaVy�arvo.get();
      } else
      {
         return Vy�arvo.EI_OOTA;
      }
   }

   private Vy�arvo haeEnsimm�inenVy�arvo()
   {
      return vy�arvot.iterator().next();
   }
}
