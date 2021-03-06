package DBConnect;

import JavaFXMods.SendData;
import Model.*;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Vector;


public class DatabaseHandler extends Config{
    Connection dbConnection;

    public Connection getDbConnection() throws SQLException, ClassNotFoundException{
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?serverTimezone=Europe/Moscow&useSSL=false";

        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

        return dbConnection;
    }

    public Vector<ProductStatisticInfo> getTableData() throws SQLException, ClassNotFoundException {
        Vector<ProductStatisticInfo> productStatisticInfos = new Vector<ProductStatisticInfo>();
        ResultSet resSet = null;
        String select =
                "SELECT \n" +
                        "    p.id_statistic,\n" +
                        "    r.name,\n" +
                        "    c.name,\n" +
                        "    p.oil,\n" +
                        "    p.cheese\n" +
                        "FROM\n" +
                        "    practice.products_statistics p\n" +
                        "        JOIN\n" +
                        "    countries c on c.id_country = p.id_country\n" +
                        "        JOIN\n" +
                        "    regions r on r.id_region = c.id_region;";
        PreparedStatement prSt = getDbConnection().prepareStatement(select);
            resSet = prSt.executeQuery();
        while(resSet.next()) {
            ProductStatisticInfo temp = new ProductStatisticInfo();
                              temp.setId(resSet.getInt(1));
                              temp.setRegion(resSet.getString(2));
                              temp.setCountry(resSet.getString(3));
                              temp.setOil(resSet.getInt(4));
                              temp.setCheese(resSet.getInt(5));
            productStatisticInfos.add(temp);
        }
        return productStatisticInfos;
    }

    public Vector<Region> getRegions() throws SQLException, ClassNotFoundException {
        Vector<Region> regions = new Vector<Region>();
        ResultSet resSet = null;
        String select = "SELECT * FROM regions;";
        PreparedStatement prSt = getDbConnection().prepareStatement(select);
            resSet = prSt.executeQuery();
        while(resSet.next()) {
            Region temp = new Region();
                   temp.setID(resSet.getInt(1));
                   temp.setName(resSet.getString(2));
            regions.add(temp);
        }
        return regions;
    }

    public boolean containsCountry(String country) throws SQLException, ClassNotFoundException {
        ResultSet resSet = null;
        String select = String.format("SELECT * FROM countries WHERE name = \'%s\'", country);
        PreparedStatement prSt = getDbConnection().prepareStatement(select);
            resSet = prSt.executeQuery();
        if (resSet.next()) {
            return true;
        }
        return false;
    }

    public void addCountry(String country, int idRegion) throws SQLException, ClassNotFoundException {
        //add country
        String insert = "INSERT countries(name, id_region) VALUES(?,?);";
        PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, country);
            prSt.setString(2, String.valueOf(idRegion));

            prSt.executeUpdate();
    }

    public String getCountryID(String id) throws SQLException, ClassNotFoundException {
        String countryID = null;

        String select = String.format("SELECT id_country FROM products_statistics WHERE id_statistic = \'%s\'", id);
        PreparedStatement prSt = getDbConnection().prepareStatement(select);
        ResultSet resSet = prSt.executeQuery();
        if(resSet.next()) {
            countryID = resSet.getString(1);
        }

        return countryID;
    }

    public String getCountryIdByName(String name) throws SQLException, ClassNotFoundException {
        String countryID = null;

        String select = String.format("SELECT id_country FROM countries WHERE name = \'%s\'", name);
        PreparedStatement prSt = getDbConnection().prepareStatement(select);
        ResultSet resSet = prSt.executeQuery();
        if(resSet.next()) {
            countryID = resSet.getString(1);
        }

        return countryID;
    }

    public void addProductsStatistic(String newCountryID, String oil, String cheese) throws SQLException, ClassNotFoundException {
        //add stats
        String insert = "INSERT products_statistics(id_country, oil, cheese) VALUES(?,?,?);";
        PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, newCountryID);
            prSt.setString(2, String.valueOf(oil));
            prSt.setString(3, String.valueOf(cheese));

            prSt.executeUpdate();
    }

    public void removeProductStatistic(ObservableList<ProductStatisticInfo> selectedItems) throws SQLException, ClassNotFoundException {
        for (ProductStatisticInfo statisticInfo: selectedItems) {
            ResultSet resSet = null;

            String sql = String.format("SELECT id_country FROM products_statistics WHERE id_statistic = %s", statisticInfo.getId());
            PreparedStatement prSt = getDbConnection().prepareStatement(sql);
                resSet = prSt.executeQuery();

            if(resSet.next()) {
                int countryID = resSet.getInt(1);
                executeSql(statisticInfo.getId(), "DELETE FROM products_statistics WHERE id_statistic = %s; ");
                executeSql(countryID, "DELETE FROM countries WHERE id_country = %s;");
            }
        }
    }

    private void executeSql(int countryID, String s) throws SQLException, ClassNotFoundException {
        PreparedStatement prSt = getDbConnection().prepareStatement(String.format(s, countryID));
                          prSt.executeUpdate();
    }

    public void updateCountry(String country, int idRegion) throws SQLException, ClassNotFoundException {
        String insert = String.format("UPDATE countries SET name = \'%s\', id_region = %d WHERE id_country = %s;", country, idRegion, getCountryID(SendData.getSendData()));
        PreparedStatement prSt = getDbConnection().prepareStatement(insert);
                          prSt.executeUpdate();
    }

    public void updateProductsStatistic(String id, String oil, String cheese) throws SQLException, ClassNotFoundException {
        String insert = "UPDATE products_statistics SET id_statistic = ?, oil = ?, cheese = ? WHERE id_statistic = ?;";
        PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, id);
            prSt.setString(2, oil);
            prSt.setString(3, cheese);
            prSt.setString(4, id);

            prSt.executeUpdate();
    }

    public ProductStatisticInfo getProductStatistic(String id) throws SQLException, ClassNotFoundException {
        ProductStatisticInfo productStatisticInfo = new ProductStatisticInfo();
        String select = String.format(
                "SELECT \n" +
                        "    p.id_statistic,\n" +
                        "    r.name,\n" +
                        "    c.name,\n" +
                        "    p.oil,\n" +
                        "    p.cheese\n" +
                        "FROM\n" +
                        "    practice.products_statistics p\n" +
                        "        JOIN\n" +
                        "    countries c on c.id_country = p.id_country\n" +
                        "        JOIN\n" +
                        "    regions r on r.id_region = c.id_region " +
                        "WHERE id_statistic = %s", id);
        PreparedStatement prSt = getDbConnection().prepareStatement(select);
        ResultSet resSet = prSt.executeQuery();
        if (resSet.next()) {
            productStatisticInfo.setId(resSet.getInt(1));
            productStatisticInfo.setRegion(resSet.getString(2));
            productStatisticInfo.setCountry(resSet.getString(3));
            productStatisticInfo.setOil(resSet.getInt(4));
            productStatisticInfo.setCheese(resSet.getInt(5));
        }
        return productStatisticInfo;
    }
}
