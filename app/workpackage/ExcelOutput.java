package workpackage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;


public class ExcelOutput {
    //Purpose of this class is to update an existing excel file with pre-made diagrams that just wait to be filled.
    private static String bundesliga = "Bundes Liga",
                         liga = "Liga",
                         premier = "Premier League";
    
    private static String pathToFile = "./DEA_Output.xlsx";

    public static void createEfficiencyOutput(String league, List<String[]>dmuList, List<double[][]>overList, List<String[]>refList)  {
        //Method that creates an Excel-Output from a given array of dmus and an efficiency-array (containing the results of some models)
        String[]dmu = fitDMUOutput(dmuList);
        double[][]overview = fitNumericalOutput(dmuList, overList, false);
        String[][]reference = fitStringOutput(dmuList, refList);

        Timestamp tstamp = new Timestamp(System.currentTimeMillis()); //Get current system time


        try {
            FileInputStream report = new FileInputStream(new File(pathToFile));   //Open file, might want to check directory
            XSSFWorkbook workbook = new XSSFWorkbook(report); //Create object POI can work with

            XSSFSheet sheet = null; //Object for sheet which is to be filled
            Cell cell = null;       //Object for editing cells
            if(league.equals(bundesliga) == true)
                sheet = workbook.getSheetAt(0);   //Get 1st sheet for Bundesliga Data
            else if(league.equals(premier) == true)
                sheet = workbook.getSheetAt(3);   //Get 1st sheet for Premier League Data
            else if(league.equals(liga) == true)
                sheet = workbook.getSheetAt(6);   //Get 1st sheet for Primera Division Data
            else
                sheet = workbook.getSheetAt(0);   //Get 1st sheet due to lack of other leagues. Needs modification later on.

            //Clear cells before refilling them
            for(int i = 0; i < 21; i++)
                for(int j = 0; j < 102; j++)
                {
                    cell = sheet.getRow(j + 2).getCell(i + 1);
                    cell.setCellType(3);    //CELL_TYPE_BLANK
                }

            //Write out timestamp
            cell = sheet.getRow(0).getCell(1);  //Get cell to be edited
            cell.setCellValue(tstamp.toString());              //Edit Cell value

            //Write out DMUs to Excel
            for (int i = 0; i < dmu.length; i++)
            {
                cell = sheet.getRow(i + 2).getCell(1);  //Get cell to be edited
                cell.setCellValue(dmu[i]);              //Edit Cell value

                if(overview[0].length > dmu.length) //In case there is a 2nd stage
                {
                    cell = sheet.getRow(i + 2 + dmu.length).getCell(1);  //Get cell to be edited
                    cell.setCellValue(dmu[i]);              //Edit Cell value
                }

                if(overview[0].length > (2 * dmu.length)) //In case there is a 3rd stage
                {
                    cell = sheet.getRow(i + 2 + (2 * dmu.length)).getCell(1);  //Get cell to be edited
                    cell.setCellValue(dmu[i]);              //Edit Cell value
                }

                if(overview[0].length > (3 * dmu.length)) //In case there is a 3rd stage
                {
                    cell = sheet.getRow(i + 2 + (3 * dmu.length)).getCell(1);  //Get cell to be edited
                    cell.setCellValue(dmu[i]);              //Edit Cell value
                }
            }

            //Write out efficiency values
            for(int i = 0; i < overview.length; i++)
            {
                for(int j = 0; j < overview[i].length; j++)
                {
                    cell = sheet.getRow(j + 2).getCell(i + 2);  //Get cell to be edited
                    cell.setCellValue(overview[i][j]);              //Edit Cell value
                }
            }

            //Write out reference units
            for(int i = 0; i < reference.length; i++)
            {
                for(int j = 0; j < reference[i].length; j++)
                {
                    cell = sheet.getRow(j + 2).getCell(i + 2 + overview.length);  //Get cell to be edited
                    cell.setCellValue(reference[i][j]);              //Edit Cell value
                }
            }
            report.close();

            //Communicate update to the .xls-File
            FileOutputStream newReport =new FileOutputStream(new File(pathToFile));
            workbook.write(newReport);
            newReport.close();
            //Debate addition of several seasons. Also, how do we ensure that the values are accurate and nothing gets swapped?

            System.out.println("File correctly written");
        }catch(FileNotFoundException ex){
            System.out.println("Incorrect file name or file in wrong directory.");
        } catch (IOException ex) {
            System.out.println("Error creating workbook-object.");
        }
    }

