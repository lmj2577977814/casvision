package cn.casair.web.rest;

import cn.casair.task.VideoRecordTaskHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: liu
 * @Description: 视频录像相关测试
 * @Date: Create in 上午11:42 18-11-8
 * @Modified:
 */
@RestController
@RequestMapping("/api/task")
public class TaskTestController {

    private final Logger logger = LoggerFactory.getLogger(TaskTestController.class);

    @Autowired
    private VideoRecordTaskHandler videoRecordTaskHandler;

    @GetMapping("/test-task")
    public String testTask() throws Exception {
        logger.info("测试视频录像移动功能");
        videoRecordTaskHandler.vrecoderFileToFolder();
        return "成功";

    }









}
