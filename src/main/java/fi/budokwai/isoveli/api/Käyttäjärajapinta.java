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
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Henkil�;

@Path("kayttaja")
@Stateless
public class K�ytt�j�rajapinta
{
   @PersistenceContext
   private EntityManager entityManager;

   @POST
   @Path("/kuva/{id}")
   @Consumes("multipart/form-data")
   public Response tallennaKuva(@MultipartForm Kuvatieto kuvatieto, @PathParam("id") int id)
   {
      Harrastaja henkil� = entityManager.find(Harrastaja.class, id);
      henkil�.setKuva(kuvatieto.getTieto());
      entityManager.persist(henkil�);
      return Response.status(200).entity("OK").build();
   }

   @GET
   @Path("/sahkopostilistalla")
   @Produces("text/plain")
   public String getS�hk�postilistalla()
   {
      StringBuilder sb = new StringBuilder();
      List<Henkil�> henkil�t = entityManager.createNamedQuery("s�hk�postilistalla", Henkil�.class).getResultList();
      henkil�t
         .forEach(h -> sb.append(String.format("%s%s", h.getYhteystiedot().getS�hk�posti(), System.lineSeparator())));
      return sb.toString();
   }

}