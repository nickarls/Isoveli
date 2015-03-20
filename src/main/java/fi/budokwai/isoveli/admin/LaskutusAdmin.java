package fi.budokwai.isoveli.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;

import org.icefaces.ace.component.column.Column;
import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import com.google.common.io.ByteStreams;

import fi.budokwai.isoveli.Asetukset;
import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.LaskuTila;
import fi.budokwai.isoveli.util.DateUtil;
import fi.budokwai.isoveli.util.Lasku2PDF;
import fi.budokwai.isoveli.util.Loggaaja;
import fi.budokwai.isoveli.util.MailManager;
import fi.budokwai.isoveli.util.Zippaaja;

@Stateful
@SessionScoped
@Named
public class LaskutusAdmin extends Perustoiminnallisuus
{
   @Inject
   private EntityManager entityManager;

   @Inject
   private MailManager mailManager;

   @Inject
   private Loggaaja loggaaja;

   private RowStateMap laskuRSM = new RowStateMap();
   private RowStateMap laskuttamattomatRSM = new RowStateMap();
   private RowStateMap laskuriviRSM = new RowStateMap();

   private List<Sopimus> laskuttamattomat;
   private List<Lasku> laskut;
   private Lasku lasku;
   private Sopimus sopimus;
   private List<SelectItem> tilasuodatukset;

   private String kuitattavatLaskut;

   @PostConstruct
   public void init()
   {
      tilasuodatukset = new ArrayList<SelectItem>();
      tilasuodatukset.add(new SelectItem("", "Kaikki", "Kaikki", false, false, true));
      tilasuodatukset.add(new SelectItem(LaskuTila.M.name(), LaskuTila.M.getNimi(), LaskuTila.M.getNimi(), false,
         false, false));
      tilasuodatukset.add(new SelectItem(LaskuTila.L.name(), LaskuTila.L.getNimi(), LaskuTila.L.getNimi(), false,
         false, false));
      tilasuodatukset.add(new SelectItem(LaskuTila.K.name(), LaskuTila.K.getNimi(), LaskuTila.K.getNimi(), false,
         false, false));
      tilasuodatukset.add(new SelectItem(LaskuTila.X.name(), LaskuTila.X.getNimi(), LaskuTila.X.getNimi(), false,
         false, false));
   }

   @Inject
   private Asetukset asetukset;

   @Produces
   @Named
   public Lasku getLasku()
   {
      return lasku;
   }

   @Produces
   @Named
   public Sopimus getLaskuttamatonSopimus()
   {
      return sopimus;
   }

   @Produces
   @Named
   public List<SelectItem> getTilasuodatukset()
   {
      return tilasuodatukset;
   }

   public void kuittaaLaskut()
   {
      if (kuitattavatLaskut == null)
      {
         return;
      }
      int virheit� = 0;
      int kuitattuja = 0;
      String[] viitenumerot = kuitattavatLaskut.split("\n");
      loggaaja.loggaa("Kuittaa %d laskua", viitenumerot.length);
      for (String viitenumero : viitenumerot)
      {
         Lasku lasku = null;
         try
         {
            lasku = entityManager.createNamedQuery("lasku_viitenumero", Lasku.class)
               .setParameter("viitenumero", viitenumero).getSingleResult();
         } catch (NoResultException e)
         {
            loggaaja.loggaa("Kuitattavaa laskua viitenumerolla '%s' ei l�ytynyt", viitenumero);
            virheit�++;
         }
         if (lasku == null)
         {
            continue;
         }
         lasku.setMaksettu(new Date());
         lasku.setTila(LaskuTila.K);
         entityManager.persist(lasku);
         entityManager.flush();
         kuitattuja++;
         loggaaja.loggaa("Lasku viitenumerolla '%s' kuitattu", viitenumero);
      }
      info("Kuittaus on suoritettu, onnistuneita on %d ja ep�onnistuineita %d. Katso mahdolliset virheet logista",
         kuitattuja, virheit�);
      kuitattavatLaskut = null;
   }

   public void l�het�Laskut()
   {
      List<Lasku> laskuttamattomat = entityManager.createNamedQuery("laskuttamattomat_laskut", Lasku.class)
         .getResultList();
      laskuttamattomat.forEach(lasku -> {
         try
         {
            mailManager.l�het�S�hk�posti(lasku.getHenkil�().getYhteystiedot().getS�hk�posti(), "Lasku", "Liitteen�",
               lasku.getPdf());
            lasku.setLaskutettu(true);
            lasku = entityManager.merge(lasku);
         } catch (IsoveliPoikkeus e)
         {

         }
      });
   }

