package com.maycontainsoftware.general;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * A container for a single child Actor. The child Actor is constrained to the specified aspect ratio, and sized as
 * large as possible given this constraint. The child is positioned in the center of the container.
 * 
 * @author Charlie
 */
public class FixedAspectContainer extends WidgetGroup {

	/** The child Actor. */
	private Actor child;

	/** The desired aspect ratio of the child Actor. */
	private float aspect;

	/**
	 * Construct a new FixedAspectContainer. The passed child is automatically added to the widget hierarchy.
	 * 
	 * @param child
	 *            The child Actor.
	 * @param aspect
	 *            The desired aspect ratio.
	 */
	public FixedAspectContainer(final Actor child, final float aspect) {
		this.child = child;
		if (child != null) {
			this.addActor(child);
		}
		this.aspect = aspect;
	}

	/** Default constructor. */
	public FixedAspectContainer() {
	}

	/**
	 * Set the contained child actor.
	 * 
	 * @param child
	 *            The child actor.
	 */
	public void setChild(final Actor child) {
		if (this.child == child) {
			return;
		}
		if (this.child != null) {
			this.removeActor(this.child);
		}
		this.child = child;
		if (child != null) {
			this.addActor(child);
		}
	}

	/**
	 * Set the desired aspect of the contained child.
	 * 
	 * @param aspect
	 *            The new aspect ratio.
	 */
	public void setAspect(final float aspect) {

		if (aspect <= 0) {
			throw new IllegalArgumentException("Invalid aspect ratio: " + aspect);
		}
		this.aspect = aspect;
	}

	@Override
	protected void sizeChanged() {

		// Allow superclass to perform layout invalidation
		super.sizeChanged();

		if (child != null) {
			// Determine new size/position for child
			final float containerAspect = getWidth() / getHeight();
			final float childHeight = (containerAspect < aspect) ? getHeight() * containerAspect / aspect : getHeight();
			final float childWidth = (containerAspect < aspect) ? getWidth() : getWidth() * aspect / containerAspect;
			final float xoffset = (getWidth() - childWidth) / 2;
			final float yoffset = (getHeight() - childHeight) / 2;

			// Resize and reposition child
			child.setSize(childWidth, childHeight);
			child.setPosition(xoffset, yoffset);
		}
	}
}
