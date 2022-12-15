package hu.petrik.etlap;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EtlapDB {
    Connection conn;

    public static String DB_DRIVER = "mysql";
    public static String DB_HOST = "localhost";
    public static String DB_PORT = "3306";
    public static String DB_DBNAME = "etlapdb";
    public static String DB_USER = "root";
    public static String DB_PASS = "";

    public EtlapDB() throws SQLException {
        String url = String.format("jdbc:%s://%s:%s/%s", DB_DRIVER, DB_HOST, DB_PORT, DB_DBNAME);
        conn = DriverManager.getConnection(url, DB_USER, DB_PASS);
    }

    public boolean createEtlap(Etlap etlap) throws SQLException {
        String sql = "INSERT INTO etlap(nev, leiras, ar, kategoria) VALUES (?,?,?,?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, etlap.getNev());
        stmt.setString(2, etlap.getLeiras());
        stmt.setInt(3, etlap.getAr());
        stmt.setString(4, etlap.getKategoria());
        return stmt.executeUpdate() > 0;
    }

    public List<Etlap> readEtlap() throws SQLException {
        List<Etlap> etlap = new ArrayList<>();
        String sql = "SELECT * FROM etlap";
        Statement stmt = conn.createStatement();
        ResultSet result = stmt.executeQuery(sql);
        while (result.next()) {
            int id = result.getInt("id");
            String nev = result.getString("nev");
            String leiras = result.getString("leiras");
            int ar = result.getInt("ar");
            String kategoria = result.getString("kategoria");
            Etlap etlap1 = new Etlap(id, nev, leiras,kategoria,ar);
            etlap.add(etlap1);
        }
        return etlap;
    }

    public boolean updateEtlap(Etlap etlap) throws SQLException {
        String sql = "UPDATE etlap SET nev = ?, leiras = ?, ar = ?, kategoria = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, etlap.getNev());
        stmt.setString(2, etlap.getLeiras());
        stmt.setInt(3, etlap.getAr());
        stmt.setString(4, etlap.getKategoria());
        stmt.setInt(5, etlap.getId());
        return stmt.executeUpdate() > 0;
    }

    public boolean deleteEtlap(int id) throws SQLException {
        String sql = "DELETE FROM etlap WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        return stmt.executeUpdate() > 0;
    }
}
