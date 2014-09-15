package fi.budokwai.isoveli;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;
import org.icefaces.application.PushRenderer;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Paiva;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenikaynti;

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
   private RowStateMap stateMap = new RowStateMap();

   private class Aikaraja
   {
      public Aikaraja(Paiva paiva, Date aika)
      {
         this.paiva = paiva;
         this.aika = aika;
      }

      private Paiva paiva;
      private Date aika;

      public Paiva getPaiva()
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

   @Produces
   @Named
   public List<Treeni> getTreenit()
   {

      Aikaraja aikaraja = haeAikaraja();
      return entityManager.createNamedQuery("treenit", Treeni.class).setParameter("paiva", aikaraja.getPaiva())
         .setParameter("kello", aikaraja.getAika()).getResultList();
   }

   private Aikaraja haeAikaraja()
   {
      Calendar kalenteri = Calendar.getInstance();
      int tunti = kalenteri.get(Calendar.HOUR);
      int minuutti = kalenteri.get(Calendar.MINUTE);
      Paiva paiva = Paiva.values()[kalenteri.get(Calendar.DAY_OF_WEEK)];
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
         String viesti = String.format("Kortti %s tuntematon, ota yhteytt‰ p‰ivyst‰j‰‰n", korttinumero);
         FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, viesti, viesti));
         return;
      }
      harrastaja = kortinOmistaja.iterator().next();
      String viesti = String.format("Tervetuloa %s, valitse treeni", harrastaja.getNimi());
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, viesti, viesti));

   }

   public void treeniValittu(SelectEvent e)
   {
      treeni = (Treeni) e.getObject();
   }

   public String tallenna()
   {
      Treenikaynti treenikaynti = new Treenikaynti(harrastaja, treeni);
      entityManager.persist(treenikaynti);
      PushRenderer.render("ilmoittautuminen");
      nollaa();
      return "ilmoittautuminen?faces-redirect=true";
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
      return harrastaja != null;
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
   }
}
