package Connection;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class CustomExclusionStrategy implements ExclusionStrategy {

    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getName().equals("class"));
    }

    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

}