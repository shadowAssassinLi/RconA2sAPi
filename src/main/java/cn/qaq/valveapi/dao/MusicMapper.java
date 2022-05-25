package cn.qaq.valveapi.dao;

import cn.qaq.valveapi.vo.MusicVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MusicMapper {

    List<MusicVO> getMusicByName(@Param("name") String name);

    MusicVO getMusicByNameAndSinger(@Param("name") String name, @Param("singer")String singer);

    void insertMusic(@Param("musicList")List<MusicVO> musicList);
}
