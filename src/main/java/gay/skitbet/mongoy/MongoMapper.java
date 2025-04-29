package gay.skitbet.mongoy;

import gay.skitbet.mongoy.annotation.IdField;
import org.bson.Document;

import java.lang.reflect.*;
import java.util.*;

/**
 * MongoMapper is responsible for converting objects to MongoDB documents and vice versa.
 * It also handles complex nested types like Lists and Maps.
 */
public class MongoMapper {

    /**
     * Converts an object to a MongoDB Document.
     * @param object The object to be converted.
     * @return The corresponding MongoDB document.
     */
    public static Document toDocument(Object object) {
        Document document = new Document();
        // loop though all declared fields
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true); // set accessable so we can edit them if theyre private
            try {
                Object value = field.get(object); // get value of field
                if (value == null) continue; // ksip if no value

//                put the document _id in if we have the @IDField
                if (field.isAnnotationPresent(IdField.class)) {
                    document.put("_id", value); //
                } else {
                    // Serialize the value of the field based on its type
                    document.put(field.getName(), serializeValue(value));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return document;
    }

    /**
     * Serializes an object into a format that MongoDB understands.
     * Handles complex types like Lists and Maps.
     * @param value The value to serialize.
     * @return The serialized value.
     */
    private static Object serializeValue(Object value) {
        // if value is a prim type or string we return it as is
        if (isPrimitiveOrWrapper(value.getClass()) || value instanceof String) {
            return value;
        } else if (value instanceof Map<?, ?> map) {
            // serialize map types as mongodb document (key-value)
            Document mapDoc = new Document();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                // recursively serialize each map entry value
                mapDoc.put(entry.getKey().toString(), serializeValue(entry.getValue()));
            }
            return mapDoc;
        } else if (value instanceof List<?> list) {
            // serialize list types as an array
            List<Object> serialized = new ArrayList<>();
            for (Object item : list) {
                // recursively serialize each item in list
                serialized.add(serializeValue(item));
            }
            return serialized;
        } else {
            // if its custom object, convert it to mongodb document
            return toDocument(value);
        }
    }

    /**
     * Checks if the given type is a primitive or wrapper class.
     * @param type The class type to check.
     * @return True if the type is primitive or wrapper; false otherwise.
     */
    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        // checks if the type is primitive (like int or booleans) or one of its wrapper classes (Integer, Boolean, etc)
        return type.isPrimitive() ||
                type == Boolean.class ||
                type == Integer.class ||
                type == Character.class ||
                type == Byte.class ||
                type == Short.class ||
                type == Double.class ||
                type == Long.class ||
                type == Float.class;
    }

    /**
     * Converts a MongoDB document into an object of the specified type.
     * @param document The MongoDB document.
     * @param clazz The class type to convert the document to.
     * @return The deserialized object.
     */
    public static <T> T fromDocument(Document document, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            // loop though all declared fields in object (including private)
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // we want to change it so set it accessable
                Object value;
                // check if annotated for ID
                if (field.isAnnotationPresent(IdField.class)) {
                    value = document.get("_id"); // get value of _id field
                } else {
                    value = document.get(field.getName()); // get field value by its name
                }

                // if value is not null, deserialize it
                if (value != null) {
                    field.set(instance, deserializeValue(value, field.getType(), field.getGenericType()));
                }
            }
            return instance; // return the constructed object!
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize document to object", e);
        }
    }

    /**
     * Deserializes a value from a MongoDB document into the appropriate Java type.
     * Handles Lists, Maps, and custom objects.
     * @param value The value from the document.
     * @param fieldType The expected field type.
     * @param genericType The generic type of the field (if applicable).
     * @return The deserialized value.
     */
    private static Object deserializeValue(Object value, Class<?> fieldType, Type genericType) {
        // if the value is mongodb document, we need to handle it
        if (value instanceof Document docValue) {
            // if the field type is map, deserialize key-value pairs.
            if (Map.class.isAssignableFrom(fieldType)) {
                Map<String, Object> map = new HashMap<>();
                ParameterizedType paramType = (ParameterizedType) genericType; // get hte paramter type of the map
                Class<?> valueType = (Class<?>) paramType.getActualTypeArguments()[1]; // extract the value type of the map
                for (String key : docValue.keySet()) {
                    Object subValue = docValue.get(key); // get each value from doc
                    // recursively deserialize the value using the type
                    map.put(key, deserializeValue(subValue, valueType, valueType));
                }
                return map;
            } else {
                // if its a custom object, deserialize the embedded document
                return fromDocument(docValue, fieldType);
            }
        } else if (value instanceof List<?> listValue && List.class.isAssignableFrom(fieldType)) {
            // if the field is a list, deserialize each item in the list
            List<Object> list = new ArrayList<>();
            ParameterizedType paramType = (ParameterizedType) genericType;
            Class<?> elementType = (Class<?>) paramType.getActualTypeArguments()[0];
            for (Object item : listValue) {
                list.add(deserializeValue(item, elementType, elementType));
            }
            return list;
        }
        return value; // For primitive or non-nested types
    }
}
