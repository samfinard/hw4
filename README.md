# hw4: Implementing KNN to cluster text document


Sam Finard, Derek Gubbens


**How to Run**

Open runner.java and in the main method change test_document_path to the filepath of the document you want to classify, and change stopword_path to the filepath of stopwords.txt on you computer. Change k and the distance metric to whatever you want.

You will need to have the files for Stanford CoreNLP 4.5.4 (used on the previous homework) on your computer in the same directory as the project. Since we worked on this project over github, we are unable to upload the folder containing all the CoreNLP files to our project as some of the file sizes are too large. This is required in order for the preprocessing to work.

**Preprocessing and vectorization**

As with HW3, we used Stanford's CoreNLP Library to preprocess the data. We were able to achieve this by repurposing the previous preprocessing.java file from HW3, and then writing a function called preprocessData in the runner.java file (the one with the main method).
The preprocessData function takes 2 inputs, filePath (the filepath to the document being processed) and stopWordPath (the filepath to the list of stop words called stopwords.txt). The function initializes the stopwords in the preprocessing class using stopWordPath, and then passes the document filepath to the preprocessing class, which tokenizes and lemmatizes the raw text at that location and returns it as a list of the token strings. Then, the function joins the list of strings into a single string which is returned.

To vectorize documents, we used the TF-IDF matrix from hw3. This is an n x d matrix with n documents and d unique words after preprocessing. Each row in the TF-IDF matrix is the vectorization of a given document.
Note: we set IDF to log((n + 1) / document frequency) instead of the textbook definition log(n / document frequency) because the +1 ensures words that appear in all documents don't have a weight of 0  and also so we avoid division by zero when computing TF-IDF, as well as [other benefits.](https://stats.stackexchange.com/questions/166812/why-add-one-in-inverse-document-frequency)

**kNN classifier**

Our kNN classifier takes three inputs: k, the distance metric, and the target document. It uses the existing 10 documents as a database to classify new documents.
- k is the number of documents most similar to the target document the algorithm should consider.
- the distance metric is used to calculate this similarity. We have three possible inputs - euclidean, cosine, and NCD. More about NCD at the bottom
- the target document is a string that we want to classify as either C1, C4, or C7.

**Performance**

To evaluate the performance of our classifier, we used 10-fold cross-validation on all 27 combinations of k=1,...,9, and our three possible distance metrics. Our model performed its best at k=1 using NCD, although all distance metrics perform about the same with k=6 or higher. 

![Performance by k](https://github.com/samfinard/hw4/assets/104854051/51605974-08e7-4d33-b768-574eeaf6aa60)
Our baseline metric is 33% accuracy because that's the probability of randomly guessing 1 out of the 3 categories correctly.

NCD stands for normalized compressed distance, which I learned about in ["“Low-Resource” Text Classification: A Parameter-Free Classification Method with Compressors"](https://aclanthology.org/2023.findings-acl.426/) which claims to outperform BERT using GZIP and kNN (just like in this homework).

NCD(x,y) returns a distance measure between string x and y from [0,1] where similar strings return a low number.
- Let C(x) be the number of bytes of the GZIP compressed version of x
  $$NCD(x,y) = \dfrac{C(xy) - \min(C(x),C(y))}{\max(C(x),C(y)}$$
- where xy is the string concatenation x + y

This implementation is found in similarity.java
