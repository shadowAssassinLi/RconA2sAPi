package cn.qaq.valveapi.common;



public enum ServerEnum {

    MANY_KENG_TU("121.40.192.147:7749","7749","坑图服"),
    MNAY_TE_GAN("8.140.130.223:9981","9981","多特服"),
    MANY_BOT("8.141.57.135:10020","10020","bot服"),
    RENT_SERVER("8.141.57.135:10010","10010","纯净服"),
    ELSE_SERVER("8.140.130.223:12315","12315","药抗服"),
    JUMP_SERVER("43.241.50.78:32050","32050","兔子窝");

    private String ip;
    private String port;
    private String name;

    ServerEnum(String ip , String port,String name){
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    public static String getIp(String port){
        for(ServerEnum server : ServerEnum.values()){
            if(server.port.equals(port)){
                return server.ip;
            }
        }
        return null;
    }

    public static String getIpByName(String name){
        for(ServerEnum server : ServerEnum.values()){
            if(server.name.equals(name)){
                return server.ip;
            }
        }
        return null;
    }
}
