package texteditor2;

import java.util.Stack;

public class CommandHistory {// The global command history is just a stack.
    private Stack<Command> history = new Stack<>();

    public void push(Command c) {  // Last in...
        history.push(c);// Push the command to the end of the history array.
    }

    public Command pop() { // ...first out
        return history.pop();// Get the most recent command from the history.
    }

    public boolean isEmpty() { return history.isEmpty(); }
}