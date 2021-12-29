package texteditor2;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

public class TextEditor2 extends JFrame implements ActionListener {
    // Size of the editing area

    private static int EDIT_ROWS = 20;
    private static int EDIT_COLS = 40;

    // Size of search and replace fields
    private static int NUM_CHARS = 15;

    // The area where the user does the editing
    public JTextArea editor;

    // Swing components to load and save files
    private JButton loadButton;
    private JButton saveButton;
    private JFileChooser chooser = new JFileChooser();

    // The Swing components dealing with searching
    private JButton searchButton;
    private JTextField searchField;
    private JCheckBox searchCaseSensitiveBox;
    private JCheckBox reverseSearchBox;

    // The Swing components dealing with replace
    private JButton replaceAllButton;
    private JButton replaceSelectionButton;
    private JTextField replaceField;

    // Create components dealing with undo redo
    private UndoManager undoManager;
    private JButton undoButton;
    private JButton redoButton;

    //close
    private JButton closeButton;

    //single transposition
    private JButton singleTransposition;
    private JButton arama_sayısı;
    private JFrame frame = new JFrame();

    //command pattern
    private JButton commandUndo;
    private JButton cut;
    private JButton paste;

    public String clipboard;
    private CommandHistory history = new CommandHistory();

    /* 
	 * Creates the user interface for the editor.
     */
    public TextEditor2() {
        super("Undo/Redo Demo");//şüpheli
        // Create the editing area.  Make it scrollable.  Give it a border so the user knows what it is for.
        editor = new JTextArea(getEDIT_ROWS(), getEDIT_COLS());
        JScrollPane editorScroller = new JScrollPane(getEditor());
        editorScroller.setBorder(BorderFactory.createTitledBorder("Editing area"));

        undoManager = new UndoManager();
        Container contentPane = frame.getContentPane();
        contentPane.add(editorScroller, BorderLayout.CENTER);

        // Create the components dealing with search
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        searchField = new JTextField(getNUM_CHARS());
        searchCaseSensitiveBox = new JCheckBox("Case sensitive", true);
        reverseSearchBox = new JCheckBox("Reverse search (buna tıklamak zorunludur)", false);
        JPanel searchPanel = new JPanel();
        searchPanel.add(searchButton);
        searchPanel.add(searchField);
        searchPanel.add(searchCaseSensitiveBox);
        searchPanel.add(reverseSearchBox);

        // Create the components dealing with replace
        replaceAllButton = new JButton("Replace all");
        replaceAllButton.addActionListener(this);
        replaceSelectionButton = new JButton("Replace selection");
        replaceSelectionButton.addActionListener(this);
        replaceField = new JTextField(getNUM_CHARS());
        JPanel replacePanel = new JPanel();
        replacePanel.add(replaceAllButton);
        replacePanel.add(replaceSelectionButton);
        replacePanel.add(replaceField);

        // Create the components to deal with files
        loadButton = new JButton("Load file");
        loadButton.addActionListener(this);
        saveButton = new JButton("Save file");
        saveButton.addActionListener(this);
        JPanel filePanel = new JPanel();
        filePanel.add(loadButton);
        filePanel.add(saveButton);

        //Create the component for close
        closeButton = new JButton("close");
        closeButton.addActionListener(this);
        JPanel closePanel = new JPanel();
        closePanel.add(closeButton);

        //single ın kısmı
        singleTransposition = new JButton("Single transposition");
        singleTransposition.addActionListener(this);
        JPanel singlePanel = new JPanel();
        singlePanel.add(singleTransposition);

        arama_sayısı = new JButton("Arama Sayısı");
        arama_sayısı.addActionListener(this);
        JPanel arama_panel = new JPanel();
        arama_panel.add(arama_sayısı);

        // Create the components to deal with undo, redo
        undoButton = new JButton("Undo");

        undoButton.setEnabled(false);
        redoButton = new JButton("Redo");
        redoButton.setEnabled(false);
        JPanel undoPanel = new JPanel();
        undoPanel.add(undoButton);
        undoPanel.add(redoButton);

        //boş kısım
        commandUndo = new JButton("Command Undo");
        commandUndo.addActionListener(this);
        JPanel commandUndoPanel = new JPanel();
        commandUndoPanel.add(commandUndo);
        cut = new JButton("Cut");
        cut.addActionListener(this);
        JPanel cutPanel = new JPanel();
        cutPanel.add(cut);
        paste = new JButton("Paste");
        paste.addActionListener(this);
        JPanel pastePanel = new JPanel();
        pastePanel.add(paste);

        editor.getDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e) {
                getUndoManager().addEdit(e.getEdit());
                updateButtons();
            }
        });

        undoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    getUndoManager().undo();
                } catch (CannotRedoException cre) {
                    cre.printStackTrace();
                }
                updateButtons();
            }
        });

        redoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    getUndoManager().redo();
                } catch (CannotRedoException cre) {
                    cre.printStackTrace();
                }
                updateButtons();
            }
        });
        commandUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });

        TextEditor2 editor = this;
        cut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCommand(new CutCommand(editor));
            }
        });
        paste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCommand(new PasteCommand(editor));
            }
        });
        //Put undo redo on top
        contentPane.add(undoPanel, BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);

        // Put the rest of the buttons at the bottom of the window
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1));
        buttonPanel.add(commandUndoPanel);
        buttonPanel.add(cutPanel);
        buttonPanel.add(pastePanel);
        buttonPanel.add(searchPanel);
        buttonPanel.add(replacePanel);        
        buttonPanel.add(singlePanel);
        buttonPanel.add(arama_panel);
        buttonPanel.add(filePanel);
        buttonPanel.add(closePanel);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }

    /* 
	 * Handle the button clicks.
     */
    public void actionPerformed(ActionEvent event) {
        JButton clickedButton = (JButton) event.getSource();
        if (clickedButton == getSearchButton()) {
            search();
        } else if (clickedButton == getReplaceAllButton()) {
            replaceAll(getSearchField().getText(), getReplaceField().getText());
        } else if (clickedButton == getReplaceSelectionButton()) {
            replaceSelection();
        } else if (clickedButton == getSaveButton()) {
            saveToFile();
        } else if (clickedButton == getLoadButton()) {
            loadFromFile();
        } else if (clickedButton == getCloseButton()) {
            close();
        } else if (clickedButton == getSingleTransposition()) {
            singleTransposition();
        } else if (clickedButton == getarama_sayısı()) {
            arama_sayısı();
        }

    }

    /*
	 * Read in the contents of a file and display it in the text area.
     */
    private void loadFromFile() {
        if (getChooser().showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File loadFile = getChooser().getSelectedFile();
            try {
                BufferedReader in = new BufferedReader(new FileReader(loadFile));
                getEditor().setText("");
                String nextLine = in.readLine();
                while (nextLine != null) {
                    getEditor().append(nextLine + "\n");
                    nextLine = in.readLine();
                }
                in.close();
            } catch (IOException e) {
                // Happens if the file cannot be read for any reason, such as:
                //    - the user might not have read permission on the file
                JOptionPane.showMessageDialog(null, "Could not load the file " + e.getMessage());
            }
        }

    }

    /*
	 * Save the contents of the editing area to a the file named "file.txt".
     */
    private void saveToFile() {
        if (getChooser().showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File saveFile = getChooser().getSelectedFile();
            try {
                PrintWriter out = new PrintWriter(new FileWriter(saveFile));
                out.print(getEditor().getText());
                out.close();
            } catch (IOException e) {
                // Happens if the file cannot be written to for any reason, such as:
                //    - the file might already exist
                //    - the disk might be full
                //    - the user might not have write permission in the directory
                JOptionPane.showMessageDialog(null, "Could not save the file " + e.getMessage());
            }
        }
    }

    /*
	 * Replace all occurrences of a string with another string.  Does nothing if oldString is empty.
     */
    private void replaceAll(String oldString, String newString) {
        if (!oldString.equals("")) {
            String editorText = getEditor().getText();
            editorText = editorText.replaceAll(oldString, newString);
            getEditor().setText(editorText);
        }
    }

    /*
	 * Replace the selected text in the editing area with the text the user entered in the replace field.
	 * If the replace field is empty, this will delete the selected text.
     */
    private void replaceSelection() {
        // Get the text out of the editing area
        String editorText = getEditor().getText();

        // Find the substrings that appear before and after the selection
        String start = editorText.substring(0, getEditor().getSelectionStart());
        String end = editorText.substring(getEditor().getSelectionEnd());

        // Replace the selected portion with the contents of the replace field
        editorText = start + getReplaceField().getText() + end;
        getEditor().setText(editorText);
    }

    /*
	 * Find the next occurrence of the string entered in the search field.  The setting of the "case sensitive"
	 * box will determine if the search requires the same capitalization or not.  The setting of the "reverse"
	 * box will determine if the search proceeds forward from the current caret position or backwards.  If
	 * there are no more occurences of the search string, nothing happens.
     */
    public int[] tekrar_sayısı = new int[87315];
    public int z;
    public String x;
    public int c;

    private void search() {
        String str = "";

        String[] veri = new String[87315];

        try {
            FileInputStream fStream = new FileInputStream("words.txt");
            DataInputStream dStream = new DataInputStream(fStream);
            BufferedReader bReader = new BufferedReader(new InputStreamReader(dStream));
            int i = 0;
            ///words txt deki kelimeleri veri dizisine atama
            while ((str = bReader.readLine()) != null) {
                veri[i] = str;
                i++;
            }
            dStream.close();
        } catch (IOException e) {
            System.err.println("Hata: " + e.getMessage());
        }
        // Get the string the user wants to search for
        String data = getEditor().getText();
        /// . , : vs vs çıkarma
        String dataArray[] = data.split("[, .;:]");
        ///inputtaki kelimeler
        for (int i = 0; i < dataArray.length; i++) {
            String s = dataArray[i];
            char[] harfler = new char[s.length()];
            ///txt de kelimeleri arıyor
            for (int j = 0; j < veri.length; j++) {
                ///kelime varsa
                if (dataArray[i].equals(veri[j])) {
                    JOptionPane.showMessageDialog(null, dataArray[i] + " kelimesi sözlükte mevcuttur.");
                    tekrar_sayısı[j] = tekrar_sayısı[j] + 1;
                    z = tekrar_sayısı[j];
                    x = veri[j];

                }
            }
        }
    }

    ///observer pattern interface
    public interface Observer {

        public void notify(String message);
    }

    ///kullanıcı temsili interface
    public interface Observable {

        void addobservable(Observer observer);

        void notifyobservable();
    }

    ///observable interface'i genişletildi
    public class notify_observer implements Observable {

        private List<Observer> observerList = new ArrayList<>();
        private String message = " KELİMESİ DAHA ÖNCE BU KADAR ARATILDI: ";

        @Override
        public void addobservable(Observer observer) {
            observerList.add(observer);
        }

        @Override
        public void notifyobservable() {
            for (Observer observer : observerList) {
                observer.notify(message);
            }
        }
    }

    ///proxy pattern interface
    public interface Idictionary {

        public void single_transpotision();
    }

    ///gerçek nesne
    class dictionary implements Idictionary {

        @Override
        public void single_transpotision() {

            String singleText = getEditor().getText();
            String str = "";
            char temp1, temp2;
            int counter = 0;
            int counter2 = 0;

            String[] veri = new String[87315];

            try {
                FileInputStream fStream = new FileInputStream("words.txt");
                DataInputStream dStream = new DataInputStream(fStream);
                BufferedReader bReader = new BufferedReader(new InputStreamReader(dStream));
                int i = 0;
                ///words txt deki kelimeleri veri dizisine atama
                while ((str = bReader.readLine()) != null) {
                    veri[i] = str;
                    i++;
                }
                dStream.close();
            } catch (IOException e) {
                System.err.println("Hata: " + e.getMessage());
            }
            ///input giriş
            Scanner scanner = new Scanner(System.in);
            String data = getEditor().getText();
            /// . , : vs vs çıkarma
            String dataArray[] = data.split("[, .;:]");
            ///inputtaki kelimeler
            for (int i = 0; i < dataArray.length; i++) {
                String s = dataArray[i];
                char[] harfler = new char[s.length()];
                ///txt de kelimeleri arıyor
                for (int j = 0; j < veri.length; j++) {
                    ///kelime varsa
                    if (dataArray[i].equals(veri[j])) {
                        JOptionPane.showMessageDialog(null, dataArray[i] + " kelimesi sözlükte mevcuttur.");
                        tekrar_sayısı[j] = tekrar_sayısı[j] + 1;
                        z = tekrar_sayısı[j];

                        x = veri[j];
                        counter++;
                    }
                    ///kelime yok ise
                    if (!dataArray[i].equals(veri[j])) {
                        for (int k = 0; k < s.length(); k++) {
                            temp1 = s.charAt(k);
                            harfler[k] = temp1;
                        }
                        ///harfleri değiştiriyor
                        for (int n = 0; n < s.length(); n++) {
                            for (int m = 1; m < s.length(); m++) {
                                temp2 = harfler[n];
                                harfler[n] = harfler[m];
                                harfler[m] = temp2;
                                String kelime = new String(harfler);
                                ///kelimeyi tekrar kıyaslıyor
                                if (kelime.equals(veri[j])) {
                                    tekrar_sayısı[j] = tekrar_sayısı[j] + 1;
                                    z = tekrar_sayısı[j];
                                    JOptionPane.showMessageDialog(null, dataArray[i] + " kelimesi " + kelime + " kelimesi olarak sözlükte vardır. ");
                                    x = kelime;
                                    counter++;
                                    break;
                                }
                                ///kelimeyi eski haline getiriyor
                                temp2 = harfler[n];
                                harfler[n] = harfler[m];
                                harfler[m] = temp2;
                            }
                            if (counter > counter2) {
                                break;
                            }
                        }
                        if (counter > counter2) {
                            counter2++;
                        }
                    }
                }
            }
        }
    }
    public class txtFileIterator implements Iterator<String> {

    List<String> words;
    int wordCount;

    public txtFileIterator(List<String> words) {
        this.words = words;
        this.wordCount = words.size();
    }

    @Override
    public boolean hasNext() {
        if (wordCount == 0) {
            return false;
        }
        return true;
    }

    @Override
    public String next() {
        if (wordCount == 0) {
            return null;
        }
        String word = words.get(words.size() - wordCount);
        wordCount--;
        return word;
    }

}
    public interface FileInterface {

    public Iterator<String> getIterator();
}

