package ${packageName};

<#list imports as import>
import ${import};
</#list>

@Service
@Mapper(componentModel = "spring")
public interface ${className} extends EntityMapper<${dtoClassName}, ${domainClassName}>{

    @Override
    ${domainClassName} toEntity(${dtoClassName} dto);

    @Override
    ${dtoClassName} toDto(${domainClassName} entity);

    default ${domainClassName} from${primaryKey}(${primaryKeyType} ${primaryKeyParameterName}) {
        if (${primaryKeyParameterName} == null) {
            return null;
        }
        ${domainClassName} ${domainParameterName} = new ${domainClassName}();
        ${domainParameterName}.set${primaryKey}(${primaryKeyParameterName});
        return ${domainParameterName};
    }

}
