// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.command.core.AbstractTriggerCommand;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * Command to trigger the Accent.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class AccentCommand extends AbstractTriggerCommand<PushControlSurface, PushConfiguration>
{
    private boolean quitAccentMode;


    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public AccentCommand (final Model model, final PushControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        switch (event)
        {
            case DOWN:
                this.quitAccentMode = false;
                break;
            case LONG:
                this.quitAccentMode = true;
                this.surface.getModeManager ().setActiveMode (Modes.MODE_ACCENT);
                break;
            case UP:
                if (this.quitAccentMode)
                    this.surface.getModeManager ().restoreMode ();
                else
                {
                    final PushConfiguration config = this.surface.getConfiguration ();
                    config.setAccentEnabled (!config.isAccentActive ());
                }
                break;
        }
    }
}
