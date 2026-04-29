package dao;

import model.User;
import util.DBConnection;

import java.sql.*;

/**
 * UserDAO - Data Access Object for the 'users' table.
 * Handles all CRUD operations related to users/voters.
 */
public class UserDAO {

    // -----------------------------------------------------------
    // REGISTER a new user
    // -----------------------------------------------------------

    /**
     * Inserts a new user into the database.
     * @return true if registration was successful
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, password, has_voted) VALUES (?, ?, 0)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getPassword());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[UserDAO] Error registering user: " + e.getMessage());
            return false;
        }
    }

    // -----------------------------------------------------------
    // LOGIN - validate credentials
    // -----------------------------------------------------------

    /**
     * Finds a user by name and password (login check).
     * @return User object if credentials match, null otherwise
     */
    public User loginUser(String name, String password) {
        String sql = "SELECT * FROM users WHERE name = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("password"),
                    rs.getInt("has_voted") == 1
                );
            }

        } catch (SQLException e) {
            System.err.println("[UserDAO] Error during login: " + e.getMessage());
        }

        return null;  // login failed
    }

    // -----------------------------------------------------------
    // CHECK if username already exists
    // -----------------------------------------------------------

    /**
     * Checks whether a username is already taken.
     * @return true if the name already exists in DB
     */
    public boolean userExists(String name) {
        String sql = "SELECT user_id FROM users WHERE name = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            return rs.next();  // true if a row was found

        } catch (SQLException e) {
            System.err.println("[UserDAO] Error checking user existence: " + e.getMessage());
            return false;
        }
    }

    // -----------------------------------------------------------
    // UPDATE has_voted flag after a user casts their vote
    // -----------------------------------------------------------

    /**
     * Marks the user as having voted (has_voted = 1).
     * Called inside the voting transaction in VoteDAO.
     *
     * @param conn  the shared transactional connection
     * @param userId the ID of the voter
     */
    public void markUserAsVoted(Connection conn, int userId) throws SQLException {
        String sql = "UPDATE users SET has_voted = 1 WHERE user_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}
