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

import cn.casair.generator.internal.java.ActualTableName;
import cn.casair.generator.internal.java.JavaTypeResolver;
import cn.casair.generator.internal.TableConfiguration;
import cn.casair.generator.internal.java.FullyQualifiedJavaType;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static cn.casair.generator.util.StringUtil.stringContainsSQLWildcard;


/**
 * 数据库信息处理类
 */
public class DatabaseIntrospector {
    private final Logger log = LoggerFactory.getLogger(DatabaseIntrospector.class);

    /** 数据库源信息 */
    private DatabaseMetaData databaseMetaData;

    /** Java类型解析器 */
    private JavaTypeResolver javaTypeResolver;

    public DatabaseIntrospector(DatabaseMetaData databaseMetaData, JavaTypeResolver javaTypeResolver) {
        super();
        this.databaseMetaData = databaseMetaData;
        this.javaTypeResolver = javaTypeResolver;
    }

    /**
     * 根据表配置获取对应表信息
     * @param tc
     * @return
     * @throws SQLException
     */
    public List<IntrospectedTable> introspectTables(TableConfiguration tc) throws SQLException {

        Map<ActualTableName, List<IntrospectedColumn>> columns = getColumns(tc);

        if (columns.isEmpty()) {
            log.warn("Table configuration with catalog {0}, schema {1}, and table {2} did not resolve to any tables", tc.getCatalog(), tc.getSchema(), tc.getTableName());
            return null;
        }

        calculateExtraColumnInformation(tc, columns);
        calculateIdentityColumns(tc, columns);

        List<IntrospectedTable> introspectedTables = calculateIntrospectedTables(tc, columns);

        Iterator<IntrospectedTable> iter = introspectedTables.iterator();
        while (iter.hasNext()) {
            IntrospectedTable introspectedTable = iter.next();

            if (!introspectedTable.hasAnyColumns()) {
                log.warn("Table {} does not exist, this table will be ignored", introspectedTable.getFullyQualifiedTable().toString());
                iter.remove();
            } else if (!introspectedTable.hasPrimaryKeyColumns() && !introspectedTable.hasBaseColumns()) {
                log.warn("Table {} contains only LOB fields, this table will be ignored", introspectedTable.getFullyQualifiedTable().toString());
                iter.remove();
            } else {
                // now make sure that all columns called out in the
                // configuration
                // actually exist
                //reportIntrospectionWarnings(introspectedTable, tc,
                //introspectedTable.getFullyQualifiedTable());
            }
        }

        return introspectedTables;
    }


