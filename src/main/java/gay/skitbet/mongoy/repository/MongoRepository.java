package gay.skitbet.mongoy.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import gay.skitbet.mongoy.Mongoy;
import gay.skitbet.mongoy.MongoMapper;
import gay.skitbet.mongoy.annotation.CollectionName;
import org.bson.Document;

import java.util.*;

/**
 * MongoRepository provides common CRUD operations for MongoDB entities.
 * It uses annotations to determine the MongoDB collection to operate on.
 */
public abstract class MongoRepository<T> {

    protected final MongoCollection<Document> collection;
    protected final Class<T> type;
    protected final String collectionName;

    /**
     * Constructor initializes the repository with a specific entity type.
     * @param type The entity class type.
     */
    public MongoRepository(Class<T> type) {
        this.type = type;

        CollectionName annotation = getClass().getAnnotation(CollectionName.class);
        if (annotation == null) {
            throw new IllegalStateException("@CollectionName annotation missing on " + this.getClass().getName());
        }
        this.collectionName = annotation.value();

        this.collection = resolveCollection();
    }

    /**
     * Resolves the MongoDB collection based on @CollectionName value
     * @return The resolved MongoDB collection.
     * @throws IllegalStateException If the @CollectionName annotation is missing.
     */
    private MongoCollection<Document> resolveCollection() {
        MongoDatabase database = Mongoy.getDatabase();
        return database.getCollection(collectionName);
    }

    /**
     * Saves an entity into the MongoDB collection, either inserting or updating.
     * @param entity The entity to save.
     */
    public void save(T entity) {
        if (entity instanceof BaseEntity base) {
            Date now = new Date();
            if (base.getCreatedAt() == null) {
                base.setCreatedAt(now);
            }
            base.setUpdatedAt(now);
        }

        Document doc = MongoMapper.toDocument(entity);
        Object id = doc.get("_id");
        System.out.println(doc);
        if (id == null) {
            throw new IllegalStateException("Entity missing @IdField value.");
        }

        collection.replaceOne(Filters.eq("_id", id), doc, new com.mongodb.client.model.ReplaceOptions().upsert(true));
    }

    /**
     * Finds an entity by its ID.
     * @param id The ID of the entity to find.
     * @return The found entity or null if not found.
     */
    public T findById(Object id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        if (doc == null) return null;
        return MongoMapper.fromDocument(doc, type);
    }

    /**
     * Finds all entities in the collection.
     * @return A list of all entities in the collection.
     */
    public List<T> findAll() {
        List<T> result = new ArrayList<>();
        for (Document doc : collection.find()) {
            result.add(MongoMapper.fromDocument(doc, type));
        }
        return result;
    }

    /**
     * Counts the total number of documents in the collection.
     * @return The number of documents in the collection.
     */
    public long count() {
        return collection.countDocuments();
    }

    /**
     * Checks if an entity exists by its ID.
     * @param id The ID to check.
     * @return True if the entity exists; false otherwise.
     */
    public boolean existsById(Object id) {
        return collection.find(Filters.eq("_id", id)).first() != null;
    }

    /**
     * Deletes an entity by its ID.
     * @param id The ID of the entity to delete.
     */
    public void deleteById(Object id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    /**
     * Deletes all entities in the collection.
     */
    public void deleteAll() {
        collection.deleteMany(new Document());
    }
}
