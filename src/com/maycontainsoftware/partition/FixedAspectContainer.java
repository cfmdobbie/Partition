package com.maycontainsoftware.partition;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class FixedAspectContainer extends WidgetGroup {
	private final Actor child;
	private final float aspect;

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
