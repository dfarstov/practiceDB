package JavaFXMods;


import Model.ProductStatisticInfo;
import javafx.collections.ObservableList;

import java.util.Vector;

public class SendData{
    static private String sendData;
    static private Vector<ProductStatisticInfo> sendVector;

    public SendData() {
        SendData.sendData = "";
        SendData.sendVector = new Vector<>();
    }

    public SendData(String sendData, Vector<ProductStatisticInfo> sendVector) {
        SendData.sendData = sendData;
        SendData.sendVector = sendVector;
    }

    public static String getSendData() {
        return sendData;
    }

    public static void setSendData(String sendData) {
        SendData.sendData = sendData;
    }

    public static Vector<ProductStatisticInfo> getSendVector() {
        return sendVector;
    }

    public static void setSendVector(Vector<ProductStatisticInfo> sendVector) {
        SendData.sendVector = sendVector;
    }

    public static void setSendVector(ObservableList<ProductStatisticInfo> sendList) {
        Vector<ProductStatisticInfo> newVector = new Vector<>();
        for (ProductStatisticInfo data: sendList) {
            newVector.add(data);
        }
        SendData.sendVector = newVector;
    }
}
