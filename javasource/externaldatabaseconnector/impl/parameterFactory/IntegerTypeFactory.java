package externaldatabaseconnector.impl.parameterFactory;

import externaldatabaseconnector.impl.callablestatement.*;
import externaldatabaseconnector.pojo.QueryParameter;

public class IntegerTypeFactory {
  public static final String BIGINT_TYPE = "bigint";
  public static final String BIGSERIAL_TYPE = "bigserial";
  public static final String INTEGER_TYPE = "integer";
  public static final String SERIAL_TYPE = "serial";
  public static final String SMALLINT_TYPE = "smallint";
  public static final String SMALLSERIAL_TYPE = "smallserial";

  public static SqlParameterPrimitiveValue GetIntegerSqlParameter(QueryParameter queryParameter, int index){
      switch (queryParameter.getSqlDataType().toLowerCase()) {
        case SMALLINT_TYPE:
        case SMALLSERIAL_TYPE:
          return new SqlParameterShort(queryParameter, index);
        case INTEGER_TYPE:
        case SERIAL_TYPE:
          return new SqlParameterInteger(queryParameter, index);
        case BIGINT_TYPE:
        case BIGSERIAL_TYPE:
        default:
          return new SqlParameterLong(queryParameter, index);
      }
  }
}
