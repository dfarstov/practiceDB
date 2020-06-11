package DBConnect;

import Model.*;

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

    public Vector<ProductsStatistic> getTableData() throws SQLException, ClassNotFoundException {
        Vector<ProductsStatistic> productsStatistics = new Vector<ProductsStatistic>();
        ResultSet resSet = null;
        String select =
                "SELECT \n" +
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
            ProductsStatistic temp = new ProductsStatistic();
                              temp.setRegion(resSet.getString(1));
                              temp.setCountry(resSet.getString(2));
                              temp.setOil(resSet.getInt(3));
                              temp.setCheese(resSet.getInt(4));
            productsStatistics.add(temp);
        }
        return productsStatistics;
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
        String select = "SELECT * FROM countries WHERE name = " + country;
        PreparedStatement prSt = getDbConnection().prepareStatement(select);
            resSet = prSt.executeQuery();
        if (resSet == null) {
            return false;
        }
        return true;
    }

    public void addCountry(String country, int id) {

    }
}
