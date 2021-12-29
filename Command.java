package texteditor2;



public abstract class Command {// The base command class defines the common interface for all
// concrete commands.
    public TextEditor2 editor;
    private String backup;

    Command(TextEditor2 editor) {
        this.editor = editor;
    }

    void backup() {// Make a backup of the editor's state.
        backup = editor.getEditor().getText();
    }

    public void undo() {// Restore the editor's state.
        editor.getEditor().setText(backup);
    }

    public abstract boolean execute();
    // The execution method is declared abstract to force all
    // concrete commands to provide their own implementations.
    // The method must return true or false depending on whether
    // the command changes the editor's state.
}
