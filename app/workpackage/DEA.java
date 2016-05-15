package workpackage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import ilog.concert.*;
import ilog.cplex.*;


public class DEA {


    //Formulation of a generic (as far as possible) model. The formulation is based on CCR, the use-case is Haas 2003a.
    public final String [] dmu;       //Array of DMUs
    public final double [][] input;    //Array for input parameter [Set of parameters][Data of each DMU]
    public final double [][] output;   //Array for output parameter [Set of parameters][Data of each DMU]

    //Constructor
    public DEA(String[]dmu, double[][]input, double[][]output)
    {
        System.err.println("Debug: DEA Constructor!");
        this.dmu = dmu;
        this.input = input;
        this.output = output;
    }

    public String justAString(){return "just a string";}

    public double[][] solve_Linear_CCR_Input() throws IloException {
        double [][] solution = new double[dmu.length][input.length+output.length+1];

        //Epsilon
        double e = 0.000000001;

        //Considering that the model has to be run for every DMU, the build-up is put in a loop iterating over the DMU-array.
        //An instance of cplex is only created in the loop because creation outside led to an exception.
        for(int i = 0; i < dmu.length; i++)
        {
            //System.out.println("Running " + dmu[i]);
            IloCplex cplex = new IloCplex();    //Create CPLEX-Instance
            cplex.setOut(null);                 //Set empty result

            //Decision variables
            IloNumVar[][] ori = new IloNumVar[dmu.length][input.length];  //Inputn Decision Variables (input weight). Necessary for each DMU and each Input. Therefore: [DMU][Input]
            IloNumVar[][] sec = new IloNumVar[dmu.length][output.length];  //Output Decision Variables (output weight). Necessary for each DMU and each Output. Therefore: [DMU][Output]

            for(int k = 0; k < dmu.length; k++) //Create matrices for saving the appropriate values. For each DMU, an array is created which has enough space for every input or output.
            {
                ori[k] = cplex.numVarArray(input.length, e, 1 - e);
                sec[k] = cplex.numVarArray(output.length, e, 1 - e);
            }

            //Objective function
            IloLinearNumExpr objFunc = cplex.linearNumExpr();
            for(int z = 0; z < output.length; z++)
                objFunc.addTerm(output[z][i], sec[i][z]);
            cplex.addMaximize(objFunc);
            //System.out.println(objFunc.toString());

            //Constraints
            IloLinearNumExpr leftside = cplex.linearNumExpr();  //Expression used for leftside-values of outputs
            IloLinearNumExpr leftside_in = cplex.linearNumExpr();  //Expression used for leftside-values of inputs

            //Set Input to 1
            for(int a = 0; a < input.length; a++)
                leftside.addTerm(input[a][i], ori[i][a]);

            cplex.addEq(leftside, 1);
            //System.out.println(leftside.toString() + " =1"); //Print out Constraint
            leftside.clear();

            //Output Constraints
            for(int b = 0; b < dmu.length; b++)
            {
                for(int d = 0; d < output.length; d++)//Get sum up all outputs of DMU b
                {
                    leftside.addTerm(output[d][b], sec[i][d]);
                }
                for(int c = 0; c < input.length; c++)//Get sum of all Inputs of DMU b
                {
                    leftside_in.addTerm(input[c][b], ori[i][c]);
                }
                cplex.addLe(cplex.diff(leftside, leftside_in), 0.00);  //Create actual constraint
                //System.out.println(cplex.diff(leftside, leftside_in).toString() + "<=0"); //Print out Constraint
                leftside.clear();       //Clear expression for next iteration
                leftside_in.clear();    //Clear expression for next iteration
            }
            cplex.solve();  //Solve current program. ATTENTION: Should this be put elsewehere?
            //Placement here appears to make sense, as the program needs to be solved for each DMU individually. Also, each iteration leads to a new programme

            //Fill up Array with solution values
            for(int g = 0; g < input.length; g++)   //Put input weights into solution-matrix
            {
                solution[i][g] = cplex.getValue(ori[i][g]);
            }
            for(int f = 0; f < output.length; f++)    //Put output weights into solution-matrix
            {
                solution[i][f + input.length - 1] = cplex.getValue(sec[i][f]);
            }
            solution[i][solution[i].length-1] = cplex.getObjValue();     //Put value of objective function into solution-matrix
            //System.out.println(Arrays.toString(solution[i]));
            //System.out.println("Finished " + dmu[i]);
        }
        return solution;
    }

