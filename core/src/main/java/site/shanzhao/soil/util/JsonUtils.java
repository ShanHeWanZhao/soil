package site.shanzhao.soil.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tanruidong
 * @date 2021/01/30 12:54
 */
public class JsonUtils {
    private static final ObjectMapper MAPPER;
    static {
        MAPPER = new ObjectMapper();
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    public static <T> T string2Object(String json, Class<T> targetClass){
        try{
            return MAPPER.readValue(json, targetClass);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String object2String(Object o){
        try {
            return MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
