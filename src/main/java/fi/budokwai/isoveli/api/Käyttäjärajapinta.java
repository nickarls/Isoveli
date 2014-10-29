package fi.budokwai.isoveli.api;

import java.io.IOException;
import java.util.List;

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

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import fi.budokwai.isoveli.admin.RaporttiAdmin;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Henkilö;
import fi.budokwai.isoveli.malli.Lasku;
import fi.budokwai.isoveli.util.AuditManager;
import fi.budokwai.isoveli.util.Lasku2PDF;
import fi.budokwai.isoveli.util.MailManager;

@Path("kayttaja")
@Stateless
public class Käyttäjärajapinta
{
   @PersistenceContext
   private EntityManager entityManager;

   @Inject
   private RaporttiAdmin raporttiAdmin;

   @Inject
   private AuditManager auditManager;

   @Inject
   private MailManager mailManager;

   @GET
   @Path("/sahkopostilistalla")
   @Produces("text/plain")
   public String getSähköpostilistalla()
   {
      StringBuilder sb = new StringBuilder();
      List<Henkilö> henkilöt = entityManager.createNamedQuery("sähköpostilistalla", Henkilö.class).getResultList();
      henkilöt
         .forEach(h -> sb.append(String.format("%s%s", h.getYhteystiedot().getSähköposti(), System.lineSeparator())));
      return sb.toString();
   }

   @GET
   @Path("/resetoiSalasana/{avain}")
   @Produces("text/plain")
   public String resetoiSalasana(@PathParam("avain") String avain)
   {
      Integer id = auditManager.haeResetoitava(avain);
      if (id == null)
      {
         return "Resetoitavaa henkilöä ei löytynyt. Pyyntö on mahdollisesti vanhentunut, tee uusi yritys";
      }
      Henkilö henkilö = entityManager.find(Henkilö.class, id);
      if (henkilö == null)
      {
         return "Resetoitavaa henkilöä ei löytynyt tietokannasta. Omituista. Ota yhteyttä salin päivystäjään";
      }
      String salasana = henkilö.luoUusiSalasana();
      entityManager.persist(henkilö);
      mailManager.lähetäSähköposti(henkilö.getYhteystiedot().getSähköposti(), "Isoveli - uusi salasana",
         String.format("Uusi salasanasi on %s", salasana));
      return String.format("Uusi salasana on lähetetty osoitteeseen %s", henkilö.getYhteystiedot().getSähköposti());
   }

   @GET
   @Path("/l")
   @Produces("application/pdf")
   public byte[] l() throws Exception
   {
      Lasku l = entityManager.find(Lasku.class, 1);
      byte[] m = entityManager.find(BlobData.class, 1).getTieto();
      return new Lasku2PDF(m, l).muodosta();
   }

   @GET
   @Path("jasenkortit")
   @Produces("application/zip")
   public Response muodostaJäsenkortit() throws RowsExceededException, WriteException, IOException
   {
      BlobData blobData = raporttiAdmin.muodostaJäsenkortit();
      ResponseBuilder response = Response.ok(blobData.getTieto(),
         MediaType.valueOf(blobData.getTyyppi().getMimetyyppi()));
      String tiedostonimi = String.format("%s.%s", blobData.getNimi(), blobData.getTyyppi().getTyyppi()).toLowerCase();
      response.header("Content-Disposition", String.format("attachment; filename=%s", tiedostonimi));
      response.header("Content-Length", blobData.getTieto().length);
      return response.build();
   }

}