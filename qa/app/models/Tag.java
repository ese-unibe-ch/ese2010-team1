package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Tag extends Model implements Comparable<Tag> {

	public String name;
	
	private Tag(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public int compareTo(Tag otherTag) {
		return name.compareTo(otherTag.name);
	}
	
	public static Tag findOrCreateByName(String name) {
		Tag tag = Tag.find("byName", name).first();
		if(tag == null) {
			tag =new Tag(name);
		}
		return tag;
	}
	
	public static List<Map> getTagCloud() {
		List<Map> result = Tag.find("select new map(t.name as tag, count(q.id) as pound) from Question q join p.tags as t group by t.name order by t.name").fetch();
		return result;
	}
	
	
}
