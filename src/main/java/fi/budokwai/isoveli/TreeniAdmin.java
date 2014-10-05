package fi.budokwai.isoveli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.model.table.RowStateMap;

import fi.budokwai.isoveli.malli.Harrastaja;
import fi.budokwai.isoveli.malli.Treeni;
import fi.budokwai.isoveli.malli.Treenityyppi;
import fi.budokwai.isoveli.malli.Viikonp‰iv‰;

@Named
@SessionScoped
@Stateful
public class TreeniAdmin extends Perustoiminnallisuus
{
   private Collection<Treeni> treenit;

   @PersistenceContext(type = PersistenceContextType.EXTENDED)
   private EntityManager entityManager;

   private Treeni treeni;

   private RowStateMap rowStateMap = new RowStateMap();

   private Collection<Treenityyppi> treenityypit;

   private Collection<Harrastaja> kaikkivet‰j‰t;
   private Collection<Harrastaja> treenivet‰j‰t;

   @PostConstruct
   public void init()
   {
      ((Session) entityManager.getDelegate()).setFlushMode(FlushMode.MANUAL);
   }

   @Produces
   @Named
   public Collection<Harrastaja> getVet‰j‰t()
   {
      if (treeni == null)
      {
         return Collections.emptyList();
      }
      if (kaikkivet‰j‰t == null)
      {
         kaikkivet‰j‰t = entityManager.createNamedQuery("treenivet‰j‰t", Harrastaja.class).getResultList();
         Collection<Harrastaja> tulos = new ArrayList<Harrastaja>();
         for (Harrastaja harrastaja : kaikkivet‰j‰t)
         {
            if (harrastaja.isTreenienVet‰j‰())
            {
               tulos.add(harrastaja);
            }
         }
         kaikkivet‰j‰t = tulos;
      }
      if (treenivet‰j‰t == null)
      {
         treenivet‰j‰t = new ArrayList<Harrastaja>();
         treenivet‰j‰t.addAll(kaikkivet‰j‰t);
         treenivet‰j‰t.removeAll(treeni.getVet‰j‰t());
      }
      return treenivet‰j‰t;
   }

   @Produces
   @Named
   public List<SelectItem> getViikonp‰iv‰t()
   {
      List<SelectItem> tulos = new ArrayList<SelectItem>();
      for (Viikonp‰iv‰ viikonpaiva : Viikonp‰iv‰.values())
      {
         tulos.add(new SelectItem(viikonpaiva, viikonpaiva.toString()));
      }
      return tulos;
   }

   @Produces
   @Named
   public Collection<Treenityyppi> getTreenityypit()
   {
      if (treenityypit == null)
      {
         treenityypit = entityManager.createNamedQuery("treenityypit", Treenityyppi.class).getResultList();
      }
      return treenityypit;
   }

   @Produces
   @Named
   public Collection<Treeni> getTreenit()
   {
      if (treenit == null)
      {
         treenit = entityManager.createNamedQuery("treenit", Treeni.class).getResultList();
      }
      return treenit;
   }

   @Produces
   @Named
   public Treeni getTreeni()
   {
      return treeni;
   }

   public void tallennaTreeni()
   {
      entityManager.persist(treeni);
      entityManager.flush();
      treenit = null;
      info("Treeni tallennettu");
   }

   public void peruutaMuutos()
   {
      treeni = null;
      rowStateMap.setAllSelected(false);
      virhe("Muutokset peruttu");
   }

   public void poistaTreeni()
   {
      entityManager.remove(treeni);
      entityManager.flush();
      treeni = null;
      treenit = null;
      rowStateMap.setAllSelected(false);
      info("Treeni poistettu");
   }

   public boolean isTreeniPoistettavissa()
   {
      return treeni != null && treeni.getId() > 0;
   }

   public void lis‰‰Treeni()
   {
      treeni = new Treeni();
      treenivet‰j‰t = null;
      rowStateMap.setAllSelected(false);
      info("Uusi treeni alustettu");
   }

   public void treeniValittu(SelectEvent e)
   {
      treeni = (Treeni) e.getObject();
      treenivet‰j‰t = null;
   }

   public RowStateMap getRowStateMap()
   {
      return rowStateMap;
   }

   public void setRowStateMap(RowStateMap rowStateMap)
   {
      this.rowStateMap = rowStateMap;
   }

}
