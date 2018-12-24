package cn.casair.service.dto;

/**
 * @Author: liu
 * @Description: 视频录像DTO
 * @Date: Create in 下午2:24 18-11-9
 * @Modified:
 */
public class VideoRecordDTO {

    /** 通道名称 */
    private String channelName;

    /** 录像日期 */
    private String startTime;

    /** 最新更新时间 */
    private String lastModifiedTime;

    /** 视频名称 */
    private String filename;

    /** 视频录制开始时间 */
    private String videoRecordStartTime;

    /** 视频时长 */
    private String videoRecordDuration;

    /** 视频字节数 */
    private Long videoRecordBytes;

    /** 视频地址 */
    private String videoRecordUrl;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getVideoRecordStartTime() {
        return videoRecordStartTime;
    }

    public void setVideoRecordStartTime(String videoRecordStartTime) {
        this.videoRecordStartTime = videoRecordStartTime;
    }

    public String getVideoRecordDuration() {
        return videoRecordDuration;
    }

    public void setVideoRecordDuration(String videoRecordDuration) {
        this.videoRecordDuration = videoRecordDuration;
    }

    public Long getVideoRecordBytes() {
        return videoRecordBytes;
    }

    public void setVideoRecordBytes(Long videoRecordBytes) {
        this.videoRecordBytes = videoRecordBytes;
    }

    public String getVideoRecordUrl() {
        return videoRecordUrl;
    }

    public void setVideoRecordUrl(String videoRecordUrl) {
        this.videoRecordUrl = videoRecordUrl;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "VideoRecordDTO{" +
                "channelName='" + channelName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", lastModifiedTime='" + lastModifiedTime + '\'' +
                ", filename='" + filename + '\'' +
                ", videoRecordStartTime='" + videoRecordStartTime + '\'' +
                ", videoRecordDuration='" + videoRecordDuration + '\'' +
                ", videoRecordBytes=" + videoRecordBytes +
                ", videoRecordUrl='" + videoRecordUrl + '\'' +
                '}';
    }
}
