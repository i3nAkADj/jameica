/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/gui/Navigation.java,v $
 * $Revision: 1.22 $
 * $Date: 2004/08/18 23:14:19 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.gui;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLParserFactory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import de.willuhn.datasource.GenericIterator;
import de.willuhn.jameica.plugin.PluginContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;
import de.willuhn.util.Logger;

/**
 * Bildet den Navigations-Baum im linken Frame ab.
 * @author willuhn
 */
public class Navigation {

  private NavigationItem root		= null;
  
  private Composite parent			= null;
  private Tree tree							= null;
  
	/**
	 * Mapping NavigationItem->TreeItem
	 */
  private Map mapping						= new Hashtable();
	
	/**
	 * Mapping Plugin -> NavigationItem
	 */
	private Map pluginMap					= new Hashtable();

  /**
   * Erzeugt die Navigation.
   * @param parent Das Eltern-Element.
   * @throws Exception
   */
  protected Navigation(Composite parent) throws Exception
	{
		IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
		parser.setReader(new StdXMLReader(getClass().getResourceAsStream("/navigation.xml")));
		IXMLElement xml = (IXMLElement) parser.parse();

		this.parent = parent;

		// Tree erzeugen
		this.tree = new Tree(this.parent, SWT.BORDER);
		this.tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		// Listener fuer "Folder auf machen"
		tree.addListener(SWT.Expand, new Listener() {
			public void handleEvent(Event event) {
				handleFolderOpen(event);
			}
		});
		// Listener fuer "Folder auf machen"
		tree.addListener(SWT.Collapse, new Listener() {
			public void handleEvent(Event event) {
				handleFolderClose(event);
			}
		});

		// Listener fuer die Aktionen
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				handleSelect(event);
			}
		});


		// add elements
		root = new NavigationItemXml(null,xml.getFirstChildNamed("item"),Application.getI18n());
		load(root);
	}

	/**
	 * Laedt rekursiv alle Kinder.
   * @param element Laedt alles Kinder.
   * @throws Exception
   */
  private void load(NavigationItem element) throws Exception
	{
		if (element == null)
			return;
		// Wir malen uns erstmal selbst.
		TreeItem item = null;
		NavigationItem myParent = (NavigationItem) element.getParent();
		if (myParent == null)
		{
			// Wir sind die ersten
			item = new TreeItem(this.tree,SWT.NONE);
		}
		else
		{
			// Wir holen uns das TreeItem vom Parent
			TreeItem ti = (TreeItem) mapping.get(element.getParent());
			item = new TreeItem(ti,SWT.NONE);
		}
		item.setImage(element.getIconClose());
		item.setData("iconClose",element.getIconClose());
		item.setData("iconOpen",element.getIconOpen());
		item.setData("listener",element.getListener());
		item.setText(element.getName());
		mapping.put(element,item);

		GenericIterator childs = element.getChilds();
		if (childs == null || childs.size() == 0)
			return;
		while (childs.hasNext())
		{
			load((NavigationItem) childs.next());
		}
	}

  /**
	 * Fuer zur Navigation den Navi-Tree eines Plugins hinzu.
   * @param container der PluginContainer.
   * @throws Exception
   */
  protected void addPlugin(PluginContainer container) throws Exception
	{
		if (container == null)
		{
			Logger.warn("unable to add navigation, plugin container was null");
			return;
		}
		if (!container.isInstalled())
		{
			Logger.warn("plugin is not installed, skipping navigation");
			return;
		}

		InputStream naviStream = container.getNavigation();
		if (naviStream == null)
		{
			Logger.warn("plugin contains no navigation, skipping");
			return;
		}

		I18N i18n = container.getPlugin().getResources().getI18N();

		IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
		parser.setReader(new StdXMLReader(naviStream));
		IXMLElement xml = (IXMLElement) parser.parse();

		NavigationItem item = new NavigationItemXml(root,xml.getFirstChildNamed("item"),i18n);
		pluginMap.put(container.getPluginClass(),item);
		load(item);
	}

	/**
	 * Liefert das Navigation-Item, in dem das genannte Plugin einghaengt ist.
	 * Will ein Plugin beispielsweise dynamisch seine Navigation erweitern,
	 * erhaelt es hier genau das NavigationItem, an das es sich hinten dran haengen kann.
   * @param pluginClass Klasse des Plugins, dessen oberstes NavigationItem geholt werden soll.
   * @return das NavigationItem.
   */
  public NavigationItem getPluginNavigation(Class pluginClass)
	{
		return (NavigationItem) pluginMap.get(pluginClass);
	}

	/**
   * Behandelt das Event "Ordner auf".
   * @param event das ausgeloeste Event.
   */
  private void handleFolderOpen(Event event)
	{
		Widget widget = event.item;
		if (!(widget instanceof TreeItem))
			return;
		TreeItem item = (TreeItem) widget;
		Image icon = (Image) item.getData("iconOpen");
		if (icon != null) {
			item.setImage(icon);
		}
	}

	/**
	 * Behandelt das Event "Ordner zu".
	 * @param event das ausgeloeste Event.
	 */
	private void handleFolderClose(Event event)
	{
		Widget widget = event.item;
		if (!(widget instanceof TreeItem))
			return;
		TreeItem item = (TreeItem) widget;
		Image icon = (Image) item.getData("iconClose");
		if (icon != null) {
			item.setImage(icon);
		}
	}

	/**
	 * Behandelt das Event "listener". 
	 * @param event das ausgeloeste Event.
	 */
	private void handleSelect(Event event)
	{
		Widget widget = event.item;
		if (!(widget instanceof TreeItem))
			return;
		TreeItem item = (TreeItem) widget;

		Listener l = (Listener) item.getData("listener");
		if (l == null)
			return;
		l.handleEvent(event);
	}
}


