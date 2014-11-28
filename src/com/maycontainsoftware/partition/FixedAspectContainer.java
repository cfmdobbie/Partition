package com.maycontainsoftware.partition;

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
	private final Actor child;

	/** The desired aspect ratio of the child Actor. */
	private final float aspect;

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
		this.addActor(child);
		this.aspect = aspect;
	}

	@Override
	protected void sizeChanged() {

		// Allow superclass to perform layout invalidation
		super.sizeChanged();

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
