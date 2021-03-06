//////////////////////////////////////////////////////////////////////////////////////////
//
// Implementation of the Blueprints Interface for ArangoDB by triAGENS GmbH Cologne.
//
// Copyright triAGENS GmbH Cologne.
//
//////////////////////////////////////////////////////////////////////////////////////////

package com.tinkerpop.blueprints.impls.arangodb.batch;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.impls.arangodb.client.ArangoDBBaseDocument;
import com.tinkerpop.blueprints.impls.arangodb.client.ArangoDBException;
import com.tinkerpop.blueprints.impls.arangodb.client.ArangoDBSimpleVertex;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.util.StringFactory;

/**
 * @author Achim Brandt (http://www.triagens.de)
 * @author Johannes Gocke (http://www.triagens.de)
 * @author Guido Schwab (http://www.triagens.de)
 */

public class ArangoDBBatchVertex extends ArangoDBBatchElement implements Vertex {

	/**
	 * Creates a vertex
	 * 
	 * @param graph
	 *            a ArangoDBBatchGraph
	 * @param id
	 *            the id (key) of the vertex (can be null)
	 * 
	 * @throws IllegalArgumentException
	 */

	static ArangoDBBatchVertex create(ArangoDBBatchGraph graph, Object id) {
		String key = (id != null) ? id.toString() : null;

		if (key == null) {
			key = graph.getNewId().toString();
		}

		JSONObject properties = new JSONObject();
		try {
			properties.put(ArangoDBBaseDocument._REV, "");
			properties.put(ArangoDBBaseDocument._ID, "");
			properties.put(ArangoDBBaseDocument._KEY, key);
		} catch (JSONException e1) {
		}

		try {
			ArangoDBSimpleVertex v = new ArangoDBSimpleVertex(properties);
			return build(graph, v);
		} catch (ArangoDBException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Creates a vertex by loading it
	 * 
	 * @param graph
	 *            a ArangoDBGraph
	 * @param id
	 *            the id (key) of the vertex (can be null)
	 * 
	 * @throws IllegalArgumentException
	 */

	static ArangoDBBatchVertex load(ArangoDBBatchGraph graph, Object id) {
		if (id == null) {
			throw ExceptionFactory.vertexIdCanNotBeNull();
		}

		String key = id.toString();

		ArangoDBBatchVertex vert = graph.vertexCache.get(key);
		if (vert != null) {
			return vert;
		}

		try {
			ArangoDBSimpleVertex v = graph.client.getVertex(graph.getRawGraph(), key);
			return build(graph, v);
		} catch (ArangoDBException e) {
			// nothing found
			return null;
		}
	}

	static ArangoDBBatchVertex build(ArangoDBBatchGraph graph, ArangoDBSimpleVertex simpleVertex)
			throws ArangoDBException {
		String id = simpleVertex.getDocumentKey();

		ArangoDBBatchVertex vert = graph.vertexCache.get(id);
		if (vert != null) {
			vert.setDocument(simpleVertex);
			return vert;
		}

		ArangoDBBatchVertex newVertex = new ArangoDBBatchVertex(graph, simpleVertex);
		graph.vertexCache.put(newVertex.getRaw().getDocumentKey(), newVertex);
		graph.addCreatedVertex(newVertex);
		return newVertex;
	}

	private ArangoDBBatchVertex(ArangoDBBatchGraph graph, ArangoDBSimpleVertex vertex) {
		this.graph = graph;
		this.document = vertex;
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public Iterable<Edge> getEdges(Direction direction, String... labels) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public Iterable<Vertex> getVertices(Direction direction, String... labels) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public VertexQuery query() {
		throw new UnsupportedOperationException();
	}

	public ArangoDBSimpleVertex getRawVertex() {
		return (ArangoDBSimpleVertex) document;
	}

	public String toString() {
		return StringFactory.vertexString(this);
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	public void save() throws ArangoDBException {
		throw new UnsupportedOperationException();
	}

	public Edge addEdge(String label, Vertex inVertex) {
		return ArangoDBBatchEdge.create(this.graph, null, this, inVertex, label);
	}

}
