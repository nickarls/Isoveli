package fi.budokwai.isoveli.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Sopimustyyppi;

public class Lasku2PDF
{
   public static byte[] muodosta(byte[] pohja, Lasku lasku) throws DocumentException, IOException
   {
      PdfReader lukija = new PdfReader(pohja);
      ByteArrayOutputStream tulos = new ByteArrayOutputStream();
      PdfStamper kirjoittaja = new PdfStamper(lukija, tulos);
      PdfContentByte kangas = kirjoittaja.getOverContent(1);
      PdfPTable taulu = teeLaskurivit(lasku.getLaskurivit());
      taulu.writeSelectedRows(0, -1, 20, 500 + taulu.getTotalHeight(), kangas);
      kirjoittaja.getWriter().add(teeLaskurivit(lasku.getLaskurivit()));
      lukija.close();
      kirjoittaja.close();
      return tulos.toByteArray();
   }

   private static PdfPTable teeLaskurivit(List<Laskurivi> laskurivit)
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

   private static void kirjoitaOtsikot(PdfPTable taulukko, String[] otsikot)
   {
      taulukko.getDefaultCell().setBackgroundColor(Color.LIGHT_GRAY);
      Font fontti = FontFactory.getFont("Arial", 10, Font.BOLD);
      for (String otsikko : otsikot)
      {
         taulukko.addCell(new Phrase(otsikko, fontti));
      }
   }

   private static void kirjoitaLaskurivit(PdfPTable taulukko, List<Laskurivi> laskurivit)
   {
      for (Laskurivi laskurivi : laskurivit)
      {
         kirjoitaLaskurivi(taulukko, laskurivi);
      }
   }

   private static void kirjoitaLaskurivi(PdfPTable taulukko, Laskurivi laskurivi)
   {
      taulukko.getDefaultCell().setBackgroundColor(Color.WHITE);
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

   private static String hinta(double hinta)
   {
      return DecimalFormat.getCurrencyInstance(Locale.GERMANY).format(hinta);
   }

}