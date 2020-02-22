package databasetest;

import java.sql.*;
import org.json.simple.*;
import java.util.*;

public class DatabaseTest {

    public static void main(String[] args) {
        System.out.print(getJSONData());
    }
    
    public static JSONArray getJSONData(){
                
        Connection conn = null;
        PreparedStatement pstSelect = null, pstUpdate = null;
        ResultSet resultset = null;
        ResultSetMetaData metadata = null;
        
        String query, value;
        ArrayList<String> key = new ArrayList<>();
        boolean hasresults;
        int resultCount, columnCount, updateCount = 0;
        JSONArray records = new JSONArray();
        
        try {
            
            /* Identify the Server */
            
            String server = ("jdbc:mysql://localhost/p2_test");
            String username = "root";
            String password = "001198939";
            
            /* Load the MySQL JDBC Driver */
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            /* Open Connection */

            conn = DriverManager.getConnection(server, username, password);
            
            /* Test Connection */
            
            if (conn.isValid(0)) {
                
                /* Prepare Select Query */
                
                query = "SELECT * FROM people";
                pstSelect = conn.prepareStatement(query);
                
                /* Execute Select Query */
                                
                hasresults = pstSelect.execute();                
                
                /* Get Results */
                                
                while ( hasresults || pstSelect.getUpdateCount() != -1 ) {

                    if ( hasresults ) {
                        
                        /* Get ResultSet Metadata */
                        
                        resultset = pstSelect.getResultSet();
                        metadata = resultset.getMetaData();
                        columnCount = metadata.getColumnCount();
                        
                        /* Get Column Names; Print as Table Header */
                        
                        for (int i = 2; i <= columnCount; i++) {
                    
                            key.add(metadata.getColumnLabel(i));
                            
                        }
                        
                        /* Get Data; Print as Table Rows */
                        
                        while(resultset.next()) {
                                                        
                            JSONObject jsonObject = new JSONObject();
                            
                            /* Loop Through ResultSet Columns; Print Values */

                            for (int i = 2; i <= columnCount; i++) {
                                
                                JSONObject object = new JSONObject();
                                value = resultset.getString(i);

                                if (resultset.wasNull()) {
                                    object.put(key.get(i - 2), "NULL");
                                    object.toJSONString();
                                }

                                else {
                                    object.put(key.get(i - 2), value);
                                    object.toJSONString();
                                }
                                
                                jsonObject.putAll(object);
                            }
                            
                            records.add(jsonObject);
                        }
                        
                    }

                    else {

                        resultCount = pstSelect.getUpdateCount();  

                        if ( resultCount == -1 ) {
                            break;
                        }

                    }
                    
                    /* Check for More Data */

                    hasresults = pstSelect.getMoreResults();

                }
                
            }
                        
            /* Close Database Connection */
            
            conn.close();
            
        }
        
        catch (Exception e) {
            System.err.println(e.toString());
        }
        
        /* Close Other Database Objects */
        
        finally {
            
            if (resultset != null) { try { resultset.close(); resultset = null; } catch (Exception e) {} }
            
            if (pstSelect != null) { try { pstSelect.close(); pstSelect = null; } catch (Exception e) {} }
            
            if (pstUpdate != null) { try { pstUpdate.close(); pstUpdate = null; } catch (Exception e) {} }
            
        }
        
        return records;
    }
}