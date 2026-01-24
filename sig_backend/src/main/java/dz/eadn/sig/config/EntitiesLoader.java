package dz.eadn.sig.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class EntitiesLoader {

    @Autowired
    protected DataSource dataSource;

    List<String> entitiesList = new ArrayList<>();

    @Bean
    void getEntitiesList() throws Exception {
        DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
        ResultSet tables = metaData.getTables(null, null, null, new String[] { "TABLE" });
        while (tables.next()) {
            entitiesList.add(tables.getString("TABLE_NAME"));
        }
    }

    public List<String> returnEntitiesList(){
        return this.entitiesList;
    }


}
