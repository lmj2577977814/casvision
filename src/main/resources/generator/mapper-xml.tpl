<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${fullRepositoryClassName}">
    <sql id="Base_Column_List">
     <#list vars as varr>${varr["column"]}<#if varr_has_next==true> ,</#if></#list>
    </sql>

    <resultMap id="BaseResultMap" type="${fullDomainClassName}">
    <#list vars as varr>
    <#if varr["isPrimaryKey"]==true>
       <id column="${varr["column"]}" jdbcType="${varr["jdbcType"]}" property="${varr["property"]}" />
    <#else>
       <result column="${varr["column"]}" jdbcType="${varr["jdbcType"]}" property="${varr["property"]}" />
    </#if>
    </#list>
    </resultMap>

    <insert id="insert" parameterType="${fullDomainClassName}" useGeneratedKeys="true" keyProperty="id">
        insert into ${tableName} (
        <#list vars as varr>
           ${varr["column"]}<#if varr_has_next==true>,</#if>
        </#list>
        )
        values (
        <#list vars as varr>
          ${r'#'}{${varr["property"]},jdbcType=${varr["jdbcType"]}}<#if varr_has_next==true>,</#if>
        </#list>
        )
    </insert>

    <insert id="insertAll" useGeneratedKeys="true" keyProperty="id">
        insert into ${tableName} (
            <#list vars as varr>
              ${varr["column"]}<#if varr_has_next==true>,</#if>
            </#list>
        ) values
        <foreach close="" collection="list" index="index" item="item" open="" separator=",">
            (
             <#list vars as varr>
               ${r'#'}{item.${varr["property"]},jdbcType=${varr["jdbcType"]}}<#if varr_has_next==true>,</#if>
             </#list>
            )
        </foreach>
    </insert>

    <delete id="deleteBy${primaryKeyProperty}" parameterType="${primaryKeyJavaType}">
        delete from ${tableName}
        where ${primaryKeyColumn} = ${r'#'}{${primaryKeyVariable},jdbcType=${primaryKeyJdbcType}}
    </delete>

    <delete id="deleteByObject" parameterType="${fullDomainClassName}">
        delete from ${tableName}
        <where>
         <#list vars as varr>
         <if test="${varr["property"]} != null">
            and ${varr["column"]} = ${r'#'}{${varr["property"]},jdbcType=${varr["jdbcType"]}}
         </if>
         </#list>
        </where>
    </delete>

    <delete id="deleteBy${primaryKeyProperty}s">
        delete from ${tableName}
        where ${primaryKeyColumn} in
        <foreach close=")" collection="list" index="index" item="item" open="(" separator=",">
            ${r'#'}{item,jdbcType=${primaryKeyJdbcType}}
        </foreach>
    </delete>

    <delete id="deleteAll">
       delete from ${tableName}
    </delete>

    <update id="updateBy${primaryKeyProperty}" parameterType="${fullDomainClassName}">
        update ${tableName}
        <set>
           <#list vars as varr>
           <#if varr["isPrimaryKey"]!=true>
            <if test="${varr["property"]} != null">
             ${varr["column"]} = ${r'#'}{${varr["property"]},jdbcType=${varr["jdbcType"]}},
            </if>
            </#if>
           </#list>
        </set>
        where ${primaryKeyColumn} = ${r'#'}{${primaryKeyVariable},jdbcType=${primaryKeyJdbcType}}
    </update>

    <select id="selectOneBy${primaryKeyProperty}" parameterType="${primaryKeyJavaType}" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List" />
        from ${tableName}
        where ${primaryKeyColumn} = ${r'#'}{${primaryKeyVariable},jdbcType=${primaryKeyJdbcType}}
    </select>

    <select id="selectOneByObject" parameterType="${fullDomainClassName}" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List" />
        from ${tableName}
        <where>
         <#list vars as varr>
         <if test="${varr["property"]} != null">
         and ${varr["column"]} = ${r'#'}{${varr["property"]},jdbcType=${varr["jdbcType"]}}
         </if>
         </#list>
        </where>
    </select>

    <select id="selectAllByObject" parameterType="${fullDomainClassName}" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List" />
        from ${tableName}
        <where>
         <#list vars as varr>
         <if test="${varr["property"]} != null">
         and ${varr["column"]} = ${r'#'}{${varr["property"]},jdbcType=${varr["jdbcType"]}}
         </if>
         </#list>
        </where>
    </select>

    <select id="selectAll" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List" />
        from ${tableName}
    </select>

    <select id="count" parameterType="${fullDomainClassName}" resultType="long">
        select count(1)
        from ${tableName}
        <where>
       <#list vars as varr>
       <if test="${varr["property"]} != null">
       and ${varr["column"]} = ${r'#'}{${varr["property"]},jdbcType=${varr["jdbcType"]}}
       </if>
       </#list>
        </where>
    </select>

    <select id="countAll" resultType="long">
        select count(1)
        from ${tableName}
  </select>

</mapper>