package campaignmanager.app;

import campaignmanager.backend.*;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
    private JComboBox heroNameComboBox;
    private JComboBox missionNameComboBox;
    private JLabel heroFateName;
    private JLabel missionFateName;
    private JLabel fateText;
    private JLabel missionManagText;
    private JButton viewAvailableMissionsButton;
    private JButton viewFreeHeroesButton;
    private JComboBox missionListComboBox;
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
    private org.slf4j.Logger log = LoggerFactory.getLogger(App.class);
    private DataSource dataSource;
    private JOptionPane dialog;

    //TODO: vlozit databazu
    //TODO: opravit bundles + dorobit pre vynimky
    //TODO:(vylepsit update posielania hrdinov na misie)

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
        missionListComboBox.removeAllItems();
        for (Mission mission1 : missionManager.findAllMission() ){
            missionListComboBox.addItem(mission1);
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
                    log.error("Hero name field is not filled");
                    JOptionPane.showMessageDialog(dialog, "Hero name field is not filled", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                hero.setName(heroNameTextField.getText());
                hero.setLevel((Integer)heroLevelSpinner.getValue());
                HeroTableModel model = (HeroTableModel) heroTable.getModel();

                heroNameComboBox.addItem(hero);
                model.addRow(hero);
            }
        });

        heroUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Updating hero...");
                HeroTableModel model = (HeroTableModel) heroTable.getModel();
                if (heroNameTextField.getText().isEmpty()) {
                    log.error("Cannot update hero! Hero name field is not filled.");
                    JOptionPane.showMessageDialog(dialog, "Cannot update hero! Hero name field is not filled.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int selectedRow = heroTable.getSelectedRow();
                Hero hero = heroManager.findHeroById(Long.parseLong(model.getValueAt(selectedRow, 0).toString()));
                hero.setName(heroNameTextField.getText());
                hero.setLevel((Integer)heroLevelSpinner.getValue());

                int index = heroNameComboBox.getSelectedIndex();
                heroNameComboBox.removeItem(hero);

                heroNameComboBox.insertItemAt(hero, index);
                heroNameComboBox.setSelectedIndex(0);

                model.updateRow(hero, selectedRow);
                defaultHeroSettings();
            }
        });

        heroDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Deleting hero...");
                HeroTableModel model = (HeroTableModel) heroTable.getModel();
                int selectedRow = heroTable.getSelectedRow();
                Hero hero = heroManager.findHeroById(Long.parseLong(model.getValueAt(selectedRow, 0).toString()));
                heroNameComboBox.removeItem(hero);
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
                log.info("Creating mission...");
                Mission mission = new Mission();
                if (missionNameTextField.getText().isEmpty()) {
                    log.error("Mission name field is not filled");
                    JOptionPane.showMessageDialog(dialog, "Mission name field is not filled", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                mission.setMission_name(missionNameTextField.getText());
                if (missionAvailabilityCheckBox.isSelected()) {
                    mission.setAvailable(true);
                }
                else mission.setAvailable(false);

                mission.setCapacity((Integer)missionCapacitySpinner.getValue());
                mission.setLevelRequired((Integer)missionLevelSpinner.getValue());
                MissionTableModel model = (MissionTableModel) missionTable.getModel();
                missionListComboBox.addItem(mission);
                missionNameComboBox.addItem(mission);
                model.addRow(mission);
                defaultMissionSettings();
            }
        });

        missionUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Updating mission...");
                MissionTableModel model = (MissionTableModel) missionTable.getModel();

                if (missionNameTextField.getText().isEmpty()) {
                    log.error("Cannot update. Mission name is not filled");
                    JOptionPane.showMessageDialog(dialog, "Cannot update. Mission name is not filled", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int selectedRow = missionTable.getSelectedRow();
                Mission mission = missionManager.findMissionById(Long.parseLong(model.getValueAt(selectedRow, 0).toString()));
                missionListComboBox.setSelectedItem(mission);
                missionNameComboBox.setSelectedItem(mission);

                int index = missionListComboBox.getSelectedIndex();
                missionListComboBox.removeItem(mission);
                mission.setMission_name(missionNameTextField.getText());

                int index2 = missionNameComboBox.getSelectedIndex();
                missionNameComboBox.removeItem(mission);


                if (missionAvailabilityCheckBox.isSelected()) {
                    mission.setAvailable(true);
                }
                else mission.setAvailable(false);
                mission.setCapacity((Integer)missionCapacitySpinner.getValue());
                mission.setLevelRequired((Integer)missionLevelSpinner.getValue());

                missionListComboBox.insertItemAt(mission, index);
                missionListComboBox.setSelectedIndex(0);

                missionNameComboBox.insertItemAt(mission, index2);
                missionNameComboBox.setSelectedIndex(0);

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

        missionListComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HeroTableModel model = (HeroTableModel) heroTable.getModel();
                Mission mission = (Mission) missionListComboBox.getSelectedItem();
                model.filterTable(mission, 4);
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
                log.info("Hero is leaving mission...");
                campaignManager.removeHeroFromMission((Hero) heroNameComboBox.getSelectedItem(), (Mission) missionNameComboBox.getSelectedItem());
                missionNameComboBox.setEnabled(true);
                leaveMissionButton.setEnabled(false);
                sendToMissionButton.setEnabled(true);
            }
        });

        sendToMissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("Hero is sending to mission...");
                if (!((Mission) missionNameComboBox.getSelectedItem()).isAvailable()) {
                    log.error("Mission is full.");
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
                else {
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
                switch (comboBox1.getSelectedIndex()) {
                    case 3:
                            model.filterTable(obj, 3);
                            break;
                    case 2:
                        try {
                            model.filterTable(Integer.parseInt(obj.toString()), 2);
                        } catch (NumberFormatException ex) {
                            log.error("Cannot search!");
                            JOptionPane.showMessageDialog(dialog, "Cannot search! The field must contain a number.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    default:
                        log.error("Cannot search!");
                        JOptionPane.showMessageDialog(dialog, "Cannot search!", "Error", JOptionPane.ERROR_MESSAGE);
                }
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
                else {
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

    private void defaultHeroSettings() {
        heroCreateButton.setEnabled(true);
        heroUpdateButton.setEnabled(false);
        heroDeleteButton.setEnabled(false);

        heroNameTextField.setText("");
        heroLevelSpinner.setValue(1);
    }

    private void defaultMissionSettings() {
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(App.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        BasicConfigurator.configure();

        JFrame frame = new JFrame("Campaign manager");
        frame.setContentPane(new App().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
