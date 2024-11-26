package externaldatabaseconnector.pojo;

public class QueryParameter {
    private final String name;
    private final String dataType;
    private final String sqlDataType;
    private String value;
    private String parameterMode;
    private String databaseParameterName;

    public QueryParameter(String name, String dataType, String sqlDataType, String value, String parameterMode, String databaseParameterName) {
        this.name = name;
        this.dataType = dataType;
        this.sqlDataType = sqlDataType;
        this.value = value;
        this.parameterMode = parameterMode;
        this.databaseParameterName = databaseParameterName;
    }

    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }

    public String getSqlDataType() {
        return sqlDataType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getParameterMode() {
        return parameterMode;
    }

    public String getDatabaseParameterName() {
        return databaseParameterName;
    }
}
