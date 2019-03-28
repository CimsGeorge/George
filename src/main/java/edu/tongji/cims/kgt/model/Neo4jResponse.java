package edu.tongji.cims.kgt.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@Getter
@Setter
@ToString
public class Neo4jResponse {

    private List<Result> results;
    private List<Object> errors;
}

