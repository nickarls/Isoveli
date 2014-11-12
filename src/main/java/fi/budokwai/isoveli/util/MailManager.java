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
   private static final String LÄHETTÄJÄ = "nicklas.karlsson@affecto.com";

   @Resource(name = "java:jboss/mail/Isoveli")
   private Session mailiSessio;

   public void lähetäSähköposti(String osoite, String otsikko, String teksti)
   {
      MimeMessage viesti = new MimeMessage(mailiSessio);
      try
      {
         viesti.setRecipients(Message.RecipientType.TO, osoite);
         viesti.setFrom(new InternetAddress(LÄHETTÄJÄ));
         viesti.setSubject(otsikko);
         viesti.setSentDate(new java.util.Date());
         Transport.send(viesti);
      } catch (MessagingException e)
      {
         throw new IsoveliPoikkeus("Sähköpostin lähetys epäonnistui", e);
      }
   }

   public void lähetäSähköposti(String sähköposti, String otsikko, String teksti, BlobData pdf)
   {
      try
      {
         MimeMessage viesti = teeViesti(sähköposti, otsikko);
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
         throw new IsoveliPoikkeus(String.format("Viestin lähettäminen osoitteeseen %s epäonnistui", sähköposti), e);
      }
   }

   private MimeMessage teeViesti(String sähköposti, String otsikko)
   {
      MimeMessage viesti = new MimeMessage(mailiSessio);
      try
      {
         viesti.setRecipients(Message.RecipientType.TO, sähköposti);
         viesti.setFrom(new InternetAddress(LÄHETTÄJÄ));
         viesti.setSubject(otsikko);
         viesti.setSentDate(new java.util.Date());
      } catch (MessagingException e)
      {
         throw new IsoveliPoikkeus("Sähköpostin lähetys epäonnistui", e);
      }
      return viesti;
   }
}
