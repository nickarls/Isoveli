package fi.budokwai.isoveli.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.common.base.Strings;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.Henkilˆ;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Sopimustyyppi;
import fi.budokwai.isoveli.malli.Vastaanottaja;

public class Lasku2PDF
{
   private static class Formatointi
   {
      public Formatointi(Font fontti, int hTasaus, int vTasaus, int kehys, float kehysPaksuus)
      {
         this.fontti = fontti;
         this.hTasaus = hTasaus;
         this.vTasaus = vTasaus;
         this.kehys = kehys;
         this.kehysPaksuus = kehysPaksuus;
      }

      public Formatointi()
      {
      }

      private Font fontti = FontFactory.getFont("Arial", 10);
      private int hTasaus = Element.ALIGN_RIGHT;
      private int vTasaus = Element.ALIGN_UNDEFINED;
      private int kehys = Rectangle.NO_BORDER;
      private float kehysPaksuus = 0f;

      public static final Formatointi DATA = new Formatointi();

      public static final Formatointi OTSIKKO = new Formatointi(FontFactory.getFont("Arial", 10, Font.BOLD),
         Element.ALIGN_RIGHT, Element.ALIGN_TOP, Rectangle.BOTTOM, 1f);

      public static final Formatointi V_OTSIKKO = new Formatointi(FontFactory.getFont("Arial", 10, Font.BOLD),
         Element.ALIGN_LEFT, Element.ALIGN_TOP, Rectangle.BOTTOM, 1f);

      public static final Formatointi V_DATA = new Formatointi(FontFactory.getFont("Arial", 10), Element.ALIGN_LEFT,
         Element.ALIGN_TOP, Rectangle.NO_BORDER, 0f);

      public static final Formatointi Y_DATA = new Formatointi(FontFactory.getFont("Arial", 10), Element.ALIGN_RIGHT,
         Element.ALIGN_TOP, Rectangle.TOP, 2f);

   }

   @SuppressWarnings("unused")
   private class Otsikkotiedot
   {
      private Date p‰iv‰ys;
      private String yTunnus = "301005";
      private int asiakasnumero;
      private int laskunumero;
      private int viitenumero;
      private String tilinumero = "FI04 5711 6140 0501 84";
      private String BICkoodi = "OKOYFIHH";
      private String maksuehto = "10pv netto";
      private Date er‰p‰iv‰;
      private String viiv‰styskorko = "5%";
      private String valuutta = "EUR";
      private String yhteyshenkilˆ;
      private String toimituspvm;

      public Otsikkotiedot(Lasku lasku)
      {
         p‰iv‰ys = lasku.getLuotu();
         asiakasnumero = lasku.getHenkilˆ().getId();
         laskunumero = lasku.getId();
         viitenumero = lasku.getId();
         er‰p‰iv‰ = lasku.getEr‰p‰iv‰();
         yhteyshenkilˆ = (lasku.getHenkilˆ() instanceof Henkilˆ) ? "Huoltaja" : "Harrastaja";
         toimituspvm = "jatkuva";
      }
   }

   private PdfReader lukija;
   private ByteArrayOutputStream tulos;
   private PdfStamper kirjoittaja;
   private PdfContentByte kangas;
   private Lasku lasku;

   public Lasku2PDF(byte[] pohja, Lasku lasku)
   {
      try
      {
         lukija = new PdfReader(pohja);
         tulos = new ByteArrayOutputStream();
         kirjoittaja = new PdfStamper(lukija, tulos);
         kangas = kirjoittaja.getOverContent(1);
      } catch (IOException | DocumentException e)
      {
         throw new IsoveliPoikkeus(String.format("Laskun %d muodostus ep‰onnistui", lasku.getId()), e);
      }
      this.lasku = lasku;
   }

