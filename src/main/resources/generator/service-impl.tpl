package ${packageName};

<#list imports as import>
import ${import};
</#list>

@Service
@Transactional
public class ${className} implements ${iClassName}  {

    private final Logger log = LoggerFactory.getLogger(${domainClassName}ServiceImpl.class);

    private final ${domainClassName}Repository ${domainParameterName}Repository;

    private final ${domainClassName}Mapper ${domainParameterName}Mapper;

    public ${domainClassName}ServiceImpl(${domainClassName}Repository ${domainParameterName}Repository, ${domainClassName}Mapper ${domainParameterName}Mapper) {
        this.${domainParameterName}Repository = ${domainParameterName}Repository;
        this.${domainParameterName}Mapper = ${domainParameterName}Mapper;
    }

    @Override
    public ${dtoClassName} add(${domainClassName}DTO ${domainParameterName}DTO) {
        log.debug("添加{}", ${domainParameterName}DTO);
        ${domainClassName} ${domainParameterName} = ${domainParameterName}Mapper.toEntity(${domainParameterName}DTO);
        ${domainParameterName}Repository.insert(${domainParameterName});
        return ${domainParameterName}Mapper.toDto(${domainParameterName});
    }

    @Override
    public void addAll(List<${domainClassName}DTO> ${domainParameterName}DTOs) {
        log.debug("批量添加{}", ${domainParameterName}DTOs);
        List<${domainClassName}> ${domainParameterName}s = ${domainParameterName}Mapper.toEntity(${domainParameterName}DTOs);
        ${domainParameterName}Repository.insertAll(${domainParameterName}s);
    }

    @Override
    public ${dtoClassName} edit(${domainClassName}DTO ${domainParameterName}DTO) {
        log.debug("编辑{}", ${domainParameterName}DTO);
        ${domainClassName} ${domainParameterName} = ${domainParameterName}Mapper.toEntity(${domainParameterName}DTO);
        ${domainParameterName}Repository.updateById(${domainParameterName});
        return this.${domainParameterName}Mapper.toDto(this.${domainParameterName}Repository.selectOneById(${domainParameterName}.getId()));
    }

    @Override
    public void save(${domainClassName}DTO ${domainParameterName}DTO) {
        log.debug("保存{}", ${domainParameterName}DTO);
        if(${domainParameterName}DTO.get${primaryKey}() != null) {
            this.edit(${domainParameterName}DTO);
        } else {
            this.add(${domainParameterName}DTO);
        }
    }

    @Override
    public Optional<${domainClassName}DTO> findOneBy${primaryKey}(${primaryKeyType} ${primaryKeyParameterName}) {
        log.debug("根据id查询One{}", ${primaryKeyParameterName});
        return Optional.ofNullable(${domainParameterName}Mapper.toDto(${domainParameterName}Repository.selectOneBy${primaryKey}(${primaryKeyParameterName})));
    }

    @Override
    public Optional<${domainClassName}DTO> findOneByObject(${domainClassName}DTO ${domainParameterName}DTO) {
        log.debug("根据对象查询One{}", ${domainParameterName}DTO);
        return Optional.ofNullable(${domainParameterName}Mapper.toDto(${domainParameterName}Repository.selectOneByObject(${domainParameterName}Mapper.toEntity(${domainParameterName}DTO))));
    }

    @Override
    public List<${domainClassName}DTO> findAllByObject(${domainClassName}DTO ${domainParameterName}DTO) {
        log.debug("根据对象查询All{}", ${domainParameterName}DTO);
        return ${domainParameterName}Repository.selectAllByObject(${domainParameterName}Mapper.toEntity(${domainParameterName}DTO))
                .stream()
                .map(${domainParameterName}Mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<${domainClassName}DTO> findAll() {
        log.debug("查询All");
        return ${domainParameterName}Repository.selectAll()
                .stream()
                .map(${domainParameterName}Mapper::toDto)
                .collect(Collectors.toList());

    }

    @Override
    public PageInfo<${domainClassName}DTO> findPage(${domainClassName}DTO ${domainParameterName}DTO, int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        List<${domainClassName}> ${domainParameterName}s = ${domainParameterName}Repository.selectAllByObject(${domainParameterName}Mapper.toEntity(${domainParameterName}DTO));
        //todo:此处必须先使用${domainParameterName}s对象，因为该对象不是单纯的List对象，而是pagehelper实现的ArrayList的子类对象Page，新建pageInfo时会将额外信息复制到PageInfo里
        PageInfo pageInfo = new PageInfo(${domainParameterName}s);
        List<${domainClassName}DTO> ${domainParameterName}DTOS = ${domainParameterName}s
                .stream()
                .map(${domainParameterName}Mapper::toDto)
                .collect(Collectors.toList());
        pageInfo.setList(${domainParameterName}DTOS);

        return pageInfo;
    }

    @Override
    public long count(${domainClassName}DTO ${domainParameterName}DTO) {
        log.debug("count");
        return ${domainParameterName}Repository.count(${domainParameterName}Mapper.toEntity(${domainParameterName}DTO));
    }

    @Override
    public long countAll() {
        log.debug("countALL");
        return ${domainParameterName}Repository.countAll();
    }

    @Override
    public void deleteBy${primaryKey}(${primaryKeyType} ${primaryKeyParameterName}) {
        log.debug("根据id删除{}", ${primaryKeyParameterName});
        ${domainParameterName}Repository.deleteById(${primaryKeyParameterName});
    }

    @Override
    public void deleteByObject(${domainClassName}DTO ${domainParameterName}DTO) {
        log.debug("根据对象删除{}", ${domainParameterName}DTO);
        ${domainParameterName}Repository.deleteByObject(${domainParameterName}Mapper.toEntity(${domainParameterName}DTO));
    }

    @Override
    public void deleteBy${primaryKey}s(List<${primaryKeyType}> ${primaryKeyParameterName}s) {
        log.debug("根据${primaryKey}s删除{}", ${primaryKeyParameterName}s);
        ${domainParameterName}Repository.deleteBy${primaryKey}s(${primaryKeyParameterName}s);
    }
}