    public double[][] solve_Dual_Basic_Input(boolean bcc) throws IloException {
        //CCR-Case: Modeled acc. to Cooper, Seiford et al. (2007), p. 43
        //BCC-Case: Modeled acc. Cooper, Seiford et al (2007), p. 91
        double [][] solution = new double[dmu.length][dmu.length + 1];

        for(int i = 0; i < dmu.length; i++)
        {
            IloCplex cplex = new IloCplex();    //Create CPLEX-Instance
            cplex.setOut(null);                 //Set empty result

            //Decision variables
            IloNumVar[] lamda = new IloNumVar[dmu.length];  //Vector of decision variables
            lamda = cplex.numVarArray(dmu.length, 0, 1);
            IloNumVar teta = cplex.numVar(0, 1);

            //Objective function
            IloLinearNumExpr objFunc = cplex.linearNumExpr();
            objFunc.addTerm(1, teta);
            cplex.addMinimize(objFunc);

            //Constraints
            IloLinearNumExpr leftside = cplex.linearNumExpr();  //Expression used for leftside-values of constraints

            //Input constraints (only constraints that are mentioning teta)
            for(int a = 0; a < input.length; a++)
            {
                for(int b = 0; b < dmu.length; b++)
                    leftside.addTerm(input[a][b], lamda[b]);

                cplex.addGe(cplex.diff(cplex.prod(teta, input[a][i]), leftside), 0);
                leftside.clear();
            }

            //Output constraints
            for(int c = 0; c < output.length; c++)
            {
                for(int d = 0; d < dmu.length; d++)
                    leftside.addTerm(output[c][d], lamda[d]);

                cplex.addGe(leftside, output[c][i]);
                leftside.clear();
            }

            if(bcc == true) //BCC constraint: sum of all lamda is equal to 1
            {
                for(int e = 0; e < dmu.length; e++)
                    leftside.addTerm(1, lamda[e]);
                cplex.addEq(leftside, 1);

                leftside.clear();
            }
            else
            {
                for(int e = 0; e < dmu.length; e++)
                    cplex.addGe(lamda[e], 0);

                leftside.clear();
            }

            cplex.solve();  //Solve current program

            //Fill up Array with solution values
            for(int g = 0; g < dmu.length; g++)   //Put input weights into solution-matrix
                solution[i][g] = cplex.getValue(lamda[g]);

            solution[i][dmu.length] = cplex.getObjValue();     //Put value of objective function into solution-matrix
        }
        return solution;
    }