    public static void createMQIOutput(String league, List<String[]>dmuList, List<double[][]>malmList)  {
        //Method that creates an Excel-Output from a given array of dmus, an aray of efficiencies and a malmquist-array (containing the results of some models)
        String[]dmu = fitDMUOutput(dmuList);
        double[][]malmquist = fitNumericalOutput(dmuList, malmList, true);

        Timestamp tstamp = new Timestamp(System.currentTimeMillis()); //Get current system time

        try {
            FileInputStream report = new FileInputStream(new File(pathToFile));   //Open file, might want to check directory
            XSSFWorkbook workbook = new XSSFWorkbook(report); //Create object POI can work with

            XSSFSheet sheet = null; //Object for sheet which is to be filled
            Cell cell = null;       //Object for editing cells

            //Write out Malmquist-Results
            if(league.equals(bundesliga) == true)
                sheet = workbook.getSheetAt(1);   //Get 1st sheet for Bundesliga Data
            else if(league.equals(premier) == true)
                sheet = workbook.getSheetAt(4);   //Get 1st sheet for Premier League Data
            else if(league.equals(liga) == true)
                sheet = workbook.getSheetAt(7);   //Get 1st sheet for Primera Division Data
            else
                sheet = workbook.getSheetAt(1);   //Get 1st sheet due to lack of other leagues. Needs modification later on.

            //Clear cells before refilling them
            for(int i = 0; i < 10; i++)
                for(int j = 0; j < 66; j++)
                {
                    cell = sheet.getRow(j + 2).getCell(i + 1);
                    cell.setCellType(3);    //CELL_TYPE_BLANK
                }

            //Write out timestamp
            cell = sheet.getRow(0).getCell(1);  //Get cell to be edited
            cell.setCellValue(tstamp.toString());              //Edit Cell value

            //Write out DMUs to Excel
            for (int i = 0; i < dmu.length; i++)
            {
                cell = sheet.getRow(i + 2).getCell(1);  //Get cell to be edited
                cell.setCellValue(dmu[i]);              //Edit Cell value

                if(malmquist[0].length > dmu.length) //In case there is a 2nd stage
                {
                    cell = sheet.getRow(i + 2 + dmu.length).getCell(1);  //Get cell to be edited
                    cell.setCellValue(dmu[i]);              //Edit Cell value
                }

                if(malmquist[0].length > (2 * dmu.length)) //In case there is a 3rd stage
                {
                    cell = sheet.getRow(i + 2 + (2 * dmu.length)).getCell(1);  //Get cell to be edited
                    cell.setCellValue(dmu[i]);              //Edit Cell value
                }
            }

            //Write out efficiency values
            for(int i = 0; i < malmquist.length; i++)
            {
                for(int j = 0; j < malmquist[i].length; j++)
                {
                    cell = sheet.getRow(j + 2).getCell(i + 2);  //Get cell to be edited
                    cell.setCellValue(malmquist[i][j]);              //Edit Cell value
                }
            }
            report.close();

            //Communicate update to the .xls-File
            FileOutputStream newReport =new FileOutputStream(new File(pathToFile));
            workbook.write(newReport);
            newReport.close();
            //Debate addition of several seasons. Also, how do we ensure that the values are accurate and nothing gets swapped?

            System.out.println("File correctly written");
        }catch(FileNotFoundException ex){
            System.out.println("Incorrect file name or file in wrong directory.");
        } catch (IOException ex) {
            System.out.println("Error creating workbook-object.");
        }
    }

