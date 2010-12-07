package play.db.jpa;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.NoResultException;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PersistenceException;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Query;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.collection.PersistentMap;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.proxy.HibernateProxy;
import play.Play;
import play.PlayPlugin;
import play.data.binding.BeanWrapper;
import play.data.binding.Binder;
import play.data.validation.Validation;
import play.exceptions.UnexpectedException;
import play.mvc.Scope.Params;

/**
 * A super class for JPA entities 
 */
@MappedSuperclass
public class JPASupport implements Serializable {

    public transient boolean willBeSaved = false;

    public static <T extends JPASupport> T create(Class type, String name, Map<String, String[]> params) {
        try {
            Object model = type.newInstance();
            return (T) edit(model, name, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends JPASupport> T edit(String name, Params params) {
        return (T) edit(this, name, params.all());
    }

    public static <T extends JPASupport> T edit(Object o, String name, Map<String, String[]> params) {
        try {
            BeanWrapper bw = new BeanWrapper(o.getClass());
            // Start with relations
            Set<Field> fields = new HashSet<Field>();
            Class clazz = o.getClass();
            while (!clazz.equals(JPASupport.class)) {
                Collections.addAll(fields, clazz.getDeclaredFields());
                clazz = clazz.getSuperclass();
            }
            for (Field field : fields) {
                boolean isEntity = false;
                String relation = null;
                boolean multiple = false;
                //
                if (field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToOne.class)) {
                    isEntity = true;
                    relation = field.getType().getName();
                }
                if (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class)) {
                    Class fieldType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    isEntity = true;
                    relation = fieldType.getName();
                    multiple = true;
                }

                if (isEntity) {
                    if (multiple && Collection.class.isAssignableFrom(field.getType())) {
                        Collection l = new ArrayList();
                        if (Set.class.isAssignableFrom(field.getType())) {
                            l = new HashSet();
                        }
                        String[] ids = params.get(name + "." + field.getName() + "@id");
                        if(ids == null) {
                            ids = params.get(name + "." + field.getName() + ".id");
                        }
                        if (ids != null) {
                            params.remove(name + "." + field.getName() + ".id");
                            params.remove(name + "." + field.getName() + "@id");
                            for (String _id : ids) {
                                if (_id.equals("")) {
                                    continue;
                                }
                                Query q = JPA.em().createQuery("from " + relation + " where id = ?");
                                q.setParameter(1, Binder.directBind(_id, findKeyType(Play.classloader.loadClass(relation))));
                                try {
                                    l.add(q.getSingleResult());
                                } catch(NoResultException e) {
                                    Validation.addError(name+"."+field.getName(), "validation.notFound", _id);
                                }
                            }
                            bw.set(field.getName(), o, l);
                        }                        
                    } else {
                        String[] ids = params.get(name + "." + field.getName() + "@id");
                        if(ids == null) {
                            ids = params.get(name + "." + field.getName() + ".id");
                        }
                        if (ids != null && ids.length > 0 && !ids[0].equals("")) {
                            params.remove(name + "." + field.getName() + ".id");
                            params.remove(name + "." + field.getName() + "@id");
                            Query q = JPA.em().createQuery("from " + relation + " where id = ?");
                            q.setParameter(1, Binder.directBind(ids[0], findKeyType(Play.classloader.loadClass(relation))));
                            try {
                                Object to = q.getSingleResult();
                                bw.set(field.getName(), o, to);
                            } catch(NoResultException e) {
                                Validation.addError(name+"."+field.getName(), "validation.notFound", ids[0]);
                            }                            
                        } else if(ids != null && ids.length > 0 && ids[0].equals("")) {
                            bw.set(field.getName(), o , null);
                            params.remove(name + "." + field.getName() + ".id");
                            params.remove(name + "." + field.getName() + "@id");
                        }
                    }
                }
                if (field.getType().equals(FileAttachment.class) && Params.current() != null) {
                    FileAttachment fileAttachment = ((FileAttachment) field.get(o));
                    if (fileAttachment == null) {
                        fileAttachment = new FileAttachment(o, field.getName());
                        bw.set(field.getName(), o, fileAttachment);
                    }
                    File file = Params.current().get(name + "." + field.getName(), File.class);
                    if (file != null && file.exists() && file.length() > 0) {
                        fileAttachment.set(Params.current().get(name + "." + field.getName(), File.class));
                        fileAttachment.filename = file.getName();
                    } else {
                        String df = Params.current().get(name + "." + field.getName() + "_delete_", String.class);
                        if (df != null && df.equals("true")) {
                            fileAttachment.delete();
                            bw.set(field.getName(), o, null);
                        }
                    }
                    params.remove(name + "." + field.getName());
                }
            }
            // Then bind
            bw.bind(name, o.getClass(), params, "", o);
            
            return (T) o;
        } catch (Exception e) {
            throw new UnexpectedException(e);
        }
    }

    public boolean validateAndSave() {
        if(Validation.current().valid(this).ok) {
            save();
            return true;
        }
        return false;
    }

    /**
     * store (ie insert) the entity.
     */
    public <T extends JPASupport> T save() {
        if (!em().contains(this)) {
            em().persist(this);
            PlayPlugin.postEvent("JPASupport.objectPersisted", this);
        }
        avoidCascadeSaveLoops.set(new ArrayList<JPASupport>());
        try {
            saveAndCascade(true);
        } finally {
            avoidCascadeSaveLoops.get().clear();
        }
        try {
            em().flush();
        } catch (PersistenceException e) {
            if (e.getCause() instanceof GenericJDBCException) {
                throw new PersistenceException(((GenericJDBCException) e.getCause()).getSQL());
            } else {
                throw e;
            }
        }
        avoidCascadeSaveLoops.set(new ArrayList<JPASupport>());
        try {
            saveAndCascade(false);
        } finally {
            avoidCascadeSaveLoops.get().clear();
        }
        return (T) this;
    }
    static transient ThreadLocal<List<JPASupport>> avoidCascadeSaveLoops = new ThreadLocal<List<JPASupport>>();

    private void saveAndCascade(boolean willBeSaved) {
        this.willBeSaved = willBeSaved;
        if (avoidCascadeSaveLoops.get().contains(this)) {
            return;
        } else {
            avoidCascadeSaveLoops.get().add(this);
            if(willBeSaved) {
                PlayPlugin.postEvent("JPASupport.objectUpdated", this);
            }
        }
        // Cascade save
        try {
            Set<Field> fields = new HashSet<Field>();
            Class clazz = this.getClass();
            while (!clazz.equals(JPASupport.class)) {
                Collections.addAll(fields, clazz.getDeclaredFields());
                clazz = clazz.getSuperclass();
            }
            for (Field field : fields) {
                field.setAccessible(true);
                if (Modifier.isTransient(field.getModifiers())) {
                    continue;
                }
                boolean doCascade = false;
                if (field.isAnnotationPresent(OneToOne.class)) {
                    doCascade = cascadeAll(field.getAnnotation(OneToOne.class).cascade());
                }
                if (field.isAnnotationPresent(OneToMany.class)) {
                    doCascade = cascadeAll(field.getAnnotation(OneToMany.class).cascade());
                }
                if (field.isAnnotationPresent(ManyToOne.class)) {
                    doCascade = cascadeAll(field.getAnnotation(ManyToOne.class).cascade());
                }
                if (field.isAnnotationPresent(ManyToMany.class)) {
                    doCascade = cascadeAll(field.getAnnotation(ManyToMany.class).cascade());
                }
                if (doCascade) {
                    Object value = field.get(this);
                    if (value == null) {
                        continue;
                    }
                    if (value instanceof PersistentMap) {
                        if (((PersistentMap) value).wasInitialized()) {
                            for (Object o : ((Map) value).values()) {
                                if (o instanceof JPASupport) {
                                    ((JPASupport) o).saveAndCascade(willBeSaved);
                                }
                            }
                        }
                        continue;
                    }
                    if (value instanceof PersistentCollection) {
                        if (((PersistentCollection) value).wasInitialized()) {
                            for (Object o : (Collection) value) {
                                if (o instanceof JPASupport) {
                                    ((JPASupport) o).saveAndCascade(willBeSaved);
                                }
                            }
                        }
                        continue;
                    }
                    if (value instanceof HibernateProxy && value instanceof JPASupport) {
                        if (!((HibernateProxy) value).getHibernateLazyInitializer().isUninitialized()) {
                            ((JPASupport) ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation()).saveAndCascade(willBeSaved);
                        }
                        continue;
                    }
                    if (value instanceof JPASupport) {
                        ((JPASupport) value).saveAndCascade(willBeSaved);
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            throw new UnexpectedException("During cascading save()", e);
        }
    }

    static boolean cascadeAll(CascadeType[] types) {
        for (CascadeType cascadeType : types) {
            if (cascadeType == CascadeType.ALL || cascadeType == CascadeType.PERSIST) {
                return true;
            }
        }
        return false;
    }

    /**
     * Refresh the entity state.
     */
    public <T extends JPASupport> T refresh() {
        em().refresh(this);
        return (T) this;
    }

    /**
     * Merge this object to obtain a managed entity (usefull when the object comes from the Cache).
     */
    public <T extends JPASupport> T merge() {
        return (T) em().merge(this);
    }

    /**
     * Delete the entity.
     * @return The deleted entity.
     */
    public <T extends JPASupport> T delete() {
        try {
            avoidCascadeSaveLoops.set(new ArrayList<JPASupport>());
            try {
                saveAndCascade(true);
            } finally {
                avoidCascadeSaveLoops.get().clear();
            }
            em().remove(this);
            try {
                em().flush();
            } catch (PersistenceException e) {
                if (e.getCause() instanceof GenericJDBCException) {
                    throw new PersistenceException(((GenericJDBCException) e.getCause()).getSQL());
                } else {
                    throw e;
                }
            }
            avoidCascadeSaveLoops.set(new ArrayList<JPASupport>());
            try {
                saveAndCascade(false);
            } finally {
                avoidCascadeSaveLoops.get().clear();
            }
            PlayPlugin.postEvent("JPASupport.objectDeleted", this);
            return (T) this;
        } catch (PersistenceException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends JPASupport> T create(String name, Params params) {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * Count entities
     * @return number of entities of this class
     */
    public static long count() {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * Count entities with a special query.
     * Example : Long moderatedPosts = Post.count("moderated", true);
     * @param query HQL query or shortcut
     * @param params Params to bind to the query
     * @return A long
     */
    public static long count(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * Find all entities of this type
     */
    public static <T extends JPASupport> List<T> findAll() {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * Find the entity with the corresponding id.
     * @param id The entity id
     * @return The entity
     */
    public static <T extends JPASupport> T findById(Object id) {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * Prepare a query to find entities.
     * @param query HQL query or shortcut
     * @param params Params to bind to the query
     * @return A JPAQuery
     */
    public static JPAQuery find(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * Try all()
     */
    @Deprecated
    public static JPAQuery find() {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * Prepare a query to find *all* entities.
     * @return A JPAQuery
     */
    public static JPAQuery all() {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * Batch delete of entities
     * @param query HQL query or shortcut
     * @param params Params to bind to the query
     * @return Number of entities deleted
     */
    public static int delete(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * Delete all entities
     * @return Number of entities deleted
     */
    public static int deleteAll() {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * find one item matching the parametrized query
     * @param <T>
     * @param query the parametrized query expressed in OQL
     * @param params parameters of the query
     * @return <T> the first item matching the query or null
     */
    @Deprecated
    public static <T extends JPASupport> T findOneBy(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * find all items matching a parametrized query
     * @param <T>
     * @param query the parametrized query expressed in OQL
     * @param params parameters of the query
     * @return a list<T> of items matching the query
     */
    @Deprecated
    public static <T extends JPASupport> List<T> findBy(String query, Object... params) {
        throw new UnsupportedOperationException("Please annotate your JPA model with @javax.persistence.Entity annotation.");
    }

    /**
     * Retrieve the current entityManager
     * @return the current entityManager
     */
    public static EntityManager em() {
        return JPA.em();
    }

    /**
     * Try em();
     */
    @Deprecated
    public static EntityManager getEntityManager() {
        return JPA.em();
    }

    /**
     * JPASupport instances a and b are equals if either <strong>a == b</strong> or a and b have same </strong>{@link #key key} and class</strong>
     * @param other 
     * @return true if equality condition above is verified
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if ((this == other)) {
            return true;
        }
        if (!this.getClass().isAssignableFrom(other.getClass())) {
            return false;
        }
        if (this.getEntityId() == null) {
            return false;
        }
        return this.getEntityId().equals(((JPASupport) other).getEntityId());
    }

    @Override
    public int hashCode() {
        if (this.getEntityId() == null) {
            return 0;
        }
        return this.getEntityId().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getEntityId() + "]";
    }

    public static class JPAQueryException extends RuntimeException {

        public JPAQueryException(String message) {
            super(message);
        }

        public JPAQueryException(String message, Throwable e) {
            super(message + ": " + e.getMessage(), e);
        }

        public static Throwable findBestCause(Throwable e) {
            Throwable best = e;
            Throwable cause = e;
            int it = 0;
            while((cause = cause.getCause()) != null && it++ < 10) {
                if(cause == null) {
                    break;
                }
                if(cause instanceof ClassCastException) {
                    best = cause;
                    break;
                }
                if(cause instanceof SQLException) {
                    best = cause;
                    break;
                }
            }
            return best;
        }

    }

    /**
     * A JPAQuery
     */
    public static class JPAQuery {

        public Query query;
        public String sq;

        public JPAQuery(String sq, Query query) {
            this.query = query;
            this.sq = sq;
        }

        public JPAQuery(Query query) {
            this.query = query;
            this.sq = query.toString();
        }

        /**
         * Try first();
         */
        @Deprecated
        public <T> T one() {
            try {
                List<T> results = query.setMaxResults(1).getResultList();
                if (results.size() == 0) {
                    return null;
                }
                return (T) results.get(0);
            } catch (Exception e) {
                throw new JPAQueryException("Error while executing query <strong>" + sq + "</strong>", JPAQueryException.findBestCause(e));
            }
        }

        public <T> T first() {
            return (T) one();
        }

        /**
         * Bind a JPQL named parameter to the current query.
         */
        public JPAQuery bind(String name, Object param) {
            if (param.getClass().isArray()) {
                param = Arrays.asList((Object[]) param);
            }
            if (param instanceof Integer) {
                param = ((Integer) param).longValue();
            }
            query.setParameter(name, param);
            return this;
        }

        /**
         * Try fetch();
         */
        @Deprecated
        public <T> List<T> all() {
            try {
                return query.getResultList();
            } catch (Exception e) {
                throw new JPAQueryException("Error while executing query <strong>" + sq + "</strong>", JPAQueryException.findBestCause(e));
            }
        }

        /**
         * Retrieve all results of the query
         * @return A list of entities
         */
        public <T> List<T> fetch() {
            return all();
        }

        /**
         * Retrieve results of the query
         * @param max Max results to fetch
         * @return A list of entities
         */
        public <T> List<T> fetch(int max) {
            try {
                query.setMaxResults(max);
                return query.getResultList();
            } catch (Exception e) {
                throw new JPAQueryException("Error while executing query <strong>" + sq + "</strong>", JPAQueryException.findBestCause(e));
            }
        }

        /**
         * Set the position to start
         * @param position Position of the first element
         * @return A new query
         */
        public <T> JPAQuery from(int position) {
            query.setFirstResult(position);
            return this;
        }

        /**
         * Try fetch(page, length);
         */
        @Deprecated
        public <T> List<T> page(int page, int length) {
            if (page < 1) {
                page = 1;
            }
            query.setFirstResult((page - 1) * length);
            query.setMaxResults(length);
            try {
                return query.getResultList();
            } catch (Exception e) {
                throw new JPAQueryException("Error while executing query <strong>" + sq + "</strong>", JPAQueryException.findBestCause(e));
            }
        }

        /**
         * Retrieve a page of result
         * @param page Page number (start at 1)
         * @param length (page length)
         * @return a list of entities
         */
        public <T> List<T> fetch(int page, int length) {
            return page(page, length);
        }
    }

    // File attachments
    public void setupAttachment() {
        for (Field field : getClass().getDeclaredFields()) {
            if (FileAttachment.class.isAssignableFrom(field.getType())) {
                try {
                	field.setAccessible(true);
                    FileAttachment attachment = (FileAttachment) field.get(this);
                    if (attachment != null) {
                        attachment.model = this;
                        attachment.name = field.getName();
                    } else {
                        attachment = new FileAttachment();
                        attachment.model = this;
                        attachment.name = field.getName();
                        field.set(this, attachment);
                    }
                } catch (Exception ex) {
                    throw new UnexpectedException(ex);
                }
            }
        }
    }

    public void saveAttachment() {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.getType().equals(FileAttachment.class)) {
                try {
                	field.setAccessible(true);
                    FileAttachment attachment = (FileAttachment) field.get(this);
                    if (attachment != null) {
                        attachment.model = this;
                        attachment.name = field.getName();
                        attachment.save();
                    }
                } catch (Exception ex) {
                    throw new UnexpectedException(ex);
                }
            }
        }
    }

    // More utils
    public static Object findKey(Object entity) {
        try {
            Class c = entity.getClass();
            while (!c.equals(Object.class)) {
                for (Field field : c.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);
                        return field.get(entity);
                    }
                }
                c = c.getSuperclass();
            }
        } catch (Exception e) {
            throw new UnexpectedException("Error while determining the object @Id for an object of type " + entity.getClass());
        }
        return null;
    }

    public static Class findKeyType(Class c) {
        try {
            while (!c.equals(Object.class)) {
                for (Field field : c.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);
                        return field.getType();
                    }
                }
                c = c.getSuperclass();
            }
        } catch (Exception e) {
            throw new UnexpectedException("Error while determining the object @Id for an object of type " + c);
        }
        return null;
    }
    private transient Object key;

    public Object getEntityId() {
        if (key == null) {
            key = findKey(this);
        }
        return key;
    }
    // Events
    @PostLoad
    public void afterLoad() {
        setupAttachment();
    }

    @PostPersist
    @PostUpdate
    public void afterSave() {
        saveAttachment();
    }

}
