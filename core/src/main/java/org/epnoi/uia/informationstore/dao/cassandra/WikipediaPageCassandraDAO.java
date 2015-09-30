package org.epnoi.uia.informationstore.dao.cassandra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.epnoi.model.Content;
import org.epnoi.model.Context;
import org.epnoi.model.Resource;
import org.epnoi.model.WikipediaPage;
import org.epnoi.model.rdf.RDFHelper;
import org.epnoi.uia.core.Core;
import org.epnoi.uia.core.CoreUtility;
import org.epnoi.uia.informationstore.Selector;
import org.epnoi.uia.informationstore.SelectorHelper;

import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.hector.api.beans.HColumn;

public class WikipediaPageCassandraDAO extends CassandraDAO {
	private static final Pattern pattern = Pattern.compile("\\[[^\\]]*\\]");

	public void remove(String URI) {
		super.deleteRow(URI, WikipediaPageCassandraHelper.COLUMN_FAMILY);
	}

	// --------------------------------------------------------------------------------

	public void create(Resource resource, Context context) {

		WikipediaPage wikipediaPage = (WikipediaPage) resource;

		Map<String, String> pairsOfNameValues = new HashMap<String, String>();

		super.createRow(wikipediaPage.getURI(), WikipediaPageCassandraHelper.COLUMN_FAMILY);

		pairsOfNameValues.put(WikipediaPageCassandraHelper.TERM, wikipediaPage.getTerm());

		String termDefinition = (wikipediaPage.getTermDefinition() == null) ? "" : wikipediaPage.getTermDefinition();

		pairsOfNameValues.put(WikipediaPageCassandraHelper.TERM_DEFINITION, termDefinition);

		super.updateColumns(wikipediaPage.getURI(), pairsOfNameValues, WikipediaPageCassandraHelper.COLUMN_FAMILY);

		pairsOfNameValues = null;

		_createWikipediaPageSections(wikipediaPage);

	}

	// -------------------------------------------------------------------------------------------------------------------

	private void _createWikipediaPageSections(WikipediaPage wikipediaPage) {
		Map<String, String> pairsOfNameValues = new HashMap<String, String>();
		String sectionContent;

		for (String section : wikipediaPage.getSections()) {

			sectionContent = wikipediaPage.getSectionsContent().get(section);
			pairsOfNameValues.put(section, sectionContent);

		}
		super.updateColumns(wikipediaPage.getURI() + "/" + WikipediaPageCassandraHelper.CONTENT, pairsOfNameValues,
				WikipediaPageCassandraHelper.COLUMN_FAMILY);
	}

	// --------------------------------------------------------------------------------

	public Resource read(Selector selector) {

		return null;
	}

	// --------------------------------------------------------------------------------

	public Resource read(String URI) {

		ColumnSliceIterator<String, String, String> columnsIterator = super.getAllCollumns(URI,
				WikipediaPageCassandraHelper.COLUMN_FAMILY);
		List<String> sectionsContent = new ArrayList<String>();
		if (columnsIterator.hasNext()) {
			WikipediaPage paper = new WikipediaPage();
			paper.setURI(URI);
			while (columnsIterator.hasNext()) {
				HColumn<String, String> column = columnsIterator.next();
				if (WikipediaPageCassandraHelper.TERM.equals(column.getName())) {
					paper.setTerm(column.getValue());

				} else if (WikipediaPageCassandraHelper.TERM_DEFINITION.equals(column.getName())) {
					paper.setTermDefinition(column.getValue());

				}

			}

			_readSections(paper);
			return paper;
		}

		return null;
	}

