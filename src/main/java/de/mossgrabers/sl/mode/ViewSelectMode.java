// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.sl.mode;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.mode.AbstractMode;
import de.mossgrabers.framework.view.ViewManager;
import de.mossgrabers.sl.SLConfiguration;
import de.mossgrabers.sl.controller.SLControlSurface;
import de.mossgrabers.sl.controller.SLDisplay;
import de.mossgrabers.sl.view.Views;


/**
 * Mode for selecting the view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ViewSelectMode extends AbstractMode<SLControlSurface, SLConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public ViewSelectMode (final SLControlSurface surface, final Model model)
    {
        super (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void updateDisplay ()
    {
        final Display d = this.surface.getDisplay ();
        final ViewManager viewManager = this.surface.getViewManager ();
        for (int i = 0; i < 2; i++)
        {
            d.clearRow (0 + i).setBlock (0 + i, 0, "Select mode:").done (0 + i);
            d.clearRow (2 + i);
            d.setCell (2 + i, 0, (viewManager.isActiveView (Views.VIEW_CONTROL) ? SLDisplay.RIGHT_ARROW : " ") + "Control");
            d.setCell (2 + i, 1, " " + (viewManager.isActiveView (Views.VIEW_PLAY) ? SLDisplay.RIGHT_ARROW : " ") + "Play");
            d.done (2 + i);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onRowButton (final int row, final int index, final ButtonEvent event)
    {
        // Intentionally empty
    }
}
