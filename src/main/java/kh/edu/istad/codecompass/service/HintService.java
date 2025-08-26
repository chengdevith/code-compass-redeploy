package kh.edu.istad.codecompass.service;

public interface HintService {

    /**
     * Unlocks a hint for a specific problem for a user.
     * <p>
     * This method allows users to spend their earned coins to reveal a hint for a problem.
     * It deducts the cost of the hint from the user's balance and then grants them access to the hint content.
     *
     * @param id The unique identifier of the hint to be unlocked.
     * @param username The unique username of the user requesting to unlock the hint.
     * @return {@code true} if the hint was successfully unlocked; {@code false} if the user does not have enough coins or the hint is already unlocked.
     */
    Boolean unlockHint(long id, String username);

}
