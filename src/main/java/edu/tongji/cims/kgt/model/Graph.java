package edu.tongji.cims.kgt.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@Getter
@Setter
@ToString
public class Graph {

    private Set<Node> nodes;
    private Set<Link> links;
}
