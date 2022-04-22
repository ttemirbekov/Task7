package peaksoft;

import peaksoft.model.User;
import peaksoft.service.UserServiceImpl;
import peaksoft.util.Util;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {

        UserServiceImpl userService = new UserServiceImpl();

        userService.createUsersTable();
        userService.saveUser("Almazbek","Atambaev", (byte) 65);
        userService.saveUser("Roza","Orunbaeva", (byte) 71);
        userService.saveUser("Askar","Akaev", (byte) 77);
        userService.saveUser("Sooronbai","Jeenbekov", (byte) 63);
        userService.saveUser("Kurmanbek","Bakiev", (byte) 72);


        userService.removeUserById(3);

        List<User> allUsers = userService.getAllUsers();
        allUsers.forEach(System.out::println);

        System.out.println(userService.existsByFirstName("Roza"));

        userService.dropUsersTable();

        System.out.println(userService.getAllUsers());

        userService.cleanUsersTable();
    }
}