   private String getPankkiviivakoodi(Otsikkotiedot otsikkotiedot)
   {
      int versio = 4;
      String tilinumero = otsikkotiedot.tilinumero.substring(2);
      double sentit = lasku.getVerollinenHinta() % 1;
      double eurot = lasku.getVerollinenHinta() - sentit;
      String er‰p‰iv‰ = new SimpleDateFormat("yyMMdd").format(lasku.getEr‰p‰iv‰());
      return String.format("%d%s%s%s000%s%s", versio, tilinumero, String.format("%06f", eurot),
         String.format("%02f", sentit), Strings.padStart(otsikkotiedot.viitenumero + "", 20, '0'), er‰p‰iv‰);
   }

   public byte[] muodosta()
   {
      k‰sitteleRivit();
      k‰sitteleVastaanottaja();
      k‰sitteleOtsikko();
      try
      {
         t‰yt‰Kent‰t();
         return loppuk‰sittely();
      } catch (IOException | DocumentException e)
      {
         throw new IsoveliPoikkeus(String.format("Laskun %d muodostus ep‰onnistui", lasku.getId()), e);
      }
   }

   private void t‰yt‰Kent‰t() throws IOException, DocumentException
   {
      Font fontti = FontFactory.getFont("Arial", 10);
      DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
      NumberFormat nf = DecimalFormat.getCurrencyInstance(Locale.GERMANY);

      ColumnText.showTextAligned(kangas, Element.ALIGN_LEFT, new Phrase(lasku.getId() + "", fontti), 360, 108, 0);
      ColumnText.showTextAligned(kangas, Element.ALIGN_LEFT, new Phrase(df.format(lasku.getEr‰p‰iv‰()), fontti), 360,
         82, 0);
      ColumnText.showTextAligned(kangas, Element.ALIGN_LEFT, new Phrase(nf.format(lasku.getVerollinenHinta()), fontti),
         480, 82, 0);
   }

   private byte[] loppuk‰sittely() throws DocumentException, IOException
   {
      lukija.close();
      kirjoittaja.close();
      return tulos.toByteArray();
   }

   private void k‰sitteleOtsikko()
   {
      Otsikkotiedot otsikkotiedot = new Otsikkotiedot(lasku);
      String pankkiviivakoodi = getPankkiviivakoodi(otsikkotiedot);
      PdfPTable otsikkotaulu = teeOtsikko(otsikkotiedot);
      otsikkotaulu.writeSelectedRows(0, -1, 305, 600 + otsikkotaulu.getTotalHeight(), kangas);
   }

   private void k‰sitteleVastaanottaja()
   {
      Vastaanottaja vastaanottaja = new Vastaanottaja(lasku.getHenkilˆ());
      PdfPTable osoitetaulu = teeOsoite(vastaanottaja);
      osoitetaulu.writeSelectedRows(0, -1, 40, 700 + osoitetaulu.getTotalHeight(), kangas);
      osoitetaulu.writeSelectedRows(0, -1, 65, 140 + osoitetaulu.getTotalHeight(), kangas);
   }

   private void k‰sitteleRivit()
   {
      PdfPTable rivitaulu = teeLaskurivit();
      rivitaulu.writeSelectedRows(0, -1, 20, 450 + rivitaulu.getTotalHeight(), kangas);
   }

