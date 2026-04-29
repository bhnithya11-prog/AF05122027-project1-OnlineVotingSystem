package model;

/**
 * User - Model class representing a registered voter.
 * Maps directly to the 'users' table in the database.
 */
public class User {

    private int     userId;
    private String  name;
    private String  password;
    private boolean hasVoted;  // true if the user has already cast a vote

    // ----------------------------
    // Constructors
    // ----------------------------

    public User() {}

    /** Constructor for creating a new user (before DB insertion) */
    public User(String name, String password) {
        this.name     = name;
        this.password = password;
        this.hasVoted = false;
    }

    /** Full constructor (used when reading from DB) */
    public User(int userId, String name, String password, boolean hasVoted) {
        this.userId   = userId;
        this.name     = name;
        this.password = password;
        this.hasVoted = hasVoted;
    }

    // ----------------------------
    // Getters & Setters
    // ----------------------------

    public int getUserId()              { return userId;   }
    public void setUserId(int userId)   { this.userId = userId; }

    public String getName()             { return name;     }
    public void setName(String name)    { this.name = name; }

    public String getPassword()         { return password; }
    public void setPassword(String pw)  { this.password = pw; }

    public boolean isHasVoted()         { return hasVoted; }
    public void setHasVoted(boolean v)  { this.hasVoted = v; }

    @Override
    public String toString() {
        return "User{id=" + userId + ", name='" + name + "', hasVoted=" + hasVoted + "}";
    }
}
