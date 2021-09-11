package cn.qaq.valveapi.vo;

import lombok.Data;

@Data
public class FileFoldVo {

    private String folder_id;
    private String folder_name;
    private String create_time;
    private String creator;
    private String creator_name;
    private String total_file_count;
}
