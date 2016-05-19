package campaignmanager.app;

import campaignmanager.CampaignManager;
import campaignmanager.MissionManager;
import campaignmanager.Mission;
import common.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Created by Michaela Bamburová on 16.05.2016.
 */
public class MissionTableModel extends AbstractTableModel {

    private final MissionManager missionManager;
    private final CampaignManager campaignManager;
    private final ResourceBundle bundle;
    private List<Mission> missionList = new ArrayList<>();
    private ReadAllSwingWorker readWorker;
    final static Logger log = LoggerFactory.getLogger(MissionTableModel.class);
    private JOptionPane dialog;

    public MissionTableModel(MissionManager missionManager, CampaignManager campaignManager) {
        this.missionManager = missionManager;
        this.campaignManager = campaignManager;
        bundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());
        readWorker = new ReadAllSwingWorker(missionManager);
        readWorker.execute();
    }

    public MissionManager getMissionManager() {
        return missionManager;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public List<Mission> getMissionList() {
        return missionList;
    }

    public void setMissionList(List<Mission> missionList) {
        this.missionList = missionList;
    }

    @Override
    public int getRowCount() {
        return missionList.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Mission mission = missionList.get(rowIndex);
        switch(columnIndex) {
            case 0:
                return mission.getId();
            case 1:
                return mission.getMission_name();
            case 2:
                return mission.getCapacity();
            case 3:
                return mission.getLevelRequired();
            case 4:
                return mission.isAvailable();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int column) {
        switch(column) {
            case 0:
                return bundle.getString("ID");
            case 1:
                return bundle.getString("NAME");
            case 2:
                return bundle.getString("MISSIONCAPACITY");
            case 3:
                return bundle.getString("LEVELREQUIRED");
            case 4:
                return bundle.getString("AVAILABLE");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    private class ReadAllSwingWorker extends SwingWorker<List<Mission>,Void> {
        private final MissionManager missionManager;

        public ReadAllSwingWorker(MissionManager manager) {
            missionManager = manager;
        }

        @Override
        protected List<Mission> doInBackground() throws Exception {
            return missionManager.findAllMission();
        }

        @Override
        protected void done() {
            try {
                missionList = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception: ", e);
            }
        }
    }

    private class FilterSwingWorker extends SwingWorker<List<Mission>, Void> {

        private final MissionManager missionManager;
        private final CampaignManager campaignManager;
        private final Object object;
        private final int filterType;

        public FilterSwingWorker(MissionManager missionManager, CampaignManager campaignManager, Object object, int filterType) {
            this.missionManager = missionManager;
            this.campaignManager = campaignManager;
            this.object = object;
            this.filterType = filterType;
        }

        @Override
        protected List<Mission> doInBackground() throws Exception {
            switch (filterType) {
                case 0:
                    return missionManager.findAllMission();
                case 1:
                    return missionManager.viewAvailableMissions();
                case 2:
                    return missionManager.viewMissionsForLevel((int)object);
                default:
                    return null;
            }
        }

        @Override
        protected void done() {
            try {
                missionList = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception: ", e);
                return;
            }
            log.info("Filtering missions succeed");
        }
    }

    private class AddSwingWorker extends SwingWorker<Void, Void> {

        private final MissionManager missionManager;
        private final Mission mission;

        public AddSwingWorker(MissionManager missionManager, Mission mission) {
            this.missionManager = missionManager;
            this.mission = mission;
        }

        @Override
        protected Void doInBackground() throws ValidationException {
            missionManager.createMission(mission);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                missionList.add(mission);
                int lastRow = missionList.size() - 1;
                fireTableRowsInserted(lastRow, lastRow);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Adding mission failed: " + e.getCause().getMessage());
                JOptionPane.showMessageDialog(dialog, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.info("Mission added successfully.");
        }
    }

    private class UpdateSwingWorker extends SwingWorker <Void, Void> {

        private final MissionManager missionManager;
        private final Mission mission;
        private final int row;

        public UpdateSwingWorker(MissionManager missionManager, Mission mission, int row) {
            this.missionManager = missionManager;
            this.mission = mission;
            this.row = row;
        }

        @Override
        protected Void doInBackground() throws Exception {
            missionManager.updateMission(mission);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                refreshTable();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Updating mission failed: " + e.getCause().getMessage());
                JOptionPane.showMessageDialog(dialog, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.info("Mission updated successfully.");
        }
    }

    public List<Mission> getList() {
        return missionList;
    }

    public void setList(List<Mission> list) {
        missionList = list;
    }

    private AddSwingWorker addWorker;
    private UpdateSwingWorker updateWorker;
    private FilterSwingWorker filterWorker;


    public void addRow(Mission mission) {
        addWorker = new AddSwingWorker(missionManager, mission);
        addWorker.execute();
    }

    public void refreshTable() {
        readWorker = new ReadAllSwingWorker(missionManager);
        readWorker.execute();
    }

    public void updateRow(Mission mission, int row) throws ValidationException {
        updateWorker = new UpdateSwingWorker(missionManager, mission, row);
        updateWorker.execute();
    }

    public void filterTable(Object object, int filterType) {
        filterWorker = new FilterSwingWorker(missionManager, campaignManager, object, filterType);
        filterWorker.execute();
    }
}
