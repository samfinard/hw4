from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import PorterStemmer
from collections import Counter
from num2words import num2words

import nltk
import os
import string
import numpy as np
import copy
import pandas as pd
import pickle
import re
import math
from tqdm import tqdm

nltk.download('stopwords')

def convert_lower_case(data):
    return np.char.lower(data)

def remove_stop_words(data):
    stop_words = stopwords.words('english')
    words = word_tokenize(str(data))
    new_text = ""
    for w in words:
        if w not in stop_words and len(w) > 1:
            new_text = new_text + " " + w
    return new_text

def remove_punctuation(data):
    symbols = "!\"#$%&()*+-./:;<=>?@[\]^_`{|}~\n"
    for i in range(len(symbols)):
        data = np.char.replace(data, symbols[i], ' ')
        data = np.char.replace(data, "  ", " ")
    data = np.char.replace(data, ',', '')
    return data

def remove_apostrophe(data):
    return np.char.replace(data, "'", "")

def stemming(data):
    stemmer= PorterStemmer()
    
    tokens = word_tokenize(str(data))
    new_text = ""
    for w in tokens:
        new_text = new_text + " " + stemmer.stem(w)
    return new_text

def convert_numbers(data):
    tokens = word_tokenize(str(data))
    new_text = ""
    for w in tokens:
        try:
            w = num2words(int(w))
        except:
            a = 0
        new_text = new_text + " " + w
    new_text = np.char.replace(new_text, "-", " ")
    return new_text

def preprocess(data):
    data = convert_lower_case(data)
    data = remove_punctuation(data) #remove comma seperately
    data = remove_apostrophe(data)
    data = remove_stop_words(data)
    data = convert_numbers(data)
    data = stemming(data)
    data = remove_punctuation(data)
    data = convert_numbers(data)
    data = stemming(data) #needed again as we need to stem the words
    data = remove_punctuation(data) #needed again as num2word is giving few hypens and commas fourty-one
    data = remove_stop_words(data) #needed again as num2word is giving stop words 101 - one hundred and one
    return data

def apply_preprocessing(input_file_path, output_directory):
    # Check if output_directory exists, if not, create it
    if not os.path.exists(output_directory):
        os.makedirs(output_directory)
    
    # Get all .txt files in the directory
    txt_files = [f for f in os.listdir(input_file_path) if f.endswith('.txt')]

    # Apply preprocessing to each file
    for file_name in tqdm(txt_files, desc="Processing files"):
        with open(os.path.join(input_file_path, file_name), 'r', encoding="ISO-8859-1") as file:
            text = file.read()
        
        preprocessed_text = preprocess(text)  # Assuming preprocess function exists
        
        # Create a new file in the output directory with the preprocessed text
        new_file_name = f'{file_name}_pp.txt' 
        with open(os.path.join(output_directory, new_file_name), 'w', encoding='ISO-8859-1') as file:
            file.write(preprocessed_text)


def main():
    input_file_path = "../data/input"
    output_file_path = "../data/processed"
    apply_preprocessing(input_file_path, output_file_path)

if __name__ == "__main__":
    main()