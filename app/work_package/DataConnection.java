/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package work_package;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DataConnection {
    private Connection con; //Connection
    private Statement st;   //Query object. Enables "communication" with the database. 
    private ResultSet rs;   //Results, i.e. selected columns. Basically, this is a list of selected columns. 
    
    public DataConnection()
    {
        try{
            Class.forName("com.mysql.jdbc.Driver"); //Load driver class
            
            //Establish connection. Paramters: Link, UserName, UserPassword
            con = DriverManager.getConnection("jdbc:mysql://db4free.net:3306/deadatabase", "deadatabase", "ccrbcc");    //Establish connection. String needs to be adapted. 
            st = con.createStatement();     //Create statement for use in the connection           
        }catch(Exception ex){
            System.out.println("Error in Constructor: " + ex); //What went wrong?
        }
    }
    
    public String[] createDMUArray(String league, int season)    {
        try {
            //Extract DMU names from database
            rs = st.executeQuery("select name from " + league + " where year = " + "'" + season + "'");
            
            List columns = new ArrayList();   //Put ResultSet into an ArrayList. It is weird that only this approach seems to work. Requires testing later on. 
            while (rs.next()) {
                columns.add(rs.getString(1));
            } 
            return (String[]) columns.toArray(new String[columns.size()]);
        } catch (Exception ex) {
            System.out.println("Error when creating DMU-Array: " + ex); //What went wrong?
            return null;
        } 
    }
    
    public double[][] createParameterArray(String league, int season, String choice)    {
        //The method is given a String which separates the selected tables by a comma. This string is then turned into an array from which the parameters are gained
        String[]selection = choice.split(", ");
        String[][] stringPara = new String[selection.length][];   //Create String-array because an ArrayList cannot be directly converted to double
        try{
            for (int i = 0; i < selection.length; i++)    //Gather individual columns and put them directly into a 2D-array
            {
                rs = st.executeQuery("select " + selection[i] + " from " + league+ " where year = " + "'" + season + "'");   //Select only one column at a time
                
                List columns = new ArrayList();   //Put ResultSet into an ArrayList. It is weird that only this approach seems to work. Requires testing later on. 
                while (rs.next()) {
                    columns.add(rs.getString(1));
                } 
                stringPara[i] = (String[]) columns.toArray(new String[columns.size()]);
            }
            double[][] parameter = new double[stringPara.length][stringPara[0].length];   //Create final array for return
            for(int i = 0; i < stringPara.length; i++)
                for (int j = 0; j < stringPara[i].length; j++)
                    parameter[i][j] = Double.parseDouble(stringPara[i][j]);
                                
            return parameter; 
        } catch(Exception ex){
            System.out.println("Error when creating Parameter-Array: " + ex); //What went wrong?
            return null;
        }
    }
    
    public void showColumnNames(String league)  {
        //Method to get column names from the database in order to write out the used data. 
        try{
            rs = st.executeQuery("select * from " + league);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            for(int i = 1; i < columnCount - 2; i++)  //Write column name into array
                System.out.println(rsmd.getColumnName(i + 3));

            } catch(Exception ex){
            System.out.println("Error when getting column titles: " + ex); //What went wrong?
        }
    }

    public void close()    {
        try {
            con.close();
        } catch (SQLException ex) {
            System.out.println("Unable to close connection: " + ex); //What went wrong?
        }
    }
}
