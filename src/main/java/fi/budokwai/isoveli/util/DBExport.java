package fi.budokwai.isoveli.util;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.sql.DataSource;

import fi.budokwai.isoveli.IsoveliPoikkeus;
import fi.budokwai.isoveli.malli.BlobData;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.DateFormat;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

@Stateless
@Startup
public class DBExport
{

   @Resource
   // (lookup = "java:jboss/datasources/IsoveliDSXA")
   private DataSource tietol�hde;

   private class Saraketieto
   {
      public Saraketieto(int sarake, int tyyppi, String nimi)
      {
         this.sarake = sarake;
         this.tyyppi = tyyppi;
         this.nimi = nimi;
      }

      private int sarake;
      private int tyyppi;
      private String nimi;
   }

   @Inject
   private MailManager mailManager;

   @Inject
   private Loggaaja loggaja;

   // @Schedule(minute = "*/1", hour = "*", persistent = false)
   public void teeVarmuuskopio()
   {
      // loggaja.loggaa("Tekee varmuuskopioinnin");
      byte[] xls = dumppaaKanta();
      String otsikko = String.format("Varmuuskopio %s", new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
      BlobData tiedosto = BlobData.ZIP("isoveli.xls", xls);
      mailManager.l�het�S�hk�posti("nickarls@gmail.com", otsikko, "Liitteen�", tiedosto);
   }

   private byte[] dumppaaKanta()
   {
      Connection kanta = null;
      ByteArrayOutputStream ulos = new ByteArrayOutputStream();
      try
      {
         kanta = tietol�hde.getConnection();
         List<String> taulunimet = haeTaulunimet(kanta);
         WritableWorkbook xls = Workbook.createWorkbook(ulos);
         for (String taulu : taulunimet)
         {
            tallennaTaulu(taulu, kanta, xls);
         }
         xls.write();
         xls.close();
         return ulos.toByteArray();
      } catch (Exception e)
      {
         throw new IsoveliPoikkeus("Kantadumppi ep�onnistui", e);
      } finally
      {
         if (kanta != null)
         {
            try
            {
               kanta.close();
            } catch (SQLException e)
            {
               e.printStackTrace();
            }
         }
      }

   }

   private void tallennaTaulu(String taulu, Connection kanta, WritableWorkbook xls)
      throws SQLException, RowsExceededException, WriteException
   {
      WritableSheet v�lilehti = xls.createSheet(taulu.toLowerCase(), 0);
      ResultSet tulos = kanta.prepareStatement(String.format("select * from %s", taulu)).executeQuery();
      List<Saraketieto> saraketiedot = haeSaraketiedot(tulos);
      kirjoitaData(v�lilehti, saraketiedot, tulos);
   }

   private void kirjoitaData(WritableSheet v�lilehti, List<Saraketieto> saraketiedot, ResultSet tulos)
      throws RowsExceededException, WriteException, SQLException
   {
      WritableCellFormat muoto;
      for (Saraketieto saraketieto : saraketiedot)
      {
         muoto = new WritableCellFormat();
         muoto.setAlignment(Alignment.RIGHT);
         v�lilehti.addCell(new Label(saraketieto.sarake - 1, 0, saraketieto.nimi, muoto));
      }
      int rivi = 1;
      while (tulos.next())
      {
         for (int sarake = 0; sarake < saraketiedot.size(); sarake++)
         {
            int tyyppi = saraketiedot.get(sarake).tyyppi;
            Object arvo = tulos.getObject(sarake + 1);
            if (arvo == null)
            {
               continue;
            }
            switch (tyyppi)
            {
            case Types.INTEGER:
               v�lilehti.addCell(new jxl.write.Number(sarake, rivi, tulos.getInt(sarake + 1)));
               break;
            case Types.DATE:
               muoto = new WritableCellFormat(new DateFormat("dd.MM.yyyy"));
               v�lilehti.addCell(new jxl.write.DateTime(sarake, rivi, tulos.getDate(sarake + 1)));
               break;
            case Types.VARCHAR:
               v�lilehti.addCell(new jxl.write.Label(sarake, rivi, tulos.getString(sarake + 1)));
               break;
            case Types.TIME:
               muoto = new WritableCellFormat(new DateFormat("HH:mm"));
               v�lilehti.addCell(new jxl.write.DateTime(sarake, rivi, tulos.getTime(sarake + 1), muoto));
               break;
            case Types.TIMESTAMP:
               muoto = new WritableCellFormat(new DateFormat("dd.MM.yyyy HH:mm:ss"));
               v�lilehti.addCell(new jxl.write.DateTime(sarake, rivi, tulos.getTimestamp(sarake + 1), muoto));
               break;
            case Types.DOUBLE:
               v�lilehti.addCell(new jxl.write.Number(sarake, rivi, tulos.getDouble(sarake + 1)));
               break;
            case Types.DECIMAL:
               v�lilehti.addCell(new jxl.write.Number(sarake, rivi, tulos.getDouble(sarake + 1)));
               break;
            default:
               throw new RuntimeException(tyyppi + "");
            }
         }
         rivi++;
      }
   }

   private List<Saraketieto> haeSaraketiedot(ResultSet tulos) throws SQLException
   {
      List<Saraketieto> otsikot = new ArrayList<>();
      ResultSetMetaData metatieto = tulos.getMetaData();
      int sarakkeita = metatieto.getColumnCount();
      for (int i = 1; i <= sarakkeita; i++)
      {
         otsikot.add(new Saraketieto(i, metatieto.getColumnType(i), metatieto.getColumnName(i).toLowerCase()));
      }
      return otsikot;
   }

   private List<String> haeTaulunimet(Connection kanta) throws SQLException
   {
      List<String> nimet = new ArrayList<>();
      ResultSet tulos = kanta
         .prepareStatement(
            "select table_name from information_schema.tables where table_catalog ='ISOVELI' and table_type='TABLE'")
         .executeQuery();
      while (tulos.next())
      {
         nimet.add(tulos.getString(1));
      }
      return nimet;
   }


}
