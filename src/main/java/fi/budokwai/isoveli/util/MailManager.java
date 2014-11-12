package fi.budokwai.isoveli.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.sun.istack.ByteArrayDataSource;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.BlobData;

@RequestScoped
public class MailManager
{
   private static final String L�HETT�J� = "nicklas.karlsson@affecto.com";

   @Resource(name = "java:jboss/mail/Isoveli")
   private Session mailiSessio;

   public void l�het�S�hk�posti(String osoite, String otsikko, String teksti)
   {
      MimeMessage viesti = new MimeMessage(mailiSessio);
      try
      {
         viesti.setRecipients(Message.RecipientType.TO, osoite);
         viesti.setFrom(new InternetAddress(L�HETT�J�));
         viesti.setSubject(otsikko);
         viesti.setSentDate(new java.util.Date());
         Transport.send(viesti);
      } catch (MessagingException e)
      {
         throw new IsoveliPoikkeus("S�hk�postin l�hetys ep�onnistui", e);
      }
   }

   public void l�het�S�hk�posti(String s�hk�posti, String otsikko, String teksti, BlobData pdf)
   {
      try
      {
         MimeMessage viesti = teeViesti(s�hk�posti, otsikko);
         MimeBodyPart messageBodyPart = new MimeBodyPart();
         messageBodyPart.setText(teksti);
         Multipart multipart = new MimeMultipart();
         multipart.addBodyPart(messageBodyPart);
         messageBodyPart = new MimeBodyPart();
         DataSource source = new ByteArrayDataSource(pdf.getTieto(), pdf.getTyyppi().getMimetyyppi());
         messageBodyPart.setDataHandler(new DataHandler(source));
         messageBodyPart.setFileName(pdf.getNimi());
         multipart.addBodyPart(messageBodyPart);
         Transport.send(viesti);
      } catch (MessagingException e)
      {
         throw new IsoveliPoikkeus(String.format("Viestin l�hett�minen osoitteeseen %s ep�onnistui", s�hk�posti), e);
      }
   }

   private MimeMessage teeViesti(String s�hk�posti, String otsikko)
   {
      MimeMessage viesti = new MimeMessage(mailiSessio);
      try
      {
         viesti.setRecipients(Message.RecipientType.TO, s�hk�posti);
         viesti.setFrom(new InternetAddress(L�HETT�J�));
         viesti.setSubject(otsikko);
         viesti.setSentDate(new java.util.Date());
      } catch (MessagingException e)
      {
         throw new IsoveliPoikkeus("S�hk�postin l�hetys ep�onnistui", e);
      }
      return viesti;
   }
}
