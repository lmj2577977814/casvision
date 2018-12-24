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
package cn.casair.generator.internal;

/**
 * The Class TableConfiguration.
 *
 * @author Jeff Butler
 */
public class TableConfiguration {

    /** The catalog. */
    private String catalog;
    
    /** The schema. */
    private String schema;
    
    /** The table name. */
    private String tableName;
    
    /** The domain object name. */
    private String domainObjectName;

    private boolean isIdentity;

    private String generatedKey;

    public boolean isIdentity() {
        return isIdentity;
    }

    public void setIdentity(boolean identity) {
        isIdentity = identity;
    }

    public String getGeneratedKey() {
        return generatedKey;
    }

    public void setGeneratedKey(String generatedKey) {
        this.generatedKey = generatedKey;
    }

    /**
     * Instantiates a new table configuration.
     *
     */
    public TableConfiguration() {
        super();
    }

    /**
     * Gets the catalog.
     *
     * @return the catalog
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * Sets the catalog.
     *
     * @param catalog
     *            the new catalog
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    /**
     * Gets the domain object name.
     *
     * @return the domain object name
     */
    public String getDomainObjectName() {
        return domainObjectName;
    }

    /**
     * Sets the domain object name.
     *
     * @param domainObjectName
     *            the new domain object name
     */
    public void setDomainObjectName(String domainObjectName) {
        this.domainObjectName = domainObjectName;
    }

    /**
     * Gets the schema.
     *
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Sets the schema.
     *
     * @param schema
     *            the new schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * Gets the table name.
     *
     * @return the table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the table name.
     *
     * @param tableName
     *            the new table name
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
