package campaignmanager;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Anonym on 14. 5. 2016.
 */
public class MainWindow extends JFrame {

    //this is a change!!!
    private JPanel panel1;
    private JTabbedPane campaignPanel;
    private JTable heroTable;
    private JTextField heroLevelTextField;
    private JTextField heroNameTextField;
    private JButton heroCreateButton;
    private JButton heroUpdateButton;
    private JButton heroDeleteButton;
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
    private JPanel heroPanel;
    private JPanel MissionPanel;
    private JLabel heroTitle;
    private JLabel heroName;
    private JLabel heroLevel;
    private JScrollPane heroTableScrollPane;
    private JLabel heroesOfTheTavernLabel;
    private JScrollPane missionTableScrollPane;
    private JLabel tavernMissionsLabel;
    private JTable missionTable;
    private JLabel levelRequiredLabel;
    private JTextField missionNameTextField;
    private JSpinner missionLevelSpinner;
    private JLabel missionCapacityLabel;
    private JSpinner missionCapacitySpinner;
    private JCheckBox missionAvailabilityCheckBox;
    private JLabel availableLable;
    private JButton missionCreateButton;
    private JButton missionUpdateButton;
    private JLabel missionName;
    private JButton sendToMissionButton;
    private JComboBox heroNameComboBox;
    private JComboBox missionNameComboBox;
    private JButton leaveMissionButton;

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
