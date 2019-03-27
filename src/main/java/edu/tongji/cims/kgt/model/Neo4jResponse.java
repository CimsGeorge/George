package edu.tongji.cims.kgt.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Yue Lin
 * @since 2018-11-06
 */

@Setter
@Getter
@ToString
public class Neo4jResponse {

    private List<Result> results;
    private Object errors;

}

