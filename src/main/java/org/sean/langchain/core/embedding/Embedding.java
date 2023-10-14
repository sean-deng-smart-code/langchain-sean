package org.sean.langchain.core.embedding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * vector store
 */
public class Embedding {
    private final float[] vector;

    public Embedding(float[] vector) {
        this.vector = vector;
    }

    public float[] getVector() {
        return vector;
    }

    public List<Float> vectorList(){
         List<Float> vectorList = new ArrayList<Float>();
         for(float i:vector){
             vectorList.add(i);
         }
         return vectorList;
    }

    public int dimensions() {
        return vector.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Embedding that = (Embedding) o;
        return Arrays.equals(this.vector, that.vector);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vector);
    }

    @Override
    public String toString() {
        return "Embedding {" +
                " vector = " + Arrays.toString(vector) +
                " }";
    }

    public static Embedding from(float[] vector) {
        return new Embedding(vector);
    }

    public static Embedding from(List<Float> vector) {
        float[] array = new float[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            array[i] = vector.get(i);
        }
        return new Embedding(array);
    }
}