   private PdfPTable teeOtsikko(Otsikkotiedot otsikkotiedot)
   {
      PdfPTable taulukko = new PdfPTable(new float[]
      { 2, 3 });
      taulukko.getDefaultCell().setBorder(Rectangle.NO_BORDER);
      taulukko.setTotalWidth(200f);
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      lis‰‰Solu(taulukko, "P‰iv‰ys", Formatointi.V_DATA);
      lis‰‰Solu(taulukko, sdf.format(otsikkotiedot.p‰iv‰ys), Formatointi.V_DATA);
      // taulukko.addCell(new Phrase("Y-tunnus", fontti));
      // taulukko.addCell(new Phrase(otsikkotiedot.yTunnus, fontti));
      // taulukko.addCell(new Phrase("Asiakasnumero", fontti));
      // taulukko.addCell(new Phrase(otsikkotiedot.asiakasnumero + "", fontti));
      lis‰‰Solu(taulukko, "Laskunumero", Formatointi.V_DATA);
      lis‰‰Solu(taulukko, otsikkotiedot.laskunumero + "", Formatointi.V_DATA);
      lis‰‰Solu(taulukko, "Viitenumero", Formatointi.V_DATA);
      lis‰‰Solu(taulukko, otsikkotiedot.viitenumero + "", Formatointi.V_DATA);
      lis‰‰Solu(taulukko, "Tilinumero", Formatointi.V_DATA);
      lis‰‰Solu(taulukko, otsikkotiedot.tilinumero, Formatointi.V_DATA);
      lis‰‰Solu(taulukko, "BIC-koodi", Formatointi.V_DATA);
      lis‰‰Solu(taulukko, otsikkotiedot.BICkoodi, Formatointi.V_DATA);
      lis‰‰Solu(taulukko, "Maksuehto", Formatointi.V_DATA);
      lis‰‰Solu(taulukko, otsikkotiedot.maksuehto, Formatointi.V_DATA);
      lis‰‰Solu(taulukko, "Er‰p‰iv‰", Formatointi.V_DATA);
      lis‰‰Solu(taulukko, sdf.format(otsikkotiedot.er‰p‰iv‰), Formatointi.V_DATA);
      lis‰‰Solu(taulukko, "Viiv‰styskorko", Formatointi.V_DATA);
      lis‰‰Solu(taulukko, otsikkotiedot.viiv‰styskorko, Formatointi.V_DATA);
      // taulukko.addCell(new Phrase("Valuutta", fontti));
      // taulukko.addCell(new Phrase(otsikkotiedot.valuutta, fontti));
      // taulukko.addCell(new Phrase("Yhteyshenkilˆ", fontti));
      // taulukko.addCell(new Phrase(otsikkotiedot.yhteyshenkilˆ, fontti));
      // taulukko.addCell(new Phrase("Toimituspvm", fontti));
      // taulukko.addCell(new Phrase(otsikkotiedot.toimituspvm, fontti));
      return taulukko;
   }

   private PdfPTable teeOsoite(Vastaanottaja vastaanottaja)
   {
      PdfPTable taulukko = new PdfPTable(1);
      taulukko.getDefaultCell().setBorder(Rectangle.NO_BORDER);
      taulukko.setTotalWidth(230f);
      lis‰‰Solu(taulukko, vastaanottaja.getHenkilˆ().getNimi(), Formatointi.V_DATA);
      lis‰‰Solu(taulukko, vastaanottaja.getOsoite().getOsoite(), Formatointi.V_DATA);
      lis‰‰Solu(taulukko,
         String.format("%s %s", vastaanottaja.getOsoite().getPostinumero(), vastaanottaja.getOsoite().getKaupunki()),
         Formatointi.V_DATA);
      return taulukko;
   }

   private PdfPTable teeLaskurivit()
   {
      PdfPTable taulukko = new PdfPTable(new float[]
      { 1, 7, 5, 2, 2, 2, 2, 2, 2 });
      taulukko.setWidthPercentage(100f);
      taulukko.setTotalWidth(550f);
      kirjoitaRiviotsikot(taulukko);
      taulukko.setHeaderRows(1);
      kirjoitaLaskurivit(taulukko);
      kirjoitaYhteenveto(taulukko);
      taulukko.setComplete(true);
      return taulukko;
   }

