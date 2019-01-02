package Model;

        import java.io.Serializable;

/**
 * The class is used to find the 5 most frequent essences
 */
public class TermsPerDoc implements Serializable, Comparable  {


    private int tf;
    private String value;

    /**
     * Constructor- initialize the fields of 'tf' and 'value'
     * @param tf
     * @param value
     */
    public TermsPerDoc(int tf, String value) {
        this.tf = tf;
        this.value = value;

    }

    /**
     * Getter for the tf
     * @return
     */
    public int getTf() {
        return tf;
    }

    /**
     * Getter for the value
     * @return
     */
    public String getValue() {
        return value;
    }


    /**
     * Comparator which compare between two "TermsPerDoc" by the value of tf
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        if (((TermsPerDoc)o).tf>this.tf)
            return -1;
        if (((TermsPerDoc)o).tf<this.tf)
            return 1;
        if (((TermsPerDoc)o).tf==this.tf)
            return 0;
        return 0;
    }

}