    /**
     * 获取表以及对应列信息
     * @param tc
     * @return
     * @throws SQLException
     */
    private Map<ActualTableName, List<IntrospectedColumn>> getColumns(TableConfiguration tc) throws SQLException {
        String localCatalog;
        String localSchema;
        String localTableName;

        boolean delimitIdentifiers = StringUtils.containsWhitespace(tc.getCatalog())
                                    || StringUtils.containsWhitespace(tc.getSchema())
                                    || StringUtils.containsWhitespace(tc.getTableName());

        if (delimitIdentifiers) {
            localCatalog = tc.getCatalog();
            localSchema = tc.getSchema();
            localTableName = tc.getTableName();
            //检索此数据库是否将大小写混写的不带引号的 SQL 标识符作为不区分大小写的形式处理，并以小写形式存储它们。
        } else if (databaseMetaData.storesLowerCaseIdentifiers()) {
            localCatalog = tc.getCatalog() == null ? null : tc.getCatalog().toLowerCase();
            localSchema = tc.getSchema() == null ? null : tc.getSchema().toLowerCase();
            localTableName = tc.getTableName() == null ? null : tc.getTableName().toLowerCase();
            //检索此数据库是否将大小写混写的不带引号的 SQL 标识符作为不区分大小写的形式处理，并以大写形式存储它们。
        } else if (databaseMetaData.storesUpperCaseIdentifiers()) {
            localCatalog = tc.getCatalog() == null ? null : tc.getCatalog().toUpperCase();
            localSchema = tc.getSchema() == null ? null : tc.getSchema().toUpperCase();
            localTableName = tc.getTableName() == null ? null : tc.getTableName().toUpperCase();
        } else {
            localCatalog = tc.getCatalog();
            localSchema = tc.getSchema();
            localTableName = tc.getTableName();
        }

        Map<ActualTableName, List<IntrospectedColumn>> answer = new HashMap<ActualTableName, List<IntrospectedColumn>>();

        ResultSet rs = databaseMetaData.getColumns(localCatalog, localSchema, localTableName, "%");

        boolean supportsIsAutoIncrement = false;
        boolean supportsIsGeneratedColumn = false;
        ResultSetMetaData rsmd = rs.getMetaData();

        int colCount = rsmd.getColumnCount();
        for (int i = 1; i <= colCount; i++) {
            if ("IS_AUTOINCREMENT".equals(rsmd.getColumnName(i))) {
                supportsIsAutoIncrement = true;
            }
            if ("IS_GENERATEDCOLUMN".equals(rsmd.getColumnName(i))) {
                supportsIsGeneratedColumn = true;
            }
        }

        while (rs.next()) {
            IntrospectedColumn introspectedColumn = new IntrospectedColumn();

            introspectedColumn.setJdbcType(rs.getInt("DATA_TYPE"));
            introspectedColumn.setLength(rs.getInt("COLUMN_SIZE"));
            introspectedColumn.setActualColumnName(rs.getString("COLUMN_NAME"));
            introspectedColumn.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
            introspectedColumn.setScale(rs.getInt("DECIMAL_DIGITS"));
            introspectedColumn.setRemarks(rs.getString("REMARKS"));
            introspectedColumn.setDefaultValue(rs.getString("COLUMN_DEF"));

            if (supportsIsAutoIncrement) {
                introspectedColumn.setAutoIncrement("YES".equals(rs.getString("IS_AUTOINCREMENT"))); //$NON-NLS-2$
            }

            if (supportsIsGeneratedColumn) {
                introspectedColumn.setGeneratedColumn("YES".equals(rs.getString("IS_GENERATEDCOLUMN"))); //$NON-NLS-2$
            }

            ActualTableName atn = new ActualTableName(rs.getString("TABLE_CAT"), rs.getString("TABLE_SCHEM"), rs.getString("TABLE_NAME"));

            List<IntrospectedColumn> columns = answer.get(atn);
            if (columns == null) {
                columns = new ArrayList<IntrospectedColumn>();
                answer.put(atn, columns);
            }

            columns.add(introspectedColumn);
        }

        closeResultSet(rs);

        if (answer.size() > 1 && !stringContainsSQLWildcard(localSchema) && !stringContainsSQLWildcard(localTableName)) {
            ActualTableName inputAtn = new ActualTableName(tc.getCatalog(), tc.getSchema(), tc.getTableName());

            StringBuilder sb = new StringBuilder();
            boolean comma = false;
            for (ActualTableName atn : answer.keySet()) {
                if (comma) {
                    sb.append(',');
                } else {
                    comma = true;
                }
                sb.append(atn.toString());
            }

            log.warn("Table Configuration {0} matched more than one table ({1})", inputAtn.toString(), sb.toString());
        }

        return answer;
    }


