package campaignmanager;

import common.DBUtils;
import common.IllegalEntityException;
import common.ServiceFailureException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Anonym on 14. 3. 2016.
 */
public class CampaignManagerImpl implements CampaignManager {


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
    public void sendHeroToMission(Hero hero, Mission mission) throws ServiceFailureException, IllegalEntityException {
        checkDataSource();
        if(hero == null){
            throw new IllegalArgumentException("hero is null");
        }
        if(hero.getId() == null){
            throw new IllegalArgumentException("hero id is null");
        }
        if(mission == null){
            throw new IllegalArgumentException("mission is null");
        }
        if(mission.getId() == null){
            throw new IllegalArgumentException("mission id is null");
        }

        Connection conn = null;
        PreparedStatement updateSt = null;
        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            checkIfMissionHasSpace(conn, mission);

            updateSt = conn.prepareStatement(
                    "UPDATE Hero SET missionId = ? WHERE id = ? AND missionId IS NULL");
            updateSt.setLong(1, mission.getId());
            updateSt.setLong(2, hero.getId());
            int count = updateSt.executeUpdate();
            if (count == 0) {
                throw new IllegalEntityException("Hero " + hero + " not found or it is already placed in some mission");
            }
            DBUtils.checkUpdatesCount(count, hero, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when putting hero into mission";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, updateSt);
        }

    }

    private static void checkIfMissionHasSpace(Connection conn, Mission mission) throws IllegalEntityException, SQLException {
        PreparedStatement checkSt = null;

        try {
            checkSt = conn.prepareStatement(
                    "SELECT capacity, COUNT(Hero.id) as heroesCount " +
                            "FROM Mission LEFT JOIN Hero ON Mission.id = Hero.missionId " +
                            "WHERE Mission.id = ? " +
                            "GROUP BY Hero.id, capacity");
            checkSt.setLong(1, mission.getId());

            ResultSet rs = checkSt.executeQuery();
            if (rs.next()) {
                if (rs.getInt("capacity") <= rs.getInt("heroesCount")) {
                    throw new IllegalEntityException("Mission " + mission + " is already full");
                }
            } else {
                throw new IllegalEntityException("Mission " + mission + " does not exist in the database");
            }
        } finally {
            DBUtils.closeQuietly(null, checkSt);
        }
    }

    @Override
    public void removeHeroFromMission(Hero hero, Mission mission) {

        checkDataSource();
        if(hero == null){
            throw new IllegalArgumentException("hero is null");
        }
        if(hero.getId() == null){
            throw new IllegalArgumentException("hero id is null");
        }
        if(mission == null){
            throw new IllegalArgumentException("mission is null");
        }
        if(mission.getId() == null){
            throw new IllegalArgumentException("mission id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            // Temporary turn autocommit mode off. It is turned back on in
            // method DBUtils.closeQuietly(...)
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "UPDATE Hero SET missionId = NULL WHERE id = ? AND missionId = ?");
            st.setLong(1, hero.getId());
            st.setLong(2, mission.getId());
            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, hero, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when putting hero into mission";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Hero> findHeroesByMission(Mission mission) throws ServiceFailureException, IllegalEntityException {

        checkDataSource();
        if(mission == null){
            throw new IllegalArgumentException("mission is null");
        }
        if(mission.getId() == null){
            throw new IllegalArgumentException("mission id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT Hero.id, hero_name, hero_level " +
                            "FROM Hero JOIN Mission ON missin.id = Hero.missionId " +
                            "WHERE Mission.id = ?");

            st.setLong(1, mission.getId());
            return HeroManagerImpl.executeQueryForMultipleHeroes(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find bodies in grave " + mission;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public Mission findMissionByHero(Hero hero) throws ServiceFailureException, IllegalEntityException {

        checkDataSource();
        if(hero == null){
            throw new IllegalArgumentException("hero is null");
        }
        if(hero.getId() == null){
            throw new IllegalArgumentException("hero id is null");
        }

        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT mission.id, level_required, capacity, available " +
                            "FROM Mission JOIN Hero ON Mission.id = Hero.missionId " +
                            "WHERE Hero.id = ?");
            st.setLong(1, hero.getId());
            return MissionManagerImpl.executeQueryForSingleMission(st);
        } catch (SQLException ex) {
            String msg = "Error when trying to find mission with hero " + hero;
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }

    }
}

