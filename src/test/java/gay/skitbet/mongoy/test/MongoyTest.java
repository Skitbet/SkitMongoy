package gay.skitbet.mongoy.test;

import gay.skitbet.mongoy.Mongoy;
import gay.skitbet.mongoy.test.model.User;
import gay.skitbet.mongoy.test.repository.UserRepository;

public class MongoyTest {

    public static void main(String[] args) {
        // Initialize MongoDB
        Mongoy.init("testDB");

        // Create a new UserRepository
        UserRepository userRepository = new UserRepository();

        // Create a new user
        User user = new User(1, "John", "Doe");

        // Save the user to MongoDB
        userRepository.save(user);
        System.out.println("User saved to MongoDB");

        // Retrieve the user by ID
        User retrievedUser = userRepository.findById(user.getId());
        if (retrievedUser != null) {
            System.out.println("User retrieved from MongoDB: " + retrievedUser.getFirstName() + " " + retrievedUser.getLastName());
        } else {
            System.out.println("User not found");
        }

        // Close the connection
        Mongoy.close();
    }
}