    public double[][] solve_Dual_Basic_Output(boolean bcc) throws IloException {
        //CCR-Case: Modeled acc. to Cooper, Seiford et al. (2007), p. 58
        //BCC-Case: Modeled acc. Cooper, Seiford et al (2007), p. 93
        System.out.println("Debug: solve_Dual_Basic_Output");
        double [][] solution = new double[dmu.length][dmu.length + 1];

        System.out.println("Debug: solve_Dual_Basic_Output");

        for(int i = 0; i < dmu.length; i++)
        {
            System.out.println("Debug");

            IloCplex cplex = new IloCplex();    //Create CPLEX-Instance
            cplex.setOut(null);                 //Set empty result

            //Decision Variable
            IloNumVar[] lamda = cplex.numVarArray(dmu.length, 0, 1);   //Weight
            IloNumVar teta = cplex.numVar(0, 10);                        //Efficiency

            //Objective function
            cplex.addMaximize(teta);

            //Constraints
            IloLinearNumExpr leftside = cplex.linearNumExpr();  //Expression used for leftside-values of constraints

            //Input-contraints
            for(int a = 0; a < input.length; a++)
            {
                for(int b = 0; b < input[a].length; b++)
                    leftside.addTerm(input[a][b], lamda[b]);

                cplex.addGe(cplex.diff(input[a][i], leftside), 0);
                leftside.clear();
            }

            //Output-contraints
            for(int a = 0; a < output.length; a++)
            {
                for(int b = 0; b < output[a].length; b++)
                    leftside.addTerm(output[a][b], lamda[b]);

                cplex.addLe(cplex.diff(cplex.prod(teta, output[a][i]), leftside), 0);
                leftside.clear();
            }

            if(bcc == true) //BCC constraint: sum of all lamda is equal to 1
            {
                for(int e = 0; e < dmu.length; e++)
                    leftside.addTerm(1, lamda[e]);
                cplex.addEq(leftside, 1);

                leftside.clear();
            }
            else
            {
                for(int e = 0; e < dmu.length; e++)
                    cplex.addGe(lamda[e], 0);

                leftside.clear();
            }

            cplex.solve();  //Solve current program

            //Fill up Array with solution values
            for(int g = 0; g < dmu.length; g++)   //Put input weights into solution-matrix
                solution[i][g] = cplex.getValue(lamda[g]);

            solution[i][dmu.length] = 1 / cplex.getObjValue();     //Put value of objective function into solution-matrix
        }
        return solution;
    }

    public double[][] solve_Dual_SBM_Input() throws IloException    {
        //Modeled acc. Cooper, Seiford et al (2007), p. 105
        double [][] solution = new double[dmu.length][dmu.length + 1];

        for(int i = 0; i < dmu.length; i++)
        {
            IloCplex cplex = new IloCplex();    //Create CPLEX-Instance
            cplex.setOut(null);                 //Set empty result

            //Decision variables
            IloNumVar[] lamda = cplex.numVarArray(dmu.length, 0, 1);  //Vector of weight decision variables
            IloNumVar[] slack = cplex.numVarArray(input.length, 0, 10000);    //Vector of slack variables
            IloNumVar ro = cplex.numVar(0, 1);

            //Objective function, i.e. theta, question is: how is this computed? Is the single appearance in one constrain sufficient?
            double[]temp = new double[input.length];
            for(int e = 0; e < input.length; e++)
                temp[e] = 1 / input[e][i];
            cplex.addMinimize(cplex.diff(1, cplex.prod(((double)1 / input.length), cplex.scalProd(slack, temp))));

            //Constraints
            IloLinearNumExpr leftside = cplex.linearNumExpr();  //Expression used for leftside-values of constraints

            //Input constraints (only constraints that are mentioning teta)
            for(int a = 0; a < input.length; a++)
            {
                for(int b = 0; b < dmu.length; b++)
                    leftside.addTerm(input[a][b], lamda[b]);

                cplex.addEq(input[a][i], cplex.sum(slack[a], leftside));
                leftside.clear();
            }

            //Output constraints
            for(int c = 0; c < output.length; c++)
            {
                for(int d = 0; d < dmu.length; d++)
                    leftside.addTerm(output[c][d], lamda[d]);

                cplex.addLe(output[c][i], leftside);
                leftside.clear();
            }

            cplex.solve();  //Solve current program

            //Fill up Array with solution values
            for(int g = 0; g < dmu.length; g++)   //Put input weights into solution-matrix
                solution[i][g] = cplex.getValue(lamda[g]);

            solution[i][dmu.length] = cplex.getObjValue();     //Put value of objective function into solution-matrix
        }
        return solution;
    }

