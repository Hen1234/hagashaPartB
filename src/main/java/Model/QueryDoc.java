package Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The class represents an object of a doc which relevant to some query
 */
public class QueryDoc implements Comparable {

    String docNO;
    //hashMap of the QueryTerms included the QueryDoc
    HashMap<String, QueryTerm> queryTermsInDocsAndQuery;
    //length of the QueryDoc
    int length;
    //rank of the QueryDoc
    double rank;
    //boolean field for containing QueryTerm in the header of the doc
    boolean containsQueryTermInHeader;
    //boolean field for containing QueryTerm in the 5 most entity of the doc
    boolean isQueryContainEntitiy;
    //ArrayList for the locations of the QuryTerms in the QueryDoc
    ArrayList<String> locations;

    /**
     * Constructor- initialize the fields of the class
     * @param docNO
     */
    public QueryDoc(String docNO) {
        this.docNO = docNO;
        queryTermsInDocsAndQuery = new HashMap<>();
        containsQueryTermInHeader=false;
        locations = new ArrayList<>();
    }

    /**
     * Getter for "isContainsQueryTermInHeader"
     * @return
     */
    public boolean isContainsQueryTermInHeader() {
        return containsQueryTermInHeader;
    }

    /**
     * Setter for "setContainsQueryTermInHeader"
     * @param containsQueryTermInHeader
     */
    public void setContainsQueryTermInHeader(boolean containsQueryTermInHeader) {
        this.containsQueryTermInHeader = containsQueryTermInHeader;
    }

    /**
     * Getter for the locations
     * @return
     */
    public ArrayList<String> getLocations() {
        return locations;
    }


    /**
     * Getter for "isQueryContainEntitiy"
     * @return
     */
    public boolean isQueryContainEntitiy() {
        return isQueryContainEntitiy;
    }

    /**
     * Setter for "setQueryContainEntitiy"
     * @param queryContainEntitiy
     */
    public void setQueryContainEntitiy(boolean queryContainEntitiy) {
        isQueryContainEntitiy = queryContainEntitiy;
    }

    /**
     * Getter for the DocNo
     * @return
     */
    public String getDocNO() {
        return docNO;
    }

    /**
     * Getter for the hashMap of "queryTermsInDocsAndQuery"
     * @return
     */
    public HashMap<String, QueryTerm> getQueryTermsInDocsAndQuery() {
        return queryTermsInDocsAndQuery;
    }

    /**
     * Getter for the length of the doc
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     * Setter for the DocNo
     * @param docNO
     */
    public void setDocNO(String docNO) {
        this.docNO = docNO;
    }

    /**
     * Setter for "queryTermsInDocsAndQuery"
     * @param queryTermsInDocsAndQuery
     */
    public void setQueryTermsInDocsAndQuery(HashMap<String, QueryTerm> queryTermsInDocsAndQuery) {
        this.queryTermsInDocsAndQuery = queryTermsInDocsAndQuery;
    }

    /**
     * Setter for the length of the doc
     * @param length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Getter for the rank of the doc
     * @return
     */
    public double getRank() {
        return rank;
    }

    /**
     * Setter for the rank of the doc
     * @param rank
     */
    public void setRank(double rank) {
        this.rank = rank;
    }

    /**
     * Override the method "compareTo" by the rank
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        if (((QueryDoc)o).rank<this.rank)
            return -1;
        if (((QueryDoc)o).rank>this.rank)
            return 1;
        if (((QueryDoc)o).rank==this.rank)
            return 0;
        return 0;
    }

    /**
     * Override the method "toString"
     * @return
     */
    @Override
    public String toString() {
        return "docNO='" + docNO + '\'' +
                ", rank=" + rank +
                '}';
    }

}
