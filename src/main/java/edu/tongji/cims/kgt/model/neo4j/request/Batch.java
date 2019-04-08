package edu.tongji.cims.kgt.model.neo4j.request;

import edu.tongji.cims.kgt.model.neo4j.request.Parameter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@NoArgsConstructor
@ToString
public class Batch {

    public List<String> statements = new ArrayList<>();
    public List<Parameter> parameters = new ArrayList<>();

}