    public double[][] solve_Dual_SBM_Output() throws IloException    {
        //Modeled acc. Cooper, Seiford et al (2007), p. 105
        double [][] solution = new double[dmu.length][dmu.length + 1];

        for(int i = 0; i < dmu.length; i++)
        {
            IloCplex cplex = new IloCplex();    //Create CPLEX-Instance
            cplex.setOut(null);                 //Set empty result

            IloNumVar[] lamda = cplex.numVarArray(dmu.length, 0, 1);  //Vector of decision variables for DMUs
            IloNumVar[] slack =  cplex.numVarArray(output.length, 0, 10000); //Vector of slack values for each input parameter

            //Objective function
            //Since input and output arrays are column-oriented arrays, the row of the DMU has to be extracted first
            //In contrary to Cooper, Seiford, et al. (2007), we have decided to maximize here and do the division before the value is written into the solution array.
            //NOTE: Adaptations of constraints necessary?
            double[]temp = new double[output.length];
            for(int e = 0; e < output.length; e++)
                temp[e] = 1 / output[e][i];
            cplex.addMaximize(cplex.sum(1, cplex.prod(((double)1 / output.length), cplex.scalProd(slack, temp))));

            //Constraints
            IloLinearNumExpr leftside = cplex.linearNumExpr();  //Expression used for leftside-values of constraints

            //Input constraints
            for(int a = 0; a < input.length; a++)
            {
                for(int b = 0; b < dmu.length; b++)
                    leftside.addTerm(input[a][b], lamda[b]);

                cplex.addGe(input[a][i], leftside);
                leftside.clear();
            }

            //Output constraints (only constraints containing slack variables)
            for(int c = 0; c < output.length; c++)
            {
                for(int d = 0; d < dmu.length; d++)
                    leftside.addTerm(output[c][d], lamda[d]);

                cplex.addEq(output[c][i], cplex.diff(leftside, slack[c]));
                leftside.clear();
            }

            cplex.solve();  //Solve current program

            //Fill up Array with solution values
            for(int g = 0; g < dmu.length; g++)   //Put input weights into solution-matrix
                solution[i][g] = cplex.getValue(lamda[g]);

            solution[i][dmu.length] = 1 / cplex.getObjValue();     //Put value of objective function into solution-matrix
        }
        return solution;
    }

    public double[][] solve_Radial_SuperEff_Input(double[]eff, boolean bcc) throws IloException  {
        //Modeled acc. Cooper, Seiford et al (2007), p.310 and 317/318
        double [][] solution = new double[dmu.length][dmu.length + 1];

        for(int i = 0; i < dmu.length; i++)
        {
            if(eff[i] == 1) //Is the DMU already efficient?
            {
                IloCplex cplex = new IloCplex();    //Create CPLEX-Instance
                cplex.setOut(null);                 //Set empty result

                //Decision variables
                IloNumVar[] lamda = new IloNumVar[dmu.length];  //Vector of decision variables
                lamda = cplex.numVarArray(dmu.length, 0, 1);
                IloNumVar teta = cplex.numVar(1, 2);

                //Objective function, i.e. theta, question is: how is this computed? Is the single appearance in one constrain sufficient?
                IloLinearNumExpr objFunc = cplex.linearNumExpr();
                objFunc.addTerm(1, teta);
                cplex.addMinimize(objFunc);

                //Constraints
                IloLinearNumExpr leftside = cplex.linearNumExpr();  //Expression used for leftside-values of constraints

                //Input constraints (only constraints that are mentioning teta)
                for(int a = 0; a < input.length; a++)
                {
                    for(int b = 0; b < dmu.length; b++)
                        if(b != i)  //Data from DMU i must not be considered
                            leftside.addTerm(input[a][b], lamda[b]);

                    cplex.addGe(cplex.diff(cplex.prod(teta, input[a][i]), leftside), 0);
                    leftside.clear();
                }

                //Output constraints
                for(int c = 0; c < output.length; c++)
                {
                    for(int d = 0; d < dmu.length; d++)
                        if(d != i)
                            leftside.addTerm(output[c][d], lamda[d]);

                    cplex.addGe(leftside, output[c][i]);
                    leftside.clear();
                }

                if(bcc == true) //BCC constraint: sum of all lamda is equal to 1
                {
                    for(int e = 0; e < dmu.length; e++)
                        leftside.addTerm(1, lamda[e]);
                    cplex.addEq(leftside, 1);

                    leftside.clear();
                }
                else
                {
                    for(int e = 0; e < dmu.length; e++)
                        cplex.addGe(lamda[e], 0);

                    leftside.clear();
                }
                cplex.solve();  //Solve current program

                //Fill up Array with solution values
                for(int g = 0; g < lamda.length; g++)   //Put input weights into solution-matrix
                    solution[i][g] = cplex.getValue(lamda[g]);

                solution[i][dmu.length] = cplex.getObjValue();     //Put value of objective function into solution-matrix
            }
        }
        return solution;
    }

