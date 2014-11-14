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
import fi.budokwai.isoveli.malli.Henkilö;
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
      private Date päiväys;
      private String yTunnus = "301005";
      private int asiakasnumero;
      private int laskunumero;
      private int viitenumero;
      private String tilinumero = "FI04 5711 6140 0501 84";
      private String BICkoodi = "OKOYFIHH";
      private String maksuehto = "10pv netto";
      private Date eräpäivä;
      private String viivästyskorko = "5%";
      private String valuutta = "EUR";
      private String yhteyshenkilö;
      private String toimituspvm;

      public Otsikkotiedot(Lasku lasku)
      {
         päiväys = lasku.getLuotu();
         asiakasnumero = lasku.getHenkilö().getId();
         laskunumero = lasku.getId();
         viitenumero = lasku.getId();
         eräpäivä = lasku.getEräpäivä();
         yhteyshenkilö = (lasku.getHenkilö() instanceof Henkilö) ? "Huoltaja" : "Harrastaja";
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
         throw new IsoveliPoikkeus(String.format("Laskun %d muodostus epäonnistui", lasku.getId()), e);
      }
      this.lasku = lasku;
   }

   private String getPankkiviivakoodi(Otsikkotiedot otsikkotiedot)
   {
      int versio = 4;
      String tilinumero = otsikkotiedot.tilinumero.substring(2);
      double sentit = lasku.getVerollinenHinta() % 1;
      double eurot = lasku.getVerollinenHinta() - sentit;
      String eräpäivä = new SimpleDateFormat("yyMMdd").format(lasku.getEräpäivä());
      return String.format("%d%s%s%s000%s%s", versio, tilinumero, String.format("%06d", eurot),
         String.format("%02d", sentit), Strings.padStart(otsikkotiedot.viitenumero + "", 20, '0'), eräpäivä);
   }

   public byte[] muodosta()
   {
      käsitteleRivit();
      käsitteleVastaanottaja();
      käsitteleOtsikko();
      try
      {
         täytäKentät();
         return loppukäsittely();
      } catch (IOException | DocumentException e)
      {
         throw new IsoveliPoikkeus(String.format("Laskun %d muodostus epäonnistui", lasku.getId()), e);
      }
   }

   private void täytäKentät() throws IOException, DocumentException
   {
      Font fontti = FontFactory.getFont("Arial", 10);
      DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
      NumberFormat nf = DecimalFormat.getCurrencyInstance(Locale.GERMANY);

      ColumnText.showTextAligned(kangas, Element.ALIGN_LEFT, new Phrase(lasku.getId() + "", fontti), 360, 108, 0);
      ColumnText.showTextAligned(kangas, Element.ALIGN_LEFT, new Phrase(df.format(lasku.getEräpäivä()), fontti), 360,
         82, 0);
      ColumnText.showTextAligned(kangas, Element.ALIGN_LEFT, new Phrase(nf.format(lasku.getVerollinenHinta()), fontti),
         480, 82, 0);
   }

   private byte[] loppukäsittely() throws DocumentException, IOException
   {
      lukija.close();
      kirjoittaja.close();
      return tulos.toByteArray();
   }

   private void käsitteleOtsikko()
   {
      Otsikkotiedot otsikkotiedot = new Otsikkotiedot(lasku);
      String pankkiviivakoodi = getPankkiviivakoodi(otsikkotiedot);
      PdfPTable otsikkotaulu = teeOtsikko(otsikkotiedot);
      otsikkotaulu.writeSelectedRows(0, -1, 305, 600 + otsikkotaulu.getTotalHeight(), kangas);
   }

   private void käsitteleVastaanottaja()
   {
      Vastaanottaja vastaanottaja = new Vastaanottaja(lasku.getHenkilö());
      PdfPTable osoitetaulu = teeOsoite(vastaanottaja);
      osoitetaulu.writeSelectedRows(0, -1, 40, 700 + osoitetaulu.getTotalHeight(), kangas);
      osoitetaulu.writeSelectedRows(0, -1, 65, 140 + osoitetaulu.getTotalHeight(), kangas);
   }

   private void käsitteleRivit()
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
      lisääSolu(taulukko, "Päiväys", Formatointi.V_DATA);
      lisääSolu(taulukko, sdf.format(otsikkotiedot.päiväys), Formatointi.V_DATA);
      // taulukko.addCell(new Phrase("Y-tunnus", fontti));
      // taulukko.addCell(new Phrase(otsikkotiedot.yTunnus, fontti));
      // taulukko.addCell(new Phrase("Asiakasnumero", fontti));
      // taulukko.addCell(new Phrase(otsikkotiedot.asiakasnumero + "", fontti));
      lisääSolu(taulukko, "Laskunumero", Formatointi.V_DATA);
      lisääSolu(taulukko, otsikkotiedot.laskunumero + "", Formatointi.V_DATA);
      lisääSolu(taulukko, "Viitenumero", Formatointi.V_DATA);
      lisääSolu(taulukko, otsikkotiedot.viitenumero + "", Formatointi.V_DATA);
      lisääSolu(taulukko, "Tilinumero", Formatointi.V_DATA);
      lisääSolu(taulukko, otsikkotiedot.tilinumero, Formatointi.V_DATA);
      lisääSolu(taulukko, "BIC-koodi", Formatointi.V_DATA);
      lisääSolu(taulukko, otsikkotiedot.BICkoodi, Formatointi.V_DATA);
      lisääSolu(taulukko, "Maksuehto", Formatointi.V_DATA);
      lisääSolu(taulukko, otsikkotiedot.maksuehto, Formatointi.V_DATA);
      lisääSolu(taulukko, "Eräpäivä", Formatointi.V_DATA);
      lisääSolu(taulukko, sdf.format(otsikkotiedot.eräpäivä), Formatointi.V_DATA);
      lisääSolu(taulukko, "Viivästyskorko", Formatointi.V_DATA);
      lisääSolu(taulukko, otsikkotiedot.viivästyskorko, Formatointi.V_DATA);
      // taulukko.addCell(new Phrase("Valuutta", fontti));
      // taulukko.addCell(new Phrase(otsikkotiedot.valuutta, fontti));
      // taulukko.addCell(new Phrase("Yhteyshenkilö", fontti));
      // taulukko.addCell(new Phrase(otsikkotiedot.yhteyshenkilö, fontti));
      // taulukko.addCell(new Phrase("Toimituspvm", fontti));
      // taulukko.addCell(new Phrase(otsikkotiedot.toimituspvm, fontti));
      return taulukko;
   }

   private PdfPTable teeOsoite(Vastaanottaja vastaanottaja)
   {
      PdfPTable taulukko = new PdfPTable(1);
      taulukko.getDefaultCell().setBorder(Rectangle.NO_BORDER);
      taulukko.setTotalWidth(230f);
      lisääSolu(taulukko, vastaanottaja.getHenkilö().getNimi(), Formatointi.V_DATA);
      lisääSolu(taulukko, vastaanottaja.getOsoite().getOsoite(), Formatointi.V_DATA);
      lisääSolu(taulukko,
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
      PdfPCell täyttö = new PdfPCell();
      täyttö.setColspan(6);
      täyttö.setBorder(Rectangle.NO_BORDER);
      taulukko.addCell(täyttö);
      taulukko.getDefaultCell().setBorder(Rectangle.TOP);
      taulukko.getDefaultCell().setBorderWidthTop(2f);
      taulukko.getDefaultCell().setPaddingTop(5f);
      lisääSolu(taulukko, hinta(lasku.getVerotonHinta()), Formatointi.Y_DATA);
      lisääSolu(taulukko, hinta(lasku.getALVnOsuus()), Formatointi.Y_DATA);
      lisääSolu(taulukko, hinta(lasku.getVerollinenHinta()), Formatointi.Y_DATA);
   }

   private void kirjoitaRiviotsikot(PdfPTable taulukko)
   {
      lisääSolu(taulukko, "#", Formatointi.OTSIKKO);
      lisääSolu(taulukko, "Tuote", Formatointi.V_OTSIKKO);
      lisääSolu(taulukko, "Jakso", Formatointi.V_OTSIKKO);
      lisääSolu(taulukko, "Määrä", Formatointi.OTSIKKO);
      lisääSolu(taulukko, "Yks", Formatointi.OTSIKKO);
      lisääSolu(taulukko, "ALV%", Formatointi.OTSIKKO);
      lisääSolu(taulukko, "ALV0", Formatointi.OTSIKKO);
      lisääSolu(taulukko, "ALV-osa", Formatointi.OTSIKKO);
      lisääSolu(taulukko, "Hinta", Formatointi.OTSIKKO);
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
      lisääSolu(taulukko, laskurivi.getRivinumero() + "", Formatointi.DATA);
      lisääSolu(taulukko, laskurivi.getSopimuslasku().getSopimus().getTuotenimi(), Formatointi.V_DATA);
      lisääSolu(taulukko, laskurivi.getSopimuslasku().getJakso(), Formatointi.V_DATA);
      lisääSolu(taulukko, tyyppi.getMäärä() + "", Formatointi.DATA);
      lisääSolu(taulukko, tyyppi.getYksikkö(), Formatointi.DATA);
      lisääSolu(taulukko, tyyppi.getVerokanta() + "", Formatointi.DATA);
      lisääSolu(taulukko, hinta(tyyppi.getVerotonHinta()), Formatointi.DATA);
      lisääSolu(taulukko, hinta(tyyppi.getALVnOsuus()), Formatointi.DATA);
      lisääSolu(taulukko, hinta(tyyppi.getVerollinenHinta()), Formatointi.DATA);
   }

   private void lisääSolu(PdfPTable taulukko, String tieto, Formatointi formatointi)
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