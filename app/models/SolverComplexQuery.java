package models;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by carlodidomenico on 08/07/2016.
 */
public class SolverComplexQuery extends SolverSimpleQuery {
    private List<Integer> selectionOffIn;
    private List<Integer> selectionDefIn;
    private List<Integer> selectionSocIn;

    private List<Integer> selectionOffOut;
    private List<Integer> selectionDefOut;
    private List<Integer> selectionAthOut;

    private boolean[][]ramifications;

    public SolverComplexQuery(String query){
        super(query);
        ramifications = new boolean[][]{{false, false}, {false, true}, {false, false}};


        try {

            selectionOffIn = new ArrayList<Integer>();
            selectionDefIn = new ArrayList<Integer>();
            selectionOffOut = new ArrayList<Integer>();
            selectionDefOut = new ArrayList<Integer>();
            selectionAthOut = new ArrayList<Integer>();
            selectionSocIn = new ArrayList<Integer>();


            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(query);

            this.setSeason(rootNode.path("numberOfSeasons").asInt());

            JsonNode selectedOffInputsNode = rootNode.path("offSelectedInputs");
            Iterator<JsonNode> iterator = selectedOffInputsNode.iterator();
            //System.out.print("offSelectedInputs: [ ");

            while (iterator.hasNext()) {
                JsonNode input = iterator.next();
                //System.out.print(input.asInt() + " ");
                selectionOffIn.add(input.asInt());
            }

            //System.out.println("]");

            JsonNode selectedDefInputsNode = rootNode.path("defSelectedInputs");
            iterator = selectedDefInputsNode.iterator();
            //System.out.print("selectedInputs: [ ");

            while (iterator.hasNext()) {
                JsonNode input = iterator.next();
                //System.out.print(input.asInt() + " ");
                selectionDefIn.add(input.asInt());
            }

            JsonNode selectedSocInputsNode = rootNode.path("socSelectedInputs");
            iterator = selectedSocInputsNode.iterator();
            //System.out.print("offSelectedInputs: [ ");

            while (iterator.hasNext()) {
                JsonNode input = iterator.next();
                //System.out.print(input.asInt() + " ");
                selectionSocIn.add(input.asInt());
            }

            JsonNode selectedOffOutputNode = rootNode.path("offSelectedOutputs");
            iterator = selectedOffOutputNode.iterator();
            //System.out.print("offSelectedInputs: [ ");

            while (iterator.hasNext()) {
                JsonNode input = iterator.next();
                //System.out.print(input.asInt() + " ");
                selectionOffOut.add(input.asInt());
            }

            JsonNode selectedDefOutputNode = rootNode.path("defSelectedOutputs");
            iterator = selectedDefOutputNode.iterator();
            //System.out.print("offSelectedInputs: [ ");

            while (iterator.hasNext()) {
                JsonNode input = iterator.next();
                //System.out.print(input.asInt() + " ");
                selectionDefOut.add(input.asInt());
            }

            JsonNode selectedAthOutputNode = rootNode.path("spSelectedOutputs");
            iterator = selectedAthOutputNode.iterator();
            //System.out.print("offSelectedInputs: [ ");

            while (iterator.hasNext()) {
                JsonNode input = iterator.next();
                //System.out.print(input.asInt() + " ");
                selectionAthOut.add(input.asInt());
            }

            //System.out.println("]");
        }
        catch (JsonParseException e) { e.printStackTrace(); }
        catch (JsonMappingException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }




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

    public List<Integer> getSelectionSocIn() {
        return selectionSocIn;
    }

    public void setSelectionSocIn(List<Integer> selectionSocIn) {
        this.selectionSocIn = selectionSocIn;
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
