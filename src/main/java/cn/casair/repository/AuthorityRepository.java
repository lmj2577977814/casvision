package cn.casair.repository;

import cn.casair.domain.Authority;

import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Authority entity.
 */
@Repository
@Mapper
public interface AuthorityRepository{

    @Insert("insert into jhi_authority(name) values(#{name})")
    void insert(Authority authority);

    @Delete("delete from jhi_authority where name=#{name}")
    void deleteById(int name);

    @Select("select * from jhi_authority where name = #{name}")
    Authority findById(String name);

    @Select("select * from jhi_authority")
    List<Authority> findAll();
}
