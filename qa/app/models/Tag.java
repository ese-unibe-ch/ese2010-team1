package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

/**
 * The Class Tag implements methods to tag questions.
 */
@Entity
public class Tag extends Model implements Comparable<Tag> {

	/** The name. */
	public String name;

	/**
	 * Instantiates a new tag.
	 * 
	 * @param name
	 *            the name
	 */
	private Tag(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Tag otherTag) {
		return name.compareTo(otherTag.name);
	}

	/**
	 * Find or create tag by name.
	 * 
	 * @param name
	 *            the name
	 * @return the tag
	 */
	public static Tag findOrCreateByName(String name) {
		Tag tag = Tag.find("byName", name).first();
		if (tag == null) {
			tag = new Tag(name);
		}
		return tag;
	}

}
