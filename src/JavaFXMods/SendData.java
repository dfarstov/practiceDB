package JavaFXMods;


public class SendData{
    static private String sendData;

    public SendData() {
        SendData.sendData = "";
    }

    public SendData(String sendData) {
        SendData.sendData = sendData;
    }

    public static String getSendData() {
        return sendData;
    }

    public static void setSendData(String sendData) {
        SendData.sendData = sendData;
    }
}
