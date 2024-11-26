package externaldatabaseconnector.impl.service;

import com.google.gson.reflect.TypeToken;
import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.meta.IMetaAssociation;
import com.mendix.systemwideinterfaces.core.meta.IMetaObject;

import externaldatabaseconnector.constants.ParameterMode;
import externaldatabaseconnector.impl.DatabaseConnectorException;
import externaldatabaseconnector.impl.ResultSetReader;
import externaldatabaseconnector.impl.callablestatement.*;
import externaldatabaseconnector.impl.parameterFactory.SqlParameterFactory;
import externaldatabaseconnector.interfaces.ObjectInstantiator;
import externaldatabaseconnector.pojo.CallableColumnMapping;
import externaldatabaseconnector.pojo.ColumnMapping;
import externaldatabaseconnector.pojo.QueryParameter;
import externaldatabaseconnector.utils.JsonUtil;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CallableStatementService {
    private final ILogNode logNode;
    private final ObjectInstantiator objectInstantiator;

    public CallableStatementService(ILogNode logNode, ObjectInstantiator objectInstantiator) {
        this.logNode = logNode;
        this.objectInstantiator = objectInstantiator;
    }


    public IMendixObject createCallableEntityObject(IContext context, IMetaObject mainEntityMetaObject, CallableStatement callableStatementResult, String queryParameters, String columnMappingJson) throws SQLException, DatabaseConnectorException {
        if (callableStatementResult == null)
            return null;

        Type listType = new TypeToken<CallableColumnMapping>() {}.getType();
        CallableColumnMapping callableColumnMapping = JsonUtil.fromJson(columnMappingJson, listType);

        MendixObjectService mendixObjectService = new MendixObjectService(logNode, objectInstantiator);

        //Initiate entity object for out parameters (Stored procedure return entity)
        IMendixObject mainEntityMendixObject = mendixObjectService.createMendixObject(context, mainEntityMetaObject);

        ResultSet resultSet = callableStatementResult.getResultSet();
        //Check resultSet data and set it is into the  resultSet entity
        if (resultSet != null) {
            //Using 0 index column attribute mapping to set the result set, as currently we are supporting  only one resultset
            ColumnMapping columnMapping = callableColumnMapping.getResultSetEntityList().get(0);

            IMetaObject resultSetMetaObject = Core.getMetaObject(columnMapping.getEntityName());
            Collection<? extends IMetaAssociation> declaredMetaAssociationsChild = mainEntityMetaObject.getDeclaredMetaAssociationsChild();
            IMetaAssociation resultSetAssociation = declaredMetaAssociationsChild.stream().filter(a -> a.getParent().getName().equals(columnMapping.getEntityName())).findFirst().orElse(null);
            ResultSetReader resultSetReader = new ResultSetReader(resultSet, resultSetMetaObject, columnMapping.getColumnAttributeMapping());

            List<Map<String, Optional<Object>>> resultSetMap = resultSetReader.readAll();
            mendixObjectService.createMendixObjects(context, resultSetMetaObject, resultSetMap, resultSetAssociation, mainEntityMendixObject);
        }


        //Read out parameter values and prepare a attribute, value map
        Map<String, Optional<Object>> mainEntityAttributeValueMap = readOutParameters(callableStatementResult, queryParameters, callableColumnMapping.getOutputEntity());

        //Set the attirbute values in the initated out parameter entity (mainEntityMendixObject)
        for (Map.Entry<String, Optional<Object>> attributeValueMap : mainEntityAttributeValueMap.entrySet()) {
            mendixObjectService.setMemberValue(context, mainEntityMetaObject, mainEntityMendixObject, attributeValueMap.getKey(), attributeValueMap.getValue());
        }

        return mainEntityMendixObject;
    }

    private Map<String, Optional<Object>> readOutParameters(CallableStatement callableStatementResult, String queryParametersJson, ColumnMapping columnMapping) throws SQLException {
        Map<String, Optional<Object>> outParameterMap = new HashMap<>();
        outParameterMap.put(columnMapping.getColumnAttributeMapping().get("no_of_rows_affected"), Optional.ofNullable(callableStatementResult.getUpdateCount()));

        // Read OUT and INOUT parameter values from the CallableStatement result
        Type queryParameterMapType = new TypeToken<Map<String, QueryParameter>>() {}.getType();
        Map<String, QueryParameter> queryParameterMap = JsonUtil.fromJson(queryParametersJson, queryParameterMapType);

        int index = 1;
        for (QueryParameter queryParameter : new ArrayList<>(queryParameterMap.values())) {
            if (queryParameter.getParameterMode().equals(ParameterMode.OUT) || queryParameter.getParameterMode().equals(ParameterMode.INOUT)) {
                SqlParameterPrimitiveValue sqlParameter = SqlParameterFactory.getSqlParameter(queryParameter, index);

                // Read parameter value from the callable statement
                Object parameterValue = sqlParameter.getStatementValue(callableStatementResult);

                // Retrieve the associated attribute name from the column mapping for the query parameter
                String attributeName = columnMapping.getColumnAttributeMapping().get(queryParameter.getDatabaseParameterName());

                outParameterMap.put(attributeName, Optional.ofNullable(parameterValue));
            }
            index++;
        }
        return outParameterMap;
    }
}