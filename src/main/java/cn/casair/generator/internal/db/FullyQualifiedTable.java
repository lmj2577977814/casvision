/**
 *    Copyright 2006-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.casair.generator.internal.db;

import cn.casair.generator.util.StringUtil;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import static cn.casair.generator.util.StringUtil.composeFullyQualifiedTableName;
import static cn.casair.generator.util.StringUtil.hash;

/**
 * The Class FullyQualifiedTable.
 *
 * @author Jeff Butler
 */
public class FullyQualifiedTable {

    /** The introspected catalog. */
    private String introspectedCatalog;

    /** The introspected schema. */
    private String introspectedSchema;

    /** The introspected table name. */
    private String introspectedTableName;

    /** The domain object name. */
    private String domainObjectName;


    public FullyQualifiedTable(String introspectedCatalog, String introspectedSchema, String introspectedTableName, String domainObjectName) {
        super();
        this.introspectedCatalog = introspectedCatalog;
        this.introspectedSchema = introspectedSchema;
        this.introspectedTableName = introspectedTableName;

        if (!Strings.isNullOrEmpty(domainObjectName)) {
            int index = domainObjectName.lastIndexOf('.');
            if (index == -1) {
                this.domainObjectName = domainObjectName;
            } else {
                this.domainObjectName = domainObjectName.substring(index + 1);
            }
        }
    }

    /**
     * Gets the introspected catalog.
     *
     * @return the introspected catalog
     */
    public String getIntrospectedCatalog() {
        return introspectedCatalog;
    }


    /**
     * Gets the introspected schema.
     *
     * @return the introspected schema
     */
    public String getIntrospectedSchema() {
        return introspectedSchema;
    }

    /**
     * Gets the introspected table name.
     *
     * @return the introspected table name
     */
    public String getIntrospectedTableName() {
        return introspectedTableName;
    }

    /**
     * Gets the domain object name.
     *
     * @return the domain object name
     */
    public String getDomainObjectName() {
        if (!Strings.isNullOrEmpty(domainObjectName)) {
            return domainObjectName;
        } else if (!Strings.isNullOrEmpty(introspectedTableName)) {
            return StringUtil.getCamelCaseString(introspectedTableName, true);
        } else {
            return StringUtil.getCamelCaseString(introspectedTableName, true);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FullyQualifiedTable)) {
            return false;
        }

        FullyQualifiedTable other = (FullyQualifiedTable) obj;

        return Objects.equal(this.introspectedTableName,
                other.introspectedTableName)
                && Objects.equal(this.introspectedCatalog,
                        other.introspectedCatalog)
                && Objects.equal(this.introspectedSchema,
                        other.introspectedSchema);
    }

    @Override
    public int hashCode() {
        int result = 23;
        result = hash(result, introspectedTableName);
        result = hash(result, introspectedCatalog);
        result = hash(result, introspectedSchema);

        return result;
    }

    @Override
    public String toString() {
        return composeFullyQualifiedTableName(
                introspectedCatalog, introspectedSchema, introspectedTableName,
                '.');
    }
}
