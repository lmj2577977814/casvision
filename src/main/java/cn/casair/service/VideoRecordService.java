package cn.casair.service;

import cn.casair.service.dto.VideoRecordDTO;

import java.text.ParseException;
import java.util.List;

/**
 * @Author: liu
 * @Description: 视频录像相关
 * @Date: Create in 下午1:44 18-11-9
 * @Modified:
 */
public interface VideoRecordService {

    List<String> getVideoRecordList (String channelName);

    List<VideoRecordDTO> getVideoInfoList(String channelName, String dateStr) throws Exception;

















}
