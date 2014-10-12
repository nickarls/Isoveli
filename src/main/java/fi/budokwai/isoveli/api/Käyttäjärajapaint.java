package fi.budokwai.isoveli.api;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import fi.budokwai.isoveli.malli.Harrastaja;

@Path("k�ytt�j�")
@Stateless
public class K�ytt�j�rajapaint
{
   @javax.persistence.PersistenceContext
   private EntityManager entityManager;

   @POST
   @Path("/kuva/{id}")
   @Consumes("multipart/form-data")
   public Response tallennaKuva(@MultipartForm Kuvatieto kuvatieto, @PathParam("id") int id)
   {
      Harrastaja henkil� = entityManager.find(Harrastaja.class, id);
      henkil�.getHenkil�().setKuva(kuvatieto.getTieto());
      entityManager.persist(henkil�);
      return Response.status(200).entity("OK").build();
   }

}