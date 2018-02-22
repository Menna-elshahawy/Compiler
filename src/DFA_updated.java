import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;

public class DFA_updated {

	@Override
	public String toString() {
		return "DFA_updated [states=" + states + ", acceptStates=" + acceptStates + ", alphabets=" + alphabets
				+ ", startstate=" + startstate + ", transitions=" + transitions + ", inputs=" + inputs + ", accepted="
				+ accepted + "]";
	}

	ArrayList<String> states = new ArrayList<>();
	ArrayList<String> acceptStates = new ArrayList<>();
	ArrayList<String> alphabets = new ArrayList<>();
	ArrayList<String> actions = new ArrayList<>();

	String startstate = "";
	ArrayList<ArrayList<String>> transitions = new ArrayList<>();
	ArrayList<ArrayList<String>> inputs = new ArrayList<>();

	boolean accepted = false;

	public DFA_updated(ArrayList<String> states, ArrayList<String> acceptStates, ArrayList<String> alphabets,
			String startstate, ArrayList<ArrayList<String>> transitions, ArrayList<ArrayList<String>> inputs,
			boolean accepted) {
		super();
		this.states = states;
		this.acceptStates = acceptStates;
		this.alphabets = alphabets;
		this.startstate = startstate;
		this.transitions = transitions;
		this.inputs = inputs;
		this.accepted = accepted;
	}
	public DFA_updated(ArrayList<String> states, ArrayList<String> acceptStates, ArrayList<String> alphabets,
			String startstate, ArrayList<ArrayList<String>> transitions, ArrayList<ArrayList<String>> inputs,
			boolean accepted , ArrayList<String>actions) {
		super();
		this.states = states;
		this.acceptStates = acceptStates;
		this.alphabets = alphabets;
		this.startstate = startstate;
		this.transitions = transitions;
		this.inputs = inputs;
		this.accepted = accepted;
		this.actions = actions;
	}
	public DFA_updated() {
		states = new ArrayList<>();
		acceptStates = new ArrayList<>();
		alphabets = new ArrayList<>();
		startstate = "";
		transitions = new ArrayList<>();
		inputs = new ArrayList<>();
	}

	public void solveInput(String s) {
		validateDFA();
	}

	public void clear() {
		states = new ArrayList<>();
		acceptStates = new ArrayList<>();
		alphabets = new ArrayList<>();
		startstate = "";
		transitions = new ArrayList<>();
		inputs = new ArrayList<>();
		accepted = false;

	}

	public void readFromFile(String filename) {
		LineNumberReader lineNumberReader = null;
		try {
			// Construct the LineNumberReader object
			lineNumberReader = new LineNumberReader(new FileReader(filename));

			// Read all lines now; Every read increase the line number by 1
			String line = null;
			while ((line = lineNumberReader.readLine()) != null) {
				// System.out.println("Line " + lineNumberReader.getLineNumber()
				// % 7 + ": " + line);
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
					validateDFA();
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
					validateInputs();
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

	public void validateInputs() {
		for (int i = 0; i < inputs.size(); i++) {
			checkInput(inputs.get(i));
		}
	}

	public boolean checkInput(ArrayList<String> input) {
		if (accepted) {
			// String lastState = "";
			String currentstate = startstate;
			for (int i = 0; i < input.size(); i++) {
				if (alphabets.contains(input.get(i)))
					currentstate = nextState(currentstate, input.get(i));
				else {
					System.out.println("Invalid input string at " + input.get(i));
					return false;
				}
			}
			if (checkInAcceptStates(currentstate)) {
				System.out.println("Accepted");
				return true;
			} else {
				System.out.println("Rejected");
				return false;
			}
		} else {
			System.out.println("Ignored");
			return false;
		}

	}

	public boolean checkInAcceptStates(String s) {
		for (int i = 0; i < acceptStates.size(); i++) {
			if (acceptStates.get(i).equals(s))
				return true;
		}
		return false;
	}

	public String nextState(String state, String literal) {
		for (int i = 0; i < transitions.size(); i++) {
			if (state.equals(transitions.get(i).get(0)) && literal.equals(transitions.get(i).get(2)))
				return transitions.get(i).get(1).trim();

		}
		return "";

	}

	public boolean validateDFA() {
		// checking accept states.
		for (int i = 0; i < acceptStates.size(); i++) {
			// to allow empty accept states
			if (!states.contains(acceptStates.get(i)) && !acceptStates.get(i).isEmpty()) {
				System.out.println("Invalid accept state " + acceptStates.get(i));
				return false;
			}
		}
		// check start state
		if (!states.contains(startstate)) {
			System.out.println("Invalid start state");
			return false;
		}
		// check transitions
		for (int i = 0; i < transitions.size(); i++) {
			if (transitions.get(i).size() < 3) {
				System.out.println("Incomplete transition "
						+ transitions.get(i).toString().replace("[", "").replace("]", "").replace(" ", ""));
				return false;
			}

			for (int j = 0; j < transitions.get(i).size(); j++) {
				// check first 2 exist in states

				if (!states.contains(transitions.get(i).get(j)) && j < 2) {
					System.out.println("Invalid transition "
							+ transitions.get(i).toString().replace("[", "").replace("]", "").replace(" ", "")
							+ " state " + transitions.get(i).get(j) + " does not exist");
					return false;
				}
				// check last one exists in alphabets

				if (!alphabets.contains(transitions.get(i).get(j)) && j >= 2) {
					System.out.println("Invalid transition "
							+ transitions.get(i).toString().replace("[", "").replace("]", "").replace(" ", "")
							+ " input " + transitions.get(i).get(j) + " is not in the alphabet");
					return false;
				}

			}

		}
		for (int i = 0; i < states.size(); i++) {
			ArrayList<String> transitionsForState = new ArrayList<>();
			for (int j = 0; j < transitions.size(); j++) {
				if (transitions.get(j).get(0).equals(states.get(i)))
					transitionsForState.add(transitions.get(j).get(2));
			}
			// System.out.println(transitionsForState);
			for (int j = 0; j < alphabets.size(); j++) {
				if (!transitionsForState.contains(alphabets.get(j))) {
					System.out.println("Missing transition for state " + states.get(i));
					return false;
				}
			}
		}

		System.out.println("DFA constructed");
		accepted = true;
		return true;

	}

	public boolean checkAcceptStates(String[] accstates) {
		for (int i = 0; i < accstates.length; i++) {
			if (!states.contains(accstates[i]))
				return false;
		}
		return true;
	}

	public static void main(String[] args) {
		DFA_updated b = new DFA_updated();
		b.readFromFile("in.in");
		System.err.println("--------------------------------------------------");
		b.readFromFile("in1.in");
	}
}
