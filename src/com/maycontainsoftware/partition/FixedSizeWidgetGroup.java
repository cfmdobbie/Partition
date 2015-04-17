package com.maycontainsoftware.partition;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * Widget group that has a fixed size.
 * 
 * This class is used for UI components that need to participate in layout but must always maintain a particular size.
 * They are allowed to be neither smaller nor larger than the defined size.
 * 
 * @author Charlie
 */
public class FixedSizeWidgetGroup extends WidgetGroup {

	/** The fixed width. */
	private final float width;

	/** The fixed height. */
	private final float height;

	/**
	 * Construct a new FixedSizeWidgetGroup. To avoid issues with re-layout, the size is fixed in the constructor and
	 * cannot be changed after component creation. Subclasses of this class can guarantee that methods of WidgetGroup
	 * that return a size will return the correct values during their constructors. Note that methods of Actor that
	 * return a size will return different values until first layout is performed.
	 * 
	 * @param width
	 *            The fixed width.
	 * @param height
	 *            The fixed height.
	 */
	public FixedSizeWidgetGroup(final float width, final float height) {
		this.width = width;
		this.height = height;
	}

	/** Preferred width is just the fixed width. */
	@Override
	public float getPrefWidth() {
		return width;
	}

	/** Preferred height is the fixed height. */
	@Override
	public float getPrefHeight() {
		return height;
	}

	/** Maximum width is the fixed width. */
	public float getMaxWidth() {
		return getPrefWidth();
	}

	/** Maximum height is the fixed height. */
	public float getMaxHeight() {
		return getPrefHeight();
	}
}
