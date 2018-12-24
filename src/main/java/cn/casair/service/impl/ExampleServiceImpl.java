package cn.casair.service.impl;

import cn.casair.domain.Example;
import cn.casair.repository.ExampleRepository;
import cn.casair.service.ExampleService;
import cn.casair.service.dto.ExampleDTO;
import cn.casair.service.mapper.ExampleMapper;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExampleServiceImpl implements ExampleService {

    private final Logger log = LoggerFactory.getLogger(ExampleServiceImpl.class);

    private final ExampleRepository exampleRepository;

    private final ExampleMapper exampleMapper;

    public ExampleServiceImpl(ExampleRepository exampleRepository, ExampleMapper exampleMapper) {
        this.exampleRepository = exampleRepository;
        this.exampleMapper = exampleMapper;
    }

    @Override
    public void add(ExampleDTO exampleDTO) {
        log.debug("添加{}", exampleDTO);
        Example example = exampleMapper.toEntity(exampleDTO);
        exampleRepository.insert(example);
    }

    @Override
    public void addSelective(ExampleDTO exampleDTO) {
        log.debug("添加(动态){}", exampleDTO);
        Example example = exampleMapper.toEntity(exampleDTO);
        exampleRepository.insertSelective(example);
    }

    @Override
    public void addAll(List<ExampleDTO> exampleDTOs) {
        log.debug("批量添加{}", exampleDTOs);
        List<Example> examples = exampleMapper.toEntity(exampleDTOs);
        exampleRepository.insertAll(examples);
    }

    @Override
    public void edit(ExampleDTO exampleDTO) {
        log.debug("编辑{}", exampleDTO);
        Example example = exampleMapper.toEntity(exampleDTO);
        exampleRepository.updateById(example);
    }

    @Override
    public void editSelective(ExampleDTO exampleDTO) {
        log.debug("编辑(动态){}", exampleDTO);
        Example example = exampleMapper.toEntity(exampleDTO);
        exampleRepository.updateByIdSelective(example);
    }

    @Override
    public void save(ExampleDTO exampleDTO) {
        log.debug("保存{}", exampleDTO);
        if(exampleDTO.getId() != null) {
            this.edit(exampleDTO);
        } else {
            this.add(exampleDTO);
        }
    }

    @Override
    public Optional<ExampleDTO> findOneById(Long id) {
        log.debug("根据id查询One{}", id);
        return Optional.ofNullable(exampleMapper.toDto(exampleRepository.selectOneById(id)));
    }

    @Override
    public Optional<ExampleDTO> findOneByObject(ExampleDTO exampleDTO) {
        log.debug("根据对象查询One{}", exampleDTO);
        return Optional.ofNullable(exampleMapper.toDto(exampleRepository.selectOneByObject(exampleMapper.toEntity(exampleDTO))));
    }

    @Override
    public List<ExampleDTO> findAllByObject(ExampleDTO exampleDTO) {
        log.debug("根据对象查询All{}", exampleDTO);
        return exampleRepository.selectAllByObject(exampleMapper.toEntity(exampleDTO))
                .stream()
                .map(exampleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExampleDTO> findAll() {
        log.debug("查询All");
        return exampleRepository.selectAll()
                .stream()
                .map(exampleMapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public PageInfo<ExampleDTO> findPage(ExampleDTO exampleDTO, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        List<Example> examples = exampleRepository.selectAllByObject(exampleMapper.toEntity(exampleDTO));
        //todo:此处必须先使用examples对象，因为该对象不是单纯的List对象，而是pagehelper实现的ArrayList的子类对象Page，新建pageInfo时会将额外信息复制到PageInfo里
        PageInfo pageInfo = new PageInfo(examples);
        List<ExampleDTO> exampleDTOS = examples
                .stream()
                .map(exampleMapper::toDto)
                .collect(Collectors.toList());
        pageInfo.setList(exampleDTOS);

        return pageInfo;
    }

    @Override
    public long count(ExampleDTO exampleDTO) {
        log.debug("count");
        return exampleRepository.count(exampleMapper.toEntity(exampleDTO));
    }

    @Override
    public long countAll() {
        log.debug("countALL");
        return exampleRepository.countAll();
    }

    @Override
    public void deleteById(Long id) {
        log.debug("根据id删除{}", id);
        exampleRepository.deleteById(id);
    }

    @Override
    public void deleteByObject(ExampleDTO exampleDTO) {
        log.debug("根据对象删除{}", exampleDTO);
        exampleRepository.deleteByObject(exampleMapper.toEntity(exampleDTO));
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        log.debug("根据ids删除{}", ids);
        exampleRepository.deleteByIds(ids);
    }
}
