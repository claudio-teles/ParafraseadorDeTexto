package manipulacao.dom;

import java.io.IOException;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import util.UtilitarioGeral;

public class ManipulacaoDOM {
	
	private StringBuilder queryGoogle = new StringBuilder("https://www.google.com.br/search?q=");
	private StringBuilder contextoHTML = new StringBuilder();
	
	private Document documento;
	private Document paginaDePesquisaDoGoogle;

	public ManipulacaoDOM() {
		super();
	}

	public StringBuilder getContextoHTML() {
		return contextoHTML;
	}

	public void setContextoHTML(StringBuilder contextoHTML) {
		this.contextoHTML = contextoHTML;
	}

	public Document getDocumento() {
		return documento;
	}

	public void setDocumento(Document documento) {
		this.documento = documento;
	}

	public static void main(String[] args) {
		new ManipulacaoDOM().teste();
	}
	
	public StringBuilder getQueryGoogle() {
		return queryGoogle;
	}

	public void setQueryGoogle(StringBuilder queryGoogle) {
		this.queryGoogle = queryGoogle;
	}

	public void teste() {
//		ManipulacaoDocumento doc = new ManipulacaoDocumento();
//		String url = "https://ia600305.us.archive.org/20/items/ComoLidarComMulheres/Nessahan2007-VersoBruta.pdf";
//		
//		String c = "";
//		do {
//			if (url.contains(".pdf")) {
//				try {
//					c = doc.obterConteudoArquivoPDF(url);
//					System.out.println(c);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			System.out.println(c);
//		} while (c.equals(""));
		
//		List<String> palavras = new ArrayList<String>();
//		palavras.add("maçã");
//		palavras.add("banana");
//		palavras.add("laranja");
//		palavras.add("goiaba");
//		
//		List<String> urls = new ArrayList<String>();
//		urls.add("src/index.html");
//		
		UtilitarioGeral util = new UtilitarioGeral();
//		
//		FrequenciaPalavras fp = new FrequenciaPalavras();
//		fp = util.obterFrequencia(palavras, urls, this);
//		
		String palavraChave = "percorrendo";
		
		try {
			System.out.println(util.geradorTermosPesquisa("comandante", util.obterSinonimosOnline(palavraChave)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		String linkDeConsulta = queryGoogle.append("bolo+chocolate").toString();
//		
//		try {
//			this.obterLinksRedePesquisaGoogle(linkDeConsulta).forEach(link -> System.out.println(link));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	public String extrairTextoSiteHTML(String url) throws IOException {
		if (!url.endsWith(".pdf") && !url.endsWith(".doc") && !url.endsWith(".docx") && !url.endsWith(".ppt") && !url.endsWith(".pptx")) {
			documento = Jsoup.connect(url).get();
			contextoHTML.append(documento.getElementsByTag("title").text());
			contextoHTML.append(documento.getElementsByTag("h1").text());
			contextoHTML.append(documento.getElementsByTag("h2").text());
			contextoHTML.append(documento.getElementsByTag("h3").text());
			contextoHTML.append(documento.getElementsByTag("h4").text());
			contextoHTML.append(documento.getElementsByTag("h5").text());
			contextoHTML.append(documento.getElementsByTag("h6").text());
			contextoHTML.append(documento.getElementsByTag("p").text());
			contextoHTML.append(documento.getElementsByTag("span").text());
			contextoHTML.append(documento.getElementsByTag("b").text());
			contextoHTML.append(documento.getElementsByTag("a").text());
			contextoHTML.append(documento.getElementsByTag("i").text());
			contextoHTML.append(documento.getElementsByTag("meta").text());
			contextoHTML.append(documento.getElementsByTag("picture").text());
			contextoHTML.append(documento.getElementsByTag("img").text());
		}
	   return contextoHTML.toString();
	}
	
	public Stream<String> obterLinksRedePesquisaGoogle(String linkDeConsulta) throws IOException {
		paginaDePesquisaDoGoogle = Jsoup.connect(linkDeConsulta).get();
		return paginaDePesquisaDoGoogle
										.getElementsByTag("a")
										.eachAttr("href")
										.parallelStream()
										.filter(
												linha -> (
															linha.contains("url") || 
															linha.contains("http://www") || 
															linha.contains("http://www2") ||
															linha.contains("http://www3") ||
															linha.contains("https://www") || 
															linha.contains("https://www2") ||
															linha.contains("https://www3") 
														) 
													&& !linha.contains(".text") 
													&& !linha.contains("google") 
													&& !linha.contains("youtube")
												)
										.distinct()
										.limit(20);
	}
	
}
