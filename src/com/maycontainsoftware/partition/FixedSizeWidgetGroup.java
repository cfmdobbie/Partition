package com.maycontainsoftware.partition;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class FixedSizeWidgetGroup extends WidgetGroup {

	private float width;
	private float height;

	public FixedSizeWidgetGroup(final float width, final float height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public float getPrefWidth() {
		return width;
	}

	@Override
	public float getPrefHeight() {
		return height;
	}

	public float getMaxWidth() {
		return getPrefWidth();
	}

	public float getMaxHeight() {
		return getPrefHeight();
	}
}
