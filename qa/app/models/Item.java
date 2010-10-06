package models;
/**
 * A item owned by a {@link User}.
 * 
 * @author Simon Marti
 * @author Mirco Kocher
 *
 */
public abstract class Item {
	private User owner;
	
	/**
	 * Create an <code>Item</code>
	 * @param owner the {@link User} who ownes the <code>Item</code>
	 */
	public Item(User owner) {
		this.owner = owner;
		owner.registerItem(this);
	}
	
	/**
	 * Get the owner of an <code>Item</code>
	 * @return the owner
	 */
	public User owner() {
		return this.owner;
	}
	
	/**
	 * Unregisters the <code>Item</code> if it gets deleted.
	 */
	public void unregister() {
		this.unregisterUser();
	}
	
	/**
	 * Unregisters the <code>Item</code> to it's owner.
	 */
	protected void unregisterUser() {
		this.owner.unregister(this);
	}
}
