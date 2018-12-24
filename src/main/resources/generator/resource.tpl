package ${packageName};

<#list imports as import>
import ${import};
</#list>

@RestController
@RequestMapping("/api")
public class ${domainClassName}Resource {

    private final Logger log = LoggerFactory.getLogger(${domainClassName}Resource.class);

    private final ${domainClassName}Service ${domainParameterName}Service;


    public ${domainClassName}Resource(${domainClassName}Service ${domainParameterName}Service) {
        this.${domainParameterName}Service = ${domainParameterName}Service;
    }

    /**
     * 添加示例
     * @param ${domainParameterName}DTO
     * @return
     */
    @PostMapping(value = "/${restfulUrl}")
    @Timed
    public ResponseEntity<${domainClassName}DTO> add(@Valid @RequestBody ${domainClassName}DTO ${domainParameterName}DTO) {
        log.debug("添加示例 ${domainClassName}: {}", ${domainParameterName}DTO);
        ${domainClassName}DTO result = ${domainParameterName}Service.add(${domainParameterName}DTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 修改示例
     * @param ${domainParameterName}DTO
     * @return
     */
    @PutMapping(value = "/${restfulUrl}")
    @Timed
    public ResponseEntity<${domainClassName}DTO> edit(@Valid @RequestBody ${domainClassName}DTO ${domainParameterName}DTO) {
        log.debug("修改示例 ${domainClassName}:{}", ${domainParameterName}DTO);
        ${domainClassName}DTO result = ${domainParameterName}Service.edit(${domainParameterName}DTO);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除示例
     * @param ${primaryKeyParameterName}
     * @return
     */
    @DeleteMapping(value = "/${restfulUrl}/{${primaryKeyParameterName}}")
    @Timed
    public ResponseEntity<Void> delete(@PathVariable ${primaryKeyType} ${primaryKeyParameterName}) {
        log.debug("删除示例 ${domainClassName}:{}", ${primaryKeyParameterName});
        ${domainParameterName}Service.deleteById(${primaryKeyParameterName});
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除示例
     * @param ${primaryKeyParameterName}s
     * @return
     */
    @PostMapping(value = "/${restfulUrl}/deletes")
    @Timed
    public ResponseEntity<Void> deleteBatch(@RequestBody List<${primaryKeyType}> ${primaryKeyParameterName}s) {
        log.debug("批量删除示例${domainClassName}:{}", ${primaryKeyParameterName}s);
        ${domainParameterName}Service.deleteByIds(${primaryKeyParameterName}s);
        return ResponseEntity.ok().build();
    }

    /**
     * 查询示例
     * @param ${primaryKeyParameterName}
     * @return
     */
    @GetMapping(value = "/${restfulUrl}/{id}")
    @Timed
    public ResponseEntity<${domainClassName}DTO> findOne(@PathVariable ${primaryKeyType} ${primaryKeyParameterName}) {
        log.debug("查询示例${domainClassName}:{}", ${primaryKeyParameterName});

        ${domainClassName}DTO ${domainParameterName}DTO = ${domainParameterName}Service.findOneById(${primaryKeyParameterName}).get();
        return ResponseEntity.ok(${domainParameterName}DTO);
    }

    /**
     * 条件查询示例
     * @return
     */
    @GetMapping(value = "/${restfulUrl}")
    @Timed
    public ResponseEntity<List<${domainClassName}DTO>> findAll(${domainClassName}DTO ${domainParameterName}DTO) {
        log.debug("条件查询示例${domainClassName}:{}", ${domainParameterName}DTO);

        List<${domainClassName}DTO> ${domainParameterName}DTOs = ${domainParameterName}Service.findAllByObject(${domainParameterName}DTO);
        return ResponseEntity.ok(${domainParameterName}DTOs);
    }

    /**
     * 分页查询示例
     * @param ${domainParameterName}DTO
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/${restfulUrl}/page",params = {"pageNumber", "pageSize"})
    @Timed
    public ResponseEntity<PageInfo> findAllPage(@Valid @RequestBody ${domainClassName}DTO ${domainParameterName}DTO,@RequestParam int pageNumber, @RequestParam int pageSize) {
        log.debug("分页查询示例${domainClassName}:{}", ${domainParameterName}DTO, pageNumber, pageSize);

        PageInfo<${domainClassName}DTO> pageInfo = ${domainParameterName}Service.findPage(${domainParameterName}DTO, pageNumber, pageSize);
        return ResponseEntity.ok(pageInfo);
    }
}
