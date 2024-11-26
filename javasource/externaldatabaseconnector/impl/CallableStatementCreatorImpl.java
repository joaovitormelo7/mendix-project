package externaldatabaseconnector.impl;

import com.google.gson.reflect.TypeToken;
import externaldatabaseconnector.impl.callablestatement.*;
import externaldatabaseconnector.impl.parameterFactory.SqlParameterFactory;
import externaldatabaseconnector.interfaces.CallableStatementCreator;
import externaldatabaseconnector.pojo.QueryParameter;
import externaldatabaseconnector.utils.JsonUtil;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class CallableStatementCreatorImpl implements CallableStatementCreator {

    @Override
    public StatementWrapper create(String sql, String queryParametersJSON, Connection connection) throws SQLException, DatabaseConnectorException {
        final CallableStatement callableStatement = connection.prepareCall(sql);

        //Register stored procedure parameters. if there are any
        Type queryParameterMapType = new TypeToken<Map<String, QueryParameter>>() {}.getType();
        Map<String, QueryParameter> queryParameterMap = JsonUtil.fromJson(queryParametersJSON, queryParameterMapType);

        int index = 1;
        for (QueryParameter queryParameter : new ArrayList<>(queryParameterMap.values())) {
            SqlParameter sqlParameter = SqlParameterFactory.getSqlParameter(queryParameter, index);
            sqlParameter.prepareCall(callableStatement);
            index++;
        }

        return new StatementWrapper(callableStatement);
    }
}