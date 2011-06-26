package org.neo4j.kernel;

import java.util.Iterator;
import java.util.LinkedList;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.traversal.TraversalBranch;

public class BidirectionalTraversalBranchPath implements Path
{
    private final TraversalBranch start;
    private final TraversalBranch end;
    private final Node endNode;
    private final Relationship lastRelationship;

    public BidirectionalTraversalBranchPath( TraversalBranch start, TraversalBranch end )
    {
        this.start = start;
        this.end = end;
        
        Iterator<PropertyContainer> endPathEntities = end.iterator();
        this.endNode = (Node) endPathEntities.next();
        this.lastRelationship = endPathEntities.hasNext() ?
                (Relationship) endPathEntities.next() : start.lastRelationship();
    }

    @Override
    public Node startNode()
    {
        return start.startNode();
    }

    @Override
    public Node endNode()
    {
        return this.endNode;
    }

    @Override
    public Relationship lastRelationship()
    {
        return this.lastRelationship;
    }

    @Override
    public Iterable<Relationship> relationships()
    {
        // TODO Don't loop through them all up front
        LinkedList<Relationship> relationships = new LinkedList<Relationship>();
        TraversalBranch branch = start;
        while ( branch.length() > 0 )
        {
            relationships.addFirst( branch.lastRelationship() );
            branch = branch.parent();
        }
        branch = end;
        while ( branch.length() > 0 )
        {
            relationships.add( branch.lastRelationship() );
            branch = branch.parent();
        }
        return relationships;
    }

    @Override
    public Iterable<Node> nodes()
    {
        // TODO Don't loop through them all up front
        LinkedList<Node> nodes = new LinkedList<Node>();
        TraversalBranch branch = start;
        while ( branch.length() > 0 )
        {
            nodes.addFirst( branch.endNode() );
            branch = branch.parent();
        }
        nodes.addFirst( branch.endNode() );
        branch = end.parent();
        if ( branch != null )
        {
            while ( branch.length() > 0 )
            {
                nodes.add( branch.endNode() );
                branch = branch.parent();
            }
            nodes.add( branch.endNode() );
        }
        return nodes;
    }

    @Override
    public int length()
    {
        return start.length() + end.length();
    }

    @Override
    public Iterator<PropertyContainer> iterator()
    {
        // TODO Don't loop through them all up front
        LinkedList<PropertyContainer> entities = new LinkedList<PropertyContainer>();
        TraversalBranch branch = start;
        while ( branch.length() > 0 )
        {
            entities.addFirst( branch.endNode() );
            entities.addFirst( branch.lastRelationship() );
            branch = branch.parent();
        }
        branch = end.parent();
        if ( branch != null )
        {
            while ( branch.length() > 0 )
            {
                entities.add( branch.endNode() );
                entities.add( branch.lastRelationship() );
                branch = branch.parent();
            }
            entities.add( branch.endNode() );
        }
        return entities.iterator();
    }
    
    @Override
    public int hashCode()
    {
        return start.hashCode() | (end.hashCode()<<16);
    }
    
    @Override
    public boolean equals( Object obj )
    {
        if ( obj == this)
        {
            return true;
        }
        if ( !( obj instanceof Path ) )
        {
            return false;
        }
        
        // TODO Implement for real
        return relationships().equals( ((Path) obj).relationships() );
    }
}