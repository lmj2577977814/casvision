package cn.casair.service;

import cn.casair.service.dto.ExampleDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Optional;

public interface ExampleService {

    void add(ExampleDTO exampleDTO);

    void addSelective(ExampleDTO exampleDTO);

    void addAll(List<ExampleDTO> exampleDTOs);

    void edit(ExampleDTO exampleDTO);

    void editSelective(ExampleDTO exampleDTO);

    void save(ExampleDTO exampleDTO);

    void deleteById(Long id);

    void deleteByObject(ExampleDTO exampleDTO);

    void deleteByIds(List<Long> ids);

    Optional<ExampleDTO> findOneById(Long id);

    Optional<ExampleDTO> findOneByObject(ExampleDTO exampleDTO);

    List<ExampleDTO> findAllByObject(ExampleDTO exampleDTO);

    List<ExampleDTO> findAll();

    PageInfo<ExampleDTO> findPage(ExampleDTO exampleDTO, int pageNumber, int pageSize);

    long count(ExampleDTO exampleDTO);

    long countAll();
}