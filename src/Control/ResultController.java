package Control;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import JavaFXMods.SendData;
import Model.ProductStatisticInfo;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ResultController extends Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private BarChart<String, Number> chart;

    @FXML
    private Label resultLabel;

    @FXML
    private Button printButton;

    @FXML
    void initialize() {
        loadChart();
        loadResultText();
        printButton.setOnAction(actionEvent -> {
            printTable();
        });
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

    private void loadResultText() {
        resultLabel.setText(getResultString());
    }

    private String getResultString() {
        ProductStatisticInfo bestOil = new ProductStatisticInfo();
        ProductStatisticInfo bestCheese = new ProductStatisticInfo();
        ProductStatisticInfo bestSum = new ProductStatisticInfo();

        for (ProductStatisticInfo data: SendData.getSendVector()) {
            if (bestOil.getOil() < data.getOil()) bestOil = data;
            if (bestCheese.getCheese() < data.getCheese()) bestCheese = data;
            if (bestSum.getSum() < data.getSum()) bestSum = data;
        }

        String bestOilStr = String.format("Страна с наибольшим производством масла - %s (%d т.).", bestOil.getCountry(), bestOil.getOil());
        String bestCheeseStr = String.format("Страна с наибольшим производством сыра - %s (%d т.).", bestCheese.getCountry(), bestCheese.getCheese());
        String bestSumStr = String.format("Страна с наибольшим сумарным производством - %s (%d т.).", bestSum.getCountry(), bestSum.getSum());

        return String.format("%s\n%s\n%s", bestOilStr, bestCheeseStr, bestSumStr);
    }

    private void printTable() {
        Node node = new Label(getTableText());
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null) {
            boolean success = job.printPage(node);
                    success = job.printPage(resultLabel);
                    success = job.printPage(chart);
            if (success) {
                job.endJob();
            }
        }
    }

    private String getTableText() {
        String separator = "+";
        for (int i = 0; i < 63; ++i)
            separator += "-";
        separator += "+\n";
        StringBuilder tableDataBuilder = new StringBuilder();
                      tableDataBuilder.append(separator);
                      tableDataBuilder.append(String.format("|%15s|%15s|%15s|%15s|\n", "Регион", "Страна", "Масло", "Сыр"));
        for (ProductStatisticInfo productStatisticInfo: SendData.getSendVector()){
            tableDataBuilder.append(String.format(separator));
            tableDataBuilder.append(String.format("|%15s|%15s|%15d|%15d|\n", productStatisticInfo.getRegion(), productStatisticInfo.getCountry(), productStatisticInfo.getOil(), productStatisticInfo.getCheese()));
        }
        tableDataBuilder.append(separator);

        return tableDataBuilder.toString();
    }
}
