// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package externaldatabaseconnector.actions;

import com.mendix.core.Core;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.meta.IMetaObject;
import com.mendix.webui.CustomJavaAction;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import externaldatabaseconnector.impl.JdbcConnector;
import externaldatabaseconnector.proxies.constants.Constants;

public class ExecuteCallable extends CustomJavaAction<IMendixObject>
{
	private final java.lang.String connectionDetails;
	private final java.lang.String sql;
	private final java.lang.String queryParameters;
	private final java.lang.String columnMapping;
	private final java.lang.String OutputEntity;

	public ExecuteCallable(
		IContext context,
		java.lang.String _connectionDetails,
		java.lang.String _sql,
		java.lang.String _queryParameters,
		java.lang.String _columnMapping,
		java.lang.String _outputEntity
	)
	{
		super(context);
		this.connectionDetails = _connectionDetails;
		this.sql = _sql;
		this.queryParameters = _queryParameters;
		this.columnMapping = _columnMapping;
		this.OutputEntity = _outputEntity;
	}

	@java.lang.Override
	public IMendixObject executeAction() throws Exception
	{
		// BEGIN USER CODE
        try {
            IMetaObject metaObject = Core.getMetaObject(this.OutputEntity);
            IMendixObject resultObject = connector.executeCallable(connectionDetails,
                    metaObject, sql, queryParameters, columnMapping, this.getContext());
            if (logNode.isTraceEnabled()) logNode.trace(String.format("Result object: %s", resultObject.toString()));

            return resultObject;
        } catch (Exception exception) {
            logNode.error(exception.getMessage(), exception);
            throw exception;
        }
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "ExecuteCallable";
	}

	// BEGIN EXTRA CODE
    private final ILogNode logNode = Core.getLogger(Constants.getLogNode());
    private final JdbcConnector connector = new JdbcConnector(logNode);
	// END EXTRA CODE
}
