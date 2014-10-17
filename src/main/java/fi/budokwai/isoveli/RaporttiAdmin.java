package fi.budokwai.isoveli;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import fi.budokwai.isoveli.malli.Harrastaja;

@Named
public class RaporttiAdmin
{
   @PersistenceContext
   private EntityManager entityManager;

   public void muodostaJäsenkortit() throws RowsExceededException, WriteException, IOException
   {
      File kansio = new File("c:/temp/jäsenkortit");
      List<Harrastaja> harrastajat = entityManager.createNamedQuery("harrastajat", Harrastaja.class).getResultList();
      WritableWorkbook työkirja = Workbook.createWorkbook(new File(kansio, "jäsenkortit.xls"));
      WritableSheet sivu = työkirja.createSheet("jäsenkortit", 0);
      int rivi = 0;
      for (Harrastaja harrastaja : harrastajat)
      {
         sivu.addCell(new Label(0, rivi, harrastaja.getNimi()));
         sivu.addCell(new Label(1, rivi, harrastaja.getJäsennumero()));
         if (harrastaja.isKuvallinen())
         {
            BufferedImage kuva = ImageIO.read(new ByteArrayInputStream(harrastaja.getKuva()));
            ImageIO.write(kuva, "png",
               new FileOutputStream(new File(kansio, String.format("%s.png", harrastaja.getNimi()))));
         }
         rivi++;
      }
      työkirja.write();
      työkirja.close();
   }

}
