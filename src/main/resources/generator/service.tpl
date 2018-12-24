package ${packageName};

<#list imports as import>
import ${import};
</#list>

public interface ${className}  {

    ${dtoClassName} add(${dtoClassName} ${dtoParameterName});

    void addAll(List<${dtoClassName}> ${dtoParameterName}s);

    ${dtoClassName} edit(${dtoClassName} ${dtoParameterName});

    void save(${dtoClassName} ${dtoParameterName});

    void deleteBy${primaryKey}(${primaryKeyType} ${primaryKeyParameterName});

    void deleteByObject(${dtoClassName} ${dtoParameterName});

    void deleteByIds(List<${primaryKeyType}> ${primaryKeyParameterName}s);

    Optional<${dtoClassName}> findOneBy${primaryKey}(${primaryKeyType} ${primaryKeyParameterName});

    Optional<${dtoClassName}> findOneByObject(${dtoClassName} ${dtoParameterName});

    List<${dtoClassName}> findAllByObject(${dtoClassName} ${dtoParameterName});

    List<${dtoClassName}> findAll();

    PageInfo<${dtoClassName}> findPage(${dtoClassName} ${dtoParameterName}, int pageNumber, int pageSize);

    long count(${dtoClassName} ${dtoParameterName});

    long countAll();
}