   public void laskutaSopimukset()
   {
      List<Sopimus> sopimukset = haeLaskuttamattomat();
      Map<Osoite, List<Sopimus>> sopimuksetPerOsoite = sopimukset.stream().collect(
         Collectors.groupingBy(sopimus -> sopimus.getLaskutusosoite()));
      sopimuksetPerOsoite.values().forEach(osoitteenSopimukset -> teeLaskuOsoitteelle(osoitteenSopimukset));
      laskuttamattomat = haeLaskuttamattomat();
      info("Muodosti %d sopimuksesta %d laskua", sopimukset.size(), sopimuksetPerOsoite.keySet().size());
      loggaaja.loggaa("Suoritti laskutusajon");
   }

   public Lasku x(List<Sopimus> sopimukset)
   {
      if (sopimukset.isEmpty())
      {
         return new Lasku();
      }
      Henkil� henkil� = haeLaskunVastaanottaja(sopimukset);
      Lasku lasku = new Lasku(henkil�);
      for (Sopimus sopimus : sopimukset)
      {
         lasku.lis��Rivit(sopimus.laskuta());
         if (sopimus.getTyyppi().isTreenikertoja())
         {
            sopimus.lis��Treenikertoja();
            sopimus = entityManager.merge(sopimus);
         }
      }
      lasku.laskePerhealennukset();
      return lasku;
   }

   private void teeLaskuOsoitteelle(List<Sopimus> sopimukset)
   {
      if (sopimukset.isEmpty())
      {
         return;
      }
      Henkil� henkil� = haeLaskunVastaanottaja(sopimukset);
      Lasku lasku = new Lasku(henkil�);
      for (Sopimus sopimus : sopimukset)
      {
         lasku.lis��Rivit(sopimus.laskuta());
         if (sopimus.getTyyppi().isTreenikertoja())
         {
            sopimus.lis��Treenikertoja();
            sopimus = entityManager.merge(sopimus);
         }
      }
      lasku.laskePerhealennukset();
      entityManager.persist(lasku);
      lasku.laskeViitenumero();
      byte[] pdf = teePdfLasku(lasku);
      lasku.setPdf(BlobData.PDF(String.format("lasku-%d", lasku.getId()), pdf));
      entityManager.persist(lasku);
      loggaaja.loggaa(String.format("Teki laskun henkil�lle %s", henkil�.getNimi()));
   }

   private Henkil� haeLaskunVastaanottaja(List<Sopimus> sopimukset)
   {
      List<Henkil�> huoltajat = sopimukset.stream().filter(sopimus -> sopimus.getHarrastaja().getHuoltaja() != null)
         .map(sopimus -> sopimus.getHarrastaja().getHuoltaja()).collect(Collectors.toList());
      if (!huoltajat.isEmpty())
      {
         return huoltajat.stream().findFirst().get();
      }
      List<Henkil�> t�ysiik�isetPerheenj�senet = sopimukset.stream().filter(s -> s.getHarrastaja().getPerhe() != null)
         .flatMap(s -> s.getHarrastaja().getPerhe().getPerheenj�senet().stream()).filter(h -> !h.isAlaik�inen())
         .collect(Collectors.toList());
      if (!t�ysiik�isetPerheenj�senet.isEmpty())
      {
         return t�ysiik�isetPerheenj�senet.stream().findFirst().get();
      }
      List<Henkil�> t�ysiik�iset = sopimukset.stream().map(sopimus -> sopimus.getHarrastaja())
         .filter(harrastaja -> !harrastaja.isAlaik�inen()).collect(Collectors.toList());
      if (!t�ysiik�iset.isEmpty())
      {
         return t�ysiik�iset.stream().findFirst().get();
      }
      return sopimukset.stream().findFirst().get().getHarrastaja();
   }

   private byte[] teePdfLasku(Lasku lasku)
   {
      byte[] malli = null;
      Optional<BlobData> mallit = entityManager.createNamedQuery("nimetty_blobdata", BlobData.class)
         .setParameter("nimi", "laskupohja").getResultList().stream().findFirst();
      if (mallit.isPresent())
      {
         malli = mallit.get().getTieto();
      } else
      {
         try
         {
            malli = ByteStreams.toByteArray(getClass().getClassLoader().getResource("laskupohja.pdf").openStream());
         } catch (IOException e)
         {
            throw new IsoveliPoikkeus("Mallin lukeminen ep�onnistui", e);
         }
      }
      return new Lasku2PDF(malli, lasku, asetukset).muodosta();
   }

   @Produces
   @Named
   public List<Sopimus> getLaskuttamattomat()
   {
      if (laskuttamattomat == null)
      {
         haeLaskuttamattomat();
      }
      return laskuttamattomat;
   }

