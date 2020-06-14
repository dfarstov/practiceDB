package Control;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import JavaFXMods.SendData;
import Model.ProductStatisticInfo;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class ChartController extends Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private BarChart<String, Number> chart;

    @FXML
    void initialize() {
        loadChart();
    }

    private void loadChart() {
        Vector<ProductStatisticInfo> dataVector = SendData.getSendVector();
        XYChart.Series<String, Number> dataSeries1 = new XYChart.Series<String, Number>();
            dataSeries1.setName("Масло");
        XYChart.Series<String, Number> dataSeries2 = new XYChart.Series<String, Number>();
            dataSeries2.setName("Сыр");
        for (ProductStatisticInfo data: dataVector) {
            dataSeries1.getData().add(new XYChart.Data<String, Number>(data.getCountry(), data.getOil()));
            dataSeries2.getData().add(new XYChart.Data<String, Number>(data.getCountry(), data.getCheese()));
        }
        chart.getData().add(dataSeries1);
        chart.getData().add(dataSeries2);
    }
}
