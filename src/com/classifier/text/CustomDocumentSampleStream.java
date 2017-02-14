/**
 * 
 */
package com.classifier.text;

import java.io.IOException;

import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.ObjectStream;

/**
 * @author Sandeep
 *
 */
public class CustomDocumentSampleStream extends DocumentSampleStream {

	public CustomDocumentSampleStream(ObjectStream<String> samples) {
		super(samples);
	}

	@Override
	public DocumentSample read() throws IOException {
		String sampleString = (String) this.samples.read();

		if (sampleString != null) {
			String[] tokens = sampleString.split(",,,");
			DocumentSample sample = null;
			if (tokens.length > 1) {
				String docTokens = tokens[0];
				String category = tokens[1];

				sample = new DocumentSample(category, docTokens);
			} else {
				throw new IOException("Empty lines, or lines with only a category string are not allowed!");
			}
			return sample;
		}

		return null;
	}

}