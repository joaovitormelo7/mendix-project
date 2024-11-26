package externaldatabaseconnector.impl.dbsource.strategy;

import com.zaxxer.hikari.HikariDataSource;
import externaldatabaseconnector.impl.MxSnowflakeDataSource;
import externaldatabaseconnector.pojo.ConnectionDetail;
import net.snowflake.client.jdbc.SnowflakeBasicDataSource;

public class SnowflakeDataSourceStrategy extends BaseDataSourceStrategy {
  public SnowflakeDataSourceStrategy(HikariDataSource hikariDataSource, ConnectionDetail connectionDetails){
    super(hikariDataSource, connectionDetails);
  }
  @Override
  public HikariDataSource configureDataSource() throws Exception {
     SnowflakeBasicDataSource snowflakeBasicDataSource = MxSnowflakeDataSource.getSnowflakeDataSource(connectionDetails);
     dataSource.setDataSource(snowflakeBasicDataSource);
     return dataSource;
  }
}
