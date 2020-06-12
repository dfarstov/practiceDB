package Control;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
//javafx
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
//
import Model.*;
import DBConnect.DatabaseHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainController extends Controller{

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
        createTable();
        setButtonAction();
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

    private void setButtonAction() {
        filterButton.setOnAction(actionEvent -> {
            useFilter();
        });
        graphButton.setOnAction(actionEvent -> {

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
        if (cheeseCheckBox.isSelected()) {
            for(ProductStatisticInfo productStatisticInfo: dataTable.getItems()){
                if (productStatisticInfo.getCheese() < Integer.parseInt(cheeseTextField.getText()))
                    dataTable.getItems().remove(productStatisticInfo);
            }
        }
        if (oilCheckBox.isSelected()) {
            for(ProductStatisticInfo productStatisticInfo: dataTable.getItems()){
                if (productStatisticInfo.getOil() < Integer.parseInt(oilTextField.getText()))
                    dataTable.getItems().remove(productStatisticInfo);
            }
        }
        if (sumCheckBox.isSelected()) {
            for(ProductStatisticInfo productStatisticInfo: dataTable.getItems()){
                if ((productStatisticInfo.getCheese() + productStatisticInfo.getOil()) < Integer.parseInt(sumTextField.getText()))
                    dataTable.getItems().remove(productStatisticInfo);
            }
        }
    }

    private void useFilter() {
        try {
            checkData();
            applyFilters();
        } catch (Exception e) {
            showError("Ошибка!", e.getMessage());
        }
    }

    private void addProductStatistic() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("../View/add.fxml"));
            Scene scene = new Scene(root);
            Stage addWindow = new Stage();
                  addWindow.setTitle("Добавить запись");
                  addWindow.setScene(scene);
                  addWindow.setResizable(false);
                  addWindow.setOnHiding(event -> {loadTableData();} );

                  addWindow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProductStatistic() {
        try {

        } catch (Exception e) {
            showError("Ошибка!", e.getMessage());
        }
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

    private void refreshFilters() {
    }
}
