package Control;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
//javafx
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
//
import Model.*;
import DBConnect.DatabaseHandler;
import javafx.stage.Stage;

public class MainController extends Controller{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<ProductsStatistic> dataTable;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button graphBurron;

    @FXML
    private Button loadDataButton;

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

    Vector<ProductsStatistic> productsStatistics;
    DatabaseHandler databaseHandler;

    @FXML
    void initialize() {
        productsStatistics = new Vector<ProductsStatistic>();
        databaseHandler = new DatabaseHandler();
        try {
            productsStatistics = databaseHandler.getTableData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        createTable();
        setButtonsEvents();
    }

    private void setButtonsEvents(){
        addButton.setOnAction(actionEvent -> {
            try {
                addProductStatistic();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void createTable(){
        dataTable.getColumns().clear();
        dataTable.getItems().clear();
        dataTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        TableColumn<ProductsStatistic, String> column0 = new TableColumn<>("Регион");
                                               column0.setCellValueFactory(new PropertyValueFactory<>("region"));

        TableColumn<ProductsStatistic, String> column1 = new TableColumn<>("Страна");
                                               column1.setCellValueFactory(new PropertyValueFactory<>("country"));

        TableColumn<ProductsStatistic, String> column2 = new TableColumn<>("Масло");
                                               column2.setCellValueFactory(new PropertyValueFactory<>("oil"));

        TableColumn<ProductsStatistic, String> column3 = new TableColumn<>("Сыр");
                                               column3.setCellValueFactory(new PropertyValueFactory<>("cheese"));


        dataTable.getColumns().addAll(column0, column1, column2, column3);

        for(ProductsStatistic prStat: productsStatistics)
            dataTable.getItems().add(prStat);
    }

    private void addProductStatistic() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../View/add.fxml"));
        Scene scene = new Scene(root);
        Stage addWindow = new Stage();
              addWindow.setTitle("Добавить запись");
              addWindow.setScene(scene);
              addWindow.setResizable(false);

              addWindow.show();
    }
}
