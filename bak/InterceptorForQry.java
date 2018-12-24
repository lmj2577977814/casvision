package cn.casair.config;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

@Intercepts({ @Signature(type = ResultSetHandler.class,
        method = "handleResultSets",
        args = {
                Statement.class,
        }) })
public class InterceptorForQry implements Interceptor {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object intercept(Invocation invocation) throws Throwable {

        System.out.println(1123);

        if (invocation.getTarget() instanceof Optional ) {
            System.out.println("11");
        }

        Object result = invocation.proceed();

     /*   if(result instanceof Optional) {
            result = Optional.ofNullable(result);
        }*/

        if(result instanceof ArrayList) {
            ArrayList as = (ArrayList) result;
            result = as.stream().map(s -> {
                return Optional.ofNullable(s);
            }).collect(Collectors.toList());
        }
        return result;
    }

    public Object plugin(Object target) {
        System.out.println("this is the proceed ===>>" + target);
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties arg0) {
        System.out.println("this is the properties ===>>" + arg0);
    }
}
