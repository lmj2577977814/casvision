package ${packageName};

<#list imports as import>
import ${import};
</#list>

/**
 * ${classRemark}
 */
public class ${className} implements Serializable {

    <#list vars as varr>
    /** ${varr["remark"]} */
    private ${varr["type"]} ${varr["name"]};

    </#list>

    <#list vars as varr>
    public ${varr["type"]} get${varr["prop"]}() {
            return ${varr["name"]};
    }

    public void set${varr["prop"]}(${varr["type"]} ${varr["name"]}) {
        this.${varr["name"]} = ${varr["name"]};
    }

    </#list>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ${className}  that = (${className}) o;
        return Objects.equal(${primaryKeyParameterName}, that.${primaryKeyParameterName});
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(${primaryKeyParameterName});
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                <#list vars as varr>
                    .add("${varr["name"]}", ${varr["name"]})
                </#list>
                .toString();
    }
}
