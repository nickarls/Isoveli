package fi.budokwai.isoveli.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.render.Renderer;

public class DummyUIComponent extends UIComponent
{

   @Override
   public Object saveState(FacesContext context)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void restoreState(FacesContext context, Object state)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public boolean isTransient()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void setTransient(boolean newTransientValue)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public Map<String, Object> getAttributes()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ValueBinding getValueBinding(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setValueBinding(String name, ValueBinding binding)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public String getClientId(FacesContext context)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getFamily()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setId(String id)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public UIComponent getParent()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setParent(UIComponent parent)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public boolean isRendered()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void setRendered(boolean rendered)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public String getRendererType()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setRendererType(String rendererType)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public boolean getRendersChildren()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public List<UIComponent> getChildren()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public int getChildCount()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public UIComponent findComponent(String expr)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Map<String, UIComponent> getFacets()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public UIComponent getFacet(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Iterator<UIComponent> getFacetsAndChildren()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void broadcast(FacesEvent event) throws AbortProcessingException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void decode(FacesContext context)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void encodeChildren(FacesContext context) throws IOException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void encodeEnd(FacesContext context) throws IOException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   protected void addFacesListener(FacesListener listener)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   protected FacesListener[] getFacesListeners(Class clazz)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected void removeFacesListener(FacesListener listener)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void queueEvent(FacesEvent event)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void processRestoreState(FacesContext context, Object state)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void processDecodes(FacesContext context)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void processValidators(FacesContext context)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void processUpdates(FacesContext context)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public Object processSaveState(FacesContext context)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected FacesContext getFacesContext()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   protected Renderer getRenderer(FacesContext context)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
