package fi.budokwai.isoveli.util;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.google.common.base.Strings;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPRow;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfTemplate;

import fi.budokwai.isoveli.Asetukset;
import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.malli.Laskurivi;
import fi.budokwai.isoveli.malli.Vastaanottaja;

public class Lasku2PDF
{
   private PdfReader lukija;
   private ByteArrayOutputStream tulos;
   private PdfStamper kirjoittaja;
   private PdfContentByte kangas;
   private Lasku lasku;
   private Asetukset asetukset;
   private DateFormat p‰iv‰m‰‰r‰formaatti = new SimpleDateFormat("dd.MM.yyyy");
   private NumberFormat hintaformaatti = DecimalFormat.getCurrencyInstance(Locale.GERMANY);

   public Lasku2PDF(byte[] pohja, Lasku lasku, Asetukset asetukset)
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
      this.asetukset = asetukset;
   }

   public byte[] muodosta()
   {
      try
      {
         kirjoitaLaskuotsikko();
         kirjoitaYl‰otsikko();
         kirjoitaVastaanottaja();
         kirjoitaSaaja();
         kirjoitaIBAN();
         kirjoitaBIC();
         kirjoitaViitenumero();
         kirjoitaLoppusumma();
         kirjoitaEr‰p‰iv‰();
         kirjoitaHuomautus();
         kirjoitaLaskurivit();
         kirjoitaSaajanOsoitetiedot();
         kirjoitaSaajanTunnustiedot();
         kirjoitaSaajanYhteystiedot();
         kirjoitaViivakoodi();
         return loppuk‰sittely();
      } catch (IOException | DocumentException e)
      {
         throw new IsoveliPoikkeus(String.format("Laskun %d muodostus ep‰onnistui", lasku.getId()), e);
      }
   }

   private void kirjoitaViivakoodi()
   {
      Barcode128 bc128 = new Barcode128();
      bc128.setBarHeight(30f);
      bc128.setCode(getPankkiviivakoodi());
      bc128.setX(1f);
      bc128.setFont(null);
      PdfTemplate viivakoodi = bc128.createTemplateWithBarcode(kangas, null, null);
      kangas.addTemplate(viivakoodi, 15, 20);
   }

   private String getPankkiviivakoodi()
   {
      int versio = 4;
      String tilinumero = asetukset.getIBAN().substring(2).replaceAll(" ", "");
      double sentit = lasku.getYhteishinta() % 1;
      double eurot = lasku.getYhteishinta() - sentit;
      String er‰p‰iv‰ = new SimpleDateFormat("yyMMdd").format(lasku.getEr‰p‰iv‰());
      return String.format("%d%s%s%s000%s%s", versio, tilinumero, String.format("%06d", (int) eurot),
         String.format("%02d", (int) sentit), Strings.padStart(lasku.getViitenumero(), 20, '0'), er‰p‰iv‰);
   }

   void kirjoitaLaskuotsikko()
   {
      PdfPTable taulu = teeTaulu(2, 180);
      Format format = Format.create().withFont("Helvetica", 12, Font.NORMAL);
      DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
      lis‰‰Solu(taulu, "P‰iv‰ys", format);
      lis‰‰Solu(taulu, df.format(lasku.getLuotu()), format);
      lis‰‰Solu(taulu, "Laskunumero", format);
      lis‰‰Solu(taulu, "" + lasku.getId(), format);
      lis‰‰Solu(taulu, "Asiakasnumero", format);
      lis‰‰Solu(taulu, lasku.getHenkilˆ().getId() + "", format);
      lis‰‰Solu(taulu, "Maskuehto", format);
      lis‰‰Solu(taulu, String.format("%d p‰iv‰‰ netto", asetukset.getMaksuaikaa()), format);
      lis‰‰Solu(taulu, "Viiv‰styskorko", format);
      lis‰‰Solu(taulu, String.format("%d%%", asetukset.getViiv‰stysprosentti()), format);
      lis‰‰Solu(taulu, "Er‰p‰iv‰", format);
      lis‰‰Solu(taulu, df.format(lasku.getEr‰p‰iv‰()), format);
      taulu.writeSelectedRows(0, -1, 350, 770, kangas);
   }

   private void kirjoitaSaajanOsoitetiedot()
   {
      PdfPTable taulu = teeTaulu(1, 100);
      Format format = Format.create().withFont("Helvetica", 8, Font.NORMAL).withPadding(0, Rectangle.BOX);
      lis‰‰Solu(taulu, asetukset.getSaaja(), format);
      lis‰‰Solu(taulu, asetukset.getOsoite(), format);
      lis‰‰Solu(taulu, String.format("%s %s", asetukset.getPostinumero(), asetukset.getKaupunki()), format);
      taulu.writeSelectedRows(0, -1, 42, 340, kangas);
   }

   private void kirjoitaSaajanTunnustiedot()
   {
      PdfPTable taulu = teeTaulu(2, 100);
      Format format = Format.create().withFont("Helvetica", 8, Font.NORMAL).withPadding(0, Rectangle.BOX);
      lis‰‰Solu(taulu, "Y-tunnus", format);
      lis‰‰Solu(taulu, asetukset.getYTunnus(), format);
      lis‰‰Solu(taulu, "ALV-tunnus", format);
      lis‰‰Solu(taulu, asetukset.getALVtunnus(), format);
      taulu.writeSelectedRows(0, -1, 230, 340, kangas);
   }

   private void kirjoitaSaajanYhteystiedot()
   {
      PdfPTable taulu = teeTaulu(2, 100);
      Format format = Format.create().withFont("Helvetica", 8, Font.NORMAL).withPadding(0, Rectangle.BOX)
         .withNoWrap(true);
      lis‰‰Solu(taulu, "Puhelin", format);
      lis‰‰Solu(taulu, asetukset.getPuhelin(), format);
      lis‰‰Solu(taulu, "S‰hkˆposti", format);
      lis‰‰Solu(taulu, asetukset.getS‰hkˆposti(), format);
      lis‰‰Solu(taulu, "Kotisivu", format);
      lis‰‰Solu(taulu, asetukset.getKotisivut(), format);
      taulu.writeSelectedRows(0, -1, 400, 340, kangas);
   }

   private void kirjoitaLaskurivit()
   {
      PdfPTable taulu = teeLaskurivit();
      taulu.writeSelectedRows(0, -1, 20, 370 + taulu.getTotalHeight(), kangas);
   }

   private PdfPTable teeLaskurivit()
   {
      PdfPTable taulu = new PdfPTable(new float[]
      { 1, 10, 6, 3, 2, 3});
      taulu.setWidthPercentage(100);
      taulu.setTotalWidth(550);
      teeRiviotsikot(taulu);
      taulu.setHeaderRows(1);
      teeLaskurivit(taulu);
      teeLaskurivisummat(taulu);
      vuorov‰rit(taulu);
      taulu.setComplete(true);
      return taulu;
   }

   private void vuorov‰rit(PdfPTable taulu)
   {
      for (int i = 1; i < lasku.getLaskurivej‰() + 1; i++)
      {
         PdfPRow rivi = taulu.getRow(i);
         for (PdfPCell solu : rivi.getCells())
         {
            solu.setBackgroundColor(i % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
         }
      }
   }

   private void teeLaskurivisummat(PdfPTable taulu)
   {
      PdfPCell rivi = new PdfPCell();
      rivi.setColspan(6);
      rivi.setMinimumHeight(5);
      rivi.setBorder(Rectangle.NO_BORDER);
      taulu.addCell(rivi);
      PdfPCell t‰yttˆ = new PdfPCell();
      t‰yttˆ.setColspan(5);
      t‰yttˆ.setBorder(Rectangle.NO_BORDER);
      taulu.addCell(t‰yttˆ);
      taulu.getDefaultCell().setBorder(Rectangle.TOP);
      taulu.getDefaultCell().setBorderWidthTop(2);
      taulu.getDefaultCell().setPaddingTop(5);
      Format format = Format.create().withFont("Helvetica", 10, Font.BOLD).withBorder(Rectangle.TOP).withBorderWidth(2)
         .withHorizontalAlignment(Element.ALIGN_RIGHT);
      lis‰‰Solu(taulu, hintaformaatti.format(lasku.getYhteishinta()), format);
   }

   private void teeLaskurivit(PdfPTable taulu)
   {
      for (Laskurivi laskurivi : lasku.getLaskurivit())
      {
         teeLaskurivi(taulu, laskurivi);
      }
   }

   private void teeLaskurivi(PdfPTable taulu, Laskurivi laskurivi)
   {
      Format format = Format.create().withFont("Helvetica", 10, Font.NORMAL);
      lis‰‰Solu(taulu, laskurivi.getRivinumero() + "", format);
      lis‰‰Solu(taulu, laskurivi.getTuotenimi(), format);
      lis‰‰Solu(taulu, laskurivi.getInfotieto(), format);
      format.withHorizontalAlignment(Element.ALIGN_RIGHT);
      lis‰‰Solu(taulu, laskurivi.getM‰‰r‰() + "", format);
      format.withHorizontalAlignment(Element.ALIGN_LEFT);
      lis‰‰Solu(taulu, laskurivi.getYksikkˆ(), format);
      format.withHorizontalAlignment(Element.ALIGN_RIGHT);
      lis‰‰Solu(taulu, hintaformaatti.format(laskurivi.getRivihinta()), format);
   }

   private void teeRiviotsikot(PdfPTable taulu)
   {
      Format format = Format.create().withFont("Helvetica", 10, Font.BOLD).withBorder(Rectangle.BOTTOM)
         .withBorderWidth(1).withPadding(2, Rectangle.BOTTOM);
      lis‰‰Solu(taulu, "#", format);
      lis‰‰Solu(taulu, "Tuote", format);
      lis‰‰Solu(taulu, "Jakso", format);
      format.withHorizontalAlignment(Element.ALIGN_RIGHT);
      lis‰‰Solu(taulu, "M‰‰r‰", format);
      format.withHorizontalAlignment(Element.ALIGN_LEFT);
      lis‰‰Solu(taulu, "Yks", format);
      format.withHorizontalAlignment(Element.ALIGN_RIGHT);
      lis‰‰Solu(taulu, "Hinta", format);
   }

   private void kirjoitaHuomautus()
   {
      PdfPTable taulu = teeTaulu(1, 200);
      Format format = Format.create().withFont("Helvetica", 10, Font.BOLD);
      for (String rivi : asetukset.getHuomio().split("\n"))
      {
         lis‰‰Solu(taulu, rivi, format);
      }
      taulu.writeSelectedRows(0, -1, 330, 205, kangas);
   }

   private void kirjoitaEr‰p‰iv‰()
   {
      kirjoita(p‰iv‰m‰‰r‰formaatti.format(lasku.getEr‰p‰iv‰()), 360, 80);
   }

   private void kirjoitaLoppusumma()
   {
      kirjoita(new DecimalFormat("#0.00").format(lasku.getYhteishinta()), 475, 80);
   }

   private void kirjoitaViitenumero()
   {
      kirjoita(lasku.getViitenumero(), 360, 107);
   }

   private void kirjoitaBIC()
   {
      kirjoita(asetukset.getBIC(), 330, 250);
   }

   private void kirjoitaIBAN()
   {
      kirjoita(asetukset.getIBAN(), 73, 250);
   }

   private void kirjoitaSaaja()
   {
      kirjoita(asetukset.getSaaja(), 70, 212);
   }

   private void kirjoitaVastaanottaja()
   {
      Vastaanottaja vastaanottaja = new Vastaanottaja(lasku.getHenkilˆ());
      PdfPTable osoitetaulu = teeOsoitetaulu(vastaanottaja);
      osoitetaulu.writeSelectedRows(0, -1, 42, 770, kangas);
      osoitetaulu.writeSelectedRows(0, -1, 70, 190, kangas);
   }

   private PdfPTable teeOsoitetaulu(Vastaanottaja vastaanottaja)
   {
      PdfPTable taulu = teeTaulu(1, 230);
      Format format = Format.create().withFont("Helvetica", 12, Font.NORMAL);
      lis‰‰Solu(taulu, vastaanottaja.getHenkilˆ().getNimi(), format);
      lis‰‰Solu(taulu, vastaanottaja.getOsoite().getOsoite(), format);
      lis‰‰Solu(taulu, vastaanottaja.getPostinumeroJaKaupunki(), format);
      return taulu;
   }

   private void lis‰‰Solu(PdfPTable taulu, String tieto, Format format)
   {
      PdfPCell solu = new PdfPCell();
      solu.setBorder(format.getBorder());
      solu.setBorderWidth(format.getBorderWidth());
      solu.setVerticalAlignment(format.getVerticalAlignment());
      switch (format.getPaddingType())
      {
      case Rectangle.BOX:
         solu.setPadding(format.getPadding());
         break;
      case Rectangle.BOTTOM:
         solu.setPaddingBottom(format.getPadding());
         break;
      }
      solu.setNoWrap(format.isNoWrap());
      Paragraph paragraaffi = new Paragraph(tieto, format.getFont());
      paragraaffi.setAlignment(format.getHorizontalAlignment());
      solu.addElement(paragraaffi);
      taulu.addCell(solu);
   }

   private PdfPTable teeTaulu(int sarakkeita, float leveys)
   {
      return alustaTaulu(new PdfPTable(sarakkeita), leveys);
   }

   private PdfPTable alustaTaulu(PdfPTable taulu, float leveys)
   {
      taulu.getDefaultCell().setBorder(Rectangle.NO_BORDER);
      taulu.setTotalWidth(leveys);
      taulu.getDefaultCell().setPadding(1);
      return taulu;
   }

   private void kirjoitaYl‰otsikko()
   {
      Font fontti = FontFactory.getFont("Helvetica", 12);
      fontti.setStyle(Font.BOLD);
      kirjoita(asetukset.getSaaja(), 42, 795, fontti);
      kirjoita(asetukset.getYl‰otsikko(), 350, 795, fontti);
   }

   private void kirjoita(String teksti, int x, int y, Font fontti)
   {
      ColumnText.showTextAligned(kangas, Element.ALIGN_LEFT, new Phrase(teksti, fontti), x, y, 0);
   }

   private void kirjoita(String teksti, int x, int y)
   {
      kirjoita(teksti, x, y, FontFactory.getFont("Helvetica", 12));
   }

   private byte[] loppuk‰sittely() throws DocumentException, IOException
   {
      lukija.close();
      kirjoittaja.close();
      return tulos.toByteArray();
   }
}
