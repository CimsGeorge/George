package edu.tongji.cims.kgt.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Yue Lin
 * @since 2018-12-15
 */

@Getter
@Setter
public class Statement {

    private String statement;
    private Parameter parameters;

    public Statement(String statement, Parameter parameters) {
        this.statement = statement;
        this.parameters = parameters;
    }
}
