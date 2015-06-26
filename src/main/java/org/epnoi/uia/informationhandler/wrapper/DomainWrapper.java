package org.epnoi.uia.informationhandler.wrapper;

import org.epnoi.model.Content;
import org.epnoi.model.Context;
import org.epnoi.model.Domain;
import org.epnoi.model.ResearchObject;
import org.epnoi.model.Resource;
import org.epnoi.model.Term;
import org.epnoi.uia.core.Core;
import org.epnoi.uia.core.CoreUtility;
import org.epnoi.uia.informationstore.InformationStore;
import org.epnoi.uia.informationstore.InformationStoreHelper;
import org.epnoi.uia.informationstore.Selector;
import org.epnoi.uia.informationstore.SelectorHelper;
import org.epnoi.uia.informationstore.dao.rdf.RDFHelper;

public class DomainWrapper implements Wrapper {
	private Core core;

	public DomainWrapper(Core core) {
		this.core = core;
	}

	// -------------------------------------------------------------------------------------------------------------

	public void put(Resource resource, Context context) {

		InformationStore informationStore = this.core
				.getInformationStoresByType(
						InformationStoreHelper.CASSANDRA_INFORMATION_STORE)
				.get(0);
		informationStore.put(resource, context);

		informationStore = this.core.getInformationStoresByType(
				InformationStoreHelper.RDF_INFORMATION_STORE).get(0);
		informationStore.put(resource, context);

	}

	// -------------------------------------------------------------------------------------------------------------

	public Resource get(String URI) {

		Selector selector = new Selector();
		selector.setProperty(SelectorHelper.TYPE, RDFHelper.DOMAIN_CLASS);
		selector.setProperty(SelectorHelper.URI, URI);

		InformationStore informationStore = this.core
				.getInformationStoresByType(
						InformationStoreHelper.RDF_INFORMATION_STORE).get(0);

		Domain domain = (Domain) informationStore.get(selector);
		String resourcesURI = domain.getResources();

		informationStore = this.core.getInformationStoresByType(
				InformationStoreHelper.CASSANDRA_INFORMATION_STORE).get(0);
		domain = (Domain) informationStore.get(selector);
		domain.setResources(resourcesURI);

		return domain;
	}

	// -------------------------------------------------------------------------------------

	public void remove(String URI) {

		Selector selector = new Selector();
		selector.setProperty(SelectorHelper.TYPE, RDFHelper.DOMAIN_CLASS);
		selector.setProperty(SelectorHelper.URI, URI);

		InformationStore informationStore = this.core
				.getInformationStoresByType(
						InformationStoreHelper.CASSANDRA_INFORMATION_STORE)
				.get(0);

		informationStore.remove(selector);

		informationStore = this.core.getInformationStoresByType(
				InformationStoreHelper.RDF_INFORMATION_STORE).get(0);
		informationStore.remove(selector);

	}

	// -------------------------------------------------------------------------------------

	@Override
	public void update(Resource resource) {
		this.remove(resource.getURI());
		this.put(resource, Context.getEmptyContext());

	}

	// -------------------------------------------------------------------------------------

	@Override
	public boolean exists(String URI) {
		InformationStore informationStore = this.core
				.getInformationStoresByType(
						InformationStoreHelper.CASSANDRA_INFORMATION_STORE)
				.get(0);
		Selector selector = new Selector();
		selector.setProperty(SelectorHelper.TYPE, RDFHelper.DOMAIN_CLASS);
		selector.setProperty(SelectorHelper.URI, URI);
		return informationStore.exists(selector);

	}

	// -------------------------------------------------------------------------------------

	@Override
	public Content<String> getContent(Selector selector) {
		// TODO Auto-generated method stub
		return null;
	}

	// -------------------------------------------------------------------------------------

	@Override
	public void setContent(Selector selector, Content<String> content) {
		// TODO Auto-generated method stub

	}

	// -------------------------------------------------------------------------------------

	@Override
	public Content<Object> getAnnotatedContent(Selector selector) {
		// TODO Auto-generated method stub
		return null;
	}

	// -------------------------------------------------------------------------------------

	@Override
	public void setAnnotatedContent(Selector selector,
			Content<Object> annotatedContent) {
		// TODO Auto-generated method stub

	}

	// -------------------------------------------------------------------------------------

	public static void main(String[] args) {
		Core core = CoreUtility.getUIACore();
		Domain domain = new Domain();
		domain.setURI("http://www.epnoi.org/lauri");
		domain.setExpression("sparqlexpression");
		domain.setLabel("name");
		domain.setType(RDFHelper.DOMAIN_CLASS);

		domain.setResources("http://www.epnoi.org/lauri");
		core.getInformationHandler().put(domain, Context.getEmptyContext());

		System.out.println("-------> "
				+ core.getInformationHandler().get(
						"http://www.epnoi.org/lauri", RDFHelper.DOMAIN_CLASS));

		if (core.getInformationHandler().contains(domain.getURI(),
				RDFHelper.DOMAIN_CLASS)) {
			System.out.println("The domain exists!");
		} else {
			System.out.println("It doesn't exist, something went wrong :(");
		}

		core.getInformationHandler().remove(domain);

		if (!core.getInformationHandler().contains(domain.getURI(),
				RDFHelper.DOMAIN_CLASS)) {
			System.out.println("The domain doesn't exist!");
		} else {
			System.out.println("It exists, something went wrong :(");
		}

	}

	// -------------------------------------------------------------------------------------

}