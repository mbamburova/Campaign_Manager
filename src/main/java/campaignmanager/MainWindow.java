package campaignmanager;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Michaela Bamburová on 23.04.2016.
 */
public class MainWindow extends javax.swing.JFrame {


    private JPanel Mission;
    private JPanel Hero;
    private JTextField textField1;
    private JCheckBox availableCheckBox;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JButton viewAllButton;
    private JButton viewAvailableButton;
    private JTable MissionTable;
    private JTable HeroTable;
    private JButton deleteButton;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> { // zde použito funcionální rozhraní
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setTitle("Campaign Manager");
                    frame.setVisible(true);
                }
        );

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here


    }
}
