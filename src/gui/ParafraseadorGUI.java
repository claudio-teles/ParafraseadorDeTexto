package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import manipulacao.dom.ManipulacaoDOM;
import util.UtilitarioGeral;

public class ParafraseadorGUI {
	
	private static final int HTML = 1;
	private static final int PDF = 2;
	private static final int DOC = 3;

	private JFrame frame;
	private JLabel lblStatus;
	private JSplitPane splitPane;
	private JTextArea textAreaEsquerda;
	private JTextArea textAreaDireita;
	private JTextField textFieldPesquisa;
	
	private String textoOriginal;
	private String textoModificado;
	private String palavraChave;
	
//	private List<String> palavras = new ArrayList<>();
//	private List<String> sinonimos = new ArrayList<>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ParafraseadorGUI window = new ParafraseadorGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ParafraseadorGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		lblStatus = new JLabel("Status");
		frame.getContentPane().add(lblStatus, BorderLayout.SOUTH);
		
		JButton btnParafrasear = new JButton("Parafrasear");
		btnParafrasear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				List<String> palavras = new ArrayList<>();
				
				palavras.clear();
				palavras.addAll(Arrays.asList(textAreaDireita.getText().split(" ")));
				
				UtilitarioGeral utilitario = new UtilitarioGeral();
				
				List<String> sinonimos = new ArrayList<>();
				
				palavras.forEach(palavra -> {
					try {
						sinonimos.addAll(utilitario.obterSinonimosOnline(palavras.get(palavras.indexOf(palavra))));
						String expressaoFinalPesquisa = utilitario.geradorTermosPesquisa(palavraChave, sinonimos);
						
						ManipulacaoDOM md = new ManipulacaoDOM();
						String linkDeConsulta = md.getQueryGoogle().append(expressaoFinalPesquisa).toString();
						
						List<String> links = new ArrayList<>();
						links.addAll(md.obterLinksRedePesquisaGoogle(linkDeConsulta).collect(Collectors.toList()));
						
						links.forEach(link -> {
							if (links.size() > 0) {
								if (link.contains("url")) {
									String linkFinal = "";
									try {
										// obter redirecionamento da url
										linkFinal  = utilitario.obterLinkRedecionamentoUrl(link);
										
										int tipoDeArquivo = ((!linkFinal.toLowerCase().endsWith(".pdf") 
												&& !linkFinal.toLowerCase().endsWith(".doc"))) ? HTML 
												: linkFinal.toLowerCase().endsWith(".pdf") ? PDF : DOC;
										
										switch (tipoDeArquivo) {
										case PDF:
											// ler arquivo pdf online
											
											
										case DOC:
											// ler arquivo doc online
											
											
										default:
											// ler arquivo html online
											
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								} else {

								}
							}
						});
						
						sinonimos.clear();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		});
		frame.getContentPane().add(btnParafrasear, BorderLayout.EAST);
		
		splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		textAreaEsquerda = new JTextArea();
		textAreaEsquerda.setBackground(new Color(245, 245, 245));
		
		textAreaEsquerda.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				textoOriginal = textAreaEsquerda.getText();
				textAreaDireita.setText(textoOriginal);
				textAreaDireita.repaint();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				textoModificado = textAreaEsquerda.getText();
				textAreaDireita.setText(textoModificado);
				textAreaDireita.repaint();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				System.out.println("Mudança na esquerda");
			}
		});
		splitPane.setLeftComponent(textAreaEsquerda);
		
		textAreaDireita = new JTextArea();
		textAreaDireita.setBackground(new Color(245, 245, 245));
		splitPane.setRightComponent(textAreaDireita);
		splitPane.setDividerLocation(300);
		
		textFieldPesquisa = new JTextField();
		textFieldPesquisa.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String palavra = "";
				palavra += textFieldPesquisa.getText();
				palavraChave = palavra;
			}
		});
		frame.getContentPane().add(textFieldPesquisa, BorderLayout.NORTH);
		textFieldPesquisa.setColumns(10);
	}

}
