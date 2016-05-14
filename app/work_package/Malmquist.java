/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package work_package;
import java.util.Arrays;
import ilog.concert.*;
import ilog.cplex.*;


public class Malmquist {
    
    int scale;          //Variable for Scale: 1(Constant = CCR), 2(Variable = BCC), 3(Increasing), 4(Decreasing)
    boolean orientation;    //True: Input Orientation, False: Output Orientation
    public String[]dmu;        //Array of investigated DMUs
    double[][]inputOld; //Input period 1
    double[][]inputNew; //Input period 2
    double[][]outputOld;    //Output period 1
    double[][]outputNew;    //Output period 2
    
    public Malmquist(int scale, boolean orientation, String[] dmuOld, String[] dmuNew, double[][]inOld, double[][]inNew, double[][]outOld, double[][]outNew)
    {
        this.scale = scale;
        this.orientation = orientation;
        
        //Setting up DMUs so that only teams playing in both time periods are considered
        int valid = 0;  //Counter for tracking the amount of DMUs which are in both arrays
        for(int i = 0; i < dmuOld.length; i++)
            if(Arrays.asList(dmuNew).contains(dmuOld[i]) == true)//if(dmuOne[i].equals(dmuTwo[j]) == true) //Every DMU from dmuOld is checked against dmuNew. If a match is found, the counter is increased. 
                valid++;
        
        //Create empty arrays for the class variables with relevant amount of space
        this.dmu = new String[valid];
        this.inputOld = new double[inOld.length][valid];
        this.inputNew = new double[inNew.length][valid];
        this.outputOld = new double[outOld.length][valid];
        this.outputNew = new double[outNew.length][valid];
        
        int pos = 0;    //Counter to monitor position in dmu-array. Necessary for correct filling. 
        //Fill DMU-array
        for(int i = 0; i < dmuOld.length; i++)
            for(int j = 0; j < dmuNew.length; j++)
                if(dmuOld[i].equals(dmuNew[j]) == true) //Every DMU from dmuOld is checked against dmuNew. If a match is found, the counter is increased. 
                {
                    dmu[pos] = dmuOld[i];
                    pos++;
                }
        
        //Fill inputs and outputs. 
        for(int i = 0; i < inOld.length; i++)    //Iterate over input-parameter
            for (int k = 0; k < dmu.length; k++)    //Iterate over newly created set of common DMUs
                for(int j = 0; j < inOld[i].length; j++) //Iterate over individual DMU-values
                {   
                    if(dmu[k].equals(dmuOld[j]) == true)    //Checking with both DMU-arrays is not necessary because only DMUs which appear in both are considered
                    {
                        inputOld[i][k] = inOld[i][j];
                        inputNew[i][k] = inNew[i][j];
                    }
                }
        
        for(int i = 0; i < outOld.length; i++)    //Iterate over output-parameter
            for (int k = 0; k < dmu.length; k++)    //Iterate over newly created set of common DMUs
                for(int j = 0; j < outOld[i].length; j++) //Iterate over individual DMU-values
                {   
                    if(dmu[k].equals(dmuOld[j]) == true)    //Checking with both DMU-arrays is not necessary because only DMUs which appear in both are considered
                    {
                        outputOld[i][k] = outOld[i][j];
                        outputNew[i][k] = outNew[i][j];
                    }
                }
    }
    
    public double[][] calculateRadialMalmquist() throws IloException{
        //Models and formulas taken from Cooper, Seiford et al (2007) pp. 328-333
        double[][] solution = new double[3][dmu.length];
        
        for(int i = 0; i < dmu.length; i++)
        {
            double[]within = withinScore(i);    //1: D^1((x,y)^1), 2: D^2((x,y)^2)
            double[]inter = interScore(i);  
            
            solution[0][i] = (double)Math.round(within[1] / within[0] * 10000) / 10000;   //CatchUp-Value: D^2((x,y)^2) / D^1((x,y)^1)
            solution[1][i] = (double)Math.round(Math.sqrt((within[0] / inter[1]) * (inter[0] / within[1])) * 10000) / 10000;  //FrontierShift-Value: ((D^1((x,y)^1) / D^2((x,y)^1)) * ((D^1((x,y)^2) / D^2((x,y)^2)))^0.5
            //Formula: (D^1((x,y)^2) / D^1((x,y)^1)) * (D^2((x,y)^2) / D^2((x,y)^1))^0.5
            solution[2][i] = (double)Math.round(Math.sqrt((inter[0] / within[0]) * (within[1] / inter[1])) * 10000) / 10000;//Put actual MI-value into solution array            
        }       
        return solution; 
    }

