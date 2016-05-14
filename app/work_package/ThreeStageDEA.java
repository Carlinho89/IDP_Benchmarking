/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package work_package;
import ilog.concert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ThreeStageDEA {
    
    //Formulation of a generic (as far as possible) model.
    /*
     * At first, we considered simply creating a class called "MultipleDEA", but we buried that idea after coming up with the following things
     * -At each stage, it could be possible to combine efficiency with intermediary products, use only intermediary products or use only the efficiency
     * -Using a GUI to ask this at each stage did not appear to be very practical
     * -More than 2 or 3 levels can be solved by combining those 2 approaches 
     * -Filing data for x-stages appeared rather complicated, instead, a method combing all the numerous result might be created later on
     */
    private String [] dmu;       //Array of DMUs
    private double [][] input;    //Array for input parameter [Set of parameters][Data of each DMU]
    private double [][] interOne;   //Array for intermediary parameter stage one [Set of parameters][Data of each DMU]
    private double [][] interTwo;   //Array for intermediary parameter stage two [Set of parameters][Data of each DMU]
    private double [][] output;   //Array for output parameter [Set of parameters][Data of each DMU]
    //private double [][] solutionOne;   //Array for solution of stage one [DMU][Weights and efficiencies of each DMU]
    //private double [][] solutionTwo;   //Array for solution of stage two [DMU][Weights and efficiencies of each DMU]
    //private double [][] solutionThree;   //Array for solution of stage three [DMU][Weights and efficiencies of each DMU]
    private boolean [][] settings; //Array containing info about orientation (([0][i]) input = true, output = false) 
                                      //and use of intermediary products (efficiency of stage before ([1][i] = true)) or a mix of both ([2][i] = true)
                                      //Thus: Axis One: Orientation (0), use of efficiency (1) and combination (2)    
    private List<double[][]>solList = new ArrayList<>();;   //List to save solutions of different stages
    //Constructor
    public ThreeStageDEA(String[]dmu, double[][]input, double[][]interOne, double[][]interTwo, double[][]output, boolean[][]ramification)
    {
        this.dmu = dmu;
        this.input = input;
        this.interOne = interOne;
        this.interTwo = interTwo;
        this.output = output; 
        this.settings = ramification;
        
        /*for(int i = 1; i < ramification[0].length; i++)
            if(settings[1][i] == true && settings[2][i] == true)
            try {
                throw new Exception("StageEff und Combination können nicht beide true sein.");
        } catch (Exception ex) {
            System.out.println("StageEff und Combination können nicht beide true sein.");
            Logger.getLogger(TwoStageDEA.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
    public double[][] solveThreeStage(String method)    //Method: Enter CCR, BCC, SBM
    {
        DEA stageOne = new DEA(dmu, input, interOne); 
        double[][]solutionOne = null; 
        try {
            if(method.equals("CCR") == true && settings[0][0] == true)  //Input-Orientation
                solutionOne = stageOne.solve_Dual_Basic_Input(false);    //Solve stage one
            else if(method.equals("CCR") == true && settings[0][0] == false)  //Output-Orientation
                solutionOne = stageOne.solve_Dual_Basic_Output(false);    //Solve stage one
            else if(method.equals("BCC") == true && settings[0][0] == true)  //Input-Orientation
                solutionOne = stageOne.solve_Dual_Basic_Input(true);    //Solve stage one
            else if(method.equals("BCC") == true && settings[0][0] == false)  //Output-Orientation
                solutionOne = stageOne.solve_Dual_Basic_Output(true);    //Solve stage one
            else if(method.equals("SBM") == true && settings[0][0] == true) //Input-Orientation
                solutionOne = stageOne.solve_Dual_SBM_Input();    //Solve stage one
            else if(method.equals("SBM") == true && settings[0][0] == false) //Output-Orientation
                solutionOne = stageOne.solve_Dual_SBM_Output();    //Solve stage one
            /*else 
                throw new Exception("Falsche Methodik");*/
            solList.add(solutionOne);
        } catch (IloException ex) {
            Logger.getLogger(ThreeStageDEA.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Calculate Stage 2
        DEA stageTwo = null; 
        double [][]effOneFitted = null; 
        if(settings[1][1] == true)    //Efficiency of stage 1 is input of stage 2
        {
            double[]effOne = Evaluation.getEfficiency(solutionOne);
            effOneFitted = fitEfficiency(effOne);
            stageTwo = new DEA(dmu, effOneFitted, interTwo);
        }
        else
        {
            if(settings[2][1] == false)    //Input is no combination of efficiency and intermediary product
            {
                stageTwo = new DEA(dmu, interOne, interTwo);
            }
            else
            {
                double[][] combInter = combineEffInter(Evaluation.getEfficiency(solutionOne));
                stageTwo = new DEA(dmu, combInter, interTwo);
            }                
        }
        double[][]solutionTwo = null;
        try {
            if(method.equals("CCR") == true && settings[0][1] == true)  //Input-Orientation
                solutionTwo = stageTwo.solve_Dual_Basic_Input(false);   //Solve stage two
            else if(method.equals("CCR") == true && settings[0][1] == false)  //Output-Orientation
                solutionTwo = stageTwo.solve_Dual_Basic_Output(false);    //Solve stage two
            else if(method.equals("BCC") == true && settings[0][1] == true)  //Input-Orientation
                solutionTwo = stageTwo.solve_Dual_Basic_Input(true);    //Solve stage two
            else if(method.equals("BCC") == true && settings[0][1] == false)  //Output-Orientation
                solutionTwo = stageTwo.solve_Dual_Basic_Output(true);    //Solve stage two
            else if(method.equals("SBM") == true && settings[0][1] == true) //Input-Orientation
                solutionTwo = stageTwo.solve_Dual_SBM_Input();    //Solve stage two
            else if(method.equals("SBM") == true && settings[0][1] == false) //Output-Orientation
                solutionTwo = stageTwo.solve_Dual_SBM_Output();    //Solve stage two
            /*else 
                throw new Exception("Falsche Methodik");*/
            solList.add(solutionTwo);
        } catch (IloException ex) {
            Logger.getLogger(ThreeStageDEA.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        double[][]solutionOneTwo = createSolution(solutionOne, solutionTwo);  //Create a single solution array containing weights and efficiencies. 
        
        //Calculate Stage 3
        DEA stageThree = null; 
        double [][]effTwoFitted = null; 
        if(settings[1][2] == true)    //Efficiency of stage 1 is input of stage 2
        {
            double[]effTwo = Evaluation.getEfficiency(solutionTwo);
            effTwoFitted = fitEfficiency(effTwo);
            stageThree = new DEA(dmu, effTwoFitted, output);
        }
        else
        {
            if(settings[2][2] == false)    //Input is no combination of efficiency and intermediary product
            {
                stageThree = new DEA(dmu, interTwo, output);
            }
            else
            {
                double[][] combInterTwo = combineEffInter(Evaluation.getEfficiency(solutionTwo));
                stageThree = new DEA(dmu, combInterTwo, output);
            }                
        }
        double[][]solutionThree = null;
        try {
            if(method.equals("CCR") == true && settings[0][2] == true)  //Input-Orientation
                solutionThree = stageThree.solve_Dual_Basic_Input(false);    //Solve stage three
            else if(method.equals("CCR") == true && settings[0][2] == false)  //Output-Orientation
                solutionThree = stageThree.solve_Dual_Basic_Output(false);    //Solve stage three
            else if(method.equals("BCC") == true && settings[0][2] == true)  //Input-Orientation
                solutionThree = stageThree.solve_Dual_Basic_Input(true);    //Solve stage three
            else if(method.equals("BCC") == true && settings[0][2] == false)    //Output-Orientation
                solutionThree = stageThree.solve_Dual_Basic_Output(true);   //Solve stage three
            else if(method.equals("SBM") == true && settings[0][2] == true) //Input-Orientation
                solutionThree = stageThree.solve_Dual_SBM_Input();    //Solve stage two
            else if(method.equals("SBM") == true && settings[0][2] == false) //Output-Orientation
                solutionThree = stageThree.solve_Dual_SBM_Output();    //Solve stage two
            /*else 
                throw new Exception("Falsche Methodik");*/
            solList.add(solutionThree);
        } catch (IloException ex) {
            Logger.getLogger(ThreeStageDEA.class.getName()).log(Level.SEVERE, null, ex);
        }

        double[][]solutionComplete = createSolution(solutionOneTwo, solutionThree);  //Create a single solution array containing weights and efficiencies. 
        return solutionComplete; 
    }
    
    public double[][] solveThreeStageSuper(String method, double[]eff)    //Method: Enter CCR, BCC, SBM
    {
        DEA stageOne = new DEA(dmu, input, interOne); 
        double[][]solutionOne = null; 
        try {
            if(method.equals("CCR") == true && settings[0][0] == true)  //Input-Orientation
                solutionOne = stageOne.solve_Radial_SuperEff_Input(eff, false);    //Solve stage one
            else if(method.equals("CCR") == true && settings[0][0] == false)  //Output-Orientation
                solutionOne = stageOne.solve_Radial_SuperEff_Output(eff, false);    //Solve stage one
            else if(method.equals("BCC") == true && settings[0][0] == true)  //Input-Orientation
                solutionOne = stageOne.solve_Radial_SuperEff_Input(eff, true);    //Solve stage one
            else if(method.equals("BCC") == true && settings[0][0] == false)  //Output-Orientation
                solutionOne = stageOne.solve_Radial_SuperEff_Output(eff, true);    //Solve stage one
            else if(method.equals("SBM") == true && settings[0][0] == true) //Input-Orientation
                solutionOne = stageOne.solve_SBM_SuperEff_Input(eff);    //Solve stage one
            else if(method.equals("SBM") == true && settings[0][0] == false) //Output-Orientation
                solutionOne = stageOne.solve_SBM_SuperEff_Output(eff);    //Solve stage one
            /*else 
                throw new Exception("Falsche Methodik");*/
            solList.add(solutionOne);
        } catch (IloException ex) {
            Logger.getLogger(ThreeStageDEA.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Calculate Stage 2
        DEA stageTwo = null; 
        double [][]effOneFitted = null; 
        if(settings[1][1] == true)    //Efficiency of stage 1 is input of stage 2
        {
            double[]effOne = Evaluation.getEfficiency(solutionOne);
            effOneFitted = fitEfficiency(effOne);
            stageTwo = new DEA(dmu, effOneFitted, interTwo);
        }
        else
        {
            if(settings[2][1] == false)    //Input is no combination of efficiency and intermediary product
            {
                stageTwo = new DEA(dmu, interOne, interTwo);
            }
            else
            {
                double[][] combInter = combineEffInter(Evaluation.getEfficiency(solutionOne));
                stageTwo = new DEA(dmu, combInter, interTwo);
            }                
        }
        double[][]solutionTwo = null;
        try {
            if(method.equals("CCR") == true && settings[0][1] == true)  //Input-Orientation
                solutionTwo = stageTwo.solve_Radial_SuperEff_Input(eff, false);   //Solve stage two
            else if(method.equals("CCR") == true && settings[0][1] == false)  //Output-Orientation
                solutionTwo = stageTwo.solve_Radial_SuperEff_Output(eff, false);    //Solve stage two
            else if(method.equals("BCC") == true && settings[0][1] == true)  //Input-Orientation
                solutionTwo = stageTwo.solve_Radial_SuperEff_Input(eff, true);    //Solve stage two
            else if(method.equals("BCC") == true && settings[0][1] == false)  //Output-Orientation
                solutionTwo = stageTwo.solve_Radial_SuperEff_Output(eff, true);    //Solve stage two
            else if(method.equals("SBM") == true && settings[0][1] == true) //Input-Orientation
                solutionTwo = stageTwo.solve_SBM_SuperEff_Input(eff);    //Solve stage two
            else if(method.equals("SBM") == true && settings[0][1] == false) //Output-Orientation
                solutionTwo = stageTwo.solve_SBM_SuperEff_Output(eff);    //Solve stage two
            /*else 
                throw new Exception("Falsche Methodik");*/
            solList.add(solutionTwo);
        } catch (IloException ex) {
            Logger.getLogger(ThreeStageDEA.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        double[][]solutionOneTwo = createSolution(solutionOne, solutionTwo);  //Create a single solution array containing weights and efficiencies. 
        
        //Calculate Stage 3
        DEA stageThree = null; 
        double [][]effTwoFitted = null; 
        if(settings[1][2] == true)    //Efficiency of stage 1 is input of stage 2
        {
            double[]effTwo = Evaluation.getEfficiency(solutionTwo);
            effTwoFitted = fitEfficiency(effTwo);
            stageThree = new DEA(dmu, effTwoFitted, output);
        }
        else
        {
            if(settings[2][2] == false)    //Input is no combination of efficiency and intermediary product
            {
                stageThree = new DEA(dmu, interTwo, output);
            }
            else
            {
                double[][] combInterTwo = combineEffInter(Evaluation.getEfficiency(solutionTwo));
                stageThree = new DEA(dmu, combInterTwo, output);
            }                
        }
        double[][]solutionThree = null;
        try {
            if(method.equals("CCR") == true && settings[0][2] == true)  //Input-Orientation
                solutionThree = stageThree.solve_Radial_SuperEff_Input(eff, false);    //Solve stage three
            else if(method.equals("CCR") == true && settings[0][2] == false)  //Output-Orientation
                solutionThree = stageThree.solve_Radial_SuperEff_Output(eff, false);    //Solve stage three
            else if(method.equals("BCC") == true && settings[0][2] == true)  //Input-Orientation
                solutionThree = stageThree.solve_Radial_SuperEff_Input(eff, true);    //Solve stage three
            else if(method.equals("BCC") == true && settings[0][2] == false)    //Output-Orientation
                solutionThree = stageThree.solve_Radial_SuperEff_Output(eff, true);   //Solve stage three
            else if(method.equals("SBM") == true && settings[0][2] == true) //Input-Orientation
                solutionThree = stageThree.solve_SBM_SuperEff_Input(eff);    //Solve stage two
            else if(method.equals("SBM") == true && settings[0][2] == false) //Output-Orientation
                solutionThree = stageThree.solve_SBM_SuperEff_Output(eff);    //Solve stage two
            /*else 
                throw new Exception("Falsche Methodik");*/
            solList.add(solutionThree);
        } catch (IloException ex) {
            Logger.getLogger(ThreeStageDEA.class.getName()).log(Level.SEVERE, null, ex);
        }

        double[][]solutionComplete = createSolution(solutionOneTwo, solutionThree);  //Create a single solution array containing weights and efficiencies. 
        return solutionComplete; 
    }
    
    public List<double[][]> getSolutionList()
    {
        return this.solList;
    }
    
    private double[][]fitEfficiency(double[]eff)    //Method to make a 2-dimenstional array out of the efficiency in order to use it as input for stage 2
    {
        double[][]fitted = new double[1][eff.length];   //1st dimension: "amount" of different parameters, 2nd dimension: value for each DMU
        System.arraycopy(eff, 0, fitted[0], 0, eff.length); 
        return fitted; 
    }
    
    private double[][]combineEffInter(double[]eff)  //Method to create a single, 2-dimensional array out of efficiency and intermediary products
    {
        double[][]combined = new double[interOne.length + 1][dmu.length];  //1st dimension: "amount" of different parameters, 2nd dimension: value for each DMU
        
        for(int i = 0; i < combined.length; i++)    //Iteration over parameters
        {
            for(int j = 0; j < combined[i].length; j++)  //Iteration over DMU-values
            {
                if(j != (combined[i].length -1))    //Within borders of inter-array
                    combined[i][j] = interOne[i][j];
                else                                //Outside borders of inter-array, i.e. put in efficiency
                    combined[i][j] = eff[j];
            }
        }
        return combined; 
    }
    
    private double[][]createSolution(double[][]solutionOne, double[][]solutionTwo)
    {       
        double[][] solution = new double[solutionOne.length + solutionTwo.length][solutionOne[0].length];
        
        for(int i = 0; i < solutionOne.length; i++)
            System.arraycopy(solutionOne[i], 0, solution[i], 0, solutionOne[i].length);
        for(int i = solutionOne.length; i < solution.length; i++)
            System.arraycopy(solutionTwo[i - solutionOne.length], 0, solution[i], 0, solutionTwo[i - solutionOne.length].length);
        return solution;
    }
}
