import campaignmanager.Mission;
import campaignmanager.MissionManagerImpl;
import campaignmanager.common.IllegalEntityException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

public class MissionManagerImplTests {

    private MissionManagerImpl manager;
    private DataSource dataSource;


    @Before
    public void setUp() throws SQLException {
        /*
        dataSource = prepareDataSource();
        DBUtils.executeSqlScript(dataSource,MissionManager.class.getResource("src\\main\\resources\\create_table.sql"));
        manager = new MissionManagerImpl();
        manager.setDataSource(dataSource);
        */

        dataSource = prepareDataSource();

        try (Connection connection = dataSource.getConnection()) {
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(new BufferedReader(new FileReader("src\\main\\resources\\create_table.sql")));
        } catch (FileNotFoundException ignored) {
        }
        manager = new MissionManagerImpl();
        manager.setDataSource(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(new BufferedReader(new FileReader("src\\main\\resources\\drop_table.sql")));
        } catch (FileNotFoundException ignored) {
        }
    }

    @Test
    public void createMission() {

        Mission mission = newMission("m 1", 2, 3, true);
        
        manager.createMission(mission);

        Long missionId = mission.getId();
        assertNotNull(missionId);

        Mission result = manager.findMissionById(missionId);

        assertEquals(mission, result);
        assertNotSame(mission, result);
    }

    @Test
    public void findMission() {
        assertNull(manager.findMissionById(1L));

        Mission mission = newMission("m 1", 5, 4, false);
        manager.createMission(mission);
        Long missionId = mission.getId();

        Mission result = manager.findMissionById(missionId);
        assertEquals(mission, result);
    }

    @Test
    public void findAllMissions() {
        assertTrue(manager.findAllMission().isEmpty());

        Mission m1 = newMission("m 1", 5, 4, true);
        Mission m2 = newMission("m 2", 4, 2, false);

        manager.createMission(m1);
        manager.createMission(m2);

        List<Mission> expected = Arrays.asList(m1, m2);
        List<Mission> actual = manager.findAllMission();

        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullMission() {
        manager.createMission(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMissionWithNegativeCapacity() {
        Mission mission = newMission("m 1", 2, -1, true);

        manager.createMission(mission);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMissionWithNegativeLevel() {
        Mission mission = newMission("m 1", -2, 1, true);

        manager.createMission(mission);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMissionWithZeroCapacity() {
        Mission mission = newMission("m 1", 2, 0, true);
        manager.createMission(mission);
    }

    @Test(expected = IllegalEntityException.class)
    public void updateMissionSetIdNegative() {
        Mission mission = newMission("m 1", 2, 4, true);
        manager.createMission(mission);

        Long missionId = mission.getId();
        mission = manager.findMissionById(missionId);

        mission.setId(-1L);

        manager.updateMission(mission);
    }

    @Test
    public void updateMission()  {
        Mission m1 = newMission("m 1", 5, 4, true);
        Mission m2 = newMission("m 1", 4, 3, false);
        manager.createMission(m1);
        manager.createMission(m2);

        Long missionId = m1.getId();
        Mission missionResult = manager.findMissionById(missionId);

        missionResult.setCapacity(6);
        manager.updateMission(missionResult);
        missionResult = manager.findMissionById(missionId);

        assertEquals(5, missionResult.getLevelRequired());
        assertEquals(6, missionResult.getCapacity());
        assertEquals(true, missionResult.isAvailable());

        missionResult.setLevelRequired(3);
        manager.updateMission(missionResult);
        missionResult = manager.findMissionById(missionId);

        assertEquals(3, missionResult.getLevelRequired());
        assertEquals(6, missionResult.getCapacity());
        assertEquals(true, missionResult.isAvailable());

        missionResult.setAvailable(false);
        manager.updateMission(missionResult);
        missionResult = manager.findMissionById(missionId);

        assertEquals(3, missionResult.getLevelRequired());
        assertEquals(6, missionResult.getCapacity());
        assertEquals(false, missionResult.isAvailable());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNullMission() {
        manager.updateMission(null);
    }

    private static Comparator<Mission> idComparator = new Comparator<Mission>() {

        @Override
        public int compare(Mission m1, Mission m2) {
            return m1.getId().compareTo(m2.getId());
        }
    };



    public static Mission newMission(String mission_name, int levelRequired, int capacity, boolean available) {
        Mission mission = new Mission();
        mission.setMission_name(mission_name);
        mission.setLevelRequired(levelRequired);
        mission.setCapacity(capacity);
        mission.setAvailable(available);
        
        return mission;
    }

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        //we will use in memory database
        ds.setDatabaseName("memory:heroManagerImpl-test");
        ds.setCreateDatabase("create");
        return ds;
    }


    /*
    @Rule
    public ExpectedException = expectedException = ExpectedException.none();

    @Test
    public void addMissionWithZeroCapacity() {
        campaignmanager.Mission mission = newMission(0, 2, true);

        expectedException.except(IllegalArgumentException.class);

        manager.createMission(mission);
    }
     */

}
