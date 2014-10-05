package fi.budokwai.isoveli;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import fi.budokwai.isoveli.malli.Treenikäynti;
import fi.budokwai.isoveli.malli.Treenisessio;

import java.io.Serializable;
import java.util.*;

public class XTreeNode implements MutableTreeNode, Serializable
{
   private static final long serialVersionUID = 1L;

   private XTreeNode parent;
   private List<XTreeNode> children;
   private Object data;

   public XTreeNode(Object data, XTreeNode... kids)
   {
      this.data = data;
      children = new ArrayList<XTreeNode>(Arrays.asList(kids));
      for (XTreeNode t : children)
      {
         t.setupParent(this);
      }
   }

   public TreeNode getChildAt(int i)
   {
      if (children == null)
      {
         return null;
      }
      return children.get(i);
   }

   private boolean childrenSet()
   {
      return children != null && children.size() > 0;
   }

   public int getChildCount()
   {
      if (childrenSet())
      {
         return children.size();
      } else
      {
         return 0;
      }
   }

   public TreeNode getParent()
   {
      return parent;
   }

   public int getIndex(TreeNode treeNode)
   {
      return children.indexOf(treeNode);
   }

   public boolean getAllowsChildren()
   {
      return children != null;
   }

   public boolean isLeaf()
   {
      return getChildCount() == 0;
   }

   public void setupParent(XTreeNode parent)
   {
      this.parent = parent;
   }

   @SuppressWarnings("rawtypes")
   public Enumeration children()
   {
      if (children == null)
      {
         return Collections.emptyEnumeration();
      } else
      {
         return Collections.enumeration(children);
      }
   }

   public String getType()
   {
      if (data instanceof String)
      {
         return "otsikko";
      } else if (data instanceof Date)
      {
         return "päivä";
      } else if (data instanceof Treenisessio)
      {
         return "sessio";
      } else if (data instanceof Treenikäynti)
      {
         return "käynti";
      }
      return "otsikko";
   }

   public void insert(MutableTreeNode mutableTreeNode, int i)
   {
      mutableTreeNode.setParent(this);
      children.add(i, (XTreeNode) mutableTreeNode);
   }

   public void remove(int i)
   {
      children.remove(i);
   }

   public void remove(MutableTreeNode mutableTreeNode)
   {
      children.remove(mutableTreeNode);
   }

   public void setUserObject(Object o)
   {
      throw new UnsupportedOperationException();
   }

   public void removeFromParent()
   {
      if (parent != null)
      {
         parent.remove(this);
      }
   }

   public void setParent(MutableTreeNode mutableTreeNode)
   {
      parent = (XTreeNode) mutableTreeNode;
   }

   @Override
   public boolean equals(Object o)
   {
      XTreeNode x = (XTreeNode) o;
      return Objects.equals(data, x.getData());
   }

   @Override
   public int hashCode()
   {
      return data == null ? 0 : data.hashCode();
   }

   public Object getData()
   {
      return data;
   }

   public void setData(Object data)
   {
      this.data = data;
   }
}
