package edu.tongji.cims.kgt.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Yue Lin
 * @since 2019-04-11
 */

@AllArgsConstructor
@Data
public class Edge {

    private String source;
    private String target;
    private String name;
}
