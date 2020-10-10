package utilities.undoredo;

/**
 * Action interface that tells what needs to be done on execute and undo
 * Each feature that needs to supported by undo-redo feature, must implement this interface.
 * Referenced from Prof. Charles Theva's (RMIT University) Lecture Notes
 */
public interface Action {

    // Performs normal operation
    void execute();

    // Performs reversed operation, corrections you may say
    void undo();

    // Whatever has been undone, can be redone
    void redo();
}