package peaksoft;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import peaksoft.model.User;
import peaksoft.service.UserService;
import peaksoft.service.UserServiceImpl;
import peaksoft.util.Util;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private final UserService underTest = new UserServiceImpl();
    private final Connection connection;

    private final String testName = "Will";
    private final String testLastName = "Smith";
    private final byte testAge = 40;

    private static final String CREATE_TABLE_IF_NOT_EXISTS_QUERY = """
            create table if not exists users (
            id serial primary key,
            name varchar not null,
            last_name varchar not null,
            age smallint not null
            );
            """;
    public static final String DROP_TABLE_QUERY = "drop table users;";
    public static final String DROP_TABLE_IF_EXIST_QUERY = "drop table if exists users;";
    public String INSERT_INTO_QUERY = "insert into users (name, last_name, age) values (?, ?, ?);";
    public static final String GET_QUANTITY_OF_USERS = "select count(*) as quantity from users;";

    public UserServiceTest() throws SQLException {
        connection = new Util().getConnection();
    }

    @Before
    public void setUp() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(DROP_TABLE_IF_EXIST_QUERY);
            statement.execute(CREATE_TABLE_IF_NOT_EXISTS_QUERY);
            saveNewUser(testName, testLastName, testAge);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try (Statement statement = connection.createStatement();) {
            statement.execute(DROP_TABLE_IF_EXIST_QUERY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dropUsersTable() {

        underTest.dropUsersTable();

        assertThrows(RuntimeException.class, () -> execute(DROP_TABLE_QUERY));
    }

    @Test
    public void createUsersTable() {
        // given
        execute(DROP_TABLE_IF_EXIST_QUERY);

        // then
        underTest.createUsersTable();

        // when
        assertDoesNotThrow(() -> execute(DROP_TABLE_QUERY));
    }

    @Test
    public void saveUser() {
        // given
        underTest.saveUser(testName, testLastName, testAge);

        // then
        int result = getQuantityOfUsers();

        // when
        assertEquals(2, result);
    }

    @Test
    public void removeUserById() {
        // given
        saveNewUser("Zamir", "Sabyrzhanov", (byte) 28);

        // then
        underTest.removeUserById(2);

        // when
        assertEquals(1, getQuantityOfUsers());
    }

    @Test
    public void getAllUsers() {
        // given
        saveNewUser(testName, testLastName, testAge);

        // then
        List<User> result = underTest.getAllUsers();
        int quantityOfUsers = getQuantityOfUsers();

        // when
        assertEquals(quantityOfUsers, result.size());
    }

    @Test
    public void cleanUsersTable() {

        // given
        underTest.cleanUsersTable();
        // then
        int result = getQuantityOfUsers();

        // when
        assertEquals(0, result);
    }

    private int getQuantityOfUsers() {
        int quantityOfUsers = -1;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_QUANTITY_OF_USERS);) {

            if (!resultSet.next()) {
                Assertions.fail("no result from query - " + GET_QUANTITY_OF_USERS);
            }

            quantityOfUsers = resultSet.getInt("quantity");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quantityOfUsers;
    }

    private void saveNewUser(String name, String lastName, byte age) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_QUERY)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void execute(String query) {
        try (Statement statement = connection.createStatement();) {
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}