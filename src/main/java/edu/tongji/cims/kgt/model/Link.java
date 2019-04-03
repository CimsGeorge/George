package edu.tongji.cims.kgt.model;

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

//    @Override
//    public boolean equals(Object obj) {
//        Link l = (Link) obj;
//        return this.source.equals(l.source) &&
//                this.target.equals(l.target) &&
//                this.name.equals(l.name);
//    }
//
//    @Override
//    public int hashCode() {
//        int hash = 17;
//        hash = 31 * hash + this.source.hashCode();
//        hash = 31 * hash + this.target.hashCode();
//        hash = 31 * hash + this.name.hashCode();
//        return hash;
//    }
}
