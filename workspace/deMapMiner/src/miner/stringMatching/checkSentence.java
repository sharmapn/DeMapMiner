package miner.stringMatching;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import miner.models.Message;
import miner.models.Pep;
import utilities.Utilities;

public class checkSentence {
	
	//for using stringafter functions
	static Utilities u = new Utilities();
	
	public static String checkAllSentencesForBaseIdeasInMessage2(Pep[] p, String sentence, String wordsFoundList, String author,
			String statusFrom, String statusTo, Integer v_pep, Date v_date, Integer v_pepnumber, Integer v_counter, String[] baseIdeas) {
		
		//wordsFoundList = null;
		
		for (String bi : baseIdeas) {
			String words[] = bi.split(" ");
			String idea = 	words[0];
			String entity = words[1];
			String action = words[2];
			String pep = 	words[3];
			if (sentence.toLowerCase().contains(entity) && sentence.toLowerCase().contains(action) && sentence.toLowerCase().contains(pep)) {			
				wordsFoundList = idea + ", " + sentence;        //one string that would be appended
				//System.out.println(v_pepnumber + " idea=" + idea + " entity=" + entity + " action=" + action + " pep=" + pep + " wordsFoundList in Fn " + wordsFoundList);			
			}
			//System.out.println("\n---End of For Loop ");
		}
		
		System.out.println("\n---End of Fn ");
		return wordsFoundList;
	}

