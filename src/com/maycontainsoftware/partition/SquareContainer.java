package com.maycontainsoftware.partition;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class SquareContainer extends WidgetGroup {
	private final Actor child;

	public SquareContainer(final Actor child) {
		this.child = child;
		this.addActor(child);
	}

	@Override
	protected void sizeChanged() {
		// Allow superclass to perform layout invalidation
		super.sizeChanged();
		// Determine new size/position for child
		final float size = Math.min(getWidth(), getHeight());
		final float xoffset = (getWidth() - size) / 2;
		final float yoffset = (getHeight() - size) / 2;
		// Resize and reposition child
		child.setSize(size, size);
		child.setPosition(xoffset, yoffset);
	}
}