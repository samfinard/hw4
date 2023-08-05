Input: 
    - raw document (specifically file name/path, we will run for all files in ../data)
    - document TF-IDF matrix with previously determined labels
        - C1: Airline Safety, C4: Hoof and Mouth Disease, C7: Mortgage Rates
Need to make
    - one class/method that preprocesses a raw document into its corresponding TF-IDF vector representation


**Implement k-NN algorithm**
Input: raw document, similarity measure (euclidean or cosine (or new NCD?))
Output: k most similar ("nearest") documents in the TF-IDF matrix, as well as their labels/topics

**Measure Model Performance using Cross Validation**
- get accuracy
- experiment with different values for k, distance metric, and logAddOne
- reoprt findings

**Bonus(Fuzzy k-NN)**