/*********************************************************************
 * $Log: Navigation.java,v $
 * Revision 1.22  2004/08/18 23:14:19  willuhn
 * @D Javadoc
 *
 * Revision 1.21  2004/08/15 17:55:17  willuhn
 * @C sync handling
 *
 * Revision 1.20  2004/08/11 23:37:21  willuhn
 * @N Navigation ist jetzt modular erweiterbar
 *
 * Revision 1.19  2004/07/21 23:54:54  willuhn
 * @C massive Refactoring ;)
 *
 * Revision 1.18  2004/06/30 20:58:39  willuhn
 * *** empty log message ***
 *
 * Revision 1.17  2004/06/10 20:56:53  willuhn
 * @D javadoc comments fixed
 *
 * Revision 1.16  2004/05/23 15:30:52  willuhn
 * @N new color/font management
 * @N new styleFactory
 *
 * Revision 1.15  2004/04/26 21:00:11  willuhn
 * @N made menu and navigation entries translatable
 *
 * Revision 1.14  2004/03/30 22:08:26  willuhn
 * *** empty log message ***
 *
 * Revision 1.13  2004/03/24 00:46:03  willuhn
 * @C refactoring
 *
 * Revision 1.12  2004/03/03 22:27:10  willuhn
 * @N help texts
 * @C refactoring
 *
 * Revision 1.11  2004/01/28 20:51:24  willuhn
 * @C gui.views.parts moved to gui.parts
 * @C gui.views.util moved to gui.util
 *
 * Revision 1.10  2004/01/23 00:29:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.9  2004/01/08 20:50:32  willuhn
 * @N database stuff separated from jameica
 *
 * Revision 1.8  2004/01/03 18:08:05  willuhn
 * @N Exception logging
 * @C replaced bb.util xml parser with nanoxml
 *
 * Revision 1.7  2003/12/12 01:28:05  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2003/12/11 21:00:54  willuhn
 * @C refactoring
 *
 * Revision 1.5  2003/12/05 18:43:01  willuhn
 * *** empty log message ***
 *
 * Revision 1.4  2003/11/18 18:56:07  willuhn
 * @N added support for pluginmenus and plugin navigation
 *
 * Revision 1.3  2003/11/13 00:37:35  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2003/10/29 00:41:26  willuhn
 * *** empty log message ***
 *
 **********************************************************************/