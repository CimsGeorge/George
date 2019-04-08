package edu.tongji.cims.kgt.model.neo4j.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@Getter
@Setter
@AllArgsConstructor
public class Statement {

    private String statement;
    private Parameter parameters;

}