   @Produces
   @Named
   public List<Lasku> getLaskut()
   {
      if (laskut == null)
      {
         haeLaskut();
      }
      return laskut;
   }

   private void haeLaskut()
   {
      laskut = entityManager.createNamedQuery("laskut", Lasku.class).getResultList();
   }

   private List<Sopimus> haeLaskuttamattomat()
   {
      laskuttamattomat = entityManager.createNamedQuery("laskuttamattomat_sopimukset", Sopimus.class)
         .setParameter("nyt", DateUtil.t�n��nDate()).getResultList();
      laskuttamattomat.addAll(entityManager.createNamedQuery("uudet_sopimukset", Sopimus.class)
         .setParameter("nyt", DateUtil.t�n��nDate()).getResultList());
      laskuttamattomat.addAll(entityManager.createNamedQuery("laskuttamattomat_kymppikerrat", Sopimus.class)
         .getResultList());
      return laskuttamattomat;
   }

   public void tabiMuuttui(ValueChangeEvent e)
   {
      int uusiTabi = (int) e.getNewValue();
      switch (uusiTabi)
      {
      case 0:
         laskut = null;
      case 1:
         laskuttamattomat = null;
      }
      entityManager.clear();
   }

   public void laskuValittu(SelectEvent e)
   {
      lasku = (Lasku) e.getObject();
   }

   public void sopimusValittu(SelectEvent e)
   {
      sopimus = (Sopimus) e.getObject();
   }

   public void piilotaSopimus()
   {
      sopimus = null;
   }

   public void piilotaLasku()
   {
      lasku = null;
   }

   public void poistaLasku()
   {
      // lasku.getLaskurivit().forEach(l -> {
      // l.getSopimus().setLaskurivi(null);
      // l.setSopimus(null);
      // entityManager.persist(l);
      // });
      // entityManager.remove(lasku);
      // info("Lasku poistettu");
      // laskut = null;
      // haeLaskut();
   }

   public RowStateMap getLaskuRSM()
   {
      return laskuRSM;
   }

   public void setLaskuRSM(RowStateMap laskuRSM)
   {
      this.laskuRSM = laskuRSM;
   }

   public RowStateMap getLaskuttamattomatRSM()
   {
      return laskuttamattomatRSM;
   }

   public void setLaskuttamattomatRSM(RowStateMap laskuttamattomatRSM)
   {
      this.laskuttamattomatRSM = laskuttamattomatRSM;
   }

   public BlobData zippaaLaskuttamattomat()
   {
      List<Lasku> laskuttamattomat = entityManager.createNamedQuery("laskuttamattomat_laskut", Lasku.class)
         .getResultList();
      Zippaaja zippaaja = new Zippaaja();
      laskuttamattomat.forEach(lasku -> {
         zippaaja.lis��ZipTiedostoon(String.format("%s.pdf", lasku.getHenkil�().getNimi()), lasku.getPdf().getTieto());
      });
      return zippaaja.haeZipTiedosto("laskuttamattomat");
   }

   public void tallennaRivi(AjaxBehaviorEvent e)
   {
      Lasku lasku = (Lasku) laskuRSM.getSelected().iterator().next();
      lasku = entityManager.merge(lasku);
      entityManager.flush();
      info("Rivi muokattu ja lasku tallennettu");
   }

   public void tallennaLasku()
   {
      lasku = entityManager.merge(lasku);
      info("Rivi ja lasku tallennettu");
   }

   public void poistaRivi(Laskurivi laskurivi)
   {
      lasku.getLaskurivit().remove(laskurivi);
      lasku = entityManager.merge(lasku);
      entityManager.flush();
      info("Rivi poistettu ja lasku tallennettu");
   }

   public void lis��Rivi()
   {
      Laskurivi uusiRivi = new Laskurivi();
      lasku.lis��Rivi(uusiRivi);
      DataTable t = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:laskurivit");
      for (Column c : t.getColumns())
      {
         if (c.getCellEditor() != null)
         {
            laskuriviRSM.get(uusiRivi).addActiveCellEditor(c.getCellEditor());
         }
      }
   }

   public RowStateMap getLaskuriviRSM()
   {
      return laskuriviRSM;
   }

   public void setLaskuriviRSM(RowStateMap laskuriviRSM)
   {
      this.laskuriviRSM = laskuriviRSM;
   }

   public String getKuitattavatLaskut()
   {
      return kuitattavatLaskut;
   }

   public void setKuitattavatLaskut(String kuitattavatLaskut)
   {
      this.kuitattavatLaskut = kuitattavatLaskut;
   }

   public void setLasku(Lasku lasku)
   {
      this.lasku = lasku;
   }

}