	public static String checkEachSentenceNew(Pep[] p, String sentence, String wordsFoundList, String author,
			String statusFrom, String statusTo, Integer v_pep, Date v_date, Integer v_pepnumber, Integer v_counter) {

		// System.out.println("wordsFoundList " + wordsFoundList);
		
		//store states and sentences in which it was found
//		wordsFoundList = "TEST";
//		System.out.println("wordsFoundList b4 Fn " + wordsFoundList);
		// System.out.println(" i accept pep new");
		// Jan 30th 2016 ..not sure if this is right place to add this code, but
		// will do temporarily
		// maybe belongs in check each sentence
		// actually this belongs in checkeachsentence() as these 3 words below
		// can belong at different sentences in the message which woul not be
		// right.
		if (sentence.toLowerCase().contains("pep") && sentence.toLowerCase().contains("accept")) {
			// get author - i.e who says I accept
//VV			System.out.println("\nSentence " + sentence);
			wordsFoundList = "pep accepted by author, " + sentence;        //one string that would be appended
			//System.out.println("\n --------------------wordsFoundList in Fn " + wordsFoundList);
			//p[v_pepnumber].messages[v_counter].setMessageGist("pep accepted by author");	// not sure what this is used and where at this moment
			//p[v_pepnumber].messages[v_counter].addIdeaSentence("pep accepted by author",sentence);
			//System.out.println("\n --------------------wordsFoundList in Fn " + wordsFoundList);
			
		}

		// pep accepted - no reasons in these messages
		if ((sentence.toLowerCase().contains("bdfl") || sentence.toLowerCase().contains("guido"))
				&& sentence.toLowerCase().contains("pep ") && sentence.toLowerCase().contains(" has been ")
				&& sentence.toLowerCase().contains(" accepted ")) {
			wordsFoundList = "bdfl-accepts-pep, " + sentence + ", ";
			//p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-accepts-pep");
			//p[v_pepnumber].messages[v_counter].addIdeaSentence("bdfl-accepts-pep",sentence);
			//System.out.println("\n----------------------------------------wordsFoundList in Fn " + wordsFoundList);
		}

		if (sentence.toLowerCase().contains("accept")) {
			String previousSentence = p[v_pepnumber].messages[v_counter]
					.getPreviousSentence(p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter() - 2);
			if (previousSentence.contains("wrangling")) {
				// System.out.println("
				// _wrangling_________________________________________________________
				// ");
				// System.out.println(" _message id___" +
				// p[v_pepnumber].messages[v_counter].getMessageId() + "_current
				// sentence counter___" +
				// p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter());
				// System.out.println(" _current sentence counter___" +
				// p[v_pepnumber].messages[v_counter].getPreviousSentence(p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter()
				// -2));
				wordsFoundList = " bdfl-decision-accepting-when-wrangling, " + sentence + ", ";
				//p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-decision-accepting-when-wrangling");
				//p[v_pepnumber].messages[v_counter].addIdeaSentence("bdfl-decision-accepting-when-wranglingr",sentence);
			}
			//System.out.println("\n----------------------------------------wordsFoundList in Fn " + wordsFoundList);
		}
		// BDFL asking if there is consensus
		// if (author.contains("Guido") && sentence.toLowerCase().contains("Is")
		// && sentence.toLowerCase().contains("pep") &&
		// sentence.toLowerCase().contains("stage") &&
		// sentence.toLowerCase().contains("consensus") &&
		// sentence.toLowerCase().contains("?") ) {
		// wordsFoundList += " BDFL asking if there is consensus,";
		// }
		// BDFL decision
		if (sentence.toLowerCase().contains("bdfl") && !(sentence.toLowerCase().contains("no")
				|| sentence.toLowerCase().contains("nor") || sentence.toLowerCase().contains("none"))) {
			if (sentence.toLowerCase().contains("decision")) {
				wordsFoundList = " bdfl-decision, " + sentence + ", ";
				//p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-decision");
				//p[v_pepnumber].messages[v_counter].addIdeaSentence("bdfl-decision",sentence);
			}
			if (sentence.toLowerCase().contains("pronouncement")) {
				wordsFoundList = " a bdfl-pronouncement, " + sentence + ", ";
				//p[v_pepnumber].messages[v_counter].setMessageGist("a-bdfl-pronouncement");
				//p[v_pepnumber].messages[v_counter].addIdeaSentence("a-bdfl-pronouncement",sentence);
			}
			if (sentence.toLowerCase().contains("reject")) {
				if (sentence.toLowerCase().contains("pronouncement")) {
					// /rejected by BDFL pronouncement.
					wordsFoundList = " rejected-by-bdfl-pronouncement, " + sentence + ", ";
					//p[v_pepnumber].messages[v_counter].setMessageGist("rejected-by-bdfl-pronouncement");
					//p[v_pepnumber].messages[v_counter].addIdeaSentence("rejected-by-bdfl-pronouncement",sentence);
				} else {
					wordsFoundList = " bdfl-rejection " + sentence + ", ";
					//p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-rejection");
					//p[v_pepnumber].messages[v_counter].addIdeaSentence("bdfl-rejection",sentence);
				}
			}
			if (sentence.toLowerCase().contains("accepted") || sentence.toLowerCase().contains("accept")) {
				String previousSentence = p[v_pepnumber].messages[v_counter]
						.getPreviousSentence(p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter() - 2);
				// XXX System.out.println("----previousSentence" +
				// previousSentence);
				if (sentence.toLowerCase().contains("bdfl pronouncement")
						|| previousSentence.toLowerCase().contains("bdfl pronouncement")) {
					wordsFoundList = " bdfl-accepted-as-per-BDFL-pronouncement, " + sentence + ", ";
					//p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-accepted-as-per-BDFL-pronouncement");
					//p[v_pepnumber].messages[v_counter].addIdeaSentence("bdfl-accepted-as-per-BDFL-pronouncement",sentence);
				} else {
					wordsFoundList = " bdfl-accepted, " + sentence + ", ";
					//p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-accepted");
					//p[v_pepnumber].messages[v_counter].addIdeaSentence("bdfl-accepted",sentence);
				}
			}
			if (sentence.toLowerCase().contains("No argument")
					&& sentence.toLowerCase().contains("totally convincing")) {
				wordsFoundList = " bdfl-finds-not-convincing, " + sentence + ", ";
				//p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-finds-not-convincing");
				//p[v_pepnumber].messages[v_counter].addIdeaSentence("bdfl-finds-not-convincing",sentence);
			}
			//System.out.println("\n----------------------------------------wordsFoundList in Fn " + wordsFoundList);
		}
		// PEP 214 and 221 are accepted, as per recent BDFL pronouncements

		if (sentence.toLowerCase().contains("i ")) {
			if (sentence.toLowerCase().contains(" need ")
					&& (sentence.toLowerCase().contains(" input ") || sentence.toLowerCase().contains(" feedback "))) {
				// System.out.println("-----------------------------------------------
				// sentencerrr" );
				// System.out.println("----gh");
				wordsFoundList = "author-requests-feedback, " + sentence + ", ";
				//p[v_pepnumber].messages[v_counter].setMessageGist("author-requests-feedback");
				//p[v_pepnumber].messages[v_counter].addIdeaSentence("author-requests-feedback",sentence);
			}
			if (sentence.toLowerCase().contains(" checked in ") && sentence.toLowerCase().contains(" changes ")) {
				// System.out.println("-----------------------------------------------
				// sentencerrr" );
				// System.out.println("----gh");
				String previousSentence = p[v_pepnumber].messages[v_counter]
						.getPreviousSentence(p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter() - 2);
				// System.out.println("----previousSentence" +
				// previousSentence);
				if (previousSentence.toLowerCase().contains("thank")) {
					wordsFoundList = "author-makes-pep-changes-after-comments, " + sentence + ", ";
					//p[v_pepnumber].messages[v_counter].setMessageGist("author-makes-pep-changes-after-comments");
					//p[v_pepnumber].messages[v_counter].addIdeaSentence("author-makes-pep-changes-after-comments",sentence);
				}
			}
			//System.out.println("\n----------------------------------------wordsFoundList in Fn " + wordsFoundList);
		}
		// BDFL asking if there is consensus
		if (sentence.toLowerCase().contains("is") && sentence.toLowerCase().contains("pep")
				&& sentence.toLowerCase().contains("stage") && sentence.toLowerCase().contains("consensus")
				&& sentence.toLowerCase().contains("?")) {
			// wordsFoundList += "BDFL-asking-if-pep-in-consensus";
			wordsFoundList = "author-makes-pep-changes-after-comments, " + sentence + ", ";
			//p[v_pepnumber].messages[v_counter].setMessageGist("BDFL-asking-if-pep-in-consensus");
			//p[v_pepnumber].messages[v_counter].addIdeaSentence("BDFL-asking-if-pep-in-consensus",sentence);
			//System.out.println("\n----------------------------------------wordsFoundList in Fn " + wordsFoundList);
		}
		// asking to vote
		// vote
		if ((sentence.toLowerCase().contains("please"))) {
			// participate indicate
			if ((sentence.toLowerCase().contains("vote") || sentence.toLowerCase().contains("voting")
					|| sentence.toLowerCase().contains("poll") || sentence.toLowerCase().contains("consensus")
					|| sentence.toLowerCase().contains("complementary vote")
					|| sentence.toLowerCase().contains("ballot"))) {
				if (u.stringAfter(sentence, "please", "vote")) {
					wordsFoundList += "voting-called, " + sentence + ", ";
					//p[v_pepnumber].messages[v_counter].setMessageGist("voting-called");
					//p[v_pepnumber].messages[v_counter].addIdeaSentence("voting-called",sentence);
					// System.out.println("-----------------------------------------------
					// sentence...after ..." + sentence);
				}
			}
			//System.out.println("\n----------------------------------------wordsFoundList in Fn " + wordsFoundList);
		}
		if (sentence.toLowerCase().contains("I") && sentence.toLowerCase().contains(" open ")
				&& sentence.toLowerCase().contains(" this ") && sentence.toLowerCase().contains(" for ")
				&& sentence.toLowerCase().contains(" vote ")) {
			wordsFoundList = " voting-called, " + sentence + ", ";
			//p[v_pepnumber].messages[v_counter].setMessageGist("voting-called");
			//p[v_pepnumber].messages[v_counter].addIdeaSentence("voting-called",sentence);
			//System.out.println("\n----------------------------------------wordsFoundList in Fn " + wordsFoundList);
		}
		if (sentence.toLowerCase().contains("I") && sentence.toLowerCase().contains(" open ")
				&& sentence.toLowerCase().contains(" this ") && sentence.toLowerCase().contains(" for ")
				&& sentence.toLowerCase().contains(" vote ")) {
			wordsFoundList = " voting-called, " + sentence + ", ";
			//p[v_pepnumber].messages[v_counter].setMessageGist("voting-called");
			//p[v_pepnumber].messages[v_counter].addIdeaSentence("voting-called",sentence);
			//System.out.println("\n----------------------------------------wordsFoundList in Fn " + wordsFoundList);
		}
		// puts forward a proposal
		if (sentence.toLowerCase().contains(" put ") && sentence.toLowerCase().contains(" proposal ")) {
			wordsFoundList = " proposal, " + sentence + ", ";
			//p[v_pepnumber].messages[v_counter].setMessageGist("proposal");
			//p[v_pepnumber].messages[v_counter].addIdeaSentence("proposal",sentence);
			//System.out.println("\n----------------------------------------wordsFoundList in Fn " + wordsFoundList);
		}
		// I plan to run the vote
		if (sentence.toLowerCase().contains("i ") && sentence.toLowerCase().contains(" plan ")
				&& sentence.toLowerCase().contains(" run ") && sentence.toLowerCase().contains(" vote ")) {
			wordsFoundList = " voting-called, " + sentence + ", ";
			//p[v_pepnumber].messages[v_counter].setMessageGist("voting-called");
			//p[v_pepnumber].messages[v_counter].addIdeaSentence("voting-called",sentence);
			//System.out.println("\n----------------------------------------wordsFoundList in Fn " + wordsFoundList);
		}

		// if previous or next sentence has text similar to
		// thanks comments
		// Thanks for your helpful comments. I have checked in some changes to
		// the PEP
		// (r76533) which take into account the comments you made.
//xxx		System.out.println("wordsFoundList Fn End ------------"+wordsFoundList);
		return wordsFoundList;
	}

