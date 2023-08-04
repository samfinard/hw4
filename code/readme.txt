Project completed by Sam F. and Derek G.

before running: open tester.java and change the file paths for C1,C4,C7 to the file path of hw3 on your computer.

We split the project into 3 parts. 
1. Preprocessing 
2. TFIDF matrix, clustering, confusion matrix, F1 score - run tester.java to see top concepts per cluster, confusion matrix, and F1 score
3. Visualization - run Visualization.ipynb to see all 24 documents (represented by their TF-IDF vectors) on a 2-d graph.

You can play around with values in tester.java such as number of iterations, number of clusters, and whether the algorithm uses euclidean distance or cosine similarity.

Note: Sam couldn't get Stanford NLP to work on his computer so tester.java takes in already processed documents as input.

We processed the documents using preprocessing.java on Derek's computer and include preprocessing.java even though no classes use it.