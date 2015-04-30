package com.maycontainsoftware.partition;

import java.util.Stack;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * An area of the screen that holds a stack of Actors holding UI content ("cards"). Aside from during animations, only
 * the top card is shown. The stack of cards can be manipulated by pushing new cards onto the top, or popping the top
 * card off and discarding it. Transitions between cards is via horizontal sliding animations.
 * 
 * @author Charlie
 */
public class CardStack extends WidgetGroup {

	/**
	 * Stack of cards of UI content. Aside from during animations, only one is visible - the top card in the stack.
	 */
	private final Stack<Card> cards = new Stack<Card>();

	/** Time to animate between cards, in seconds. */
	private final static float SWITCH_TIME = 0.5f;

	/**
	 * Construct a new card stack.
	 * 
	 * @param initial
	 *            The first visible card.
	 */
	public CardStack(final Card initial) {

		// Initialize the stack with the first card
		cards.push(initial);
		initial.setCardStack(this);

		// Add new card to the UI
		this.addActor(initial);

		// Size isn't available at this point, but sizeChanged() will be called shortly
	}

	/**
	 * Push a new card of content onto the screen from the right.
	 * 
	 * @param next
	 *            The new card of content.
	 */
	public void push(final Card next) {

		// Animate previous card off to the left
		final Card previous = cards.peek();
		previous.addAction(Actions.moveBy(-getDeltaX(), 0, SWITCH_TIME, Interpolation.sine));

		// Add the new card to the top of the stack
		cards.push(next);
		next.setCardStack(this);

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
		final Card old = cards.pop();
		final Card next = cards.peek();

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

	@Override
	protected void sizeChanged() {

		// This should only be called shortly after initial construction.
		// There will be only one card in the stack at that point.

		// Update size of current card
		cards.peek().setSize(getWidth(), getHeight());

		super.sizeChanged();
	}

	/** The x-coordinate delta that cards need to observe when animating on or off the screen. */
	private float getDeltaX() {
		// Width of the card plus a buffer to ensure card is off screen
		return getWidth() + 50.0f;
	}

	/**
	 * Superclass of all cards that can be placed onto the card stack.
	 * 
	 * @author Charlie
	 */
	static class Card extends Table {
		/** The card stack this card is attached to. */
		protected CardStack cardStack;

		/** Set the card stack this card is attached to. */
		public void setCardStack(final CardStack cardStack) {
			this.cardStack = cardStack;
		}
	}
}
