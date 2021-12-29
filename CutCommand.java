package texteditor2;
public class CutCommand extends Command {    // The cut command does change the editor's state, therefore
    // it must be saved to the history. And it'll be saved as
    // long as the method returns true.

    public CutCommand(TextEditor2 editor) {
        super(editor);
    }

    @Override
    public boolean execute() {
        if (editor.editor.getSelectedText().isEmpty()) return false;

        backup();
        String source = editor.editor.getText();
        editor.clipboard = editor.editor.getSelectedText();
        editor.editor.setText(cutString(source));
        return true;
    }

    private String cutString(String source) {
        String start = source.substring(0, editor.editor.getSelectionStart());
        String end = source.substring(editor.editor.getSelectionEnd());
        return start + end;
    }
}
