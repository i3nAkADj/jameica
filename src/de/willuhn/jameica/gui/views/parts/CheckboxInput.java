/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/gui/views/parts/Attic/CheckboxInput.java,v $
 * $Revision: 1.4 $
 * $Date: 2004/01/23 00:29:03 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/
package de.willuhn.jameica.gui.views.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

/**
 * @author willuhn
 * Ist zustaendig fuer Eingabefelder des Typs Checkbox.
 */
public class CheckboxInput extends Input
{

  private Button button;
  private boolean value;
  
  public final static String ENABLED = "true";
  public final static String DISABLED = "false"; 

  /**
   * Erzeugt ein neues Eingabefeld und schreib den uebergebenen Wert rein.
   */
  public CheckboxInput(boolean value)
  {
    this.value = value;
  }

  /**
   * @see de.willuhn.jameica.gui.views.parts.Input#getControl()
   */
  public Control getControl()
  {
		if (button != null)
			return button;
    button = new Button(getParent(), SWT.CHECK);
    button.setSelection(value);
    return button;
  }

  /**
   * @see de.willuhn.jameica.gui.views.parts.Input#getValue()
   */
  public String getValue()
  {
  	return button.getSelection() ? ENABLED : DISABLED;
  }

  /**
   * @see de.willuhn.jameica.gui.views.parts.Input#setValue(java.lang.String)
   */
  public void setValue(String value)
  {
    if (value == null)
      return;

    this.button.setSelection(value.equalsIgnoreCase(ENABLED));
    this.button.redraw();
  }

  /**
   * @see de.willuhn.jameica.gui.views.parts.Input#focus()
   */
  public void focus()
  {
    button.setFocus();
  }

  /**
   * @see de.willuhn.jameica.gui.views.parts.Input#disable()
   */
  public void disable()
  {
    button.setEnabled(false);
  }

  /**
   * @see de.willuhn.jameica.gui.views.parts.Input#enable()
   */
  public void enable()
  {
    button.setEnabled(true);
  }



}

/*********************************************************************
 * $Log: CheckboxInput.java,v $
 * Revision 1.4  2004/01/23 00:29:03  willuhn
 * *** empty log message ***
 *
 * Revision 1.3  2003/12/29 16:29:47  willuhn
 * @N javadoc
 *
 * Revision 1.2  2003/12/25 18:27:49  willuhn
 * @N added checkBox
 *
 * Revision 1.1  2003/12/25 18:21:54  willuhn
 * @N added checkBox
 *
 **********************************************************************/