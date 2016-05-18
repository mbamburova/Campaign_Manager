package campaignmanager.app;

import campaignmanager.*;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
    private JButton sendButton;
    private JComboBox heroNameComboBox;
    private JComboBox missionNameComboBox;
    private JButton leaveButton;
    private JLabel heroFateName;
    private JLabel missionFateName;
    private JLabel fateText;
    private JLabel missionManagText;
    private HeroManagerImpl heroManager = new HeroManagerImpl();
    private MissionManagerImpl missionManager = new MissionManagerImpl();
    private DataSource dataSource;
    private org.slf4j.Logger log = LoggerFactory.getLogger(App.class);


    public App() {


        dataSource = new CampaignDatabase().setUpDatabase();
        heroManager.setDataSource(dataSource);
        heroTable.setModel(new HeroTableModel(heroManager));
        missionManager.setDataSource(dataSource);
        missionTable.setModel(new MissionTableModel(missionManager));
        missionLevelSpinner.setModel(new SpinnerNumberModel(1, 1, 20, 1));
        missionCapacitySpinner.setModel(new SpinnerNumberModel(1, 1, 12, 1));

        heroCreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hero hero = new Hero();
                hero.setName(heroNameTextField.getText());
                hero.setLevel(Integer.parseInt(heroLevelTextField.getText()));
                HeroTableModel model = (HeroTableModel) heroTable.getModel();
                model.addRow(hero);
            }
        });

        heroUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HeroTableModel model = (HeroTableModel) heroTable.getModel();
                int selectedRow = heroTable.getSelectedRow();
                Hero hero = heroManager.findHeroById(Long.parseLong(model.getValueAt(selectedRow, 0).toString()));
                hero.setName(heroNameTextField.getText());
                hero.setLevel(Integer.parseInt(heroLevelTextField.getText()));
                model.updateRow(hero, selectedRow);
                defaultHeroSettings();
            }
        });

        heroDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                log.info("Deleting hero...");
                HeroTableModel model = (HeroTableModel) heroTable.getModel();
                model.removeRow(heroTable.getSelectedRow());
                defaultHeroSettings();
            }
        });

        heroTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = heroTable.rowAtPoint(e.getPoint());
                HeroTableModel model = (HeroTableModel) heroTable.getModel();
                heroNameTextField.setText(model.getValueAt(selectedRow, 1).toString());
                heroLevelTextField.setText(model.getValueAt(selectedRow, 2).toString());

                heroUpdateButton.setEnabled(true);
                heroDeleteButton.setEnabled(true);
                heroCreateButton.setEnabled(false);

                super.mouseClicked(e);
            }
        });

        missionCreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Mission mission = new Mission();
                mission.setMission_name(missionNameTextField.getText());
                if (missionAvailabilityCheckBox.isSelected()) {
                    mission.setAvailable(true);
                }
                else mission.setAvailable(false);

                mission.setCapacity((Integer)missionCapacitySpinner.getValue());
                mission.setLevelRequired((Integer)missionLevelSpinner.getValue());
                MissionTableModel model = (MissionTableModel) missionTable.getModel();
                model.addRow(mission);
                defaultMissionSettings();
            }
        });

        missionUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MissionTableModel model = (MissionTableModel) missionTable.getModel();
                int selectedRow = missionTable.getSelectedRow();
                Mission mission = missionManager.findMissionById(Long.parseLong(model.getValueAt(selectedRow, 0).toString()));
                mission.setMission_name(missionNameTextField.getText());
                if (missionAvailabilityCheckBox.isSelected()) {
                    mission.setAvailable(true);
                }
                else mission.setAvailable(false);
                mission.setCapacity((Integer)missionCapacitySpinner.getValue());
                mission.setLevelRequired((Integer)missionLevelSpinner.getValue());
                model.updateRow(mission, selectedRow);
                defaultMissionSettings();
            }
        });

        missionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = missionTable.rowAtPoint(e.getPoint());
                MissionTableModel model = (MissionTableModel) missionTable.getModel();
                missionNameTextField.setText(model.getValueAt(selectedRow, 1).toString());
                missionLevelSpinner.setValue(Integer.parseInt(model.getValueAt(selectedRow, 2).toString()));
                missionCapacitySpinner.setValue(Integer.parseInt(model.getValueAt(selectedRow, 3).toString()));

                missionCreateButton.setEnabled(false);
                missionUpdateButton.setEnabled(true);
                super.mouseClicked(e);
            }
        });
    }

    public void defaultHeroSettings() {

        heroCreateButton.setEnabled(true);
        heroUpdateButton.setEnabled(false);
        heroDeleteButton.setEnabled(false);

        heroLevelTextField.setText("");
        heroNameTextField.setText("");
    }

    public void defaultMissionSettings() {
        missionCreateButton.setEnabled(true);
        missionUpdateButton.setEnabled(false);
    }

    public static void main(String[] args) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        JFrame frame = new JFrame("Campaign manager");
        frame.setContentPane(new App().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
