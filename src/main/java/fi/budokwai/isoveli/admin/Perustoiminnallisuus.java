package fi.budokwai.isoveli.admin;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.Validator;

import org.icefaces.util.JavaScriptRunner;

import fi.budokwai.isoveli.util.Loggaaja;

public class Perustoiminnallisuus
{

   @Inject
   protected Loggaaja loggaaja;
   
   @Inject
   protected Validator validator;

   private String infoViesti = "$().toastmessage('showToast', { text : '%s', position : 'top-right', sticky: false, type : 'success'});";
   private String virheViesti = "$().toastmessage('showToast', { text : '%s', position : 'center', sticky: false, type : 'error', stayTime: 8000});";

   private void ajaSkripti(String skripti)
   {
      if (FacesContext.getCurrentInstance() == null)
      {
         return;
      }
      JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), skripti);
   }

   protected void fokusoi(String kenttä)
   {
      ajaSkripti(String.format("document.getElementById('%s').focus();", kenttä));
   }

   protected void info(String runko, Object... parametrit)
   {
      ajaSkripti(String.format(infoViesti, kasaaViesti(runko, parametrit)));
   }

   protected void virhe(String runko, Object... parametrit)
   {
      ajaSkripti(String.format(virheViesti, kasaaViesti(runko, parametrit)));
   }

   protected String kasaaViesti(String runko, Object[] parametrit)
   {
      return String.format(runko, parametrit);
   }

}
