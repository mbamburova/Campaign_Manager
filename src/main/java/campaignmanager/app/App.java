package campaignmanager.app;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Anonym on 14. 5. 2016.
 */
public class App extends JFrame {

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
    private JPanel missionPanel;
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
    private JLabel heroFateName;
    private JLabel missionFateName;
    private JLabel fateText;
    private JLabel missionManagText;


    public App() {
        heroCreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


        heroUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });


        heroDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Campaign manager");
        frame.setContentPane(new App().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
