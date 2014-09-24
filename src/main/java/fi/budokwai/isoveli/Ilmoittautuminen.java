package fi.budokwai.isoveli;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;
import org.icefaces.application.PushRenderer;
import org.icefaces.util.JavaScriptRunner;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Päivä;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenikäynti;

@Named
@SessionScoped
@Stateful
public class Ilmoittautuminen
{
   @PersistenceContext
   private EntityManager entityManager;

   private String korttinumero;
   private Treeni treeni;
   private Harrastaja harrastaja;
   private List<Treeni> treenit;
   private RowStateMap stateMap = new RowStateMap();

   private class Aikaraja
   {
      public Aikaraja(Päivä paiva, Date aika)
      {
         this.paiva = paiva;
         this.aika = aika;
      }

      private Päivä paiva;
      private Date aika;

      public Päivä getPaiva()
      {
         return paiva;
      }

      public Date getAika()
      {
         return aika;
      }
   }

   @PostConstruct
   public void init()
   {
      PushRenderer.addCurrentSession("ilmoittautuminen");
   }

   public Harrastaja getHarrastaja()
   {
      return harrastaja;
   }

   @Produces
   @Named
   public List<Treeni> getTreenit()
   {
      if (harrastaja == null)
      {
         return Collections.emptyList();
      }
      if (treenit == null)
      {
         haeTreenit();
      }
      return treenit;

   }

   private void haeTreenit()
   {
      Aikaraja aikaraja = haeAikaraja();
      treenit = entityManager.createNamedQuery("treenit", Treeni.class).setParameter("paiva", aikaraja.getPaiva())
         .setParameter("kello", aikaraja.getAika()).setParameter("harrastaja", harrastaja)
         .setParameter("tanaan", haeTanaanPvm()).getResultList();
   }

   private Aikaraja haeAikaraja()
   {
      Calendar kalenteri = Calendar.getInstance();
      int tunti = kalenteri.get(Calendar.HOUR_OF_DAY);
      int minuutti = kalenteri.get(Calendar.MINUTE);
      Päivä paiva = Päivä.values()[kalenteri.get(Calendar.DAY_OF_WEEK)];
      kalenteri.clear();
      kalenteri.set(Calendar.HOUR, tunti);
      kalenteri.set(Calendar.MINUTE, minuutti);
      return new Aikaraja(paiva, kalenteri.getTime());
   }

   public void lueKortti()
   {
      List<Harrastaja> kortinOmistaja = entityManager.createNamedQuery("kortti", Harrastaja.class)
         .setParameter("kortti", korttinumero).getResultList();
      if (kortinOmistaja.isEmpty())
      {
         harrastaja = Harrastaja.Tuntematon;
         JavaScriptRunner.runScript(FacesContext.getCurrentInstance(),
            "document.getElementById('formi:korttinumero').focus()");
         korttinumero = null;
         return;
      }
      harrastaja = kortinOmistaja.iterator().next();
   }

   public void treeniValittu(SelectEvent e)
   {
      treeni = (Treeni) e.getObject();
   }

   public String tallenna()
   {
      Date tanaan = haeTanaanPvm();
      Treenikäynti treenikaynti = new Treenikäynti(harrastaja, treeni, tanaan);
      entityManager.persist(treenikaynti);
      PushRenderer.render("ilmoittautuminen");
      nollaa();
      return "ilmoittautuminen?faces-redirect=true";
   }

   private Date haeTanaanPvm()
   {
      Calendar kalenteri = Calendar.getInstance();
      kalenteri.clear(Calendar.MINUTE);
      kalenteri.clear(Calendar.SECOND);
      kalenteri.clear(Calendar.MILLISECOND);
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

   public boolean isHarrastajaValittu()
   {
      return harrastaja != null && harrastaja != Harrastaja.Tuntematon;
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
      return "ilmoittautuminen?faces-redirect=true";
   }

   private void nollaa()
   {
      korttinumero = null;
      treeni = null;
      harrastaja = null;
      stateMap = new RowStateMap();
      treenit = null;
   }
}
