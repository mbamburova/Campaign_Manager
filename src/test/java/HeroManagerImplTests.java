import campaignmanager.backend.Hero;
import campaignmanager.backend.HeroManager;
import campaignmanager.backend.HeroManagerImpl;
import campaignmanager.backend.common.IllegalEntityException;
import campaignmanager.backend.common.ServiceFailureException;
import campaignmanager.backend.common.ValidationException;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Anonym on 14. 3. 2016.
 */
public class HeroManagerImplTests {

    private HeroManagerImpl managerHero;
    private DataSource dataSource;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        //we will use in memory database
        dataSource.setDatabaseName("memory:heroManagerImpl-test");
        dataSource.setCreateDatabase("create");
        return dataSource;
    }

    @Before
    public void setUp() throws SQLException {

        dataSource = prepareDataSource();

        try (Connection connection = dataSource.getConnection()) {
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(new BufferedReader(new FileReader("src\\main\\resources\\create_table.sql")));
        } catch (FileNotFoundException ignored) {
        }
        managerHero = new HeroManagerImpl();
        managerHero.setDataSource(dataSource);
    }

    @After
    public void tearDown() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(new BufferedReader(new FileReader("src\\main\\resources\\drop_table.sql")));
        } catch (FileNotFoundException ignored) {
        }
    }

    private HeroBuilder sampleFemaleHero() {
        return new HeroBuilder()
                .id(null)
                .hero_name("Miska")
                .hero_level(42);
    }

    private HeroBuilder sampleMaleHero() {
        return new HeroBuilder()
                .id(null)
                .hero_name("Bartimaeus")
                .hero_level(12);
    }

    @Test
    public void createHero() {
        Hero hero = sampleFemaleHero().build();
        managerHero.createHero(hero);

        Long heroId = hero.getId();
        assertThat(heroId).isNotNull();

        assertThat(managerHero.findHeroById(heroId))
                .isNotSameAs(hero)
                .isEqualToComparingFieldByField(hero);
    }


    @Test
    public void findAllHeroes() {

        assertThat(managerHero.findAllHeroes()).isEmpty();

        Hero g1 = sampleFemaleHero().build();
        Hero g2 = sampleFemaleHero().build();

        managerHero.createHero(g1);
        managerHero.createHero(g2);

        assertThat(managerHero.findAllHeroes())
                .usingFieldByFieldElementComparator()
                .containsOnly(g1,g2);
    }


    @Test (expected = ValidationException.class)
    public void createNullHero()  { managerHero.createHero(null);
    }

    @Test
    public void createHeroWithId() {
        Hero hero = sampleFemaleHero().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        managerHero.createHero(hero);
    }

    @Test
    public void createHeroWithNullName() {
        Hero hero = sampleFemaleHero().hero_name(null).build();
        expectedException.expect(ValidationException.class);
        managerHero.createHero(hero);
    }

    @Test
    public void createHeroWithZeroLevel() {
        Hero hero = sampleFemaleHero().hero_level(0).build();
        expectedException.expect(ValidationException.class);
        managerHero.createHero(hero);
    }

    @Test
    public void createHeroWithNegativeLevel() {
        Hero hero = sampleFemaleHero().hero_level(-1).build();
        assertThatThrownBy(() -> managerHero.createHero(hero))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    public void updateHeroLevel() {
        Hero heroForUpdate = sampleFemaleHero().build();
        Hero anotherHero = sampleMaleHero().build();
        managerHero.createHero(heroForUpdate);
        managerHero.createHero(anotherHero);

        // Performa the update operation ...
        heroForUpdate.setLevel(1);

        // ... and save updated hero to database
        managerHero.updateHero(heroForUpdate);

        // Check if hero was properly updated
        assertThat(managerHero.findHeroById(heroForUpdate.getId()))
                .isEqualToComparingFieldByField(heroForUpdate);
        // Check if updates didn't affected other records
        assertThat(managerHero.findHeroById(anotherHero.getId()))
                .isEqualToComparingFieldByField(anotherHero);
    }

    @Test
    public void updateHeroName() {
        Hero heroForUpdate = sampleFemaleHero().build();
        Hero anotherHero = sampleMaleHero().build();
        managerHero.createHero(heroForUpdate);
        managerHero.createHero(anotherHero);

        // Performa the update operation ...
        heroForUpdate.setName("Terra");

        // ... and save updated hero to database
        managerHero.updateHero(heroForUpdate);

        // Check if hero was properly updated
        assertThat(managerHero.findHeroById(heroForUpdate.getId()))
                .isEqualToComparingFieldByField(heroForUpdate);
        // Check if updates didn't affected other records
        assertThat(managerHero.findHeroById(anotherHero.getId()))
                .isEqualToComparingFieldByField(anotherHero);
    }


    @Test (expected = ValidationException.class)
    public void updateNullHero() {
        managerHero.updateHero(null);
    }

    @Test
    public void updateHeroSetNullId() {
        Hero hero = sampleFemaleHero().id(null).build();
        expectedException.expect(IllegalEntityException.class);
        managerHero.updateHero(hero);
    }

    @Test
    public void updateHeroSetNegativeId() {
        Hero hero = sampleFemaleHero().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        managerHero.updateHero(hero);
    }

    @Test
    public void updateHeroSetZeroLevel() {
        Hero hero = sampleFemaleHero().build();
        managerHero.createHero(hero);
        hero.setLevel(0);
        expectedException.expect(ValidationException.class);
        managerHero.updateHero(hero);
    }

    @Test
    public void updateHeroSetNegativeLevel() {
        Hero hero = sampleFemaleHero().build();
        managerHero.createHero(hero);
        hero.setLevel(-10);
        expectedException.expect(ValidationException.class);
        managerHero.updateHero(hero);
    }

    @Test
    public void updateHeroSetNullName() {
        Hero hero = sampleFemaleHero().build();
        managerHero.createHero(hero);
        hero.setName(null);
        expectedException.expect(ValidationException.class);
        managerHero.updateHero(hero);
    }

    @Test
    public void deleteHero() {

        Hero g1 = sampleFemaleHero().build();
        Hero g2 = sampleMaleHero().build();
        managerHero.createHero(g1);
        managerHero.createHero(g2);

        assertThat(managerHero.findHeroById(g1.getId())).isNotNull();
        assertThat(managerHero.findHeroById(g2.getId())).isNotNull();

        managerHero.deleteHero(g1);

        assertThat(managerHero.findHeroById(g1.getId())).isNull();
        assertThat(managerHero.findHeroById(g2.getId())).isNotNull();

    }

    @Test (expected = IllegalArgumentException.class)
    public void deleteNullHero() {
        managerHero.deleteHero(null);
    }

    @Test
    public void deleteHeroWithNullId() {
        Hero hero = sampleFemaleHero().id(null).build();
        expectedException.expect(IllegalEntityException.class);
        managerHero.deleteHero(hero);
    }


    @Test
    public void createHeroWithSqlExceptionThrown() throws SQLException {
        // Create sqlException, which will be thrown by our DataSource mock
        // object to simulate DB operation failure
        SQLException sqlException = new SQLException();
        // Create DataSource mock object
        DataSource failingDataSource = mock(DataSource.class);
        // Instruct our DataSource mock object to throw our sqlException when
        // DataSource.getConnection() method is called.
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        // Configure our manager to use DataSource mock object
        managerHero.setDataSource(failingDataSource);

        // Create Hero instance for our test
        Hero hero = sampleFemaleHero().build();

        // Try to call Manager.createHero(Hero) method and expect that
        // exception will be thrown
        assertThatThrownBy(() -> managerHero.createHero(hero))
                // Check that thrown exception is ServiceFailureException
                .isInstanceOf(ServiceFailureException.class)
                // Check if cause is properly set
                .hasCause(sqlException);
    }

    @FunctionalInterface
    private static interface Operation<T> {
        void callOn(T subjectOfOperation);
    }


    private void testExpectedServiceFailureException(Operation<HeroManager> operation) throws SQLException {
        SQLException sqlException = new SQLException();
        DataSource failingDataSource = mock(DataSource.class);
        when(failingDataSource.getConnection()).thenThrow(sqlException);
        managerHero.setDataSource(failingDataSource);
        assertThatThrownBy(() -> {
            operation.callOn(managerHero);
        })
                .isInstanceOf(ServiceFailureException.class)
                .hasCause(sqlException);
    }

    @Test
    public void updateHeroWithSqlExceptionThrown() throws SQLException {
        Hero hero = sampleFemaleHero().build();
        managerHero.createHero(hero);
        testExpectedServiceFailureException((heroManager) -> heroManager.updateHero(hero));
    }

    @Test
    public void getHeroWithSqlExceptionThrown() throws SQLException {
        Hero hero = sampleFemaleHero().build();
        managerHero.createHero(hero);
        testExpectedServiceFailureException((heroManager) -> {
            heroManager.findHeroById(hero.getId());
        });
    }

    @Test
    public void deleteHeroWithSqlExceptionThrown() throws SQLException {
        Hero hero = sampleFemaleHero().build();
        managerHero.createHero(hero);
        testExpectedServiceFailureException((heroManager) -> heroManager.deleteHero(hero));
    }

    @Test
    public void findAllHeroesWithSqlExceptionThrown() throws SQLException {
        testExpectedServiceFailureException((heroManager) -> heroManager.findAllHeroes());
    }

    @Test
    public void deleteHeroWithNonExistingId() {
        Hero hero = sampleFemaleHero().id(1L).build();
        expectedException.expect(IllegalEntityException.class);
        managerHero.deleteHero(hero);
    }

    public static Hero newHero (String name, int level){
        Hero hero = new Hero();
        hero.setName(name);
        hero.setLevel(level);
        return hero;
    }

    private void assertMorePreciseEquals(Hero rightFormat, Hero actualFormat){
        assertEquals(rightFormat.getId(), actualFormat.getId());
        assertEquals(rightFormat.getLevel(), actualFormat.getLevel());
        assertNotSame(rightFormat.getName(), actualFormat.getName());
    }
}