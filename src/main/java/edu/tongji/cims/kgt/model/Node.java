package edu.tongji.cims.kgt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Node {

    private String name;

    @Override
    public boolean equals(Object obj) {
        Node n = (Node) obj;
        return this.name.equals(n.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
