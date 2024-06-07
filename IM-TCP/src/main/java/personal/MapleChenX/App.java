package personal.MapleChenX;


import personal.MapleChenX.imTcp.config.ConfigReader;
import personal.MapleChenX.imTcp.server.WebsocketServer;

public class App {
    public static void main( String[] args ) {
        // String 转为 int
        String port = ConfigReader.getProperty("port");
        System.out.println("port: " + port);
        new WebsocketServer(Integer.parseInt(port));
    }
}
