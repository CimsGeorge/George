package edu.tongji.cims.kgt.model;

/**
 * @author Yue Lin
 * @version 0.0.1
 */
public class Cypher {

    public final static String QUERY_NODE = "match (n:node {name: {props}.name}) return n";
    public final static String MERGE_NODE = "merge (n:node {name: {props}.name}) on create set n = {props} return n";
    public final static String MERGE_EDGE = "match (from:node{name:{props}.from}), (to:node{name:{props}.to}) merge (from)-[rel:edge]->(to) set rel.name = {props}.rel";
    public final static String QUERY_NODE_IN_FUZZY = "match (n:node) where n.name =~ {props}.name return n";

    public static String queryPath(int degree) {
//        return "match p=(from:node{name:{props}.name})-[rel*1.." + degree + "]-(to:node) where all(x in rels(p) where x.name <> '实例') RETURN p,extract(x IN rels(p)| startnode(x)) as dir";
        return "match p=(from:node{name:{props}.name})-[rel*0.." + degree + "]-(to:node) return p,extract(x IN rels(p)| startnode(x)) as dir";
    }
    public final static String SHORTEST_PATH = "match p = shortestpath((from:node{name: {props}.param1})-[*]-(to:node{name: {props}.param2})) return p,extract(x IN rels(p)| startnode(x)) as dir";

    public final static String CONTAINS_NODE = "match (n:node{name: {props}.name}) return n";
    // 0: all, 1: in, 2: out
    public static String getRelationShip(int dir) {
        if (dir == 0)
            return "match (n:node {name: {props}.name})-[rel]-() return rel.name";
        else if (dir == 1)
            return "match ()-[rel]->(n:node {name: {props}.name}) return rel.name";
        else
            return "match (n:node {name: {props}.name})-[rel]->() return rel.name";
    }

    public static String setProperty(String key) {
        return "match (n:node{name:{props}.param1}) set n." + key + " = {props}.param2";
    }


    public final static String SET_INSTANCE_ALL_PROPERTY = "match (n:node {name: {props}.name}) set n = {props}";
    public final static String MERGE_NODE_BY_NAME_AND_FIELD = "MERGE (n:node{name:{props}.param1}) " +
            "ON CREATE SET n = {name:{props}.param1, field:[{props}.param2]} " +
            "ON MATCH SET n.field = " +
            "case " +
            "when all(x IN n.field WHERE x <> {props}.param2) " +
            "then n.field + {props}.param2 " +
            "else n.field " +
            "end";
    public final static String CREATE_NODE = "create (n:node {props}) return n";
    public final static String CREATE_TRIPLE = "create (from:node{name:{props}.from})-[rel:edge{name:{props}.rel}]->(to:node{name:{props}.to}) return from, rel, to";
    public final static String CREATE_EDGE = "match (from:node{name:{props}.from}), (to:node{name:{props}.to}) create (from)-[rel:edge{name:{props}.rel}]->(to) return rel";
    public final static String MERGE_TRIPLE = "merge (from:node{name:{props}.from})-[rel:edge{name:{props}.rel}]->(to:node{name:{props}.to})";
    public final static String UPDATE_EDGE = "match (from:node{name:{props}.from})-[rel]->(to:node{name:{props}.to}) set rel.name = {props}.rel return rel";
    public final static String QUERY_INSTANCE = "match (from:node)-[rel:edge{name:{props}.param1}]->(to:node{name:{props}.param2}) return from";
    // todo
//    public final static String QUERY_NODE_IN_FUZZY = "match (n:node) where n.name =~ {props}.name with n, length(keys(n)) as c where c = 1 return n";

    public final static String QUERY_PROPERTY_KEY = "match (n:node {name: {props}.name}) return keys(n)";
    public final static String DELETE_ALL = "match(n) detach delete n";
    public final static String QUERY_IN_NODE = "match (from:node)-[rel]->(to:node{name:{props}.name}) return from.name,rel.name";
    public final static String QUERY_NEXT = "match (from:node{name:{props}.name})-[rel]->(to:node) return to";



}
