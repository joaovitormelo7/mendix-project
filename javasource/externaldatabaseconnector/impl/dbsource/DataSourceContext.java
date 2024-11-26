package externaldatabaseconnector.impl.dbsource;

import com.zaxxer.hikari.HikariDataSource;
import externaldatabaseconnector.impl.dbsource.strategy.BaseDataSourceStrategy;

public class DataSourceContext {
  private BaseDataSourceStrategy dataSourceStrategy;

  public DataSourceContext(BaseDataSourceStrategy strategy) {
    this.dataSourceStrategy = strategy;
  }

  public HikariDataSource createDataSource() throws Exception {
    return dataSourceStrategy.configureDataSource();
  }
}
