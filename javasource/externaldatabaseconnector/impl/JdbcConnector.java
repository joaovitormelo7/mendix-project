package externaldatabaseconnector.impl;

import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import com.mendix.systemwideinterfaces.core.meta.IMetaObject;
import externaldatabaseconnector.impl.service.CallableStatementService;
import externaldatabaseconnector.impl.callablestatement.StatementWrapper;
import externaldatabaseconnector.impl.service.MendixObjectService;
import externaldatabaseconnector.interfaces.CallableStatementCreator;
import externaldatabaseconnector.interfaces.ConnectionManager;
import externaldatabaseconnector.interfaces.ObjectInstantiator;
import externaldatabaseconnector.interfaces.PreparedStatementCreator;
import externaldatabaseconnector.pojo.ColumnMapping;
import externaldatabaseconnector.pojo.ConnectionDetail;
import externaldatabaseconnector.utils.JsonUtil;

import java.sql.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * JdbcConnector implements the execute query (and execute statement)
 * functionality, and returns a {@link Stream} of {@link IMendixObject}s.
 */
public class JdbcConnector {
    private final ILogNode logNode;
    private final ObjectInstantiator objectInstantiator;
    private final ConnectionManager connectionManager;
    public final PreparedStatementCreator preparedStatementCreator;
    public final CallableStatementCreator callableStatementCreator;

    public JdbcConnector(final ILogNode logNode, final ObjectInstantiator objectInstantiator,
                         final ConnectionManager connectionManager, final PreparedStatementCreator preparedStatementCreator, final CallableStatementCreator callableStatementCreator
    ) {
        this.logNode = logNode;
        this.objectInstantiator = objectInstantiator;
        this.connectionManager = connectionManager;
        this.preparedStatementCreator = preparedStatementCreator;
        this.callableStatementCreator = callableStatementCreator;
    }

    public JdbcConnector(final ILogNode logNode) {
        this(logNode, new ObjectInstantiatorImpl(), ConnectionManagerSingleton.getInstance(),
                new PreparedStatementCreatorImpl(), new CallableStatementCreatorImpl());
    }

    public List<IMendixObject> executeQuery(final String connectionDetailsJsonString,
                                            final IMetaObject metaObject, final String sql, final String queryParameters,
                                            final String columnMappingJson, final IContext context)
            throws SQLException, DatabaseConnectorException {

        ConnectionDetail connectionDetails = JsonUtil.fromJson(connectionDetailsJsonString, ConnectionDetail.class);

        if (logNode.isTraceEnabled()) logNode.trace(String.format("executeQuery: %s, %s, %s",
                connectionDetails.getConnectionString(), connectionDetails.getUserName(), sql));

        try (Connection connection = connectionManager.getConnection(connectionDetails);
             PreparedStatement preparedStatement = preparedStatementCreator.create(sql, queryParameters, connection);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            ColumnMapping columnAttributeMapping = JsonUtil.GetColumnAttributeMapping(columnMappingJson);
            ResultSetReader resultSetReader = new ResultSetReader(resultSet, metaObject, columnAttributeMapping.getColumnAttributeMapping());

            MendixObjectService mendixObjectService = new MendixObjectService(logNode, objectInstantiator);
            return mendixObjectService.createMendixObjects(context, metaObject, resultSetReader.readAll(), null, null);
        }
    }

    public long executeStatement(final String connectionDetailsJsonString, final String sql, final String queryParameters)
            throws SQLException {

        ConnectionDetail connectionDetails = JsonUtil.fromJson(connectionDetailsJsonString, ConnectionDetail.class);

        if (logNode.isTraceEnabled())
            logNode.trace(String.format("executeStatement: %s, %s, %s",
                    connectionDetails.getConnectionString(), connectionDetails.getUserName(), sql));

        try (Connection connection = connectionManager.getConnection(connectionDetails);
             PreparedStatement preparedStatement = preparedStatementCreator.create(sql, queryParameters, connection)) {
            return preparedStatement.executeUpdate();
        }
    }

    public List<IMendixObject> executeCallableQuery(String connectionDetailsJsonString, IMetaObject metaObject, String sql, String queryParameters, String columnMappingJson, IContext context) throws SQLException, DatabaseConnectorException {

        ConnectionDetail connectionDetails = JsonUtil.fromJson(connectionDetailsJsonString, ConnectionDetail.class);

        if (logNode.isTraceEnabled()) logNode.trace(String.format("executeCallableQuery: %s, %s, %s",
                connectionDetails.getConnectionString(), connectionDetails.getUserName(), sql));

        try (Connection connection = connectionManager.getConnection(connectionDetails);
             StatementWrapper callableStatement = callableStatementCreator.create(sql, queryParameters, connection);
             ResultSet resultSet = callableStatement.execute();) {

            ColumnMapping columnAttributeMapping = JsonUtil.GetColumnAttributeMapping(columnMappingJson);
            ResultSetReader resultSetReader = new ResultSetReader(resultSet, metaObject, columnAttributeMapping.getColumnAttributeMapping());

            MendixObjectService mendixObjectService = new MendixObjectService(logNode, objectInstantiator);
            return mendixObjectService.createMendixObjects(context, metaObject, resultSetReader.readAll(), null, null);
        }
    }

    public long executeCallableStatement(String connectionDetailsJsonString, String sql, String queryParameters) throws SQLException, DatabaseConnectorException {

        ConnectionDetail connectionDetails = JsonUtil.fromJson(connectionDetailsJsonString, ConnectionDetail.class);

        if (logNode.isTraceEnabled())
            logNode.trace(String.format("executeCallableStatement: %s, %s, %s",
                    connectionDetails.getConnectionString(), connectionDetails.getUserName(), sql));

        try (Connection connection = connectionManager.getConnection(connectionDetails);
             StatementWrapper callableStatement = callableStatementCreator.create(sql, queryParameters, connection)) {
            return callableStatement.executeUpdate();
        }
    }

    public IMendixObject executeCallable(String connectionDetailsJsonString, IMetaObject metaObject, String sql, String queryParameters, String columnMapping, IContext context) throws SQLException, DatabaseConnectorException {
        ConnectionDetail connectionDetails = JsonUtil.fromJson(connectionDetailsJsonString, ConnectionDetail.class);

        if (logNode.isTraceEnabled()) logNode.trace(String.format("executeCallable: %s, %s, %s",
                connectionDetails.getConnectionString(), connectionDetails.getUserName(), sql));

        try (Connection connection = connectionManager.getConnection(connectionDetails);
             StatementWrapper callableStatement = callableStatementCreator.create(sql, queryParameters, connection);
             CallableStatement callableStatementResult = callableStatement.executeCallable()) {
            CallableStatementService callableStatementService = new CallableStatementService(logNode, objectInstantiator);
            return callableStatementService.createCallableEntityObject(context, metaObject, callableStatementResult, queryParameters, columnMapping);
        }
    }
}