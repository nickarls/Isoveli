package fi.budokwai.isoveli.api;

import javax.ws.rs.FormParam;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class BlobLataus
{

   public BlobLataus()
   {
   }

   @FormParam("tiedosto")
   @PartType("application/octet-stream")
   private byte[] tieto;

//   @FormParam("id")
   private int id;

   @FormParam("tyyppi")
   private String tyyppi;

   @FormParam("nimi")
   private String nimi;

   public byte[] getTieto()
   {
      return tieto;
   }

   public void setTieto(byte[] tieto)
   {
      this.tieto = tieto;
   }

   public String getTyyppi()
   {
      return tyyppi;
   }

   public void setTyyppi(String tyyppi)
   {
      this.tyyppi = tyyppi;
   }

   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public String getNimi()
   {
      return nimi;
   }

   public void setNimi(String nimi)
   {
      this.nimi = nimi;
   }

}