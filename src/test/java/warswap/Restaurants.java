package warswap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class Restaurants
{
    private static String dbURL = "jdbc:derby:memory:resMemDB;create=true";
    private static String tableName = "restaurants";
    // jdbc Connection
    private static Connection conn = null;
    private static Statement stmt = null;

    public static void main(String[] args) throws SQLException
    {
        createConnection();
        createTable();
        insertRestaurants(5, "LaVals", "Berkeley");
        selectRestaurants();
        shutdown1();
    }
    
    private static void createTable() throws SQLException {
    	Statement sta = conn.createStatement(); 
        int count = sta.executeUpdate(
          "CREATE TABLE " + tableName + " (ID INT, restName VARCHAR(20),"
          + " City VARCHAR(20))");
        System.out.println("Table created.");
        sta.close();        		
	}

	private static void createConnection()
    {
        try
        {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connection Established");
        }
        catch (Exception except)
        {
            except.printStackTrace();
        }
    }
    
    private static void shutdown1() {
    	try {
			DriverManager.getConnection("jdbc:derby:MyDbTest;shutdown=true");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static void insertRestaurants(int id, String restName, String cityName)
    {
        try
        {
            stmt = conn.createStatement();
            stmt.execute("insert into " + tableName + " values (" +
                    id + ",'" + restName + "','" + cityName +"')");
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
    }
    
    private static void selectRestaurants()
    {
        try
        {
            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("select * from " + tableName);
            ResultSetMetaData rsmd = results.getMetaData();
            int numberCols = rsmd.getColumnCount();
            for (int i=1; i<=numberCols; i++)
            {
                //print Column Names
                System.out.print(rsmd.getColumnLabel(i)+"\t\t");  
            }

            System.out.println("\n-------------------------------------------------");

            while(results.next())
            {
                int id = results.getInt(1);
                String restName = results.getString(2);
                String cityName = results.getString(3);
                System.out.println(id + "\t\t" + restName + "\t\t" + cityName);
            }
            results.close();
            stmt.close();
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
    }
    
    private static void shutdown()
    {
        try
        {
            if (stmt != null)
            {
                stmt.close();
            }
            if (conn != null)
            {
                DriverManager.getConnection(dbURL + ";shutdown=true");
                conn.close();
            }           
        }
        catch (SQLException sqlExcept)
        {
            
        }

    }
}