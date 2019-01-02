package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The class represents an object of a term in some query
 */
public class QueryTerm {

    String value;
    //hashMap for docs and amount of thr appearences of the term in the doc
    HashMap<String, Integer> docsAndAmount; //docNo -> amountOfAppearanceInDoc
    boolean isFirstWordInQuery;
    int df;
    //number of appearance of this term in query
    int appearanceInQuery;
    //boolean for synonym
    boolean isSynonym;

    /**
     * Constructor- initialize the fields of the class
     * @param value
     */
    public QueryTerm(String value) {
        /*docs = new ArrayList<String>();
        timesInDocs = new ArrayList<Integer>();*/
        appearanceInQuery =0;
        isFirstWordInQuery = false;
        this.value = value;
        docsAndAmount = new HashMap<String, Integer>();
        df =0;
        isSynonym = false;
    }

    /**
     * Getter for the value of the term
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Getter for "isFirstWordInQuery"
     * @return
     */
    public boolean isFirstWordInQuery() {
        return isFirstWordInQuery;
    }

    /**
     * Setter for "setFirstWordInQuery"
     * @param firstWordInQuery
     */
    public void setFirstWordInQuery(boolean firstWordInQuery) {
        isFirstWordInQuery = firstWordInQuery;
    }

    /**
     * Getter for "docsAndAmount"
     * @return
     */
    public HashMap<String, Integer> getDocsAndAmount() {
        return docsAndAmount;
    }

    /**
     * Getter for "DF"
     * @return
     */
    public int getDf() {
        return df;
    }

    /**
     * Getter for "appearanceInQuery"
     * @return
     */
    public int getAppearanceInQuery() {
        return appearanceInQuery;
    }

    /**
     * Setter for the value
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Setter for "docsAndAmount"
     * @param docsAndAmount
     */
    public void setDocsAndAmount(HashMap<String, Integer> docsAndAmount) {
        this.docsAndAmount = docsAndAmount;
    }

    /**
     * Setter for "DF"
     * @param df
     */
    public void setDf(int df) {
        this.df = df;
    }

    /**
     * Setter for "isSynonym"
     * @param synonym
     */
    public void setSynonym(boolean synonym) {
        isSynonym = synonym;
    }

    /**
     * Getter for "isSynonym"
     * @return
     */
    public boolean isSynonym() {
        return isSynonym;
    }

    /**
     * Setter for appearanceInQuery
     * @param appearanceInQuery
     */
    public void setAppearanceInQuery(int appearanceInQuery) {
        this.appearanceInQuery = appearanceInQuery;
    }
}