    public static void createDataOutput(String league, List<String[]>dmuList, String[]paraNames, List<double[][]>paraValues) {
        //Method to write out the used date into an Excel sheet
        Timestamp tstamp = new Timestamp(System.currentTimeMillis()); //Get current system time
        try {
            FileInputStream report = new FileInputStream(new File("C:\\Football_DEA\\DEA_Output.xlsx"));   //Open file, might want to check directory
            XSSFWorkbook workbook = new XSSFWorkbook(report); //Create object POI can work with

            XSSFSheet sheet = null; //Object for sheet which is to be filled
            Cell cell = null;       //Object for editing cells

            //Write out Data
            if(league.equals(bundesliga) == true)
                sheet = workbook.getSheetAt(2);   //Get 1st sheet for Bundesliga Data
            else if(league.equals(premier) == true)
                sheet = workbook.getSheetAt(5);   //Get 1st sheet for Premier League Data
            else if(league.equals(liga) == true)
                sheet = workbook.getSheetAt(8);   //Get 1st sheet for Primera Division Data
            else
                sheet = workbook.getSheetAt(2);   //Get 1st sheet due to lack of other leagues. Needs modification later on.

            //Clear cells before refilling them
            for(int i = 0; i < 15; i++)
                for(int j = 0; j < 102; j++)
                {
                    cell = sheet.getRow(j + 2).getCell(i + 1);
                    cell.setCellType(3);    //CELL_TYPE_BLANK
                }

            //Write out timestamp
            cell = sheet.getRow(0).getCell(1);  //Get cell to be edited
            cell.setCellValue(tstamp.toString());              //Edit Cell value

            //Write out DMUs to Excel
            String[]dmu = null;
            for (int i = 0; i < dmuList.size(); i++)
            {
                dmu = dmuList.get(i);
                for(int j = 0; j < dmu.length; j++)
                {
                    cell = sheet.getRow(j + 2 + (i * dmu.length)).getCell(1);  //Get cell to be edited
                    cell.setCellValue(dmu[j]);              //Edit Cell value
                }
            }

            //Write out parameter names and corresponding seasons
            for(int j = 0; j < paraNames.length; j++)
            {
                cell = sheet.getRow(1).getCell(j + 2);  //Get cell to be edited
                cell.setCellValue(paraNames[j]);              //Edit Cell value
            }

            //Write out parameter values
            double[][]input = paraValues.get(0);    //Retrieve individual arrays from list
            double[][]output = paraValues.get(1);

            for(int i = 0; i < input.length; i++)
            {
                for(int j = 0; j < input[i].length; j++)
                {
                    cell = sheet.getRow(j + 2).getCell(i + 2);  //Get cell to be edited
                    cell.setCellValue(input[i][j]);              //Edit Cell value
                }
            }

            for(int i = 0; i < output.length; i++)
            {
                for(int j = 0; j < output[i].length; j++)
                {
                    cell = sheet.getRow(j + 2).getCell(i + 2 + input.length);  //Get cell to be edited
                    cell.setCellValue(output[i][j]);              //Edit Cell value
                }
            }

            report.close();

            //Communicate update to the .xls-File
            FileOutputStream newReport =new FileOutputStream(new File("C:\\Football_DEA\\DEA_Output.xlsx"));
            workbook.write(newReport);
            newReport.close();
            //Debate addition of several seasons. Also, how do we ensure that the values are accurate and nothing gets swapped?

            System.out.println("File correctly written");
        }catch(FileNotFoundException ex){
            System.out.println("Incorrect file name or file in wrong directory.");
        } catch (IOException ex) {
            System.out.println("Error creating workbook-object.");
        }
    }

