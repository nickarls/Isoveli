package fi.budokwai.isoveli.api;

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
      entityManager.persist(blobData);
      return Response.status(200).entity("OK").build();
   }

   @GET
   @Path("/lataa/{id}")
   public Response lataa(@PathParam("id") int id)
   {
      BlobData blobData = entityManager.find(BlobData.class, id);
      if (blobData == null)
      {
         return Response.serverError().build();
      }
      ResponseBuilder response = Response.ok(blobData.getTieto(),
         MediaType.valueOf(blobData.getTyyppi().getMimetyyppi()));
      String tiedostonimi = String.format("%s.%s", blobData.getNimi(), blobData.getTyyppi().getTyyppi()).toLowerCase();
      response.header("Content-Disposition", String.format("attachment; filename=%s", tiedostonimi));
      response.header("Content-Length", blobData.getTieto().length);
      return response.build();

   }
}
