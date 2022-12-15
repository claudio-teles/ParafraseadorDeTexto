package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.MalformedURLException;
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

import manipulacao.documento.ManipulacaoDocumento;
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
	private String contextoString = new String();
	private String novoTexto = new String();
	private String palavrasNegativas = new String(
				"o a os as um uma uns umas ao aos à às do dos da das dum duns duma dumas no nos na nas num nuns numa numas pelo pelos pela pelas\n" + 
				"eu tu ele ela nós vós eles elas me te se lhe nos vos lhes mo mos ma mas to tos ta tas lho lhos lha lhas no-lo no-los no-la no-las\n" + 
				"vo-lo vo-los vo-la vo-las no nos na nas mim comigo ti contigo conosco convosco te si consigo você vocês meu minha meus minhas\n" + 
				"teu tua teus teuas seu sua seus suas nossa nossa nossos nossas vosso vossa vossos vossas este esta esse essa aquele aquela\n" + 
				"estes estas esses essas aqueles aquelas isto isso aquilo mesmo mesmos àquele àquela deste desta disso nisso no tal naquilo\n" + 
				"algo alguém fulano sicrano beltrano nada ninguém outrem quem tudo cada certo certa certos certas tanto tantas quer seja talvez\n" + 
				"algum alguns alguma bastante demais mais menos muito muita nenhum nenhuns nenhuma outro outra pouco pouca qualquer quaisquer\n" + 
				"qual que quanto quanta tais tanto tanta todo toda vários várias muito muita nenhuma outro outra poucos poucas quantos quantas\n" + 
				"todos todas alguém algo nenhum ninguém nada onde ante após até com contra de desde em entre para per perante por sem sob sobre trás\n" + 
				"como conforme segundo consoante durante salvo fora mediante tirante exceto senão visto nem não só mas também como também\n" + 
				"bem como ainda porém contudo todavia entretanto entanto obstante ou ora já logo pois portanto conseguinte isso assim porque\n" + 
				"agora quando"
			);
	
	private int quantidadeTotalPalavras = 0;
	private int posicaoPalavraSelecionada = 0;
	
	private List<String> palavras = new ArrayList<>();
	private List<String> sinonimos = new ArrayList<>();
	
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
	
	private void lerDocumentoRemoto(ManipulacaoDocumento manipulacaoDocumento, ManipulacaoDOM md,
			String linkFinal) throws MalformedURLException, IOException {
		int tipoDeArquivo = ((!linkFinal.toLowerCase().endsWith(".pdf") 
				&& !linkFinal.toLowerCase().endsWith(".doc")) 
				&& !linkFinal.toLowerCase().endsWith(".text")) ? HTML 
				: linkFinal.toLowerCase().endsWith(".pdf") ? PDF : DOC;
		
		switch (tipoDeArquivo) {
		case PDF:
			// ler arquivo pdf online
			contextoString += manipulacaoDocumento.obterConteudoArquivoPDF(linkFinal);
			break;
			
		case DOC:
			// ler arquivo doc online
			contextoString += manipulacaoDocumento.obterConteudoArquivoDoc(linkFinal);
			break;
			
		default:
			// ler arquivo html online
			contextoString += md.extrairTextoSiteHTML(linkFinal);
			break;
		}
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
				
				String texto = textAreaDireita.getText();
				String textoSemPontuacao = this.removerSinaisDePontuacao(texto);
				
				palavras.clear();
				palavras.addAll(Arrays.asList(textoSemPontuacao.split(" ")));
				
				UtilitarioGeral utilitario = new UtilitarioGeral();
				
				quantidadeTotalPalavras = palavras.size();
				
				palavras.forEach(palavra -> {
					if (!palavrasNegativas.contains(palavra)) {
						posicaoPalavraSelecionada = palavras.indexOf(palavra);
						
						lblStatus.setText(
											"Status: A palavra"+palavra+" foi a última selecionada. "
													+ "Etapa: "+posicaoPalavraSelecionada+"/"+quantidadeTotalPalavras+" = "
													+String.valueOf(((posicaoPalavraSelecionada + 1) / quantidadeTotalPalavras))
										);
						
						contextoString = new String();
						
						ManipulacaoDocumento manipulacaoDocumento = new ManipulacaoDocumento();
						ManipulacaoDOM md = new ManipulacaoDOM();
						
						try {
							sinonimos.clear();
							sinonimos.addAll(utilitario.obterSinonimosOnline(palavras.get(posicaoPalavraSelecionada)));
							String expressaoFinalPesquisa = utilitario.geradorTermosPesquisa(palavraChave, sinonimos);
							
							String linkDeConsulta = md.getQueryGoogle().append(expressaoFinalPesquisa).toString();
							
							List<String> links = new ArrayList<>();
							links.addAll(md.obterLinksRedePesquisaGoogle(linkDeConsulta).collect(Collectors.toList()));
							
							int quantidadeLinks = links.size();
							
							links.forEach(linkOriginal -> {
								if (quantidadeLinks > 0) {
									if (linkOriginal.contains("url")) {// link com redirecionamento
										String linkFinal = "";
										try {
											// obter redirecionamento da url
											linkFinal  = utilitario.obterLinkRedecionamentoUrl(linkOriginal);
											
											lerDocumentoRemoto(manipulacaoDocumento, md, linkFinal);
										} catch (IOException e) {
//											e.printStackTrace();
										}
									} else {
										// link normal sem redirecionamento
										try {
											lerDocumentoRemoto(manipulacaoDocumento, md, linkOriginal);
										} catch (IOException e) {
//											e.printStackTrace();
										}
									}
								}
							});
							
							String palavraSorteada = utilitario
									.sortearPalavra(utilitario.obterFrequencia(sinonimos, Arrays.asList(contextoString.split(" "))));
							contextoString = new String();
							
							palavras.set(posicaoPalavraSelecionada, palavraSorteada);
							palavras.stream().forEach(item -> novoTexto += item+" ");
							
							textAreaDireita.setText(novoTexto); 
							
							sinonimos.clear();
						} catch (Exception e) {
							e.printStackTrace();
						}
					
					}
				});
				
				palavras.clear();
			}

			private String removerSinaisDePontuacao(String texto) {
				String textoSemPontuacao = "";
				
				if (texto.contains(".")) {
					textoSemPontuacao = texto.replaceAll(".", "");
				}
				
				if (texto.contains(",")) {
					textoSemPontuacao = texto.replaceAll(",", "");
				}
				
				if (texto.contains(";")) {
					textoSemPontuacao = texto.replaceAll(";", "");
				}
				
				if (texto.contains(":")) {
					textoSemPontuacao = texto.replaceAll(":", "");
				}
				
				if (texto.contains("?")) {
					textoSemPontuacao = texto.replaceAll("?", "");
				}
				
				if (texto.contains("!")) {
					textoSemPontuacao = texto.replaceAll("!", "");
				}
				
				if (texto.contains("\'")) {
					textoSemPontuacao = texto.replaceAll("\'", "");
				}
				
				if (texto.contains("\"")) {
					textoSemPontuacao = texto.replaceAll("\"", "");
				}
				
				if (texto.contains("(")) {
					textoSemPontuacao = texto.replaceAll("(", "");
				}
				
				if (texto.contains(")")) {
					textoSemPontuacao = texto.replaceAll(")", "");
				}
				return textoSemPontuacao;
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
