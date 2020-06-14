package Control;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
//javafx
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
//apache
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
//
import Model.*;
import DBConnect.DatabaseHandler;
import JavaFXMods.SendData;


public class MainController extends Controller{

    @FXML
    private MenuItem saveTextButton;

    @FXML
    private MenuItem saveExcelButton;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<ProductStatisticInfo> dataTable;

    @FXML
    private Button graphButton;

    @FXML
    private Button filterButton;

    @FXML
    private Button printButton;

    @FXML
    private CheckBox sumCheckBox;

    @FXML
    private TextField sumTextField;

    @FXML
    private CheckBox cheeseCheckBox;

    @FXML
    private TextField cheeseTextField;

    @FXML
    private CheckBox oilCheckBox;

    @FXML
    private TextField oilTextField;

    Vector<ProductStatisticInfo> productStatisticInfos;
    DatabaseHandler databaseHandler;
    ContextMenu cm;

    @FXML
    void initialize() {
        disableFilters();
        createTable();
        setElementsActions();
    }
    
    private void createTable(){
        dataTable.getColumns().clear();
        dataTable.getItems().clear();
        dataTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        TableColumn<ProductStatisticInfo, String> column0 = new TableColumn<>("Регион");
                                               column0.setCellValueFactory(new PropertyValueFactory<>("region"));

        TableColumn<ProductStatisticInfo, String> column1 = new TableColumn<>("Страна");
                                               column1.setCellValueFactory(new PropertyValueFactory<>("country"));

        TableColumn<ProductStatisticInfo, String> column2 = new TableColumn<>("Масло");
                                               column2.setCellValueFactory(new PropertyValueFactory<>("oil"));

        TableColumn<ProductStatisticInfo, String> column3 = new TableColumn<>("Сыр");
                                               column3.setCellValueFactory(new PropertyValueFactory<>("cheese"));


        dataTable.getColumns().addAll(column0, column1, column2, column3);

        loadTableData();
        tableContextMenuCreate();
    }

    private void setElementsActions() {
        setButtonsAction();
        setCheckBoxAction(oilCheckBox, oilTextField);
        setCheckBoxAction(cheeseCheckBox, cheeseTextField);
        setCheckBoxAction(sumCheckBox, sumTextField);
    }

    private void setButtonsAction() {
        filterButton.setOnAction(actionEvent -> {
            useFilter();
        });
        graphButton.setOnAction(actionEvent -> {
            showGraph();
        });
        saveTextButton.setOnAction(actionEvent -> {
            saveTextFile();
        });
        saveExcelButton.setOnAction(actionEvent -> {
            saveExcelFile();
        });
        printButton.setOnAction(actionEvent -> {
            printTable();
        });
    }

