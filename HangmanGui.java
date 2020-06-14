import javax.swing.JFrame;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.GridLayout;

public class HangmanGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField wordInProgress;
	private JLabel gallows;
	private JLabel livesLeft;
	private JLabel currentChar;// empty initially, latest keystroke
	private String ourCopyOfSecretWord = "";
	private JComponent[] keyButtons;
	private JRadioButton onePlayerButton = new JRadioButton("1 player", true);
	private JRadioButton twoPlayerButton = new JRadioButton("2 players", false);
	private static final String keys = "qwertyuiopasdfghjkl-zxcvbnm";

	public char getNextChar() {
		char temp = ' ';
		if (currentChar.getText().length() > 0) {
			temp = currentChar.getText().charAt(0);
			int i = keys.indexOf(temp);
			if (i != -1) {
				keyButtons[i].setEnabled(false);
			}
		}
		currentChar.setText("");
		return temp;
	}

	public void youWin() {
		JOptionPane.showMessageDialog(this, "You guessed the word!\n" + "Nice win.");
	}

	public void youLose(String word) {
		JOptionPane.showMessageDialog(this,
				"Game over you suck.\nThe correct word was " + word + " how did you not guess that?");
	}

	public int getNumPlayers() {
		if (twoPlayerButton.isSelected()) {
			return 2;
		} else {
			return 1;
		}
	}

	private void unlockKeys() {
		// idea, maybe never unlock the label at the end of the second row?
		for (int i = 0; i < keyButtons.length; i++) {
			keyButtons[i].setEnabled(true);
		}
	}
	
	private void lockKeys() {
		for (int i = 0; i < keyButtons.length; i++) {
			keyButtons[i].setEnabled(false);
		}
	}
	
	private void initDisplay(int totalLives, String newSecretWord) {
		// reset for new game, possibly the first game, clear out any prior info or
		// settings
		currentChar.setText("");// no letters guessed yet
		showLivesLeft(totalLives);
		char[] blanks = new char[newSecretWord.length()];
		for (int i = 0; i < newSecretWord.length(); i++) {
			blanks[i] = '_';
		}
		updateWordInProgress(blanks);
		unlockKeys();
	}

	public void initGameGui(int totalLives, String newSecretWord) {
		// init graphics and all member variables
		initDisplay(totalLives, newSecretWord);
		setOurCopyOfSecretWord(newSecretWord);
	}

	/**
	 * Create the frame.
	 */
	public HangmanGui(int totalLives, String newSecretWord) {
		setBounds(100, 100, 1600, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("AP CS Hangman");
		getContentPane().setLayout(new GridLayout(1, 1, 0, 0));// 1 by 1 grid says as large as possible

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 10));//10 columns, as many rows as needed
		getContentPane().add(panel);

		wordInProgress = new JTextField();
		wordInProgress.setFont(new Font("SansSerif", Font.PLAIN, 12));
		panel.add(wordInProgress);
		wordInProgress.setColumns(15);// length of longest word? May need to be higher, but would want wider box

		gallows = new JLabel("");
		gallows.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(gallows);

		JLabel livesLeftLabel = new JLabel("Lives left:");
		livesLeftLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(livesLeftLabel);

		livesLeft = new JLabel("");
		livesLeft.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(livesLeft);

		ButtonGroup numPlayers = new ButtonGroup();
		numPlayers.add(onePlayerButton);
		numPlayers.add(twoPlayerButton);

		JLabel label = new JLabel("<html><center>" + "Select" + "<br>" + "number of players" + "</center></html>");
		panel.add(label);
		panel.add(onePlayerButton);
		panel.add(twoPlayerButton);

		JButton newGameButton = new JButton("<html><center>" + "Start" + "<br>" + "new game" + "</center></html>");
		panel.add(newGameButton);
		newGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("New Game button was pressed");// TODO: remove once done debugging
				// no need to unlockKeys, handled in call to initDisplay via initGameGui
				unlockKeys();// but for now, before "new game" works, it blocks the second game
				// TODO Moshe, add logic to let Hangman know to start new game, and maybe in the meantime stop any action on current game (disable all keys?)
			}
		});

		JButton giveUpButton = new JButton("I give up");
		panel.add(giveUpButton);
		giveUpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("I give up button was pressed");// TODO: remove once done debugging
				youLose(getOurCopyOfSecretWord());
				lockKeys();
			}
		});
		

		JButton exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panel.add(exitButton);

		keyButtons = new JComponent[keys.length()];
		int nextButtonIndex = 0;
		for (int i = 0; i < keys.length(); i++) {
			boolean fakeButton = (keys.charAt(i) == '-');
			if (!fakeButton) {
				keyButtons[nextButtonIndex] = new JButton(keys.substring(i, i + 1));
				keyButtons[nextButtonIndex].setEnabled(false);// not enabled until all there,tries to avoid lost first keystroke
				((JButton) keyButtons[nextButtonIndex]).setActionCommand(keys.substring(i, i + 1));
				((JButton) keyButtons[nextButtonIndex]).addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String temp = e.getActionCommand().substring(0, 1);
						String s = currentChar.getText();
						if (!(s.length() > 0 && Character.isAlphabetic(s.charAt(0)))) {
							currentChar.setText(temp);
						}
					}
				});
			} else {
				keyButtons[nextButtonIndex] = new JLabel("");
				keyButtons[nextButtonIndex].setEnabled(false);//initially not enabled, probably fine to leave it that way, but it's a label so no big deal either way
			}
			panel.add(keyButtons[nextButtonIndex]);
			nextButtonIndex++;
		}
		currentChar = new JLabel("");
		panel.add(currentChar);
		initGameGui(totalLives, newSecretWord);

		// calling program should setVisible(true)

	}

	public void updateWordInProgress(char[] wordWithUnderscores) {
		String result = "";
		boolean first = true;
		for (int i = 0; i < wordWithUnderscores.length; i++) {
			if (!first) {
				result += ' ';
			} else {
				first = false;
			}
			result += wordWithUnderscores[i];
		}
		wordInProgress.setText(result);
	}

	public void showLivesLeft(int numLivesLeft) {
		BufferedImage img = null;
		// TODO Elie choose image based on num lives left
		try {
			img = ImageIO.read(new URL(getGallowsFilename(lives)));
	private String getGallowsFilename(int numLives){
                String result = "https://upload.wikimedia.org/wikipedia/commons/8/8b/Hangman-0.png";
		if (lives == 0){
                       filename = "https://commons.wikimedia.org/wiki/File:Hangman-6.png";
                         }
                    else if  (lives == 1){
			      filename = "https://commons.wikimedia.org/wiki/File:Hangman-1.png";
	            else if  (lives == 2){
			      filename = "https://commons.wikimedia.org/wiki/File:Hangman-2.png";
		    else if  (lives == 3){
			      filename = "https://commons.wikimedia.org/wiki/File:Hangman-3.png";
	            else if  (lives == 4){
			      filename = "https://commons.wikimedia.org/wiki/File:Hangman-4.png";
		    else if  (lives == 5){
			      filename = "https://commons.wikimedia.org/wiki/File:Hangman-5.png";
	            else if  (lives == 6){
			      filename = "https://commons.wikimedia.org/wiki/File:Hangman-6.png";
			    else{
                                filename = "https://upload.wikimedia.org/wikipedia/commons/8/8b/Hangman-0.png";
                          }
		} catch (MalformedURLException e) {
			System.out.println("Error: bad URL requested");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error: could not open URL");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		ImageIcon icon = null;
		if (!(img == null)) {
			icon = new ImageIcon(img);
		}
		if (!(icon == null)) {
			gallows.setIcon(icon);
		}
		livesLeft.setText("" + numLivesLeft);
	}

	public String getOurCopyOfSecretWord() {
		return ourCopyOfSecretWord;
	}

	public void setOurCopyOfSecretWord(String ourCopyOfSecretWord) {
		this.ourCopyOfSecretWord = ourCopyOfSecretWord;
	}

}
