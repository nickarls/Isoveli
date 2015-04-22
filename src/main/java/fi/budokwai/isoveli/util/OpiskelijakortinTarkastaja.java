package fi.budokwai.isoveli.util;

import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import fi.budokwai.isoveli.admin.Perustoiminnallisuus;
import fi.budokwai.isoveli.malli.Harrastaja;

@Singleton
@Startup
public class OpiskelijakortinTarkastaja extends Perustoiminnallisuus
{
   @Inject
   private EntityManager entityManager;

   @Schedules(
   { @Schedule(second = "0", minute = "0", hour = "0", dayOfMonth = "1", month = "2"),
         @Schedule(second = "0", minute = "0", hour = "0", dayOfMonth = "1", month = "10") })
   public void resetoi()
   {
      List<Harrastaja> opiskelijat = entityManager.createQuery(
         "select h from Harrastaja h join h.sopimukset s where s.tyyppi.opiskelija='K' and h.aktiivinen='K'",
         Harrastaja.class).getResultList();
      opiskelijat.forEach(h -> {
         h.setInfotiskille(true);
         h.lis‰‰Huomautus("N‰yt‰ opiskelijakortti");
         entityManager.merge(h);
      });
      entityManager.flush();
      loggaaja.loggaa("P‰ivitti %d opiskelijan korttitiedot", opiskelijat.size());
   }
}
