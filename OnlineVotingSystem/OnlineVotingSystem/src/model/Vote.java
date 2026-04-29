package model;

/**
 * Vote - Model class representing a single vote cast.
 * Maps directly to the 'votes' table in the database.
 */
public class Vote {

    private int voteId;
    private int userId;
    private int candidateId;

    // ----------------------------
    // Constructors
    // ----------------------------

    public Vote() {}

    /** Constructor for casting a new vote */
    public Vote(int userId, int candidateId) {
        this.userId      = userId;
        this.candidateId = candidateId;
    }

    /** Full constructor (used when reading from DB) */
    public Vote(int voteId, int userId, int candidateId) {
        this.voteId      = voteId;
        this.userId      = userId;
        this.candidateId = candidateId;
    }

    // ----------------------------
    // Getters & Setters
    // ----------------------------

    public int getVoteId()                  { return voteId;      }
    public void setVoteId(int voteId)       { this.voteId = voteId; }

    public int getUserId()                  { return userId;      }
    public void setUserId(int userId)       { this.userId = userId; }

    public int getCandidateId()             { return candidateId; }
    public void setCandidateId(int id)      { this.candidateId = id; }

    @Override
    public String toString() {
        return "Vote{voteId=" + voteId + ", userId=" + userId +
               ", candidateId=" + candidateId + "}";
    }
}
