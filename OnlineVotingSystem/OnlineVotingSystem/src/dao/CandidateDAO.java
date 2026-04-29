package dao;

import model.Candidate;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CandidateDAO - Data Access Object for the 'candidates' table.
 * Handles add, view, delete, and vote-count update operations.
 */
public class CandidateDAO {

    // -----------------------------------------------------------
    // ADD a new candidate
    // -----------------------------------------------------------

    /**
     * Inserts a new candidate into the database.
     * @return true if insertion was successful
     */
    public boolean addCandidate(Candidate candidate) {
        String sql = "INSERT INTO candidates (name, party, vote_count) VALUES (?, ?, 0)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getParty());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[CandidateDAO] Error adding candidate: " + e.getMessage());
            return false;
        }
    }

    // -----------------------------------------------------------
    // GET ALL candidates
    // -----------------------------------------------------------

    /**
     * Retrieves all candidates from the database, ordered by vote_count DESC.
     * @return List of Candidate objects
     */
    public List<Candidate> getAllCandidates() {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates ORDER BY vote_count DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Candidate(
                    rs.getInt("candidate_id"),
                    rs.getString("name"),
                    rs.getString("party"),
                    rs.getInt("vote_count")
                ));
            }

        } catch (SQLException e) {
            System.err.println("[CandidateDAO] Error fetching candidates: " + e.getMessage());
        }

        return list;
    }

    // -----------------------------------------------------------
    // GET a single candidate by ID
    // -----------------------------------------------------------

    /**
     * Retrieves a candidate by their ID.
     * @return Candidate object or null if not found
     */
    public Candidate getCandidateById(int candidateId) {
        String sql = "SELECT * FROM candidates WHERE candidate_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidateId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Candidate(
                    rs.getInt("candidate_id"),
                    rs.getString("name"),
                    rs.getString("party"),
                    rs.getInt("vote_count")
                );
            }

        } catch (SQLException e) {
            System.err.println("[CandidateDAO] Error fetching candidate by ID: " + e.getMessage());
        }

        return null;
    }

    // -----------------------------------------------------------
    // DELETE a candidate by ID
    // -----------------------------------------------------------

    /**
     * Deletes a candidate from the database.
     * Note: Cascading deletes on the votes table are NOT set by default —
     * admin should only delete candidates when no votes exist.
     * @return true if deletion was successful
     */
    public boolean deleteCandidate(int candidateId) {
        String sql = "DELETE FROM candidates WHERE candidate_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, candidateId);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[CandidateDAO] Error deleting candidate: " + e.getMessage());
            return false;
        }
    }

    // -----------------------------------------------------------
    // INCREMENT vote_count (called inside voting transaction)
    // -----------------------------------------------------------

    /**
     * Increments the vote_count of a candidate by 1.
     * Uses the shared transactional Connection so it participates
     * in the same commit/rollback as VoteDAO.insertVote().
     *
     * @param conn        shared transactional connection
     * @param candidateId the candidate being voted for
     */
    public void incrementVoteCount(Connection conn, int candidateId) throws SQLException {
        String sql = "UPDATE candidates SET vote_count = vote_count + 1 WHERE candidate_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            ps.executeUpdate();
        }
    }

    // -----------------------------------------------------------
    // GET total votes cast (sum across all candidates)
    // -----------------------------------------------------------

    /**
     * Returns the total number of votes cast in the election.
     */
    public int getTotalVotes() {
        String sql = "SELECT SUM(vote_count) AS total FROM candidates";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("[CandidateDAO] Error fetching total votes: " + e.getMessage());
        }

        return 0;
    }
}