/*
txtFile class
*/


public class txtFile implements FileInterface {

        private File file;
        private int linesCount = 0;
        List<String> wordList;

        public txtFile(File file) throws Exception {
            boolean isTxtFile = checkFileExtension(file);
            if (isTxtFile) {
                this.file = file;
                wordList = new ArrayList<>();
                loadContentsIntoList();
            } else {
                this.file = null;
            }
        }

        private boolean checkFileExtension(File file) {
            String fileName = file.getName();
            int length = fileName.length();
            int startIndex = length - 4;
            String extension = fileName.substring(startIndex);
            if (extension.equals(".txt")) {
                return true;
            } else {
                return false;
            }
        }

        private void loadContentsIntoList() throws FileNotFoundException, IOException {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                linesCount++;
                String[] words = line.split(" ");
                for(int i=0; i<words.length; i++){
                    wordList.add(words[i]);
                }              
            }
        }

        @Override
        public Iterator<String> getIterator() {
            return new txtFileIterator(wordList);
        }

        public String getFileName() {
            String fileName = this.file.getName();
            if(!fileName.equals(""))
                return fileName;
            return null;
        }

        public String getSize() {
            long size = file.length();
            double sizeToKb = (double) size/1024;
            String s = sizeToKb+" Kb";
            return s;
        }
      
        public int getNumberOfCharacters(){
            int count = 0;
            Iterator<String> iterator = getIterator();
            while(iterator.hasNext()){
                String word = iterator.next();
                count += word.length();
            }
            return count;
        }
      
        public int getNumberOflines(){
            return linesCount;
        }
    }
