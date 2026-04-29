package dao;

import model.Vote;
import util.DBConnection;

import java.sql.*;

/**
 * VoteDAO - Data Access Object for the 'votes' table.
 *
 * The castVote() method implements FULL TRANSACTION MANAGEMENT:
 *   1. Insert row into 'votes'
 *   2. Increment candidate's vote_count
 *   3. Mark user as has_voted = 1
 *
 * All three steps are wrapped in a single transaction.
 * On any failure, the transaction is rolled back entirely.
 */
public class VoteDAO {

    private final UserDAO      userDAO      = new UserDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();

    // -----------------------------------------------------------
    // CAST a vote — full transactional operation
    // -----------------------------------------------------------

    /**
     * Atomically:
     *   - Inserts a vote record
     *   - Updates candidate vote_count
     *   - Marks the user as having voted
     *
     * @param vote Vote object containing userId and candidateId
     * @return true if the vote was cast successfully
     */
    public boolean castVote(Vote vote) {
        Connection conn = null;

        try {
            conn = DBConnection.getConnection();

            // ---- BEGIN TRANSACTION ----
            conn.setAutoCommit(false);

            // Step 1: Insert into votes table
            insertVote(conn, vote);

            // Step 2: Increment candidate's vote count
            candidateDAO.incrementVoteCount(conn, vote.getCandidateId());

            // Step 3: Mark voter as has_voted = 1
            userDAO.markUserAsVoted(conn, vote.getUserId());

            // ---- COMMIT if all three steps succeeded ----
            conn.commit();
            System.out.println("[VoteDAO] Vote cast successfully. Transaction committed.");
            return true;

        } catch (SQLException e) {
            // ---- ROLLBACK on any failure ----
            System.err.println("[VoteDAO] Transaction failed: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("[VoteDAO] Transaction rolled back.");
                } catch (SQLException rollbackEx) {
                    System.err.println("[VoteDAO] Rollback failed: " + rollbackEx.getMessage());
                }
            }
            return false;

        } finally {
            // Restore auto-commit mode
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("[VoteDAO] Error restoring auto-commit: " + e.getMessage());
                }
            }
        }
    }

    // -----------------------------------------------------------
    // INSERT a vote row (used internally within transaction)
    // -----------------------------------------------------------

    /**
     * Inserts a row into the votes table using a shared connection.
     * This is called inside castVote() as part of the transaction.
     */
    private void insertVote(Connection conn, Vote vote) throws SQLException {
        String sql = "INSERT INTO votes (user_id, candidate_id) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vote.getUserId());
            ps.setInt(2, vote.getCandidateId());
            ps.executeUpdate();
        }
    }

    // -----------------------------------------------------------
    // CHECK if a user has already voted (redundant safety check)
    // -----------------------------------------------------------

    /**
     * Checks whether a vote exists for the given user.
     * (The has_voted flag in 'users' is the primary guard;
     *  this serves as a double-check.)
     *
     * @return true if the user has already voted
     */
    public boolean hasUserVoted(int userId) {
        String sql = "SELECT vote_id FROM votes WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("[VoteDAO] Error checking vote status: " + e.getMessage());
            return false;
        }
    }
}
