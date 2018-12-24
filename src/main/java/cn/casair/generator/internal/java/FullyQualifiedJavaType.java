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
package cn.casair.generator.internal.java;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Java类型封装类
 * @代码来源: mybatis-generator-plugin
 */
public class FullyQualifiedJavaType implements Comparable<FullyQualifiedJavaType> {
    
    /** The Constant JAVA_LANG. */
    private static final String JAVA_LANG = "java.lang";
    
    /** The int instance. */
    private static FullyQualifiedJavaType intInstance = null;
    
    /** The string instance. */
    private static FullyQualifiedJavaType stringInstance = null;
    
    /** The boolean primitive instance. */
    private static FullyQualifiedJavaType booleanPrimitiveInstance = null;
    
    /** The object instance. */
    private static FullyQualifiedJavaType objectInstance = null;
    
    /** The date instance. */
    private static FullyQualifiedJavaType dateInstance = null;
    
    /** The criteria instance. */
    private static FullyQualifiedJavaType criteriaInstance = null;
    
    /** The generated criteria instance. */
    private static FullyQualifiedJavaType generatedCriteriaInstance = null;


    /** The short name without any generic arguments. 基础短名*/
    private String baseShortName;

    /** The fully qualified name without any generic arguments. 基础完整名称 */
    private String baseQualifiedName;

    /** 是否需要明确导入(原始类型不需要显示导入) */
    private boolean explicitlyImported;
    
    /** 包名 */
    private String packageName;

    /** 是否为原始类型 */
    private boolean primitive;
    
    /** 是否为数组类型 */
    private boolean isArray;
    
    /** 原始类型包装对象 */
    private PrimitiveTypeWrapper primitiveTypeWrapper;
    
    /** 类型参数列表 */
    private List<FullyQualifiedJavaType> typeArguments;

    /** 是否为通配符类型 */
    private boolean wildcardType;

    /** 是否为限定下界配符类型 < ? super T > */
    private boolean boundedWildcard;

    /** 是否为限制上界通配符类型 < ? extends T > */
    private boolean extendsBoundedWildcard;

    public FullyQualifiedJavaType(String fullTypeSpecification) {
        super();
        typeArguments = new ArrayList<FullyQualifiedJavaType>();
        parse(fullTypeSpecification);
    }

    /**
     * 解析类型
     * @param fullTypeSpecification
     */
    private void parse(String fullTypeSpecification) {
        String spec = fullTypeSpecification.trim();

        if (spec.startsWith("?")) {
            wildcardType = true;
            spec = spec.substring(1).trim();
            if (spec.startsWith("extends ")) {
                boundedWildcard = true;
                extendsBoundedWildcard = true;
                spec = spec.substring(8);  // "extends ".length()
            } else if (spec.startsWith("super ")) {
                boundedWildcard = true;
                extendsBoundedWildcard = false;
                spec = spec.substring(6);  // "super ".length()
            } else {
                boundedWildcard = false;
            }
            parse(spec);
        } else {
            int index = fullTypeSpecification.indexOf('<');
            if (index == -1) {
                simpleParse(fullTypeSpecification);
            } else {
                simpleParse(fullTypeSpecification.substring(0, index));
                int endIndex = fullTypeSpecification.lastIndexOf('>');
                if (endIndex == -1) {
                    throw new RuntimeException("Invalid Type Specification: " + fullTypeSpecification +".");
                }
                genericParse(fullTypeSpecification.substring(index, endIndex + 1));
            }

            // this is far from a perfect test for detecting arrays, but is close
            // enough for most cases.  It will not detect an improperly specified
            // array type like byte], but it will detect byte[] and byte[   ]
            // which are both valid
            isArray = fullTypeSpecification.endsWith("]");
        }
    }

