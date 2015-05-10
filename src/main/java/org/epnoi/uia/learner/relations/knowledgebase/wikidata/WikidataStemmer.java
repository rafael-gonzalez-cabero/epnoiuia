package org.epnoi.uia.learner.relations.knowledgebase.wikidata;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.EnglishStemmer;

import com.google.common.base.Joiner;

public class WikidataStemmer {
	private EnglishStemmer stemmer = new EnglishStemmer();
	Joiner joiner = Joiner.on(" ").skipNulls();

	/**
	 * A method that given an expression of may be multiple words, returns an expression where this words have been stemmed
	 *(currently just for english!)
	 * @param expression a possibly multiword term
	 * @return
	 */

	public String stem(String expression) {
		List<String> result = new ArrayList<String>();
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_0);
		try {
			TokenStream stream = analyzer.tokenStream(null, new StringReader(
					expression));
			stream.reset();
			while (stream.incrementToken()) {

				this.stemmer.setCurrent(stream.getAttribute(
						CharTermAttribute.class).toString());

				this.stemmer.stem();
				result.add(this.stemmer.getCurrent());
			}
		} catch (IOException e) {
			// not thrown b/c we're using a string reader...
			throw new RuntimeException(e);
		}
		analyzer.close();

		return this.joiner.join(result);
	}

	public static void main(String[] args) {
		WikidataStemmer stemmer = new WikidataStemmer();
		int i = 0;

		while (i < 500) {
			long currentTime = System.currentTimeMillis();
			stemmer.stem("Matrix Michele Bachmann amenities pressed her allegations that the former head of her Iowa presidential bid was bribed by the campaign of rival Ron Paul to endorse him, even as one of her own aides denied the charge.");
			i++;

			System.out.println("It took "
					+ (System.currentTimeMillis() - currentTime));
		}
	}
}
