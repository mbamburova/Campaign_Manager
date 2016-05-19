package campaignmanager.app;

import campaignmanager.*;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.event.*;

/**
 * Created by Michaela Bamburova on 14. 5. 2016.
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
    private JSpinner heroLevelSpinner;
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
    private JButton viewAvailableMissionsButton;
    private JButton viewFreeHeroesButton;
    private JComboBox missionListcomboBox;
    private JButton sendToMissionButton;
    private JButton leaveMissionButton;
    private JComboBox comboBox1;
    private JButton button1;
    private JComboBox comboBox2;
    private JButton button2;
    private JTextField textField1;
    private HeroManagerImpl heroManager = new HeroManagerImpl();
    private CampaignManagerImpl campaignManager = new CampaignManagerImpl();
    private MissionManagerImpl missionManager = new MissionManagerImpl();
    private DataSource dataSource;
    private org.slf4j.Logger log = LoggerFactory.getLogger(App.class);
    private JOptionPane dialog;


    public App() {

        dataSource = new CampaignDatabase().setUpDatabase();
        heroManager.setDataSource(dataSource);
        heroTable.setModel(new HeroTableModel(heroManager, campaignManager));
        missionManager.setDataSource(dataSource);
        missionTable.setModel(new MissionTableModel(missionManager, campaignManager));
        campaignManager.setDataSource(dataSource);
        spinner1.setModel(new SpinnerNumberModel(1, 1, 35, 1));
        heroLevelSpinner.setModel(new SpinnerNumberModel(1, 1, 42, 1));
        missionLevelSpinner.setModel(new SpinnerNumberModel(1, 1, 35, 1));
        missionCapacitySpinner.setModel(new SpinnerNumberModel(1, 1, 20, 1));
        heroNameComboBox.removeAllItems();
        for (Hero hero1: heroManager.findAllHeroes()) {
            heroNameComboBox.addItem(hero1);
        }
        missionNameComboBox.removeAllItems();
        for (Mission missionName : missionManager.findAllMission()) {
            missionNameComboBox.addItem(missionName);
        }

        missionListcomboBox.removeAllItems();
        for (Mission mission1 : missionManager.findAllMission() ){
            missionListcomboBox.addItem(mission1);
        }

        comboBox1.addItem("view all heroes");
        comboBox1.addItem("view free heroes");
        comboBox1.addItem("view heroes by selected level");
        comboBox1.addItem("view heroes by selected name");

        comboBox2.addItem("view all missions");
        comboBox2.addItem("view available missions");
        comboBox2.addItem("view available missions for level");


        heroCreateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hero hero = new Hero();
                if (heroNameTextField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Hero name field is not filled", "Error", JOptionPane.ERROR_MESSAGE);
                }
                hero.setName(heroNameTextField.getText());
                hero.setLevel((Integer)heroLevelSpinner.getValue());
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
                hero.setLevel((Integer)heroLevelSpinner.getValue());
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
                heroLevelSpinner.setValue(Integer.parseInt(model.getValueAt(selectedRow, 2).toString()));

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
                missionListcomboBox.addItem(mission);
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
                missionListcomboBox.setSelectedItem(mission);
                int index = missionListcomboBox.getSelectedIndex();
                missionListcomboBox.removeItem(mission);
                mission.setMission_name(missionNameTextField.getText());
                if (missionAvailabilityCheckBox.isSelected()) {
                    mission.setAvailable(true);
                }
                else mission.setAvailable(false);
                mission.setCapacity((Integer)missionCapacitySpinner.getValue());
                mission.setLevelRequired((Integer)missionLevelSpinner.getValue());
                missionListcomboBox.insertItemAt(mission, index);
                missionListcomboBox.setSelectedIndex(0);

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
                if (model.getValueAt(selectedRow, 4).toString().equals("true")) {
                    missionAvailabilityCheckBox.setSelected(true);
                }
                else missionAvailabilityCheckBox.setSelected(false);

                missionCreateButton.setEnabled(false);
                missionUpdateButton.setEnabled(true);
                super.mouseClicked(e);
            }
        });
        missionListcomboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HeroTableModel model = (HeroTableModel) heroTable.getModel();
                Mission mission = (Mission) missionListcomboBox.getSelectedItem();
                model.filterTable(mission, 4);
            }
        });

        missionNameComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                Mission mission = (Mission) missionListcomboBox.getSelectedItem();

            }
        });
        heroNameComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hero hero = (Hero) heroNameComboBox.getSelectedItem();
                Mission mission = campaignManager.findMissionByHero(hero);
                if (mission == null) {
                    missionNameComboBox.setEnabled(true);
                    sendToMissionButton.setEnabled(true);
                }
                else {
                    missionNameComboBox.setSelectedItem(mission);
                    missionNameComboBox.setEnabled(false);
                    leaveMissionButton.setEnabled(true);
                    sendToMissionButton.setEnabled(false);
                }
            }
        });
        leaveMissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                campaignManager.removeHeroFromMission((Hero) heroNameComboBox.getSelectedItem(), (Mission) missionNameComboBox.getSelectedItem());
                missionNameComboBox.setEnabled(true);
                leaveMissionButton.setEnabled(false);
                sendToMissionButton.setEnabled(true);
            }
        });
        sendToMissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!((Mission) missionNameComboBox.getSelectedItem()).isAvailable()) {
                    JOptionPane.showMessageDialog(dialog, "Bulimission is full", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                campaignManager.sendHeroToMission((Hero)heroNameComboBox.getSelectedItem(), (Mission)missionNameComboBox.getSelectedItem());
                sendToMissionButton.setEnabled(false);
                leaveMissionButton.setEnabled(true);
                missionNameComboBox.setEnabled(false);

            }
        });
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HeroTableModel model = (HeroTableModel) heroTable.getModel();
                if (comboBox1.getSelectedIndex() == 0) {
                    model.refreshTable();
                    textField1.setEnabled(false);
                    button1.setEnabled(false);
                }
                else if (comboBox1.getSelectedIndex() == 1) {
                    model.filterTable(null, 1);
                    textField1.setEnabled(false);
                    button1.setEnabled(false);
                }
                else if (comboBox1.getSelectedIndex() == 2) {
                    textField1.setEnabled(true);
                    button1.setEnabled(true);
                }
                else/* (heroNameComboBox.getSelectedItem().equals(3)) */{
                    textField1.setEnabled(true);
                    button1.setEnabled(true);
                }
            }
        });

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HeroTableModel model = (HeroTableModel) heroTable.getModel();
                Object obj = textField1.getText();
                if(comboBox1.getSelectedIndex() == 2) {
                    model.filterTable(obj, 2);
                }
                else model.filterTable(obj, 3);
            }
        });
        comboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MissionTableModel model = (MissionTableModel) missionTable.getModel();
                if (comboBox2.getSelectedIndex() == 0) {
                    model.refreshTable();
                    spinner1.setEnabled(false);
                    button2.setEnabled(false);
                }
                else if (comboBox2.getSelectedIndex() == 1) {
                    model.filterTable(null, 1);
                    spinner1.setEnabled(false);
                    button2.setEnabled(false);
                }
                else/* (heroNameComboBox.getSelectedItem().equals(3)) */{
                    spinner1.setEnabled(true);
                    button2.setEnabled(true);
                }
            }

        });
        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MissionTableModel model = (MissionTableModel) missionTable.getModel();
                Object obj = spinner1.getValue();
                if (comboBox2.getSelectedIndex() == 2) {
                    model.filterTable(obj, 2);
                }
            }
        });
    }

    public void defaultHeroSettings() {
        heroCreateButton.setEnabled(true);
        heroUpdateButton.setEnabled(false);
        heroDeleteButton.setEnabled(false);

        heroNameTextField.setText("");
        heroLevelSpinner.setValue(1);
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