    public double calculateCatchUp(int i) throws IloException  {
        double[] within = withinScore(i);
        
        return (double)Math.round(within[1] / within[0] * 10000) / 10000;   //D^2((x,y)^2) / D^1((x,y)^1)
    }
    
    public double calculateFrontierShift(int i) throws IloException  {
        double[] within = withinScore(i);
        double[] inter = interScore(i); 
        
        return (double)Math.round(Math.sqrt((within[0] / inter[1]) * (inter[0] / within[1])) * 10000) / 10000;   //((D^1((x,y)^1) / D^2((x,y)^1)) * ((D^1((x,y)^2) / D^2((x,y)^2)))^0.5
    }
    
    private double[] withinScore(int i) throws IloException   {
        double[]solution = new double[2];
        
        for(int j = 0; j < 2; j++) //Calculation of D^1((x,y)^1) and D^2((x,y)^2)
        {
            IloCplex cplex = new IloCplex();    //Create CPLEX-Instance    
            cplex.setOut(null);                 //Set empty result
            
            //Decision variables
            IloNumVar[] lamda = new IloNumVar[dmu.length];  //Vector of decision variables
            lamda = cplex.numVarArray(dmu.length, 0, 100); //High Value at upper border, constraints are decreasing possible range later on. 
            IloNumVar teta = cplex.numVar(0, 10);
            
            //Objective function, i.e. theta, question is: how is this computed? Is the single appearance in one constrain sufficient?
            IloLinearNumExpr objFunc = cplex.linearNumExpr();            
            
            //Constraints
            IloLinearNumExpr leftside = cplex.linearNumExpr();  //Expression used for leftside-values of constraints
                
            if(orientation == true) //Input-orientation
            {
                objFunc.addTerm(1, teta);
                cplex.addMinimize(objFunc);
                if(j == 0)  //Equals t=1, i.e. the value for the 1st period is computed
                {
                    //Orientation constraints (only constraints that are mentioning teta)
                    for(int a = 0; a < inputOld.length; a++)
                    {
                        for(int b = 0; b < dmu.length; b++)
                            leftside.addTerm(inputOld[a][b], lamda[b]);
                
                        cplex.addGe(cplex.prod(teta, inputOld[a][i]), leftside);
                        leftside.clear();
                    }
            
                    //Non-Orientation constraints 
                    for(int c = 0; c < outputOld.length; c++)
                    {
                        for(int d = 0; d < dmu.length; d++)
                            leftside.addTerm(outputOld[c][d], lamda[d]);
                
                        cplex.addGe(leftside, outputOld[c][i]);
                        leftside.clear();
                    } 
                }
                else  //Equals t=2, i.e. the value for the 2nd period is computed
                {
                    //Orientation constraints (only constraints that are mentioning teta)
                    for(int a = 0; a < inputNew.length; a++)
                    {
                        for(int b = 0; b < dmu.length; b++)
                            leftside.addTerm(inputNew[a][b], lamda[b]);
                
                        cplex.addGe(cplex.prod(teta, inputNew[a][i]), leftside);
                        leftside.clear();
                    }
            
                    //Non-Orientation constraints 
                    for(int c = 0; c < outputNew.length; c++)
                    {
                        for(int d = 0; d < dmu.length; d++)
                            leftside.addTerm(outputNew[c][d], lamda[d]);
                
                        cplex.addGe(leftside, outputNew[c][i]);
                        leftside.clear();
                    }     
                }
            }
            else    //i.e. orientation == false, i.e. output-orientation
            {
                objFunc.addTerm(1, teta);
                cplex.addMaximize(objFunc); //Cooper requires a minimization and then uses 1/teta in the constraints. 
                                            //Since CPLEX doesn't allow divisions, we switched things to maximize and a regular teta. 
                                            //The division is done in the end where the solution is given to the array. 
                if(j == 0)  //Equals t=1, i.e. the value for the 1st period is computed
                {
                    //Orientation constraints (only constraints that are mentioning teta)
                    for(int a = 0; a < outputOld.length; a++)
                    {
                        for(int b = 0; b < dmu.length; b++)
                            leftside.addTerm(outputOld[a][b], lamda[b]);
                        //Adaption required! Before: (1/teta) * outputOld[a][i] <= leftside 
                        cplex.addLe(cplex.prod(teta, outputOld[a][i]), leftside); //Above mentioned constraint-change
                        leftside.clear();
                    }
            
                    //Non-Orientation constraints 
                    for(int c = 0; c < inputOld.length; c++)
                    {
                        for(int d = 0; d < dmu.length; d++)
                            leftside.addTerm(inputOld[c][d], lamda[d]);
                
                        cplex.addGe(inputOld[c][i], leftside);
                        leftside.clear();
                    }
                }
                else  //Equals t=2, i.e. the value for the 2nd period is computed
                {
                    //Orientation constraints (only constraints that are mentioning teta)
                    for(int a = 0; a < outputNew.length; a++)
                    {
                        for(int b = 0; b < dmu.length; b++)
                            leftside.addTerm(outputNew[a][b], lamda[b]);
                        //Adaption required! Before: (1/teta) * outputOld[a][i] <= leftside 
                        cplex.addLe(cplex.prod(teta, outputNew[a][i]), leftside); //Above mentioned constraint-change
                        leftside.clear();
                    }
            
                    //Non-Orientation constraints 
                    for(int c = 0; c < inputNew.length; c++)
                    {
                        for(int d = 0; d < dmu.length; d++)
                            leftside.addTerm(inputNew[c][d], lamda[d]);
                
                        cplex.addGe(leftside, inputNew[c][i]);
                        leftside.clear();
                    }                      
                }
            }
            
            //Constraints limiting weight variables                    
            for(int e = 0; e < dmu.length; e++)                            
                leftside.addTerm(1, lamda[e]);
            //if(scale == 1)    //CRS is already covered by the regular non-negativity        
            if(scale == 2)  //VRS aka. BCC: sum of all lamda is equal to 1
                cplex.addEq(leftside, 1);
            else if(scale == 3) //Increasing returns to scale
                cplex.addGe(leftside, 1);
            else if(scale == 4) //Decreasing returns to scale
                cplex.addLe(leftside, 1);
            leftside.clear(); 
                    
            cplex.solve();
            if(orientation == true)
                solution[j] = cplex.getObjValue();
            else
                solution[j] = 1 / cplex.getObjValue();
        }
        return solution;
    }
    
