/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package work_package;

import ilog.concert.IloException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ScenarioMQI {
    private String league;          //Which league are we looking at?
    private DataConnection connection;   //Connect to database
    private String[] dmuOld;           //Array for selected DMUs of year one
    private double[][] inputOld;       //Array for selected inputs of year one
    private double[][] outputOld;       //Array for selected outputs of year one
    private String[] dmuNew;           //Array for selected DMUs of year two
    private double[][] inputNew;       //Array for selected inputs of year two
    private double[][] outputNew;       //Array for selected outputs of year two
    private double[][] interOneOld;    //Array of Stage 2 parameters of year one
    private double[][] interTwoOld;    //Array of Stage 3 parameters of year one
    private double[][] interOneNew;    //Array of Stage 2 parameters of year two
    private double[][] interTwoNew;    //Array of Stage 3 parameters of year two
    private int scale;              //Variable for Scale: 1(Constant = CCR), 2(Variable = BCC), 3(Increasing), 4(Decreasing)
    private int start;              //Starting year of the observations
    private int seasons;            //Amount of seasons looked at
    private boolean orientation;   //Boolean to figure out orientation of a single-stage MQI (true: input, false: output)
    private boolean [][]settings;   //Settings: orientation[0][], use efficiency of last stage as input [1][], combined input [2][]
    private List<String[]>dmuList;  //List of used DMUs
    private List<String[]>dataDMUList;  //List of used DMUs
    private List<double[][]>overList;   //List over overview-arrays from results
    private List<double[][]>paraValues; //List containing the data used.
    
    public ScenarioMQI(String league, String inputStr, String outputStr, boolean orientation, boolean data, int start, int seasons, int scale) throws Exception    
    {
        this.connection = new DataConnection();  //Establish connection to database
        
        //Initialize parameters
        this.league = league; 
        this.scale = scale;
        this.orientation = orientation;
        this.start = start; 
        this.seasons = seasons;
        this.dmuList = new ArrayList<>();
        this.dataDMUList = new ArrayList<>();
        this.overList = new ArrayList<>();
        
        if(seasons < 2)
            throw new Exception ("Seasons must at least have a value of 2!");
        
        double[][]paraIn = null;    //Arrays for saving parameter values
        double[][]paraOut = null;
        for(int i = 0; i < seasons - 1; i++)
        {
            //Get parameters of year one
            this.dmuOld = connection.createDMUArray(league, start + i);
            this.inputOld = connection.createParameterArray(league, start + i, inputStr);
            this.outputOld = connection.createParameterArray(league, start + i, outputStr); 
            
            //Get parameters of year two
            this.dmuNew = connection.createDMUArray(league, start + i + 1);
            this.inputNew = connection.createParameterArray(league, start + i + 1, inputStr);
            this.outputNew = connection.createParameterArray(league, start + i + 1, outputStr);
            
            if(data == true)    //Data is to be printed out, so it needs to be gathered
                if(i == 0)
                {
                    dataDMUList.add(dmuOld);
                    dataDMUList.add(dmuNew);
                    paraIn = inputOld;
                    paraIn = ExcelOutput.combineOverviewStage(paraIn, inputNew);
                    paraOut = outputOld;
                    paraOut = ExcelOutput.combineOverviewStage(paraOut, inputNew);
                }
                else
                {
                    dataDMUList.add(dmuNew);
                    paraIn = ExcelOutput.combineOverviewStage(paraIn, inputNew);
                    paraOut = ExcelOutput.combineOverviewStage(paraOut, inputNew);
                }
            
            try {
                Malmquist mqi = new Malmquist(scale, orientation, dmuOld, dmuNew, inputOld, inputNew, outputOld, outputNew);
                dmuList.add(mqi.dmu);            
                overList.add(mqi.calculateRadialMalmquist());
            } catch (IloException ex) {
                System.out.println("Error calculating MQI. No solution could be found.");
                Logger.getLogger(ScenarioMQI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Create Excel output
        ExcelOutput.createMQIOutput(league, dmuList, overList);
        
        if(data == true)
        {
            //Print out data that was used in the analysis
            String[] paraNames = ExcelOutput.combineStringArray(inputStr.split(", "), outputStr.split(", "));
            this.paraValues = new ArrayList<>();  //Unify input and output list
            paraValues.add(paraIn);
            paraValues.add(paraOut);
            ExcelOutput.createDataOutput(league, dataDMUList, paraNames, paraValues);
        }
        connection.close();
    }       
    
    public ScenarioMQI(String league, String inputStr, String interOneStr, String interTwoStr, String outputStr, boolean[][] settings, boolean data, int start, int seasons, int scale) throws Exception 
    {
        this.connection = new DataConnection();  //Establish connection to database
        
        this.league = league; 
        this.scale = scale;
        this.settings = settings;
        this.start = start; 
        this.seasons = seasons;
        this.dmuList = new ArrayList<>();        
        this.overList = new ArrayList<>();
        
        if(seasons < 2)
            throw new Exception ("Seasons must at least have a value of 2!");
        
        this.dataDMUList = new ArrayList<>();
        double[][]paraIn = null;    //Arrays for saving parameter values
        double[][]paraIntOne = null; 
        double[][]paraIntTwo = null; 
        double[][]paraOut = null; 
        
        for(int i = 0; i < (seasons - 1); i++)
        {
            //Get parameters of year one
            this.dmuOld = connection.createDMUArray(league, start + i);
            this.inputOld = connection.createParameterArray(league, start + i, inputStr);
            this.interOneOld = connection.createParameterArray(league, start + i, interOneStr);
            this.outputOld = connection.createParameterArray(league, start + i, outputStr);                       
            
            //Get parameters of year two
            this.dmuNew = connection.createDMUArray(league, start + i + 1);
            this.inputNew = connection.createParameterArray(league, start + i + 1, inputStr);
            this.interOneNew = connection.createParameterArray(league, start + i, interOneStr);
            this.outputNew = connection.createParameterArray(league, start + i + 1, outputStr);
            
            if(interTwoStr != null) //Parameters for intermediary product stage 2 - stage 3
            {
                this.interTwoOld = connection.createParameterArray(league, start + i, interTwoStr);
                this.interTwoNew = connection.createParameterArray(league, start + i + 1, interTwoStr);
            }
            
            if(data == true)
            {
                if(i == 0)
                {
                    dataDMUList.add(dmuOld);
                    dataDMUList.add(dmuNew);
                    paraIn = inputOld;
                    paraIn = ExcelOutput.combineOverviewStage(paraIn, inputNew);
                    paraIntOne = interOneOld;
                    paraIntOne = ExcelOutput.combineOverviewStage(paraIntOne, interOneNew);
                    if(interTwoStr != null)
                    {
                        paraIntTwo = interTwoOld;
                        paraIntTwo = ExcelOutput.combineOverviewStage(paraIntOne, interTwoNew);
                    }
                    paraOut = outputOld;
                    paraOut = ExcelOutput.combineOverviewStage(paraOut, inputNew);
                }
                else
                {
                    dataDMUList.add(dmuNew);
                    paraIn = ExcelOutput.combineOverviewStage(paraIn, inputNew);
                    paraIntOne = ExcelOutput.combineOverviewStage(paraIntOne, interOneNew);
                    if(interTwoStr != null)
                        paraIntTwo = ExcelOutput.combineOverviewStage(paraIntOne, interTwoNew);

                    paraOut = ExcelOutput.combineOverviewStage(paraOut, inputNew);
                }
            }
            
            try {
                Malmquist mqiOne = new Malmquist(scale, settings[0][0], dmuOld, dmuNew, inputOld, inputNew, interOneOld, interOneNew);
                double[][]overview = mqiOne.calculateRadialMalmquist();
                 
                Malmquist mqiTwo = null; 
                Malmquist mqiThree = null;
                if(interTwoStr != null)
                {
                    mqiTwo = new Malmquist(scale, settings[0][1], dmuOld, dmuNew, interOneOld, interOneNew, interTwoOld, interTwoNew);
                    overview = ExcelOutput.combineOverviewStage(overview, mqiTwo.calculateRadialMalmquist());
                    mqiThree = new Malmquist(scale, settings[0][0], dmuOld, dmuNew, interTwoOld, interTwoNew, outputOld, outputNew);
                }
                else
                    mqiThree = new Malmquist(scale, settings[0][0], dmuOld, dmuNew, interOneOld, interOneNew, outputOld, outputNew);
                
                overview = ExcelOutput.combineOverviewStage(overview, mqiThree.calculateRadialMalmquist());
                dmuList.add(mqiOne.dmu);           
                overList.add(overview);
            } catch (IloException ex) {
                System.out.println("Error calculating MQI. No solution could be found.");
                Logger.getLogger(ScenarioMQI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Create Excel output
        ExcelOutput.createMQIOutput(league, dmuList, overList);
        
        if(data == true)
        {
            String[] paraNames = ExcelOutput.combineStringArray(inputStr.split(", "), interOneStr.split(", "));
            if(interTwoStr != null)
                paraNames = ExcelOutput.combineStringArray(paraNames, interTwoStr.split(", "));
            paraNames = ExcelOutput.combineStringArray(paraNames, outputStr.split(", "));
            this.paraValues = new ArrayList<>();  //Unify input and output list
            paraValues.add(paraIn);
            paraValues.add(paraIntOne);
            if(interTwoStr != null)
                paraValues.add(paraIntTwo);
            paraValues.add(paraOut);
            ExcelOutput.createDataOutput(league, dmuList, paraNames, paraValues);
        }
        connection.close();
    }
    
    public List<String[]> getDMUList()  {
        return this.dmuList;
    }
    
    public List<double[][]> getOverviewList()  {
        return this.overList;
    }
}
