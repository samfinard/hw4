# hw4: Implementing KNN to cluster text document

1. Preprocessing and vectorization

As with HW3, we used Stanford's CoreNLP Library to preprocess the data. We were able to achieve this by repurposing the previous preprocessing.java file from HW3, and then writing a function called preprocessData in the runner.java file (the one with the main method).
The preprocessData function takes 2 inputs, filePath (the filepath to the document being processed) and stopWordPath (the filepath to the list of stop words called stopwords.txt). The function initializes the stopwords in the preprocessing class using stopWordPath, and then passes the document filepath to the preprocessing class, which tokenizes and lemmatizes the raw text at that location and returns it as a list of the token strings. Then, the function joins the list of strings into a single string which is returned.

To vectorize documents, we used the TF-IDF matrix from hw3. This is an $n x d$ matrix with n documents and d unique words after preprocessing. Each document vector represents a row in   the TF-IDF matrix.



paper: 

Need to write about NCD

Write about 1 + IDF smoothing

Write about how to run it and where to go for this github


Testing testing.... i am different.
