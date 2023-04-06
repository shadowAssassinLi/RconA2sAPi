package cn.qaq.valveapi.service;


import cn.qaq.valveapi.chatgpt.ChatGptService;
import cn.qaq.valveapi.common.ParamCommon;
import cn.qaq.valveapi.dao.MessageMapper;
import cn.qaq.valveapi.dao.MusicMapper;
import cn.qaq.valveapi.dao.ParamMapper;
import cn.qaq.valveapi.feign.QueryImageUrlRandomFeign;
import cn.qaq.valveapi.feign.SendMessageToGroupFeign;
import cn.qaq.valveapi.utils.StringUtil;
import cn.qaq.valveapi.utils.TcpTools;
import cn.qaq.valveapi.utils.UdpServer;
import cn.qaq.valveapi.vo.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ApiService {

    private static String NEXT_LINE = "\r\n";

    private static String QQ_ROBOT = "[CQ:at,qq=%]";

    private Map<String, Object> repeatMod = new HashMap<>();


    @Autowired
    private SendMessageToGroupFeign sendMessageToGroupFeign;
    @Autowired
    private QueryImageUrlRandomFeign queryImageUrlRandomFeign;
    @Resource
    private ParamMapper paramMapper;
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private MusicMapper musicMapper;

    @Autowired
    private ChatGptService chatGptService;

    @SneakyThrows
    public List<HashMap<String, Object>> getPlayers(String ip)
    {
        return UdpServer.getPlayers(ip);
    }

    public HashMap<String, Object> getServers(String ip)
    {
        return UdpServer.getServers(ip);
    }

    @SneakyThrows
    public String execRcon(String ip, String cmd, String passwd)
    {
        TcpTools tools =new TcpTools();
        if(tools.initTcp(ip))
            return tools.send(cmd,passwd);
        else
        {
            return UdpServer.udpRcon(ip,passwd,cmd);
        }
    }

    public void queryServer(Map<String , Object> map) throws Exception {

        String groupId = StringUtil.mapGet(map.get("group_id"));
        String userId = StringUtil.mapGet(map.get("user_id"));

        List<ParamVo> groupIdParam = paramMapper.getParamByKey(ParamCommon.MY_GROUP_QQ_Key);
        if (CollectionUtils.isEmpty(groupIdParam)) {
            return;
        }
//        Integer count = messageMapper.isExist(StringUtil.mapGet(map.get("message_id")));
//        if(count>0){
//            return;
//        }

        //不是配置好的群聊或qq，不回
        if ((!StringUtils.isEmpty(groupId) && groupIdParam.get(0).getValue().contains(groupId))
           ||(!StringUtils.isEmpty(userId) && groupIdParam.get(0).getValue().contains(userId))){
            groupMessage(map , groupId , userId);
        }
    }
    public void groupMessage(Map<String , Object> map , String groupId , String userId) throws Exception{
        String postType = StringUtil.mapGet(map.get("post_type"));
        String noticeType = StringUtil.mapGet(map.get("notice_type"));
        String subType = StringUtil.mapGet(map.get("sub_type"));
        String messageType = StringUtil.mapGet(map.get("message_type"));
        String message = StringUtil.mapGet(map.get("message"));
        String operateId = StringUtil.mapGet(map.get("operator_id"));
        String selfId = StringUtil.mapGet(map.get("self_id"));
        String robotId = QQ_ROBOT.replace("%", selfId);
        if("notice".equals(postType)){
            //操作信息记录
            messageMapper.insertOperate(tranMap(map));

            if("group_increase".equals(noticeType)){
                //群员增加
                increaseGroupMember(groupId , userId,noticeType);
            }else if("group_ban".equals(noticeType)){
                //解禁言
                groupMemberBan(map,groupId , userId ,operateId ,subType);
            }else if("notify".equals(noticeType)){
                if("poke".equals(subType)){
                    //被戳
                    pokeInGroup(map, groupId, userId, selfId);
                }
            }

        }else if("message".equals(postType)){
            //群聊信息记录
            messageMapper.insertMessage(tranMap(map));

            //获得小雪花名字
            List<ParamVo> nameParamList = paramMapper.getParamByKey(ParamCommon.CODE_NAME);
            if(CollectionUtils.isEmpty(nameParamList)){
                return;
            }
            ParamVo nameParam = nameParamList.get(0);
            //群消息
            if("group".equals(messageType)){
                //群消息
                if (message.contains(robotId) || message.contains(nameParam.getValue())) {
                    //@机器人聊天
                    log.info("接受群信息={}",message);
                    chatWithRobot(map, message.replace(robotId, "").replace(nameParam.getValue(), "").trim(), groupId);
                }else if(message.startsWith(ParamCommon.START_PRE_MUSIC)){
                    message = message.replace(ParamCommon.START_PRE_MUSIC,"").trim();
                    //查询歌曲
                    if(!StringUtils.isEmpty(message)) {
                        queryMusic(groupId, userId, message,nameParam.getValue());
                    }
                } else {
                    //没有@机器人，查看是否是查询服务器
                    queryServerInfo(map,message, groupId,userId);
                }
            }else{
                //私聊消息
                if(!userId.equals(selfId)) {
                    chatWithPerson(message, userId ,nameParam.getValue());
                }
            }
        }
    }

    private Map<String, String> tranMap(Map<String, Object> map) {
        Map<String, String> mapStr = new HashMap<>();
        for(String key : map.keySet()){
            mapStr.put(key , StringUtil.mapGet(map.get(key)));
        }
        return mapStr;
    }

    //判断参数是否为空
    private boolean isEmptyParam(ParamVo paramByKey) {
        if(null == paramByKey || StringUtils.isEmpty(paramByKey.getValue())){
            return true;
        }
        return false;
    }

    //戳一戳
    private void pokeInGroup(Map<String, Object> map, String groupId, String userId,String robotId) {
        String targetId = StringUtil.mapGet(map.get("target_id"));
        if(!StringUtils.isEmpty(targetId) && robotId.equals(targetId)){
            List<ParamVo> chatAnswerList = paramMapper.getParamByCode(ParamCommon.CODE_POKE_ANSWER);
            if(!CollectionUtils.isEmpty(chatAnswerList)){
                Random random = new Random();
                String result = chatAnswerList.get(random.nextInt(chatAnswerList.size())).getValue();
                sendMessageToGroup(groupId,result);
            }
        }
    }

    //解禁言
    private void groupMemberBan(Map<String , Object> map, String groupId, String userId, String operateId, String subType) {
        String time = StringUtil.mapGet(map.get("duration"));
        String result = null;
        if("ban".equals(subType)){
            Integer banTime = Integer.parseInt(time)/60;
            Integer day = banTime / 60 / 24;
            Integer hour = banTime / 60 % 24 ;
            Integer minute = banTime % 60;
            time = (day == 0?"":day+"天")+ (hour == 0? "": hour+"小时")+ (minute == 0?"":minute+"分");
            result = "[CQ:at,qq="+userId+"]被[CQ:at,qq="+operateId+"]禁言"+time+"!";
        }else{
            result = "[CQ:at,qq="+userId+"]已被[CQ:at,qq="+operateId+"]解禁言!";
        }
        sendMessageToGroup(groupId,result);
    }

    //新增群员
    private void increaseGroupMember(String groupId, String userId ,String noticeType) {
        if("group_increase".equals(noticeType)){
            String result = null;
            List<ParamVo> welcomeList = paramMapper.getParamByCode(ParamCommon.CODE_NEW_MEMBER);
            result = "欢迎[CQ:at,qq="+userId+"]小伙伴的加入，";
            //欢迎语
            if(!CollectionUtils.isEmpty(welcomeList)){
                Random random = new Random();
                result = result + welcomeList.get(random.nextInt(welcomeList.size())).getValue();
            }
            sendMessageToGroup(groupId,result);
        }
    }

    //私聊
    private void chatWithPerson(String message, String userId , String robotName) throws Exception{
        ParamVo chatQQ = paramMapper.getParamByKeyAndCode(ParamCommon.KEY_QQ_ID, ParamCommon.CODE_QQ);
        ParamVo chatGroupQQ = paramMapper.getParamByKeyAndCode(ParamCommon.MY_GROUP_QQ_Key, ParamCommon.CODE_QQ);
        //如果在配置好的聊天qq里就回信息
        if(null != chatQQ && chatQQ.getValue().contains(userId)) {
            String result = null;
            if (message.startsWith(ParamCommon.START_PRE_MUSIC)) {
                message = message.replace(ParamCommon.START_PRE_MUSIC,"").trim();
                result = music(message, robotName);
            } else {
//                String[] dealStr = message.split(",");
//                //如果是能分成两段，可能是查询群文件urlde
//                if (2 == dealStr.length && null != chatGroupQQ && chatGroupQQ.getValue().contains(dealStr[0])) {
//                    queryGroupUrl(dealStr[0], dealStr[1], userId);
//                }else{
//                    if(message.contains(robotName)){
//                        result = chat(message , userId);
//                    }else{
//                        result = serviceInfo(message, userId);
//                    }
//                }
                result = chatGptService.chatWithGPT(message);
            }
            //如果有信息就发送
            if(!StringUtils.isEmpty(result)){
                sendMessageToPerson(userId, result, null);
            }
        }
    }

    //查询群文件url
    private void queryGroupUrl(String groupQQ, String fileName, String userId) throws Exception{
        try {
            Map<String,Object> fileReturnVo = queryRootFilesToGroup(groupQQ);
            if(null == fileReturnVo.get("data")){
                return;
            }
            fileReturnVo = (Map<String, Object>) fileReturnVo.get("data");
            List<String> fileStr = new ArrayList<>();
            //第一层文件夹
            if (null != fileReturnVo && null !=fileReturnVo.get("folders")){
                for(Map<String, String> map : (List<Map<String,String>>)fileReturnVo.get("folders")){
                    if(fileName.equals(map.get("folder_name"))){
                        Map<String,Object> fileReturnVo1 = queryGroupFilesByFolder(groupQQ , map.get("folder_id"));
                        if(null == fileReturnVo1.get("data")){
                            return;
                        }
                        fileReturnVo1 = (Map<String, Object>) fileReturnVo1.get("data");
                        if(null != fileReturnVo1 && null != fileReturnVo1.get("files")){
                            fileStr = queryFileUrl(groupQQ , (List<Map<String,String>>)fileReturnVo1.get("files"));
                        }
                    }
                }
            }
            //文件
            if (null != fileReturnVo && null != fileReturnVo.get("files") && "所有".equals(fileName)){
                fileStr = queryFileUrl(groupQQ , (List<Map<String,String>>)fileReturnVo.get("files"));
            }
            if(!CollectionUtils.isEmpty(fileStr)) {
                for (String result : fileStr){
                    sendMessageToPerson(userId, result, null);
                }
            }

        }catch (Exception e){
            throw e;
        }
    }



    //群里面和机器人互动
    private void chatWithRobot(Map<String, Object> map, String message,String groupId) {
        String result = null;
        //复读机模式
//        if("开启复读机模式".equals(message)){
//            repeatMod.put("id",StringUtil.mapGet(map.get("user_id")));
//            repeatMod.put("groupId",groupId);
//            repeatMod.put("time", LocalDateTime.now());
//            result = "开启复读机模式";
//        }else if("关闭复读机模式".equals(message)){
//            if(!CollectionUtils.isEmpty(repeatMod) && StringUtil.mapGet(map.get("user_id")).equals(repeatMod.get("id")) && groupId.equals(repeatMod.get("groupId"))) {
//                repeatMod = new HashMap<>();
//                result = "关闭复读机模式";
//            }
//        } else {
//            result = chat(message, groupId);
//        }
        result = chatGptService.chatWithGPT(message);
        System.out.println("机器人返回"+result);
//        result = "你好";
        if(!StringUtils.isEmpty(result)) {
            sendMessageToGroup(groupId, result);
        }
    }

    //处理和机器人互动，返回小雪花要说的话
    private String chat( String message ,String id){
        String result = null;
        if("你好".equals(message)){
            result = "nice to meet you too!";
        }else if(message.contains("爬!") || message.contains("死!")) {
            result = "你不要凶人家嘛~";
        }else if((message.contains("?") || message.contains("？")) &&( message.contains("吗")|| message.contains("嘛"))){
            result = message.replace("?","!").replace("吗","")
                    .replace("？","!").replace("嘛","");
            result = changeYouAndMe(result);
        }else{
            List<ParamVo> messageList = paramMapper.getParamListByKey(message).stream().filter( e ->
                    {
                        if(ParamCommon.CODE_HELP.equals(e.getCode()) || ParamCommon.CODE_CHAT.equals(e.getCode())){
                            return true;
                        }
                        return false;
                    }
            ).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(messageList)){
                Random random = new Random();
                result = messageList.get(random.nextInt(messageList.size())).getValue();
            }else {
                List<ParamVo> chatAnswerList = paramMapper.getParamListByKeyAndCode(ParamCommon.NO_MATCH_KEY,ParamCommon.CODE_CHAT);
                if(!CollectionUtils.isEmpty(chatAnswerList)){
                    Random random = new Random();
                    result = chatAnswerList.get(random.nextInt(chatAnswerList.size())).getValue();
                }
            }

        }
        return result;
    }


    //查询服务器
    private void queryServerInfo(Map<String, Object> map, String message ,String groupId ,String userId) throws Exception{
        String result = null;
        //开启复读机模式
        if(!CollectionUtils.isEmpty(repeatMod)){
            if(((LocalDateTime)repeatMod.get("time")).isBefore(LocalDateTime.now().minusMinutes(5))){
                repeatMod = new HashMap<>();
            }else if(userId.equals(repeatMod.get("id")) && groupId.equals(repeatMod.get("groupId"))){
                result = message;
            }
        }else {
            result = serviceInfo(message, userId);
        }
        if(!StringUtils.isEmpty(result)){
            sendMessageToGroup(groupId, result);
        }
    }

    //查询服务器信息
    private String serviceInfo( String message ,String userId){
        try {
            List<ParamVo> paramByKeyList = paramMapper.getParamByKey(message);
            if (!CollectionUtils.isEmpty(paramByKeyList)) {
                String result = null;
                ParamVo paramByKey = paramByKeyList.get(0);
                if (ParamCommon.CODE_IP.equals(paramByKey.getCode())) {
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    String time = dateFormat.format(new Date());
                    result = queryServerInfo(null, paramByKey.getValue(), time);
                } else if (ParamCommon.CODE_ALL_IP.equals(paramByKey.getCode())) {
                    List<ParamVo> paramVoList = paramMapper.getParamByCode(ParamCommon.CODE_IP);
                    StringBuilder sb = new StringBuilder();
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    String time = dateFormat.format(new Date());
                    for (ParamVo paramVo : paramVoList) {
                        sb.append(queryServerInfo(null, paramVo.getValue(), time)).append(NEXT_LINE);
                    }
                    result = sb.toString().trim();
                } else {
                    Random random = new Random();
                    List<ParamVo> personAnswer = paramByKeyList.stream().filter(e -> e.getCode().contains(userId)).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(personAnswer)) {
                        //私人定制回答
                        sendMessageToGroup(null, personAnswer.get(random.nextInt(personAnswer.size())).getValue());
                    } else {
                        List<ParamVo> noAtAnswer = paramByKeyList.stream().filter(e -> ParamCommon.CODE_NO_AT.equals(e.getCode())).collect(Collectors.toList());
                        //不加上名字对话
                        if (!CollectionUtils.isEmpty(noAtAnswer)) {
                            result = noAtAnswer.get(random.nextInt(noAtAnswer.size())).getValue();
                        }
                    }
                }
                return  result;
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }

    //查询服务器信息
    private String queryServerInfo(String groupId, String ip ,String time) throws Exception{
        String result = null;
        try {
            List<HashMap<String, Object>> players = UdpServer.getPlayers(ip);
            HashMap<String, Object> server = UdpServer.getServers(ip);
            int index = 1;
            result = server.get("name") + NEXT_LINE +
                    "地图:" + server.get("map") + NEXT_LINE +
                    "延迟:" + server.get("time") + NEXT_LINE +
                    "时间:" +time +NEXT_LINE+
                    "ip:" + ip + NEXT_LINE +
                    "人数:" + server.get("players") + NEXT_LINE;
            for (HashMap<String, Object> player : players) {
                result = result + index + ".\"" + player.get("name") + "\"" + "在线时间" + player.get("time") + NEXT_LINE;
                index++;
            }
        }catch (Exception e){
        }
        if(StringUtils.isEmpty(result)){
            result = noAnswerResult();
        }
        return result;
    }

    //在群里发送消息
    private void sendMessageToGroup(String groupId , String result){
        Map<String , Object> sendMsg = new HashMap<>();
        sendMsg.put("group_id",groupId);
        sendMsg.put("message",result);
        sendMessageToGroupFeign.sendMessageToGroup(sendMsg);
    }

    //发送私人消息
    private void sendMessageToPerson(String userId , String result , String groupId){
        Map<String , Object> sendMsg = new HashMap<>();
        if(!StringUtils.isEmpty(groupId)){
            sendMsg.put("group_id",groupId);
        }
        sendMsg.put("user_id",userId);
        sendMsg.put("message",result);
        sendMessageToGroupFeign.sendMessageToPerson(sendMsg);
    }

    //获取群根目录文件列表
    private Map<String,Object> queryRootFilesToGroup(String groupId){
        Map<String , Object> sendMsg = new HashMap<>();
        sendMsg.put("group_id",groupId);
        return sendMessageToGroupFeign.getGroupRootFiles(sendMsg);
    }

    //获取群子目录文件列表
    private Map<String,Object> queryGroupFilesByFolder(String groupId ,String folderId){
        Map<String , Object> sendMsg = new HashMap<>();
        sendMsg.put("group_id",groupId);
        sendMsg.put("folder_id",folderId);
        return sendMessageToGroupFeign.getGroupFilesByFolder(sendMsg);
    }

    //获取群文件资源链接
    private Map<String,Object> queryGroupFileUrl(String groupId ,String fileId ,String busId){
        Map<String , Object> sendMsg = new HashMap<>();
        sendMsg.put("group_id",groupId);
        sendMsg.put("file_id",fileId);
        sendMsg.put("busid",busId);
        return sendMessageToGroupFeign.getGroupFileUrl(sendMsg);
    }

    //没有返回的时候
    private String noAnswerResult() {
        List<ParamVo> paramByKey1 = paramMapper.getParamByKey(ParamCommon.NO_ANSWER_KEY);
        if(CollectionUtils.isEmpty(paramByKey1)){
            return ParamCommon.NO_ANSWER_KEY_STR;
        }
        ParamVo paramByKey = paramByKey1.get(0);
        return paramByKey.getValue();
    }

    //交换你和我的位置
    private String changeYouAndMe(String result) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i <result.length() ; i++){
            String s = String.valueOf(result.charAt(i));
            if(s.equals("你")){
                sb.append("我");
            }else if(s.equals("我")){
                sb.append("你");
            }else{
                sb.append(s);
            }
        }
        return sb.toString();
    }

    //查询文件 Url
    private List<String> queryFileUrl(String groupQQ, List<Map<String, String>> files) {
        List<String> filesStr = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for(int i = 1 ; i<=files.size() ; i++){
            Map<String, Object> map = queryGroupFileUrl(groupQQ, files.get(i-1).get("file_id"), String.valueOf(files.get(i-1).get("busid")));
            sb.append(files.get(i-1).get("file_name")).append(",").append(StringUtil.mapGet(((Map<String, Object>)map.get("data")).get("url"))).append(NEXT_LINE).append(NEXT_LINE);
            if( i%10 == 0){
                filesStr.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        if(!StringUtils.isEmpty(sb.toString())){
            filesStr.add(sb.toString());
        }
        return filesStr;
    }

    //查询歌曲并返回
    private void queryMusic(String groupId, String userId, String message ,String robotName) {
        String result = music(message,robotName);
        if(!StringUtils.isEmpty(result)) {
            sendMessageToGroup(groupId, result);
        }
    }

    private String music(String message ,String robotName) {
        List<ParamVo> musicModeList = paramMapper.getParamListByKeyAndCode(ParamCommon.MUSIC_MODE_KEY, ParamCommon.MUSIC_MODE_CODE);
        if (!CollectionUtils.isEmpty(musicModeList)) {
            String mode = musicModeList.get(0).getValue();
            StringBuilder result = new StringBuilder();
            if (message.contains("/")) {
                //查询歌名加歌手    用 / 号隔开
                String[] params = message.split("/");
                MusicVO musicVO = musicMapper.getMusicByNameAndSinger(params[0], params[1]);
                if (null != musicVO && null != musicVO.getId()) {
                    String id = musicVO.getId();
                    String title = musicVO.getName() + (StringUtils.isEmpty(musicVO.getSinger()) ? "" : NEXT_LINE + musicVO.getSinger());
                    result.append(mode.replace(ParamCommon.MUSIC_ID, id)
                            .replace(ParamCommon.MUSIC_NAME, title));
                } else {
                    //DOTO 需要查询并插入数据库
                }
            } else {
                //查询歌名
                List<MusicVO> musicVOList = musicMapper.getMusicByName(message);
                if (!CollectionUtils.isEmpty(musicVOList)) {
                    for (MusicVO musicVO : musicVOList) {
                        String song = musicVO.getName() + "/" + musicVO.getSinger();
                        result.append(song + NEXT_LINE);
                    }
                } else {
                    //DOTO 需要查询并插入数据库
                }
            }

            if (StringUtils.isEmpty(result.toString())) {
                result.append(robotName).append("怎么也找不到这首歌啦...");
            }
            return result.toString();
        }
        return null;
    }
}
