package externaldatabaseconnector.interfaces;

import com.zaxxer.hikari.HikariDataSource;
import externaldatabaseconnector.pojo.ConnectionDetail;

public interface DataSourceStrategy {
  HikariDataSource configureDataSource() throws Exception;
}
