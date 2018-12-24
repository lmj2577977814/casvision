package cn.casair.config;

import com.google.common.collect.Iterables;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class OptionalAwareObjectFactory extends DefaultObjectFactory {

    /*public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        if (Optional.class.isAssignableFrom(type)) {
            return Optional.ofNullable(Iterables.getOnlyElement(constructorArgs));
        } else {
            return super.create(type, constructorArgTypes, constructorArgs);
        }
    }*/


 /*   public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        if (Optional.class.isAssignableFrom(type)) {
            return (T) Optional.ofNullable(Iterables.getOnlyElement(constructorArgs));
        } else {
            return super.create(type, constructorArgTypes, constructorArgs);
        }
    }*/

}
