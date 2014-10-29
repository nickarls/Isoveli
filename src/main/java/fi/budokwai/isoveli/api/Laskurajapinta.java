package fi.budokwai.isoveli.api;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import fi.budokwai.isoveli.admin.LaskutusAdmin;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Lasku;

@Stateless
@Path("laskut")
public class Laskurajapinta
{
   @PersistenceContext
   private EntityManager entityManager;

   @Inject
   private LaskutusAdmin laskutusAdmin;

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

   @GET
   @Path("zippaaLaskuttamattomat")
   @Produces("application/zip")
   public Response zippaaLaskuttamattomat()
   {
      BlobData blobData = laskutusAdmin.zippaaLaskuttamattomat();
      ResponseBuilder response = Response.ok(blobData.getTieto(),
         MediaType.valueOf(blobData.getTyyppi().getMimetyyppi()));
      String tiedostonimi = String.format("%s.%s", blobData.getNimi(), blobData.getTyyppi().getTyyppi()).toLowerCase();
      response.header("Content-Disposition", String.format("attachment; filename=%s", tiedostonimi));
      response.header("Content-Length", blobData.getTieto().length);
      return response.build();

   }
}
