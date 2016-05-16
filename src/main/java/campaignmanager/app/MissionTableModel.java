package campaignmanager.app;

import campaignmanager.Mission;
import campaignmanager.MissionManager;
import campaignmanager.MissionManagerImpl;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Michaela Bamburová on 16.05.2016.
 */
public class MissionTableModel extends AbstractTableModel {

    private final MissionManager missionManager;
    private final ResourceBundle bundle;
    private List<Mission> missionList;

    public MissionTableModel() {
        missionManager = new MissionManagerImpl(); //????
        bundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());
        missionList = missionManager.findAllMission();
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
                return bundle.getString("id");
            case 1:
                return bundle.getString("Mission.name");
            case 2:
                return bundle.getString("Mission.capacity");
            case 3:
                return bundle.getString("Mission.levelRequired");
            case 4:
                return bundle.getString("Mission.available");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void update(Mission mission) {
        int i;
        for (i = 0; i < missionList.size() - 1; i++) {
            if (missionList.get(i).getId().equals(mission.getId())) {
                break;
            }
        }
        missionList.set(i, mission);
        fireTableRowsUpdated(i, i);
    }
}
