// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.view;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.configuration.AbstractConfiguration;
import de.mossgrabers.framework.configuration.Configuration;
import de.mossgrabers.framework.controller.display.Display;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.AbstractPlayView;
import de.mossgrabers.framework.view.SceneView;
import de.mossgrabers.launchpad.LaunchpadConfiguration;
import de.mossgrabers.launchpad.controller.LaunchpadColors;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;

import java.util.Arrays;


/**
 * The play view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class PlayView extends AbstractPlayView<LaunchpadControlSurface, LaunchpadConfiguration> implements SceneView
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final LaunchpadControlSurface surface, final Model model)
    {
        this ("Play", surface, model);
    }


    /**
     * Constructor.
     *
     * @param name The name of the view
     * @param surface The surface
     * @param model The model
     */
    public PlayView (final String name, final LaunchpadControlSurface surface, final Model model)
    {
        super (name, surface, model, true);

        final Configuration configuration = this.surface.getConfiguration ();
        configuration.addSettingObserver (AbstractConfiguration.ACTIVATE_FIXED_ACCENT, this::initMaxVelocity);
        configuration.addSettingObserver (AbstractConfiguration.FIXED_ACCENT_VALUE, this::initMaxVelocity);
    }


    /** {@inheritDoc} */
    @Override
    public void onActivate ()
    {
        super.onActivate ();
        this.initMaxVelocity ();
        this.surface.setLaunchpadToPrgMode ();
        this.surface.scheduleTask (this::delayedUpdateArrowButtons, 150);
    }


    private void delayedUpdateArrowButtons ()
    {
        this.surface.setButton (this.surface.getSessionButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (this.surface.getNoteButton (), LaunchpadColors.LAUNCHPAD_COLOR_OCEAN_HI);
        this.surface.setButton (this.surface.getDeviceButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
        this.surface.setButton (this.surface.getUserButton (), LaunchpadColors.LAUNCHPAD_COLOR_GREY_LO);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        if (this.model.canSelectedTrackHoldNotes ())
        {
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, LaunchpadColors.LAUNCHPAD_COLOR_OCEAN_HI);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, LaunchpadColors.LAUNCHPAD_COLOR_OCEAN_HI);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, LaunchpadColors.LAUNCHPAD_COLOR_WHITE);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, LaunchpadColors.LAUNCHPAD_COLOR_OCEAN_HI);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, LaunchpadColors.LAUNCHPAD_COLOR_OCEAN_HI);
        }
        else
        {
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE1, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE2, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE3, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE4, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE5, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE6, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE7, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
            this.surface.setButton (LaunchpadControlSurface.LAUNCHPAD_BUTTON_SCENE8, LaunchpadColors.LAUNCHPAD_COLOR_BLACK);
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onScene (final int scene, final ButtonEvent event)
    {
        if (event != ButtonEvent.DOWN)
            return;
        if (!this.model.canSelectedTrackHoldNotes ())
            return;
        final Display display = this.surface.getDisplay ();
        String name;
        switch (scene)
        {
            case 0:
                this.scales.nextScaleLayout ();
                name = this.scales.getScaleLayout ().getName ();
                this.surface.getConfiguration ().setScaleLayout (name);
                display.notify (name);
                break;
            case 1:
                this.scales.prevScaleLayout ();
                name = this.scales.getScaleLayout ().getName ();
                this.surface.getConfiguration ().setScaleLayout (name);
                display.notify (name);
                break;
            case 5:
                this.scales.toggleChromatic ();
                final boolean isChromatic = this.scales.isChromatic ();
                this.surface.getConfiguration ().setScaleInKey (!isChromatic);
                display.notify (isChromatic ? "Chromatic" : "In Key");
                break;
            case 6:
                this.scales.setScaleOffset (this.scales.getScaleOffset () + 1);
                name = Scales.BASES[this.scales.getScaleOffset ()];
                this.surface.getConfiguration ().setScaleBase (name);
                display.notify (name);
                break;
            case 7:
                this.scales.setScaleOffset (this.scales.getScaleOffset () - 1);
                name = Scales.BASES[this.scales.getScaleOffset ()];
                this.surface.getConfiguration ().setScaleBase (name);
                display.notify (name);
                break;
            default:
                // Intentionally empty
                break;
        }
        this.updateNoteMapping ();
    }


    private void initMaxVelocity ()
    {
        final int [] maxVelocity = new int [128];
        final LaunchpadConfiguration config = this.surface.getConfiguration ();
        Arrays.fill (maxVelocity, config.getFixedAccentValue ());
        maxVelocity[0] = 0;
        this.surface.setVelocityTranslationTable (config.isAccentActive () ? maxVelocity : this.defaultVelocity);
    }
}