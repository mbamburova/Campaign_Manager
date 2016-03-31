import campaignmanager.CampaignManagerImpl;
import campaignmanager.Hero;
import campaignmanager.Mission;
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

/**
 * Created by Michaela Bamburová on 15.03.2016.
 */
public class CampaignManagerImplTests {

    private CampaignManagerImpl manager;
    private DataSource dataSource;

    @Before
    public void setUp() throws SQLException {
        dataSource = prepareDataSource();
        try (Connection connection = dataSource.getConnection()) {
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(new BufferedReader(new FileReader("src\\main\\resources\\create_table.sql")));
        } catch (FileNotFoundException ignored) {
        }
        manager = new CampaignManagerImpl();
    }


    @After
    public void tearDown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(new BufferedReader(new FileReader("src\\main\\resources\\drop_table.sql")));
        } catch (FileNotFoundException ignored) {
        }
    }


    @Test(expected = IllegalStateException.class)
    public void sendHeroToMission() throws Exception {
        Mission mission = MissionManagerImplTests.newMission(1, 2, true);
        Hero hero = HeroManagerImplTests.newHero("Dragon", 2);

        manager.sendHeroToMission(hero, mission);

    }

    @Test (expected = IllegalStateException.class)
    public void removeHeroFromMission() {
        Mission mission = MissionManagerImplTests.newMission(1, 2, true);
        Hero hero = HeroManagerImplTests.newHero("Dragon", 2);

        manager.removeHeroFromMission(hero, mission);
    }

    @Test (expected = IllegalStateException.class)
    public void sendHeroToUnavailableMission() {
        Mission mission = MissionManagerImplTests.newMission(1, 2, false);
        Hero hero = HeroManagerImplTests.newHero("Dragon", 2);

        manager.sendHeroToMission(hero, mission);
    }

    @Test (expected = IllegalStateException.class)
    public void sendHeroToFullMission () {
        Mission mission = MissionManagerImplTests.newMission(0, 2, false);
        Hero hero = HeroManagerImplTests.newHero("Dragon", 2);

        manager.sendHeroToMission(hero, mission);
    }

    @Test (expected = IllegalStateException.class)
    public void sendHeroToMissionWithNotRequiredLevel() {
        Mission mission = MissionManagerImplTests.newMission(0, 2, false);
        Hero hero = HeroManagerImplTests.newHero("Dragon", 5);

        manager.sendHeroToMission(hero, mission);
    }


    private static EmbeddedDataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource ds = new EmbeddedDataSource();
        // we will use in memory database
        ds.setDatabaseName("memory:heromgr-test");
        // database is created automatically if it does not exist yet
        ds.setCreateDatabase("create");
        return ds;
    }
}
