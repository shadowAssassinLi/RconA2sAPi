package cn.qaq.valveapi.feign;


import cn.qaq.valveapi.vo.FileReturnVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(name = "sendMessage", url = "http://127.0.0.1:5700")
public interface SendMessageToGroupFeign {

    @RequestMapping(value = "/send_group_msg" , method = RequestMethod.POST)
    Map<String,Object> sendMessageToGroup(Map<String ,Object> map);

    @RequestMapping(value = "/send_private_msg" , method = RequestMethod.POST)
    Map<String,Object> sendMessageToPerson(Map<String ,Object> map);

    @RequestMapping(value = "/get_group_member_info" , method = RequestMethod.POST)
    Map<String,Object> getGroupMemberInfo(Map<String ,Object> map);

    @RequestMapping(value = "/get_group_root_files" , method = RequestMethod.POST)
    Map<String,Object> getGroupRootFiles(Map<String ,Object> map);

    @RequestMapping(value = "/get_group_files_by_folder" , method = RequestMethod.POST)
    Map<String,Object> getGroupFilesByFolder(Map<String ,Object> map);

    @RequestMapping(value = "/get_group_file_url" , method = RequestMethod.POST)
    Map<String,Object> getGroupFileUrl(Map<String ,Object> map);
}
