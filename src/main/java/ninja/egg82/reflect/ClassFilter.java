package ninja.egg82.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassFilter {
    private static final Logger logger = LoggerFactory.getLogger(PackageFilter.class);

    private ClassFilter() {}

    public static Object[] getStaticFields(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz cannot be null.");
        }

        Field[] fields = clazz.getDeclaredFields();
        List<Object> returns = new ArrayList<>();

        for (int i = 0; i < fields.length; i++) {
            if (!Modifier.isPrivate(fields[i].getModifiers())) {
                try {
                    returns.add(fields[i].get(null));
                } catch (IllegalAccessException ex) {
                    logger.warn(ex.getMessage(), ex);
                }
            }
        }

        return returns.toArray();
    }
}