public txtFile fileOpened;

    public void openFile() {
        try {
            JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
            int ret = chooser.showOpenDialog(null);
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                System.out.println("Opening: " + file.getName() + ".");
                fileOpened = new txtFile(file);
                //This is where a real application would open the file.
            } else {
                System.out.println("Open command cancelled by user.");
            }
        } catch (Exception ex) {
            System.out.println("Failed to open the file");
            ex.printStackTrace();
        }

    }
    void printWordByWord() {
        Iterator<String> iterator = fileOpened.getIterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    ///user class
    public class kullanıcı implements Observer {

        private Observable observable;

        @Override
        public void notify(String message) {
            JOptionPane.showMessageDialog(null, " ARADIĞINIZ " + x + message + z);
        }
    }

    ///proxy object
    class proxyobject implements Idictionary {

        private dictionary sözlük = null;

        @Override
        public void single_transpotision() {
            sözlük = new dictionary();
            sözlük.single_transpotision();

        }

        public void arama_sayısı() {
            kullanıcı user = new kullanıcı();
            notify_observer notify_obs = new notify_observer();
            notify_obs.addobservable(user);
            String str = "";
            char temp1, temp2;
            int counter = 0;
            int counter2 = 0;

            String[] veri = new String[87315];

            try {
                FileInputStream fStream = new FileInputStream("words.txt");
                DataInputStream dStream = new DataInputStream(fStream);
                BufferedReader bReader = new BufferedReader(new InputStreamReader(dStream));
                int i = 0;
                ///words txt deki kelimeleri veri dizisine atama
                while ((str = bReader.readLine()) != null) {
                    veri[i] = str;
                    i++;
                }
                dStream.close();
            } catch (IOException e) {
                System.err.println("Hata: " + e.getMessage());
            }
            ///input giriş
            Scanner scanner = new Scanner(System.in);
            String data = getEditor().getText();
            /// . , : vs vs çıkarma
            String dataArray[] = data.split("[, .;:]");
            ///inputtaki kelimeler
            for (int i = 0; i < dataArray.length; i++) {
                String s = dataArray[i];
                char[] harfler = new char[s.length()];
                ///txt de kelimeleri arıyor
                for (int j = 0; j < veri.length; j++) {
                    ///kelime varsa
                    if (dataArray[i].equals(veri[j])) {
                        z = tekrar_sayısı[j];
                        x = veri[j];
                        counter++;

                        notify_obs.notifyobservable();
                    }
                    ///kelime yok ise
                    if (!dataArray[i].equals(veri[j])) {
                        for (int k = 0; k < s.length(); k++) {
                            temp1 = s.charAt(k);
                            harfler[k] = temp1;
                        }
                        ///harfleri değiştiriyor
                        for (int n = 0; n < s.length(); n++) {
                            for (int m = 1; m < s.length(); m++) {
                                temp2 = harfler[n];
                                harfler[n] = harfler[m];
                                harfler[m] = temp2;
                                String kelime = new String(harfler);
                                ///kelimeyi tekrar kıyaslıyor
                                if (kelime.equals(veri[j])) {
                                    z = tekrar_sayısı[j];
                                    x = veri[j];
                                    counter++;

                                    notify_obs.notifyobservable();

                                    break;
                                }
                                ///kelimeyi eski haline getiriyor
                                temp2 = harfler[n];
                                harfler[n] = harfler[m];
                                harfler[m] = temp2;
                            }
                            if (counter > counter2) {
                                break;
                            }
                        }
                        if (counter > counter2) {
                            counter2++;
                        }
                    }
                }
            }

        }
    }

    private void singleTransposition() {

        proxyobject sözlük_proxy = new proxyobject();
        sözlük_proxy.single_transpotision();

    }

    private void arama_sayısı() {
        proxyobject sözlük_proxy = new proxyobject();
        sözlük_proxy.arama_sayısı();
    }

    private void close() {
        getFrame().setVisible(false);
    }

    private void updateButtons() {
        getUndoButton().setText(getUndoManager().getUndoPresentationName());
        getRedoButton().setText(getUndoManager().getRedoPresentationName());
        getUndoButton().setEnabled(getUndoManager().canUndo());
        getRedoButton().setEnabled(getUndoManager().canRedo());
    }

    private void executeCommand(Command command) {// Execute a command and check whether it has to be added to
        // the history.

        if (command.execute()) {
            history.push(command);
        }
    }

    private void undo() {
        // Take the most recent command from the history and run its
        // undo method. Note that we don't know the class of that
        // command. But we don't have to, since the command knows
        // how to undo its own action.

        if (history.isEmpty()) {
            return;
        }
        Command command = history.pop();
        if (command != null) {
            command.undo();
        }
    }

    /**
     * @return the EDIT_ROWS
     */
    public static int getEDIT_ROWS() {
        return EDIT_ROWS;
    }

    /**
     * @param aEDIT_ROWS the EDIT_ROWS to set
     */
    public static void setEDIT_ROWS(int aEDIT_ROWS) {
        EDIT_ROWS = aEDIT_ROWS;
    }

    /**
     * @return the EDIT_COLS
     */
    public static int getEDIT_COLS() {
        return EDIT_COLS;
    }

    /**
     * @param aEDIT_COLS the EDIT_COLS to set
     */
    public static void setEDIT_COLS(int aEDIT_COLS) {
        EDIT_COLS = aEDIT_COLS;
    }

    /**
     * @return the NUM_CHARS
     */
    public static int getNUM_CHARS() {
        return NUM_CHARS;
    }

    /**
     * @param aNUM_CHARS the NUM_CHARS to set
     */
    public static void setNUM_CHARS(int aNUM_CHARS) {
        NUM_CHARS = aNUM_CHARS;
    }

    /**
     * @return the editor
     */
    public JTextArea getEditor() {
        return editor;
    }

    /**
     * @param editor the editor to set
     */
    public void setEditor(JTextArea editor) {
        this.editor = editor;
    }

    /**
     * @return the loadButton
     */
    public JButton getLoadButton() {
        return loadButton;
    }

    /**
     * @param loadButton the loadButton to set
     */
    public void setLoadButton(JButton loadButton) {
        this.loadButton = loadButton;
    }

    /**
     * @return the saveButton
     */
    public JButton getSaveButton() {
        return saveButton;
    }

    /**
     * @param saveButton the saveButton to set
     */
    public void setSaveButton(JButton saveButton) {
        this.saveButton = saveButton;
    }

    /**
     * @return the chooser
     */
    public JFileChooser getChooser() {
        return chooser;
    }

    /**
     * @param chooser the chooser to set
     */
    public void setChooser(JFileChooser chooser) {
        this.chooser = chooser;
    }

    /**
     * @return the searchButton
     */
    public JButton getSearchButton() {
        return searchButton;
    }

    /**
     * @param searchButton the searchButton to set
     */
    public void setSearchButton(JButton searchButton) {
        this.searchButton = searchButton;
    }

    /**
     * @return the searchField
     */
    public JTextField getSearchField() {
        return searchField;
    }

    /**
     * @param searchField the searchField to set
     */
    public void setSearchField(JTextField searchField) {
        this.searchField = searchField;
    }

    /**
     * @return the searchCaseSensitiveBox
     */
    public JCheckBox getSearchCaseSensitiveBox() {
        return searchCaseSensitiveBox;
    }

    /**
     * @param searchCaseSensitiveBox the searchCaseSensitiveBox to set
     */
    public void setSearchCaseSensitiveBox(JCheckBox searchCaseSensitiveBox) {
        this.searchCaseSensitiveBox = searchCaseSensitiveBox;
    }

    /**
     * @return the reverseSearchBox
     */
    public JCheckBox getReverseSearchBox() {
        return reverseSearchBox;
    }

    /**
     * @param reverseSearchBox the reverseSearchBox to set
     */
    public void setReverseSearchBox(JCheckBox reverseSearchBox) {
        this.reverseSearchBox = reverseSearchBox;
    }

    /**
     * @return the replaceAllButton
     */
    public JButton getReplaceAllButton() {
        return replaceAllButton;
    }

    /**
     * @param replaceAllButton the replaceAllButton to set
     */
    public void setReplaceAllButton(JButton replaceAllButton) {
        this.replaceAllButton = replaceAllButton;
    }

    /**
     * @return the replaceSelectionButton
     */
    public JButton getReplaceSelectionButton() {
        return replaceSelectionButton;
    }

    /**
     * @param replaceSelectionButton the replaceSelectionButton to set
     */
    public void setReplaceSelectionButton(JButton replaceSelectionButton) {
        this.replaceSelectionButton = replaceSelectionButton;
    }

    /**
     * @return the replaceField
     */
    public JTextField getReplaceField() {
        return replaceField;
    }

    /**
     * @param replaceField the replaceField to set
     */
    public void setReplaceField(JTextField replaceField) {
        this.replaceField = replaceField;
    }

    /**
     * @return the undoManager
     */
    public UndoManager getUndoManager() {
        return undoManager;
    }

    /**
     * @param undoManager the undoManager to set
     */
    public void setUndoManager(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    /**
     * @return the undoButton
     */
    public JButton getUndoButton() {
        return undoButton;
    }

    /**
     * @param undoButton the undoButton to set
     */
    public void setUndoButton(JButton undoButton) {
        this.undoButton = undoButton;
    }

    /**
     * @return the redoButton
     */
    public JButton getRedoButton() {
        return redoButton;
    }

    /**
     * @param redoButton the redoButton to set
     */
    public void setRedoButton(JButton redoButton) {
        this.redoButton = redoButton;
    }

    /**
     * @return the closeButton
     */
    public JButton getCloseButton() {
        return closeButton;
    }

    /**
     * @param closeButton the closeButton to set
     */
    public void setCloseButton(JButton closeButton) {
        this.closeButton = closeButton;
    }

    /**
     * @return the singleTransposition
     */
    public JButton getSingleTransposition() {
        return singleTransposition;
    }

    public JButton getarama_sayısı() {
        return arama_sayısı;
    }

    /**
     * @param singleTransposition the singleTransposition to set
     */
    public void setSingleTransposition(JButton singleTransposition) {
        this.singleTransposition = singleTransposition;
    }

    public void setarama_sayısı(JButton arama_sayısı) {
        this.arama_sayısı = arama_sayısı;
    }

    /**
     * @return the frame
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * @param frame the frame to set
     */
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public static void main(String[] args) {
        new TextEditor2();
    }
}
