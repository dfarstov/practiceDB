package Control;

import DBConnect.DatabaseHandler;
import JavaFXMods.SendData;
import Model.Region;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import jdk.dynalink.beans.StaticClass;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;

public class UpdateController extends Controller{
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
    private Button updateButton;

    DatabaseHandler databaseHandler;
    Vector<Region> regions;

    @FXML
    void initialize() {
        loadRegions();
        setUpdateButtonAction();
    }

    private void setUpdateButtonAction() {
        updateButton.setOnAction(actionEvent -> {
            updateData();
        });
    }

    private void updateData() {
        try {
            String country = countryTextField.getText();
            String oil = oilTextField.getText();
            String cheese = cheeseTextField.getText();

            checkData(country, oil, cheese);

            databaseHandler.updateCountry(country, getRegionId());
            databaseHandler.updateProductsStatistic(SendData.getSendData(), oil, cheese);
        }  catch (Exception e) {
            showError("Ошибка заполнения!", e.getMessage());
        }
    }

    private int getRegionId() {
        for (Region region : regions)
            if (region.getName().equals(regionChoiceBox.getSelectionModel().getSelectedItem()))
                return region.getID();
        return -1;
    }

    private void checkData(String country, String oil, String cheese) throws Exception {
        //country_test
        if (country.equals("")) throw new Exception("Вы не добавили название страны");
        //oil&cheese test
        if (oil.equals("")) throw new Exception("Вы не добавили значение масла");
        if (cheese.equals("")) throw new Exception("Вы не добавили значение сыра");
        if (!oil.chars().allMatch( Character::isDigit)) throw new Exception("Введите только цифры для значения масла!");
        if (!cheese.chars().allMatch( Character::isDigit)) throw new Exception("Введите только цифры для значения сыра!");
    }

    private void loadRegions() {
        databaseHandler = new DatabaseHandler();
        regions = new Vector<Region>();
        try {
            regions = databaseHandler.getRegions();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Vector<String> regionNames = new Vector<String>();
        for(Region region : regions)
            regionNames.add(region.getName());

        regionChoiceBox.getItems().addAll(regionNames);
        regionChoiceBox.setValue(regionChoiceBox.getItems().get(0));
    }
}
