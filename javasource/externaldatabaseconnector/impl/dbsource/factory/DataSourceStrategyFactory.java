package externaldatabaseconnector.impl.dbsource.factory;

import com.zaxxer.hikari.HikariDataSource;
import externaldatabaseconnector.impl.dbsource.DataSourceConstants;
import externaldatabaseconnector.impl.dbsource.DataSourceContext;
import externaldatabaseconnector.impl.dbsource.strategy.BaseDataSourceStrategy;
import externaldatabaseconnector.impl.dbsource.strategy.DefaultDataSourceStrategy;
import externaldatabaseconnector.impl.dbsource.strategy.PostgresDataSourceStrategy;
import externaldatabaseconnector.impl.dbsource.strategy.SnowflakeDataSourceStrategy;
import externaldatabaseconnector.pojo.ConnectionDetail;

public class DataSourceStrategyFactory {
  public static HikariDataSource createHikariDataSource(ConnectionDetail connectionDetails, Integer connPoolKey) throws Exception {

    HikariDataSource hikariDataSource = new HikariDataSource();
    hikariDataSource.setPoolName(String.format("MxDbConnector-HikaryCP-%d", connPoolKey));
    hikariDataSource.setMinimumIdle(0);

    BaseDataSourceStrategy dataSourceStrategy;

    switch (connectionDetails.getDatabaseType()) {
      case DataSourceConstants.POSTGRESQL:
        dataSourceStrategy = new PostgresDataSourceStrategy(hikariDataSource, connectionDetails);
        break;
      case DataSourceConstants.SNOWFLAKE:
        dataSourceStrategy = new SnowflakeDataSourceStrategy(hikariDataSource, connectionDetails);
        break;
      default:
        dataSourceStrategy = new DefaultDataSourceStrategy(hikariDataSource, connectionDetails);
        break;
    }

    DataSourceContext dataSourceContext = new DataSourceContext(dataSourceStrategy);
    return dataSourceContext.createDataSource();
  }
}
