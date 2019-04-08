package edu.tongji.cims.kgt.model.neo4j.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@Getter
@Setter
@AllArgsConstructor
public class Neo4jRequest {

    private List<Statement> statements;

}
