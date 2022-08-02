package sampleCodes;

public class DriverTF_IDF {
	public static void main(String[] args) {
        String[] docs = {"knowledge building needs innovative environments are better at helping their inhabitants explore the adjacent possible",
                        "As a basis for evaluating explanations, creative knowledge building weight of evidence is a poor substitute for the first two criteria listed above.",
                        "A public idea database makes every passing idea visible to everyone else in the organization and do creative work.",
                        "questioning and various disturbances initiate cycles of innovation and creative organization knowledge.",
                        "We need some way to ensure knowledge to spread among environments that any notes that are dropped are dropped."};
        
        TF_IDF tfIdf = new TF_IDF(docs);
        for(int i = 0; i < tfIdf.docs.size(); i++) {
            System.out.print(i+1 + "\t");
            for (int j = 0; j < tfIdf.docs.size(); j++) {
                System.out.print(tfIdf.getSimilarity(i, j) + "\t");
            }
            System.out.println();
        }
    }

}
