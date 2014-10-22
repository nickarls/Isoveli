package fi.budokwai.isoveli.api;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import fi.budokwai.isoveli.malli.Lasku;

@Stateless
@Path("laskut")
public class Laskurajapinta
{
   @PersistenceContext
   private EntityManager entityManager;

   @GET
   @Path("/maksettu/{viitenumero}")
   public void merkkaaMaksetuksi(@PathParam("viitenumero") int viitenumero)
   {
      Lasku lasku = entityManager.find(Lasku.class, viitenumero);
      if (lasku == null)
      {
         return;
      }
      lasku.setMaksettu(new Date());
      entityManager.persist(lasku);
   }
}