    public double[][] solve_Radial_SuperEff_Output(double[]eff, boolean bcc) throws IloException  {
        //Modeled acc. Cooper, Seiford et al (2007), p.310 and 317/318
        double [][] solution = new double[dmu.length][dmu.length + 1];

        for(int i = 0; i < dmu.length; i++)
        {
            if(eff[i] == 1) //Is the DMU already efficient?
            {
                IloCplex cplex = new IloCplex();    //Create CPLEX-Instance
                cplex.setOut(null);                 //Set empty result

                //Decision variables
                IloNumVar[] lamda = new IloNumVar[dmu.length];  //Vector of decision variables
                lamda = cplex.numVarArray(dmu.length, 0, 1);
                IloNumVar teta = cplex.numVar(0, 10);

                //Objective function, i.e. theta, question is: how is this computed? Is the single appearance in one constrain sufficient?
                IloLinearNumExpr objFunc = cplex.linearNumExpr();
                objFunc.addTerm(1, teta);
                cplex.addMaximize(objFunc);

                //Constraints
                IloLinearNumExpr leftside = cplex.linearNumExpr();  //Expression used for leftside-values of constraints

                //Output constraints (only constraints that are mentioning teta)
                for(int a = 0; a < output.length; a++)
                {
                    for(int b = 0; b < dmu.length; b++)
                        if(b != i)
                            leftside.addTerm(output[a][b], lamda[b]);

                    cplex.addLe(cplex.prod(teta, output[a][i]), leftside);
                    leftside.clear();
                }

                //Input constraints
                for(int c = 0; c < input.length; c++)
                {
                    for(int d = 0; d < dmu.length; d++)
                        if(d != i)
                            leftside.addTerm(input[c][d], lamda[d]);

                    cplex.addLe(leftside, input[c][i]);
                    leftside.clear();
                }

                if(bcc == true) //BCC constraint: sum of all lamda is equal to 1
                {
                    for(int e = 0; e < dmu.length; e++)
                        leftside.addTerm(1, lamda[e]);
                    cplex.addEq(leftside, 1);

                    leftside.clear();
                }
                else
                {
                    for(int e = 0; e < dmu.length; e++)
                        cplex.addGe(lamda[e], 0);

                    leftside.clear();
                }

                cplex.solve();  //Solve current program

                //Fill up Array with solution values
                for(int g = 0; g < dmu.length; g++)   //Put input weights into solution-matrix
                    solution[i][g] = cplex.getValue(lamda[g]);

                solution[i][dmu.length] = 1 / cplex.getObjValue();     //Put value of objective function into solution-matrix
            }
        }
        return solution;
    }

