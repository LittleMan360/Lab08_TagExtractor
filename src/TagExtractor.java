import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;

// TagExtractor class
public class TagExtractor extends JFrame
{
    // variables
    private final JFileChooser fileChooser;
    private final JTextArea textArea;
    private final Map<String, Integer> tagCount;

    private final Set<String> stopWordsList;


    // constructor
    public TagExtractor()
    {

        // buttons
        JButton openFileBtn = new JButton("Open File");
        JButton displayTagsBtn = new JButton("Display Tags");
        JButton saveTagsBtn = new JButton("Save Tags");
        openFileBtn.addActionListener(e -> openFile());

        // display tags
        displayTagsBtn.addActionListener(e -> displayTags());

        // save tags
        saveTagsBtn.addActionListener(e -> SaveTags());

        // file chooser
        fileChooser = new JFileChooser();
        textArea = new JTextArea();
        tagCount = new HashMap<>();

        // scroll pane
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        //panel layout
        JPanel panel = new JPanel();
        panel.add(openFileBtn);
        panel.add(displayTagsBtn);
        panel.add(saveTagsBtn);

        // content pane
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // frame
        setTitle("Tag Extractor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        // stop words
        stopWordsList = StopWords();
    }

    // open file
    private void openFile() {
        // file chooser
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                // file reader
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                // process line
                while ((line = bufferedReader.readLine()) != null) {
                    processLine(line);
                }
                // close file reader
                fileReader.close();
                // close buffered reader
            } catch (IOException e) {
                e.printStackTrace();
            }
            // set text
            textArea.setText("File loaded: " + file.getName());
        }
    }

    // process line
    private void processLine(String line) {
        String[] words = line.split("[^a-zA-Z0-9]+");
        for (String word : words) {
            word = word.toLowerCase();
            if (word.length() > 0 && !stopWordsList.contains(word)) {
                if (tagCount.containsKey(word)) {
                    tagCount.put(word, tagCount.get(word) + 1);
                } else {
                    tagCount.put(word, 1);
                }
            }
        }
        displayTags();
    }


    // stop words
    private Set<String> StopWords() {
        Set<String> stopWords = new HashSet<>();
        try (Scanner scanner = new Scanner(new File("Stop_Words.txt"))) {
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    // display tags
    private void displayTags() {
        List<Entry<String, Integer>> sortedTags = new ArrayList<>(tagCount.entrySet());
        sortedTags.sort(Entry.comparingByValue());
        Collections.reverse(sortedTags);
        textArea.setText("");
        for (Entry<String, Integer> entry : sortedTags) {
            textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    // save tags
    private void SaveTags() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                for (Entry<String, Integer> entry : tagCount.entrySet()) {
                    bufferedWriter.write(entry.getKey() + ": " + entry.getValue() + "\n");
                }
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            textArea.setText("Tags saved to: " + file.getName());
        }
    }
}