package Model;

import java.util.*;

/**
 * The class ranks the relevat docs of the query by a rank formula
 */
public class Ranker {

    private PriorityQueue<QueryDoc> qDocQueue;
    private int queryLength;

    /**
     * Constructor- initialize the fields of the class
     */
    public Ranker() {
        qDocQueue = new PriorityQueue<QueryDoc>();
        queryLength = 0;

    }


    /**
     * Getter for the qDocQueue
     *
     * @return
     */
    public PriorityQueue<QueryDoc> getqDocQueue() {
        return qDocQueue;
    }

    /**
     * Setter for the qDocQueue
     *
     * @param qDocQueue
     */
    public void setqDocQueue(PriorityQueue<QueryDoc> qDocQueue) {
        this.qDocQueue = qDocQueue;
    }

    /**
     * The method get a queryDoc from the searcher and ranks it according to a rank formula- BM25
     * and other parameters
     *
     * @param currentQueryDoc
     * @param queryLength
     */
    public void getQueryDocFromSearcher(QueryDoc currentQueryDoc, int queryLength) {


        //set the queryLength
        this.queryLength = queryLength;
        //iterator for the QueryTermsInTheQueryDoc
        Iterator it = currentQueryDoc.getQueryTermsInDocsAndQuery().entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();
            QueryTerm currentQueryTerm = (QueryTerm) pair.getValue();
            //set the rank of the doc according to BM25 formula
            currentQueryDoc.setRank(currentQueryDoc.getRank() + BM25func(currentQueryTerm, currentQueryDoc, (double) queryLength));

        }


        checkNumOfCommonWordsDocAndQuery(currentQueryDoc);
        moreThanOneTerm(currentQueryDoc);
        checkLocations(currentQueryDoc);
        checkFirstLocation(currentQueryDoc);
        checkEntities(currentQueryDoc);
        qDocQueue.add(currentQueryDoc);
    }

    private void checkEntities(QueryDoc currentQueryDoc) {

        if(currentQueryDoc.isQueryContainEntitiy){
            currentQueryDoc.setRank(currentQueryDoc.getRank()+0.2);
        }
    }

    /**
     * The method sets the rank of the queryDoc according to the the num of
     * common terms between the query and doc
     *
     * @param currentQueryDoc
     */
    private void checkNumOfCommonWordsDocAndQuery(QueryDoc currentQueryDoc) {
        //duplicate the size of the num of terms in doc and query
        currentQueryDoc.setRank(currentQueryDoc.getRank() * currentQueryDoc.getQueryTermsInDocsAndQuery().size());

    }

    /**
     * The method checks if the doc contains a term at the start of the doc
     * and set it's rank accordingly
     *
     * @param currentQueryDoc
     */
    private void checkFirstLocation(QueryDoc currentQueryDoc) {

        ArrayList<String> locations = currentQueryDoc.getLocations();
        if (locations.contains(0) || locations.contains(1) || locations.contains(2)) {
            currentQueryDoc.setRank(currentQueryDoc.getRank() + 1.2);

        }

    }

    /**
     * The method checks the proximity between the terms of the query at the doc
     * and sets it's rank accordingly
     *
     * @param currentQueryDoc
     */
    private void checkLocations(QueryDoc currentQueryDoc) {

        ArrayList<String> locations = currentQueryDoc.getLocations();
        for (int i = 0; i < locations.size(); i++) {
            for (int j = 0; j < locations.size(); j++) {
                if (locations.get(i).equals(locations.get(j)) || locations.get(i).equals(locations.get(j) + 1)) {

                    currentQueryDoc.setRank(currentQueryDoc.getRank() + 1.2);

                }
            }

        }

    }

    /**
     * The method checks if the doc contains more than one term of the query
     * and ranks it accordingly
     *
     * @param currentQueryDoc
     */
    private void moreThanOneTerm(QueryDoc currentQueryDoc) {

        if (currentQueryDoc.getQueryTermsInDocsAndQuery().size() == 1) {
            currentQueryDoc.setRank(currentQueryDoc.getRank() - 0.2);
        }

    }

    /**
     * The method sets the rank of the queryDoc according to the BM25 formula
     *
     * @param currentQueryTerm
     * @param currentQueryDoc
     * @param queryLength
     * @return
     */
    private double BM25func(QueryTerm currentQueryTerm, QueryDoc currentQueryDoc, double queryLength) {


        double cwq = currentQueryTerm.getAppearanceInQuery();
        double d = currentQueryDoc.getLength();
        double df = currentQueryTerm.getDf();
        double avdl = Searcher.avdl;
        double M = Searcher.numOfDocumentsInCorpus;
        double k = 1.25;
        double b = 0.5;

        //double cwd = currentQueryTerm.getDocsAndAmount().get(currentQueryDoc.getDocNO()) d ; // normalization
        double cwd = currentQueryDoc.queryTermsInDocsAndQuery.get(currentQueryTerm.value).docsAndAmount.get(currentQueryDoc.docNO);

        /*return (Math.log10((M + 1) / df) * cwq * (((b+1) * cwd) / (cwd + (k * ((1-b) + (b * (d / avdl)))))));*/
        if (currentQueryTerm.isFirstWordInQuery())
            return 5 * Math.log10((M - df + 0.5) / (df + 0.5)) * (((k + 1) * cwd) / (cwd + (k * ((1 - b) + (b * (d / avdl))))));

        if (currentQueryTerm.isSynonym() && !currentQueryDoc.isContainsQueryTermInHeader()) {
            return 0.2 * Math.log10((M - df + 0.5) / (df + 0.5)) * (((k + 1) * cwd) / (cwd + (k * ((1 - b) + (b * (d / avdl))))));
        }


        return Math.log10((M - df + 0.5) / (df + 0.5)) * (((k + 1) * cwd) / (cwd + (k * ((1 - b) + (b * (d / avdl))))));


    }


    private void inHeader(QueryDoc curerntQueryDoc) {

        if (curerntQueryDoc.containsQueryTermInHeader) {
            curerntQueryDoc.setRank(curerntQueryDoc.getRank() + 0.2);
        }
    }


}
