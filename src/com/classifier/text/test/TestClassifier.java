package com.classifier.text.test;

import com.classifier.text.TrainClassifier;

public class TestClassifier {

	public static void main(String[] args) {

		TrainClassifier trainClassifier = new TrainClassifier();
		//trainClassifier.train();
		// Input text to find the question Category
		String inputText = "what is the nature of learning ?";
		try {
			trainClassifier.getQuestionCategory(inputText);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
