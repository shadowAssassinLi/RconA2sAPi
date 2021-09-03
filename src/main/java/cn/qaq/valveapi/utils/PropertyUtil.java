package cn.qaq.valveapi.utils;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil implements ServletContextListener {

    public static final Properties prop = new Properties();

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("++++++++++++++++++++++++++++++"+System.getProperty("user.dir"));
        InputStream inStream = getClass().getClassLoader().getResourceAsStream(System.getProperty("user.dir") + "/configue.properties");

        try {
            prop.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
