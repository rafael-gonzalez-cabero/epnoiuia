package org.epnoi.rest.clients;

import org.epnoi.model.rdf.RDFHelper;
import org.epnoi.uia.commons.GateUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import gate.Document;

public class NLPClient {

	public static void main(String[] args) {
		// Core core = CoreUtility.getUIACore();

		String uri = "http://en.wikipedia.org/wiki/Autism/first/object/gate";

		Document document = _retrieveAnnotatedDocument(uri);
		System.out.println("> " + document);
	}

	private static Document _retrieveAnnotatedDocument(String uri) {
		ClientConfig config = new DefaultClientConfig();

		Client client = Client.create(config);
		String basePath = "/uia/nlp/process";

		WebResource service = client.resource("http://localhost:8080/epnoi/rest");

		// http://en.wikipedia.org/wiki/Autism/first/object/gate

		String content = service.path(basePath).queryParam("content", "My taylor is rich and my mother is in the kitchen")
				.type(javax.ws.rs.core.MediaType.APPLICATION_XML).get(String.class);

		Document document = GateUtils.deserializeGATEDocument(content);
		return document;
	}

}