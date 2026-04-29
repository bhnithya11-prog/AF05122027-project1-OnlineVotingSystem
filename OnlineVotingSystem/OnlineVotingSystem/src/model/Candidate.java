package model;

/**
 * Candidate - Model class representing an election candidate.
 * Maps directly to the 'candidates' table in the database.
 */
public class Candidate {

    private int    candidateId;
    private String name;
    private String party;
    private int    voteCount;

    // ----------------------------
    // Constructors
    // ----------------------------

    public Candidate() {}

    /** Constructor for adding a new candidate */
    public Candidate(String name, String party) {
        this.name      = name;
        this.party     = party;
        this.voteCount = 0;
    }

    /** Full constructor (used when reading from DB) */
    public Candidate(int candidateId, String name, String party, int voteCount) {
        this.candidateId = candidateId;
        this.name        = name;
        this.party       = party;
        this.voteCount   = voteCount;
    }

    // ----------------------------
    // Getters & Setters
    // ----------------------------

    public int getCandidateId()               { return candidateId; }
    public void setCandidateId(int id)        { this.candidateId = id; }

    public String getName()                   { return name;  }
    public void setName(String name)          { this.name = name; }

    public String getParty()                  { return party; }
    public void setParty(String party)        { this.party = party; }

    public int getVoteCount()                 { return voteCount; }
    public void setVoteCount(int voteCount)   { this.voteCount = voteCount; }

    @Override
    public String toString() {
        return "Candidate{id=" + candidateId + ", name='" + name +
               "', party='" + party + "', votes=" + voteCount + "}";
    }
}
