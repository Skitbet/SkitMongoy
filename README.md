# Mongoy - MongoDB Java Integration

`Mongoy` is a simple and extensible Java library for interacting with MongoDB. It provides utilities for object-to-document mapping and simplifies common database operations such as saving, finding, and deleting documents.

**This is my first open and advanceish library so it not as clean as I would like.**

## Features

- Easy-to-use MongoDB connection management.
- Automatic object-to-document mapping and vice versa using reflection.
- Support for custom annotations (`@IdField` and `@CollectionName`).
- CRUD operations on MongoDB collections.
- Customizable document serialization and deserialization.

## Prerequisites

- Java 17 or higher.
- MongoDB instance (local or remote).

## Setup

### 1. Clone the repository

You can clone the project and run `mvn install` and just use depend code below, or just import the code into your own project!

```bash
git clone https://github.com/yourusername/mongoy.git
cd mongoy
```

### 2. Add Dependency

```xml
<dependency>
    <groupId>gay.skitbet</groupId>
    <artifactId>SkitMongoy</artifactId>
    <version>x.y.z</version>
</dependency>
```

**For Gradle**:

```gradle
implementation 'gay.skitbet:SkitMongoy:x.y.z'
```

### 3. Configure MongoDB Connection

To connect to your MongoDB database, use the following `Mongoy` configuration:

```java
// Initialize MongoDB client and database
Mongoy.init("mongodb://localhost:27017","yourDatabaseName");

// Default local host no auth
Mongoy.init("yourDatabaseName");
```

### 4. Define Your Models

For Models/Entities, define Java classes and annotate a field `@IdField` that represents the `_id`.

Example `User` class:

```java
package gay.skitbet.mongoy.test.model;

import gay.skitbet.mongoy.annotation.IdField;

public class User {

    @IdField
    private int id; // can be int, long, or string
    private String firstName;
    private String lastName;
    
    // Supports multiple models inside itself, works on hashmaps.
    // Doesn't need @IdField in this model just variables for data. 
    // private List<UserSettings> userSettings = new ArrayList<>(); 
    // private HashMap<String, FriendData> friends = new HashMap<>();

    // No-argument constructor
    public User() {
    }

    // Constructor with parameters
    public User(int firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
```

### 5. Create Repositories

Define repository classes to interact with MongoDB collections. They must be defined with a `@CollectionName` which defines the collection name in Mongo.

Example `MongoRepository`:

```java
package gay.skitbet.mongoy.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import gay.skitbet.mongoy.Mongoy;
import org.bson.Document;

@CollectionName("ExampleCollection")
public class UserCollection extends MongoRepository<User> {
    
    public MongoRepository() {
        super(User.class);
    }
}
```

## Example

1. **Init Mongoy and create Repository.
```java
    Mongoy.init("yourDatabaseName"); // may use uri as first argument for uri
    UserRepository userRepo = new UserRepository();
```

2. **Create a User:**

```java
User user = new User(1, "John", "Doe");
userRepo.save(user);
```

3. **Find a User by ID:**

```java
User foundUser = userRepo.findById(user.getId());
```

4. **Delete a User:**

```java
userRepo.deleteById(user.getId());
```

## More Methods

### `Mongoy.init(uri, databaseName)`
Initializes the MongoDB connection using the provided URI and database name.

### `MongoRepository.save(entity)`
Saves the provided entity to the MongoDB collection. If the entity already exists, it will be updated.

### `MongoRepository.findById(id)`
Finds and returns the entity by its ID. Returns `null` if not found.

### `MongoRepository.deleteById(id)`
Deletes the entity by its ID.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Feel free to submit issues and pull requests. This is my first open and advanceish library so it not as clean as I would like.
