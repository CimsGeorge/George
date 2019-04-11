package edu.tongji.cims.kgt.service;

import edu.tongji.cims.kgt.model.ontology.RelationshipEnum;

/**
 * @author Yue Lin
 * @version 0.0.1
 */

class CypherService {

    final static String MERGE_CLASS = "merge (n:class{name:{props}.prop1}) on create " +
            "set n.name = {props}.prop1 return n";
    final static String MERGE_INDIVIDUAL = "merge (n:individual{name:{props}.prop1}) on create " +
            "set n.name = {props}.prop1 return n";
    final static String MERGE_RELATIONSHIP = "match (from{name:{props}.prop1}), (to{name:{props}.prop3}) " +
            "merge (from)-[rel:relationship]->(to) set rel.name = {props}.prop2";

    final static String GET_NODE_LABEL = "match (n) where n.name = {props}.prop1 return LABELS(n)";
    final static String GET_DATA_PROPERTY = "match (n) where n.name = {props}.prop1 return n";

    final static String QUERY_NEXT = "match (from{name:{props}.prop1})-[rel]->(to) return to.name";

    final static String CONTAINS_NODE = "match (n{name:{props}.prop1}) return n";

    final static String DELETE_CLASS = "match (from:class{name:{props}.prop1})-[rel]-(to) delete from, rel";
    final static String DELETE_INDIVIDUAL = "match (from:individual{name:{props}.prop1})-[rel]-(to) delete from, rel";
    final static String DELETE_ALL = "match(n) detach delete n";

    static String getNodeByProperty(String key, Boolean fuzzy) {
        if (fuzzy)
            return "match (n) where n." + key + " =~ {props}.prop1 return n.name";
        return "match (n) where n." + key + " = {props}.prop1 return n.name";
    }

    static String setProperty(String componentType, String key) {
        if (componentType.equals("relationship"))
            return "match (from{name:{props}.prop1})-[rel{name:{props}.prop2}]->(to{name:{props}.prop3}) set rel." + key + " = {props}.prop4";
        else
            return "match (n:" + componentType + "{name:{props}.prop1}) set n." + key + " = {props}.prop2";
    }

    final static String SHORTEST_PATH = "match p = shortestpath((from{name: {props}.prop1})-[*]-(to{name: {props}.prop2})) return p,extract(x IN rels(p)| startnode(x)) as dir";
    static String queryPath(int degree) {
//        return "match p=(from:node{name:{props}.name})-[rel*1.." + degree + "]-(to:node) where all(x in rels(p) where x.name <> '实例') RETURN p,extract(x IN rels(p)| startnode(x)) as dir";
        return "match p=(from{name:{props}.prop1})-[rel*0.." + degree + "]-(to) return p,extract(x IN rels(p)| startnode(x)) as dir";
    }



    final static String MERGE_SUB_CLASS = "match (from:class{name:{props}.prop1}), (to:class{name:{props}.prop2}) " +
            "merge (from)-[rel:relationship]->(to) set rel.name = '" + RelationshipEnum.SUB_CLASS.getName() + "'";
    final static String MERGE_ANOTHER_INDIVIDUAL = "match (from:class{name:{props}.prop1}), (to:class{name:{props}.prop2}) " +
            "merge (from)-[rel:relationship]->(to) set rel.name = '" + RelationshipEnum.INDIVIDUAL.getName() + "'";
    // 0: all, 1: in, 2: out
    static String getRelationShip(int dir) {
        if (dir == 0) return "match (n{name:{props}.prop1})-[rel]-() return rel";
        else if (dir == 1) return "match ()-[rel]->(n{name:{props}.prop1}) return rel";
        else return "match (n:{name:{props}.prop1})-[rel]->() return rel";
    }

    private final static String CREATE_TRIPLE = "create (from:node{name:{props}.from})-[rel:edge{name:{props}.rel}]->(to:node{name:{props}.to}) return from, rel, to";
    private final static String CREATE_EDGE = "match (from:node{name:{props}.from}), (to:node{name:{props}.to}) create (from)-[rel:edge{name:{props}.rel}]->(to) return rel";
    private final static String MERGE_TRIPLE = "merge (from:node{name:{props}.from})-[rel:edge{name:{props}.rel}]->(to:node{name:{props}.to})";
    private final static String UPDATE_EDGE = "match (from:node{name:{props}.from})-[rel]->(to:node{name:{props}.to}) set rel.name = {props}.rel return rel";
    private final static String QUERY_INSTANCE = "match (from:node)-[rel:edge{name:{props}.param1}]->(to:node{name:{props}.param2}) return from";
    // todo
//    public final static String QUERY_NODE_IN_FUZZY = "match (n:node) where n.name =~ {props}.name with n, length(keys(n)) as c where c = 1 return n";

    private final static String QUERY_PROPERTY_KEY = "match (n:node {name: {props}.name}) return keys(n)";
    private final static String QUERY_IN_NODE = "match (from:node)-[rel]->(to:node{name:{props}.name}) return from.name,rel.name";



}
