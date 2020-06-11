package Model;

public class ProductsStatistic {
    private String region;
    private String country;
    private int oil;
    private int cheese;

    public ProductsStatistic() {

    }

    public ProductsStatistic(String region, String country, int oil, int cheese) {
        this.region = region;
        this.country = country;
        this.oil = oil;
        this.cheese = cheese;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getOil() {
        return oil;
    }

    public void setOil(int oil) {
        this.oil = oil;
    }

    public int getCheese() {
        return cheese;
    }

    public void setCheese(int cheese) {
        this.cheese = cheese;
    }
}
