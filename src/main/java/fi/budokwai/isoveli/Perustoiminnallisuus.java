package fi.budokwai.isoveli;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.icefaces.util.JavaScriptRunner;

public class Perustoiminnallisuus
{
   protected void fokusoi(String kenttä)
   {
      String js = String.format("document.getElementById('%s').focus()", kenttä);
      JavaScriptRunner.runScript(FacesContext.getCurrentInstance(), js);
   }
   
   protected void info(String runko, Object... parametrit)
   {
      viesti(kasaaViesti(runko, parametrit), FacesMessage.SEVERITY_INFO);
   }

   protected void virhe(String runko, Object... parametrit)
   {
      viesti(kasaaViesti(runko, parametrit), FacesMessage.SEVERITY_ERROR);
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
