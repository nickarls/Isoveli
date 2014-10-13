package fi.budokwai.isoveli;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.icefaces.ace.model.tree.NodeStateMap;
import org.icefaces.application.PushRenderer;

import fi.budokwai.isoveli.malli.Treenik‰ynti;
import fi.budokwai.isoveli.malli.Treenisessio;

@Named
@Stateful
@SessionScoped
public class X
{

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private NodeStateMap nsm = new NodeStateMap();

   private List<XTreeNode> juuri;

   private Date alkaa = t‰n‰‰n();
   private Date p‰‰ttyy = new Date();

   public List<XTreeNode> getJuuri()
   {
      if (juuri == null)
      {
         juuri = teePuu();
      }
      return juuri;
   }

   private Date t‰n‰‰n()
   {
      Calendar kalenteri = Calendar.getInstance();
      kalenteri.set(Calendar.HOUR_OF_DAY, 0);
      kalenteri.set(Calendar.MINUTE, 0);
      kalenteri.set(Calendar.SECOND, 0);
      kalenteri.set(Calendar.MILLISECOND, 0);
      return kalenteri.getTime();
   }

   public void resetoiPuu() {
      entityManager.clear();
      juuri = null;
   }
   
   @PostConstruct
   public void init()
   {
      PushRenderer.addCurrentSession("ilmoittautuminen");
   }

   private List<XTreeNode> teePuu()
   {
      entityManager.clear();
      List<XTreeNode> tulos = new ArrayList<XTreeNode>();
      List<Treenisessio> sessiot = entityManager.createNamedQuery("treenisessiot", Treenisessio.class)
         .setParameter("alkaa", alkaa).setParameter("p‰‰ttyy", p‰‰ttyy).getResultList();
      Map<Date, List<Treenisessio>> mappaus = new HashMap<Date, List<Treenisessio>>();
      for (Treenisessio sessio : sessiot)
      {
         List<Treenisessio> lista = mappaus.get(sessio.getP‰iv‰());
         if (lista == null)
         {
            lista = new ArrayList<Treenisessio>();
            mappaus.put(sessio.getP‰iv‰(), lista);
         }
         lista.add(sessio);
      }
      List<XTreeNode> p‰iv‰m‰‰r‰t = new ArrayList<XTreeNode>();
      for (Map.Entry<Date, List<Treenisessio>> rivi : mappaus.entrySet())
      {
         List<XTreeNode> sessionoodit = new ArrayList<XTreeNode>();
         for (Treenisessio sessio : rivi.getValue())
         {
            List<XTreeNode> k‰yntinoodit = new ArrayList<XTreeNode>();
            for (Treenik‰ynti k‰ynti : sessio.getTreenik‰ynnit())
            {
               XTreeNode k‰yntinoodi = new XTreeNode(k‰ynti);
               k‰yntinoodit.add(k‰yntinoodi);
               System.out.println("TK " + k‰ynti.getHarrastaja().getNimi());
            }
            XTreeNode sessionoodi = new XTreeNode(sessio, k‰yntinoodit.toArray(new XTreeNode[]
            {}));
            sessionoodit.add(sessionoodi);
         }
         XTreeNode p‰iv‰ = new XTreeNode(rivi.getKey(), sessionoodit.toArray(new XTreeNode[]
         {}));
         p‰iv‰m‰‰r‰t.add(p‰iv‰);
      }
      XTreeNode juuri = new XTreeNode("Treenip‰iv‰t", p‰iv‰m‰‰r‰t.toArray(new XTreeNode[]
      {}));
      tulos.add(juuri);
      return tulos;
   }

   public NodeStateMap getNsm()
   {
      return nsm;
   }

   public void setNsm(NodeStateMap nsm)
   {
      this.nsm = nsm;
   }

   public void poistaValittu()
   {
      entityManager.remove(getValittu());
      entityManager.flush();
      juuri = null;
      entityManager.clear();
      nsm.setAllSelected(false);
   }

   public boolean isJuuriValittu()
   {
      return isTyyppiValittu("otsikko");
   }

   public boolean isP‰iv‰Valittu()
   {
      return isTyyppiValittu("p‰iv‰");
   }

   public boolean isSessioValittu()
   {
      return isTyyppiValittu("sessio");
   }

   public boolean isK‰yntiValittu()
   {
      return isTyyppiValittu("k‰ynti");
   }

   public boolean isTyyppiValittu(String tyyppi)
   {
      if (nsm.getSelected().isEmpty())
      {
         return false;
      }
      XTreeNode xtn = (XTreeNode) nsm.getSelected().iterator().next();
      return tyyppi.equals(xtn.getType());
   }

   public Object getValittu()
   {
      return ((XTreeNode) nsm.getSelected().iterator().next()).getData();
   }

   public Date getAlkaa()
   {
      return alkaa;
   }

   public void setAlkaa(Date alkaa)
   {
      this.alkaa = alkaa;
   }

   public Date getP‰‰ttyy()
   {
      return p‰‰ttyy;
   }

   public void setP‰‰ttyy(Date p‰‰ttyy)
   {
      this.p‰‰ttyy = p‰‰ttyy;
   }
}
