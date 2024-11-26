package externaldatabaseconnector.impl.dbsource.strategy;

import com.zaxxer.hikari.HikariDataSource;
import externaldatabaseconnector.interfaces.DataSourceStrategy;
import externaldatabaseconnector.pojo.ConnectionDetail;

public abstract class BaseDataSourceStrategy implements DataSourceStrategy {
  protected HikariDataSource dataSource;
  protected ConnectionDetail connectionDetails;

  public BaseDataSourceStrategy(HikariDataSource hikariDataSource, ConnectionDetail connectionDetails){
    this.dataSource = hikariDataSource;
    this.connectionDetails = connectionDetails;
  }

  public abstract HikariDataSource configureDataSource() throws Exception;

  protected void setBasicConnectionProperties(){
    dataSource.setJdbcUrl(connectionDetails.getConnectionString());
    dataSource.setUsername(connectionDetails.getUserName());
    dataSource.setPassword(connectionDetails.getPassword());
  }
}
