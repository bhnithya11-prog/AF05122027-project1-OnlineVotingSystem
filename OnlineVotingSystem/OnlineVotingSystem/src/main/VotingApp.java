package main;

import dao.CandidateDAO;
import dao.UserDAO;
import dao.VoteDAO;
import model.Candidate;
import model.User;
import model.Vote;
import util.DBConnection;

import java.util.List;
import java.util.Scanner;

/**
 * VotingApp - Entry point for the Online Voting System.
 *
 * Menu structure:
 *   Main Menu
 *     1. User Registration
 *     2. User Login  -->  User Menu (Vote, View Results, Logout)
 *     3. Admin Login -->  Admin Menu (Add/View/Delete Candidates, Results, Logout)
 *     4. Exit
 */
public class VotingApp {

    // DAO instances
    private static final UserDAO      userDAO      = new UserDAO();
    private static final CandidateDAO candidateDAO = new CandidateDAO();
    private static final VoteDAO      voteDAO      = new VoteDAO();

    private static final Scanner scanner = new Scanner(System.in);

    // ================================================================
    // MAIN
    // ================================================================

    public static void main(String[] args) {
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> handleUserRegistration();
                case 2 -> handleUserLogin();
                case 3 -> handleAdminLogin();
                case 4 -> {
                    running = false;
                    DBConnection.closeConnection();
                    System.out.println("\nThank you for using the Online Voting System. Goodbye!");
                }
                default -> System.out.println("  [!] Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    // ================================================================
    // MAIN MENU
    // ================================================================

    private static void printBanner() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║     ONLINE VOTING SYSTEM  v1.0           ║");
        System.out.println("║     Powered by Java + JDBC + MySQL       ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    private static void printMainMenu() {
        System.out.println("\n══════════════ MAIN MENU ══════════════");
        System.out.println("  1. Register as Voter");
        System.out.println("  2. Voter Login");
        System.out.println("  3. Admin Login");
        System.out.println("  4. Exit");
        System.out.println("═══════════════════════════════════════");
    }

    // ================================================================
    // USER REGISTRATION
    // ================================================================

    private static void handleUserRegistration() {
        System.out.println("\n--- USER REGISTRATION ---");
        String name = readString("Enter your name: ");

        // Check if username already taken
        if (userDAO.userExists(name)) {
            System.out.println("  [!] Username '" + name + "' is already taken. Please choose another.");
            return;
        }

        String password = readString("Create a password: ");

        User newUser = new User(name, password);
        if (userDAO.registerUser(newUser)) {
            System.out.println("  [✓] Registration successful! You can now log in.");
        } else {
            System.out.println("  [✗] Registration failed. Please try again.");
        }
    }

    // ================================================================
    // USER LOGIN & MENU
    // ================================================================

    private static void handleUserLogin() {
        System.out.println("\n--- VOTER LOGIN ---");
        String name     = readString("Username: ");
        String password = readString("Password: ");

        User user = userDAO.loginUser(name, password);

        if (user == null) {
            System.out.println("  [!] Invalid credentials. Please try again.");
            return;
        }

        System.out.println("  [✓] Welcome, " + user.getName() + "!");
        userMenu(user);
    }

    private static void userMenu(User user) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n══════════════ VOTER MENU ══════════════");
            System.out.println("  Logged in as: " + user.getName()
                    + (user.isHasVoted() ? " [VOTED]" : " [NOT VOTED]"));
            System.out.println("  1. View Candidates");
            System.out.println("  2. Cast Vote");
            System.out.println("  3. View Results");
            System.out.println("  4. Logout");
            System.out.println("════════════════════════════════════════");

            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> displayCandidates(false);
                case 2 -> handleVoting(user);
                case 3 -> displayResults();
                case 4 -> {
                    loggedIn = false;
                    System.out.println("  [✓] Logged out successfully.");
                }
                default -> System.out.println("  [!] Invalid choice.");
            }
        }
    }

    // ================================================================
    // VOTE CASTING
    // ================================================================

    private static void handleVoting(User user) {
        // Prevent double voting using the has_voted flag
        if (user.isHasVoted()) {
            System.out.println("  [!] You have already cast your vote. Each user can vote only once.");
            return;
        }

        List<Candidate> candidates = candidateDAO.getAllCandidates();
        if (candidates.isEmpty()) {
            System.out.println("  [!] No candidates are available for voting.");
            return;
        }

        System.out.println("\n--- CAST YOUR VOTE ---");
        displayCandidates(false);

        int candidateId = readInt("Enter Candidate ID to vote for: ");

        // Validate candidate ID
        Candidate chosen = candidateDAO.getCandidateById(candidateId);
        if (chosen == null) {
            System.out.println("  [!] Invalid Candidate ID.");
            return;
        }

        System.out.println("  You are voting for: " + chosen.getName() + " (" + chosen.getParty() + ")");
        String confirm = readString("Confirm vote? (yes/no): ");

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("  [!] Vote cancelled.");
            return;
        }

        // Cast vote using a full DB transaction
        Vote vote = new Vote(user.getUserId(), candidateId);
        boolean success = voteDAO.castVote(vote);

        if (success) {
            user.setHasVoted(true);  // update in-memory user state
            System.out.println("  [✓] Your vote has been recorded successfully!");
        } else {
            System.out.println("  [✗] Vote failed due to a database error. Please try again.");
        }
    }

    // ================================================================
    // ADMIN LOGIN & MENU
    // ================================================================

    private static void handleAdminLogin() {
        System.out.println("\n--- ADMIN LOGIN ---");
        String username = readString("Admin Username: ");
        String password = readString("Admin Password: ");

        // Simple hardcoded admin check (credentials in DBConnection)
        if (!username.equals(DBConnection.ADMIN_USERNAME) ||
            !password.equals(DBConnection.ADMIN_PASSWORD)) {
            System.out.println("  [!] Invalid admin credentials.");
            return;
        }

        System.out.println("  [✓] Admin login successful!");
        adminMenu();
    }

    private static void adminMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n══════════════ ADMIN MENU ══════════════");
            System.out.println("  1. Add Candidate");
            System.out.println("  2. View All Candidates");
            System.out.println("  3. Delete Candidate");
            System.out.println("  4. View Election Results");
            System.out.println("  5. Logout");
            System.out.println("════════════════════════════════════════");

            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> handleAddCandidate();
                case 2 -> displayCandidates(true);
                case 3 -> handleDeleteCandidate();
                case 4 -> displayResults();
                case 5 -> {
                    loggedIn = false;
                    System.out.println("  [✓] Admin logged out.");
                }
                default -> System.out.println("  [!] Invalid choice.");
            }
        }
    }

    // ================================================================
    // CANDIDATE MANAGEMENT (Admin)
    // ================================================================

    private static void handleAddCandidate() {
        System.out.println("\n--- ADD CANDIDATE ---");
        String name  = readString("Candidate Name  : ");
        String party = readString("Party Name      : ");

        Candidate candidate = new Candidate(name, party);
        if (candidateDAO.addCandidate(candidate)) {
            System.out.println("  [✓] Candidate '" + name + "' added successfully.");
        } else {
            System.out.println("  [✗] Failed to add candidate.");
        }
    }

    private static void handleDeleteCandidate() {
        System.out.println("\n--- DELETE CANDIDATE ---");
        displayCandidates(true);

        int candidateId = readInt("Enter Candidate ID to delete: ");

        // Verify candidate exists before deleting
        Candidate candidate = candidateDAO.getCandidateById(candidateId);
        if (candidate == null) {
            System.out.println("  [!] No candidate found with ID " + candidateId);
            return;
        }

        System.out.println("  Are you sure you want to delete '" + candidate.getName() + "'?");
        String confirm = readString("  Type 'yes' to confirm: ");

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("  [!] Deletion cancelled.");
            return;
        }

        if (candidateDAO.deleteCandidate(candidateId)) {
            System.out.println("  [✓] Candidate deleted successfully.");
        } else {
            System.out.println("  [✗] Deletion failed. (Tip: Remove associated votes first)");
        }
    }

    // ================================================================
    // DISPLAY CANDIDATES
    // ================================================================

    /**
     * Displays the list of candidates.
     * @param showVotes if true, also shows vote counts (for admin / results)
     */
    private static void displayCandidates(boolean showVotes) {
        List<Candidate> candidates = candidateDAO.getAllCandidates();

        if (candidates.isEmpty()) {
            System.out.println("  [!] No candidates available.");
            return;
        }

        System.out.println("\n┌──────┬──────────────────────────┬──────────────────────" +
                (showVotes ? "┬───────────┐" : "┐"));
        System.out.printf("│ %-4s │ %-24s │ %-20s" +
                (showVotes ? " │ %-9s │%n" : " │%n"), "ID", "Name", "Party", "Votes");
        System.out.println("├──────┼──────────────────────────┼──────────────────────" +
                (showVotes ? "┼───────────┤" : "┤"));

        for (Candidate c : candidates) {
            if (showVotes) {
                System.out.printf("│ %-4d │ %-24s │ %-20s │ %-9d │%n",
                        c.getCandidateId(), c.getName(), c.getParty(), c.getVoteCount());
            } else {
                System.out.printf("│ %-4d │ %-24s │ %-20s │%n",
                        c.getCandidateId(), c.getName(), c.getParty());
            }
        }

        System.out.println("└──────┴──────────────────────────┴──────────────────────" +
                (showVotes ? "┴───────────┘" : "┘"));
    }

    // ================================================================
    // RESULT ANALYTICS
    // ================================================================

    private static void displayResults() {
        List<Candidate> candidates = candidateDAO.getAllCandidates();
        int totalVotes = candidateDAO.getTotalVotes();

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║           ELECTION RESULTS & ANALYTICS          ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        if (candidates.isEmpty() || totalVotes == 0) {
            System.out.println("  No votes have been cast yet.");
            return;
        }

        System.out.printf("%n  %-4s  %-22s  %-18s  %6s  %s%n",
                "Rank", "Candidate", "Party", "Votes", "Percentage / Bar");
        System.out.println("  " + "─".repeat(75));

        // Candidates are already sorted by vote_count DESC from the DAO
        int rank = 1;
        Candidate winner = candidates.get(0);

        for (Candidate c : candidates) {
            double pct = (totalVotes > 0) ? (c.getVoteCount() * 100.0 / totalVotes) : 0;
            String bar = buildBar(pct, 20);

            System.out.printf("  %-4d  %-22s  %-18s  %6d  %5.1f%% %s%n",
                    rank++, c.getName(), c.getParty(), c.getVoteCount(), pct, bar);
        }

        System.out.println("  " + "─".repeat(75));
        System.out.printf("  Total votes cast: %d%n%n", totalVotes);

        // ---- Winner announcement ----
        System.out.println("  ★ WINNER: " + winner.getName()
                + " (" + winner.getParty() + ")"
                + " with " + winner.getVoteCount() + " votes"
                + String.format(" (%.1f%%)", (winner.getVoteCount() * 100.0 / totalVotes)));
        System.out.println();
    }

    /**
     * Builds a simple ASCII progress bar proportional to the percentage.
     * @param pct       percentage value (0–100)
     * @param maxWidth  total bar width in characters
     */
    private static String buildBar(double pct, int maxWidth) {
        int filled = (int) Math.round(pct / 100.0 * maxWidth);
        return "[" + "█".repeat(filled) + "░".repeat(maxWidth - filled) + "]";
    }

    // ================================================================
    // INPUT HELPERS
    // ================================================================

    /** Reads a trimmed non-empty string from stdin */
    private static String readString(String prompt) {
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("  [!] Input cannot be empty.");
            }
        } while (input.isEmpty());
        return input;
    }

    /** Reads a valid integer from stdin, looping on bad input */
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Please enter a valid number.");
            }
        }
    }
}
