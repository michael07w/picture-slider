import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.MalformedURLException;
import java.net.URL;

public class PictureSlider extends JFrame {
	private final Timer timer = new Timer(5000, null);
	private JFileChooser chooser;
	private ImageIcon[] icons;
	private static int j = 0;		// tracks which photo is displayed
	private static int photoCount = 0; // number of photos to be displayed
	
	public PictureSlider() {
		super("Picture Slider");
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// add label to display images
		JLabel label = new JLabel();
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.add(label);
		Container contentPane = getContentPane();
		contentPane.add(mainPanel);
		
		// add picture control area
		JPanel bottomPanel = new JPanel(new FlowLayout());
		JButton home = new JButton("Home");
		bottomPanel.add(home);
		JButton prev = new JButton("Previous");
		bottomPanel.add(prev);
		JButton play = new JButton("Play");
		bottomPanel.add(play);
		JButton next = new JButton("Next");
		bottomPanel.add(next);
		JButton last = new JButton("Last");
		bottomPanel.add(last);
		JButton thumbnail = new JButton("Photos");
		mainPanel.add(bottomPanel, getConstraints(0, 1, 0.0, 0.1, GridBagConstraints.PAGE_END));
		
		// add home button functionality
		home.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// display first photo
				j = 0;
				label.setIcon(icons[j]);
				timer.stop();
			}
		});
		
		// add previous button functionality
		prev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// display previous photo
				if (--j == -1)
					j = photoCount - 1;
				label.setIcon(icons[j]);
				timer.stop();
			}
		});
		
		// add play button functionality
		play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// start timer to begin slideshow
				timer.start();
			}
		});
		
		// add next button functionality
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// display next photo
				if (++j == photoCount)
					j = 0;
				label.setIcon(icons[j]);
				timer.stop();
			}
		});
		
		// add last button functionality
		last.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// display last photo
				j = photoCount - 1;
				label.setIcon(icons[j]);
				timer.stop();
			}
		});
		
		// add thumbnail button functionality
		thumbnail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// create scrollable window for display of photos
				JFrame frame = new JFrame();
				JPanel container = new JPanel();
				JScrollPane scrPane = new JScrollPane(container);
				frame.add(scrPane);
				// determine number of rows to generate
				int rows = 1;
				if (photoCount > 4 && photoCount % 4 > 0)
					rows = photoCount / 4 + 1;
				else if (photoCount > 4)
					rows = photoCount / 4;

				container.setLayout(new GridLayout(rows, 4));
				
				// populate labels with photo thumbnails
				JLabel labels[] = new JLabel[photoCount];
				for (int i = 0; i < photoCount; i++) {
					labels[i] = new JLabel();
					labels[i].setIcon(new ImageIcon(getScaledImage(icons[i].getImage(), 120, 100)));
					container.add(labels[i]);
				}
				frame.setSize(700, 450);
				frame.setVisible(true);
			}
		});
		
		// set up file chooser
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));
		
		// set up menu bar
		JMenuBar menuBar = new JMenuBar();
		contentPane.add(menuBar, BorderLayout.NORTH);
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		JMenuItem openItem = new JMenuItem("Open");
		JMenuItem exitItem = new JMenuItem("Exit");
		fileMenu.add(openItem);
		fileMenu.add(exitItem);
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMenu);
		JMenuItem aboutItem = new JMenuItem("About");
		JMenuItem userManItem = new JMenuItem("Help/User Manual");
		helpMenu.add(aboutItem);
		helpMenu.add(userManItem);
		
		// open file functionality
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> photos = new ArrayList<String>();
				// trigger file chooser dialog
				int r = chooser.showOpenDialog(null);
				// get images from file
				if (r == JFileChooser.APPROVE_OPTION) {
					String name = chooser.getSelectedFile().getPath();
					try {
						photos = getPhotos(name);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					
					// create ImageIcons from provided photo URLs
					icons = new ImageIcon[photoCount];
					for (int i = 0; i < photos.size(); i++) {
						try {
							ImageIcon unsizedImg = new ImageIcon(new URL(photos.get(i)));
							ImageIcon resizedImg = new ImageIcon(getScaledImage(unsizedImg.getImage(), 600, 500));
							icons[i] = resizedImg;
						} catch (MalformedURLException e1) {
							e1.printStackTrace();
						}
					}
					label.setIcon(icons[j]);		// display first image
					
					// display thumbnail button
					bottomPanel.add(thumbnail);
				}
			}
		});
		
		// add exit option functionality
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(EXIT_ON_CLOSE);
			}
		});
		
		// add about option functionality
		aboutItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame("About");
				frame.setLayout(new GridBagLayout());
				JLabel author = new JLabel("Author: Michael Wessels");
				JLabel version = new JLabel("Version: 1.0");
				frame.add(author, getConstraints(0, 0, 0, 0, GridBagConstraints.CENTER));
				frame.add(version, getConstraints(0, 1, 0, 0, GridBagConstraints.CENTER));
				frame.setSize(500, 350);
				frame.setVisible(true);
			}
		});
		
		// add help/user manual option functionality
		userManItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame("Help/User Manual");
				frame.setLayout(new GridBagLayout());
				JLabel sysReqs = new JLabel("1. System Requirements");
				JLabel macOS = new JLabel("				- Mac OSX");
				JLabel win10 = new JLabel("				- Windows 7/8/10");
				JLabel slideFormat = new JLabel("2. Slide Format File");
				JLabel fileType = new JLabel("				- A txt file in the following format:");
				ImageIcon ii = new ImageIcon("example.png");
				JLabel fileEx = new JLabel(ii);
				JLabel instructions = new JLabel("3. Starting the System");
				JLabel steps = new JLabel("				- Select File > Open > select correctly formatted txt file > images will display within 5 secs");

				frame.add(sysReqs, getConstraints(0, 0, 0.1, 0.1, GridBagConstraints.FIRST_LINE_START));		// add 1st label
				frame.add(win10, getConstraints(0, 1, 0.1, 0.1, GridBagConstraints.FIRST_LINE_START));			// add 2nd label
				frame.add(macOS, getConstraints(0, 2, 0.1, 0.2, GridBagConstraints.FIRST_LINE_START));			// add 3rd label
				frame.add(slideFormat, getConstraints(0, 3, 0.1, 0.1, GridBagConstraints.FIRST_LINE_START));	// add 4th label
				frame.add(fileType, getConstraints(0, 4, 0.1, 0.1, GridBagConstraints.FIRST_LINE_START));		// add 5th label
				
				JPanel panel = new JPanel(); 	// panel to house the example image in correct location
				panel.add(fileEx);
				frame.add(panel, getConstraints(0, 5, 0.1, 0.2, GridBagConstraints.FIRST_LINE_START));			// add 6th label
				frame.add(instructions, getConstraints(0, 6, 0.1, 0.1, GridBagConstraints.FIRST_LINE_START));	// add 7th label
				frame.add(steps, getConstraints(0, 7, 0.1, 0.1, GridBagConstraints.FIRST_LINE_START));			// add 8th label
				
				frame.setSize(600, 350);
				frame.setVisible(true);
			}
		});
		
		// set action to be completed on each timer cycle
		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// rotate through photos, displaying each one
				if (++j == photoCount)
					j = 0;
				label.setIcon(icons[j]);
			}
		});
	}
	
	
	// Gets/Returns photo URLs from passed filename
	private ArrayList<String> getPhotos(String filename) throws FileNotFoundException {
		ArrayList<String> photoURLs = new ArrayList<String>();
		Scanner sc = new Scanner(new File(filename));
		String delims = "[#]+";
		int i = 0;
		while (sc.hasNext()) {
			String phrase = sc.nextLine();
			String[] tokens = phrase.split(delims);
			photoURLs.add(i++, tokens[1]);
			System.out.println(tokens[1]);
		}
		photoCount = i;
		System.out.println(photoCount);
		return photoURLs;
	}
	
	
	// Resizes photos for display
	private Image getScaledImage(Image srcImg, int w, int h) {
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();
		
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		
		return resizedImg;
	}
	
	// Configures GridBagConstraints to add items to GridBagLayout
	private GridBagConstraints getConstraints(int gridx, int gridy, double weightx, double weighty, int anchor) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		c.weightx = weightx;
		c.weighty = weighty;
		c.anchor = anchor;
		
		return c;
	}
	
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable () {
			@Override
			public void run() {
				PictureSlider frame = new PictureSlider();
				frame.setVisible(true);
			}
		});
	}
}
