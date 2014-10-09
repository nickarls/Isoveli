package fi.budokwai.isoveli;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import fi.budokwai.isoveli.malli.Harrastaja;

@Path("API")
@Stateless
public class UploadFileService
{
   @javax.persistence.PersistenceContext
   private EntityManager entityManager;

   @POST
   @Path("/kuva/{id}")
   @Consumes("multipart/form-data")
   public Response uploadFile(@MultipartForm FileUploadForm form, @PathParam("id") int id)
   {
      Harrastaja h = entityManager.find(Harrastaja.class, id);
      h.getHenkilö().setKuva(form.getData());
      entityManager.persist(h);
      return Response.status(200).entity("OK").build();
   }

}