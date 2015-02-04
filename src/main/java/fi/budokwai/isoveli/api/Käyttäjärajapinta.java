package fi.budokwai.isoveli.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.ws.WebServiceContext;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.google.common.io.ByteStreams;

import fi.budokwai.isoveli.admin.RaporttiAdmin;
import fi.budokwai.isoveli.malli.BlobData;
import fi.budokwai.isoveli.malli.Harrastaja;
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

   @Resource
   private WebServiceContext webServiceContext;

   @GET
   @Path("/kuva/{id}")
   @Produces("image/png")
   public byte[] getKuva(@PathParam("id") int id, @Context ServletContext sctx) throws IOException
   {
      Harrastaja harrastaja = entityManager.find(Harrastaja.class, id);
      if (harrastaja == null)
      {
         return lataaKuva("mies.png", sctx);
      } else if (harrastaja.isKuvallinen())
      {
         return harrastaja.getKuva().getTieto();
      } else if (harrastaja.isNainen())
      {
         return lataaKuva("nainen.png", sctx);
      } else
      {
         return lataaKuva("mies.png", sctx);
      }
   }

   private byte[] lataaKuva(String kuva, ServletContext ctx) throws IOException
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteStreams.copy(ctx.getResourceAsStream(String.format("kuvat/%s", kuva)), out);
      return out.toByteArray();
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
      return new Lasku2PDF(m, l, null).muodosta();
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