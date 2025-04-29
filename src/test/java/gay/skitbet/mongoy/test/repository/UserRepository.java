package gay.skitbet.mongoy.test.repository;

import gay.skitbet.mongoy.annotation.CollectionName;
import gay.skitbet.mongoy.repository.MongoRepository;
import gay.skitbet.mongoy.test.model.User;

@CollectionName("users")
public class UserRepository extends MongoRepository<User> {
    /**
     * Constructor initializes the repository with a specific entity type.
     *
     * @param type The entity class type.
     */
    public UserRepository() {
        super(User.class);
    }
}
