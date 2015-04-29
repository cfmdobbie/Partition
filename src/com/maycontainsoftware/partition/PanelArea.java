package com.maycontainsoftware.partition;

import java.util.Stack;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * An area of the screen that holds a stack of panels of UI content. Only the top panel is shown at any one time, and
 * panels animate on and off the screen. Popped panels are destroyed.
 * 
 * @author Charlie
 */
public class PanelArea extends WidgetGroup {

	/**
	 * Stack of panels of UI content. Aside from during animations, only one panel is visible - the top panel in the
	 * stack.
	 */
	private final Stack<Panel> panels = new Stack<Panel>();

	/** Time to animate between panels, in seconds. */
	private final static float SWITCH_TIME = 0.5f;

	/**
	 * Construct a new panel area
	 * 
	 * @param initial
	 *            The first visible panel.
	 */
	public PanelArea(final Panel initial) {

		// Initialize the stack with the first panel
		panels.push(initial);
		initial.setPanelArea(this);

		// Add new panel to the UI
		this.addActor(initial);

		// Size isn't available at this point, but sizeChanged() will be called shortly
	}

	/**
	 * Push a new panel of content onto the screen from the right.
	 * 
	 * @param next
	 *            The new panel of content.
	 */
	public void push(final Panel next) {

		// Animate previous panel off to the left
		final Panel previous = panels.peek();
		previous.addAction(Actions.moveBy(-getDeltaX(), 0, SWITCH_TIME, Interpolation.sine));

		// Add the new panel to the top of the stack
		panels.push(next);
		next.setPanelArea(this);

		// Add new panel to the UI and set its size and position
		this.addActor(next);
		next.setSize(getWidth(), getHeight());
		next.setPosition(getDeltaX(), 0);

		// Animate the new panel in from the right
		next.addAction(Actions.moveTo(0, 0, SWITCH_TIME, Interpolation.sine));
	}

	/** Pop the current panel of content and animate the next earliest panel in from the left. */
	public void pop() {

		if (panels.size() <= 1) {
			throw new RuntimeException("PanelArea::pop;panels.size<=1");
		}

		// Get references to the relevant panels
		final Panel old = panels.pop();
		final Panel next = panels.peek();

		// Animate old panel off to the right
		old.addAction(Actions.sequence(Actions.moveBy(getDeltaX(), 0, SWITCH_TIME, Interpolation.sine), new Action() {
			@Override
			public boolean act(float delta) {
				PanelArea.this.removeActor(old);
				return true;
			}
		}));

		// Animate next panel in from the left
		next.setPosition(-getDeltaX(), 0);
		next.addAction(Actions.moveTo(0, 0, SWITCH_TIME, Interpolation.sine));
	}

	@Override
	protected void sizeChanged() {

		// This should only be called shortly after initial construction.
		// There will be only one panel in the stack at that point.

		// Update size of current panel
		panels.peek().setSize(getWidth(), getHeight());

		super.sizeChanged();
	}

	/** The x-coordinate delta that panels need to observe when animating on or off the screen. */
	private float getDeltaX() {
		// Width of the panel plus a buffer to ensure panel is off screen
		return getWidth() + 50.0f;
	}

	/**
	 * Superclass of all panels that can be placed into the panel area.
	 * 
	 * @author Charlie
	 */
	static class Panel extends Table {
		/** The panel area this panel is attached to. */
		protected PanelArea panelArea;

		/** Set the panel area this panel is attached to. */
		public void setPanelArea(final PanelArea panelArea) {
			this.panelArea = panelArea;
		}
	}
}
