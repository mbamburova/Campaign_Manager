package campaignmanager;
import common.DBUtils;
import common.IllegalEntityException;
import common.ServiceFailureException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by Michaela Bamburová on 14.03.2016.
 */
public class MissionManagerImpl implements MissionManager {

    private static final Logger logger = Logger.getLogger(
            MissionManagerImpl.class.getName());

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource is not set");
        }
    }

    @Override
    public void createMission(Mission mission) {
        checkDataSource();
        validate(mission);

        if (mission.getId() != null) {
            throw new IllegalEntityException("mission id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Mission (level_required,capacity,available) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, mission.getLevelRequired());
            st.setInt(2, mission.getCapacity());
            st.setBoolean(3, mission.isAvailable());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, mission, true);

            Long id = DBUtils.getId(st.getGeneratedKeys());
            mission.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting mission into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }


    @Override
    public void updateMission(Mission mission) throws ServiceFailureException {
        checkDataSource();
        validate(mission);

        if (mission.getId() == null) {
            throw new IllegalEntityException("mission id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE Mission SET level_required = ?, capacity = ?, available = ? WHERE id = ?");
            st.setInt(1, mission.getLevelRequired());
            st.setInt(2, mission.getCapacity());
            st.setBoolean(3, mission.isAvailable());
            st.setLong(4, mission.getId());


            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, mission, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating mission in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new IllegalArgumentException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Mission findMissionById(Long id) throws ServiceFailureException {

        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, level_required, capacity, available FROM Mission WHERE id = ?");
            st.setLong(1, id);
            return executeQueryForSingleMission(st);
        } catch (SQLException ex) {
            String msg = "Error when getting mission with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    static Mission executeQueryForSingleMission(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Mission result = rowToMission(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more missions with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public List<Mission> findAllMission() throws ServiceFailureException {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, level_required, capacity, available FROM Mission");
            return executeQueryForMultipleMissions(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all bodies from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    static List<Mission> executeQueryForMultipleMissions(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Mission> result = new ArrayList<Mission>();
        while (rs.next()) {
            result.add(rowToMission(rs));
        }
        return result;
    }

    static private Mission rowToMission(ResultSet rs) throws SQLException {
        Mission result = new Mission();
        result.setId(rs.getLong("id"));
        result.setLevelRequired(rs.getInt("level_required"));
        result.setCapacity(rs.getInt("capacity"));
        result.setAvailable(rs.getBoolean("available"));

        return result;
    }

    private void validate(Mission mission) throws IllegalArgumentException {
        if (mission == null) {
            throw new IllegalArgumentException("mission is null");
        }

        if (mission.getLevelRequired() < 1) {
            throw new IllegalArgumentException("required level is less than 1");
        }

        if(mission.getCapacity() < 1){
            throw new IllegalArgumentException("mission capacity is less than 1");
        }
    }
}
