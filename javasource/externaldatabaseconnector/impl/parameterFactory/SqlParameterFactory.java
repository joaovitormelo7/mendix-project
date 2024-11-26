package externaldatabaseconnector.impl.parameterFactory;

import externaldatabaseconnector.impl.callablestatement.*;
import externaldatabaseconnector.pojo.QueryParameter;

import java.sql.SQLException;

public class SqlParameterFactory {
    public static SqlParameterPrimitiveValue getSqlParameter(QueryParameter queryParameter, int index) throws SQLException {
        try {
            switch (queryParameter.getDataType()) {
                case "INTEGER":
                    return IntegerTypeFactory.GetIntegerSqlParameter(queryParameter, index);
                case "STRING":
                    return StringTypeFactory.GetStringSqlParameter(queryParameter, index);
                case "BOOLEAN":
                    return new SqlParameterBoolean(queryParameter, index);
                case "DECIMAL":
                    return DecimalTypeFactory.GetDecimalSqlParameter(queryParameter, index);
                case "DATETIME":
                    return DateTimeTypeFactory.GetDateTimeSqlParameter(queryParameter, index);
                default:
                    throw new IllegalArgumentException(String.format("The parameter type '%s' is invalid, for the parameter named '%s'.", queryParameter.getDataType(), queryParameter.getName()));
            }
        } catch (Exception exception) {

            //If exception is caught with type of IllegalArgumentException, do not alter message throw it as it is.
            if (exception.getClass().equals(IllegalArgumentException.class)) {
                throw exception;
            }

            String message = String.format("An error has occurred while handling the parameter with name: '%s', \nCause : %s",
                    queryParameter.getName(), exception.toString());
            throw new SQLException(message, exception);
        }
    }
}