    /** 添加列扩展信息 */
    private void calculateExtraColumnInformation(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {
        //todo tc暂时保留，后续添加忽略列s
        for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns
                .entrySet()) {
            for (IntrospectedColumn introspectedColumn : entry.getValue()) {
                String calculatedColumnName = introspectedColumn.getActualColumnName();
                introspectedColumn.setJavaProperty(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL,calculatedColumnName));
                introspectedColumn.setJavaVariable(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,calculatedColumnName));

                FullyQualifiedJavaType fullyQualifiedJavaType = javaTypeResolver.calculateJavaType(introspectedColumn);

                if (fullyQualifiedJavaType != null) {
                    introspectedColumn.setFullyQualifiedJavaType(fullyQualifiedJavaType);
                    introspectedColumn.setJdbcTypeName(javaTypeResolver.calculateJdbcTypeName(introspectedColumn));
                } else {

                    introspectedColumn.setFullyQualifiedJavaType(FullyQualifiedJavaType.getObjectInstance());
                    introspectedColumn.setJdbcTypeName("OTHER");

                    log.warn("Unsupported Data Type {0} in table {1}, column: {2}, property defaults to Object type.",
                            Integer.toString(introspectedColumn.getJdbcType()),
                            entry.getKey().toString(),
                            introspectedColumn.getActualColumnName());
                }
            }
        }
    }

    /** 计算自增列 */
    private void calculateIdentityColumns(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {
        String generatedKey = tc.getGeneratedKey();
        if (generatedKey == null) {
            return;
        }

        for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns
                .entrySet()) {
            for (IntrospectedColumn introspectedColumn : entry.getValue()) {
                if (Objects.equals(introspectedColumn, generatedKey)) {
                    if (tc.isIdentity()) {
                        introspectedColumn.setIdentity(true);
                        introspectedColumn.setSequenceColumn(false);
                    } else {
                        introspectedColumn.setIdentity(false);
                        introspectedColumn.setSequenceColumn(true);
                    }
                }
            }
        }
    }

    /** 计算表信息 */
    private List<IntrospectedTable> calculateIntrospectedTables(TableConfiguration tc, Map<ActualTableName, List<IntrospectedColumn>> columns) {

        List<IntrospectedTable> answer = new ArrayList<IntrospectedTable>();

        for (Map.Entry<ActualTableName, List<IntrospectedColumn>> entry : columns.entrySet()) {
            ActualTableName atn = entry.getKey();

            String objectName = Strings.isNullOrEmpty(tc.getDomainObjectName())
                    ? CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, atn.getTableName()) : tc.getDomainObjectName();

            FullyQualifiedTable table = new FullyQualifiedTable(
                    StringUtils.containsWhitespace(tc.getCatalog()) ? atn.getCatalog() : null,
                    StringUtils.containsWhitespace(tc.getSchema()) ? atn.getSchema() : null,
                    atn.getTableName(),
                    objectName);

            IntrospectedTable introspectedTable = new IntrospectedTable();
            introspectedTable.setFullyQualifiedTable(table);

            for (IntrospectedColumn introspectedColumn : entry.getValue()) {
                introspectedTable.addColumn(introspectedColumn);
            }

            calculatePrimaryKey(table, introspectedTable);
            
            enhanceIntrospectedTable(introspectedTable);

            answer.add(introspectedTable);
        }

        return answer;
    }

    /** 计算主键 */
    private void calculatePrimaryKey(FullyQualifiedTable table, IntrospectedTable introspectedTable) {
        ResultSet rs = null;

        try {
            rs = databaseMetaData.getPrimaryKeys(table.getIntrospectedCatalog(), table.getIntrospectedSchema(), table.getIntrospectedTableName());
        } catch (SQLException e) {
            closeResultSet(rs);
            log.warn("Cannot obtain primary key information from the database, generated objects may be incomplete");
            return;
        }

        try {
            Map<Short, String> keyColumns = new TreeMap<Short, String>();
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                short keySeq = rs.getShort("KEY_SEQ");
                keyColumns.put(keySeq, columnName);
            }

            for (String columnName : keyColumns.values()) {
                introspectedTable.addPrimaryKeyColumn(columnName);
            }
        } catch (SQLException e) {
            // ignore the primary key if there's any error
        } finally {
            closeResultSet(rs);
        }
    }

    /** 扩展表信息 */
    private void enhanceIntrospectedTable(IntrospectedTable introspectedTable) {
        try {
            FullyQualifiedTable fqt = introspectedTable.getFullyQualifiedTable();

            ResultSet rs = databaseMetaData.getTables(fqt.getIntrospectedCatalog(), fqt.getIntrospectedSchema(), fqt.getIntrospectedTableName(), null);
            if (rs.next()) {
                String remarks = rs.getString("REMARKS");
                String tableType = rs.getString("TABLE_TYPE");
                introspectedTable.setRemarks(remarks);
                introspectedTable.setTableType(tableType);
            }
            closeResultSet(rs);
        } catch (SQLException e) {
            log.warn("Exception retrieving table metadata: {}", e.getMessage());
        }
    }


    /**
     * 关闭结果集
     * @param rs
     */
    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}
