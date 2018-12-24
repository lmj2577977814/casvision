package cn.casair.web.rest;

import cn.casair.service.ExampleService;
import cn.casair.service.dto.ExampleDTO;
import com.codahale.metrics.annotation.Timed;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ExampleResource {

    private final Logger log = LoggerFactory.getLogger(ExampleResource.class);

    private final ExampleService exampleService;


    public ExampleResource(ExampleService exampleService) {
        this.exampleService = exampleService;
    }

    /**
     * 添加示例
     * @param exampleDTO
     * @return
     */
    @PostMapping(value = "/examples")
    @Timed
    public ResponseEntity<ExampleDTO> add(@Valid @RequestBody ExampleDTO exampleDTO) {
        log.debug("添加示例 Example: {}", exampleDTO);
        exampleService.addSelective(exampleDTO);
        return ResponseEntity.ok(exampleDTO);
    }

    /**
     * 修改示例
     * @param exampleDTO
     * @return
     */
    @PutMapping(value = "/examples")
    @Timed
    public ResponseEntity<ExampleDTO> edit(@Valid @RequestBody ExampleDTO exampleDTO) {
        log.debug("修改示例 Example:{}", exampleDTO);
        exampleService.editSelective(exampleDTO);
        return ResponseEntity.ok(exampleDTO);
    }

    /**
     * 删除示例
     * @param id
     * @return
     */
    @DeleteMapping(value = "/examples/{id}")
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("删除示例 Example:{}", id);
        exampleService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 批量删除示例
     * @param ids
     * @return
     */
    @PostMapping(value = "/examples/deletes")
    @Timed
    public ResponseEntity<Void> deleteBatch(@RequestBody List<Long> ids) {
        log.debug("批量删除示例Example:{}", ids);
        exampleService.deleteByIds(ids);
        return ResponseEntity.ok().build();
    }

    /**
     * 查询示例
     * @param id
     * @return
     */
    @GetMapping(value = "/examples/{id}")
    @Timed
    public ResponseEntity<ExampleDTO> findOne(@PathVariable Long id) {
        log.debug("查询示例Example:{}", id);

        ExampleDTO exampleDTO = exampleService.findOneById(id).get();
        return ResponseEntity.ok(exampleDTO);
    }

    /**
     * 条件查询示例
     * @return
     */
    @GetMapping(value = "/examples")
    @Timed
    public ResponseEntity<List<ExampleDTO>> findAll(ExampleDTO exampleDTO) {
        log.debug("条件查询示例Example:{}", exampleDTO);

        List<ExampleDTO> exampleDTOs = exampleService.findAllByObject(exampleDTO);
        return ResponseEntity.ok(exampleDTOs);
    }

    /**
     * 分页查询示例
     * @param exampleDTO
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/examples/page",params = {"pageNumber", "pageSize"})
    @Timed
    public ResponseEntity<PageInfo> findAllPage(@Valid @RequestBody ExampleDTO exampleDTO,@RequestParam int pageNumber, @RequestParam int pageSize) {
        log.debug("分页查询示例Example:{}", exampleDTO, pageNumber, pageSize);

        PageInfo<ExampleDTO> pageInfo = exampleService.findPage(exampleDTO, pageNumber, pageSize);
        return ResponseEntity.ok(pageInfo);
    }
}