	private void _readSections(WikipediaPage page) {

		ColumnSliceIterator<String, String, String> columnsIterator = super.getAllCollumns(
				page.getURI() + "/" + WikipediaPageCassandraHelper.CONTENT, WikipediaPageCassandraHelper.COLUMN_FAMILY);

		if (columnsIterator.hasNext()) {

			while (columnsIterator.hasNext()) {
				HColumn<String, String> column = columnsIterator.next();
				String section = column.getName();
				String sectionContent = column.getValue();
				page.getSections().add(section);
				page.getSectionsContent().put(section, sectionContent);
			}

		}

	}
	// --------------------------------------------------------------------------------

	@Override
	public Content<String> getContent(Selector selector) {

		throw (new RuntimeException("The getContent method of the WikipediaPageCassandraDAO should not be invoked"));
	}

	// --------------------------------------------------------------------------------

	@Override
	public Content<String> getAnnotatedContent(Selector selector) {

		throw (new RuntimeException("The setContent method of the WikipediaPageCassandraDAO should not be invoked"));
	}

	// --------------------------------------------------------------------------------

	@Override
	public void setContent(Selector selector, Content<String> content) {

		throw (new RuntimeException("The setContent method of the WikipediaPageCassandraDAO should not be invoked"));
	}

	// --------------------------------------------------------------------------------

	@Override
	public void setAnnotatedContent(Selector selector, Content<String> annotatedContent) {

		throw (new RuntimeException("The setContent method of the WikipediaPageCassandraDAO should not be invoked"));

	}

	// --------------------------------------------------------------------------------

	public static void main(String[] args) {
		System.out.println("WikipediaPage Cassandra Test--------------");
		System.out.println("Initialization --------------------------------------------");
		/*
		 * WikipediaPageCassandraDAO wikipediaPageCassandraDAO = new
		 * WikipediaPageCassandraDAO();
		 * 
		 * wikipediaPageCassandraDAO.init();
		 */
		System.out.println(" --------------------------------------------");
		String URI = "http://externalresourceuri";
		WikipediaPage wikipediaPage = new WikipediaPage();
		wikipediaPage.setURI(URI);
		wikipediaPage.setTerm("Proof Term");
		wikipediaPage.setTermDefinition("Proof Term is whatever bla bla bla");
		wikipediaPage.setSections(Arrays.asList("first", "middle section", "references"));
		wikipediaPage.setSectionsContent(new HashMap<String, String>());
		wikipediaPage.getSectionsContent().put("first", "This is the content of the first section");
		wikipediaPage.getSectionsContent().put("middle section", "This is the content of the middle section");
		wikipediaPage.getSectionsContent().put("references", "This is the content for the references");

		// wikipediaPageCassandraDAO.create(wikipediaPage,
		// Context.getEmptyContext());
		/*
		 * System.out.println(
		 * "Reading the wikipedia page-------------------------------------------"
		 * ); WikipediaPage readedPage=
		 * (WikipediaPage)wikipediaPageCassandraDAO.read(
		 * "http://externalresourceuri");
		 */
		// WikipediaPage page = (WikipediaPage) wikipediaPageCassandraDAO
		// .read("http://en.wikipedia.org/wiki/Glossary_of_American_football");

		// System.out.println("page> " + page);

		Core core = CoreUtility.getUIACore();
		core.getInformationHandler().remove(URI, RDFHelper.WIKIPEDIA_PAGE_CLASS);
		core.getInformationHandler().put(wikipediaPage, Context.getEmptyContext());

		WikipediaPage readedPage = (WikipediaPage) core.getInformationHandler().get(URI,
				RDFHelper.WIKIPEDIA_PAGE_CLASS);

		for (String content : readedPage.getSections()) {
			System.out.println("-----------------------------------------------------------------");
			System.out.println("---> " + content);
			System.out.println("---> " + readedPage.getSectionsContent().get(content));
		}

	}

	// --------------------------------------------------------------------------------

	@Override
	public boolean exists(Selector selector) {

		return (super.getAllCollumns(selector.getProperty(SelectorHelper.URI),
				WikipediaPageCassandraHelper.COLUMN_FAMILY).hasNext());

	}
}
