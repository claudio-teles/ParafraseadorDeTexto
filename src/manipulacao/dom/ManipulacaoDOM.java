package manipulacao.dom;

import java.io.IOException;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import manipulacao.documento.ManipulacaoDocumento;
import util.UtilitarioGeral;

public class ManipulacaoDOM {
	
	private StringBuilder queryGoogle = new StringBuilder("https://www.google.com.br/search?q=");
	private StringBuilder contexto = new StringBuilder();
	
	private Document documento;
	private Document paginaDePesquisaDoGoogle;

	public ManipulacaoDOM() {
		super();
	}

	public StringBuilder getContexto() {
		return contexto;
	}

	public void setContexto(StringBuilder contexto) {
		this.contexto = contexto;
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
	
	public String lerDocWordOnline(ManipulacaoDocumento doc, String linkOriginal) throws IOException {
		String conteudo = null;
		String linkFinal = new UtilitarioGeral().obterLinkRedecionamentoUrl(linkOriginal);
		if (linkFinal.endsWith(".doc")) {
			conteudo = doc.obterConteudoArquivoDoc(linkFinal);
		}
		return conteudo;
	}

	public String extrairContextoSite(String url) throws IOException {
		if (!url.endsWith(".pdf") && !url.endsWith(".doc") && !url.endsWith(".docx") && !url.endsWith(".ppt") && !url.endsWith(".pptx")) {
			documento = Jsoup.connect(url).get();
			contexto.append(documento.getElementsByTag("title").text());
			contexto.append(documento.getElementsByTag("h1").text());
			contexto.append(documento.getElementsByTag("h2").text());
			contexto.append(documento.getElementsByTag("h3").text());
			contexto.append(documento.getElementsByTag("h4").text());
			contexto.append(documento.getElementsByTag("h5").text());
			contexto.append(documento.getElementsByTag("h6").text());
			contexto.append(documento.getElementsByTag("p").text());
			contexto.append(documento.getElementsByTag("span").text());
			contexto.append(documento.getElementsByTag("b").text());
			contexto.append(documento.getElementsByTag("a").text());
			contexto.append(documento.getElementsByTag("i").text());
			contexto.append(documento.getElementsByTag("meta").text());
			contexto.append(documento.getElementsByTag("picture").text());
			contexto.append(documento.getElementsByTag("img").text());
		}
	   return contexto.toString();
	}
	
	public Stream<String> obterLinksRedePesquisaGoogle(String linkDeConsulta) throws IOException {
		paginaDePesquisaDoGoogle = Jsoup.connect(linkDeConsulta).get();
		return paginaDePesquisaDoGoogle
										.getElementsByTag("a")
										.eachAttr("href")
										.parallelStream()
										.filter(
												linha -> (linha.contains("url") || linha.contains("https://www")) 
													&& !linha.contains("google") 
													&& !linha.contains("youtube")
												)
										.distinct()
										.limit(3);
	}
	
}
