package org.sean.langchain.core.chain;

/**
 * this is execute chain
 * @param <Input>
 * @param <Output>
 */
public interface Chain <Input,Output>{

    Output execute(Input input);

}
