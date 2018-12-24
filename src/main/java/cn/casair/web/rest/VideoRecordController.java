package cn.casair.web.rest;

import cn.casair.service.VideoRecordService;
import cn.casair.service.dto.VideoRecordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: liu
 * @Description:
 * @Date: Create in 上午10:44 18-11-12
 * @Modified:
 */
@RestController
@RequestMapping("/api/video-record")
public class VideoRecordController {

    private final Logger logger = LoggerFactory.getLogger(VideoRecordController.class);

    @Autowired
    private VideoRecordService videoRecordService;

    /**
     * 获取某个通道A,在某一天的视频录像
     * @param videoRecordDTO
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/getByChannelAndTime")
    public ResponseEntity<List<VideoRecordDTO>> getVrByChannelAndTime(@RequestBody VideoRecordDTO videoRecordDTO) throws Exception {

        logger.info("获取通道:" + videoRecordDTO.getChannelName() + ",下" + videoRecordDTO.getStartTime() + "的视频录像");
        List<VideoRecordDTO> videoRecordDTOS = videoRecordService.getVideoInfoList(videoRecordDTO.getChannelName(),videoRecordDTO.getStartTime());
        return new ResponseEntity<>(videoRecordDTOS, HttpStatus.OK);
    }

    /**
     * 获取某个通道A下的录像日期列表
     * @param channelName
     * @return
     */
    @GetMapping("/{channelName}")
    public ResponseEntity<List<String>> getVideoRecordList(@PathVariable("channelName") String channelName){
        logger.info("获取通道:" + channelName + ",下的视频录像日期列表");
        List<String> recordList = videoRecordService.getVideoRecordList(channelName);
        return ResponseEntity.ok(recordList);

    }























}
