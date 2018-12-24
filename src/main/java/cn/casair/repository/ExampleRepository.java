package cn.casair.repository;

import cn.casair.domain.Example;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ExampleRepository {

    void insert(Example example);

    void insertSelective(Example example);

    void insertAll(List<Example> examples);

    void deleteById(Long id);

    void deleteByObject(Example example);

    void deleteByIds(List<Long> ids);

    void updateById(Example example);

    void updateByIdSelective(Example example);

    Example selectOneById(Long id);

    Example selectOneByObject(Example example);

    List<Example> selectAllByObject(Example example);

    List<Example> selectAll();

    long count(Example example);

    long countAll();


  /*  @Insert("insert example(name,created_by, created_date, last_modified_by,last_modified_date) values(#{name},#{createdBy},#{createdDate},#{lastModifiedBy},#{lastModifiedDate})")
    void insert(Example example);

    @Insert("update test_domain set name = #{name}, last_modified_by = #{lastModifiedBy}, last_modified_date = #{lastModifiedDate} where id = #{id}")
    void update(Example example);

    @Select("select * from test_domain")
    List<Example> selectAll();

    @Delete("delete from test_domain where id = #{id}")
    void deleteById(Long id);*/
}
