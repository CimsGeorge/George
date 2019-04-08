package edu.tongji.cims.kgt.model.neo4j.response;

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
public class Result {

    private List<Data> data;

}
