# hw4: Implementing KNN to cluster text document

**Preprocessing and vectorization**

As with HW3, we used Stanford's CoreNLP Library to preprocess the data. We were able to achieve this by repurposing the previous preprocessing.java file from HW3, and then writing a function called preprocessData in the runner.java file (the one with the main method).
The preprocessData function takes 2 inputs, filePath (the filepath to the document being processed) and stopWordPath (the filepath to the list of stop words called stopwords.txt). The function initializes the stopwords in the preprocessing class using stopWordPath, and then passes the document filepath to the preprocessing class, which tokenizes and lemmatizes the raw text at that location and returns it as a list of the token strings. Then, the function joins the list of strings into a single string which is returned.

To vectorize documents, we used the TF-IDF matrix from hw3. This is an n x d matrix with n documents and d unique words after preprocessing. Each row in the TF-IDF matrix is the vectorization of a given document.
Note: we set IDF to log((n + 1) / document frequency) instead of the textbook definition log(n / document frequency) because the +1 ensures words that appear in all documents don't have a weight of 0  and also so we avoid division by zero when computing TF-IDF, as well as [other benefits.](https://stats.stackexchange.com/questions/166812/why-add-one-in-inverse-document-frequency)

**k-NN classifier**
Our k-NN classifier takes three inputs: k, the distance metric, and the target document. It uses the existing 10 documents as a database to classify new documents.
- k is the number of documents most similar to the target document the algorithm should consider.
- the distance metric is used to calculate this similarity. We have three possible inputs - euclidean, cosine, and NCD. More about NCD at the bottom
- the target document is a string that we want to classify as either C1, C4, or C7.

**Performance**





paper: 

Need to write about NCD

Write about how to run it and where to go for this github

**How to Run**
