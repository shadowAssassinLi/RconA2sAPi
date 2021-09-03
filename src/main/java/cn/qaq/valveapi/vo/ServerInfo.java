package cn.qaq.valveapi.vo;

import lombok.Data;

import java.util.List;

@Data
public class ServerInfo {

    private String name;

    private String map;

    private String players;

    private String time;

    private List<String> playerList;

}
