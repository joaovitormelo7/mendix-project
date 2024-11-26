package externaldatabaseconnector.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import externaldatabaseconnector.pojo.ColumnMapping;

import java.lang.reflect.Type;

public class JsonUtil {

    private static final Gson gsonObject = new Gson();

    public static <T> T fromJson(String jsonString, Type resultType) {
        return gsonObject.fromJson(jsonString, resultType);
    }

    //Deserialize and return column attribute mapping
    public static ColumnMapping GetColumnAttributeMapping(String columnAttributeMappingJson) {
        Type columnMapType = new TypeToken<ColumnMapping>() {}.getType();
        return fromJson(columnAttributeMappingJson, columnMapType);
    }
}