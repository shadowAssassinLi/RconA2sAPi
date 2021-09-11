package cn.qaq.valveapi.vo;

import lombok.Data;

import java.util.List;

@Data
public class FileReturnVo {
    public List<FilesVo> files;

    public List<FileFoldVo> folders;
}
