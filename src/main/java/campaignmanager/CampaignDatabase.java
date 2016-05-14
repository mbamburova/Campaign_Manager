package campaignmanager;

import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.sql.SQLException;


/**
 * Created by Michaela Bamburová on 18.04.2016.
 */
public class CampaignDatabase {

    public static void main(String[]args) throws SQLException, FileNotFoundException {
        EmbeddedDataSource embeddedDataSource = new EmbeddedDataSource();
        embeddedDataSource.setUser("user");

    }


    public static DataSource prepareDataSource() throws SQLException {
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        //we will use in memory database
        dataSource.setDatabaseName("memory:heroManagerImpl-test");
        dataSource.setCreateDatabase("create");
        return dataSource;
    }


}
