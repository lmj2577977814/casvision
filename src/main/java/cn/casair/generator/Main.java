package cn.casair.generator;

import cn.casair.config.ApplicationProperties;
import cn.casair.generator.internal.TableConfiguration;
import cn.casair.generator.internal.db.DatabaseIntrospector;
import cn.casair.generator.internal.java.JavaTypeResolver;
import cn.casair.generator.internal.java.FullyQualifiedJavaType;
import cn.casair.generator.internal.db.IntrospectedColumn;
import cn.casair.generator.internal.db.IntrospectedTable;
import cn.casair.generator.util.GeneratorHelper;
import cn.casair.generator.util.StringUtil;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private final static Logger log = LoggerFactory.getLogger(Main.class);
    private static Configuration cfg = null;
    private static ApplicationProperties.GeneratorProperties generator = null;

    static {
        cfg = new Configuration(Configuration.VERSION_2_3_22);
        try {
            cfg.setDirectoryForTemplateLoading(new File("/Users/linjianwei/Documents/javaprojects/casvision/src/main/resources/generator"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        generator = new ApplicationProperties.GeneratorProperties();

        String basePath = "/Users/linjianwei/Documents/javaprojects/casvision/src/main/java/";
        String domainPackage = "cn.casair.domain";
        String repositoryPackage = "cn.casair.repository";
        String repositoryXmlPathRela = "src/main/resources/mapper";
        String servicePackage = "cn.casair.service";
        String serviceImplPackage = "cn.casair.service.impl";
        String dtoPackage = "cn.casair.service.dto";
        String dtoMapperPackage = "cn.casair.service.mapper";
        String resourcePackage = "cn.casair.web.rest";

        generator.setBasePath(basePath);
        generator.setDomainPackage(domainPackage);
        generator.setRepositoryPackage(repositoryPackage);
        generator.setRepositoryXmlPathRela(repositoryXmlPathRela);
        generator.setServicePackage(servicePackage);
        generator.setServiceImplPackage(serviceImplPackage);
        generator.setDtoPackage(dtoPackage);
        generator.setDtoMapperPackage(dtoMapperPackage);
        generator.setResourcePackage(resourcePackage);

    }

    public static void main(String[] args) throws SQLException{
        IntrospectedTable table = getTables().get(0);
        String domainClassName = table.getFullyQualifiedTable().getDomainObjectName();

        GeneratorContext generatorContext = new GeneratorContext(generator, domainClassName);

        generateDomainClass(table, generatorContext);
        generateRepository(table, generatorContext);
        generateDTOClass(table, generatorContext);
        generateDTOMapperClass(table, generatorContext);
        generateServiceClass(table, generatorContext);
        generateServiceImplClass(table, generatorContext);
        generateResourceClass(table, generatorContext);
        generateMapperXml(table, generatorContext);
    }

    private static void generateMapperXml(IntrospectedTable table, GeneratorContext context) {
        FullyQualifiedJavaType repositoryJavaType = context.getRepositoryJavaType();
        FullyQualifiedJavaType domainJavaType = context.getDomainJavaType();


        Map<String, Object> data = new HashMap<>();

        data.put("fullRepositoryClassName", repositoryJavaType.getFullyQualifiedNameWithoutTypeParameters());
        data.put("fullDomainClassName", domainJavaType.getFullyQualifiedNameWithoutTypeParameters());
        data.put("tableName", table.getFullyQualifiedTable().getIntrospectedTableName());

        List<Map<String, Object>> vars = table.getAllColumns()
                .stream()
                .map(introspectedColumn -> {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("column",introspectedColumn.getActualColumnName());
                    map.put("jdbcType",introspectedColumn.getJdbcTypeName());
                    map.put("property",introspectedColumn.getJavaVariable());
                    map.put("isPrimaryKey",introspectedColumn.isIdentity() || introspectedColumn.isAutoIncrement());
                    return map;
                }).collect(Collectors.toList());

        IntrospectedColumn primaryColumn = table.getPrimaryKeyColumns().get(0);
        data.put("primaryKeyColumn",primaryColumn.getActualColumnName());
        data.put("primaryKeyJdbcType",primaryColumn.getJdbcTypeName());
        data.put("primaryKeyVariable",primaryColumn.getJavaVariable());
        data.put("primaryKeyProperty",primaryColumn.getJavaProperty());
        data.put("primaryKeyJavaType",primaryColumn.getFullyQualifiedJavaType().getFullyQualifiedNameWithoutTypeParameters());

        data.put("vars", vars);
        generateFile(context.getMapperXmlFilePath(), "mapper-xml.tpl", data);
    }

    private static void generateResourceClass(IntrospectedTable table, GeneratorContext context) {
        FullyQualifiedJavaType resourceJavaType = context.getResourceJavaType();
        FullyQualifiedJavaType serviceJavaType = context.getServiceJavaType();
        FullyQualifiedJavaType domainJavaType = context.getDomainJavaType();
        FullyQualifiedJavaType dtoJavaType = context.getDtoJavaType();

        //导入包
        List<String> imports = Lists.newArrayList();

        imports.add("com.codahale.metrics.annotation.Timed");
        imports.add("org.slf4j.Logger");
        imports.add("org.slf4j.LoggerFactory");
        imports.add("com.github.pagehelper.PageInfo");
        imports.add("org.springframework.http.ResponseEntity");
        imports.add("org.springframework.web.bind.annotation.*");
        imports.add("javax.validation.Valid");
        imports.add("java.util.List");
        imports.addAll(serviceJavaType.getImportList());
        imports.addAll(dtoJavaType.getImportList());

        Map<String, Object> data = new HashMap<>();

        data.put("packageName", resourceJavaType.getPackageName());
        data.put("className", resourceJavaType.getShortName());
        data.put("imports", Sets.newHashSet(imports));
        data.put("domainClassName", domainJavaType.getShortName());
        data.put("domainParameterName", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, domainJavaType.getShortNameWithoutTypeArguments()));
        data.put("dtoClassName", dtoJavaType.getShortName());
        data.put("dtoParameterName", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, dtoJavaType.getShortName()));

        data.put("restfulUrl", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, StringUtil.convertPlural(domainJavaType.getShortNameWithoutTypeArguments())));

        data.putAll(getPrimaryKey(table));

        generateFile(context.getResourceClassFilePath(), "resource.tpl", data);
    }

    /**
     * 生成serviceImpl类
     * @param table
     * @param context
     */
    private static void generateServiceImplClass(IntrospectedTable table, GeneratorContext context) {
        FullyQualifiedJavaType serviceImplJavaType = context.getServiceImplJavaType();
        FullyQualifiedJavaType serviceJavaType = context.getServiceJavaType();
        FullyQualifiedJavaType domainJavaType = context.getDomainJavaType();
        FullyQualifiedJavaType dtoJavaType = context.getDtoJavaType();
        FullyQualifiedJavaType dtoMapperJavaType = context.getDtoMapperJavaType();
        FullyQualifiedJavaType repositoryJavaType = context.getRepositoryJavaType();

        //导入包
        List<String> imports = Lists.newArrayList();
        imports.add("com.github.pagehelper.PageHelper");
        imports.add("com.github.pagehelper.PageInfo");
        imports.add("org.slf4j.Logger");
        imports.add("org.slf4j.LoggerFactory");
        imports.add("org.springframework.stereotype.Service");
        imports.add("org.springframework.transaction.annotation.Transactional");
        imports.add("java.util.List");
        imports.add("java.util.Optional");
        imports.add("java.util.stream.Collectors");
        imports.addAll(serviceJavaType.getImportList());
        imports.addAll(domainJavaType.getImportList());
        imports.addAll(dtoJavaType.getImportList());
        imports.addAll(dtoMapperJavaType.getImportList());
        imports.addAll(repositoryJavaType.getImportList());

        Map<String, Object> data = new HashMap<>();

        data.put("packageName", serviceImplJavaType.getPackageName());
        data.put("className", serviceImplJavaType.getShortName());
        data.put("imports", Sets.newHashSet(imports));
        data.put("domainClassName", domainJavaType.getShortName());
        data.put("domainParameterName", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, domainJavaType.getShortName()));
        data.put("dtoClassName", dtoJavaType.getShortName());
        data.put("dtoParameterName", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, dtoJavaType.getShortName()));
        data.put("iClassName", serviceJavaType.getShortName());

        data.putAll(getPrimaryKey(table));

        generateFile(context.getServiceImplClassFilePath(), "service-impl.tpl", data);

    }

    /**
     * 生成service类
     * @param table
     * @param context
     */
    private static void generateServiceClass(IntrospectedTable table, GeneratorContext context) {
        FullyQualifiedJavaType serviceJavaType = context.getServiceJavaType();
        FullyQualifiedJavaType dtoJavaType = context.getDtoJavaType();

        List<String> imports = Lists.newArrayList();
        imports.add("com.github.pagehelper.PageInfo");
        imports.add("java.util.List");
        imports.add("java.util.Optional");
        imports.addAll(dtoJavaType.getImportList());

        Map<String, Object> data = new HashMap<>();

        data.put("packageName", generator.getServicePackage());
        data.put("className", serviceJavaType.getShortName());
        data.put("imports", Sets.newHashSet(imports));
        data.put("dtoClassName", dtoJavaType.getShortName());
        data.put("dtoParameterName", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, dtoJavaType.getShortName()));

        data.putAll(getPrimaryKey(table));

        generateFile(context.getServiceClassFilePath(), "service.tpl", data);
    }

    /**
     * 生成dtoMapper类
     * @param table
     * @param context
     */
    private static void generateDTOMapperClass(IntrospectedTable table, GeneratorContext context) {
        FullyQualifiedJavaType dtoMapperJavaType = context.getDtoMapperJavaType();
        FullyQualifiedJavaType domainJavaType = context.getDomainJavaType();
        FullyQualifiedJavaType dtoJavaType = context.getDtoJavaType();

        List<String> imports = Lists.newArrayList();
        imports.add("org.mapstruct.Mapper");
        imports.add("org.mapstruct.Mapping");
        imports.add("org.mapstruct.Mappings");
        imports.add("org.springframework.stereotype.Service");
        imports.addAll(domainJavaType.getImportList());
        imports.addAll(dtoJavaType.getImportList());

        Map<String, Object> data = new HashMap<>();

        data.put("packageName", dtoMapperJavaType.getPackageName());
        data.put("imports", Sets.newHashSet(imports));
        data.put("className", dtoMapperJavaType.getShortName());

        data.put("domainClassName", domainJavaType.getShortName());
        data.put("domainParameterName", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, domainJavaType.getShortName()));
        data.put("dtoClassName", dtoJavaType.getShortName());
        data.put("dtoParameterName", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, dtoJavaType.getShortName()));

        data.putAll(getPrimaryKey(table));
        
        generateFile(context.getDtoMapperClassFilePath(), "dto-mapper.tpl", data);

    }

    /**
     * 生成DTO类
     * @param table
     * @param context
     */
    private static void generateDTOClass(IntrospectedTable table, GeneratorContext context) {
        FullyQualifiedJavaType dtoJavaType = context.getDtoJavaType();

        Map<String, Object> data = new HashMap<>();
        data.put("packageName", dtoJavaType.getPackageName());
        data.put("className", dtoJavaType.getShortName());
        data.put("classRemark", table.getRemarks());

        List<String> imports = Lists.newArrayList();
        imports.add("com.google.common.base.MoreObjects");
        imports.add("com.google.common.base.Objects");
        imports.add("java.io.Serializable");
        imports.addAll(table.getAllColumns()
                .stream()
                .map(introspectedColumn -> {
                    return introspectedColumn
                            .getFullyQualifiedJavaType()
                            .getImportList();
                }).flatMap(Collection::stream).collect(Collectors.toList()));


        List<Map<String, String>> vars = table.getAllColumns()
                .stream()
                .map(introspectedColumn -> {
                    Map<String, String> map = Maps.newHashMap();
                    map.put("type",introspectedColumn.getFullyQualifiedJavaType().getShortName());
                    map.put("prop",introspectedColumn.getJavaProperty());
                    map.put("name",introspectedColumn.getJavaVariable());
                    map.put("remark",introspectedColumn.getRemarks());
                    return map;
                }).collect(Collectors.toList());


        data.put("imports", Sets.newHashSet(imports));
        data.put("vars", vars);
        data.putAll(getPrimaryKey(table));

        generateFile(context.getDTOClassFilePath(), "dto.tpl", data);

    }

    /**
     * 生成Repository类
     * @throws IOException
     */
    private static void generateRepository(IntrospectedTable table, GeneratorContext context){
        FullyQualifiedJavaType domainJavaType = context.getDomainJavaType();
        FullyQualifiedJavaType repositoryJavaType = context.getRepositoryJavaType();


        List<String> imports = Lists.newArrayList();
        imports.add("org.springframework.stereotype.Repository");
        imports.add("org.apache.ibatis.annotations.Mapper");
        imports.add("java.util.List");
        imports.addAll(domainJavaType.getImportList());


        Map<String, Object> data = new HashMap<>();

        data.put("packageName", repositoryJavaType.getPackageName());
        data.put("imports", Sets.newHashSet(imports));
        data.put("className", repositoryJavaType.getShortName());
        data.put("domainClassName", domainJavaType.getShortName());
        data.put("domainParameterName", CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, domainJavaType.getShortName()));
        data.putAll(getPrimaryKey(table));

        generateFile(context.getRepositoryClassFilePath(), "repository.tpl", data);
    }

    /**
     * 生成domain类
     */
    private static void generateDomainClass(IntrospectedTable table, GeneratorContext context) {
        FullyQualifiedJavaType domainJavaType = context.getDomainJavaType();

        Map<String, Object> data = new HashMap<>();
        data.put("packageName", domainJavaType.getPackageName());
        data.put("className", domainJavaType.getShortName());
        data.put("classRemark", table.getRemarks());

        List<String> imports = Lists.newArrayList();
        imports.add("com.google.common.base.MoreObjects");
        imports.add("com.google.common.base.Objects");
        imports.add("java.io.Serializable");
        imports.addAll(table.getAllColumns()
                .stream()
                .map(introspectedColumn -> {
                    return introspectedColumn
                            .getFullyQualifiedJavaType()
                            .getImportList();
                }).flatMap(Collection::stream).collect(Collectors.toList()));


        List<Map<String, String>> vars = table.getAllColumns()
                .stream()
                .map(introspectedColumn -> {
                    Map<String, String> map = Maps.newHashMap();
                    map.put("type",introspectedColumn.getFullyQualifiedJavaType().getShortName());
                    map.put("prop",introspectedColumn.getJavaProperty());
                    map.put("name",introspectedColumn.getJavaVariable());
                    map.put("remark",introspectedColumn.getRemarks());
                    return map;
                }).collect(Collectors.toList());


        data.put("imports", Sets.newHashSet(imports));
        data.put("vars", vars);
        data.putAll(getPrimaryKey(table));

        generateFile(context.getDomainClassFilePath(), "domain.tpl", data);
    }

    private static void generateFile(String filePath, String templateName, Object data) {
        log.debug("代码生成:[路径:{}, 模板:{}]", filePath, templateName);
        File file = new File(filePath);

        boolean deleteFile = true;

        if(deleteFile) {
            if(file.exists()) {
                file.delete();
            }
            return;
        }

        try {
            Files.createParentDirs(file);
            Files.touch(file);
            Template temp = cfg.getTemplate(templateName);
            temp.process(data, Files.newWriter(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    private static  Map getPrimaryKey(IntrospectedTable table) {
        List<IntrospectedColumn> primaryKeyColumns  = table.getPrimaryKeyColumns();
        IntrospectedColumn introspectedColumn = primaryKeyColumns.get(0);
        String primaryKeyType = introspectedColumn.getFullyQualifiedJavaType().getShortName();
        String primaryKey = introspectedColumn.getJavaProperty();
        String primaryKeyParameterName = introspectedColumn.getJavaVariable();

        Map data = Maps.newHashMap();

        data.put("primaryKey", primaryKey);
        data.put("primaryKeyType", primaryKeyType);
        data.put("primaryKeyParameterName", primaryKeyParameterName);
        return data;
    }

    private static List<IntrospectedTable>  getTables() throws SQLException {
        Properties props =new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", "centling");
        props.setProperty("remarks", "true");
        props.setProperty("useInformationSchema", "true");// mysql获取表注释需要
        Connection connection = DriverManager.getConnection("jdbc:mysql://49.4.70.8:3306/casvision?useUnicode=true&characterEncoding=utf8&useSSL=false", props);

        DatabaseIntrospector databaseIntrospector =  new DatabaseIntrospector(connection.getMetaData(), new JavaTypeResolver());
        TableConfiguration tc = new TableConfiguration();
        tc.setTableName("example_copy");
        //tc.setDomainObjectName("cn.casair.domain.Example");
        List<IntrospectedTable> tables =  databaseIntrospector.introspectTables(tc);
        System.out.println(tables);
        return tables;
    }

    public static class GeneratorContext {
        private ApplicationProperties.GeneratorProperties properties;
        private String domainName;

        private FullyQualifiedJavaType domainJavaType = null;
        private FullyQualifiedJavaType repositoryJavaType = null;
        private FullyQualifiedJavaType dtoJavaType = null;
        private FullyQualifiedJavaType dtoMapperJavaType = null;
        private FullyQualifiedJavaType serviceJavaType = null;
        private FullyQualifiedJavaType serviceImplJavaType = null;
        private FullyQualifiedJavaType resourceJavaType = null;

        public GeneratorContext(ApplicationProperties.GeneratorProperties properties, String domainName) {
            this.properties = properties;
            this.domainName = domainName;

            domainJavaType = new FullyQualifiedJavaType(properties.getDomainPackage() + "." + domainName);
            repositoryJavaType = new FullyQualifiedJavaType(properties.getRepositoryPackage() + "." +domainName + "Repository");
            dtoJavaType = new FullyQualifiedJavaType(properties.getDtoPackage() + "." +domainName + "DTO");
            dtoMapperJavaType = new FullyQualifiedJavaType(properties.getDtoMapperPackage() + "." +domainName + "Mapper");
            serviceJavaType = new FullyQualifiedJavaType(properties.getServicePackage() + "." +domainName + "Service");
            serviceImplJavaType = new FullyQualifiedJavaType(properties.getServiceImplPackage() + "." +domainName + "ServiceImpl");
            resourceJavaType = new FullyQualifiedJavaType(properties.getResourcePackage() + "." +domainName + "Resource");
        }

        public String getDomainClassFilePath() {
            String domainPath = GeneratorHelper.generatorPath(generator.getBasePath(), GeneratorHelper.packageConvertToPath(generator.getDomainPackage()));
            return domainPath + domainJavaType.getShortNameWithoutTypeArguments() + ".java";
        }

        public String getRepositoryClassFilePath() {
            String repositoryPath = GeneratorHelper.generatorPath(generator.getBasePath(), GeneratorHelper.packageConvertToPath(generator.getRepositoryPackage()));
            return repositoryPath + repositoryJavaType.getShortNameWithoutTypeArguments() + ".java";
        }

        public String getDTOClassFilePath() {
            String dtoPath = GeneratorHelper.generatorPath(generator.getBasePath(), GeneratorHelper.packageConvertToPath(generator.getDtoPackage()));
            return dtoPath + dtoJavaType.getShortNameWithoutTypeArguments() + ".java";
        }

        public String getDtoMapperClassFilePath() {
            String dtoMapperPath = GeneratorHelper.generatorPath(generator.getBasePath(), GeneratorHelper.packageConvertToPath(generator.getDtoMapperPackage()));
            return dtoMapperPath + dtoMapperJavaType.getShortNameWithoutTypeArguments() + ".java";
        }

        public String getServiceClassFilePath() {
            String servicePath = GeneratorHelper.generatorPath(generator.getBasePath(), GeneratorHelper.packageConvertToPath(generator.getServicePackage()));
            return servicePath + serviceJavaType.getShortNameWithoutTypeArguments() + ".java";
        }

        public String getServiceImplClassFilePath() {
            String serviceImplPath = GeneratorHelper.generatorPath(generator.getBasePath(), GeneratorHelper.packageConvertToPath(generator.getServiceImplPackage()));
            return serviceImplPath + serviceImplJavaType.getShortNameWithoutTypeArguments() + ".java";
        }

        public String getResourceClassFilePath() {
            String resourcePath = GeneratorHelper.generatorPath(generator.getBasePath(), GeneratorHelper.packageConvertToPath(generator.getResourcePackage()));
            return resourcePath + resourceJavaType.getShortNameWithoutTypeArguments() + ".java";
        }

        public String getMapperXmlFilePath() {
            String resourcePath = GeneratorHelper.generatorPath("/Users/linjianwei/Documents/javaprojects/casvision/", generator.getRepositoryXmlPathRela());
            return resourcePath + domainJavaType.getShortNameWithoutTypeArguments() + "Mapper.xml";
        }

        public String getDomainName() {
            return domainName;
        }

        public FullyQualifiedJavaType getDomainJavaType() {
            return domainJavaType;
        }

        public FullyQualifiedJavaType getRepositoryJavaType() {
            return repositoryJavaType;
        }

        public FullyQualifiedJavaType getDtoJavaType() {
            return dtoJavaType;
        }

        public FullyQualifiedJavaType getDtoMapperJavaType() {
            return dtoMapperJavaType;
        }

        public FullyQualifiedJavaType getServiceJavaType() {
            return serviceJavaType;
        }

        public FullyQualifiedJavaType getServiceImplJavaType() {
            return serviceImplJavaType;
        }

        public FullyQualifiedJavaType getResourceJavaType() {
            return resourceJavaType;
        }
    }
}
