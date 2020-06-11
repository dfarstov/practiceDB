package Control;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import Model.ProductsStatistic;
import Model.Region;
import DBConnect.DatabaseHandler;

public class AddController extends Controller{
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField countryTextField;

    @FXML
    private TextField oilTextField;

    @FXML
    private TextField cheeseTextField;

    @FXML
    private ChoiceBox<String> regionChoiceBox;

    @FXML
    private Button addButton;

    DatabaseHandler databaseHandler;
    Vector<Region> regions;

    @FXML
    void initialize() {
        databaseHandler = new DatabaseHandler();
        loadRegions();
        setAddButtonAction();
    }

    private void setAddButtonAction() {
        addButton.setOnAction(actionEvent -> {
            addData();
        });
    }

    private void addData() {
        try {
            String country = countryTextField.getText();
            String oil = oilTextField.getText();
            String cheese = cheeseTextField.getText();

            if (country == null) throw new Exception("Вы не добавили название страны");
            if (oil == null) throw new Exception("Вы не добавили значение масла");
            if (cheese == null) throw new Exception("Вы не добавили значение сыра");
            if (databaseHandler.containsCountry(country)) throw new Exception("Данная страна уже добавлена! Нажмите кнопку обновить данные");

            int regionId = 0;
            for (Region region: regions)
                if (region.getName().equals(regionChoiceBox.getItems().get(0)))
                    regionId = region.getID();

            databaseHandler.addCountry(country, regionId);
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRegions() {
        regions = new Vector<Region>();
        try {
            regions = databaseHandler.getRegions();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Vector<String> regionNames = new Vector<String>();
        for(Region region: regions)
            regionNames.add(region.getName());

        regionChoiceBox.getItems().addAll(regionNames);
        regionChoiceBox.setValue(regionChoiceBox.getItems().get(0));
    }
}
