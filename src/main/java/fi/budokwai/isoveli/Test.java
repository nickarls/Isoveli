package fi.budokwai.isoveli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public class Test
{

   public static void main(String[] args) throws FileNotFoundException, IOException, DocumentException
   {
      PdfReader reader = new PdfReader(new FileInputStream("c:/temp/lasku.pdf"));
      PdfStamper stamper = new PdfStamper(reader, new FileOutputStream("c:/temp/lasku2.pdf"));
      stamper.getAcroFields().setField("Päiväys", "1.1.2000");
      stamper.close();
      reader.close();
   }

}
