package com.maycontainsoftware.partition;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * An area of the screen that holds a stack of Actors holding UI content ("cards"). Aside from during animations, only
 * the top card is shown. The stack of cards can be manipulated by pushing new cards onto the top, or popping the top
 * card off and discarding it. Transitions between cards is via horizontal sliding animations.
 * 
 * @author Charlie
 */
public class CardStack extends WidgetGroup {

	/** Stack of cards of UI content. Aside from during animations, only one is visible - the top card in the stack. */
	private final Stack<Actor> cards = new Stack<Actor>();

	/** Time to animate between cards, in seconds. */
	private final static float SWITCH_TIME = 0.25f;

	/** Set of listeners interested in changes to the stack of cards. */
	private Set<IStackChangeListener> stackChangedListeners = new HashSet<IStackChangeListener>();

	/**
	 * Initialize the stack with the first card. This is only valid to call when the stack is empty.
	 * 
	 * @param initial
	 *            The initial card.
	 */
	public void setInitialCard(final Actor initial) {
		if (!cards.isEmpty()) {
			throw new RuntimeException("CardStack");
		}

		cards.push(initial);
		notifyListeners();

		this.addActor(initial);
		initial.setSize(getWidth(), getHeight());
	}

	/**
	 * Push a new card of content onto the screen from the right.
	 * 
	 * @param next
	 *            The new card of content.
	 */
	public void push(final Actor next) {

		// Animate previous card off to the left
		final Actor previous = cards.peek();
		previous.addAction(Actions.moveBy(-getDeltaX(), 0, SWITCH_TIME, Interpolation.sine));

		// Add the new card to the top of the stack
		cards.push(next);
		notifyListeners();

		// Add new card to the UI and set its size and position
		this.addActor(next);
		next.setSize(getWidth(), getHeight());
		next.setPosition(getDeltaX(), 0);

		// Animate the new card in from the right
		next.addAction(Actions.moveTo(0, 0, SWITCH_TIME, Interpolation.sine));
	}

	/** Pop the current card and animate the previous one in from the left. */
	public void pop() {

		if (cards.size() <= 1) {
			throw new RuntimeException("CardStack::pop;cards.size<=1");
		}

		// Get references to the relevant cards
		final Actor old = cards.pop();
		notifyListeners();
		final Actor next = cards.peek();

		// Animate old card off to the right
		old.addAction(Actions.sequence(Actions.moveBy(getDeltaX(), 0, SWITCH_TIME, Interpolation.sine), new Action() {
			@Override
			public boolean act(float delta) {
				CardStack.this.removeActor(old);
				return true;
			}
		}));

		// Animate next card in from the left
		next.setPosition(-getDeltaX(), 0);
		next.addAction(Actions.moveTo(0, 0, SWITCH_TIME, Interpolation.sine));
	}

	/** Switch to the first card, discarding all other cards. */
	public void first() {

		if (cards.size() <= 1) {
			throw new RuntimeException("CardStack::first;cards.size=" + cards.size());
		}

		// Get rid of current card
		final Actor current = cards.pop();
		// Animate card off to the right
		current.addAction(Actions.sequence(Actions.moveBy(getDeltaX(), 0, SWITCH_TIME, Interpolation.sine),
				new Action() {
					@Override
					public boolean act(float delta) {
						CardStack.this.removeActor(current);
						return true;
					}
				}));

		// Silently drop any extra cards in the stack
		while (cards.size() > 1) {
			final Actor unwanted = cards.pop();
			removeActor(unwanted);
		}

		// Animate first card in from the left
		final Actor first = cards.peek();
		first.setPosition(-getDeltaX(), 0);
		first.addAction(Actions.moveTo(0, 0, SWITCH_TIME, Interpolation.sine));

		notifyListeners();
	}

	/**
	 * The number of cards in the stack.
	 * 
	 * @return The size of the card stack.
	 */
	public int stackSize() {
		return cards.size();
	}

	/**
	 * Add a listener to the set of listeners interested in stack changed events.
	 * 
	 * @param listener
	 *            The IStackChangeListener implementation.
	 */
	public void addListener(final IStackChangeListener listener) {
		this.stackChangedListeners.add(listener);
	}

	/** The x-coordinate delta that cards need to observe when animating on or off the screen. */
	private float getDeltaX() {
		// Width of the card plus a buffer to ensure card is off screen
		return getWidth() + 50.0f;
	}

	/** Notify the listeners of a change in the card stack. Listeners are notified of the new stack size. */
	private void notifyListeners() {
		for (final IStackChangeListener listener : stackChangedListeners) {
			listener.stackChanged(cards.size());
		}
	}

	/**
	 * Interface for all objects interested in receiving stack changed events.
	 * 
	 * @author Charlie
	 */
	public static interface IStackChangeListener {
		/**
		 * Callback to notify the implementation that the stack has changed.
		 * 
		 * @param newSize
		 *            The new size of the stack.
		 */
		public void stackChanged(int newSize);
	}
}