    private void showGraph() {
        try {
            SendData.setSendVector(dataTable.getItems());
            showSecondaryWindow("../View/chart.fxml", "Граф. представление");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printTable() {
        Node node = new Label(getTableText());
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null) {
            boolean success = job.printPage(node);
            if (success) {
                job.endJob();
            }
        }
    }

    private void saveTextFile() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                PrintWriter writer;
                writer = new PrintWriter(file);
                String content = getTableText();
                writer.println(content);
                writer.close();
            } catch (IOException ex) {
                showError("Ошибка сохранения", ex.getMessage());
            }
        }
    }

    private void saveExcelFile() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Книга excel(*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);

        Stage stage = new Stage();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet("Статистика");

                int rownum = 0;
                Cell cell;
                Row row = sheet.createRow(rownum);

                // Reg
                cell = row.createCell(0, CellType.STRING);
                cell.setCellValue("Регион");
                // Country
                cell = row.createCell(1, CellType.STRING);
                cell.setCellValue("Страна");
                // Oil
                cell = row.createCell(2, CellType.STRING);
                cell.setCellValue("Масло");
                // Cheese
                cell = row.createCell(3, CellType.STRING);
                cell.setCellValue("Сыр");

                // Data
                for (ProductStatisticInfo data : dataTable.getItems()) {
                    rownum++;
                    row = sheet.createRow(rownum);

                    // Reg
                    cell = row.createCell(0, CellType.STRING);
                    cell.setCellValue(data.getRegion());
                    // Country
                    cell = row.createCell(1, CellType.STRING);
                    cell.setCellValue(data.getCountry());
                    // Oil
                    cell = row.createCell(2, CellType.NUMERIC);
                    cell.setCellValue(data.getOil());
                    // Cheese
                    cell = row.createCell(3, CellType.NUMERIC);
                    cell.setCellValue(data.getCheese());
                }

                workbook.write(new FileOutputStream(file));
                workbook.close();
            } catch (IOException ex) {
                showError("Ошибка сохранения", ex.getMessage());
            }
        }
    }

    private void setCheckBoxAction(CheckBox checkBox, TextField textField) {
        checkBox.setOnAction(actionEvent -> {
            if (checkBox.isSelected()) {
                textField.setDisable(false);
            } else {
                textField.setDisable(true);
                useFilter();
            }
        });
    }

    private void getProductsStatistics() {
        productStatisticInfos = new Vector<ProductStatisticInfo>();
        databaseHandler = new DatabaseHandler();
        try {
            productStatisticInfos = databaseHandler.getTableData();
        } catch (SQLException throwables) {
            showError("Ошибка!", throwables.getMessage());
        } catch (ClassNotFoundException e) {
            showError("Ошибка!", e.getMessage());
        }
    }

    private void loadTableData() {
        getProductsStatistics();
        dataTable.getItems().clear();
        for(ProductStatisticInfo prStat: productStatisticInfos)
            dataTable.getItems().add(prStat);
    }

    private void tableContextMenuCreate() {
        MenuItem mi1 = new MenuItem("Добавить запись");
        MenuItem mi2 = new MenuItem("Обновить запись");
        MenuItem mi3 = new MenuItem("Удалить выделенное");
            cm = new ContextMenu();
            cm.getItems().add(mi1);
            cm.getItems().add(mi2);
            cm.getItems().add(mi3);

        dataTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(t.getButton() == MouseButton.PRIMARY) {
                    cm.hide();
                }
                if(t.getButton() == MouseButton.SECONDARY) {
                    cm.show(dataTable, t.getScreenX(), t.getScreenY());
                    mi1.setOnAction(actionEvent -> {
                        addProductStatistic();
                    });
                    mi2.setOnAction(actionEvent -> {
                        updateProductStatistic();
                    });
                    mi3.setOnAction(actionEvent -> {
                        removeProductStatistic();
                    });
                }
            }
        });
    }

    private void checkData() throws Exception {
        if (cheeseCheckBox.isSelected()) {
            dataTest(cheeseTextField, "сыра");
        }
        if (oilCheckBox.isSelected()) {
            dataTest(oilTextField, "масла");
        }
        if (sumCheckBox.isSelected()) {
            dataTest(sumTextField, "суммы");
        }
    }

    private void dataTest(TextField textField, String type) throws Exception {
        String text = textField.getText();
        if (text.equals("")) throw new Exception(String.format("Вы не добавили значение минимума %s!", type));
        if (!text.chars().allMatch(Character::isDigit)) throw new Exception(String.format("Используйте только цифры для значения минимума %s!", type));
    }

    private void applyFilters() {
        Vector<ProductStatisticInfo> removeVector = new Vector<ProductStatisticInfo>();
        if (cheeseCheckBox.isSelected()) {
            removeVector.clear();
            for(ProductStatisticInfo productStatisticInfo: dataTable.getItems()){
                if (productStatisticInfo.getCheese() < Integer.parseInt(cheeseTextField.getText()))
                    removeVector.add(productStatisticInfo);
            }
            dataTable.getItems().removeAll(removeVector);
        }
        if (oilCheckBox.isSelected()) {
            removeVector.clear();
            for(ProductStatisticInfo productStatisticInfo: dataTable.getItems()){
                if (productStatisticInfo.getOil() < Integer.parseInt(oilTextField.getText()))
                    removeVector.add(productStatisticInfo);
            }
            dataTable.getItems().removeAll(removeVector);
        }
        if (sumCheckBox.isSelected()) {
            removeVector.clear();
            for(ProductStatisticInfo productStatisticInfo: dataTable.getItems()){
                if ((productStatisticInfo.getCheese() + productStatisticInfo.getOil()) < Integer.parseInt(sumTextField.getText()))
                    removeVector.add(productStatisticInfo);
            }
            dataTable.getItems().removeAll(removeVector);
        }
    }

    private void useFilter() {
        try {
            loadTableData();
            checkData();
            applyFilters();
        } catch (Exception e) {
            showError("Ошибка!", e.getMessage());
        }
    }

    private void addProductStatistic() {
        try {
            showSecondaryWindow("../View/add.fxml", "Добавить запись");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProductStatistic() {
        try {
            SendData.setSendData(String.valueOf(dataTable.getSelectionModel().getSelectedItem().getId()));
            showSecondaryWindow("../View/update.fxml", "Обновить запись");
        } catch (Exception e) {
            showError("Ошибка!", e.getMessage());
        }
    }

    private void showSecondaryWindow(String fxmlPath, String title) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Scene scene = new Scene(root);
        Stage secondaryWindow = new Stage();
              secondaryWindow.setTitle(title);
              secondaryWindow.setScene(scene);
              secondaryWindow.setResizable(false);
              secondaryWindow.setOnHiding(event -> {
                  loadTableData();
              });

              secondaryWindow.show();
    }

    private void removeProductStatistic() {
        try {
            databaseHandler.removeProductStatistic(dataTable.getSelectionModel().getSelectedItems());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dataTable.getItems().removeAll(dataTable.getSelectionModel().getSelectedItems());
        dataTable.refresh();
    }

    private void disableFilters() {
        oilTextField.setDisable(true);
        cheeseTextField.setDisable(true);
        sumTextField.setDisable(true);
    }

    private String getTableText() {
        String separator = "+";
        for (int i = 0; i < 63; ++i)
            separator += "-";
        separator += "+\n";
        StringBuilder tableDataBuilder = new StringBuilder();
        tableDataBuilder.append(separator);
        tableDataBuilder.append(String.format("|%15s|%15s|%15s|%15s|\n", "Регион", "Страна", "Масло", "Сыр"));
        for (ProductStatisticInfo productStatisticInfo: dataTable.getItems()){
            tableDataBuilder.append(String.format(separator));
            tableDataBuilder.append(String.format("|%15s|%15s|%15d|%15d|\n", productStatisticInfo.getRegion(), productStatisticInfo.getCountry(), productStatisticInfo.getOil(), productStatisticInfo.getCheese()));
        }
        tableDataBuilder.append(separator);

        return tableDataBuilder.toString();
    }
}
