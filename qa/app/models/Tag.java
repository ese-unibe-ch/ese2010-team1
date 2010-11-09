package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public String toString() {
		return name;
	}

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

	/**
	 * Gets the tag cloud.
	 * 
	 * @return the tag cloud
	 */
	public static List<Map> getTagCloud() {
		List<Map> result = Tag
				.find("select new map(t.name as tag, count(q.id) as pound) from Question q join p.tags as t group by t.name order by t.name")
				.fetch();
		return result;
	}

	public static List<Tag> getAllTags() {
		List<Tag> allTags = new ArrayList();
		allTags.addAll(Question.<Tag> findAll());

		return allTags;
	}

	public void editTag(String newName) {
		this.name = newName;
	}
}
