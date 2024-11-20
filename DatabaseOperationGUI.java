import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DatabaseOperationGUI extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/db"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "09032006KDob"; 

    private JTextArea resultArea;
    private JTextField columnField;
    private JTextField tableField;
    private Image backgroundImage;  

    public DatabaseOperationGUI() {
        setTitle("RTMS");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\kannan\\Downloads\\gradient-blue-pink-abstract-art-wallpaper-preview"));
        } catch (IOException e) {
            System.out.println("Error loading background image: " + e.getMessage());
        }

        columnField = new JTextField(20);
        tableField = new JTextField(20);
        JButton maxButton = new JButton("Get Max");
        JButton minButton = new JButton("Get Min");
        JButton avgButton = new JButton("Get Avg");
        JButton selectAllButton = new JButton("History");
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setForeground(Color.pink);
        resultArea.setBackground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10));
        inputPanel.add(new JLabel("TEMPERATURE or HUMIDITY:"));
        inputPanel.add(columnField);
        inputPanel.add(new JLabel("PLACE:"));
        inputPanel.add(tableField);
        panel.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(maxButton);
        buttonPanel.add(minButton);
        buttonPanel.add(avgButton);
        buttonPanel.add(selectAllButton);
        panel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(scrollPane, BorderLayout.EAST);

        maxButton.addActionListener(e -> executeQuery("MAX"));
        minButton.addActionListener(e -> executeQuery("MIN"));
        avgButton.addActionListener(e -> executeQuery("AVG"));
        selectAllButton.addActionListener(e -> selectAllRecords());

        add(panel);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); 
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void executeQuery(String operation) {
        String column = columnField.getText().trim();
        String table = tableField.getText().trim();
        resultArea.setText("");

        if (column.isEmpty() || table.isEmpty()) {
            resultArea.setText("Please provide both a column and a table name.");
            return;
        }

        String query = String.format("SELECT %s(%s) FROM %s", operation, column, table);

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            if (rs.next()) {
                resultArea.setText(operation + " of " + column + ": " + rs.getString(1));
            } else {
                resultArea.setText("No results found.");
            }

        } catch (SQLException e) {
            resultArea.setText("Error connecting to database. Please check your connection settings.");
        }
    }

    private void selectAllRecords() {
        String table = tableField.getText().trim();
        resultArea.setText("");

        if (table.isEmpty()) {
            resultArea.setText("Please provide a table name.");
            return;
        }

        String query = String.format("SELECT * FROM %s", table);

        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            StringBuilder results = new StringBuilder();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                results.append(metaData.getColumnName(i)).append("\t");
            }
            results.append("\n");

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    results.append(rs.getString(i)).append("\t");
                }
                results.append("\n");
            }

            if (results.length() == 0) {
                resultArea.setText("No records found.");
            } else {
                resultArea.setText(results.toString());
            }

        } catch (SQLException e) {
            resultArea.setText("SQL Exception: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DatabaseOperationGUI gui = new DatabaseOperationGUI();
            gui.setVisible(true);
        });
    }
}