    private static String[] fitDMUOutput(List<String[]>dmu)    {
        //Method used to combine the DMUs of x arrays (given in a list) into one array where each DMU is appearing only once.
        //Setting up DMUs
        int valid = 0; //Int-value equal to the length of all arrays in the given list
        for(int i = 0; i < dmu.size(); i++)
            valid = valid + dmu.get(i).length;

        String[] aggregation = new String[valid];   //Array which will be given valid DMU-names. Has the length of the maximum amount of DMUs possible and will be shortened afterwards.
        String[] inter = null;
        int pos = 0;    //Variable for counting the amount of valid DMUs
        for(int i = 0; i < dmu.size(); i++) //Iterate through list to consider each element
        {
            if(i == 0)  //First element of the list has a special position
            {
                inter = dmu.get(i); //get the Array that is currently being tried
                for(int j = 0; j < inter.length; j++)   //Fill the aggregatory array with the data
                {
                    aggregation[j] = inter[j];
                    pos++;
                }
            }
            else    //it is necessary to check the following Arrays for stuff that is already considered (or not)
            {
                inter = dmu.get(i); //get the Array that is currently being tried

                for(int j = 0; j < inter.length; j++)
                    if(Arrays.asList(aggregation).contains(inter[j]) == false)  //if the DMU is not already included, it will be
                    {
                        aggregation[(i * inter.length) + j] = inter[j];
                        pos++;
                    }
            }
        }

        String[] result = new String[pos];

        pos = 0;    //This int serves as a position indicator for the result array. This is necessary to avoid exceeding the capacity of this array.
        for(int i = 0; i < aggregation.length; i++)  //Necessary to avoid potential Nullpointer-issues.
            if(pos < result.length)
                if(aggregation[i] != null)
                {
                    result[pos] = aggregation[i];
                    pos++;
                }

        Arrays.sort(result);
        return result;
    }

    private static double[][] fitNumericalOutput(List<String[]>dmuList, List<double[][]>effList, boolean mqi)   {
        //Method to combine the results of 2 individual Malmquist or DEA-objects for the Excel-Output
        String[]dmu = fitDMUOutput(dmuList);    //Get common DMU-Array for correct positioning for the values
        int factor = effList.get(0)[0].length / dmuList.get(0).length;//Determine amount of stages
        int columns = 0;
        if(mqi == false)
            columns = 4;
        else
            columns = 3;
        double[][]eff = new double[effList.size() * columns][dmu.length * factor];   //amount of columns equals columns-times the amount of elements in the list

        String[]interDMU = null;
        double[][]interEff = null;
        int index;  //int to find the correct DMu to the corresponding value
        for(int i = 0; i < effList.size(); i++)
        {
            interDMU = dmuList.get(i);      //Get relevant arrays for comparison
            interEff = effList.get(i);
            for(int j = 0; j < interEff.length; j++)   //Iterate over element-columns
            {
                for(int k = 0; k < interEff[j].length; k++)   //Iterate over element rows
                {
                    if(k < interDMU.length)   //One stage DEA
                    {
                        index = Arrays.asList(dmu).indexOf(interDMU[k]);//Search for index in shared DMU-array
                        eff[(i * columns) + j][index] = interEff[j][k];
                    }
                    else if(k >= interDMU.length && k < (2 * interDMU.length))   //Two stage DEA
                    {
                        index = Arrays.asList(dmu).indexOf(interDMU[k - interDMU.length]);//Search for index in shared DMU-array
                        eff[(i * columns) + j][index + dmu.length] = interEff[j][k];
                    }
                    else if(k >= (2 * interDMU.length) && k < (3 * interDMU.length))   //Three stage DEA
                    {
                        index = Arrays.asList(dmu).indexOf(interDMU[k - (2 * interDMU.length)]);//Search for index in shared DMU-array
                        eff[(i * columns) + j][index + (2 * dmu.length)] = interEff[j][k];
                    }
                    else if(k >= (3 * interDMU.length) && k < (4 * interDMU.length))   //Four stage DEA
                    {
                        index = Arrays.asList(dmu).indexOf(interDMU[k - (3 * interDMU.length)]);//Search for index in shared DMU-array
                        eff[(i * columns) + j][index + (3 * dmu.length)] = interEff[j][k];
                    }
                }
            }
        }
        return eff;
    }

