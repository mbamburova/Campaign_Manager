package campaignmanager.backend;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.apache.ibatis.jdbc.ScriptRunner;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;

public class CampaignDatabase {

    private DataSource dataSource;

    public DataSource setUpDatabase() {
        try {
            dataSource = CampaignDatabase.prepareDataSource();
            Connection connection = dataSource.getConnection();
            ScriptRunner scriptRunner = new ScriptRunner(connection);
            scriptRunner.runScript(new BufferedReader(new FileReader("src\\main\\resources\\create_table.sql")));
            scriptRunner.runScript(new BufferedReader(new FileReader("src\\main\\resources\\test_data.sql")));
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }

    public static EmbeddedDataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource embeddedDataSource = new EmbeddedDataSource();
        embeddedDataSource.setDatabaseName("memory:campaign-database");
        embeddedDataSource.setCreateDatabase("create");
        return embeddedDataSource;
    }
}