	public static String checkEachSentence(Pep[] p, String sentence, String wordsFoundList, String author,
			String statusFrom, String statusTo, Integer v_pep, Date v_date, Integer v_pepnumber, Integer v_counter) {

		// proposal
		// got fairly positive responses
		// (more positive than I'd expected, in fact) but I'm bracing myself for
		// fierce discussion here on python-dev. It's important to me that if if
		// this is accepted it is a "rough consensus" decision (working code we
		// already have plenty of :-), not something enforced by a vocal
		// minority
		// or an influential individual such as myself. If there's too much
		// opposition I'll withdraw the PEP so as not to waste everybody's time
		// with a fruitless discussion.

		// took place. Everyone on the SIG was positive with the idea so I wrote
		// a
		// PEP, got positive feedback from the SIG again, and so now I present
		// to you
		// PEP 488 for discussion.

		/*
		 * Given that if-then-else expressions keep being requested, I hereby
		 * put forward a proposal. I am neutral on acceptance of the proposal;
		 * I'd like the c.l.py community to accept or reject it or come up with
		 * a counter-proposal. Please understand that I don't have a lot of time
		 * to moderate the discussion; I'll just wait for the community to
		 * discuss the proposal and agree on some way to count votes, then count
		 * them.
		 */

		System.out.println(" i accept pep orig");

		// System.out.println(" _message id___" +
		// p[v_pepnumber].messages[v_counter].getMessageId() + "_current
		// sentence counter___" +
		// p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter());

		if (sentence.toLowerCase().contains("accept")) {
			String previousSentence = p[v_pepnumber].messages[v_counter]
					.getPreviousSentence(p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter() - 2);
			if (previousSentence.contains("wrangling")) {
				// System.out.println("
				// _wrangling_________________________________________________________
				// ");
				// System.out.println(" _message id___" +
				// p[v_pepnumber].messages[v_counter].getMessageId() + "_current
				// sentence counter___" +
				// p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter());
				// System.out.println(" _current sentence counter___" +
				// p[v_pepnumber].messages[v_counter].getPreviousSentence(p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter()
				// -2));
				wordsFoundList += " bdfl-decision-accepting-when-wrangling &";
				p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-decision-accepting-when-wrangling");
			}
		}

		if (sentence.toLowerCase().contains("discuss")) {
			if (sentence.toLowerCase().contains("blog") || sentence.toLowerCase().contains("survey")
					|| sentence.toLowerCase().contains("offline") || sentence.toLowerCase().contains("twitter")
					|| sentence.toLowerCase().contains("plus.google.com")) {
				wordsFoundList += " offline-discussion &";
				p[v_pepnumber].messages[v_counter].setMessageGist("offline-discussion");
			}
		}

		// proposal after discusions
		if (sentence.toLowerCase().contains("positive") && sentence.toLowerCase().contains("discussion")) {
			if (sentence.toLowerCase().contains("responses") || sentence.toLowerCase().contains("feedback"))
				if ((sentence.toLowerCase().contains("propose")) || (sentence.toLowerCase().contains("present"))) {
					wordsFoundList += " new pep proposal";
					p[v_pepnumber].messages[v_counter].setMessageGist("author-likes-pep");
				} // checkNull(wordsFoundList, "new pep proposal");
		}

		// System.out.println(" heer 1.6" );

		// proposal
		if (sentence.toLowerCase().contains("new pep") && sentence.toLowerCase().contains("discussions")
				&& (sentence.toLowerCase().contains("proposes") || sentence.toLowerCase().contains("proposal"))) {
			wordsFoundList += " new pep proposal from another list,";
		}
		// new proposal
		if (sentence.toLowerCase().contains("i ")) {
			if (sentence.toLowerCase().contains(" like ") && sentence.toLowerCase().contains(" pep ")) {
				// if
				// (p[v_pepnumber].messages[v_counter].getMessageIdOfMessage()
				// == 644)
				// {
				// VVV
				// System.out.println("-----------------------------------------------
				// sentence" + sentence);
				// }
				// VVV wordsFoundList += " author-likes-pep &";
				// VVV
				// p[v_pepnumber].messages[v_counter].setMessageGist("author-likes-pep");
			}
			if (sentence.toLowerCase().contains(" don't ") && sentence.toLowerCase().contains(" like ")
					&& sentence.toLowerCase().contains(" pep ")) {
				wordsFoundList += " author-dislikes-pep &";
				p[v_pepnumber].messages[v_counter].setMessageGist("author-dislikes-pep");
			}
			if (sentence.toLowerCase().contains(" suggest ") && sentence.toLowerCase().contains(" changes ")) {
				wordsFoundList += " author-suggests-changes &";
				p[v_pepnumber].messages[v_counter].setMessageGist("author-suggests-changes");
			}
			if (sentence.toLowerCase().contains(" put ") && sentence.toLowerCase().contains(" proposal ")) {
				wordsFoundList += " author-proposes &";
				p[v_pepnumber].messages[v_counter].setMessageGist("author-proposes");
			}
			if (sentence.toLowerCase().contains("community") && sentence.toLowerCase().contains("discuss")
					&& sentence.toLowerCase().contains("proposal")) {
				wordsFoundList += " author-proposes-for-discussion &";
				p[v_pepnumber].messages[v_counter].setMessageGist("author-proposes-for-discussion");
			}
			if (sentence.toLowerCase().contains("would like")
					&& sentence.toLowerCase().contains("pronouncement on pep")) // author
																				// of
																				// pep
																				// asking
																				// for
																				// a
																				// pronouncement
			{
				wordsFoundList += " author-requests-pronouncement &";
				p[v_pepnumber].messages[v_counter].setMessageGist("author-requests-pronouncement");
			}
			if (sentence.toLowerCase().contains(" need  ") && sentence.toLowerCase().contains(" input ")) {
				System.out.println("----------------------------------------------- sentence" + sentence);
				wordsFoundList += " a author-requests-feedback &";
				p[v_pepnumber].messages[v_counter].setMessageGist("a author-requests-feedback");
			}
			if (sentence.toLowerCase().contains(" would ") && sentence.toLowerCase().contains(" like ")
					&& sentence.toLowerCase().contains(" to get ") && sentence.toLowerCase().contains(" votes ")) {
				wordsFoundList += " author-requests-votes &";
				p[v_pepnumber].messages[v_counter].setMessageGist("author-requests-votes");
			}
			if (sentence.toLowerCase().contains(" feedback ") || sentence.toLowerCase().contains(" input ")) {
				System.out.println("----------------------------------------------- sentence" + sentence);
				wordsFoundList += " b author-requests-feedback &";
				p[v_pepnumber].messages[v_counter].setMessageGist("b author-requests-feedback ");
			}
			if (sentence.toLowerCase().contains("ongoing discusssion")) {
				wordsFoundList += " ongoing-discusssion &";
				p[v_pepnumber].messages[v_counter].setMessageGist("ongoing-discusssion");
			}
		}

		// deferring it.
		if ((sentence.toLowerCase().contains("deferring"))) {
			// participate indicate
			if ((sentence.toLowerCase().contains("deferring")) || (sentence.toLowerCase().contains("it"))) {
				wordsFoundList += " deferring it &";
				p[v_pepnumber].messages[v_counter].setMessageGist("deferring it");
			}
		}

		// vote
		if ((sentence.toLowerCase().contains("please"))) {
			// participate indicate
			if ((sentence.toLowerCase().contains("vote") || sentence.toLowerCase().contains("voting")
					|| sentence.toLowerCase().contains("poll") || sentence.toLowerCase().contains("consensus")
					|| sentence.toLowerCase().contains("complementary vote")
					|| sentence.toLowerCase().contains("ballot"))) {
				if (u.stringAfter(sentence, "please", "vote")) {
					wordsFoundList += "s asking to vote &";
					p[v_pepnumber].messages[v_counter].setMessageGist("asking to vote ");
					System.out
							.println("----------------------------------------------- sentence...after ..." + sentence);
				}
			}
		}

		if (sentence.toLowerCase().contains("lets ") && sentence.toLowerCase().contains(" all ")
				&& sentence.toLowerCase().contains(" vote")) {
			wordsFoundList += " requests-votes &";
			p[v_pepnumber].messages[v_counter].setMessageGist("requests-votes ");
		}
		if (sentence.toLowerCase().contains("comments ") && sentence.toLowerCase().contains(" are ")
				&& sentence.toLowerCase().contains(" invited ")) {
			wordsFoundList += " requests-feedback &";
			p[v_pepnumber].messages[v_counter].setMessageGist("requests-feedback");
		}
		if (sentence.toLowerCase().contains("PEP ") && sentence.toLowerCase().contains(" submitted ")
				&& sentence.toLowerCase().contains(" for pronouncement ")) {
			wordsFoundList += " submitted-for-pronouncement &";
			p[v_pepnumber].messages[v_counter].setMessageGist("submitted-for-pronouncement");
		}
		if (sentence.toLowerCase().contains("I") && sentence.toLowerCase().contains(" open ")
				&& sentence.toLowerCase().contains(" this ") && sentence.toLowerCase().contains(" for ")
				&& sentence.toLowerCase().contains(" vote ")) {
			wordsFoundList += " asks-for-vote &";
			p[v_pepnumber].messages[v_counter].setMessageGist("asks-for-vote");
		}
		if (sentence.toLowerCase().contains("I") && sentence.toLowerCase().contains(" open ")
				&& sentence.toLowerCase().contains(" this ") && sentence.toLowerCase().contains(" for ")
				&& sentence.toLowerCase().contains(" vote ")) {
			wordsFoundList += " asks-for-vote &";
			p[v_pepnumber].messages[v_counter].setMessageGist("asks-for-votep");
		}
		if (sentence.toLowerCase().contains("votes") && sentence.toLowerCase().contains(" received ")) {
			wordsFoundList += " votes-received &";
			p[v_pepnumber].messages[v_counter].setMessageGist("votes-received");
		}
		if (sentence.toLowerCase().contains("air ") && sentence.toLowerCase().contains(" their ")
				&& sentence.toLowerCase().contains(" views ")) {
			wordsFoundList += " asking-for-views &";
			p[v_pepnumber].messages[v_counter].setMessageGist("asking-for-views");
		}
		if (sentence.toLowerCase().contains("I ") && sentence.toLowerCase().contains(" have ")
				&& sentence.toLowerCase().contains(" addressed ") && sentence.toLowerCase().contains(" comments ")
				&& sentence.toLowerCase().contains(" discussion ")) {
			wordsFoundList += " author-addressed-concerns &";
			p[v_pepnumber].messages[v_counter].setMessageGist("author-addressed-concerns");
		}
		if (sentence.toLowerCase().contains("pep ") && sentence.toLowerCase().contains(" in ")
				&& sentence.toLowerCase().contains(" consensus ")) {
			wordsFoundList += " BDFL-asking-if-pep-in-consensus &";
			p[v_pepnumber].messages[v_counter].setMessageGist("BDFL-asking-if-pep-in-consensus");
		}
		if (sentence.toLowerCase().contains("pep ") && sentence.toLowerCase().contains(" has been ")
				&& sentence.toLowerCase().contains(" accepted ")) {
			wordsFoundList += " pep has been accepted &";
			p[v_pepnumber].messages[v_counter].setMessageGist("pep has been accepted");
		}
		if (sentence.toLowerCase().contains("expediate ") && sentence.toLowerCase().contains(" a ")
				&& sentence.toLowerCase().contains(" pronouncement ")) {
			wordsFoundList += " asking-for-pep-pronouncement &";
			p[v_pepnumber].messages[v_counter].setMessageGist("asking-for-pep-pronouncement");
		}
		if (sentence.toLowerCase().contains("pep ") && sentence.toLowerCase().contains(" ready ")
				&& sentence.toLowerCase().contains(" for review ")) {
			wordsFoundList += " pep-ready-for-review &";
			p[v_pepnumber].messages[v_counter].setMessageGist("pep-ready-for-review");
		}
		// if ( sentence.toLowerCase().contains("gets") &&
		// sentence.toLowerCase().contains(" my ") &&
		// sentence.toLowerCase().contains(" vote ") )
		// wordsFoundList += " gets my vote,";
		// this PEP has been submitted to GvR for pronouncement.
		// /I'm ready to open this one for a vote.
		// &&
		// ||

		// Voter Information
		// votes I've received

		// asking for feedback
		if (sentence.toLowerCase().contains("your")) {
			if (sentence.toLowerCase().contains("feedback")) {
				System.out.println("----------------------------------------------- sentence" + sentence);
				wordsFoundList += " asking-for-feedback &";
				p[v_pepnumber].messages[v_counter].setMessageGist("asking-for-feedback");
			}
			if (sentence.toLowerCase().contains("comments")) // asking for
																// comments
			{
				System.out.println("----------------------------------------------- sentence" + sentence);
				wordsFoundList += " asking-for-comments &";
				p[v_pepnumber].messages[v_counter].setMessageGist("asking-for-comments");
			}
			if (sentence.toLowerCase().contains("ideas")) // asking for ideas
			{
				wordsFoundList += " asking-for-ideas &";
				p[v_pepnumber].messages[v_counter].setMessageGist("asking-for-ideas");
			}
			if (sentence.toLowerCase().contains("pronouncement")) // asking for
																	// pronouncement
			{
				wordsFoundList += " asking-for-pronouncement &";
				p[v_pepnumber].messages[v_counter].setMessageGist("asking-for-pronouncement");
			}
		}

		// avoting
		if (sentence.toLowerCase().contains("i") || sentence.contains("i") || sentence.contains("I")) {
			// System.out.println(" heer ==============================" );
			if (sentence.toLowerCase().contains("-1") || sentence.toLowerCase().contains("+1")
					|| sentence.toLowerCase().contains("-0") || sentence.toLowerCase().contains("+0")) {
				// System.out.println(" heer tt =============================="
				// + v_pep + " " + v_date);
				// QQQ wordsFoundList += " votes against &";
				if (p[v_pep].setvotingStartDate(v_date)) {
					wordsFoundList += " voting-start";
					p[v_pepnumber].messages[v_counter].setMessageGist("voting-start");
				}

				p[v_pep].setvotingEndDate(v_date);
			}
		}

		// Thanking for feedback
		if (sentence.toLowerCase().contains("thank") && sentence.toLowerCase().contains("feedback")) {
			wordsFoundList += " Thanking-for-feedback &";
			p[v_pepnumber].messages[v_counter].setMessageGist("Thanking-for-feedback");
		}
		// decision
		// if (sentence.toLowerCase().contains("so") &&
		// sentence.toLowerCase().contains("it is")) {
		// wordsFoundList += " decision made,";
		// }

		// asking to vote
		if ((sentence.toLowerCase().contains("vote") && (sentence.toLowerCase().contains("Please participate"))
				|| sentence.toLowerCase().contains("please indicate"))) {
			wordsFoundList += "d requests-vote &";
			p[v_pepnumber].messages[v_counter].setMessageGist("d-requests-vote");
		}

		// vote results
		if (sentence.toLowerCase().contains("vot") && sentence.toLowerCase().contains("result")
				&& sentence.toLowerCase().contains("summarized")) {
			wordsFoundList += "s vote-results &";
			p[v_pepnumber].messages[v_counter].setMessageGist("s-vote-results");
		}

		// The results shown below are for the complementary, UNOFFICIAL vote on
		// the issue of adding a ternary operator to Python (PEP308).
		if (sentence.toLowerCase().contains("results")) {
			// System.out.println(" alternate voting here====================="
			// + sentence);
			if (sentence.toLowerCase().contains("complementary")) { // ||
																	// sentence.toLowerCase().contains("unofficial")
				wordsFoundList += " complimentary-vote-results &";
				// System.out.println(" alternate voting
				// here=====================" + sentence);
				p[v_pep].setalternateVotingResultsDate(v_date);
			}
		}

		// Following the discussion, a vote was held. While there was an overall
		// + interest in having some form of if-then-else expressions, no one
		// + format was able to draw majority support. Accordingly, the PEP was
		// + rejected due to the lack of an overwhelming majority for change.
		// + Also, a Python design principle has been to prefer the status quo
		// + whenever there are doubts about which path to take.

		//

		// decision analysis
		if (sentence.toLowerCase().contains("i want") && sentence.toLowerCase().contains("reject")
				&& sentence.toLowerCase().contains("pep")) {
			wordsFoundList += "authorOfMessage-wants-pep-rejected &";
			p[v_pepnumber].messages[v_counter].setMessageGist("author-likes-pep");
		}
		// BDFL asking if there is consensus
		// if (author.contains("Guido") && sentence.toLowerCase().contains("Is")
		// && sentence.toLowerCase().contains("pep") &&
		// sentence.toLowerCase().contains("stage") &&
		// sentence.toLowerCase().contains("consensus") &&
		// sentence.toLowerCase().contains("?") ) {
		// wordsFoundList += " BDFL asking if there is consensus,";
		// }
		// BDFL decision
		if (sentence.toLowerCase().contains("bdfl") && !(sentence.toLowerCase().contains("no")
				|| sentence.toLowerCase().contains("nor") || sentence.toLowerCase().contains("none"))) {
			if (sentence.toLowerCase().contains("decision")) {
				wordsFoundList += " bdfl-decision &";
				p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-decision");
			}
			if (sentence.toLowerCase().contains("pronouncement")) {
				wordsFoundList += " a bdfl-pronouncement &";
				p[v_pepnumber].messages[v_counter].setMessageGist("a-bdfl-pronouncement");
			}
			if (sentence.toLowerCase().contains("reject")) {
				if (sentence.toLowerCase().contains("pronouncement")) {
					// /rejected by BDFL pronouncement.
					wordsFoundList += " rejected-by-bdfl-pronouncement &";
					p[v_pepnumber].messages[v_counter].setMessageGist("rejected-by-bdfl-pronouncement");
				} else {
					wordsFoundList += " bdfl-rejection &";
					p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-rejection");
				}
			}
			if (sentence.toLowerCase().contains("accepted") || sentence.toLowerCase().contains("accept")) {
				if (sentence.toLowerCase().contains("bdfl pronouncement")) {
					wordsFoundList += " bdfl-accepted-as-per-BDFL-pronouncement &";
					p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-accepted-as-per-BDFL-pronouncement");
				} else {
					wordsFoundList += " bdfl-accepted &";
					p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-accepted");
				}
			}
			if (sentence.toLowerCase().contains("No argument")
					&& sentence.toLowerCase().contains("totally convincing")) {
				wordsFoundList += " bdfl-finds-not-convincing &";
				p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-finds-not-convincing");
			}
		}
		// PEP 214 and 221 are accepted, as per recent BDFL pronouncements

		//
		if (sentence.toLowerCase().contains("twitter")) {
			if (sentence.toLowerCase().contains(" want pep")) {
				wordsFoundList += " support for pep on twitter";
				p[v_pepnumber].messages[v_counter].setMessageGist("support-for-pep-on-twitter");
			}

		}

		if (sentence.toLowerCase().contains("accept")) {
			String previousSentence = p[v_pepnumber].messages[v_counter]
					.getPreviousSentence(p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter() - 2);
			if (previousSentence.contains("wrangling")) {
				// System.out.println("
				// _wrangling_________________________________________________________
				// ");
				// System.out.println(" _message id___" +
				// p[v_pepnumber].messages[v_counter].getMessageId() + "_current
				// sentence counter___" +
				// p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter());
				// System.out.println(" _current sentence counter___" +
				// p[v_pepnumber].messages[v_counter].getPreviousSentence(p[v_pepnumber].messages[v_counter].getCurrentSentenceCounter()
				// -2));
				wordsFoundList += " bdfl-decision-accepting-when-wrangling &";
				p[v_pepnumber].messages[v_counter].setMessageGist("bdfl-decision-accepting-when-wrangling");
			}
		}

		if (sentence.toLowerCase().contains("r>")) {
			// System.out.println("
			// _wrangling_________________________________________________________
			// ");
			wordsFoundList = "";
			p[v_pepnumber].messages[v_counter].setMessageGist(null);
		}

		// ADD MESSAGE CLASSIFICATION INFO

		// I'm sorry I won't be able to come to the language summit, but I would
		// like if
		// possible to expedite a pronouncement on PEP 391 (configuring logging
		// using
		// dictionaries). I believe I addressed all the comments made on the
		// discussion
		// threads mentioned in the PEP and so I'm not sure what more I need to
		// do to get a pronouncement. I guess the stdlib slot gives an
		// opportunity for people to air their views and so I'd be grateful if
		// you added it to the agenda.
		// status changed

		// System.out.println(" wordsFoundList" + wordsFoundList);
		return wordsFoundList;
	}

}
