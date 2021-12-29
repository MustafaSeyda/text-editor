package texteditor2;



public class PasteCommand extends Command {     

    public PasteCommand(TextEditor2 editor) {
        super(editor);
    }

    @Override
    public boolean execute() {
        if (editor.clipboard == null || editor.clipboard.isEmpty()) return false;

        backup();
        editor.editor.insert(editor.clipboard, editor.editor.getCaretPosition());
        return true;
    }
}