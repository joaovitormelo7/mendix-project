package externaldatabaseconnector.impl.dbsource.strategy;

import com.zaxxer.hikari.HikariDataSource;
import externaldatabaseconnector.pojo.ConnectionDetail;

public class DefaultDataSourceStrategy extends BaseDataSourceStrategy {
  public DefaultDataSourceStrategy(HikariDataSource hikariDataSource, ConnectionDetail connectionDetails) {
    super(hikariDataSource, connectionDetails);
  }

  @Override
  public HikariDataSource configureDataSource() {
    this.setBasicConnectionProperties();
    return dataSource;
  }
}