    private static String[][] fitStringOutput(List<String[]>dmuList, List<String[]>refList)   {
        //Method to combine the results of 2 individual Malmquist or DEA-objects for the Excel-Output
        String[]dmu = fitDMUOutput(dmuList);    //Get common DMU-Array for correct positioning for the values
        int factor = refList.get(0).length / dmuList.get(0).length;//Determine amount of stages
        String[][]ref = new String[refList.size()][dmu.length * factor];   //amount of columns equals the amount of elements in the list

        String[]interDMU = null;
        String[]interRef = null;
        int index;  //int to find the correct DMu to the corresponding value
        for(int i = 0; i < refList.size(); i++)
        {
            interDMU = dmuList.get(i);      //Get relevant arrays for comparison
            interRef = refList.get(i);
            for(int j = 0; j < interRef.length; j++)   //Iterate over element-columns
            {
                if(j < interDMU.length)   //One stage DEA
                {
                    index = Arrays.asList(dmu).indexOf(interDMU[j]);//Search for index in shared DMU-array
                    ref[i][index] = interRef[j];
                }
                else if(j >= interDMU.length && j < (2 * interDMU.length))   //Two stage DEA
                {
                    index = Arrays.asList(dmu).indexOf(interDMU[j - interDMU.length]);//Search for index in shared DMU-array
                    ref[i][index + dmu.length] = interRef[j];
                }
                else if(j >= (2 * interDMU.length) && j < (3 * interDMU.length))   //Three stage DEA
                {
                    index = Arrays.asList(dmu).indexOf(interDMU[j - (2 * interDMU.length)]);//Search for index in shared DMU-array
                    ref[i][index + (2 * dmu.length)] = interRef[j];
                }
                else if(j >= (3 * interDMU.length) && j < (4 * interDMU.length))   //Four stage DEA
                {
                    index = Arrays.asList(dmu).indexOf(interDMU[j - (3 * interDMU.length)]);//Search for index in shared DMU-array
                    ref[i][index + (3 * dmu.length)] = interRef[j];
                }
            }
        }
        return ref;
    }

    public static double[][] combineOverviewStage(double[][]overOne, double[][]overTwo)    {
        //This combines 2 overview-Arrays into one for the purpose of putting this into the Excel-report.
        //Here, the combination is by stage, i.e. a new overview will be attached at the bottom.
        double[][] overview = new double[overOne.length][overOne[0].length + overTwo[0].length];

        for(int i = 0; i < overview.length; i++)
            for(int j = 0; j < overOne[i].length; j++)
            {
                overview[i][j] = overOne[i][j];
                if(j < overTwo[i].length)
                    overview[i][j + overOne[i].length] = overTwo[i][j];
            }
        return overview;
    }

    public static String[] combineStringArray(String[]dmuOne, String[]dmuTwo)   {
        String[] dmu= new String[dmuOne.length + dmuTwo.length];
        System.arraycopy(dmuOne, 0, dmu, 0, dmuOne.length);
        System.arraycopy(dmuTwo, 0, dmu, dmuOne.length, dmuTwo.length);
        return dmu;
    }

    public static double[][] mergeArray(double[][] effOne, double[][] effTwo) {
        double[][] result = new double[effOne.length + effTwo.length][];
        System.arraycopy(effOne, 0, result, 0, effOne.length);
        System.arraycopy(effTwo, 0, result, effOne.length, effTwo.length);
        return result;
    }
}

