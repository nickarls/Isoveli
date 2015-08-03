package fi.budokwai.isoveli.admin;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.icefaces.ace.component.column.Column;
import org.icefaces.ace.component.datatable.DataTable;
import org.icefaces.ace.event.RowEditEvent;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import com.google.common.io.ByteStreams;

import fi.budokwai.isoveli.Asetukset;
import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Henkil�;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.LaskuTila;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Sopimus;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.util.DateUtil;
import fi.budokwai.isoveli.util.Lasku2PDF;
import fi.budokwai.isoveli.util.Loggaaja;
import fi.budokwai.isoveli.util.MailManager;
import fi.budokwai.isoveli.util.Tulostaja;

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
   private RowStateMap laskuriviRSM = new RowStateMap();

   private List<Sopimus> laskuttamattomatSopimukset;
   private List<Lasku> l�hett�m�tt�m�tLaskut;
   private List<Lasku> maksamattomatLaskut;
   private List<Lasku> maksetutLaskut;

   private Lasku lasku;

   private String kuitattavatLaskut;

   @Inject
   private Asetukset asetukset;

   @Produces
   @Named
   public Lasku getLasku()
   {
      return lasku;
   }

   public String laskutushistoria(Sopimus sopimus)
   {
      StringBuilder sb = new StringBuilder();
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      NumberFormat nf = DecimalFormat.getCurrencyInstance(Locale.GERMANY);
      sopimus.getSopimuslaskut().stream().map(s -> s.getLaskurivi()).forEach(lr -> {
         sb.append(String.format("%s: %s\n", sdf.format(lr.getLasku().getEr�p�iv�()), nf.format(lr.getRivihinta())));
      });
      sb.append("------------\n");
      LocalDate seuraava = sopimus.getViimeksiLaskutettu() == null ? DateUtil.t�n��n() : DateUtil.Date2LocalDate(sopimus.getViimeksiLaskutettu());
      if (seuraava == null)
      {
         seuraava = DateUtil.t�n��n();
      }
      for (int i = 0; i < 5; i++)
      {
         seuraava = seuraava.plus(sopimus.getMaksuv�li(), ChronoUnit.MONTHS);
         sb.append(String.format("%s: %s\n", sdf.format(DateUtil.LocalDate2Date(seuraava)),
            nf.format(sopimus.getTyyppi().getHinta() * sopimus.getMaksuv�li())));
      }
      return sb.toString();
   }

   public double getVuosikertym�(Sopimustyyppi sopimustyyppi)
   {
      List<Sopimus> sopimukset = entityManager.createNamedQuery("sopimukset_tyypill�", Sopimus.class)
         .setParameter("tyyppi", sopimustyyppi).getResultList();
      if (sopimustyyppi.isJ�senmaksutyyppi() || sopimustyyppi.isAlkeiskurssi())
      {
         return sopimukset.stream().mapToDouble(s -> s.getTyyppi().getHinta()).sum();
      } else
      {
         return sopimukset.stream().mapToDouble(s -> s.getTyyppi().getHinta() * 12).sum();
      }
   }

   public double getYhteisvuosikertym�()
   {
      List<Sopimustyyppi> tyypit = entityManager.createNamedQuery("sopimustyypit", Sopimustyyppi.class).getResultList();
      return tyypit.stream().mapToDouble(t -> getVuosikertym�(t)).sum();
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
      maksamattomatLaskut = null;
      maksetutLaskut = null;
   }

   @Inject
   private Tulostaja tulostaja;

   public void l�het�Laskut()
   {
      List<Lasku> laskuttamattomat = entityManager.createNamedQuery("l�hett�m�tt�m�t_laskut", Lasku.class)
         .getResultList();
      laskuttamattomat.stream().filter(lasku -> !lasku.getHenkil�().isPaperilasku()).forEach(lasku -> {
         try
         {
            mailManager.l�het�S�hk�posti(lasku.getHenkil�().getYhteystiedot().getS�hk�posti(), "Lasku", "Liitteen�",
               lasku.getPdf());
            lasku.setL�hetetty(new Date());
            lasku.setTila(LaskuTila.L);
            lasku = entityManager.merge(lasku);
         } catch (IsoveliPoikkeus e)
         {
            loggaaja.loggaa(e.getMessage());
         }
      });
      info("L�hetti %d laskua", laskuttamattomat.size());
      laskuttamattomat.stream().filter(lasku -> lasku.getHenkil�().isPaperilasku()).forEach(lasku -> {
         tulostaja.tulostaTiedosto(lasku.getPdf());
      });
      l�hett�m�tt�m�tLaskut = null;
      maksamattomatLaskut = null;
   }

   public void laskutaSopimukset()
   {
      List<Sopimus> sopimukset = haeLaskuttamattomatSopimukset();
      Map<Osoite, List<Sopimus>> sopimuksetPerOsoite = sopimukset.stream()
         .collect(Collectors.groupingBy(sopimus -> sopimus.getLaskutusosoite()));
      sopimuksetPerOsoite.values().forEach(osoitteenSopimukset -> teeLaskuOsoitteelle(osoitteenSopimukset));
      laskuttamattomatSopimukset = haeLaskuttamattomatSopimukset();
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
      lasku.setPdf(BlobData.PDF(String.format("lasku-%d.pdf", lasku.getId()), pdf));
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
   public List<Sopimus> getLaskuttamattomatSopimukset()
   {
      if (laskuttamattomatSopimukset == null)
      {
         laskuttamattomatSopimukset = haeLaskuttamattomatSopimukset();
      }
      return laskuttamattomatSopimukset;
   }

   @Produces
   @Named
   public List<Lasku> getL�hett�m�tt�m�tLaskut()
   {
      if (l�hett�m�tt�m�tLaskut == null)
      {
         l�hett�m�tt�m�tLaskut = entityManager.createNamedQuery("l�hett�m�tt�m�t_laskut", Lasku.class).getResultList();
      }
      return l�hett�m�tt�m�tLaskut;
   }

   @Produces
   @Named
   public List<Lasku> getMaksamattomatLaskut()
   {
      if (maksamattomatLaskut == null)
      {
         maksamattomatLaskut = entityManager.createNamedQuery("maksamattomat_laskut", Lasku.class).getResultList();
      }
      return maksamattomatLaskut;
   }

   @Produces
   @Named
   public List<Lasku> getMaksetutLaskut()
   {
      if (maksetutLaskut == null)
      {
         maksetutLaskut = entityManager.createNamedQuery("maksetut_laskut", Lasku.class).getResultList();
      }
      return maksetutLaskut;
   }

   private List<Sopimus> haeLaskuttamattomatSopimukset()
   {
      List<Sopimus> sopimukset = null;
      sopimukset = entityManager.createNamedQuery("laskuttamattomat_sopimukset", Sopimus.class)
         .setParameter("nyt", DateUtil.t�n��nDate()).getResultList();
      sopimukset.addAll(entityManager.createNamedQuery("uudet_sopimukset", Sopimus.class)
         .setParameter("nyt", DateUtil.t�n��nDate()).getResultList());
      sopimukset.addAll(entityManager.createNamedQuery("laskuttamattomat_kymppikerrat", Sopimus.class).getResultList());
      return sopimukset;
   }

   public void tabiMuuttui(ValueChangeEvent e)
   {
      int uusiTabi = (int) e.getNewValue();
      switch (uusiTabi)
      {
      case 0:
         laskuttamattomatSopimukset = null;
      case 1:
         l�hett�m�tt�m�tLaskut = null;
      }
      entityManager.clear();
   }

   public void laskuValittu(SelectEvent e)
   {
      lasku = (Lasku) e.getObject();
   }

   public void piilotaLasku()
   {
      lasku = null;
   }

   public void poistaLasku()
   {
      entityManager.remove(lasku);
      entityManager.flush();
      info("Lasku poistettu");
   }

   public RowStateMap getLaskuRSM()
   {
      return laskuRSM;
   }

   public void setLaskuRSM(RowStateMap laskuRSM)
   {
      this.laskuRSM = laskuRSM;
   }

   public void tallennaRivi(RowEditEvent e)
   {
      Laskurivi rivi = (Laskurivi) e.getObject();
      lasku = entityManager.merge(rivi.getLasku());
      if (lasku.getPdf() == null)
      {
         lasku.setPdf(BlobData.PDF(String.format("lasku-%d", lasku.getId()), teePdfLasku(lasku)));
      } else
      {
         lasku.getPdf().setTieto(teePdfLasku(lasku));
      }
      entityManager.flush();
      info("Rivi muokattu ja lasku tallennettu");
   }

   public void muodostaPDF(Lasku lasku)
   {
      if (lasku.getPdf() == null)
      {
         lasku.setPdf(BlobData.PDF(String.format("lasku-%d", lasku.getId()), teePdfLasku(lasku)));
      } else
      {
         lasku.getPdf().setTieto(teePdfLasku(lasku));
      }
      entityManager.merge(lasku);
   }

   public void poistaRivi(Laskurivi laskurivi)
   {
      lasku.getLaskurivit().remove(laskurivi);
      lasku = entityManager.merge(lasku);
      if (lasku.getPdf() == null)
      {
         lasku.setPdf(BlobData.PDF(String.format("lasku-%d", lasku.getId()), teePdfLasku(lasku)));
      } else
      {
         lasku.getPdf().setTieto(teePdfLasku(lasku));
      }
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

   public void l�het�Lasku(Lasku lasku)
   {
      loggaaja.loggaa("L�hett�� laskun %s osoitteeseen %s", lasku.getId(), "nickarls@gmail.com");
      mailManager.l�het�S�hk�posti("nicklas@gmail.com", "Budokwai lasku", "Liitteen�", lasku.getPdf());
   }
}
