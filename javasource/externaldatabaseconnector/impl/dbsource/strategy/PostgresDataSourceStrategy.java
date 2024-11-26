package externaldatabaseconnector.impl.dbsource.strategy;

import com.zaxxer.hikari.HikariDataSource;
import externaldatabaseconnector.impl.dbsource.DataSourceConstants;
import externaldatabaseconnector.pojo.ConnectionDetail;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PostgresDataSourceStrategy extends BaseDataSourceStrategy {
  private static final Set<String> SSL_MODES_WITHOUT_SERVER_VERIFICATION = Set.of("prefer", "require");

  public PostgresDataSourceStrategy(HikariDataSource hikariDataSource, ConnectionDetail connectionDetails){
    super(hikariDataSource, connectionDetails);
  }

  @Override
  public HikariDataSource configureDataSource() {
    this.setBasicConnectionProperties();

    setSSLProperties(dataSource, connectionDetails);
    return dataSource;
  }

  private void setSSLProperties(HikariDataSource dataSource, ConnectionDetail connectionDetails) {
    Map<String, String> additionalProperties = connectionDetails.getAdditionalProperties();
    String authenticationMethod = additionalProperties.get(DataSourceConstants.AUTHENTICATION_METHOD);

    if (authenticationMethod == null || !authenticationMethod.equals(DataSourceConstants.SSL_AUTHENTICATION))
      return;


    String sslMode = getSslModeFromJdbcUrl(connectionDetails.getConnectionString());
    String identifier = additionalProperties.get(DataSourceConstants.CLIENT_CERTIFICATE_IDENTIFIER);

    // if identifier is empty and sslmode is prefer or require, no need to set the SSLFactory
    if (SSL_MODES_WITHOUT_SERVER_VERIFICATION.contains(sslMode.toLowerCase()) && identifier.isBlank())
      return;

    var conProps = new Properties();
    conProps.setProperty(DataSourceConstants.CLIENT_CERTIFICATE_IDENTIFIER, identifier);
    conProps.setProperty(DataSourceConstants.SSL_FACTORY_PROPERTY_KEY, DataSourceConstants.SSL_FACTORY_NAMESPACE);
    dataSource.setDataSourceProperties(conProps);
  }

  private static String getSslModeFromJdbcUrl(String jdbcUrl) {
    // default sslMode is prefer
    String sslModeValue = DataSourceConstants.DEFAULT_SSL_MODE;
    // Split the URL by "?" to separate base and query parameters
    String[] urlParts = jdbcUrl.split("\\?");
    if (urlParts.length < 2) {
      return sslModeValue; // No query parameters, return default
    }

    // Split the query part by "&" to get all key-value pairs
    String[] params = urlParts[1].split("&");
    for (String param : params) {
      // Split key and value by "="
      String[] keyValue = param.split("=");
      if (keyValue.length == 2 && keyValue[0].equalsIgnoreCase(DataSourceConstants.SSL_MODE_PROPERTY)) {
        sslModeValue = keyValue[1]; // get the value of sslmode
      }
    }
    return sslModeValue;
  }
}
