package cn.qaq.valveapi.dao;

import cn.qaq.valveapi.vo.ParamVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParamMapper {

    ParamVo getParamByKey(@Param("key")String key);

    List<ParamVo> getParamByCode(@Param("code")String code);
}