    private double[] interScore(int i) throws IloException  {
        double[]solution = new double[2];
        
        for(int j = 0; j < 2; j++) //Calculation of D^1((x,y)^2) and D^2((x,y)^1)
        {
            IloCplex cplex = new IloCplex();    //Create CPLEX-Instance    
            cplex.setOut(null);                 //Set empty result
            
            //Decision variables
            IloNumVar[] lamda = new IloNumVar[dmu.length];  //Vector of decision variables
            lamda = cplex.numVarArray(dmu.length, 0, 100); //High Value at upper border, constraints are decreasing possible range later on. 
            IloNumVar teta = cplex.numVar(0, 10);
            
            //Objective function, i.e. theta, question is: how is this computed? Is the single appearance in one constrain sufficient?
            IloLinearNumExpr objFunc = cplex.linearNumExpr();             
            
            //Constraints
            IloLinearNumExpr leftside = cplex.linearNumExpr();  //Expression used for leftside-values of constraints
                
            if(orientation == true) //Input-orientation
            {
                objFunc.addTerm(1, teta);
                cplex.addMinimize(objFunc);
                if(j == 0)  //Equals t=1, i.e. the value for D^1((x,y)^2) is computed
                {
                    //Orientation constraints (only constraints that are mentioning teta)
                    for(int a = 0; a < inputOld.length; a++)
                    {
                        for(int b = 0; b < dmu.length; b++)
                            leftside.addTerm(inputOld[a][b], lamda[b]);
                
                        cplex.addGe(cplex.prod(teta, inputNew[a][i]), leftside);
                        leftside.clear();
                    }
            
                    //Non-Orientation constraints 
                    for(int c = 0; c < outputOld.length; c++)
                    {
                        for(int d = 0; d < dmu.length; d++)
                            leftside.addTerm(outputOld[c][d], lamda[d]);
                
                        cplex.addGe(leftside, outputNew[c][i]);
                        leftside.clear();
                    } 
                }
                else  //Equals t=2, i.e. the value for D^2((x,y)^1) is computed
                {
                    //Orientation constraints (only constraints that are mentioning teta)
                    for(int a = 0; a < inputNew.length; a++)
                    {
                        for(int b = 0; b < dmu.length; b++)
                            leftside.addTerm(inputNew[a][b], lamda[b]);
                
                        cplex.addGe(cplex.prod(teta, inputOld[a][i]), leftside);
                        leftside.clear();
                    }
            
                    //Non-Orientation constraints 
                    for(int c = 0; c < outputNew.length; c++)
                    {
                        for(int d = 0; d < dmu.length; d++)
                            leftside.addTerm(outputNew[c][d], lamda[d]);
                
                        cplex.addGe(leftside, outputOld[c][i]);
                        leftside.clear();
                    }     
                }
            }
            else    //i.e. orientation == false, i.e. output-orientation
            {
                objFunc.addTerm(1, teta);
                cplex.addMaximize(objFunc); //Cooper requires a minimization and then uses 1/teta in the constraints. 
                                            //Since CPLEX doesn't allow divisions, we switched things to maximize and a regular teta. 
                                            //The division is done in the end where the solution is given to the array.                 
                if(j == 0)  //Equals t=1, i.e. the value for D^1((x,y)^2) is computed
                {
                    //Orientation constraints (only constraints that are mentioning teta)
                    for(int a = 0; a < outputOld.length; a++)
                    {
                        for(int b = 0; b < dmu.length; b++)
                            leftside.addTerm(outputOld[a][b], lamda[b]);
                        //Adaption required! Before: (1/teta) * outputOld[a][i] <= leftside 
                        cplex.addLe(cplex.prod(teta, outputNew[a][i]), leftside); //Above mentioned constraint change
                        leftside.clear();
                    }
            
                    //Non-Orientation constraints 
                    for(int c = 0; c < inputOld.length; c++)
                    {
                        for(int d = 0; d < dmu.length; d++)
                            leftside.addTerm(inputOld[c][d], lamda[d]);
                
                        cplex.addGe(inputNew[c][i], leftside);
                        leftside.clear();
                    }
                }
                else  //Equals t=2, i.e. the value for D^2((x,y)^1) is computed
                {
                    //Orientation constraints (only constraints that are mentioning teta)
                    for(int a = 0; a < outputNew.length; a++)
                    {
                        for(int b = 0; b < dmu.length; b++)
                            leftside.addTerm(outputNew[a][b], lamda[b]);
                        //Adaption required! Before: (1/teta) * outputOld[a][i] <= leftside 
                       cplex.addLe(cplex.prod(teta, outputOld[a][i]), leftside); //Above mentioned constraint change
                        leftside.clear();
                    }
            
                    //Non-Orientation constraints 
                    for(int c = 0; c < inputNew.length; c++)
                    {
                        for(int d = 0; d < dmu.length; d++)
                            leftside.addTerm(inputNew[c][d], lamda[d]);
                
                        cplex.addGe(leftside, inputOld[c][i]);
                        leftside.clear();
                    }                      
                }
            }
            
            //Constraints limiting weight variables                    
            for(int e = 0; e < dmu.length; e++)                            
                leftside.addTerm(1, lamda[e]);
            //if(scale == 1)    //CRS is already covered by the regular non-negativity        
            if(scale == 2)  //VRS aka. BCC: sum of all lamda is equal to 1
                cplex.addEq(leftside, 1);
            else if(scale == 3) //Increasing returns to scale
                cplex.addGe(leftside, 1);
            else if(scale == 4) //Decreasing returns to scale
                cplex.addLe(leftside, 1);
            leftside.clear(); 
                    
            cplex.solve();
            
            if(orientation == true)
                solution[j] = cplex.getObjValue();
            else
                solution[j] = 1 / cplex.getObjValue();
        }
        return solution;  
    }
}
