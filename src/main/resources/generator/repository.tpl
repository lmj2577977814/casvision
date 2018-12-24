package ${packageName};

<#list imports as import>
import ${import};
</#list>

@Repository
@Mapper
public interface ${className} {

    void insert(${domainClassName} ${domainParameterName});

    void insertAll(List<${domainClassName}> ${domainParameterName}s);

    void deleteBy${primaryKey}(${primaryKeyType} ${primaryKeyParameterName});

    void deleteByObject(${domainClassName} ${domainParameterName});

    void deleteBy${primaryKey}s(List<${primaryKeyType}> ${primaryKeyParameterName}s);

    void updateBy${primaryKey}(${domainClassName} ${domainParameterName});

    ${domainClassName} selectOneBy${primaryKey}(${primaryKeyType} ${primaryKeyParameterName});

    ${domainClassName} selectOneByObject(${domainClassName} ${domainParameterName});

    List<${domainClassName}> selectAllByObject(${domainClassName} ${domainParameterName});

    List<${domainClassName}> selectAll();

    long count(${domainClassName} ${domainParameterName});

    long countAll();
}