   private void kirjoitaYhteenveto(PdfPTable taulukko)
   {
      PdfPCell rivi = new PdfPCell();
      rivi.setColspan(9);
      rivi.setMinimumHeight(5f);
      rivi.setBorder(Rectangle.NO_BORDER);
      taulukko.addCell(rivi);
      PdfPCell t‰yttˆ = new PdfPCell();
      t‰yttˆ.setColspan(6);
      t‰yttˆ.setBorder(Rectangle.NO_BORDER);
      taulukko.addCell(t‰yttˆ);
      taulukko.getDefaultCell().setBorder(Rectangle.TOP);
      taulukko.getDefaultCell().setBorderWidthTop(2f);
      taulukko.getDefaultCell().setPaddingTop(5f);
      lis‰‰Solu(taulukko, hinta(lasku.getVerotonHinta()), Formatointi.Y_DATA);
      lis‰‰Solu(taulukko, hinta(lasku.getALVnOsuus()), Formatointi.Y_DATA);
      lis‰‰Solu(taulukko, hinta(lasku.getVerollinenHinta()), Formatointi.Y_DATA);
   }

   private void kirjoitaRiviotsikot(PdfPTable taulukko)
   {
      lis‰‰Solu(taulukko, "#", Formatointi.OTSIKKO);
      lis‰‰Solu(taulukko, "Tuote", Formatointi.V_OTSIKKO);
      lis‰‰Solu(taulukko, "Jakso", Formatointi.V_OTSIKKO);
      lis‰‰Solu(taulukko, "M‰‰r‰", Formatointi.OTSIKKO);
      lis‰‰Solu(taulukko, "Yks", Formatointi.OTSIKKO);
      lis‰‰Solu(taulukko, "ALV%", Formatointi.OTSIKKO);
      lis‰‰Solu(taulukko, "ALV0", Formatointi.OTSIKKO);
      lis‰‰Solu(taulukko, "ALV-osa", Formatointi.OTSIKKO);
      lis‰‰Solu(taulukko, "Hinta", Formatointi.OTSIKKO);
   }

   private void kirjoitaLaskurivit(PdfPTable taulukko)
   {
      for (Laskurivi laskurivi : lasku.getLaskurivit())
      {
         kirjoitaLaskurivi(taulukko, laskurivi);
      }
   }

   private void kirjoitaLaskurivi(PdfPTable taulukko, Laskurivi laskurivi)
   {
      Sopimustyyppi tyyppi = laskurivi.getSopimuslasku().getSopimus().getTyyppi();
      lis‰‰Solu(taulukko, laskurivi.getRivinumero() + "", Formatointi.DATA);
      lis‰‰Solu(taulukko, laskurivi.getSopimuslasku().getSopimus().getTuotenimi(), Formatointi.V_DATA);
      lis‰‰Solu(taulukko, laskurivi.getSopimuslasku().getJakso(), Formatointi.V_DATA);
      lis‰‰Solu(taulukko, tyyppi.getM‰‰r‰() + "", Formatointi.DATA);
      lis‰‰Solu(taulukko, tyyppi.getYksikkˆ(), Formatointi.DATA);
      lis‰‰Solu(taulukko, tyyppi.getVerokanta() + "", Formatointi.DATA);
      lis‰‰Solu(taulukko, hinta(tyyppi.getVerotonHinta()), Formatointi.DATA);
      lis‰‰Solu(taulukko, hinta(tyyppi.getALVnOsuus()), Formatointi.DATA);
      lis‰‰Solu(taulukko, hinta(tyyppi.getVerollinenHinta()), Formatointi.DATA);
   }

   private void lis‰‰Solu(PdfPTable taulukko, String tieto, Formatointi formatointi)
   {
      PdfPCell solu = new PdfPCell();
      solu.setBorder(formatointi.kehys);
      solu.setVerticalAlignment(formatointi.vTasaus);
      solu.setBorderWidth(formatointi.kehysPaksuus);
      Paragraph paragraaffi = new Paragraph(tieto, formatointi.fontti);
      paragraaffi.setAlignment(formatointi.hTasaus);
      solu.addElement(paragraaffi);
      taulukko.addCell(solu);
   }

   private String hinta(double hinta)
   {
      return DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(hinta);
   }

}