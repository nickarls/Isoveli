package fi.budokwai.isoveli.util;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import fi.budokwai.isoveli.IsoveliPoikkeus;

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
         viesti.setSubject("Isoveli - salasanan resetointipyynt�");
         viesti.setSentDate(new java.util.Date());
         Transport.send(viesti);
      } catch (MessagingException e)
      {
         throw new IsoveliPoikkeus("S�hk�postin l�hetys ep�onnistui", e);
      }
   }
}
