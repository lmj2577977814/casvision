package cn.casair.task;

import cn.casair.web.rest.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.Date;

/**
 * @Author: liu
 * @Description:
 * @Date: Create in 下午5:28 18-11-7
 * @Modified:
 */
@Component
public class VideoRecordTaskHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** 存放历史监控视频 */
//    @Value("/data/tmp")
    @Value("/usr/liu/test")
    private String historyPath;

    /** 存放今天的监控视频 */
//    @Value("/data/record")
    @Value("/usr/liu/work/nginx-rtmp-new/nginx-1.10.3/www/tmp")
    private String curDayPath;

//    private String fileNameTemplate = "yyyy-MM-dd-HH_mm_ss";

    private String fileNameTemplate = "yyyyMMddHHmmss";

    /**
     * 将监控录像按天归类到不同的文件夹
     */
//    @Scheduled(fixedRate = 10000)
    public void vrecoderFileToFolder() throws Exception {
        logger.info("将监控录像按天归类到不同的文件夹：" + System.currentTimeMillis());
        File file = new File(curDayPath);
        if(!file.exists()) {
            throw new Exception("路径" + curDayPath + "不存在");
        }
        File[] files = file.listFiles();
//        logger.info("文件夹位置:" + curDayPath + "," + file.getPath());
        logger.info("文件夹内文件个数:" + files.length);
        for(int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();

//            logger.info("文件名:" + fileName);
            if(!fileName.endsWith(".flv")){continue;}
            /** 文件格式为xx-日期 */
            String proName = fileName.substring(0,fileName.indexOf("-"));
            String fileDate = fileName.substring(fileName.indexOf("-") + 1,fileName.lastIndexOf("."));
            Date d ;
            try {
                d = DateUtils.strToDate(fileDate,fileNameTemplate);
            } catch (ParseException e) {
                throw new Exception("文件:" + fileName + " 的日期格式无法解析");
            }
            String fileDateStr = DateUtils.dateToStr(d);
            String dateStr = DateUtils.dateToStr(new Date());
            /** 判断是否为当天的监控录像 */
            if(!fileDateStr.startsWith(dateStr)) {
                /** 判断历史录像文件夹下是否有对应日期的文件夹 */
                String folderName = proName + "/" + fileDateStr;
                File historyFile = new File(historyPath + "/" + folderName);
                if(!historyFile.exists()) {
                    historyFile.mkdirs();
                }
                Path fromPath = Paths.get(curDayPath + "/" + fileName);
                Path toPath = Paths.get(historyPath + "/" + folderName+ "/" +fileName.substring(fileName.indexOf("-") + 1));
                try {
                    Files.move(fromPath,toPath,StandardCopyOption.ATOMIC_MOVE);
                } catch (IOException e) {
                    logger.error("文件:" + fileName + "移动失败");
                    e.printStackTrace();
                }

            }


        }

    }



}
