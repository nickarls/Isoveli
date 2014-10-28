package fi.budokwai.isoveli.api;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import fi.budokwai.isoveli.malli.BlobData;

@Path("blob")
@Stateless
public class BlobKäsittelijä
{
   @PersistenceContext
   private EntityManager entityManager;

   @POST
   @Path("/tallenna")
   @Consumes("multipart/form-data")
   public Response tallennaKuva(@MultipartForm BlobLataus blobLataus)
   {
      BlobData blobData = new BlobData(blobLataus);
      if (blobLataus.isIdKäytössä())
      {
         blobData = entityManager.find(BlobData.class, Integer.valueOf(blobLataus.getId()));
         blobData.setNimi(blobData.getNimi());
         blobData.setTyyppi(blobData.getTyyppi());
         blobData.setTieto(blobLataus.getTieto());
      }
      entityManager.persist(blobData);
      return Response.status(200).entity("OK").build();
   }

   @GET
   @Path("/lataa/{avain}")
   public Response lataa(@PathParam("avain") String avain)
   {
      List<BlobData> tulokset = entityManager.createNamedQuery("blobdata", BlobData.class).setParameter("avain", avain)
         .getResultList();
      if (tulokset.isEmpty())
      {
         return Response.serverError().build();
      }
      BlobData blobData = tulokset.iterator().next();
      ResponseBuilder response = Response.ok(blobData.getTieto(),
         MediaType.valueOf(blobData.getTyyppi().getMimetyyppi()));
      String tiedostonimi = String.format("%s.%s", blobData.getNimi(), blobData.getTyyppi().getTyyppi()).toLowerCase();
      response.header("Content-Disposition", String.format("attachment; filename=%s", tiedostonimi));
      response.header("Content-Length", blobData.getTieto().length);
      return response.build();

   }
}
