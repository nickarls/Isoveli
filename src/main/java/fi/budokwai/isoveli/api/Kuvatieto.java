package fi.budokwai.isoveli.api;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class Kuvatieto
{

   public Kuvatieto()
   {
   }

   private byte[] tieto;

   public byte[] getTieto()
   {
      return tieto;
   }

   @FormParam("tiedosto")
   @PartType("application/octet-stream")
   public void setTieto(byte[] tieto)
   {
      this.tieto = tieto;
   }

}