import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.List;

public class TagExtractor extends JFrame
{
    private JFileChooser fileChooser;
    private JTextArea textArea;
    private Map<String, Integer> tagCount;

    private Set<String> stopWordsList;


    public TagExtractor()
    {


        JButton openFileBtn = new JButton("Open File");
        JButton displayTagsBtn = new JButton("Display Tags");
        JButton saveTagsBtn = new JButton("Save Tags");
        openFileBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        displayTagsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayTags();
            }
        });

        saveTagsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SaveTags();
            }
        });



        fileChooser = new JFileChooser();
        textArea = new JTextArea();
        tagCount = new HashMap<String, Integer>();

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        JPanel panel = new JPanel();
        panel.add(openFileBtn);
        panel.add(displayTagsBtn);
        panel.add(saveTagsBtn);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        setTitle("Tag Extractor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        stopWordsList = StopWords("Stop_Words.txt");
    }

    private void openFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    processLine(line);
                }
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            textArea.setText("File loaded: " + file.getName());
        }
    }

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


    private Set<String> StopWords(String filename) {
        Set<String> stopWords = new HashSet<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    private void displayTags() {
        List<Entry<String, Integer>> sortedTags = new ArrayList<>(tagCount.entrySet());
        sortedTags.sort(Entry.comparingByValue());
        Collections.reverse(sortedTags);
        textArea.setText("");
        for (Entry<String, Integer> entry : sortedTags) {
            textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

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