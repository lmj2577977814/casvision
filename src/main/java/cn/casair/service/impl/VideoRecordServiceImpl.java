package cn.casair.service.impl;

import cn.casair.service.VideoRecordService;
import cn.casair.service.dto.VideoRecordDTO;
import cn.casair.web.rest.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: liu
 * @Description:
 * @Date: Create in 下午3:29 18-11-9
 * @Modified:
 */
@Service
public class VideoRecordServiceImpl implements VideoRecordService{

    /** 存放历史监控视频 */
//    @Value("/data/tmp")
    @Value("/usr/liu/test")
    private String historyPath;

    /** 存放今天的监控视频 */
//    @Value("/data/record")
    @Value("/usr/liu/work/nginx-rtmp-new/nginx-1.10.3/www/tmp")
    private String curDayPath;

    private Logger logger = LoggerFactory.getLogger(VideoRecordServiceImpl.class);


    /**
     * 获取某一个通道A的录像视频日期列表
     * @return
     * @throws Exception
     */
    @Override
    public List<String> getVideoRecordList(String channelName) {
        /** 获取通道A的历史录像视频日期 */
        File hPath = new File(historyPath + "/" + channelName);
        List<String> dateListStr = new ArrayList<>();
        if(hPath.exists()) {
            File[] files = hPath.listFiles();
            List<File> fileList = getDateList(Arrays.asList(files));
            for(File file: fileList) {
                String name = file.getName();
                dateListStr.add(name);
            }

        }

        return dateListStr;
    }

    /**
     * 获取某个通道A在某一天的录像
     * @param channelName 通道名称
     * @param dateStr 日期,需要格式为yyyy-MM-dd
     * @return
     */
    @Override
    public List<VideoRecordDTO> getVideoInfoList(String channelName, String dateStr) throws Exception {
        /** 判断是否为请求当天的录像回放 */
        String curDay = DateUtils.dateToStr(new Date());

        /** 定义存放视频列表 */
        List<File> fileList = new ArrayList<>();
        List<VideoRecordDTO> videoRecordDTOS = new ArrayList<>();

        /** 当天的录像 */
        if(curDay.equals(dateStr)) {
            File file = new File(curDayPath);
            if(file.exists()) {
                File[] files = file.listFiles();
                /** 获取出当天通道的视频列表 */
                for(File f: files) {
                    String fileName = f.getName();
                    if(fileName.startsWith(channelName)) {
                        fileList.add(f);
                    }
                }
            }
        }else {
            File hfile =new File(historyPath + "/" + channelName + "/" + dateStr);
            if(hfile.exists()) {
                fileList = Arrays.asList(hfile.listFiles());
            }
        }
        /** 获取排序后的文件列表 */
        List<File> fList = getFileSort(fileList);
        for(File f: fList) {
            /** 获取文件字节数 */
            long length = f.length();
            String fileName = f.getName();
            Date date = DateUtils.strToDate(fileName.substring(0, fileName.lastIndexOf(".")), "yyyyMMddHHmmss");
            /** 获取开始时间 */
            String startTime = DateUtils.dateToStr(date, "yyyy-MM-dd HH:mm:ss");
            int duration = getVideoTime(f.getPath());
            VideoRecordDTO videoRecordDTO = new VideoRecordDTO();
            videoRecordDTO.setVideoRecordBytes(length);
            videoRecordDTO.setVideoRecordDuration(String.valueOf(duration));
            videoRecordDTO.setVideoRecordStartTime(startTime);
            videoRecordDTO.setFilename(f.getName());
            videoRecordDTO.setVideoRecordUrl("测试url");
            videoRecordDTOS.add(videoRecordDTO);
        }

        return videoRecordDTOS;
    }


    /**
     *  获取某个目录下所有的文件按照日期排序
     * @param list
     * @return
     */
    public static List<File> getFileSort(List<File> list) {

//        List<File> list = getFiles(path, new ArrayList<File>());

        if (list != null && list.size() > 0) {

            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() > newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }

                }
            });

        }

        return list;
    }

    /**
     *
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {

        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }


    /**
     * 获取视频总时间
     */
    private int getVideoTime(String video_path) {
        List<String> commands = new ArrayList<>();
        commands.add("ffmpeg");
        commands.add("-i");
        commands.add(video_path);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            Process p = builder.start();

            //从输入流中读取视频信息
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            br.close();

            //从视频信息中解析时长
            String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
            Pattern pattern = Pattern.compile(regexDuration);
            Matcher m = pattern.matcher(stringBuilder.toString());
            if (m.find()) {
                int time = getTimelen(m.group(1));
                return time;
//                System.out.println("视频时长：" + time + "s , 开始时间：" + m.group(2) + ", 比特率：" + m.group(3) + "kb/s");
            }


//            String regexVideo = "Video: (.*?), (.*?), (.*?)[,\\s]";
//            pattern = Pattern.compile(regexVideo);
//            m = pattern.matcher(stringBuilder.toString());
//            if (m.find()) {
//                System.out.println("编码格式：" + m.group(1) + ", 视频格式：" + m.group(2) + ", 分辨率：" + m.group(3) + "kb/s");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 格式:"00:00:10.68"
    private int getTimelen(String timelen) {
        int min = 0;
        String strs[] = timelen.split(":");
        if (strs[0].compareTo("0") > 0) {
            // 秒
            min += Integer.valueOf(strs[0]) * 60 * 60;
        }
        if (strs[1].compareTo("0") > 0) {
            min += Integer.valueOf(strs[1]) * 60;
        }
        if (strs[2].compareTo("0") > 0) {
            min += Math.round(Float.valueOf(strs[2]));
        }
        return min;
    }

    /**
     * 按照文件夹(日期格式)名称排序
     * @param list
     * @return
     */
    private List<File> getDateList(List<File> list)  {

        if (list != null && list.size() > 0) {

            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    String fileName = file.getName();
                    String newFileName = newFile.getName();
                    Date date = new Date();
                    Date newDate = new Date();
                    try {
                         date = DateUtils.strToDate(fileName);
                         newDate = DateUtils.strToDate(newFileName);
                    } catch (ParseException e) {
                        logger.error("文件名:" + fileName + ",无法解析为指定的日期格式");
                        e.printStackTrace();
                    }
                    if (date.getTime() > newDate.getTime()) {
                        return 1;
                    } else if (date.getTime() == newDate.getTime()) {
                        return 0;
                    } else {
                        return -1;
                    }

                }
            });

        }

        return list;

    }







}
