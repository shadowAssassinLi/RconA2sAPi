package cn.qaq.valveapi.vo;

import lombok.Data;

@Data
public class FilesVo {

    private String file_id;
    private String file_name;
    private String busid;
    private String file_size;
    private String upload_time;
    private String dead_time;
    private String modify_time;
    private String download_times;
    private String uploader;
    private String uploader_name;

}