    /**
     * 简单解析
     * @param typeSpecification
     */
    private void simpleParse(String typeSpecification) {
        baseQualifiedName = typeSpecification.trim();
        if (baseQualifiedName.contains(".")) {
            packageName = getPackage(baseQualifiedName);
            baseShortName = baseQualifiedName
                    .substring(packageName.length() + 1);
            int index = baseShortName.lastIndexOf('.');
            if (index != -1) {
                baseShortName = baseShortName.substring(index + 1);
            }

            if (JAVA_LANG.equals(packageName)) {
                explicitlyImported = false;
            } else {
                explicitlyImported = true;
            }
        } else {
            baseShortName = baseQualifiedName;
            explicitlyImported = false;
            packageName = "";

            if ("byte".equals(baseQualifiedName)) {
                primitive = true;
                primitiveTypeWrapper = PrimitiveTypeWrapper.getByteInstance();
            } else if ("short".equals(baseQualifiedName)) {
                primitive = true;
                primitiveTypeWrapper = PrimitiveTypeWrapper.getShortInstance();
            } else if ("int".equals(baseQualifiedName)) {
                primitive = true;
                primitiveTypeWrapper = PrimitiveTypeWrapper.getIntegerInstance();
            } else if ("long".equals(baseQualifiedName)) {
                primitive = true;
                primitiveTypeWrapper = PrimitiveTypeWrapper.getLongInstance();
            } else if ("char".equals(baseQualifiedName)) {
                primitive = true;
                primitiveTypeWrapper = PrimitiveTypeWrapper.getCharacterInstance();
            } else if ("float".equals(baseQualifiedName)) {
                primitive = true;
                primitiveTypeWrapper = PrimitiveTypeWrapper.getFloatInstance();
            } else if ("double".equals(baseQualifiedName)) {
                primitive = true;
                primitiveTypeWrapper = PrimitiveTypeWrapper.getDoubleInstance();
            } else if ("boolean".equals(baseQualifiedName)) {
                primitive = true;
                primitiveTypeWrapper = PrimitiveTypeWrapper.getBooleanInstance();
            } else {
                primitive = false;
                primitiveTypeWrapper = null;
            }
        }
    }

    /**
     * 一般解析
     * @param genericSpecification
     */
    private void genericParse(String genericSpecification) {
        int lastIndex = genericSpecification.lastIndexOf('>');
        if (lastIndex == -1) {
            // shouldn't happen - should be caught already, but just in case...
            throw new RuntimeException("Invalid Type Specification: " + genericSpecification +".");
        }
        String argumentString = genericSpecification.substring(1, lastIndex);
        // need to find "," outside of a <> bounds
        StringTokenizer st = new StringTokenizer(argumentString, ",<>", true);
        int openCount = 0;
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if ("<".equals(token)) {
                sb.append(token);
                openCount++;
            } else if (">".equals(token)) {
                sb.append(token);
                openCount--;
            } else if (",".equals(token)) {
                if (openCount == 0) {
                    typeArguments.add(new FullyQualifiedJavaType(sb.toString()));
                    sb.setLength(0);
                } else {
                    sb.append(token);
                }
            } else {
                sb.append(token);
            }
        }

        if (openCount != 0) {
            throw new RuntimeException("Invalid Type Specification: " + genericSpecification +".");
        }

