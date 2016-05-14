package campaignmanager;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Anonym on 14. 5. 2016.
 */
public class MainWindow extends JFrame {
    private JPanel panel1;
    private JTabbedPane tabbedPane1;
    private JTable table1;
    private JTextField textField1;
    private JTextField textField2;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JTable table2;
    private JTextField textField3;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JCheckBox checkBox1;
    private JButton button4;
    private JButton button5;
    private JTextField textField4;
    private JTextField textField5;
    private JButton button6;
    private JButton button7;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> { // zde použito funcionální rozhraní
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setTitle("Campaign Manager");
                    frame.setVisible(true);
                }
        );
    }
}
