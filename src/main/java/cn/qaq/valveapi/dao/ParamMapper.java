package cn.qaq.valveapi.dao;

import cn.qaq.valveapi.vo.ParamVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParamMapper {

    List<ParamVo> getParamByKey(@Param("key")String key);

    List<ParamVo>  getParamListByKey(@Param("key")String key);

    ParamVo getParamByKeyAndCode(@Param("key")String key,@Param("code")String code);

    List<ParamVo> getParamByCode(@Param("code")String code);

    List<ParamVo> getParamListByKeyAndCode(@Param("key")String key,@Param("code")String code);
}
