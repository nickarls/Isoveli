package fi.budokwai.isoveli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.icefaces.ace.model.chart.GaugeSeries;

import fi.budokwai.isoveli.admin.Perustoiminnallisuus;
import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.JäljelläVyökokeeseen;
import fi.budokwai.isoveli.malli.Vyöarvo;

@Stateful
@SessionScoped
@Named
public class Käyttäjäylläpito extends Perustoiminnallisuus
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private Harrastaja itse;
   private List<Vyöarvo> vyöarvot;

   @PostConstruct
   public void init()
   {
      itse = entityManager.find(Harrastaja.class, 1);
      vyöarvot = entityManager.createNamedQuery("vyöarvot", Vyöarvo.class).getResultList();
   }

   @Produces
   @Named
   public Harrastaja getItse()
   {
      return itse;
   }

   public void tallennaItse()
   {
      entityManager.persist(itse);
      info("Tiedot tallennettu");
   }

   public List<GaugeSeries> getAikaaVyökokeeseen()
   {
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      JäljelläVyökokeeseen jäljelläVyökokeeseen = itse.getJäljelläVyökokeeseen(vyöarvot);
      int max = jäljelläVyökokeeseen.getSeuraavaVyöarvo().getMinimikuukaudet() * 30;
      sarja.setMax(max);
      sarja.setMin(0);
      int arvo = max - jäljelläVyökokeeseen.getAika().getDays();
      sarja.setValue(arvo);
      sarja.setLabel("Aikarajoitus");
      data.add(sarja);
      return Arrays.asList(new GaugeSeries[]
      { sarja });
   }

   public List<GaugeSeries> getTreenejäVyökokeeseen()
   {
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      JäljelläVyökokeeseen jäljelläVyökokeeseen = itse.getJäljelläVyökokeeseen(vyöarvot);
      int max = jäljelläVyökokeeseen.getSeuraavaVyöarvo().getMinimitreenit();
      sarja.setMax(max);
      sarja.setMin(0);
      int arvo = (int) itse.getTreenejäViimeVyökokeesta();
      sarja.setValue(arvo);
      sarja.setLabel("Treenirajoitus");
      data.add(sarja);
      return Arrays.asList(new GaugeSeries[]
      { sarja });
   }

}
