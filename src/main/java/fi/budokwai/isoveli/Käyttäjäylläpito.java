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
import fi.budokwai.isoveli.malli.J�ljell�Vy�kokeeseen;
import fi.budokwai.isoveli.malli.Vy�arvo;

@Stateful
@SessionScoped
@Named
public class K�ytt�j�yll�pito extends Perustoiminnallisuus
{
   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private Harrastaja itse;
   private List<Vy�arvo> vy�arvot;

   @PostConstruct
   public void init()
   {
      itse = entityManager.find(Harrastaja.class, 1);
      vy�arvot = entityManager.createNamedQuery("vy�arvot", Vy�arvo.class).getResultList();
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

   public List<GaugeSeries> getAikaaVy�kokeeseen()
   {
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      J�ljell�Vy�kokeeseen j�ljell�Vy�kokeeseen = itse.getJ�ljell�Vy�kokeeseen(vy�arvot);
      int max = j�ljell�Vy�kokeeseen.getSeuraavaVy�arvo().getMinimikuukaudet() * 30;
      sarja.setMax(max);
      sarja.setMin(0);
      int arvo = max - j�ljell�Vy�kokeeseen.getAika().getDays();
      sarja.setValue(arvo);
      sarja.setLabel("Aikarajoitus");
      data.add(sarja);
      return Arrays.asList(new GaugeSeries[]
      { sarja });
   }

   public List<GaugeSeries> getTreenej�Vy�kokeeseen()
   {
      List<GaugeSeries> data = new ArrayList<GaugeSeries>();
      GaugeSeries sarja = new GaugeSeries();
      J�ljell�Vy�kokeeseen j�ljell�Vy�kokeeseen = itse.getJ�ljell�Vy�kokeeseen(vy�arvot);
      int max = j�ljell�Vy�kokeeseen.getSeuraavaVy�arvo().getMinimitreenit();
      sarja.setMax(max);
      sarja.setMin(0);
      int arvo = (int) itse.getTreenej�ViimeVy�kokeesta();
      sarja.setValue(arvo);
      sarja.setLabel("Treenirajoitus");
      data.add(sarja);
      return Arrays.asList(new GaugeSeries[]
      { sarja });
   }

}
