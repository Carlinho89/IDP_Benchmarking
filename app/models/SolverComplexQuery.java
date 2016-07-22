package models;

import java.util.List;

/**
 * Created by carlodidomenico on 08/07/2016.
 */
public class SolverComplexQuery extends SolverSimpleQuery {
    private List<Integer> selectionOffIn;
    private List<Integer> selectionDefIn;
    private List<Integer> selectionOffOut;
    private List<Integer> selectionDefOut;
    private List<Integer> selectionAthOut;

    private List<Integer> selectionSocOut;
    private boolean[][]ramifications;

    public SolverComplexQuery(String query){
        super(query);
        this.ramifications = new boolean[][]{{false, false}, {false, true}, {false, false}};
    }

    /*
    {
    "leagueID": "2",
    "leagueName": "Premier",
    "season": 2014,
    "superEff": true,
    "numberOfSeasons": 2,
    "solver": "complex",
    "selectedInputs": [
        20,
        14,
        12
    ],
    "selectedInputsNames": [
        "Number Of Players",
        "Shots On Target Per Game",
        "Tackles Per Game"
    ],
    "offSelectedInputs": [
        14
    ],
    "offSelectedInputsNames": [
        "Shots On Target Per Game"
    ],
    "socSelectedInputs": [
        20
    ],
    "socSelectedInputsNames": [
        "Number Of Players"
    ],
    "defSelectedInputs": [
        12
    ],
    "defSelectedInputsNames": [
        "Tackles Per Game"
    ],
    "selectedOutputs": [
        6,
        4,
        5
    ],
    "selectedOutputsNames": [
        "Rank",
        "Goals Scored",
        "Goals Against"
    ],
    "spSelectedOutputs": [
        6
    ],
    "spSelectedOutputsNames": [
        "Rank"
    ],
    "offSelectedOutputs": [
        4
    ],
    "offSelectedOutputsNames": [
        "Goals Scored"
    ],
    "defSelectedOutputs": [
        5
    ],
    "defSelectedOutputsNames": [
        "Goals Against"
    ],
    "teamID": "11",
    "teamName": "Arsenal"
}
    {"leagueID":"2","leagueName":"Premier","season":2014,"superEff":true,"numberOfSeasons":2,"solver":"complex","selectedInputs":[20,14,12],"selectedInputsNames":["Number Of Players","Shots On Target Per Game","Tackles Per Game"],"offSelectedInputs":[14],"offSelectedInputsNames":["Shots On Target Per Game"],"socSelectedInputs":[20],"socSelectedInputsNames":["Number Of Players"],"defSelectedInputs":[12],"defSelectedInputsNames":["Tackles Per Game"],"selectedOutputs":[6,4,5],"selectedOutputsNames":["Rank","Goals Scored","Goals Against"],"spSelectedOutputs":[6],"spSelectedOutputsNames":["Rank"],"offSelectedOutputs":[4],"offSelectedOutputsNames":["Goals Scored"],"defSelectedOutputs":[5],"defSelectedOutputsNames":["Goals Against"],"teamID":"11","teamName":"Arsenal"}
*/
    public boolean[][] getRamifications() {
        return ramifications;
    }

    public void setRamifications(boolean[][] ramifications) {
        this.ramifications = ramifications;
    }

    public List<Integer> getSelectionSocOut() {
        return selectionSocOut;
    }

    public void setSelectionSocOut(List<Integer> selectionSocOut) {
        this.selectionSocOut = selectionSocOut;
    }

    public List<Integer> getSelectionDefIn() {
        return selectionDefIn;
    }

    public void setSelectionDefIn(List<Integer> selectionDefIn) {
        this.selectionDefIn = selectionDefIn;
    }

    public List<Integer> getSelectionOffOut() {
        return selectionOffOut;
    }

    public void setSelectionOffOut(List<Integer> selectionOffOut) {
        this.selectionOffOut = selectionOffOut;
    }

    public List<Integer> getSelectionAthOut() {
        return selectionAthOut;
    }

    public void setSelectionAthOut(List<Integer> selectionAthOut) {
        this.selectionAthOut = selectionAthOut;
    }

    public List<Integer> getSelectionDefOut() {
        return selectionDefOut;
    }

    public void setSelectionDefOut(List<Integer> selectionDefOut) {
        this.selectionDefOut = selectionDefOut;
    }

    public List<Integer> getSelectionOffIn() {
        return selectionOffIn;
    }

    public void setSelectionOffIn(List<Integer> selectionOffIn) {
        this.selectionOffIn = selectionOffIn;
    }

}
