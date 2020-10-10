package utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that keeps tracks of all undo-redo operations in the application
 * Referenced from Prof. Charles Theva's (RMIT University) Lecture Notes
 */
public class CommandManager {

    private static CommandManager commandTracker = null;

    // Top element represents latest operation
    private OpStack<Action> stackNormal;

    // Top element represents latest undone operation - Used for redo
    private OpStack<Action> stackReverse;

    // Stores the actions that have been performed so far
    private List<String> actionHistory;

    // Returns the singleton instance of the class
    public static CommandManager getCommandTracker(){
        if(commandTracker != null)
            return commandTracker;
        return new CommandManager();
    }

    // Constructor - initializes the stacks. The stack is in scope until application is running.
    private CommandManager() {
        stackNormal = new OpStack<>();
        stackReverse = new OpStack<>();
        actionHistory = new ArrayList<>();
    }


    // Executes the feature. Each feature has it's own class
    public void execute(Action action){
        action.execute();
        stackNormal.push(action);
        actionHistory.add("Something happened");
    }


    /**
     *  Executes undone operation
     *  Pops latest operation. executes it. Pushes that on redo stack.
     */
    public void undo() {
        Action a;
        if (stackNormal.size() > 0)
        {   a = stackNormal.pop();
            a.undo();
            stackReverse.push(a);
            actionHistory.add("Something - undo");
        }
    }

    /**
     * Clear normal operation stack
     */
    public void clearNormal() {
        stackNormal.clear();
    }

    /**
     * Clears reverse operation stack
     */
    public void clearReverse() {
        stackReverse.clear();
    }

    /**
     * Returns the action history that was performed during session time
     * @return
     */
    List<String> getActionHistory() {
        return actionHistory;
    }
}
