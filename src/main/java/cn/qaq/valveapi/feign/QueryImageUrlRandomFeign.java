package cn.qaq.valveapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "getImage", url = "https://api.dongmanxingkong.com/suijitupian/acg/1080p/")
public interface QueryImageUrlRandomFeign {

    @RequestMapping(value = "index.php?return=json" , method = RequestMethod.POST)
    Map<String, Object> getImageUrlRandom();
}
