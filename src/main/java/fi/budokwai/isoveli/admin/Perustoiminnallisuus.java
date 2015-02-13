package fi.budokwai.isoveli.admin;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.icefaces.util.JavaScriptRunner;

public class Perustoiminnallisuus
{
   private String infoViesti = "$().toastmessage('showToast', { text : '%s', position : 'top-right', sticky: false, type : 'success'});";
   private String virheViesti = "$().toastmessage('showToast', { text : '%s', position : 'center', sticky: false, type : 'error', stayTime: 8000});";

   protected void fokusoi(String kenttä)
   {
      String js = String.format("document.getElementById('%s').focus();", kenttä);
      JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), js);
   }

   protected void info(String runko, Object... parametrit)
   {
      JavaScriptRunner.runScript(FacesContext.getCurrentInstance(),
         String.format(infoViesti, kasaaViesti(runko, parametrit)));
   }

   protected void virhe(String runko, Object... parametrit)
   {
      JavaScriptRunner.runScript(FacesContext.getCurrentInstance(),
         String.format(virheViesti, kasaaViesti(runko, parametrit)));
   }

   protected String kasaaViesti(String runko, Object[] parametrit)
   {
      return String.format(runko, parametrit);
   }

   protected void viesti(String viesti, FacesMessage.Severity taso)
   {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(taso, viesti, null));
   }
}
