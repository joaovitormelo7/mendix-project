package externaldatabaseconnector.impl.callablestatement;

import externaldatabaseconnector.constants.ParameterMode;
import externaldatabaseconnector.impl.DatabaseConnectorException;
import externaldatabaseconnector.pojo.QueryParameter;

import java.sql.CallableStatement;
import java.sql.SQLException;

public abstract class SqlParameter {
    protected QueryParameter queryParameter;

    public SqlParameter(QueryParameter queryParameter) {
        this.queryParameter = queryParameter;
    }

    public void prepareCall(CallableStatement cStatement) throws SQLException, DatabaseConnectorException {
        switch (this.queryParameter.getParameterMode()) {
            case ParameterMode.IN:
                prepareInput(cStatement);
                break;
            case ParameterMode.OUT:
                prepareOutput(cStatement);
                break;
            case ParameterMode.INOUT:
                prepareInput(cStatement);
                prepareOutput(cStatement);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized parameter mode" + this.queryParameter.getParameterMode());
        }
    }


    /**
     * Method to register the output value from an executed CallableStatement based on parameter type.
     */
    protected abstract void prepareOutput(CallableStatement cStatement) throws SQLException, DatabaseConnectorException;

    /**
     * Method to set the input value in a CallableStatement based on parameter type.
     */
    protected abstract void prepareInput(CallableStatement cStatement) throws SQLException, DatabaseConnectorException;
}
