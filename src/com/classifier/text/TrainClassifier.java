/**
 * 
 */
package com.classifier.text;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerEvaluator;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import com.classifier.text.CustomDocumentSampleStream;

/**
 * @author Sandeep
 *
 */
public class TrainClassifier {

	private static String NLP_MODEL_PATH = "D:/NLP_DATA/model-data/";
	public static DocumentCategorizerME _documentCategorizer = null;

	public void train() {

		DoccatModel doccatModel = null;
		InputStream dataInputStream = null;
		OutputStream nlpModelOutput = null;

		try {
			// Read data file

			dataInputStream = new FileInputStream(getClass().getResource("/resources/LabelledData.txt").getFile());
			// Read each training instance
			ObjectStream<String> lineStream = new PlainTextByLineStream(dataInputStream, "UTF-8");
			// ObjectStream<DocumentSample> sampleStream = new
			// DocumentSampleStream(lineStream);
			ObjectStream<DocumentSample> sampleStream = new CustomDocumentSampleStream(lineStream);
			// calculate the model
			doccatModel = DocumentCategorizerME.train("en", sampleStream);

		} catch (IOException e) {
			System.out.println("TextClassifierTrain.train():" + e.getMessage());
		} finally {
			if (dataInputStream != null) {
				try {
					dataInputStream.close();
				} catch (IOException e2) {
					System.out.println("TextClassifierTrain.train():" + e2.getMessage());
				}
			}
		}

		try {
			File outputFile = new File(NLP_MODEL_PATH);
			if (!outputFile.isDirectory()) {
				outputFile.mkdirs();
			}
			nlpModelOutput = new BufferedOutputStream(new FileOutputStream(outputFile + "/en-category.bin"));
			doccatModel.serialize(nlpModelOutput);

		} catch (IOException e) {
			System.out.println("TextClassifierTrain.train():" + e.getMessage());
		} finally {
			if (nlpModelOutput != null) {
				try {
					nlpModelOutput.close();
				} catch (Exception e2) {
					System.out.println("TextClassifierTrain.train():" + e2.getLocalizedMessage());
				}
			}
		}

	}

	public void test(String category, String text) {

		try {

			InputStream is = new FileInputStream(NLP_MODEL_PATH + "en-category.bin");
			DoccatModel classificationModel = new DoccatModel(is);
			DocumentCategorizerME classificationME = new DocumentCategorizerME(classificationModel);
			DocumentCategorizerEvaluator modelEvaluator = new DocumentCategorizerEvaluator(classificationME);
			String expectedDocumentCategory = category;
			String documentContent = text;
			DocumentSample sample = new DocumentSample(expectedDocumentCategory, documentContent);
			double[] classDistribution = classificationME.categorize(documentContent);
			String predictedCategory = classificationME.getBestCategory(classDistribution);
			modelEvaluator.evaluteSample(sample);
			double result = modelEvaluator.getAccuracy();
			System.out.println("Model prediction : " + predictedCategory);
			System.out.println("Accuracy : " + result);
		} catch (IOException e) {
			System.out.println("TextClassifierTrain.test():" + e.getMessage());
		}

	}

	public DocumentCategorizerME createCategorizer() {
		if (_documentCategorizer == null) {
			InputStream modelIn = null;
			try {
				modelIn = new FileInputStream(NLP_MODEL_PATH + "en-category.bin");
				final DoccatModel m = new DoccatModel(modelIn);

				_documentCategorizer = new DocumentCategorizerME(m);

			} catch (final IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if (modelIn != null) {
					try {
						modelIn.close();
					} catch (final IOException e) {
					}
				}
			}
		}
		return _documentCategorizer;
	}

	public String getQuestionCategory(String inputText) throws Exception {
		DocumentCategorizerME myCategorizer = createCategorizer();
		double[] outcomes = myCategorizer.categorize(inputText);

		String category = myCategorizer.getBestCategory(outcomes);
		System.out.println("--------------------------------------------------------");
		System.out.println("QuestionCategory is:" + category);

		return category;
	}

}