    public double [][] solve_SBM_SuperEff_Input(double[]eff) throws IloException  {
        //Modeled acc. Cooper, Seiford et al (2007), p. 316
        double [][] solution = new double[dmu.length][dmu.length + 1];

        for(int i = 0; i < dmu.length; i++)
        {
            if(eff[i] == 1) //Is the DMU already efficient?
            {
                IloCplex cplex = new IloCplex();    //Create CPLEX-Instance
                cplex.setOut(null);                 //Set empty result

                //Decision variables
                IloNumVar[] lamda = cplex.numVarArray(dmu.length, 0, 1); //Vector of decision variables
                IloNumVar[] teta = cplex.numVarArray(input.length, 0, 1);

                //Objective function
                IloLinearNumExpr leftside = cplex.linearNumExpr();  //Expression used for leftside-values of constraints
                for(int k = 0; k < teta.length; k++)
                    leftside.addTerm(1, teta[k]);
                cplex.addMinimize(cplex.sum(1, cplex.prod(((double)1 / input.length), leftside)));
                leftside.clear();

                //Constraints
                //Input constraints (only constraints that are mentioning teta)
                for(int a = 0; a < input.length; a++)
                {
                    for(int b = 0; b < dmu.length; b++)
                        if(b != i)  //Data from DMU i must not be considered
                            leftside.addTerm(input[a][b], lamda[b]);

                    cplex.addLe(cplex.diff(leftside, cplex.prod(input[a][i], teta[a])), input[a][i]);
                    leftside.clear();
                }

                //Output constraints
                for(int c = 0; c < output.length; c++)
                {
                    for(int d = 0; d < dmu.length; d++)
                        if(d != i)
                            leftside.addTerm(output[c][d], lamda[d]);

                    cplex.addGe(leftside, output[c][i]);
                    leftside.clear();
                }

                for(int e = 0; e < dmu.length; e++)
                    cplex.addGe(lamda[e], 0);

                leftside.clear();

                cplex.solve();  //Solve current program

                //Fill up Array with solution values
                for(int g = 0; g < lamda.length; g++)   //Put input weights into solution-matrix
                    solution[i][g] = cplex.getValue(lamda[g]);

                solution[i][dmu.length] = cplex.getObjValue();     //Put value of objective function into solution-matrix
            }
        }
        return solution;
    }

    public double[][] solve_SBM_SuperEff_Output(double[]eff) throws IloException  {
        //Modeled acc. Cooper, Seiford et al (2007), p.310 and 316
        double [][] solution = new double[dmu.length][dmu.length + 1];

        for(int i = 0; i < dmu.length; i++)
        {
            if(eff[i] == 1) //Is the DMU already efficient?
            {
                IloCplex cplex = new IloCplex();    //Create CPLEX-Instance
                cplex.setOut(null);                 //Set empty result

                //Decision variables
                IloNumVar[] lamda = cplex.numVarArray(dmu.length, 0, 1);  //Vector of decision variables
                IloNumVar[] teta = cplex.numVarArray(output.length, 0, 1);

                //Objective function
                IloLinearNumExpr leftside = cplex.linearNumExpr();  //Expression used for leftside-values of constraints
                for(int k = 0; k < teta.length; k++)
                    leftside.addTerm(1, teta[k]);
                cplex.addMaximize(cplex.diff(1, cplex.prod(((double)1 / output.length), leftside)));
                leftside.clear();

                //Constraints
                //Output constraints (only constraints that are mentioning teta)
                for(int a = 0; a < output.length; a++)
                {
                    for(int b = 0; b < dmu.length; b++)
                        if(b != i)
                            leftside.addTerm(output[a][b], lamda[b]);

                    cplex.addGe(cplex.sum(leftside, cplex.prod(output[a][i], teta[a])), output[a][i]);
                    leftside.clear();
                }

                //Input constraints
                for(int c = 0; c < input.length; c++)
                {
                    for(int d = 0; d < dmu.length; d++)
                        if(d != i)
                            leftside.addTerm(input[c][d], lamda[d]);

                    cplex.addLe(leftside, input[c][i]);
                    leftside.clear();
                }

                for(int e = 0; e < dmu.length; e++)
                    cplex.addGe(lamda[e], 0);

                leftside.clear();

                cplex.solve();  //Solve current program

                //Fill up Array with solution values
                for(int g = 0; g < dmu.length; g++)   //Put input weights into solution-matrix
                    solution[i][g] = cplex.getValue(lamda[g]);

                solution[i][dmu.length] = 1 / cplex.getObjValue();     //Put value of objective function into solution-matrix
            }
        }
        return solution;
    }
}