        String finalType = sb.toString();
        if (!Strings.isNullOrEmpty(finalType)) {
            typeArguments.add(new FullyQualifiedJavaType(finalType));
        }
    }

    public boolean isExplicitlyImported() {
        return explicitlyImported;
    }

    /**
     * 获取完整类型名
     * @return
     */
    public String getFullyQualifiedName() {
        StringBuilder sb = new StringBuilder();
        if (wildcardType) {
            sb.append('?');
            if (boundedWildcard) {
                if (extendsBoundedWildcard) {
                    sb.append(" extends ");
                } else {
                    sb.append(" super ");
                }

                sb.append(baseQualifiedName);
            }
        } else {
            sb.append(baseQualifiedName);
        }

        if (typeArguments.size() > 0) {
            boolean first = true;
            sb.append('<');
            for (FullyQualifiedJavaType fqjt : typeArguments) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(fqjt.getFullyQualifiedName());

            }
            sb.append('>');
        }

        return sb.toString();
    }

    /**
     * 获取完整类型名(不包含参数)
     * @return
     */
    public String getFullyQualifiedNameWithoutTypeParameters() {
        return baseQualifiedName;
    }

    /**
     * 获取导入包列表
     * @return
     */
    public List<String> getImportList() {
        List<String> answer = new ArrayList<String>();
        if (isExplicitlyImported()) {
            int index = baseShortName.indexOf('.');
            if (index == -1) {
                answer.add(baseQualifiedName);
            } else {
                // an inner class is specified, only import the top
                // level class
                StringBuilder sb = new StringBuilder();
                sb.append(packageName);
                sb.append('.');
                sb.append(baseShortName.substring(0, index));
                answer.add(sb.toString());
            }
        }

        for (FullyQualifiedJavaType fqjt : typeArguments) {
            answer.addAll(fqjt.getImportList());
        }

        return answer;
    }

    /**
     * 获取包名
     * @return
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * 获取简单名
     * @return
     */
    public String getShortName() {
        StringBuilder sb = new StringBuilder();
        if (wildcardType) {
            sb.append('?');
            if (boundedWildcard) {
                if (extendsBoundedWildcard) {
                    sb.append(" extends ");
                } else {
                    sb.append(" super ");
                }

                sb.append(baseShortName);
            }
        } else {
            sb.append(baseShortName);
        }

        if (typeArguments.size() > 0) {
            boolean first = true;
            sb.append('<');
            for (FullyQualifiedJavaType fqjt : typeArguments) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(fqjt.getShortName());

            }
            sb.append('>');
        }

        return sb.toString();
    }

    /**
     * 获取简单名(不包含参数)
     * @return
     */
    public String getShortNameWithoutTypeArguments() {
        return baseShortName;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public PrimitiveTypeWrapper getPrimitiveTypeWrapper() {
        return primitiveTypeWrapper;
    }

    /**
     * Gets the int instance.
     *
     * @return the int instance
     */
    public static final FullyQualifiedJavaType getIntInstance() {
        if (intInstance == null) {
            intInstance = new FullyQualifiedJavaType("int");
        }

        return intInstance;
    }

    /**
     * Gets the new map instance.
     *
     * @return the new map instance
     */
    public static final FullyQualifiedJavaType getNewMapInstance() {
        // always return a new instance because the type may be parameterized
        return new FullyQualifiedJavaType("java.util.Map");
    }

    /**
     * Gets the new list instance.
     *
     * @return the new list instance
     */
    public static final FullyQualifiedJavaType getNewListInstance() {
        // always return a new instance because the type may be parameterized
        return new FullyQualifiedJavaType("java.util.List");
    }

    /**
     * Gets the new hash map instance.
     *
     * @return the new hash map instance
     */
    public static final FullyQualifiedJavaType getNewHashMapInstance() {
        // always return a new instance because the type may be parameterized
        return new FullyQualifiedJavaType("java.util.HashMap");
    }

    /**
     * Gets the new array list instance.
     *
     * @return the new array list instance
     */
    public static final FullyQualifiedJavaType getNewArrayListInstance() {
        // always return a new instance because the type may be parameterized
        return new FullyQualifiedJavaType("java.util.ArrayList");
    }

    /**
     * Gets the new iterator instance.
     *
     * @return the new iterator instance
     */
    public static final FullyQualifiedJavaType getNewIteratorInstance() {
        // always return a new instance because the type may be parameterized
        return new FullyQualifiedJavaType("java.util.Iterator");
    }

    /**
     * Gets the string instance.
     *
     * @return the string instance
     */
    public static final FullyQualifiedJavaType getStringInstance() {
        if (stringInstance == null) {
            stringInstance = new FullyQualifiedJavaType("java.lang.String");
        }

        return stringInstance;
    }

    /**
     * Gets the boolean primitive instance.
     *
     * @return the boolean primitive instance
     */
    public static final FullyQualifiedJavaType getBooleanPrimitiveInstance() {
        if (booleanPrimitiveInstance == null) {
            booleanPrimitiveInstance = new FullyQualifiedJavaType("boolean");
        }

        return booleanPrimitiveInstance;
    }

    /**
     * Gets the object instance.
     *
     * @return the object instance
     */
    public static final FullyQualifiedJavaType getObjectInstance() {
        if (objectInstance == null) {
            objectInstance = new FullyQualifiedJavaType("java.lang.Object");
        }

        return objectInstance;
    }

    /**
     * Gets the date instance.
     *
     * @return the date instance
     */
    public static final FullyQualifiedJavaType getDateInstance() {
        if (dateInstance == null) {
           // dateInstance = new FullyQualifiedJavaType("java.util.Date");
            dateInstance = new FullyQualifiedJavaType("java.time.Instant");
        }

        return dateInstance;
    }

    /**
     * Gets the criteria instance.
     *
     * @return the criteria instance
     */
    public static final FullyQualifiedJavaType getCriteriaInstance() {
        if (criteriaInstance == null) {
            criteriaInstance = new FullyQualifiedJavaType("Criteria");
        }

        return criteriaInstance;
    }

    /**
     * Gets the generated criteria instance.
     *
     * @return the generated criteria instance
     */
    public static final FullyQualifiedJavaType getGeneratedCriteriaInstance() {
        if (generatedCriteriaInstance == null) {
            generatedCriteriaInstance = new FullyQualifiedJavaType("GeneratedCriteria");
        }

        return generatedCriteriaInstance;
    }

    public int compareTo(FullyQualifiedJavaType other) {
        return getFullyQualifiedName().compareTo(other.getFullyQualifiedName());
    }

    public void addTypeArgument(FullyQualifiedJavaType type) {
        typeArguments.add(type);
    }

    public boolean isArray() {
        return isArray;
    }

    public List<FullyQualifiedJavaType> getTypeArguments() {
        return typeArguments;
    }

    private static String getPackage(String baseQualifiedName) {
        int index = baseQualifiedName.lastIndexOf('.');
        return baseQualifiedName.substring(0, index);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FullyQualifiedJavaType)) {
            return false;
        }

        FullyQualifiedJavaType other = (FullyQualifiedJavaType) obj;

        return getFullyQualifiedName().equals(other.getFullyQualifiedName());
    }

    @Override
    public int hashCode() {
        return getFullyQualifiedName().hashCode();
    }

    @Override
    public String toString() {
        return getFullyQualifiedName();
    }
}
