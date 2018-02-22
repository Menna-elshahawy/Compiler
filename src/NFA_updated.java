import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class NFA_updated {

	ArrayList<String> states = new ArrayList<>();
	ArrayList<String> acceptStates = new ArrayList<>();
	ArrayList<String> alphabets = new ArrayList<>();
	String startstate = "";
	ArrayList<ArrayList<String>> transitions = new ArrayList<>();
	ArrayList<ArrayList<String>> inputs = new ArrayList<>();

	//
	HashSet<ArrayList<String>> dfa_transitions = new HashSet<ArrayList<String>>();
	HashSet<HashSet<String>> dfa_acceptStates = new HashSet<HashSet<String>>();
	TreeSet<String> helper = new TreeSet<String>();
	HashSet<HashSet<String>> stateshash = new HashSet<>();
	//
	boolean accepted = false;

	public void clear() {
		states = new ArrayList<>();
		acceptStates = new ArrayList<>();
		alphabets = new ArrayList<>();
		startstate = "";
		transitions = new ArrayList<>();
		inputs = new ArrayList<>();
		accepted = false;
		dfa_acceptStates.clear();
		dfa_transitions.clear();

	}

	public void readFromFile(String filename) {
		LineNumberReader lineNumberReader = null;

		try {
			// Construct the LineNumberReader object
			lineNumberReader = new LineNumberReader(new FileReader(filename));
			// Read all lines now; Every read increase the line number by 1
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				String[] s = line.split(",");
				switch (lineNumberReader.getLineNumber() % 7) {
				case 0:
					// new dfa
					System.out.println();
					clear();
					break;
				case 1:
					// states
					for (int i = 0; i < s.length; i++) {
						states.add(s[i]);
					}
					break;
				case 2:
					// accept states
					for (int i = 0; i < s.length; i++) {
						acceptStates.add(s[i]);
					}
					break;
				case 3:
					// alphabets
					for (int i = 0; i < s.length; i++) {
						alphabets.add(s[i]);
					}
					break;
				case 4:
					// start state
					startstate = s[0];
					break;
				case 5:
					// transitions
					String[] supdated = line.split("#");
					for (int i = 0; i < supdated.length; i++) {
						String[] sm = supdated[i].split(",");
						ArrayList<String> mm = new ArrayList<>();
						for (int j = 0; j < sm.length; j++) {
							mm.add(sm[j]);
						}
						transitions.add(mm);
					}
					validateNFA();

					break;
				case 6:
					// check inputs
					String[] supdated1 = line.split("#");
					for (int i = 0; i < supdated1.length; i++) {
						String[] sm = supdated1[i].split(",");
						ArrayList<String> mm = new ArrayList<>();
						for (int j = 0; j < sm.length; j++) {
							mm.add(sm[j]);
						}
						inputs.add(mm);
					}
					if (accepted)
						convertToDFA();
					break;

				default:
					break;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// Close the LineNumberReader
			try {
				if (lineNumberReader != null) {
					lineNumberReader.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void convertToDFA() {

		System.out.println("Equivalent DFA:");

		String dfa_startState = startstate;
		TreeSet<String> start = epsilonClosure(startstate);
		for (int i = 0; i < start.size(); i++) {
			dfa_startState += "*" + start.toArray()[i].toString().trim();
		}
		// System.out.println(dfa_startState);

		HashSet<String> dfa_states = new HashSet<>();
		dfa_states.add(dfa_startState);
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < dfa_states.size(); i++) {
				dfa_states.addAll(getAllStates((String) dfa_states.toArray()[i]));
			}
		}
		ArrayList<String> list = new ArrayList<>(dfa_states);

		System.out.println(list.toString().replace("]", "").replace("[", ""));

		ArrayList<String> dfa_acceptStates = new ArrayList<>();
		for (int i = 0; i < dfa_states.size(); i++) {
			if (checkIfAccept((String) dfa_states.toArray()[i]))
				dfa_acceptStates.add((String) dfa_states.toArray()[i]);
		}
		ArrayList<String> dfa_states2 = new ArrayList<>();
		for (int i = 0; i < dfa_states.size(); i++) {
			dfa_states2.add((String) dfa_states.toArray()[i]);
		}
		ArrayList<ArrayList<String>> dfa_transitions2 = new ArrayList<>();
		for (int i = 0; i < dfa_transitions.size(); i++) {
			dfa_transitions2.add((ArrayList<String>) dfa_transitions.toArray()[i]);
		}
		System.out.println(dfa_acceptStates.toString().replace("]", "").replace("[", ""));
		System.out.println(alphabets.toString().replace("]", "").replace("[", "").replace(" ", ""));

		System.out.println(dfa_startState);
		System.out.println(
				dfa_transitions.toString().replace("], [", "#").replace(" ", "").replace("[[", "").replace("]]", ""));
		System.out.println(inputs.toString().replace("], [", "#").replace(" ", "").replace("[[", "").replace("]]", ""));
		DFA_updated dfa = new DFA_updated(dfa_states2, dfa_acceptStates, alphabets, dfa_startState, dfa_transitions2,
				inputs, accepted);
		dfa.validateDFA();
		for (int i = 0; i < inputs.size(); i++) {
			// System.out.println(inputs.get(i));
			dfa.checkInput(inputs.get(i));
		}

	}

	public HashSet<String> getAllStates(String state) {
		String[] splitted = state.split("\\*");
		HashSet<String> out = new HashSet<>();
		for (int i = 0; i < alphabets.size(); i++) {
			ArrayList<String> check = new ArrayList<>();
			TreeSet<String> temp = new TreeSet<>();
			for (int j = 0; j < splitted.length; j++) {
				temp.addAll(allPossibleNextStates(splitted[j].trim(), alphabets.get(i)));
			}
			for (int j = 0; j < splitted.length; j++) {

				if (!temp.isEmpty()) {
					out.add(temp.toString().replace(", ", "*").replace("[", "").replace("]", "").trim());
					ArrayList<String> trans = new ArrayList<>();
					trans.add(state.toString().replace(", ", "*").replace("[", "").replace("]", "").trim());
					trans.add(temp.toString().replace(", ", "*").replace("[", "").replace("]", "").trim());
					trans.add(alphabets.get(i));
					dfa_transitions.add(trans);
					check.add("check");
				}
			}
			if (check.isEmpty()) {
				out.add("Dead");
				ArrayList<String> trans = new ArrayList<>();
				trans.add(state.toString().replace(", ", "*").replace("[", "").replace("]", "").trim());
				trans.add("Dead");
				trans.add(alphabets.get(i));
				dfa_transitions.add(trans);

			}
		}
		return out;
	}

	public boolean checkIfAccept(String state) {
		String[] splitted = state.split("\\*");
		for (int i = 0; i < splitted.length; i++) {
			for (int j = 0; j < acceptStates.size(); j++) {
				if (splitted[i].equals(acceptStates.get(j)))
					return true;

			}
		}
		return false;
	}

	// gets the next state from a state with a literal
	public TreeSet<String> allPossibleNextStates(String state, String literal) {
		TreeSet<String> output = new TreeSet<>();
		for (int j = 0; j < transitions.size(); j++) {
			if (transitions.get(j).get(0).equals(state) && transitions.get(j).get(2).equals(literal)) {
				// if (!output.contains(transitions.get(j).get(1))) {
				output.add(transitions.get(j).get(1));
				output.addAll(epsilonClosure(transitions.get(j).get(1)));
				// }
				// System.err.println(output);
			}

		}

		return output;
	}

	// to get all the epsilon transitions for a state
	public TreeSet<String> epsilonClosure(String state) {
		HashSet<String> epsilonClosures = new HashSet<String>();
		epsilonClosures.add(state);
		helper.clear();
		return epRec(epsilonClosures, state);
	}

	public TreeSet<String> epRec(HashSet<String> out, String state) {
		if (out.isEmpty()) {
			return helper;
		}
		for (int i = 0; i < transitions.size(); i++) {
			// found an epsilon
			if (transitions.get(i).get(0).equals(state) && transitions.get(i).get(2).equals("$")) {
				out.add(transitions.get(i).get(1));
				out.remove(state);
				if (!helper.contains(transitions.get(i).get(1)))
					helper.add(transitions.get(i).get(1));
				else {
//					return helper;
					return epRec(out, transitions.get(i).get(1));

				}
//				return epRec(out, transitions.get(i).get(1));
			}
		}
		return helper;
	}

	public void validateNFA() {

		boolean f = true;
		while (f) {
			// checking accept states.
			for (int i = 0; i < acceptStates.size(); i++) {
				// to allow empty accept states
				if (!states.contains(acceptStates.get(i)) && !acceptStates.get(i).isEmpty()) {
					System.out.println("Invalid accept state " + acceptStates.get(i));
					f = false;
				}
			}
			// check start state
			if (!states.contains(startstate)) {
				System.out.println("Invalid start state");
				f = false;
			}
			// check transitions
			for (int i = 0; i < transitions.size(); i++) {
				if (transitions.get(i).size() < 3) {
					System.out.println("Incomplete transition "
							+ transitions.get(i).toString().replace("[", "").replace("]", "").replace(" ", ""));
					f = false;
				}

				for (int j = 0; j < transitions.get(i).size(); j++) {
					// check first 2 exist in states

					if (!states.contains(transitions.get(i).get(j)) && j < 2) {
						System.out.println("Invalid transition "
								+ transitions.get(i).toString().replace("[", "").replace("]", "").replace(" ", "")
								+ " state " + transitions.get(i).get(j) + " does not exist");
						f = false;
						break;
					}
					// check last one exists in alphabets or epsilon transition.
					if (!transitions.get(i).get(j).equals("$") && !alphabets.contains(transitions.get(i).get(j))
							&& j >= 2) {
						System.out.println("Invalid transition "
								+ transitions.get(i).toString().replace("[", "").replace("]", "").replace(" ", "")
								+ " input " + transitions.get(i).get(j) + " is not in the alphabet");
						f = false;
						break;
					}

				}

			}
			if (f) {
				System.out.println("NFA constructed");
				accepted = true;
				break;
			} else {
				accepted = false;
				System.out.println("DFA Construction skipped and inputs are ignored");
				break;
			}
		}

	}

	public static void main(String[] args) {
		PrintStream out;
		// Redirect console output to output.txt file
		try {
			out = new PrintStream(new File("output.txt"));
			System.setOut(out);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		NFA_updated b = new NFA_updated();
		b.readFromFile("nfain.in");
		System.out.println("-----------------------------------------------------------");
		b.readFromFile("nfain1.in");

	}
}
