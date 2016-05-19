package campaignmanager;

import common.DBUtils;
import common.IllegalEntityException;
import common.ServiceFailureException;
import common.ValidationException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Anonym on 14. 3. 2016.
 */
public class HeroManagerImpl implements HeroManager {

    private ResourceBundle bundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());
    private static final Logger logger = Logger.getLogger(
            HeroManagerImpl.class.getName());

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
    public List<Hero> findAllHeroes() {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, hero_name, hero_level FROM hero");
            return executeQueryForMultipleHeroes(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all heroes from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Hero> viewFreeHeroes() {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, hero_name, hero_level FROM hero WHERE missionId IS NULL");
            return executeQueryForMultipleHeroes(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all heroes from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Hero> viewHeroesByLevel(int level) {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, hero_name, hero_level FROM hero WHERE hero_level = ?");
            st.setInt(1, level);
            return executeQueryForMultipleHeroes(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all heroes from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public List<Hero> viewHeroesByName(String name) {
        checkDataSource();
        Connection conn = null;
        PreparedStatement st = null;

        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, hero_name, hero_level FROM hero WHERE hero_name LIKE '%' || ? || '%'");
            st.setString(1, name);
            return executeQueryForMultipleHeroes(st);
        } catch (SQLException ex) {
            String msg = "Error when getting all heroes from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    static List<Hero> executeQueryForMultipleHeroes(PreparedStatement st) throws SQLException {
        ResultSet rs = st.executeQuery();
        List<Hero> result = new ArrayList<Hero>();
        while (rs.next()) {
            result.add(rowToHero(rs));
        }
        return result;
    }

    private static Hero rowToHero(ResultSet rs) throws SQLException {
        Hero result = new Hero();
        result.setId(rs.getLong("id"));
        result.setName(rs.getString("hero_name"));
        result.setLevel(rs.getInt("hero_level"));
        return result;
    }

    private void validate(Hero hero) throws ValidationException {
        if (hero == null) {
            throw new ValidationException(bundle.getString("hero is null"));
        }
        if (hero.getLevel() < 1) {
            throw new ValidationException(bundle.getString("level is less than 1"));
        }
        if(hero.getName() == null){
            throw new ValidationException(bundle.getString("name is null"));
        }
    }

    @Override
    public Hero findHeroById(Long id) {
        checkDataSource();

        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.prepareStatement(
                    "SELECT id, hero_name, hero_level FROM Hero WHERE id = ?");
            st.setLong(1, id);
            return executeQueryForSingleHero(st);
        } catch (SQLException ex) {
            String msg = "Error when getting hero with id = " + id + " from DB";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.closeQuietly(conn, st);
        }
    }

    static Hero executeQueryForSingleHero(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            Hero result = rowToHero(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal integrity error: more heroes with the same id found!");
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public void createHero(Hero hero) {
        checkDataSource();
        validate(hero);
        if (hero.getId() != null) {
            throw new IllegalEntityException("Hero id is already set");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "INSERT INTO Hero (hero_name,hero_level) VALUES (?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, hero.getName());
            st.setInt(2, hero.getLevel());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, hero, true);
            Long id = DBUtils.getId(st.getGeneratedKeys());
            hero.setId(id);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when inserting hero into db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void updateHero(Hero hero) {
        checkDataSource();
        validate(hero);
        if (hero.getId() == null) {
            throw new IllegalEntityException("Hero id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                  "UPDATE Hero SET hero_name = ?, hero_level = ? WHERE id = ?");
            st.setString(1, hero.getName());
            st.setInt(2, hero.getLevel());
            st.setLong(3, hero.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, hero, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when updating hero in the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }

    @Override
    public void deleteHero(Hero hero) {
        checkDataSource();
        if (hero == null) {
            throw new IllegalArgumentException("hero is null");
        }
        if (hero.getId() == null) {
            throw new IllegalEntityException("hero id is null");
        }
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            st = conn.prepareStatement(
                    "DELETE FROM Hero WHERE id = ?");
            st.setLong(1, hero.getId());

            int count = st.executeUpdate();
            DBUtils.checkUpdatesCount(count, hero, false);
            conn.commit();
        } catch (SQLException ex) {
            String msg = "Error when deleting hero from the db";
            logger.log(Level.SEVERE, msg, ex);
            throw new ServiceFailureException(msg, ex);
        } finally {
            DBUtils.doRollbackQuietly(conn);
            DBUtils.closeQuietly(conn, st);
        }
    }
}
