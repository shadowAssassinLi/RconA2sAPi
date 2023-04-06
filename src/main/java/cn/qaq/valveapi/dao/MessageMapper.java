package cn.qaq.valveapi.dao;

import feign.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface MessageMapper {

    void insertMessage(Map<String, String> map);

    void insertOperate(Map<String, String> map);

    int isExist(@Param("messageId") String messageId);
}
