//import edu.stanford.nlp.simple.*;
//import edu.stanford.nlp.simple.Document;

//import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.util.CoreMap;


// input: 3 folders with 8 text documents
// output: 
// filter and remove stop words
// tokenization, stemming, lemmization
// named-entity extraction (NER)
// sliding window to merge remaining phrases

public class preprocessing{
    //static List<String> stopWords = List.of("the", "of", "and", "but", "be", "by", "are", "on", "from", "in", ".", ",", "'", "-", ":", "--");

    static List<String> stopWords;

    public static void stopWordDoc(String filePath) throws IOException {
        stopWords = Files.readAllLines(Paths.get(filePath));
    }

    public static List<String> reader(String dataPath) throws IOException {
        List<String> doc_contents = new ArrayList<String>();
        
        Files.walk(Path.of(dataPath)).filter(Files::isRegularFile)
        .forEach(doc -> {
            try {
                String text = new String(Files.readAllBytes(doc));


                List<String> tokens = new ArrayList<String>();
                Properties nlpProps = new Properties();
                nlpProps.setProperty("annotators", "tokenize, ssplit, pos, lemma");
                StanfordCoreNLP pipeline = new StanfordCoreNLP(nlpProps);
                Annotation document = new Annotation(text);
                pipeline.annotate(document);
                List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
                for (CoreMap sentence: sentences) {
                    for (CoreLabel token: sentence.get((CoreAnnotations.TokensAnnotation.class))){
                        String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                        String stem = token.get(CoreAnnotations.StemAnnotation.class);

                        lemma = lemma.replaceAll("\\p{Punct}", "");
                        //String POS = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                        if (!stopWords.contains(lemma.toLowerCase())) {
                            //if (NER != null){
                                //System.out.println("SJKDHFKSDLHFGSLDKHGFKSDJHFGHDSLKJF");
                            //    tokens.add(NER);
                            //}
                            if (stem != null){
                                tokens.add(stem);
                            }
                            else {
                                tokens.add(lemma.toLowerCase());
                            }
                        }
                    }
                }

                String processedText = String.join(" ", tokens);

                doc_contents.add(processedText);

            } catch (IOException e) {
                e.printStackTrace();
            }     
        });

        //System.out.println("END OF DOCS");
        
        
        for (int i=0; i<doc_contents.size(); i++){
                    String docString = doc_contents.get(i);
                    //System.out.println("CURRENT DOC STRING: ");
                    //System.out.println(docString);
                    Properties nlpProps = new Properties();
                    nlpProps.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner");
                    Annotation doctate = new Annotation(docString);
                    StanfordCoreNLP pipeline = new StanfordCoreNLP(nlpProps);
                    pipeline.annotate(doctate);
                    String docStringFinal = "";

                    List<CoreMap> sents = doctate.get(CoreAnnotations.SentencesAnnotation.class);
                    for (CoreMap sent : sents){

                        List<CoreMap> entityMentions = ((CoreMap) sent).get(CoreAnnotations.MentionsAnnotation.class);
                        for (CoreMap entityMention : entityMentions){
                            String[] tempTokens = entityMention.toString().split(" ");
                            String joined = String.join("_", tempTokens);
                            docStringFinal = docStringFinal + joined + " ";
                        }

                        List<CoreLabel> sentTokens = sent.get(CoreAnnotations.TokensAnnotation.class);
                        for (int n=0; n<sentTokens.size(); n++){
                            CoreLabel sentToken = sentTokens.get(n);
                            Integer nameEntity = sentToken.get(CoreAnnotations.EntityMentionIndexAnnotation.class);
                            if (nameEntity == null) {
                                docStringFinal = docStringFinal + sentToken.word() + " ";;
                            }
                        }
                    }
                    doc_contents.set(i, docStringFinal);
                }

        return doc_contents;
    }

    // public static void main(String[] args) throws IOException {
    //     stopWordDoc("/Users/derekgubbens/Documents/hw3/stopwords.txt");
    //     List<String> fileStringsC1 = reader("/Users/derekgubbens/Documents/hw3/data/C1");
    //     //System.out.println(fileStrings);
    //     System.out.println("--------------------------------------------------------------------------");
    //     System.out.println("FOLDER 1:");
    //     System.out.println("--------------------------------------------------------------------------");

    //     for (int i=0; i<fileStringsC1.size(); i++){
    //         System.out.println(fileStringsC1.get(i) + "\n");
    //     }

    //     List<String> fileStringsC4 = reader("/Users/derekgubbens/Documents/hw3/data/C4");

    //     System.out.println("--------------------------------------------------------------------------");
    //     System.out.println("FOLDER 2:");
    //     System.out.println("--------------------------------------------------------------------------");

    //     for (int i=0; i<fileStringsC4.size(); i++){
    //         System.out.println(fileStringsC4.get(i) + "\n");
    //     }

    //     List<String> fileStringsC7 = reader("/Users/derekgubbens/Documents/hw3/data/C7");

    //     System.out.println("--------------------------------------------------------------------------");
    //     System.out.println("FOLDER 3:");
    //     System.out.println("--------------------------------------------------------------------------");

    //     for (int i=0; i<fileStringsC7.size(); i++){
    //         System.out.println(fileStringsC7.get(i) + "\n");
    //     }
    // }
    

}