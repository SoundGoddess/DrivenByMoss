// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.push.view;

import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.daw.AbstractTrackBankProxy;
import de.mossgrabers.framework.daw.SceneBankProxy;
import de.mossgrabers.framework.mode.ModeManager;
import de.mossgrabers.framework.view.AbstractSessionView;
import de.mossgrabers.framework.view.SessionColor;
import de.mossgrabers.push.PushConfiguration;
import de.mossgrabers.push.controller.PushColors;
import de.mossgrabers.push.controller.PushControlSurface;
import de.mossgrabers.push.mode.Modes;


/**
 * The Session view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class SessionView extends AbstractSessionView<PushControlSurface, PushConfiguration>
{
    /**
     * Constructor.
     *
     * @param surface The surface
     * @param model The model
     */
    public SessionView (final PushControlSurface surface, final Model model)
    {
        super ("Session", surface, model, 8, 8, true);

        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int redLo = isPush2 ? PushColors.PUSH2_COLOR2_RED_LO : PushColors.PUSH1_COLOR2_RED_LO;
        final int redHi = isPush2 ? PushColors.PUSH2_COLOR2_RED_HI : PushColors.PUSH1_COLOR2_RED_HI;
        final int black = isPush2 ? PushColors.PUSH2_COLOR2_BLACK : PushColors.PUSH1_COLOR2_BLACK;
        final int green = isPush2 ? PushColors.PUSH2_COLOR2_GREEN : PushColors.PUSH1_COLOR2_GREEN;
        final int amber = isPush2 ? PushColors.PUSH2_COLOR2_AMBER : PushColors.PUSH1_COLOR2_AMBER;
        final SessionColor isRecording = new SessionColor (redHi, redHi, false);
        final SessionColor isRecordingQueued = new SessionColor (redHi, black, true);
        final SessionColor isPlaying = new SessionColor (green, green, false);
        final SessionColor isPlayingQueued = new SessionColor (green, black, true);
        final SessionColor hasContent = new SessionColor (amber, -1, false);
        final SessionColor noContent = new SessionColor (black, -1, false);
        final SessionColor recArmed = new SessionColor (redLo, -1, false);
        this.setColors (isRecording, isRecordingQueued, isPlaying, isPlayingQueued, hasContent, noContent, recArmed);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int index = note - 36;
        int t = index % this.columns;
        int s = this.rows - 1 - index / this.columns;
        final boolean flipSession = this.surface.getConfiguration ().isFlipSession ();
        if (flipSession)
        {
            final int dummy = t;
            t = s;
            s = dummy;
        }
        final AbstractTrackBankProxy tb = this.model.getCurrentTrackBank ();

        // Birds-eye-view navigation
        if (this.surface.isShiftPressed ())
        {
            // Calculate page offsets
            final int numTracks = tb.getNumTracks ();
            final int numScenes = tb.getNumScenes ();
            final int trackPosition = tb.getTrack (0).getPosition () / numTracks;
            final int scenePosition = tb.getScenePosition () / numScenes;
            final int selX = flipSession ? scenePosition : trackPosition;
            final int selY = flipSession ? trackPosition : scenePosition;
            final int padsX = flipSession ? this.rows : this.columns;
            final int padsY = flipSession ? this.columns : this.rows;
            final int offsetX = selX / padsX * padsX;
            final int offsetY = selY / padsY * padsY;
            tb.scrollToChannel (offsetX * numTracks + t * padsX);
            tb.scrollToScene (offsetY * numScenes + s * padsY);
            return;
        }

        // Duplicate a clip
        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_DUPLICATE))
        {
            if (tb.getTrack (t).doesExist ())
                tb.getClipLauncherSlots (t).duplicateClip (s);
            return;
        }

        // Stop clip
        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_CLIP_STOP))
        {
            tb.stop (t);
            return;
        }

        // Browse for clips
        if (this.surface.isPressed (PushControlSurface.PUSH_BUTTON_BROWSE))
        {
            if (!tb.getTrack (t).doesExist ())
                return;
            this.surface.setButtonConsumed (PushControlSurface.PUSH_BUTTON_BROWSE);
            tb.getClipLauncherSlots (t).getItemAt (s).browseToInsertClip ();
            final ModeManager modeManager = this.surface.getModeManager ();
            if (!modeManager.isActiveMode (Modes.MODE_BROWSER))
                modeManager.setActiveMode (Modes.MODE_BROWSER);
            return;
        }

        super.onGridNote (note, velocity);
    }


    /** {@inheritDoc} */
    @Override
    public void updateButtons ()
    {
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_UP, ColorManager.BUTTON_STATE_OFF);
        this.surface.updateButton (PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN, ColorManager.BUTTON_STATE_OFF);
    }


    /** {@inheritDoc} */
    @Override
    public void updateSceneButtons ()
    {
        final SceneBankProxy sceneBank = this.model.getSceneBank ();
        final boolean isPush2 = this.surface.getConfiguration ().isPush2 ();
        final int off = isPush2 ? PushColors.PUSH2_COLOR_BLACK : PushColors.PUSH1_COLOR_BLACK;
        final int green = isPush2 ? PushColors.PUSH2_COLOR_SCENE_GREEN : PushColors.PUSH1_COLOR_SCENE_GREEN;
        for (int i = 0; i < 8; i++)
            this.surface.updateButton (PushControlSurface.PUSH_BUTTON_SCENE1 + i, sceneBank.sceneExists (7 - i) ? green : off);
    }


    /** {@inheritDoc} */
    @Override
    public boolean usesButton (final int buttonID)
    {
        switch (buttonID)
        {
            case PushControlSurface.PUSH_BUTTON_REPEAT:
            case PushControlSurface.PUSH_BUTTON_OCTAVE_DOWN:
            case PushControlSurface.PUSH_BUTTON_OCTAVE_UP:
                return false;

            default:
                return !this.surface.getConfiguration ().isPush2 () || buttonID != PushControlSurface.PUSH_BUTTON_USER_MODE;
        }
    }
}