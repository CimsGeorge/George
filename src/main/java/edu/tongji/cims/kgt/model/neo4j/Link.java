package edu.tongji.cims.kgt.model.neo4j;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@AllArgsConstructor
@Data
public class Link {

    private String source;
    private String target;
    private String name;

}
