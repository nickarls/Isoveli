package fi.budokwai.isoveli.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Osoite;
import fi.budokwai.isoveli.malli.Sopimustyyppi;

public class Lasku2PDF
{
   private class Vastaanottaja
   {
      private Henkilö henkilö;
      private Osoite osoite;

      public Vastaanottaja(Harrastaja harrastaja)
      {
         if (harrastaja.getHuoltaja() != null)
         {
            henkilö = harrastaja.getHuoltaja();
            osoite = harrastaja.getHuoltaja().getPerhe() != null ? harrastaja.getHuoltaja().getPerhe().getOsoite()
               : harrastaja.getHuoltaja().getOsoite();
         } else
         {
            henkilö = harrastaja;
            osoite = harrastaja.getPerhe() != null ? harrastaja.getPerhe().getOsoite() : harrastaja.getOsoite();
         }
         osoite = osoite == null ? new Osoite() : osoite;
      }
   }

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
         asiakasnumero = lasku.getHarrastaja().getId();
         laskunumero = lasku.getId();
         viitenumero = lasku.getId();
         eräpäivä = lasku.getEräpäivä();
         yhteyshenkilö = lasku.getHarrastaja().getHuoltaja() != null ? "Huoltaja" : "Harrastaja";
         toimituspvm = "jatkuva";
      }
   }

   private PdfReader lukija;
   private ByteArrayOutputStream tulos;
   private PdfStamper kirjoittaja;
   private PdfContentByte kangas;
   private Lasku lasku;

   public Lasku2PDF(byte[] pohja, Lasku lasku) throws Exception
   {
      lukija = new PdfReader(pohja);
      tulos = new ByteArrayOutputStream();
      kirjoittaja = new PdfStamper(lukija, tulos);
      kangas = kirjoittaja.getOverContent(1);
      this.lasku = lasku;
   }

   public byte[] muodosta() throws DocumentException, IOException
   {
      käsitteleRivit();
      käsitteleVastaanottaja();
      käsitteleOtsikko();
      täytäKentät();
      return loppukäsittely();
   }

   private void täytäKentät() throws IOException, DocumentException
   {
      Font fontti = FontFactory.getFont("Arial", 10);
      DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
      NumberFormat nf = DecimalFormat.getCurrencyInstance(Locale.GERMANY);

      ColumnText.showTextAligned(kangas, Element.ALIGN_LEFT, new Phrase(lasku.getId() + "", fontti), 360, 108, 0);
      ColumnText.showTextAligned(kangas, Element.ALIGN_LEFT, new Phrase(df.format(lasku.getEräpäivä()), fontti), 360,
         82, 0);
      ColumnText.showTextAligned(kangas, Element.ALIGN_LEFT, new Phrase(nf.format(lasku.getYhteishinta()), fontti),
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
      PdfPTable otsikkotaulu = teeOtsikko(otsikkotiedot);
      otsikkotaulu.writeSelectedRows(0, -1, 305, 580 + otsikkotaulu.getTotalHeight(), kangas);
   }

   private void käsitteleVastaanottaja()
   {
      Vastaanottaja vastaanottaja = new Vastaanottaja(lasku.getHarrastaja());
      PdfPTable osoitetaulu = teeOsoite(vastaanottaja);
      osoitetaulu.writeSelectedRows(0, -1, 40, 700 + osoitetaulu.getTotalHeight(), kangas);
      osoitetaulu.writeSelectedRows(0, -1, 65, 140 + osoitetaulu.getTotalHeight(), kangas);
   }

   private void käsitteleRivit()
   {
      PdfPTable rivitaulu = teeLaskurivit(lasku.getLaskurivit());
      rivitaulu.writeSelectedRows(0, -1, 20, 500 + rivitaulu.getTotalHeight(), kangas);
   }

   private PdfPTable teeOtsikko(Otsikkotiedot otsikkotiedot)
   {
      Font fontti = FontFactory.getFont("Arial", 10);
      PdfPTable taulukko = new PdfPTable(new float[]
      { 2, 3 });
      taulukko.getDefaultCell().setBorder(Rectangle.NO_BORDER);
      taulukko.setTotalWidth(200f);
      SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
      taulukko.addCell(new Phrase("Päiväys", fontti));
      taulukko.addCell(new Phrase(sdf.format(otsikkotiedot.päiväys), fontti));
      taulukko.addCell(new Phrase("Y-tunnus", fontti));
      taulukko.addCell(new Phrase(otsikkotiedot.yTunnus, fontti));
      taulukko.addCell(new Phrase("Asiakasnumero", fontti));
      taulukko.addCell(new Phrase(otsikkotiedot.asiakasnumero + "", fontti));
      taulukko.addCell(new Phrase("Laskunumero", fontti));
      taulukko.addCell(new Phrase(otsikkotiedot.laskunumero + "", fontti));
      taulukko.addCell(new Phrase("Viitenumero", fontti));
      taulukko.addCell(new Phrase(otsikkotiedot.viitenumero + "", fontti));
      taulukko.addCell(new Phrase("Tilinumero", fontti));
      taulukko.addCell(new Phrase(otsikkotiedot.tilinumero, fontti));
      taulukko.addCell(new Phrase("BIC-koodi", fontti));
      taulukko.addCell(new Phrase(otsikkotiedot.BICkoodi, fontti));
      taulukko.addCell(new Phrase("Maksuehto", fontti));
      taulukko.addCell(new Phrase(otsikkotiedot.maksuehto, fontti));
      taulukko.addCell(new Phrase("Eräpäivä", fontti));
      taulukko.addCell(new Phrase(sdf.format(otsikkotiedot.eräpäivä), fontti));
      taulukko.addCell(new Phrase("Viivästyskorko", fontti));
      taulukko.addCell(new Phrase(otsikkotiedot.viivästyskorko, fontti));
      taulukko.addCell(new Phrase("Valuutta", fontti));
      taulukko.addCell(new Phrase(otsikkotiedot.valuutta, fontti));
      taulukko.addCell(new Phrase("Yhteyshenkilö", fontti));
      taulukko.addCell(new Phrase(otsikkotiedot.yhteyshenkilö, fontti));
      taulukko.addCell(new Phrase("Toimituspvm", fontti));
      taulukko.addCell(new Phrase(otsikkotiedot.toimituspvm, fontti));
      return taulukko;
   }

   private PdfPTable teeOsoite(Vastaanottaja vastaanottaja)
   {
      PdfPTable taulukko = new PdfPTable(1);
      Font fontti = FontFactory.getFont("Arial", 10);
      taulukko.getDefaultCell().setBorder(Rectangle.NO_BORDER);
      taulukko.setTotalWidth(230f);
      taulukko.addCell(new Phrase(vastaanottaja.henkilö.getNimi(), fontti));
      taulukko.addCell(new Phrase(vastaanottaja.osoite.getOsoite(), fontti));
      taulukko.addCell(new Phrase(String.format("%s %s", vastaanottaja.osoite.getPostinumero(),
         vastaanottaja.osoite.getKaupunki()), fontti));
      return taulukko;
   }

   private PdfPTable teeLaskurivit(List<Laskurivi> laskurivit)
   {
      String[] otsikot = new String[]
      { "#", "Tuotenimi", "Tuotekoodi", "Määrä", "Yksikkö", "Verokanta", "Veroton hinta", "ALVn osuus",
            "Verollinen hinta" };
      PdfPTable taulukko = new PdfPTable(new float[]
      { 1, 3, 2, 2, 2, 2, 2, 2, 2 });
      taulukko.setWidthPercentage(100f);
      taulukko.setTotalWidth(550f);
      kirjoitaOtsikot(taulukko, otsikot);
      taulukko.setHeaderRows(1);
      kirjoitaLaskurivit(taulukko, laskurivit);
      taulukko.setComplete(true);
      return taulukko;
   }

   private void kirjoitaOtsikot(PdfPTable taulukko, String[] otsikot)
   {
      taulukko.getDefaultCell().setBorder(Rectangle.BOTTOM);
      Font fontti = FontFactory.getFont("Arial", 10, Font.BOLD);
      for (String otsikko : otsikot)
      {
         taulukko.addCell(new Phrase(otsikko, fontti));
      }
   }

   private void kirjoitaLaskurivit(PdfPTable taulukko, List<Laskurivi> laskurivit)
   {
      for (Laskurivi laskurivi : laskurivit)
      {
         kirjoitaLaskurivi(taulukko, laskurivi);
      }
   }

   private void kirjoitaLaskurivi(PdfPTable taulukko, Laskurivi laskurivi)
   {
      taulukko.getDefaultCell().setBorder(Rectangle.NO_BORDER);
      Font fontti = FontFactory.getFont("Arial", 10);
      Sopimustyyppi tyyppi = laskurivi.getSopimus().getTyyppi();
      taulukko.addCell(new Phrase(laskurivi.getRivinumero() + "", fontti));
      taulukko.addCell(new Phrase(tyyppi.getNimi(), fontti));
      taulukko.addCell(new Phrase(tyyppi.getTuotekoodi(), fontti));
      taulukko.addCell(new Phrase(tyyppi.getMäärä() + "", fontti));
      taulukko.addCell(new Phrase(tyyppi.getYksikkö(), fontti));
      taulukko.addCell(new Phrase("ALV" + tyyppi.getVerokanta(), fontti));
      taulukko.addCell(new Phrase(hinta(tyyppi.getVerotonHinta()), fontti));
      taulukko.addCell(new Phrase(hinta(tyyppi.getALVnOsuus()), fontti));
      taulukko.addCell(new Phrase(hinta(tyyppi.getVerollinenHinta()), fontti));
   }

   private String hinta(double hinta)
   {
      return DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(hinta);
   }

}