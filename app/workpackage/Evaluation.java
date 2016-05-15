package workpackage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


public class Evaluation {
    public static double[] getEfficiency(double[][]solution) //Returns rounded efficiency values.
    {
        double[] efficiency = new double[solution.length];

        for(int i = 0; i < efficiency.length; i++) //Fill efficiency values into new array
            efficiency[i] = (double)Math.round(solution[i][solution[0].length-1] * 10000) / 10000; //solution[i][solution[0].length-1]

        return efficiency;
    }

    public static double[] createScaleEfficiency(double[]effCCR, double[]effBCC)    //Returns rounded values!
    {
        //CCR and BCC solutions must be from the same set of DMUs!
        double[]effScale = new double[effCCR.length];

        for(int i = 0; i < effScale.length; i++) //Fill efficiency values into new array
            effScale[i] = (double)Math.round((effCCR[i] / effBCC[i]) * 1000) / 1000;

        return effScale;
    }

    public static double[] createScaleEfficiency(double[][]effCCR, double[][]effBCC)
    {
        //CCR and BCC solutions must be from the same set of DMUs!
        //Seperate method in case no efficiency-array was given to the method.
        double[]CCR = getEfficiency(effCCR);    //Get efficiency of CCR part
        double[]BCC = getEfficiency(effBCC);    //Get efficiency of BCC part

        double[]effScale = createScaleEfficiency(CCR, BCC);

        return effScale;
    }

    public static double[][] createOverview(double[]effCCR, double[]effBCC, double[]effScale, double[]effSBM)
    {
        double[][]overview = new double[4][effCCR.length];

        overview[0] = effCCR;
        overview[1] = effBCC;
        overview[2] = effScale;
        overview[3] = effSBM;

        return overview;
    }

    protected double[][]combineEffInter(double[][]inter, double[]eff)  //Method to create a single, 2-dimensional array out of efficiency and intermediary products
    {
        double[][]combined = new double[inter.length + 1][inter[0].length];  //1st dimension: "amount" of different parameters, 2nd dimension: value for each DMU

        for(int i = 0; i < combined.length; i++)    //Iteration over parameters
            for(int j = 0; j < combined[i].length; j++)  //Iteration over DMU-values
                if(i < (combined.length -1))    //Within borders of inter-array
                    combined[i][j] = inter[i][j];
                else                                //Outside borders of inter-array, i.e. put in efficiency
                    combined[i][j] = eff[j];
        return combined;
    }

    public static double[][]fitEfficiency(double[]eff)    {
        //Method to make a 2-dimenstional array out of the efficiency in order to use it as input for stage 2
        double[][]fitted = new double[1][eff.length];   //1st dimension: "amount" of different parameters, 2nd dimension: value for each DMU
        System.arraycopy(eff, 0, fitted[0], 0, eff.length);
        return fitted;
    }

    public static String[] createReferenceSetDual(String[] dmu, double[][]solution)    {
        //In a dual program, the weights are the DMUs. And since only efficient DMUs are weighted, the weights are indicators for reference units
        String[] reference = new String[solution.length];    //String-array to save reference units

        for(int i = 0; i < solution.length; i++)    //Check if a DMU is efficient. If it is, there are no reference units
            reference[i] = "None";

        for(int i = 0; i < solution.length; i++)
            for(int j = 0; j < dmu.length; j++)   //Check the weights. Once a value differs from 0, the DMU is a reference unit
                if(solution[i][solution[i].length - 1] != 1)
                    if(solution[i][j] != 0)
                        if(i != j)
                            if(reference[i].equals("None") == true)    //No reference unit has been found this far
                                reference[i] = dmu[j];
                            else
                                reference[i] += ", " + dmu[j];

        return reference;
    }
}
