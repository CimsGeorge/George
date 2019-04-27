package edu.tongji.cims.kgt.service;

import edu.tongji.cims.kgt.model.ontology.RelationshipEnum;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

class CypherService {

    String MERGE_CLASS = "merge (n:class{name:{props}.prop1}) on create " +
            "set n.name = {props}.prop1 return n";
    String MERGE_INDIVIDUAL = "merge (n:individual{name:{props}.prop1}) on create " +
            "set n.name = {props}.prop1 return n";
    String MERGE_RELATIONSHIP = "match (from{name:{props}.prop1}), (to{name:{props}.prop3}) " +
            "merge (from)-[rel:relationship]->(to) set rel.name = {props}.prop2";

    String GET_NODE_LABEL = "match (n) where n.name = {props}.prop1 return LABELS(n)";
    String GET_DATA_PROPERTY = "match (n) where n.name = {props}.prop1 return n";

    String QUERY_NEXT = "match (from{name:{props}.prop1})-[rel]->(to) return to.name";

    String CONTAINS_NODE = "match (n{name:{props}.prop1}) return n";

    String DELETE_CLASS = "match (from:class{name:{props}.prop1})-[rel]-(to) delete from, rel";
    String DELETE_INDIVIDUAL = "match (from:individual{name:{props}.prop1})-[rel]-(to) delete from, rel";
    String DELETE_ALL = "match(n) detach delete n";

    String GET_INDIVIDUAL = "match (from:class{name:{props}.prop1})-[rel:relationship{name:'individual'}]" +
            "->(to) return to.name";

    String GET_ALL = "match p=(n)-[r]-(t) " +
            "return p, extract(x in rels(p)| startnode(x)) as dir";

    String SHORTEST_PATH = "match p = shortestpath((from{name: {props}.prop1})-[*]-(to{name: {props}.prop2})) " +
            "return p,extract(x IN rels(p)| startnode(x)) as dir";

    String getNodeByProperty(String key, Boolean fuzzy) {
        if (fuzzy)
            return "match (n) where n." + key + " =~ {props}.prop1 return n.name";
        return "match (n) where n." + key + " = {props}.prop1 return n.name";
    }

    String setProperty(String componentType, String key) {
        if (componentType.equals("relationship"))
            return "match (from{name:{props}.prop1})-[rel{name:{props}.prop2}]->(to{name:{props}.prop3}) set rel." + key + " = {props}.prop4";
        else
            return "match (n:" + componentType + "{name:{props}.prop1}) set n." + key + " = {props}.prop2";
    }

    String queryPath(int degree) {
//        return "match p=(from:node{name:{props}.name})-[rel*1.." + degree + "]-(to:node) where all(x in rels(p) where x.name <> '实例') RETURN p,extract(x IN rels(p)| startnode(x)) as dir";
        return "match p=(from{name:{props}.prop1})-[relationships*0.." + degree + "]-(to) return p,extract(x IN rels(p)| startnode(x)) as dir";
    }


    String MERGE_SUB_CLASS = "match (from:class{name:{props}.prop1}), (to:class{name:{props}.prop2}) " +
            "merge (from)-[rel:relationship]->(to) set rel.name = '" + RelationshipEnum.SUB_CLASS.getName() + "'";
    String MERGE_ANOTHER_INDIVIDUAL = "match (from:class{name:{props}.prop1}), (to:class{name:{props}.prop2}) " +
            "merge (from)-[rel:relationship]->(to) set rel.name = '" + RelationshipEnum.INDIVIDUAL.getName() + "'";
    // 0: all, 1: in, 2: out
    String getRelationShip(int dir) {
        if (dir == 0) return "match (n{name:{props}.prop1})-[rel]-() return rel";
        else if (dir == 1) return "match ()-[rel]->(n{name:{props}.prop1}) return rel";
        else return "match (n:{name:{props}.prop1})-[rel]->() return rel";
    }

    String CREATE_TRIPLE = "create (from:node{name:{props}.from})-[rel:edge{name:{props}.rel}]->(to:node{name:{props}.to}) return from, rel, to";
    String CREATE_EDGE = "match (from:node{name:{props}.from}), (to:node{name:{props}.to}) create (from)-[rel:edge{name:{props}.rel}]->(to) return rel";
    String MERGE_TRIPLE = "merge (from:node{name:{props}.from})-[rel:edge{name:{props}.rel}]->(to:node{name:{props}.to})";
    String UPDATE_EDGE = "match (from:node{name:{props}.from})-[rel]->(to:node{name:{props}.to}) set rel.name = {props}.rel return rel";
    String QUERY_INSTANCE = "match (from:node)-[rel:edge{name:{props}.param1}]->(to:node{name:{props}.param2}) return from";
    // todo
//    public final static String QUERY_NODE_IN_FUZZY = "match (n:node) where n.name =~ {props}.name with n, length(keys(n)) as c where c = 1 return n";

    String QUERY_PROPERTY_KEY = "match (n:node {name: {props}.name}) return keys(n)";
    String QUERY_IN_NODE = "match (from:node)-[rel]->(to:node{name:{props}.name}) return from.name,rel.name";



}
